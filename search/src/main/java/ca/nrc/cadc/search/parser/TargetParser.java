/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.				(c) 2013.
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

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.search.parser.exception.TargetParserException;
import ca.nrc.cadc.util.StringUtil;


/**
 * ra [sep] dec [sep] [radius[unit]] [sep] [coordsys]
 * <p></p>
 * Class takes a String target value and attempts to parse
 * the target into R.A., Dec., and radius values. If it is
 * unable to parse the target into coordinates, it passes
 * the target to a name resolver to retrieve the coordinates.
 * <p></p>
 * If the name is simply an Object Name rather than a name that can be parsed,
 * then attempt all resolution, then give up.
 * <p></p>
 * 1 token is resolved to coordinates.
 * 2 tokens are a RA and DEC.
 * 3 tokens are a RA, DEC, and radius or coordinate system.
 * 4 tokens are a RA, DEC, radius and coordinate system. TODO: 4 tokens could be 2 sexagesimal
 * coordinates missing the seconds.
 * 5 tokens are ambiguous and not parsed.
 * Chris: TODO list: 5 tokens as 2 sexagesimal coordinates missing the seconds and a radius or coordinate system.
 * 6 tokens are a RA and DEC in sexagesimal.
 * 7 tokens are a RA and DEC in sexagesimal and a radius or coordinate system.
 * 8 tokens are a RA and DEC in sexagesimal and a radius and a coordinate system.
 *
 * @author jburke
 */
public class TargetParser
{
    private static final Logger log = Logger.getLogger(TargetParser.class);

    private static final String DEGREE_SYMBOL =
            Character.toString((char) 0x00b0);
    private static final String RA_DEC_SEPARATORS =
            "'\"dhmsDHMS:" + DEGREE_SYMBOL;

    private final Resolver resolver;


    /**
     * Complete target constructor.
     *
     * @param resolver     The resolver to use when resolving is needed.
     */
    public TargetParser(final Resolver resolver)
    {
        this.resolver = resolver;
    }


    /**
     * Attempts to determine the coordinates of the query.
     *
     * @param target        The String target.
     * @param resolver      The resolver to use.
     *
     * @return Target Data for this parser's information.
     * @throws TargetParserException If the query is null or cannot be parsed.
     */
    public TargetData parse(final String target, final String resolver)
            throws TargetParserException
    {
        if (!StringUtil.hasText(target))
        {
            throw new TargetParserException("Null or empty target");
        }
        else if (!StringUtil.hasText(resolver))
        {
            throw new TargetParserException("Null or empty resolver");
        }

        return parseTarget(target, resolver);
    }

    /**
     * Clean up the input for processing.
     *
     * @param target The target value entered.
     * @return The sanitized value.
     */
    protected String sanitizeTarget(final String target)
    {
        return target.replace(",", "").replaceAll("(\\s*)(\\.{2,})(\\s*)",
                                                  "$2");
    }

    /**
     * Check if query is asking for degrees.
     *
     * @param query     The query to check.
     * @return      True if degrees, false otherwise.
     */
    public boolean isQueryInDegrees(final String query)
    {
        boolean raIsDegrees = true;
        boolean decIsDegrees = true;

        // Try and split into RA and DEC.
        final String temp = sanitizeTarget(query);
        String[] parts = temp.trim().split("\\s+");

        // More than 4 arguments, must be sexigesimal.
        if (parts.length > 4)
        {
            return false;
        }

        // Check if the RA is a single value.
        if (parts.length > 0 && parts[0] != null)
        {
            String s = parts[0].trim();
            StringTokenizer st = new StringTokenizer(s, RA_DEC_SEPARATORS,
                                                     true);
            raIsDegrees = (st.countTokens() == 1);
        }

        // Check if the DEC is a single value.
        if (parts.length > 1 && parts[1] != null)
        {
            String s = parts[1].trim();
            StringTokenizer st = new StringTokenizer(s, RA_DEC_SEPARATORS,
                                                     true);
            decIsDegrees = (st.countTokens() == 1);
        }

        return (raIsDegrees || decIsDegrees);
    }

    /**
     * Attempts to parse the target string into R.A., Dec., and radius. If that fails
     * then try to resolve the target into coordinates using a name resolver.
     *
     * @param target            The String target.
     * @param resolverName      The resolver to use.
     *
     * @throws TargetParserException If the query is null or empty, or an
     *                               error in parsing occurs.
     */
    private TargetData parseTarget(final String target,
                                   final String resolverName)
            throws TargetParserException
    {
        TargetData targetData;

        log.debug("parse: " + target);

        try
        {
            final PositionParser parser = new PositionParser();
            targetData = parser.parse(target);

            log.debug(parser);
        }
        catch (PositionParserException e)
        {
            try
            {
                targetData = resolver.resolveTarget(target, resolverName);

                if (targetData == null)
                {
                    throw new TargetParserException("Unable to resolve: "
                                                    + target, e,
                                                    TargetParserException.
                                                            ExceptionType.
                                                            NAMERESOLVER_TARGET_NOT_FOUND);
                }
            }
            catch (IOException re)
            {
                throw new TargetParserException("Unable to resplve "
                                                + target);
            }
        }
        catch (IllegalArgumentException iae)
        {
            throw new TargetParserException("Illegal argument for target " +
                                            iae.getMessage());
        }

        return targetData;
    }
}
