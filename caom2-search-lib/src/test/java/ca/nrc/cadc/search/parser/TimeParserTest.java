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

import ca.nrc.cadc.search.parser.exception.NumericParserException;
import org.apache.log4j.Level;
import ca.nrc.cadc.search.parser.exception.TimeParserException;
import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jburke
 */
public class TimeParserTest
{
    private static Logger log = Logger.getLogger(TimeParserTest.class);
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.search.parser", Level.INFO);
    }

    public static final String SPACE = " ";
    public static final Double VALUE = 1.0;
    public static final String UNIT = "s";

    public TimeParserTest() { }

    @Test
    public void testNullString() throws Exception
    {
        log.debug("nullString()...");
        try
        {
            new TimeParser(null);
            fail("Failed to throw TimeParserException parsing null string");
        }
        catch (NumericParserException e)
        {
            // Good.
        }

        log.info("nullString() passed.");
    }

    @Test
    public void testValue() throws Exception
    {
        log.debug("testValue()...");

        final String query = VALUE.toString();
        final TimeParser parser = new TimeParser(query);

        assertEquals(VALUE, parser.getValue());
        assertNull(parser.getUnit());
//        assertEquals(String.format("Should be empty, but is '%s'.",
//                                   parser.getUnit()), "", parser.getUnit());

        log.info("testValue() passed.");
    }

    @Test
    public void testValueUnit() throws Exception
    {
        log.debug("testValueUnit()...");

        final String query = VALUE + UNIT;
        final TimeParser testSubject = new TimeParser(query);

        assertEquals(VALUE, testSubject.getValue());
        assertEquals(UNIT, testSubject.getUnit());

        final String query2 = VALUE + SPACE + UNIT;
        final TimeParser testSubject2 = new TimeParser(query2);
        assertEquals(VALUE, testSubject2.getValue());
        assertEquals(UNIT, testSubject2.getUnit());

        log.info("testValueUnit() passed.");
    }
}