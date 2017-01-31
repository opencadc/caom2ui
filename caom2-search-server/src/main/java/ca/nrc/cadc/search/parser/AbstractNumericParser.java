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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.parser;

import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.util.StringUtil;

/**
 *
 * @author jburke
 */
public abstract class AbstractNumericParser
{
    public static String QUERY_SEPARATER_REGEX = "[\\s,]+";


    private String source;
    private Numeric result;
    private boolean parseUnit;


    /**
     * Complete constructor.
     *
     * @param val       The String value to parse.
     */
    public AbstractNumericParser(final String val)
            throws NumericParserException
    {
        this.parseUnit = true;
        setSource(val);
        parse();
    }

    public AbstractNumericParser()
    {
        this.parseUnit = true;
    }


    /**
     * Parse this parser's source string into this parser's result.
     *
     * @throws NumericParserException   If the source can't be parsed.
     */
    public abstract void parse() throws NumericParserException;

    /**
     * Parse a double number from the given String.  If the given String does
     * not start with a digit or period, then it will not go any further, and
     * return null.
     *
     * @return      Double instance.  Possibly Double.NaN.
     * @throws NumberFormatException  If the found double cannot be parsed.
     */
    protected Double parseDouble() throws NumberFormatException
    {
        Double returnValue;
        final String s = getSource();
        
        // First try the string as a double.
        try
        {
            returnValue = Double.parseDouble(s);
            parseUnit = false;
        }
        catch (NumberFormatException ignore)
        {
            if (isDoubleCharacter(s.charAt(0)))
            {
                final StringBuilder numberStringBuilder = new StringBuilder(8);
                final char[] charArray = s.toCharArray();

                int index = 0;
                char c;

                while ((index < charArray.length)
                       && isDoubleCharacter((c = charArray[index++])))
                {
                    numberStringBuilder.append(c);
                }

                returnValue = Double.parseDouble(numberStringBuilder.toString());
            }
            else
            {
                returnValue = null;
            }
        }

        return returnValue;
    }

    /**
     * Parse the unit from this parser's source.
     *
     * @return  String unit.
     */
    protected String parseUnit()
    {
        final String s = getSource();
        if (parseUnit && StringUtil.hasLength(s))
        {
            final StringBuilder unitStringBuilder = new StringBuilder(8);

            for (final char c : s.toCharArray())
            {
                if (!isDoubleCharacter(c) && !Character.isSpaceChar(c))
                {
                    unitStringBuilder.append(c);
                }
            }

            return unitStringBuilder.toString();
        }
        else
        {
            return null;
        }
    }

    private boolean isDoubleCharacter(final char c)
    {
        return (Character.isDigit(c) || (c == '.'));
    }


    protected void setResult(final Numeric res)
    {
        this.result = res;
    }

    public Numeric getResult()
    {
        return this.result;
    }

    public Number getValue()
    {
        return getResult().value;
    }

    public Number getTolerance()
    {
        return getResult().tolerance;
    }

    public String getUnit()
    {
        return getResult().unit;
    }

    public void setSource(final String val)
    {
        this.source = val;
    }

    protected String getSource()
    {
        return this.source;
    }
}
