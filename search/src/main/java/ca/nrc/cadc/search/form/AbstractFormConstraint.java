/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 8/26/11 - 11:35 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.form;

import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.Range;
import ca.nrc.cadc.search.parser.RangeParser;
import ca.nrc.cadc.search.parser.exception.RangeParserException;
import ca.nrc.cadc.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractFormConstraint implements FormConstraint
{
    // The unique characteristic of this constraint.
    private final String uType;

    // Form value
    private String formValue = "";

    // Unit parsed from the formValue
    private String formValueUnit;
    
    // Operand for this constraint.
//    private Operand operand;
    
    private Range<String> range;

    // List of validation errors
    private List<FormError> errorList;


    protected AbstractFormConstraint(final String uType)
    {
        this.uType = uType;
        this.formValueUnit = null;
    }

    /**
     * List containing errors encountered during form validation. The
     * List contains FormError objects, where the name is the form element name,
     * and the value is the error message displayed on the form.
     *
     * @return Map containing validation errors.
     */
    @Override
    public List<FormError> getErrorList()
    {
        if (errorList == null)
        {
            errorList = new ArrayList<>();
        }

        return errorList;
    }

    protected void addError(final FormError formError)
    {
        getErrorList().add(formError);
    }

    public String getLowerValue()
    {
        if (getRange() == null)
        {
            throw new IllegalStateException("formValue must be set");
        }

        return getRange().getLowerValue();
    }

    public String getUpperValue()
    {
        if (getRange() == null)
        {
            throw new IllegalStateException("formValue must be set");
        }

        return getRange().getUpperValue();
    }

    /**
     * Obtain the actual value for an EQUALS operand search criterion.
     * 
     * @return      String base value, or empty string.
     */
    public String getValue()
    {
        if (getRange() == null)
        {
            throw new IllegalStateException("formValue must be set");
        }

        return getRange().getValue();
    }

    /**
     * Obtain the operand used.
     *
     * @return Operand instance.  Defaults to EQUALS.
     * @see ca.nrc.cadc.search.parser.Operand
     */
    @Override
    public Operand getOperand()
    {
        if (getRange() == null)
        {
            throw new IllegalStateException("formValue must be set");
        }

        return getRange().getOperand();
    }

    public void setOperand(final Operand operand)
    {
        if (getRange() == null)
        {
            throw new IllegalStateException("formValue must be set");
        }

        try
        {
            parseRange(operand);
        }
        catch (RangeParserException e)
        {
            setRange(new Range<String>(getFormValue(), null, null, null,
                                       operand));
        }
    }

    /**
     * A form has data when it has processable values. A form has
     * processable values when all of the form values are valid,
     * and at least one of the form values is not null. It is up
     * to the individual implementation to determine what form
     * values are required for a form to have processable data.
     *
     * @return boolean true if form contains processable values, false otherwise.
     */
    @Override
    public boolean hasData()
    {
        return StringUtil.hasLength(getFormValue());
    }

    @Override
    public String getFormValue()
    {
        return formValue;
    }

    protected void setFormValue(final String formValue)
    {
        if (formValue == null)
        {
            this.formValue = "";
        }
        else
        {
            this.formValue = formValue.trim();
        }

        try
        {
            parseRange(null);
        }
        catch (RangeParserException e)
        {
            setRange(new Range<>(null, getFormValue(), null, null,
                                 Operand.EQUALS));
        }
    }

    protected void parseRange(final Operand operand) throws RangeParserException
    {
        final RangeParser rangeParser = new RangeParser(getFormValue(),
                                                        operand);
        setRange(rangeParser.parse());
    }

    @Override
    public String getFormValueUnit()
    {
        return formValueUnit;
    }

    public void setFormValueUnit(final String formValueUnit)
    {
        this.formValueUnit = formValueUnit;
    }
    
    @Override
    public String getUType()
    {
        return uType;
    }

    public Range<String> getRange()
    {
        return range;
    }

    protected void setRange(final Range<String> range)
    {
        this.range = range;
    }
}
