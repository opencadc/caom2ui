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

import java.util.List;


import org.apache.log4j.Logger;

import ca.nrc.cadc.caom2.NumericSearch;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.parser.EnergyParser;
import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.Range;
import ca.nrc.cadc.search.parser.TimeParser;
import ca.nrc.cadc.search.validate.EnergyValidator;
import ca.nrc.cadc.search.validate.PropertyValidator;
import ca.nrc.cadc.search.validate.TimeValidator;
import ca.nrc.cadc.search.validate.ValidationException;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;


/**
 * Class to represent an Number form component.
 *
 * @author jburke
 */
public class Number extends AbstractNumericFormConstraint
        implements SearchableFormConstraint {

    private static Logger log = Logger.getLogger(Number.class);

    // Constants used to construct name for form elements.
    public static final String NAME = "@Number";
    public static final String VALUE = "@Number.value";

    // Energy values are normalized to meters.
    public static String TIME_NORMALIZED_UNITS = "s";


    /**
     * Constructor used for testing.
     *
     * @param utype The utype.
     */
    Number(final String utype) {
        super(utype);
    }

    /**
     * Good, basic constructor that will do what we need it to.
     *
     * @param value The value to set.
     * @param utype This constraint's utype.
     */
    public Number(final String value, final String utype) {
        super(utype);
        setFormValue(value);
    }

    /**
     * Number constructor instantiates a new instance with the given parameters.
     *
     * @param job   The UWS Job.
     * @param utype The utype of the form.
     */
    public Number(final Job job, final String utype) {
        super(utype);
        for (final Parameter parameter : job.getParameterList()) {
            if (parameter.getName().equals(getUType())) {
                setFormValue(parameter.getValue());
            }
        }
    }

    // Create a NumericSearch to SearchTemplates.
    public SearchTemplate buildSearch(List<FormError> errorList) {
        SearchTemplate template = null;

        try {
            final Operand operand = this.getOperand();

            if ((this.getLowerNumber() == null)
                    && (this.getUpperNumber() == null)) {
                template = new NumericSearch(this.getUType(),
                                             Double.parseDouble(
                                                     this.getFormValue()));
            } else {
                template = new NumericSearch(this.getUType(),
                                             this.getLowerNumber(),
                                             this.getUpperNumber(),
                                             operand.equals(
                                                     Operand.LESS_THAN_EQUALS)
                                                     || operand.equals(Operand.RANGE),
                                             operand.equals(
                                                     Operand.GREATER_THAN_EQUALS)
                                                     || operand.equals(Operand.RANGE)
                );
            }
        } catch (IllegalArgumentException e) {
            errorList.add(new FormError(Number.NAME, e.getMessage()));
            log.debug("Invalid Number parameters: " + e.getMessage() + " "
                              + this.toString());
        }

        return template;
    }

    /**
     * Number is valid if the Number lower and upper values
     * have been successfully validated.
     *
     * @return boolean true if form values are valid, false otherwise.
     */
    public boolean isValid(final FormErrors formErrors) {
        final String utype = getUType();

        if (super.hasData()) {
            try {
                if (ObsModel.isTimeUtype(utype) || ObsModel.isAngleUtype(utype)) {
                    validateTime(utype, formErrors);
                } else if (ObsModel.isEnergyUtype(utype)) {
                    validateEnergy(utype, formErrors);
                } else {
                    validateProperty(utype);
                }
            } catch (NumberFormatException | ValidationException e) {
                formErrors.set(utype + VALUE,
                               new FormError(utype + VALUE, e.getMessage()));
            }
        }

        return !formErrors.hasErrors();
    }

    /**
     * Number has processable data if neither the Number
     * lower or upper value are null. A null lower or
     * upper value implies an open ended search.
     *
     * @return boolean true if form contains a processable value,
     * false  otherwise.
     */
    @Override
    public boolean hasData() {
        return super.hasData() || (getLowerNumber() != null || getUpperNumber() != null);
    }

    /**
     * @return String representation of the Number form.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Number[");
        sb.append(getUType());
        sb.append(", ");
        sb.append(getLowerNumber());
        sb.append(", ");
        sb.append(getUpperNumber());
        sb.append(", ");

        if (getUnit() != null) {
            sb.append(getUnit());
        }

        sb.append("]");

        return sb.toString();
    }

    private void validateTime(final String utype, final FormErrors formErrors) {
        Time time = new Time(getFormValue(), utype);
        TimeParser timeParser = new TimeParser();
        TimeValidator timeValidator = new TimeValidator();
        String normalizedUnit =
                utype.equals("Plane.time.bounds.width") ? "d" : "s";
        new RangeValidation(time, timeParser, timeValidator,
                            normalizedUnit, VALUE)
                .isValid(formErrors);

        this.setUnit(time.getUnit());
        this.setFormValue(time.getFormValue());
        this.setFormValueUnit(time.getFormValueUnit());
        this.setLowerNumber(time.getLowerNumber());
        this.setUpperNumber(time.getUpperNumber());
    }

    private void validateEnergy(final String utype, final FormErrors formErrors) {
        Energy energy = new Energy(getFormValue(), utype);
        EnergyParser energyParser = new EnergyParser();
        EnergyValidator energyValidator = new EnergyValidator();
        new RangeValidation(energy, energyParser, energyValidator,
                            Energy.NORMALIZED_UNITS, Energy.VALUE)
                .isValid(formErrors);

        this.setUnit(energy.getUnit());
        this.setFormValue(energy.getFormValue());
        this.setFormValueUnit(energy.getFormValueUnit());

        if (isNotARange(energy.getRange())) {
            Double formValue = Double.parseDouble(energy.getFormValue());
            Double delta = formValue / 200;
            Double valueLower = formValue - delta;
            Double valueUpper = formValue + delta;
            energy.setLowerNumber(valueLower);
            energy.setUpperNumber(valueUpper);
            energy.setRange(constructRange(valueLower, valueUpper));
        }

        this.setLowerNumber(energy.getLowerNumber());
        this.setUpperNumber(energy.getUpperNumber());
    }

    /**
     *
     */
    private boolean isNotARange(final Range<String> range) {
        return Operand.EQUALS.equals(range.getOperand())
                && StringUtil.hasLength(this.getFormValue());
    }

    /**
     *
     */
    private Range<String> constructRange(final Double valueLower,
                                         final Double valueUpper) {
        final String lowerString = String.valueOf(valueLower);
        final String upperString = String.valueOf(valueUpper);

        return new Range<>(Operand.RANGE.toString(), null, lowerString,
                           upperString, Operand.RANGE);
    }

    @Override
    public String resolveUnit(final String forUnit) {
        return forUnit;
    }


    private void validateProperty(final String utype)
            throws ValidationException {
        final PropertyValidator validator = new PropertyValidator(utype);
        setLowerNumber(validator.validate(getLowerValue()));
        setUpperNumber(validator.validate(getUpperValue()));

        if ((getLowerNumber() == null && getUpperNumber() == null)
                && super.hasData()) {
            setLowerNumber(validator.validate(getFormValue()));
        }
    }

}
