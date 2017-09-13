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
package ca.nrc.cadc.search.parser;

import ca.nrc.cadc.search.parser.exception.RangeParserException;
import ca.nrc.cadc.util.StringUtil;


/**
 * @author jburke
 */
public class RangeParser
{
    private final String rangeValue;
    private Operand operand;


    public RangeParser(final String _rangeValue, final Operand _operand)
    {
        this.rangeValue = _rangeValue;
        this.operand = _operand;
    }

    /**
     * Attempts to parse an input into a range of strings
     * that contains the given operand.
     *
     * @return a Range containing the parsed values.
     * @throws ca.nrc.cadc.search.parser.exception.RangeParserException
     *              if the input doesn't contain the given operand or unable
     *              to parse the input into a range.
     */
    public Range<String> parse() throws RangeParserException
    {
        final String currRangeValue = getRangeValue();

        if (!StringUtil.hasText(currRangeValue))
        {
            final String message = "null or empty range";
            throw new RangeParserException(message);
        }

        final Operand op = (getOperand() == null) ? parseOperand()
                                                  : getOperand();

        if (op == null)
        {
            final String message = "Unable to parse operand from range " +
                                   currRangeValue;
            throw new RangeParserException(message);
        }

        String value = null;
        String lower = null;
        String upper = null;

        final String operandValue = op.getOperand();
        final int index = currRangeValue.lastIndexOf(operandValue);

        if (index == -1)
        {
            final String message = "operand " + operandValue +
                                   " not found in range " + currRangeValue;
            throw new RangeParserException(message);
        }

        if (op == Operand.RANGE)
        {
            lower = currRangeValue.substring(0, index).trim();
            upper = currRangeValue.substring(index
                                             + operandValue.length()).trim();
        }
        else if (op.getOperand().startsWith(Operand.LESS_THAN.getOperand()))
        {
            upper = currRangeValue.substring(index
                                             + operandValue.length()).trim();
        }
        else if (op.getOperand().startsWith(Operand.GREATER_THAN.getOperand()))
        {
            lower = currRangeValue.substring(index
                                             + operandValue.length()).trim();
        }
        else if (op == Operand.EQUALS)
        {
            value = currRangeValue.substring(index
                                             + operandValue.length()).trim();
        }

        if (value == null && lower == null && upper == null)
        {
            final String message = "Unable to parse range " + currRangeValue;
            throw new RangeParserException(message);
        }

        return new Range<String>(currRangeValue, value, lower, upper, op);
    }

    /**
     * Attempts to parse an input into a range of doubles
     * that contains the given operand.
     *
     * @return a Range containing the parsed values.
     * @throws ca.nrc.cadc.search.parser.exception.RangeParserException
     *              if the input doesn't contain the given operand or unable
     *              to parse the input into a range.
     */
    public Range<Double> parseDouble() throws RangeParserException
    {
        final Range<String> sRange = parse();
        final Double value;
        final Double lower;
        final Double upper;
        try
        {
            if (sRange.getValue() == null)
            {
                value = null;
            }
            else
            {
                value = Double.valueOf(sRange.getValue());
            }
            if (sRange.getLowerValue() == null)
            {
                lower = null;
            }
            else
            {
                lower = Double.valueOf(sRange.getLowerValue());
            }
            if (sRange.getUpperValue() == null)
            {
                upper = null;
            }
            else
            {
                upper = Double.valueOf(sRange.getUpperValue());
            }
        }
        catch (NumberFormatException e)
        {
            final String message = "Unable to parse range from "
                                   + getRangeValue() + " because "
                                   + e.getMessage();
            throw new RangeParserException(message);
        }

        return new Range<Double>(getRangeValue(), value, lower, upper,
                                 sRange.getOperand());
    }

    /**
     * Removes single spaces from either side of a range operand
     * in the given value.
     *
     * @param value the value to trim.
     * @return value with no spaces around the range operand.
     */
    public static String trimRange(final String value)
    {
        if (!StringUtil.hasText(value))
        {
            return value;
        }

        int fromIndex = 0;
        int index = value.indexOf(Operand.RANGE.getOperand(), fromIndex);
        if (index == -1)
        {
            return value;
        }

        final StringBuilder sb = new StringBuilder();

        // Check for spaces on either side on the operand.
        if (value.charAt(index - 1) == ' ')
        {
            sb.append(value.substring(0, index - 1));
        }
        else
        {
            sb.append(value.substring(0, index));
        }
        sb.append(Operand.RANGE.getOperand());

        fromIndex = index + 2;
        if (value.charAt(index + Operand.RANGE.getOperand().length()) == ' ')
        {
            fromIndex++;
        }

        index = value.indexOf(Operand.RANGE.getOperand(), fromIndex);
        if (index != -1)
        {
            // Check for spaces on either side on the operand.
            if (value.charAt(index - 1) == ' ')
            {
                sb.append(value.substring(0, index - 1));
            }
            else
            {
                sb.append(value.substring(0, index));
            }
            sb.append(Operand.RANGE.getOperand());

            fromIndex = index + 2;
            if (value.charAt(index + 3) == ' ')
            {
                fromIndex++;
            }
            sb.append(value.substring(fromIndex));
        }
        sb.append(value.substring(fromIndex));

        return sb.toString();
    }

    /**
     * Parse out the Operand used in the field constraint.
     *
     *
     * @return Operand for this range.
     */
    protected Operand parseOperand()
    {
        final String range = getRangeValue();
        final Operand op;

        if (!StringUtil.hasText(range))
        {
            op = null;
        }
        else
        {
            if (range.contains(Operand.RANGE.getOperand()))
            {
                op = Operand.RANGE;
            }
            else if (range.startsWith(Operand.LESS_THAN_EQUALS.getOperand()))
            {
                op = Operand.LESS_THAN_EQUALS;
            }
            else if (range.startsWith(Operand.GREATER_THAN_EQUALS.getOperand()))
            {
                op = Operand.GREATER_THAN_EQUALS;
            }
            else if (range.startsWith(Operand.LESS_THAN.getOperand()))
            {
                op = Operand.LESS_THAN;
            }
            else if (range.startsWith(Operand.GREATER_THAN.getOperand()))
            {
                op = Operand.GREATER_THAN;
            }
            else if (range.startsWith(Operand.EQUALS.getOperand()))
            {
                op = Operand.EQUALS;
            }
            else
            {
                op = null;
            }
        }

        return op;
    }

    public String getRangeValue()
    {
        return rangeValue;
    }

    public Operand getOperand()
    {
        return operand;
    }

    protected void setOperand(final Operand _operand)
    {
        this.operand = _operand;
    }
}
