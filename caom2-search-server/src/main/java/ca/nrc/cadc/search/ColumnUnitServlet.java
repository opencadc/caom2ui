/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2012.                         (c) 2012.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 1/11/12 - 10:06 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;



import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


/**
 * Servlet to obtain units for a given Column Name.
 */
public class ColumnUnitServlet extends HttpServlet {

    private static Logger LOGGER = Logger.getLogger(ColumnUnitServlet.class);

    // Create Map to hold Column Names and the key/value items to pass
    // from it.  The key of the value map is the column name, and the value is
    // the Display Unit -> VALUE for the selected unit.
    private static Map<String, Map<String, String>> MAIN_CACHE =
            Collections.synchronizedMap(new HashMap<String, Map<String, String>>());


    /**
     * Initialize this servlet's cache.
     *
     * @param config The ServletConfig instance.
     * @throws ServletException If anything happened during init.
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        initCache();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        initCache();
    }

    /**
     * Initialize the cache.
     */
    protected void initCache() {
        final Map<String, String> integrationTimeUnitMap = new LinkedHashMap<>();

        integrationTimeUnitMap.put("Seconds", "SECONDS");
        integrationTimeUnitMap.put("Minutes", "MINUTES");
        integrationTimeUnitMap.put("Hours", "HOURS");

        MAIN_CACHE.put("Integration Time", integrationTimeUnitMap);

        final Map<String, String> spatialRAUnitMap = new LinkedHashMap<>();

        spatialRAUnitMap.put("H:M:S", "SEXAGESIMAL");
        spatialRAUnitMap.put("Degrees", "DEGREES");

        final Map<String, String> spatialDecUnitMap = new LinkedHashMap<>();

        spatialDecUnitMap.put("D:M:S", "SEXAGESIMAL");
        spatialDecUnitMap.put("Degrees", "DEGREES");

        MAIN_CACHE.put("RA (J2000.0)", spatialRAUnitMap);
        MAIN_CACHE.put("Dec. (J2000.0)", spatialDecUnitMap);

        final Map<String, String> wavelengthUnitMap = new LinkedHashMap<>();
        final String[] wavelengthUnits =
                new String[] {"m", "cm", "mm", "um", "nm", "A", "Hz", "kHz",
                              "MHz", "GHz", "eV", "keV", "MeV", "GeV"};

        for (final String unit : wavelengthUnits) {
            wavelengthUnitMap.put(unit, unit);
        }

        MAIN_CACHE.put("Min. Wavelength", wavelengthUnitMap);
        MAIN_CACHE.put("Max. Wavelength", wavelengthUnitMap);

        final Map<String, String> dateUnitMap = new LinkedHashMap<>();

        dateUnitMap.put("Calendar", "IVOA");
        dateUnitMap.put("MJD", "MJD");

        MAIN_CACHE.put("Start Date", dateUnitMap);
    }


    /**
     * Only supports GET operations.  This will write out JSON to the Response.
     *
     * @param req  The Request.  The parameter name that will be
     *             sought out is 'column'. e.g. ?column=Target Name
     * @param resp The Response object to write JSON to.
     * @throws IOException
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Writer writer = new OutputStreamWriter(resp.getOutputStream());
        final String[] columnNames = req.getParameterValues("column");
        final int responseStatus = ((columnNames != null)
                && (columnNames.length > 0)
                ? 200 : 400);

        // Do nothing if no column name has been provided.
        try {
            resp.setContentType("application/json");

            if (responseStatus != 200) {
                resp.setStatus(responseStatus);
                LOGGER.error("Column name is mandatory.");
                throw new IOException("Column name is mandatory.");
            } else {
                writeJSON(writer, columnNames);
                writer.flush();
            }
        } catch (JSONException e) {
            LOGGER.error("Unable to write out JSON.", e);
            throw new IOException(e);
        } finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * Write the JSON to the given Writer.
     *
     * @param writer      The Writer to use.
     * @param columnNames The Column Names to look for.
     * @throws JSONException If writing the JSON can't happen.
     * @throws IOException   If writing anything else can't happen.
     */
    protected void writeJSON(final Writer writer, final String[] columnNames) throws IOException, JSONException {
        final JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.array();

        if (columnNames != null) {
            for (final String columnName : columnNames) {
                jsonWriter.object();
                jsonWriter.key("column");
                jsonWriter.value(columnName);

                final Map<String, String> unitMap = MAIN_CACHE.containsKey(columnName)
                        ? MAIN_CACHE.get(columnName)
                        : new HashMap<String, String>();

                jsonWriter.key("units");
                jsonWriter.array();

                for (final String key : unitMap.keySet()) {
                    jsonWriter.object();

                    jsonWriter.key("displayValue");
                    jsonWriter.value(key);

                    jsonWriter.key("value");
                    jsonWriter.value(unitMap.get(key));

                    jsonWriter.endObject();

                    writer.flush();
                }

                jsonWriter.endArray();
                jsonWriter.endObject();
            }
        }

        jsonWriter.endArray();
    }
}
