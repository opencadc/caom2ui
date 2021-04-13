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

import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.form.*;
import ca.nrc.cadc.search.form.FormConstraint;
import ca.nrc.cadc.search.form.Number;
import ca.nrc.cadc.search.parser.NumberParser;
import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.search.util.ParameterUtil;
import ca.nrc.cadc.search.validate.EnergyValidator;
import ca.nrc.cadc.util.ArrayUtil;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import java.lang.reflect.Constructor;
import java.util.*;


import org.apache.log4j.Logger;


/**
 * Class checks the request parameter names for potential forms, and
 * if found instantiates the form class and add it to a list of forms.
 * The forms can be checked for validity, and retrieved for further
 * processing.
 *
 * @author jburke
 */
public class FormData {
    private static final Logger LOGGER = Logger.getLogger(FormData.class);

    private static final String ENERGY_FREQ_WIDTH_UTYPE = "Plane.energy.freqWidth";
    private static final String ENERGY_FREQ_SAMPLE_SIZE_UTYPE = "Plane.energy.freqSampleSize";
    private static final Map<String, String> ENERGY_FREQ_UTYPES = new HashMap<>();

    private final ParameterUtil parameterUtil = new ParameterUtil();


    static {
        ENERGY_FREQ_UTYPES.put("Plane.energy.bounds.width", ENERGY_FREQ_WIDTH_UTYPE);
        ENERGY_FREQ_UTYPES.put("Plane.energy.sampleSize", ENERGY_FREQ_SAMPLE_SIZE_UTYPE);
        ENERGY_FREQ_UTYPES.put("Plane.energy.restwav", "Plane.energy.restwav");
    }

    private static final String FORM_PACKAGE_NAME = "ca.nrc.cadc.search.form.";

    // List of forms from the Job.
    private final List<FormConstraint> formConstraints = new ArrayList<>();

    /**
     * List of form validation errors.
     */
    private final List<FormError> errorList = new ArrayList<>();


    /**
     * Map of non-default form field utypes and units.
     */
    private final Map<String, String> formValueUnits = new HashMap<>();


    /**
     * Empty constructor for testing.
     */
    FormData() {

    }

    /**
     * FormData constructor. The Job if first checked to see if a parameter
     * name using any of the Enumerated, Shape2, and Shape1 utype exists. If a
     * parameter is found, then the corresponding class is instantiated and
     * added to the list of forms.
     * The form utypes and form class names for the Interval and Number classes
     * are combined to form a parameter name. If this parameter name exists in
     * the Job, a class for that form is instantiated with the utype as a
     * argument and added to the list of forms.
     *
     * @param job the UWS Job.
     */
    public FormData(final Job job) {
        logParameters(job);

        // Gather all form name parameters
        final List<String> formNames = parameterUtil.getValues(FormConstraint.FORM_NAME, job.getParameterList());
        if (formNames != null) {
            // Process the form id's.
            for (final String formName : formNames) {
                final String[] names = formName.split("@");

                // Check for stray form parameters not in AS format.
                if (names.length == 1) {
                    LOGGER.debug("unknown form name " + formName);
                } else {
                    final String component = FORM_PACKAGE_NAME + names[1];
                    final String uType = names[0];

                    // Ignore the cutouts.
                    if (!uType.endsWith("DOWNLOADCUTOUT")) {
                        LOGGER.debug("init: utype " + uType + ", component " + component);

                        try {
                            @SuppressWarnings("unchecked")
                            final Class<? extends SearchableFormConstraint> componentClass =
                                    (Class<? extends SearchableFormConstraint>) Class.forName(component);
                            addFormConstraints(componentClass, job, uType);
                        } catch (ClassNotFoundException e) {
                            LOGGER.error("Class not found for component " + component);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add the necessary form constraints.
     *
     * @param componentClass The class to create for.
     * @param job            The job to use.
     * @param uType          The uType.
     */
    private void addFormConstraints(final Class<? extends SearchableFormConstraint> componentClass, final Job job,
                                    final String uType) {
        if (componentClass == Enumerated.class) {
            addEnumeratedFormConstraints(job, uType);
        } else if (componentClass == Select.class) {
            addSelectFormConstraints(job, uType);
        } else {
            // Create a default form constraint.
            try {
                final Constructor<? extends SearchableFormConstraint> constructor =
                        componentClass.getDeclaredConstructor(Job.class, String.class);
                final SearchableFormConstraint formConstraint = constructor.newInstance(job, uType);

                // Story 888 - Special case for Number frequency unit.
                if (ObsModel.isEnergyUtype(uType) && (componentClass == Number.class)) {
                    addFrequencyConstraints((Number) formConstraint);
                } else {
                    addFormConstraint(formConstraint);
                }

                LOGGER.debug("add " + formConstraint);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating class " + componentClass.getName(), e);
            }
        }
    }

    /**
     * Story 888.  Handle special frequency searches.
     * <p>
     * TODO - This method is ridiculously ugly.  Will try to improve.
     * TODO - jenkinsd 2012.01.27
     *
     * @param numberEnergyConstraint The FormConstraint object.
     * @throws NumericParserException If the input cannot be parsed.
     */
    void addFrequencyConstraints(final Number numberEnergyConstraint) throws NumericParserException {
        final String lowerValue = numberEnergyConstraint.getLowerValue();
        final String upperValue = numberEnergyConstraint.getUpperValue();

        if (numberEnergyConstraint.getOperand() == Operand.RANGE) {
            final NumberParser lowerParser = new NumberParser(lowerValue);
            final NumberParser upperParser = new NumberParser(upperValue);

            String lowerUnit;
            String upperUnit;

            if (!StringUtil.hasText(lowerParser.getUnit())) {
                lowerUnit = "";
            } else {
                lowerUnit = lowerParser.getUnit();
            }

            if (!StringUtil.hasText(upperParser.getUnit())) {
                upperUnit = lowerUnit;
            } else {
                upperUnit = upperParser.getUnit();
            }

            // Check that the units are set.
            if (!StringUtil.hasText(lowerUnit)) {
                lowerUnit = upperUnit;
            } else if (!StringUtil.hasText(upperUnit)) {
                upperUnit = lowerUnit;
            }

            // Lower unit is frequency, upper unit is not.
            if ((ArrayUtil.matches("^" + lowerUnit + "$",
                                   EnergyValidator.FREQUENCY_UNITS, true) >= 0)
                && (ArrayUtil.matches("^" + upperUnit + "$",
                                      EnergyValidator.FREQUENCY_UNITS, true) < 0)) {
                addFormConstraint(
                        new Number(">= " + lowerValue,
                                   ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
                addFormConstraint(
                        new Number("<= " + upperValue
                                   + (!StringUtil.hasLength(upperUnit) ? "Hz" : ""),
                                   (!StringUtil.hasLength(upperUnit)
                                    ? ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())
                                    : numberEnergyConstraint.getUType())));
            }
            // Upper unit is frequency, lower is not.
            else if ((ArrayUtil.matches("^" + lowerUnit + "$",
                                        EnergyValidator.FREQUENCY_UNITS, true) < 0)
                     && (ArrayUtil.matches("^" + upperUnit + "$",
                                           EnergyValidator.FREQUENCY_UNITS, true) >= 0)) {
                addFormConstraint(
                        new Number(">= " + lowerValue
                                   + (!StringUtil.hasLength(lowerUnit) ? "Hz" : ""),
                                   (!StringUtil.hasLength(lowerUnit)
                                    ? ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())
                                    : numberEnergyConstraint.getUType())));
                addFormConstraint(
                        new Number("<= " + upperValue,
                                   ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
            }
            // Both items are of frequency unit.
            else if (ArrayUtil.matches("^" + lowerUnit + "$",
                                       EnergyValidator.FREQUENCY_UNITS, true) >= 0) {
                addFormConstraint(
                        new Number(numberEnergyConstraint.getFormValue(),
                                   ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
            } else {
                // Normal non-frequency search.
                addFormConstraint(numberEnergyConstraint);
            }
        }
        // Not a range search, just a greater than.
        else if (StringUtil.hasLength(lowerValue)) {
            final NumberParser lowerParser = new NumberParser(lowerValue);
            if (StringUtil.hasLength(lowerParser.getUnit())
                && (ArrayUtil.matches("^" + lowerParser.getUnit() + "$",
                                      EnergyValidator.FREQUENCY_UNITS, true) >= 0)) {
                addFormConstraint(
                        new Number(numberEnergyConstraint.getOperand().getOperand() + " " + lowerValue,
                                   ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
            } else {
                // Normal non-frequency search.
                addFormConstraint(numberEnergyConstraint);
            }
        }
        // Not a range search, just a lower than
        else if (StringUtil.hasLength(upperValue)) {
            final NumberParser upperParser = new NumberParser(upperValue);
            if (StringUtil.hasLength(upperParser.getUnit())
                && (ArrayUtil.matches("^" + upperParser.getUnit() + "$",
                                      EnergyValidator.FREQUENCY_UNITS, true) >= 0)) {
                addFormConstraint(
                        new Number(numberEnergyConstraint.getOperand().getOperand() + " " + upperValue,
                                   ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
            } else {
                // Normal non-frequency search.
                addFormConstraint(numberEnergyConstraint);
            }
        }
        // Basic equals search.
        else if (StringUtil.hasLength(numberEnergyConstraint.getFormValue())) {
            final String value = numberEnergyConstraint.getValue();
            final NumberParser valueParser = new NumberParser(value);
            if (StringUtil.hasLength(valueParser.getUnit())
                && (ArrayUtil.matches("^" + valueParser.getUnit() + "$",
                                      EnergyValidator.FREQUENCY_UNITS, true) >= 0)) {
                addFormConstraint(new Number(value, ENERGY_FREQ_UTYPES.get(numberEnergyConstraint.getUType())));
            } else {
                // Normal non-frequency search.
                addFormConstraint(numberEnergyConstraint);
            }
        }
    }

    /**
     * Add form constraints based on the enumerated inputs from the form.
     *
     * @param job   The Job to use.
     * @param utype The UType to identify this.
     */
    void addEnumeratedFormConstraints(final Job job, final String utype) {
        final List<Parameter> parameterList = job.getParameterList();
        final String[] values = parameterUtil.getValuesAsArray(utype, parameterList);

        if (!ArrayUtil.isEmpty(values) && !(values.length == 1 && values[0].trim().isEmpty())) {
            LOGGER.debug("Enumerated[" + utype + "]");
            try {
                final Constructor<Enumerated> constructor =
                        Enumerated.class.getDeclaredConstructor(Job.class, String.class, String[].class, boolean.class);
                final FormConstraint formConstraint = constructor.newInstance(job, utype, values, false);
                addFormConstraint(formConstraint);
                LOGGER.debug("add " + formConstraint);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating class Enumerated", e);
            }
        }
    }

    /**
     * Add form constraints for the select columns.
     *
     * @param job   The Job to use.
     * @param utype The UType to identify this.
     */
    private void addSelectFormConstraints(final Job job, final String utype) {
        final String[] values = parameterUtil.getValuesAsArray(utype + "@" + Select.NAME, job.getParameterList());

        if (!ArrayUtil.isEmpty(values) && !((values.length == 1) && (values[0].trim().length() == 0))) {
            try {
                final Constructor<Select> constructor =
                        Select.class.getDeclaredConstructor(Job.class, String.class, String[].class, boolean.class);
                final FormConstraint formConstraint = constructor.newInstance(job, utype, values, false);
                addFormConstraint(formConstraint);
                LOGGER.debug("add " + formConstraint);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating class Select", e);
            }
        }
    }

    /**
     * Loops through the forms and checks whether the form is valid.
     * If a form is not valid, the Map of errors from the form is copied
     * into the FormData Map of errors.
     *
     * @param formErrors Form Errors instance.
     * @return boolean true if all forms are valid, false otherwise
     */
    public boolean isValid(final FormErrors formErrors) {
        for (final FormConstraint formConstraint : getAllFormConstraints()) {
            if (formConstraint.isValid(formErrors)) {
                if (formConstraint.getFormValueUnit() != null) {
                    getFormValueUnits().put(formConstraint.getUType(), formConstraint.getFormValueUnit());
                }

                LOGGER.debug("valid form " + formConstraint);
            } else {
                LOGGER.debug("invalid form " + formConstraint + getErrors(formConstraint.getErrorList()));
                getErrorList().addAll(formConstraint.getErrorList());
            }
        }

        return getErrorList().isEmpty();
    }

    /**
     * Add a new Form Constraint.
     *
     * @param formConstraint The constraint to add.
     */
    void addFormConstraint(final FormConstraint formConstraint) {
        getAllFormConstraints().add(formConstraint);
    }

    Collection<FormConstraint> getAllFormConstraints() {
        return formConstraints;
    }

    public Map<String, String> getFormValueUnits() {
        return formValueUnits;
    }

    public List<FormError> getErrorList() {
        return errorList;
    }

    /**
     * Creates a List of all the forms with data that can
     * be used to generate the search templates.
     * <p>
     *
     * @return List of all forms with processable data
     */
    public List<SearchableFormConstraint> getFormConstraints() {
        final List<SearchableFormConstraint> list = new ArrayList<>();

        for (final FormConstraint formConstraint : getAllFormConstraints()) {
            if (formConstraint.hasData()) {
                list.add((SearchableFormConstraint) formConstraint);
                LOGGER.debug("valid data " + formConstraint);
            } else {
                LOGGER.debug("no data " + formConstraint);
            }
        }
        return list;
    }

    /**
     * @return String representation of the FormData forms
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final FormConstraint formConstraint : getAllFormConstraints()) {
            if (formConstraint.hasData()) {
                sb.append(formConstraint).append("\n");
            }
        }

        return sb.toString();
    }

    // Writes all Job parameters and values to the debug log
    private void logParameters(final Job job) {
        for (final Parameter parameter : job.getParameterList()) {
            LOGGER.debug("job " + parameter.getName() + " = " + parameter.getValue());
        }
    }

    // Create a String containing all errors in the errorMap
    private String getErrors(List<FormError> errorList) {
        final StringBuilder sb = new StringBuilder();
        for (final FormError formError : errorList) {
            sb.append(" ");
            sb.append(formError.name);
            sb.append(" - ");
            sb.append(formError.value);
        }
        return sb.toString();
    }
}
