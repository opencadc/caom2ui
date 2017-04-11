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


import ca.nrc.cadc.search.parser.AbstractNumericParser;
import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.search.validate.NumberValidator;
import ca.nrc.cadc.search.validate.ValidationException;
import ca.nrc.cadc.util.StringUtil;


/**
 * Class to represent a constraint entered from the form, that displays a range.
 *
 * Things that should always be true:
 * - units for the constraint should be set
 * - set a range for the constraint.  If the form field has a single
 * value entered, set a tiny range to work around equals comparisons with floating
 * point numbers.
 *
 * @author jburke
 */
public class RangeValidation
{
    private AbstractNumericFormConstraint constraint;
    private AbstractNumericParser parser;
    private NumberValidator validator;
    private String normalizedUnits;
    private String formElementValue;

    public RangeValidation(AbstractNumericFormConstraint constraint,
                           AbstractNumericParser parser,
                           NumberValidator validator,
                           String normalizedUnits,
                           String formElementValue)
    {
        this.constraint = constraint;
        this.parser = parser;
        this.validator = validator;
        this.normalizedUnits = normalizedUnits;
        this.formElementValue = formElementValue;
    }

    /**
     * Text is valid if the Energy valueFrom and valueTo have been successfully validated.
     *
     * @return boolean true if form value is valid, false otherwise.
     */
    public boolean isValid(FormErrors formErrors)
    {
        final String utype = constraint.getUType();

        String lowerUnit = null;
        String upperUnit = null;

        try
        {
            if (StringUtil.hasText(constraint.getLowerValue()))
            {
                parser.setSource(constraint.getLowerValue());
                parser.parse();
                if (parser.getValue() != null)
                {
                    constraint.setLowerNumber(parser.getValue());
                }

                if (StringUtil.hasText(parser.getUnit()))
                {
                    lowerUnit = parser.getUnit();
                }
            }

            if (StringUtil.hasText(constraint.getUpperValue()))
            {
                parser.setSource(constraint.getUpperValue());
                parser.parse();
                if (parser.getValue() != null)
                {
                    constraint.setUpperNumber(parser.getValue());
                }

                if (StringUtil.hasText(parser.getUnit()))
                {
                    upperUnit = parser.getUnit();
                }
            }

            if (!StringUtil.hasText(constraint.getLowerValue()) &&
                !StringUtil.hasText(constraint.getUpperValue()) &&
                StringUtil.hasLength(constraint.getFormValue()))
            {
                parser.setSource(constraint.getFormValue());
                parser.parse();

                if (parser.getValue() != null)
                {
                    constraint.setFormValue(Double.toString(
                            parser.getValue().doubleValue()));
                    if (StringUtil.hasText(parser.getUnit()))
                    {
                        lowerUnit = parser.getUnit();
                        upperUnit = lowerUnit;
                    }
                }

            }

        }
        catch (NumericParserException e)
        {
            e.printStackTrace();
            formErrors
                    .set(utype + formElementValue,
                         new FormError(utype + formElementValue,
                                       e.getMessage()));
        }

        // Check that the units are set.
        if (!StringUtil.hasText(lowerUnit) && StringUtil.hasText(upperUnit))
        {
            lowerUnit = upperUnit;
        }
        else if (StringUtil.hasText(lowerUnit)
                 && !StringUtil.hasText(upperUnit))
        {
            upperUnit = lowerUnit;
        }

        if (!StringUtil.hasText(lowerUnit))
        {
            if (!StringUtil.hasLength(constraint.getFormValueUnit()))
            {
                lowerUnit = normalizedUnits;
            }
            else
            {
                lowerUnit = constraint.getFormValueUnit();
            }
        }

        if (!StringUtil.hasText(upperUnit))
        {
            if (!StringUtil.hasLength(constraint.getFormValueUnit()))
            {
                upperUnit = normalizedUnits;
            }
            else
            {
                upperUnit = constraint.getFormValueUnit();
            }
        }

        // Check if a non-default unit was given.
        if (StringUtil.hasText(constraint.getFormValue()) &&
            lowerUnit.equals(upperUnit))
        {
            constraint.setFormValueUnit(lowerUnit);
        }

        try
        {
            // Validate the from unit and normalize the from value.
            if (constraint.getLowerNumber() != null)
            {
                validator.setUtype(utype);
                validator.setValue(constraint.getLowerNumber());
                validator.setUnit(lowerUnit);
                constraint.setLowerNumber(validator.validate());

                // Set the unit to the normalized value.
                constraint.setUnit(constraint.resolveUnit(lowerUnit));
            }

            // If there is a upper value, check the unit and normalize the
            // value.
            if (constraint.getUpperNumber() != null)
            {
                validator.setUtype(utype);
                validator.setValue(constraint.getUpperNumber());
                validator.setUnit(upperUnit);
                constraint.setUpperNumber(validator.validate());

                if (!StringUtil.hasText(constraint.getUnit()))
                {
                    // Set the unit to the normalized value.
                    constraint.setUnit(constraint.resolveUnit(upperUnit));
                }
            }

            if ((constraint.getLowerValue() == null) &&
                (constraint.getUpperValue() == null) &&
                StringUtil.hasText(constraint.getFormValue()) &&
                !formErrors.hasErrors())
            {
                validator.setUtype(utype);
                validator.setValue(Double.parseDouble(
                        constraint.getFormValue()));
                validator.setUnit(constraint.getFormValueUnit());
                constraint.setFormValue(
                        Double.toString(validator.validate()));
                constraint.setUnit(constraint.resolveUnit(
                        constraint.getFormValueUnit()));

                // treat the single value like a range
                constraint.setLowerNumber(
                        Double.parseDouble(constraint.getFormValue()));
                constraint.setUpperNumber(
                        Double.parseDouble(constraint.getFormValue()));

            }
        }
        catch (ValidationException e)
        {
            formErrors
                    .set(utype + formElementValue,
                         new FormError(utype + formElementValue,
                                       e.getMessage()));
        }

        if (!formErrors.hasErrors())
        {
            constraint.swapTrueValuesIfNecessary();
        }

        return !formErrors.hasErrors();
    }

    /**
     * Text has processable data if the Time valueFrom is not null.
     *
     * @return boolean true if form contains a processable value, false otherwise.
     */
    public boolean hasData()
    {
        return (constraint.getLowerNumber() != null)
               || (constraint.getUpperNumber() != null);
    }

    /**
     * @return String representation of the Time form.
     */
    @Override
    public String toString()
    {
        return "RangeValidation[" + constraint.getUType() + ", " + constraint
                .getLowerNumber() + ", " + constraint
                       .getUpperNumber() + ", " + constraint.getUnit() + "]";
    }

}
