/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
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
 * 3/4/13 - 1:58 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.cutout.stc;

import ca.nrc.cadc.AbstractUnitTest;

import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.types.Circle;
import ca.nrc.cadc.caom2.types.Location;
import ca.nrc.cadc.caom2.types.Point;

import org.junit.Test;
import static org.junit.Assert.*;


public class STCCutoutImplTest extends AbstractUnitTest<STCCutoutImpl>
{
    protected static final double ONE_ARC_MIN_AS_DEG = 0.016666666666666666;

    @Test
    public void formatNullCutoutString() throws Exception
    {
        setTestSubject(new STCCutoutImpl(null, null));

        assertEquals("Should be empty string", "", getTestSubject().format());
    }

    @Test
    public void formatCircleCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(
                        new SpatialSearch("TEST circle",
                                          new Circle(new Point(0.4d, 0.5d),
                                                     0.5d)), null));

        assertEquals("Should be Circle ICRS 0.4 0.5 0.5",
                     "Circle ICRS 0.4 0.5 0.5",
                     getTestSubject().format());
    }

    @Test
    public void formatLocationCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(
                        new SpatialSearch("TEST location",
                                          new Location(new Point(0.4d, 0.5d))),
                        null));

        assertEquals("Should be Circle ICRS 0.4 0.5 "
                     + ONE_ARC_MIN_AS_DEG,
                     "Circle ICRS 0.4 0.5 " + ONE_ARC_MIN_AS_DEG,
                     getTestSubject().format());
    }

    @Test
    public void formatSpectralIntervalCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(null,
                                  new IntervalSearch("TEST Interval", 88.1d,
                                                     88.9d, "Hz")));

        assertEquals("Should be SpectralInterval 88.1 88.9 Hz",
                     "SpectralInterval 88.1 88.9 Hz",
                     getTestSubject().format());
    }

    @Test
    public void formatMixEmptySpatialCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(null,
                                  new IntervalSearch("TEST Interval", 88.1d,
                                                     88.9d, "m")));

        assertEquals("Should be SpectralInterval 88.1 88.9 m",
                     "SpectralInterval 88.1 88.9 m",
                     getTestSubject().format());
    }

    @Test
    public void formatMixEmptySpectralCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(
                        new SpatialSearch("TEST location",
                                          new Location(new Point(0.4d, 0.5d))),
                        null));

        assertEquals("Should be Circle ICRS 0.4 0.5 "
                     + ONE_ARC_MIN_AS_DEG,
                     "Circle ICRS 0.4 0.5 " + ONE_ARC_MIN_AS_DEG,
                     getTestSubject().format());
    }

    @Test
    public void formatMixCutoutString() throws Exception
    {
        setTestSubject(
                new STCCutoutImpl(
                        new SpatialSearch("TEST location",
                                          new Location(new Point(0.4d, 0.5d))),
                        new IntervalSearch("TEST Interval", 88.1d, 88.9d,
                                           "MHz")));

        assertEquals("Should be Circle ICRS 0.4 0.5 "
                     + ONE_ARC_MIN_AS_DEG
                     + " SpectralInterval 88.1 88.9 MHz",
                     "Circle ICRS 0.4 0.5 " + ONE_ARC_MIN_AS_DEG
                     + " SpectralInterval 88.1 88.9 MHz",
                     getTestSubject().format());
    }
}
