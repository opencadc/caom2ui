/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2008.                            (c) 2008.
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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


import org.apache.log4j.Logger;

import org.json.JSONException;
import org.json.JSONWriter;

import ca.nrc.cadc.search.form.*;
import ca.nrc.cadc.util.StringUtil;


/**
 * Validates form inputs and returns JSON containing the invalid utype and an
 * error message.
 *
 * @author jburke
 */
public class FormValidationServlet extends HttpServlet {

    private static final long serialVersionUID = 201310020918L;

    private static Logger LOGGER = Logger.getLogger(FormValidationServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Passed to doGet.
     *
     * @param request  The servlet request.
     * @param response The servlet response.
     * @throws ServletException If servlet exception.
     * @throws IOException      If IO exception.
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * The query parameters are form input utypes and values. Validate the
     * input value for each input utype.
     *
     * @param request  The servlet request.
     * @param response The servlet response.
     * @throws ServletException If servlet exception.
     * @throws IOException      If IO exception.
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        // Validate the parameters.
        final FormErrors formErrors = getFormErrors(request.getParameterMap());

        // Write out any errors as JSON.
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        writeFormErrors(formErrors, response.getWriter());
    }

    /**
     * Validates the parameter names and values.
     *
     * @param parameters Map of parameter names and values.
     * @return FormError containing any validation errors.
     *
     * @throws ServletException Any Servlet errors to be bubbled up.
     */
    protected FormErrors getFormErrors(final Map<String, String[]> parameters) throws ServletException {
        // Fields to validate.
        final String[] fields = parameters.get("field");

        if (fields == null) {
            throw new IllegalArgumentException("Required field parameter not found");
        }

        // Holds any validation errors.
        final FormErrors formErrors = new FormErrors();

        for (final String field : fields) {
            // Form parameters are assumed to be single valued, else unable to
            // determine which input is incorrect.
            final String[] values = parameters.get(field);

            if ((values != null) && (values.length == 1)) {
                final String value = values[0];

                // Try and create a FormConstraint.
                final FormConstraint form = getFormConstraint(field, value);

                if (form == null) {
                    final String message = "BUG: unknown form utype " + field;
                    throw new ServletException(message);
                } else {
                    // Validate the input.
                    final boolean isValid = form.isValid(formErrors);
                    LOGGER.debug(field + "[" + value + "] valid: " + isValid);
                }
            } else if (values != null && values.length > 1) {
                final String message =
                        "BUG: form utype " + field
                                + " should be distinct but found multiple form values";
                throw new ServletException(message);
            }
        }
        return formErrors;
    }

    /**
     * For the given utype return the corresponding FormConstraint class.
     *
     * @param field Form input name.
     * @param value Form input value.
     * @return A FormConstraint
     */
    FormConstraint getFormConstraint(final String field, final String value) {
        final FormConstraint formConstraint;

        if (StringUtil.hasText(field)) {
            // Field might have a @[form name].value appended, strip out.
            int index = field.indexOf("@");
            final String utype = (index == -1) ? field : field.substring(0, index);

            // Text
            if (ObsModel.isTextUtype(utype)) {
                formConstraint = new Text(utype, value, false);
            }

            // Number
            else if (ObsModel.isNumberUtype(utype)) {
                formConstraint = new ca.nrc.cadc.search.form.Number(value, utype);
            }

            // Timestamp
            else if (ObsModel.isUTCDateUtype(utype)) {
                formConstraint = new TimestampFormConstraint(value, utype);
            }

            // Date
            else if (ObsModel.isMJDUtype(utype) || ObsModel.isLocalDateUtype(utype) || ObsModel.isTimeUtype(utype)) {
                formConstraint = new ca.nrc.cadc.search.form.Date(value, utype, null);
            }

            // Energy
            else if (ObsModel.isEnergyUtype(utype)) {
                formConstraint = new Energy(value, utype);
            } else {
                formConstraint = null;
            }
        } else {
            formConstraint = null;
        }

        return formConstraint;
    }

    /**
     * Writes the FormErrors validation errors as JSON to the writer.
     *
     * @param formErrors FormErrors containing validation errors.
     * @param writer     Response Writer.
     * @throws IOException if unable to write JSON.
     */
    void writeFormErrors(final FormErrors formErrors, final Writer writer) throws IOException {
        final JSONWriter jsonWriter = new JSONWriter(writer);
        try {
            // Each FormConstraint class can create multiple errors, but JSON
            // keys must be unique, so don't add duplicate keys.
            final Set<String> keys = new HashSet<>();
            jsonWriter.object();

            for (final FormError formError : formErrors.get()) {
                if (keys.add(formError.name)) {
                    jsonWriter.key(formError.name).value(formError.value);
                }
            }

            jsonWriter.endObject();
        } catch (JSONException e) {
            LOGGER.error("Unable to create JSON.", e);
            throw new IOException("Unable to create JSON.", e);
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
