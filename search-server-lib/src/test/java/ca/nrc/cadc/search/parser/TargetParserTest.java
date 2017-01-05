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


import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.parser.exception.TargetParserException;

import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


/**
 * @author jburke
 */
public class TargetParserTest extends AbstractUnitTest<TargetParser>
{
    private final Resolver mockResolver = createMock(Resolver.class);

    public TargetParserTest()
    {
    }

    @Test
    public void nullString() throws Exception
    {
        try
        {
            final TargetParser parser = new TargetParser(mockResolver);
            parser.parse(null, null);

            fail("Failed to throw TargetParserException parsing null string");
        }
        catch (TargetParserException e)
        {
            // Good!
        }
    }

    @Test
    public final void parseSexiCoordinates() throws Exception
    {
        final String[] targets =
                {
                        "12:34:56 56:43:21",   // colon
                        "12h34m56s 56d43m21s",  // lower
                        "12H34M56S 56D43M21S", // upper
                        "12h34M56s 56D43m21S", // mixed case
                        "12:34M56S 56:43m21S", // mixed everything
                        "12h34:56S 56d43:21S", // mixed everything
                        "12h34m56 56d43m21",   // mixed everything
                        "12 34 56 56 43 21"    // colon
                };

        final String[] noSecondTargets =
                {
                        "12:34 56:43",         // missing sec
                        "12h34 56d43"         // missing sec
                };

        for (final String t : targets)
        {
            reset(mockResolver);
            final TargetParser parser = new TargetParser(mockResolver);

            replay(mockResolver);

            final TargetData data = parser.parse(t, "SIMBAD");

            assertEquals("Wrong RA for " + t, 188.733d, data.getRA(), 0.01d);
            assertEquals("Wrong Dec for " + t, 56.722d, data.getDec(), 0.01d);

            verify(mockResolver);
        }

        for (final String t : noSecondTargets)
        {
            reset(mockResolver);
            final TargetParser parser = new TargetParser(mockResolver);

            replay(mockResolver);

            final TargetData data = parser.parse(t, "SIMBAD");

            assertEquals("Wrong RA for " + t, 188.5d, data.getRA(), 0.0d);
            assertEquals("Wrong Dec for " + t, 56.722d, data.getDec(), 0.01d);

            verify(mockResolver);
        }
    }


    @Test
    public final void parseCoordinatesAndRadius() throws Exception
    {
        final String[] targets =
                {
                        "123 45, 6",
                        "123,45,6",
                        "123 , 45 , 6"
                };

        final String[] arcSecondTargets =
                {
                        "123 , 45 , 6'"
                };

        for (final String t : targets)
        {
            reset(mockResolver);
            final TargetParser parser =
                    new TargetParser(mockResolver);

            replay(mockResolver);

            final TargetData data = parser.parse(t, "SIMBAD");

            assertEquals("Wrong RA for " + t, 123.0d, data.getRA(), 0.0d);
            assertEquals("Wrong Dec for " + t, 45.0, data.getDec(), 0.0d);
            assertEquals("Wrong Radius for " + t, 6.0d, data.getRadius(), 0.0d);

            verify(mockResolver);
        }

        for (final String t : arcSecondTargets)
        {
            reset(mockResolver);
            final TargetParser parser =
                    new TargetParser(mockResolver);

            replay(mockResolver);

            final TargetData data = parser.parse(t, "SIMBAD");

            assertEquals("Wrong RA for " + t, 123.0d, data.getRA(), 0.0d);
            assertEquals("Wrong Dec for " + t, 45.0, data.getDec(), 0.0d);
            assertEquals("Wrong Radius for " + t, 0.1d, data.getRadius(), 0.0d);

            verify(mockResolver);
        }

        // TEST 3

        reset(mockResolver);
        final String TARGET_1 = "123 , 45 , 6\"";
        final TargetParser parser =
                new TargetParser(mockResolver);

        replay(mockResolver);

        final TargetData data = parser.parse(TARGET_1, "SIMBAD");

        assertEquals("Wrong RA for " + TARGET_1, 123.0d, data.getRA(), 0.0d);
        assertEquals("Wrong Dec for " + TARGET_1, 45.0, data.getDec(), 0.0d);
        assertEquals("Wrong Radius for " + TARGET_1, 0.001666d,
                     data.getRadius(), 0.0001d);

        verify(mockResolver);

        // TEST 4
        reset(mockResolver);
        final String TARGET_2 = "12 34 56 56 43 21, 6''";
        final TargetParser parser2 =
                new TargetParser(mockResolver);

        replay(mockResolver);

        final TargetData data2 = parser2.parse(TARGET_2, "SIMBAD");

        assertEquals("Wrong RA for " + TARGET_2, 188.733d, data2.getRA(),
                     0.001d);
        assertEquals("Wrong Dec for " + TARGET_2, 56.7225d, data2.getDec(),
                     0.0001d);
        assertEquals("Wrong Radius for " + TARGET_2, 0.001666d,
                     data2.getRadius(), 0.0001d);

        verify(mockResolver);
    }

    @Test
    public final void parseCoordinatesRadiusAndCoordsys() throws Exception
    {
        final String[] targets =
                {
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

        double index = 0.0;
        for (final String t : targets)
        {
            final TargetParser parser =
                    new TargetParser(mockResolver);
            final TargetData data = parser.parse(t, "SIMBAD");
            final String equivalentTarget = targets[(int) index];

            assertEquals("Wrong Coordsys for " + t,
                         equivalentTarget.substring(
                                 equivalentTarget.lastIndexOf(" ")).trim(),
                         data.getCoordsys());

            index++;
        }
    }

    @Test
    public final void parseRadius() throws Exception
    {
        final String t = "12 34 56 56 43 21";
        final TargetParser parser = new TargetParser(mockResolver);
        final TargetData data = parser.parse(t, "SIMBAD");

        replay(mockResolver);

        assertEquals(188.7D, data.getRA(), 0.1D);
        assertEquals(56.7D, data.getDec(), 0.1D);
        assertEquals("Radius should be 0.0", 0.0, data.getRadius(), 0.0);

        verify(mockResolver);
    }

    @Test
    public final void isQueryInDegrees() throws Exception
    {
        final String[] shouldPass =
                {
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

        for (final String t : shouldPass)
        {
            reset(mockResolver);
            final TargetParser parser = new TargetParser(mockResolver);

            replay(mockResolver);
            assertTrue(t + " failed test", parser.isQueryInDegrees(t));

            verify(mockResolver);
        }

        final String[] shouldFail =
                {
                        "12 34 56 56 43 21",
                        "12 34 56 56 43 21 6",
                        "12 34 56 56 43 21, 6'' ICRS",
                        "12 34 56 56 43 21, 6'' J2000",
                        "12 34 56 56 43 21, 6'' FK4",
                        "12 34 56 56 43 21, 6 GAL"
                };

        for (final String t : shouldFail)
        {
            reset(mockResolver);
            final TargetParser parser = new TargetParser(mockResolver);

            replay(mockResolver);
            assertFalse(t + " failed test", parser.isQueryInDegrees(t));

            verify(mockResolver);
        }

    }

    @Test
    public final void parseNameRadius() throws Exception
    {
        final String target = "M101 0.5";
        final String resolver = "SIMBAD";

        reset(mockResolver);
        final TargetParser testSubject =
                new TargetParser(mockResolver);
        final TargetData resolverData = new TargetData("M101 0.5",
                                                       13.5D, null, // raRange
                                                       -13.5D, null, // decRange
                                                       0.5, // radius
                                                       "COORD",
                                                       "SIMBAD",
                                                       88,
                                                       "M101",
                                                       null,
                                                       null);

        expect(mockResolver.resolveTarget(target, resolver)).andReturn(
                resolverData).once();

        replay(mockResolver);

        final TargetData resultData = testSubject.parse(target, resolver);

        assertEquals("Wrong radius.", 0.5D, resultData.getRadius(), 0.0D);
        assertEquals("Wrong object name.", "M101", resultData.getObjectName());

        verify(mockResolver);
    }
}