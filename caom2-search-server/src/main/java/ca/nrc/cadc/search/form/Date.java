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

package ca.nrc.cadc.search.form;

import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.parser.DateParser;
import ca.nrc.cadc.search.parser.exception.DateParserException;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.ParameterUtil;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


import org.apache.log4j.Logger;


/**
 * Class to represent an Date interval form component.
 *
 * @author jburke
 */
public class Date extends AbstractNumericFormConstraint
        implements SearchableFormConstraint {

    private static Logger log = Logger.getLogger(Date.class);

    // Constants used to construct name for form elements.
    public static final String NAME = "@Date";
    public static final String VALUE = "@Date.value";

    private static final String PRESET = "_PRESET";

    private final java.util.Date currentDate;


    /**
     * Constructor to use without a job.
     *
     * @param value       The value entered.
     * @param utype       This date's uType.
     * @param currentDate The optional override for a current date.  Use new
     *                    java.util.Date otherwise.
     */
    public Date(final String value, final String utype,
                final java.util.Date currentDate) {
        super(utype);

        this.currentDate = (currentDate == null) ? new java.util.Date()
                : currentDate;

        if (StringUtil.hasText(value)) {
            DatePreset datePreset;

            try {
                // Check for date preset.
                datePreset = DatePreset.valueOf(value);
            } catch (IllegalArgumentException e) {
                datePreset = null;
            }

            if (datePreset == null) {
                // Not a preset...
                init(utype + VALUE, value);
            } else {
                init(utype + PRESET + VALUE, value);
            }
        }
    }


    /**
     * Date constructor instantiates a new instance with the given parameters.
     *
     * @param job   The UWS Job.
     * @param utype The utype from the form input field.
     */
    public Date(final Job job, final String utype) {
        super(utype);

        this.currentDate = new java.util.Date();

        final List<Parameter> jobParameterList = job.getParameterList();
        final String presetValue =
                ParameterUtil.findParameterValue(utype + PRESET + VALUE,
                                                 jobParameterList);

        if (StringUtil.hasText(presetValue)) {
            init(utype + PRESET + VALUE, presetValue);
        } else {
            init(utype + VALUE,
                 ParameterUtil.findParameterValue(utype + VALUE,
                                                  jobParameterList));
        }
    }

    void init(final String name, final String value) {
        if (name.equals(getUType() + VALUE)) {
            setFormValue(value);
        } else if (name.equals(getUType() + PRESET + VALUE)
                && StringUtil.hasText(value)) {
            setFormValue(calculateValue(DatePreset.valueOf(value)));
        } else {
            setFormValue("");
        }
    }

    // Create and add a IntervalSearch to SearchTemplates.
    public SearchTemplate buildSearch(final List<FormError> errorList) {
        SearchTemplate template = null;

        try {
            if ((getLowerNumber() == null) && (getUpperNumber() == null)) {
                template = new IntervalSearch(getUType(),
                                              Double.parseDouble(
                                                      getFormValue()),
                                              getFormValueUnit());
            } else {
                template = new IntervalSearch(getUType(),
                                              ((getLowerNumber() == null)
                                                      ? null
                                                      : getLowerNumber().doubleValue()),
                                              ((getUpperNumber() == null)
                                                      ? null
                                                      : getUpperNumber().doubleValue()),
                                              getFormValueUnit());
            }
        } catch (IllegalArgumentException e) {
            errorList.add(new FormError(Date.NAME, e.getMessage()));
            log.debug("Invalid Time parameters: " + e.getMessage() + " "
                              + toString());
        }

        return template;
    }

    /**
     * Text is valid if the Date valueFrom and valueTo have been successfully
     * validated.
     *
     * @return boolean true if form value is valid, false otherwise.
     */
    public boolean isValid(final FormErrors formErrors) {
        final String utype = getUType();
        boolean isLowerDefaultUnit = true;
        boolean isUpperDefaultUnit = true;
        boolean isDefaultUnit = true;

        if (ObsModel.isMJDUtype(utype)) {
            try {
                if (StringUtil.hasText(getLowerValue())) {
                    final DateParser lowerDateParser =
                            new DateParser(getLowerValue());

                    if (lowerDateParser.getDate() != null) {
                        setLowerNumber(DateUtil.toModifiedJulianDate(
                                lowerDateParser.getDate()));
                        if (!lowerDateParser.isJulianDate()) {
                            isLowerDefaultUnit = false;
                        }
                    }
                }

                if (StringUtil.hasText(getUpperValue())) {
                    final DateParser upperDateParser =
                            new DateParser(getUpperValue());

                    if (upperDateParser.getDate() != null) {
                        setUpperNumber(DateUtil.toModifiedJulianDate(
                                upperDateParser.getDate()));
                        if (!upperDateParser.isJulianDate()) {
                            isUpperDefaultUnit = false;
                        }
                    }
                }

                // Single value entered.  Treat it as a range.
                if ((getLowerNumber() == null) && (getUpperNumber() == null)
                        && super.hasData()) {
                    final DateParser dateParser =
                            new DateParser(getFormValue());
                    final java.util.Date enteredDate = dateParser.getDate();
                    final Calendar lowerCalendar = Calendar.getInstance(
                            TimeZone.getTimeZone("UTC"));
                    final Calendar upperCalendar = Calendar.getInstance(
                            TimeZone.getTimeZone("UTC"));

                    lowerCalendar.setTime(enteredDate);
                    upperCalendar.setTime(enteredDate);

                    if (dateParser.getLastParsedField() == Calendar.MILLISECOND) {
                        upperCalendar.add(Calendar.MILLISECOND, 1);
                    } else if (dateParser.getLastParsedField() == Calendar.SECOND) {
                        upperCalendar.add(Calendar.SECOND, 1);
                    } else if (dateParser.getLastParsedField() == Calendar.MINUTE) {
                        upperCalendar.add(Calendar.MINUTE, 1);
                    } else if (dateParser.getLastParsedField()
                            == Calendar.HOUR_OF_DAY) {
                        upperCalendar.add(Calendar.HOUR_OF_DAY, 1);
                    } else if (dateParser.getLastParsedField()
                            == Calendar.DAY_OF_MONTH) {
                        upperCalendar.add(Calendar.HOUR_OF_DAY, 24);
                    } else if (dateParser.getLastParsedField() == Calendar.MONTH) {
                        upperCalendar.add(Calendar.MONTH, 1);
                    } else {
                        upperCalendar.add(Calendar.YEAR, 1);
                    }

                    setLowerNumber(DateUtil.toModifiedJulianDate(
                            lowerCalendar.getTime()));
                    setUpperNumber(DateUtil.toModifiedJulianDate(
                            upperCalendar.getTime()));

                    // Maintain the original value, but converted.
                    setFormValue(
                            Double.toString(getLowerNumber().doubleValue()));

                    if (!dateParser.isJulianDate()) {
                        isDefaultUnit = false;
                    }
                }
            } catch (DateParserException e) {
                addError(new FormError(utype + VALUE, e.getMessage()));
            }
        } else {
            addError(new FormError(utype + VALUE, "Invalid utype " + utype));
        }

        if ((!isLowerDefaultUnit && !isUpperDefaultUnit) || !isDefaultUnit) {
            setFormValueUnit("IVOA");
        }

        final boolean hasErrors = !getErrorList().isEmpty();
        if (!hasErrors) {
            swapTrueValuesIfNecessary();
        }

        formErrors.set(utype + NAME, getErrorList());
        return !hasErrors;
    }

    @Override
    public String resolveUnit(final String forUnit) {
        return forUnit;
    }

    /**
     * @return String representation of the Date form.
     */
    @Override
    public String toString() {
        return "Date[" + getUType() + ", " + getLowerNumber() + ", "
                + getUpperNumber() + "]";
    }

    /**
     * Calculate the constraint value from the given preset.
     *
     * @param datePreset The DatePreset from the input.
     * @return String date input.
     */
    String calculateValue(final DatePreset datePreset) {
        return datePreset.getStringValue(currentDate);
    }
}
