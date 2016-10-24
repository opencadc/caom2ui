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
 * 12/15/11 - 1:46 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.form;

import java.lang.Number;

public abstract class AbstractNumericFormConstraint
        extends AbstractFormConstraint 
{
    // Normalized unit value
    protected String unit;

    // Validated and normalized Number lower value
    private Number lowerNumber;

    // Validated and normalized Number upper value
    private Number upperNumber;


    protected AbstractNumericFormConstraint(final String utype)
    {
        super(utype);

        lowerNumber = null;
        upperNumber = null;
        unit = null;
    }

    /**
     * Obtain the validated and normalized numeric lower value
     * @return      Number instance.
     */
    public Number getLowerNumber()
    {
        return lowerNumber;
    }

    /**
     * Set the validated and normalized numeric lower value
     * @param valueLower      The lower (from) value.
     */
    public void setLowerNumber(Number valueLower)
    {
        this.lowerNumber = valueLower;
    }

    /**
     * Obtain the validated and normalized numeric upper value
     * @return      Number instance.
     */
    public Number getUpperNumber()
    {
        return upperNumber;
    }

    /**
     * Set the validated and normalized numeric upper value
     * @param valueUpper      The upper (to) value.
     */
    public void setUpperNumber(Number valueUpper)
    {
        this.upperNumber = valueUpper;
    }

    /**
     * Swap this constraint's numbers, if the lower is greater than the upper.
     * This comparison will be done using the Numbers double value.
     */
    protected void swapTrueValuesIfNecessary()
    {
        if ((getLowerNumber() != null) && (getUpperNumber() != null)
            && (getLowerNumber().doubleValue() > getUpperNumber().doubleValue()))
        {
            swapTrueValues();
        }
    }

    /**
     * Swap the values.
     */
    protected void swapTrueValues()
    {
        final Number n = getLowerNumber();

        setLowerNumber(getUpperNumber());
        setUpperNumber(n);
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public abstract String resolveUnit(final String forUnit);
}
