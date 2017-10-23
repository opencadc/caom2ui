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
import ca.nrc.cadc.search.parser.exception.TimeParserException;
import ca.nrc.cadc.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * Parser to parse out Time entries.  Specifically those with units.
 *
 * @author jburke
 */
public class TimeParser extends AbstractNumericParser
{
    private static Logger LOGGER = Logger.getLogger(TimeParser.class);


    /**
     * Complete constructor.
     *
     * @param val The String value to parse.
     * @throws  NumericParserException      For number parsing errors.
     */
    public TimeParser(final String val) throws NumericParserException
    {
        super(val);
    }

    public TimeParser()
    {
        super();
    }


    @Override
    public void parse() throws NumericParserException
    {
        final String query = getSource();

        LOGGER.debug("parse: " + query);

        // Check for an zero length string.
        if (!StringUtil.hasText(query))
        {
            throw new TimeParserException("Given String is empty.");
        }

        // Returned value and unit.
        final Numeric time = new Numeric();

        // Split the string on the query separator.
        final String[] tokens = query.split(QUERY_SEPARATER_REGEX);

        // Should have 1 or 2 tokens for time.
        if ((tokens.length == 0) || (tokens.length > 2))
        {
            throw new TimeParserException(
                    "Invalid number of arguments to parse: " + tokens.length);
        }

        // First token could be value, or value and unit.
        if (tokens.length >= 1)
        {
            // Try as an value and unit.
            final NumberParser numberParser = new NumberParser(tokens[0]);

            if (numberParser.getValue() == null)
            {
                throw new TimeParserException("Unable to parse query " + query);
            }
            else
            {
                time.value = numberParser.getValue();
                time.unit = numberParser.getUnit();
            }
        }

        // Second token must be a unit.
        if (tokens.length >= 2)
        {
            if (!StringUtil.hasText(time.unit))
            {
                final NumberParser numberParser = new NumberParser(tokens[1]);

                if ((numberParser.getUnit() == null)
                    || (numberParser.getValue() != null))
                {
                    throw new TimeParserException(
                            "Unable to parse query for units in " + query);
                }
                else
                {
                    time.unit = numberParser.getUnit();
                }
            }
            else
            {
                throw new TimeParserException("Unable to parse query " + query);
            }
        }

        // Check that at a value has been parsed.
        if (time.value == null)
        {
            throw new TimeParserException("Unable to parse query " + query);
        }

        setResult(time);

        LOGGER.debug(time);
    }
}
