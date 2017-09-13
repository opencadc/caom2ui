/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2012.                         (c) 2012.
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
 * 5/24/12 - 3:20 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.parser;

import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.util.StringUtil;

import org.apache.log4j.Logger;
import java.util.regex.PatternSyntaxException;


/**
 * Accepts a radius entry and parses it out.
 */
public class RadiusParser extends AbstractNumericParser
{
    private static final Logger LOGGER = Logger.getLogger(RadiusParser.class);
    private static final double MAX_RADIUS = 90.0;    


    private String radiusFormat;

    
    /**
     * Complete constructor.
     *
     * @param val The String value to parse.
     */
    public RadiusParser(final String val) throws NumericParserException
    {
        super(val);
    }


    @Override
    public void parse() throws NumericParserException
    {
        final Numeric result = new Numeric();
        final String rad = getSource();

        if (StringUtil.hasText(rad))
        {
            try
            {
                final Double radiusValue = parseDouble();
                final String radiusUnit = parseUnit();

                if (!StringUtil.hasText(radiusUnit)
                    || radiusUnit.matches("^[dD]+[eE]?[gG]?.*$"))
                {
                    LOGGER.debug("parseRadius: " + rad
                                 + " looks like a number in degrees");
                    result.value = radiusValue;
                }
                else if (radiusUnit.matches("\"|''|^[aA]+[rR]?[cC]?[sS]+.*$"))
                {
                    LOGGER.debug("parseRadius: " + rad
                                 + " looks like a number in arcsec");

                    result.value = radiusValue / 3600.0;

                    setRadiusFormat(buildNumberFormat(radiusUnit, 6));
                }
                else if (radiusUnit.matches("'|^[aA]+[rR]?[cC]?[mM]+.*$"))
                {
                    LOGGER.debug("parseRadius: " + rad
                                 + " looks like a number in arcmin");

                    result.value = radiusValue / 60.0;

                    setRadiusFormat(buildNumberFormat(radiusUnit, 4));
                }

                LOGGER.debug("parseRadius: " + rad
                          + " did not match any pattern/expectations");
            }
            catch (PatternSyntaxException pse)
            {
                LOGGER.debug("Pattern syntax error for radius " + rad
                          + pse.getMessage());
            }
            catch (NumberFormatException nfe)
            {
                LOGGER.debug("Parsing error for radius " + rad + nfe.getMessage());
            }
        }

        if ((result.value != null)
            && (new Double(result.value.doubleValue()).compareTo(MAX_RADIUS)
                > 0))
        {
            throw new NumericParserException("Radius is out of range.");
        }
        else
        {
            setResult(result);
        }
    }

    /**
     * Creates a number format for the given String.
     *
     * @param s         Create a human readable number for the given string.
     * @param pad       How any decimal places to pad.
     * @return          String with numeric content.
     */
    private String buildNumberFormat(final String s, final int pad)
    {
        int decimals = pad;
        String format = "##0";

        int index = s.indexOf('.');
        if (index != -1)
        {
            int len = s.length();
            if (len > index + 1)
            {
                decimals += len - (index + 1);
            }
        }

        if (decimals > 0)
        {
            format += ".";
        }

        for (int i = 0; i < decimals; i++)
        {
            format += "0";
        }

        return format;
    }


    public String getRadiusFormat()
    {
        return radiusFormat;
    }

    private void setRadiusFormat(final String radiusFormat)
    {
        this.radiusFormat = radiusFormat;
    }
}
