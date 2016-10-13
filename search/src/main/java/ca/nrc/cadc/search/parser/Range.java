/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                           (c) 2013.
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

package ca.nrc.cadc.search.parser;

/**
 * Simple class to capture the state of a range of values.
 * 
 * @author jburke
 * @param <T>
 */
public class Range<T extends java.lang.Comparable<T>>
{
    private final String range;
    private final T value;
    private final T lowerValue;
    private final T upperValue;
    private final Operand operand;


    /**
     * 
     * @param range the unparsed range
     * @param value single valued range
     * @param lowerValue lower range value
     * @param upperValue upper range value
     * @param operand the range operand
     */
    public Range(final String range, final T value, final T lowerValue,
                 final T upperValue, final Operand operand)
    {
        this.range = range;
        this.value = value;
        this.lowerValue = lowerValue;
        this.upperValue = upperValue;
        this.operand = operand;
    }


    /**
     * The unparsed range.
     * 
     * @return unparsed range.
     */
    public String getRange()
    {
        return range;
    }
    
    /**
     * Single valued range. Shall be null if the range has
     * lower and upper values.
     * 
     * @return single range value
     */
    public T getValue()
    {
        return value;
    }
    
    /**
     * Lower or min range value. Shall be null if the range
     * is single valued.
     * 
     * @return lower range value.
     */
    public T getLowerValue()
    {
        return lowerValue;
    }
    
    /**
     * Upper or max range value. Shall be null if the range
     * is single valued.
     * 
     * @return upper range value.
     */
    public T getUpperValue()
    {
        return upperValue;
    }
    
    /**
     * The range operand. Should not be null.
     * 
     * @return the range operand.
     */
    public Operand getOperand()
    {
        return operand;
    }


    @Override
    public String toString()
    {
        return "Range[" + range + "," + (value == null ? "" : value) + ","
               + (lowerValue == null ? "" : lowerValue) + ","
               + (upperValue == null ? "" : upperValue) + ","
               + (operand == null ? "" : operand.getOperand()) + "]";
    }
    
}
