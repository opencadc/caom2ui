/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits r√©serv√©s
*
*  NRC disclaims any warranties,        Le CNRC d√©nie toute garantie
*  expressed, implied, or               √©nonc√©e, implicite ou l√©gale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           √™tre tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou g√©n√©ral,
*  arising from the use of the          accessoire ou fortuit, r√©sultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        √™tre utilis√©s pour approuver ou
*  products derived from this           promouvoir les produits d√©riv√©s
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  pr√©alable et particuli√®re
*                                       par √©crit.
*
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.search.parser;

import ca.nrc.cadc.search.parser.exception.EnergyParserException;
import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.util.StringUtil;
import org.apache.log4j.Logger;


/**
 * %SEPARATOR% ∈ (%SPACE%, comma)
 * %ENERGY% ::= %value%[[%SPACE%]%UNIT%]
 * %ENERGY_SPEC% ::= %NAME%
 * ::= %ENERGY_VALUE%
 * ::= %ENERGY_VALUE%%SEPARATOR%%TOLERANCE%
 * ::= %ENERGY_VALUE%%SEPARATOR%%ENERGY_VALUE%
 * ::= %ENERGY_VALUE%%SEPARATOR%%ENERGY_VALUE%%SEPARATOR%%TOLERANCE%
 *
 * @author jburke
 */
public class EnergyParser extends AbstractNumericParser
{
    private static Logger LOGGER = Logger.getLogger(EnergyParser.class);


    /**
     * Complete constructor.
     *
     * @param val The String value to parse.
     */
    public EnergyParser(final String val) throws NumericParserException
    {
        super(val);
    }

    public EnergyParser()
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
            throw new EnergyParserException("Given String is empty.");
        }

        // Returned value, tolerance, and unit.
        Numeric energy = new Numeric();

        // Split the string on the query separator.
        String[] tokens = query.split(QUERY_SEPARATER_REGEX);

        // Should have 1, 2, or 3 tokens for energy.
        if (tokens.length == 0 || tokens.length > 3)
        {
            throw new EnergyParserException(
                    "Invalid number of arguments to parse: " + tokens.length);
        }

        // First token could be name, energy, or energy and unit.
        if (tokens.length >= 1)
        {
            // Try as an energy and unit.
            final NumberParser numberParser = new NumberParser(tokens[0]);
            final Numeric numeric = numberParser.getResult();

            if (numeric.value == null)
            {
                throw new EnergyParserException(
                        "Unable to parse query " + query);
            }

            energy.value = numeric.value;
            energy.unit = numeric.unit;
        }

        // Second token must be unit or tolerance.
        if (tokens.length >= 2)
        {
            final NumberParser numberParser = new NumberParser(tokens[1]);
            final Numeric numeric = numberParser.getResult();
            if (!StringUtil.hasText(energy.unit))
            {
                if (numeric.unit == null)
                {
                    throw new EnergyParserException(
                            "Unable to parse query for units in " + query);
                }
                else
                {
                    energy.unit = numeric.unit;
                }
            }
            else
            {
                if (numeric.value == null)
                {
                    throw new EnergyParserException(
                            "Unable to parse query for tolerance in " + query);
                }
                else
                {
                    energy.tolerance = numeric.value;
                }
            }
        }

        // Third token must be tolerance.
        if (tokens.length >= 3)
        {
            if (energy.tolerance != null)
            {
                throw new EnergyParserException(
                        "Unable to parse query for tolerance in " + query);
            }

            final NumberParser numberParser = new NumberParser(tokens[2]);
            final Numeric numeric = numberParser.getResult();

            if (numeric.value == null)
            {
                throw new EnergyParserException(
                        "Unable to parse query for tolerance in " + query);
            }
            else
            {
                energy.tolerance = numeric.value;
            }
        }

        // Check that at a value has been parsed.
        if (energy.value == null)
        {
            throw new EnergyParserException("Unable to parse query " + query);
        }

        setResult(energy);

        LOGGER.debug(energy);
    }
}
