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
 * 1/25/12 - 2:58 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;


import ca.nrc.cadc.AbstractUnitTest;

import ca.nrc.cadc.date.DateUtil;
import org.apache.log4j.Level;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ca.nrc.cadc.search.form.*;
import ca.nrc.cadc.search.form.Number;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.util.Log4jInit;

import static ca.nrc.cadc.search.UnitConversionServlet.CAOM2_ENERGY_FIELD;
import static ca.nrc.cadc.search.UnitConversionServlet.CAOM2_TIME_FIELD;
import static ca.nrc.cadc.search.UnitConversionServlet.CAOM2_TIME_PRESET_UTYPE;
import static org.junit.Assert.*;


public class UnitConversionServletTest extends AbstractUnitTest<UnitConversionServlet> {

    private StringWriter stringWriter = new StringWriter();
    private JSONWriter jsonWriter = new JSONWriter(stringWriter);
    private Map<String, String[]> parameters = new HashMap<>();

    private final FormErrors formErrors = new FormErrors();


    @Before
    public void setUp() {
        Log4jInit.setLevel("ca.nrc.cadc", Level.INFO);
    }

    @Test
    public void getUType() {
        setTestSubject(new UnitConversionServlet());
        final String result = getTestSubject().getUType("/Plane.time.bounds.samples");
        assertEquals(CAOM2_TIME_FIELD, result);
    }

    @Test
    public void writeTimestampJSON() {
        setTestSubject(new UnitConversionServlet());

        getTestSubject().writeTimestamp(jsonWriter, formErrors,
                                        new TimestampFormConstraint("<= 1977-11-25",
                                                                    "Plane.dataRelease"));
        assertEquals("Wrong JSON Timestamp.",
                     "[\" (<= 1977-11-25 00:00:00.000)\"]",
                     stringWriter.toString());
    }

    @Test
    public void writeDate() {
        setTestSubject(new UnitConversionServlet());

        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        cal.set(Calendar.MILLISECOND, 0);

        getTestSubject().writeDate(jsonWriter, formErrors,
                                   new Date(DatePreset.PAST_24_HOURS.name(),
                                            CAOM2_TIME_PRESET_UTYPE, cal.getTime()));

        assertEquals("Wrong JSON Timestamp.",
                     "[\" (43471.133333..43472.133333 MJD)\"]",
                     stringWriter.toString());
    }

    @Test
    public void writeJSON() {
        setTestSubject(new UnitConversionServlet());

        getTestSubject().writeJSON("NO SUCH UTYPE", null, jsonWriter,
                                   parameters);
        assertEquals("Test 1 - Should be empty array.", "[]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject().writeJSON("Plane.position.sampleSize",
                                   "0.02..0.05arcmin", jsonWriter,
                                   parameters);
        assertEquals("Test 2 - Should be arcseconds.",
                     "[\" (1.2..3.0 arcseconds)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON(CAOM2_TIME_FIELD, "> 2010-09-22", jsonWriter,
                           parameters);
        assertEquals("Test 3 - Should be MJD.",
                     "[\" (>= 55461.0 MJD)\"]",
                     stringWriter.toString());

        // Test with spaces.
        resetDataMembers();
        getTestSubject()
                .writeJSON(CAOM2_TIME_FIELD, " > 2010-09-22", jsonWriter,
                           parameters);
        assertEquals("Test 3.5 - Should be MJD.",
                     "[\" (>= 55461.0 MJD)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject().writeJSON("Plane.time.exposure", "2m..1h", jsonWriter,
                                   parameters);
        assertEquals("Test 4 - Should be seconds.",
                     "[\" (120.0..3600.0 seconds)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.time.bounds.width", "1y..2y", jsonWriter,
                           parameters);
        assertEquals("Test 5 - Should be days.",
                     "[\" (365.0..730.0 days)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON(CAOM2_ENERGY_FIELD, "800nm", jsonWriter,
                           parameters);
        assertEquals("Test 6 - Should be metres.",
                     "[\" (= 8.000E-7 metres)\"]".toLowerCase(),
                     stringWriter.toString().toLowerCase());

        resetDataMembers();
        getTestSubject()
                .writeJSON(CAOM2_ENERGY_FIELD, "0.21m", jsonWriter,
                           parameters);
        assertEquals("Test 7 - Should be metres.",
                     "[\" (= 0.21 metres)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON(CAOM2_ENERGY_FIELD, "300..400GHz", jsonWriter,
                           parameters);
        assertEquals("Test 8 - Should be metres.",
                     "[\" (7.495E-4..9.993E-4 metres)\"]".toLowerCase(),
                     stringWriter.toString().toLowerCase());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Char.SpectralAxis.Coverage.Bounds.Limits",
                           "300..400GHz", jsonWriter, parameters);
        assertEquals("Test 8.1 - Should be metres from ObsCore.",
                     "[\" (7.495E-4..9.993E-4 metres)\"]".toLowerCase(),
                     stringWriter.toString().toLowerCase());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.energy.sampleSize", "2..3GHz", jsonWriter,
                           parameters);
        assertEquals("Test 9 - Should be Hz.",
                     "[\" (2.0E9..3.0E9 Hz)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.energy.sampleSize", "> 3GHz", jsonWriter,
                           parameters);
        assertEquals("Test 10 - Should be > 3.0E9Hz.",
                     "[\" (> 3.0E9 Hz)\"]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.energy.sampleSize", "> ", jsonWriter,
                           parameters);
        assertEquals("Test 11 - Should be empty array", "[]",
                     stringWriter.toString());

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.energy.bounds.width", "< 1000A", jsonWriter,
                           parameters);
        assertEquals("Test 12 - Should be empty string.",
                     "[\" (< 1.000E-7 metres)\"]".toLowerCase(),
                     stringWriter.toString().toLowerCase());

        setTestSubject(new UnitConversionServlet() {
            /**
             * Resolve the given target.
             *
             * @param value         The value to resolve.
             * @param resolverValue The resolver value desired.
             * @return TargetData instance.
             */
            @Override
            protected TargetData resolveTarget(final String value, final String resolverValue) {
                assertEquals("Value should be trimmed.", "m101", value);
                return new TargetData(value, 88.0d, null, 88.0d, null, 0.0d,
                                      "COORDSYS", "SERVICE", 88, "OTYPE",
                                      "ONAME", "MTYPE");
            }
        });

        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.position.bounds", " m101", jsonWriter,
                           parameters);
        final JSONObject json = new JSONObject(stringWriter.toString());

        assertEquals("Test 11 - Should be trimmed value (m101).",
                     "m101", json.getString("resolveTarget"));


        resetDataMembers();
        getTestSubject()
                .writeJSON("Plane.dataRelease", "1977-11-25..2000", jsonWriter,
                           parameters);

        assertEquals("Test 12 - Should be date range.",
                     "[\" (1977-11-25 00:00:00.000..2000-01-01 00:00:00.000)\"]",
                     stringWriter.toString());
    }

    @Test
    public void getNumericDisplayValue() {
        setTestSubject(new UnitConversionServlet());

        assertEquals("Should be 9.893E-2", "9.893E-2".toLowerCase(),
                     getTestSubject().getNumericDisplayValue(
                             0.09893083333333333).toLowerCase());

        assertEquals("Should be 0.256", "0.256",
                     getTestSubject().getNumericDisplayValue(0.256));

        assertEquals("Should be 1", "1",
                     getTestSubject().getNumericDisplayValue(1));

        assertEquals("Should be 44.56", "44.56",
                     getTestSubject().getNumericDisplayValue(44.56));
    }

    @Test
    public void getNumericRangeValue() {
        setTestSubject(new UnitConversionServlet());

        final Number numberFormConstraint =
                new Number("0.09893083333333333..0.09993083333333333",
                           "Plane.energy.sampleSize") {
                    /**
                     * Obtain the validated and normalized numeric lower value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getLowerNumber() {
                        return 0.09893083333333333;
                    }

                    /**
                     * Obtain the validated and normalized numeric upper value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getUpperNumber() {
                        return 0.09993083333333333;
                    }
                };

        final String s1 =
                getTestSubject().getNumericRangeValue(numberFormConstraint,
                                                      "metres");
        assertEquals("Should be (9.893E-2..9.993E-2 metres)",
                     " (9.893E-2..9.993E-2 metres)".toLowerCase(), s1.toLowerCase());


        // TEST 2

        setTestSubject(new UnitConversionServlet());

        final Number numberFormConstraint2 =
                new Number("0.09893083333333333..0.09993083333333333hz",
                           "Plane.energy.bounds.width") {
                    /**
                     * Obtain the validated and normalized numeric lower value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getLowerNumber() {
                        return 0.09893083333333333;
                    }

                    /**
                     * Obtain the validated and normalized numeric upper value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getUpperNumber() {
                        return 0.09993083333333333;
                    }

                    @Override
                    public String getUnit() {
                        return "hz";
                    }
                };

        final String s2 =
                getTestSubject().getNumericRangeValue(numberFormConstraint2,
                                                      "Hz");
        assertEquals("Should be (9.893E-2..9.993E-2 Hz)",
                     " (9.893E-2..9.993E-2 Hz)".toLowerCase(), s2.toLowerCase());


        // TEST 3

        setTestSubject(new UnitConversionServlet());

        final Number numberFormConstraint3 =
                new Number("0.7..0.9mHz", "Plane.energy.bounds.width") {
                    /**
                     * Obtain the validated and normalized numeric lower value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getLowerNumber() {
                        return 0.7;
                    }

                    /**
                     * Obtain the validated and normalized numeric upper value
                     *
                     * @return Number instance.
                     */
                    @Override
                    public java.lang.Number getUpperNumber() {
                        return 0.99;
                    }

                    @Override
                    public String getUnit() {
                        return "mHz";
                    }
                };

        final String s3 =
                getTestSubject().getNumericRangeValue(numberFormConstraint3,
                                                      "Hz");
        assertEquals("Should be (0.7..0.99 Hz)", " (0.7..0.99 Hz)", s3);
    }

    private void resetDataMembers() {
        try {
            if (stringWriter != null) {
                stringWriter.flush();
                stringWriter.close();
            }
        } catch (Exception ignore) {
        }

        stringWriter = new StringWriter();
        jsonWriter = new JSONWriter(stringWriter);
        parameters = new HashMap<>();
    }

}
