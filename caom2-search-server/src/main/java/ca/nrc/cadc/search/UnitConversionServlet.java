/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2012.                            (c) 2012.
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
 * 1/25/12 - 2:59 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.*;
import java.util.Map;
import java.util.TimeZone;

import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.search.parser.resolver.ResolverImpl;
import org.json.JSONException;
import org.json.JSONWriter;

import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.search.form.*;
import ca.nrc.cadc.search.form.Number;
import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.TargetParser;
import ca.nrc.cadc.search.parser.exception.TargetParserException;
import ca.nrc.cadc.util.StringUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet to convert values for the Filter box inputs of the results page to
 * the default query units expected by the service.  The converted value,
 * with units, shows on the UI in the form ( 'converted value' 'units').
 * This feedback useful for users as input to building custom ADQL queries,
 * and for showing users the units for query execution.
 */
public class UnitConversionServlet extends HttpServlet {

    static final String CAOM2_TIME_FIELD = "Plane.time.bounds.samples";
    static final String CAOM2_ENERGY_FIELD = "Plane.energy.bounds.samples";
    static final String CAOM2_TIME_PRESET_UTYPE = CAOM2_TIME_FIELD + "_PRESET";

    private static final DateFormat DATE_FORMATTER =
            DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT, TimeZone.getTimeZone("UTC"));


    /**
     * Handle GET requests.  This will ALWAYS return JSON data!
     *
     * @param req  The Request.
     * @param resp The Response.
     * @throws IOException Any other unforeseen errors.
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        writeSourceJSON(getUType(req.getPathInfo()), req, resp);
    }

    /**
     * Write out the JSON autocomplete source to the given response.
     *
     * @param utype    The utype of the field.
     * @param request  The HTTP Request.
     * @param response The Response.
     * @throws IOException If any unforeseen exception occurs.
     */
    private void writeSourceJSON(final String utype, final HttpServletRequest request,
                                 final HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(response.getOutputStream());
            final JSONWriter jsonWriter = new JSONWriter(writer);

            writeJSON(utype, request.getParameter("term"), jsonWriter,
                      request.getParameterMap());
        } catch (JSONException e) {
            throw new IOException("Unable to write out JSON.", e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }


    /**
     * Write an array of JSON items as converted back to the view.
     *
     * @param uType             The utype of the field in the current context.
     *                          This may have been modified by the caller from the
     *                          original value.
     * @param value             The value entered so far.
     * @param jsonWriter        The ready JSONWriter that has an array started.
     * @param requestParameters The parameters from the request.
     * @throws JSONException If anything goes wrong writing data.
     */
    void writeJSON(final String uType, final String value, final JSONWriter jsonWriter,
                   final Map<String, String[]> requestParameters) throws JSONException {
        if (StringUtil.hasText(value)) {
            final FormErrors formErrors = new FormErrors();

            if (ObsModel.isUTCDateUtype(uType)) {
                writeTimestamp(jsonWriter, formErrors,
                               new TimestampFormConstraint(value, uType));
            } else if (isObservationDateUType(uType)) {
                writeDate(jsonWriter, formErrors, new Date(value, uType, null));
            } else if (uType.equals("Plane.energy.restwav")) {
                writeNumericEnergy(new Number(value, uType), jsonWriter,
                                   formErrors);
            } else if (uType.equals(CAOM2_ENERGY_FIELD)
                    || uType.equals("Char.SpectralAxis.Coverage.Bounds.Limits")) {
                writeNumericEnergy(new Energy(value, uType), jsonWriter, formErrors);
            } else if (uType.equals("Plane.position.bounds") ||
                    uType.equals("Char.SpatialAxis.Coverage.Support.Area")) {
                writeTargetResolution(jsonWriter, NetUtil.decode(value),
                                      (requestParameters.containsKey("resolver")
                                              ? requestParameters.get("resolver")[0]
                                              : "ALL"));
            } else {
                writeNumeric(uType, value, jsonWriter, formErrors);
            }
        } else {
            jsonWriter.array().endArray();
        }
    }

    private boolean isObservationDateUType(final String utype) {
        return utype.equals(CAOM2_TIME_FIELD)
                || utype.equals(CAOM2_TIME_PRESET_UTYPE)
                || utype.startsWith("Char.TimeAxis.Coverage.Bounds.Limits");
    }

    /**
     * Obtain the display value for a particular number.  If the amount of
     * characters after the decimal point exceed three items, and it's less than
     * 1.0, then format it with an exponent.
     *
     * @param number The constraint.
     * @return String value to display.
     */
    String getNumericDisplayValue(final java.lang.Number number) {
        final java.lang.Number safeNumber;

        if (number == null) {
            safeNumber = 0.0;
        } else {
            safeNumber = number;
        }

        final String returnValue;
        final String stringValue = safeNumber.toString();
        final int indexOfDecimal = stringValue.indexOf(".");
        final DecimalFormat numberFormat;

        if ((indexOfDecimal >= 0) && (safeNumber.doubleValue() < 1.0d)
                && (stringValue.substring(indexOfDecimal + 1).length() > 3)) {
            numberFormat = new DecimalFormat("0.000E0");
            returnValue = numberFormat.format(number);
        }
        // Exponents are already in place, so leave it be.  This seems a little
        // hacky.
        //
        // This is here because the Double.toString() method that sets the
        // stringValue is actually better than any formatting that I could come
        // up with in the else statement; meaning a source of 2000000000.0
        // ought to look like 2.0E9, which Double.toString() gives us, but the
        // formatter would still produce just the raw string value with all of
        // the zeroes.
        //
        // jenkinsd 2015.01.21
        else if (stringValue.indexOf("E") > 0) {
            returnValue = stringValue;
        } else {
            numberFormat = new DecimalFormat();

            numberFormat.setGroupingUsed(false);
            numberFormat.setMaximumFractionDigits(6);

            if (!(number instanceof Integer)) {
                numberFormat.setMinimumFractionDigits(1);
            } else {
                numberFormat.setParseIntegerOnly(true);
            }

            returnValue = numberFormat.format(safeNumber);
        }

        return returnValue;
    }


    /**
     * Obtain the time range value for the given Number.  This implementation
     * will take the lower and upper values of the range, if present, and build
     * a converted String representation.
     *
     * @param numericConstraint The Number constraint.
     * @param displayUnit       The value of the units.
     * @return String of the converted range.
     */
    String getNumericRangeValue(
            final AbstractNumericFormConstraint numericConstraint,
            final String displayUnit) {
        final String s;

        switch (numericConstraint.getOperand()) {
            case EQUALS: {
                java.lang.Number n;

                try {
                    n = Float.valueOf(numericConstraint.getFormValue());
                } catch (NumberFormatException e) {
                    n = Double.NaN;
                }

                s = Operand.EQUALS.getOperand() + " "
                        + getNumericDisplayValue(n);
                break;
            }

            case RANGE: {
                s = getNumericDisplayValue(numericConstraint.getLowerNumber())
                        + Operand.RANGE.getOperand()
                        + getNumericDisplayValue(
                        numericConstraint.getUpperNumber());
                break;
            }

            case LESS_THAN:
            case LESS_THAN_EQUALS: {
                s = numericConstraint.getOperand().getOperand() + " "
                        + getNumericDisplayValue(
                        numericConstraint.getUpperNumber());

                break;
            }

            case GREATER_THAN:
            case GREATER_THAN_EQUALS: {
                s = numericConstraint.getOperand().getOperand() + " "
                        + getNumericDisplayValue(
                        numericConstraint.getLowerNumber());
                break;
            }

            default: {
                s = "";
            }
        }

        return " (" + s + " " + displayUnit + ")";
    }

    private String formatDate(final java.util.Date date) {
        return DATE_FORMATTER.format(date);
    }

    /**
     * Write out the Timestamp value.
     *
     * @param jsonWriter              The JSON Writer to write to.
     * @param formErrors              If the Form Data is invalid.
     * @param timestampFormConstraint The Timestamp Form entry
     * @throws JSONException For any JSON error(s).
     */
    void writeTimestamp(final JSONWriter jsonWriter,
                        final FormErrors formErrors,
                        final TimestampFormConstraint timestampFormConstraint)
            throws JSONException {
        jsonWriter.array();

        try {
            if (timestampFormConstraint.isValid(formErrors)) {
                String s;
                switch (timestampFormConstraint.getOperand()) {
                    case EQUALS: {
                        s = timestampFormConstraint.getFormValue();
                        break;
                    }

                    case RANGE: {
                        s = formatDate(timestampFormConstraint
                                               .getLowerDate()) + ".."
                                + formatDate(timestampFormConstraint
                                                     .getUpperDate());
                        break;
                    }

                    case LESS_THAN:
                    case LESS_THAN_EQUALS: {
                        s = "<= "
                                + formatDate(timestampFormConstraint
                                                     .getUpperDate());
                        break;
                    }

                    case GREATER_THAN:
                    case GREATER_THAN_EQUALS: {
                        s = ">= "
                                + formatDate(timestampFormConstraint
                                                     .getLowerDate());
                        break;
                    }

                    default: {
                        s = "Invalid operand: "
                                + timestampFormConstraint.getFormValue();
                    }
                }
                jsonWriter.value(" (" + s + ")");
            } else {
                jsonWriter.value("Invalid: " + timestampFormConstraint
                        .getFormValue());
            }
        } finally {
            jsonWriter.endArray();
        }
    }

    /**
     * The JSON data for a date filter.
     *
     * @param jsonWriter     The JSON Writer to write to.
     * @param formErrors     If the Form Data is invalid.
     * @param dateConstraint The Date constraint as submitted.
     * @throws JSONException For any JSON error(s).
     */
    void writeDate(final JSONWriter jsonWriter,
                   final FormErrors formErrors,
                   final Date dateConstraint) throws JSONException {
        jsonWriter.array();

        try {
            if (dateConstraint.isValid(formErrors)) {
                String s;
                switch (dateConstraint.getOperand()) {
                    case EQUALS: {
                        s = dateConstraint.getFormValue();
                        break;
                    }

                    case RANGE: {
                        s = getNumericDisplayValue(
                                dateConstraint.getLowerNumber())
                                + ".."
                                + getNumericDisplayValue(
                                dateConstraint.getUpperNumber());
                        break;
                    }

                    case LESS_THAN:
                    case LESS_THAN_EQUALS: {
                        s = "<= " + getNumericDisplayValue(
                                dateConstraint.getUpperNumber());
                        break;
                    }

                    case GREATER_THAN:
                    case GREATER_THAN_EQUALS: {
                        s = ">= " + getNumericDisplayValue(
                                dateConstraint.getLowerNumber());
                        break;
                    }

                    default: {
                        s = "Invalid operand: "
                                + dateConstraint.getFormValue();
                    }
                }
                jsonWriter.value(" (" + s + " MJD)");
            } else {
                jsonWriter.value("Invalid: " + dateConstraint.getFormValue());
            }
        } finally {
            jsonWriter.endArray();
        }
    }

    /**
     * Write the JSON converted energy data.
     *
     * @param numericFormConstraint Numeric form constraint.
     * @param jsonWriter            The JSON Writer to write to.
     * @param formErrors            The FormErrors object to populate, if necessary.
     * @throws JSONException Any JSON writing errors.
     */
    private void writeNumericEnergy(
            final AbstractNumericFormConstraint numericFormConstraint,
            final JSONWriter jsonWriter, final FormErrors formErrors)
            throws JSONException {
        jsonWriter.array();

        try {
            if (numericFormConstraint.isValid(formErrors)) {
                jsonWriter.value(getNumericRangeValue(numericFormConstraint,
                                                      "metres"));
            }
        } finally {
            jsonWriter.endArray();
        }
    }

    /**
     * Write the JSON converted numeric data.
     *
     * @param utype      The uType value of this number.
     * @param value      The value to convert.
     * @param jsonWriter The JSON Writer to write to.
     * @param formErrors The FormErrors object to populate, if necessary.
     * @throws JSONException Any JSON writing errors.
     */
    private void writeNumeric(final String utype, final String value,
                              final JSONWriter jsonWriter,
                              final FormErrors formErrors)
            throws JSONException {
        final Number number = new Number(value, utype);

        jsonWriter.array();

        try {
            if (number.isValid(formErrors)) {
                switch (utype) {
                    case "Plane.position.sampleSize":
                        jsonWriter.value(getNumericRangeValue(number,
                                                              "arcseconds"));
                        break;
                    case "Plane.energy.bounds.width":
                    case "Char.SpectralAxis.Coverage.Bounds.Limits":
                    case "Plane.energy.sampleSize":
                        jsonWriter.value(
                                getNumericRangeValue(number,
                                                     (StringUtil.hasLength(
                                                             number.getUnit())
                                                             && number.getUnit().
                                                             matches("^.*([Hh]+[Zz]+)"))
                                                             ? "Hz"
                                                             : "metres"));
                        break;
                    case "Char.TimeAxis.Coverage.Bounds.Limits":
                    case "Plane.time.bounds.width":
                        jsonWriter.value(getNumericRangeValue(number,
                                                              "days"));
                        break;
                    case "Plane.time.exposure":
                        jsonWriter.value(getNumericRangeValue(number,
                                                              "seconds"));
                        break;
                    default:
                        jsonWriter.value(value);
                        break;
                }
            }
        } finally {
            jsonWriter.endArray();
        }
    }

    /**
     * Write the JSON target resolution.
     *
     * @param jsonWriter    The JSON Writer to write to.
     * @param value         The value to convert.
     * @param resolverValue The resolver value desired.
     * @throws JSONException Any JSON writing errors.
     */
    private void writeTargetResolution(final JSONWriter jsonWriter, final String value, final String resolverValue)
            throws JSONException {
        try {
            jsonWriter.object();
            final TargetData targetData = resolveTarget(value.trim(),
                                                        resolverValue);

            jsonWriter.key("resolveStatus").value("GOOD");
            jsonWriter.key("resolveValue").value(targetData(targetData));
            jsonWriter.key("resolveTarget").value(targetData.getTarget());
        } catch (Exception e) {
            jsonWriter.key("resolveStatus").value("NOT_FOUND");
            jsonWriter.key("resolveValue").value("");
            jsonWriter.key("resolveTarget").value("");
        } finally {
            jsonWriter.endObject();
        }

    }

    private String targetData(final TargetData targetData) {
        return ((targetData.getTarget() == null)
                ? "" : "target: " + targetData.getTarget())
                + "\nDec: " + targetData.getDec() +
                "\nRA: " + targetData.getRA() +
                "\nRadius: " + ((targetData.getRadius() == null)
                || (targetData.getRadius().equals(Double.NaN))
                ? "N/A" : targetData.getRadius())
                + ((targetData.getCoordsys() == null)
                ? "" : "\ncoordsys: " + targetData.getCoordsys())
                + ((targetData.getService() == null)
                ? "" : "\nservice: " + targetData.getService())
                + ((targetData.getTime() == null)
                ? "" : "\ntime: " + targetData.getTime())
                + ((targetData.getObjectName() == null)
                ? "" : "\noname: " + targetData.getObjectName())
                + ((targetData.getObjectType() == null)
                ? "" : "\notype: " + targetData.getObjectType())
                + ((targetData.getMorphologyType() == null)
                ? "" : "\nmtype: " + targetData.getMorphologyType());
    }


    /**
     * Resolve the given target.
     *
     * @param value         The value to resolve.  Will be trimmed here.
     * @param resolverValue The resolver value desired.
     * @return TargetData instance.
     *
     * @throws TargetParserException If it cannot be resolved or parsed.
     */
    protected TargetData resolveTarget(final String value, final String resolverValue) throws TargetParserException {
        final TargetParser targetParser = new TargetParser(new ResolverImpl());
        return targetParser.parse(value, resolverValue);
    }

    protected String getUType(final String path) {
        return (path.contains("/") ? path.substring(path.indexOf("/") + 1) : path);
    }
}
