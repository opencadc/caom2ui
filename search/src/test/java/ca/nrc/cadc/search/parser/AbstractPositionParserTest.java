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

import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jburke
 */
public class AbstractPositionParserTest
{
    private static Logger log = Logger.getLogger(AbstractPositionParserTest.class);
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.search.parser", org.apache.log4j.Level.DEBUG);
    }

    public AbstractPositionParserTest() { }

    @Test
    public void testNullString()
    {
        log.debug("nullString()...");
        try
        {
            PositionParser parser = new PositionParser();
            parser.parse(null);
            fail("Failed to throw PositionParserException parsing null string");
        }
        catch (PositionParserException e) { }
        catch (Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("nullString() passed.");
    }

    @Test
    public final void testParseSexiCoordinates()
    {
        log.debug("testParseSexiCoordinates()...");
        try
        {
            String[] targets =
            {
                "12:34:56 56:43:21",   // colon
                "12h34m5s 56d43m21s",  // lower
                "12H34M56S 56D43M21S", // upper
                "12h34M56s 56D43m21S", // mixed case
                "12:34M56S 56:43m21S", // mixed everything
                "12h34:56S 56d43:21S", // mixed everything
                "12h34m56 56d43m21",   // mixed everything
                "12:34 56:43",         // missing sec
                "12h34 56d43",         // missing sec
                "12 34 56 56 43 21"    // colon
            };
            for (String t : targets)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
                assertNotNull(data.getRA());
                assertNotNull(data.getDec());
            }
        }
        catch(Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testParseSexiCoordinates() passed.");
    }

    @Test
    public final void testParseCoordinatesAndRadius()
    {
        log.debug("testParseCoordinatesAndRadius()...");
        try
        {
            String[] targets = {
                "123 45, 6",
                "123,45,6",
                "123 , 45 , 6",
                "123 , 45 , 6'",
                "123 , 45 , 6\"",
                "12 34 56 56 43 21, 6''"
            };
            for (String t : targets)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
                assertNotNull(data.getRA());
                assertNotNull(data.getDec());
                assertNotNull(data.getRadius());
            }
        }
        catch(Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testParseCoordinatesAndRadius() passed.");
    }

    @Test
    public final void testParseCoordinatesRadiusAndCoordsys()
    {
        log.debug("testParseCoordinatesAndRadius()...");
        try
        {
            String[] targets = {
                "123 45, 6 ICRS",
                "123 45, 6 GAL",
                "123 45, 6 FK4",
                "123 45, 6 FK5",
                "123 45, 6 B1950",
                "123 45, 6 B1950.0",
                "123 45, 6 J2000",
                "12 34 56 56 43 21, 6'' ICRS",
                "12 34 56 56 43 21, 6'' FK4",
                "12 34 56 56 43 21, 6 GAL"
            };
            for (String t : targets)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
                assertNotNull(data.getRA());
                assertNotNull(data.getDec());
                assertNotNull(data.getRadius());
            }
        }
        catch(Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testParseCoordinatesAndRadius() passed.");
    }

    @Test
    public final void parseRadius() throws Exception
    {
        final String t = "12 34 56 56 43 21";
        PositionParser parser = new PositionParser();
        TargetData data = parser.parse(t);
        log.debug(t + " -> " + data);
        assertNotNull(data.getRA());
        assertNotNull(data.getDec());
        assertNotNull(data.getRadius());
        assertEquals(188.7D, data.getRA(), 0.1D);
        assertEquals(56.7D, data.getDec(), 0.1D);
        assertEquals("Radius should be 0.0", 0.0, data.getRadius(), 0.0);

        log.info("parseRadius() passed.");
    }

//    @Test
    public final void testIsQueryInDegrees()
    {
        log.debug("testIsQueryInDegrees()...");
        try
        {
            String[] shouldPass = {
                "20 20",
                "20 20 1",
                "20 20 1 ICRS",
                "123 45, 6 ICRS",
                "123 45, 6 GAL",
                "123 45, 6 FK4",
                "123 45, 6 FK5",
                "123 45, 6 B1950.0",
                "123 45, 6 J2000.0",
                "123 45, 6 B1950"
            };
            for (String t : shouldPass)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
            }

            String[] shouldFail = {
                "12 34 56 56 43 21",
                "12 34 56 56 43 21 6",
                "12 34 56 56 43 21, 6'' ICRS",
                "12 34 56 56 43 21, 6'' J2000",
                "12 34 56 56 43 21, 6'' FK4",
                "12 34 56 56 43 21, 6 GAL"
            };
            for (String t : shouldFail)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
            }
        }
        catch(Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testIsQueryInDegrees() passed.");
    }
    
    /**
     * 28.123 -20.123
     * +45:12:10 20:40:12
     * +45:12:10 -20.123
     * >23 45..90
     * -40:12:10..10:20:30 <-40.00
     * >23 45...90 (where we arbitrarily take the first . to be the decimal dot)
     * >23 45 .. 90   
     * 
     * 210.05 54.3
     * 210.05 54.3 0.5
     * 210.05 54.3 0.5 FK4
     * 210.05..210.15 54.2..54.4
     * 08:45:07.5 +54:18:00
     * 08:45:07.5 +54:18:00 0.5
     * 08 45 07.5 +54 18 00 0.5
     * 08h45m07.5s +54d18m00s 0.5
     * 0.0 0.0 GAL
     * 0.0..5.0 -5.0..0.0 GAL
     */
    @Test
    public final void testParseHelpQueries()
    {
        log.debug("testParseHelpQueries()...");
        try
        {
            String[] targets =
            {
                "28.123 -20.123",
                "+45:12:10 20:40:12",
                "+45:12:10 -20.123",
                ">23 45..90",
                "-40:12:10..10:20:30 <-40.00",
                ">23 45...90",
                ">23 45 .. 90",
                "210.05 54.3",
                "210.05 54.3 0.5",
                "210.05 54.3 0.5 FK4",
                "210.05..210.15 54.2..54.4",
                "08:45:07.5 +54:18:00",
                "08:45:07.5 +54:18:00 0.5",
                "08 45 07.5 +54 18 00 0.5",
                "08h45m07.5s +54d18m00s 0.5",
                "0.0 0.0 GAL",
                "0.0..5.0 -5.0..0.0 GAL"
            };
            for (String t : targets)
            {
                PositionParser parser = new PositionParser();
                TargetData data = parser.parse(t);
                log.debug(t + " -> " + data);
                assertNotNull(data.getRA() == null ? data.getRaRange() : data.getRA());
                assertNotNull(data.getDec() == null ? data.getDecRange() : data.getDec());
            }
        }
        catch(Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testParseHelpQueries() passed.");
    }
    
}