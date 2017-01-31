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
 * 10/31/13 - 1:37 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.parser;


import org.junit.Test;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.parser.exception.PositionParserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class PositionParserTest extends AbstractUnitTest<PositionParser>
{
    @Test
    public void sanitizeTarget() throws Exception
    {
        setTestSubject(new PositionParser());

        final String sanitizedTarget =
                getTestSubject().sanitizeTarget(">23 45 .. 90");

        assertEquals("Not sanitized properly.", ">23 45..90", sanitizedTarget);
    }

    @Test
    public void sanitizeTarget2() throws Exception
    {
        setTestSubject(new PositionParser());

        final String sanitizedTarget =
                getTestSubject().sanitizeTarget("-40:12:10 ..10:20:30 45... 90");

        assertEquals("Not sanitized properly.", "-40:12:10..10:20:30 45...90",
                     sanitizedTarget);
    }

    @Test
    public void parseWithSpacesInRange() throws Exception
    {
        setTestSubject(new PositionParser());

        final TargetData targetData = getTestSubject().parse(">23 45 .. 90");
        final Range<Double> raRange = targetData.getRaRange();
        final Range<Double> decRange = targetData.getDecRange();

        assertEquals("Bad RA range.", 23.0d, raRange.getLowerValue(), 0.0d);
        assertEquals("Bad Dec range.", 45.0d, decRange.getLowerValue(), 0.0d);
        assertEquals("Bad Dec range.", 90.0d, decRange.getUpperValue(), 0.0d);
    }

    @Test
    public void parseWithSpacesInRange2() throws Exception
    {
        setTestSubject(new PositionParser());

        final TargetData targetData =
                getTestSubject().parse("-40:12:10 ..10:20:30 45... 90");
        final Range<Double> raRange = targetData.getRaRange();
        final Range<Double> decRange = targetData.getDecRange();

        assertEquals("Bad RA range.", 123.04d, raRange.getLowerValue(), 0.002d);
        assertEquals("Bad RA range.", 155.125d, raRange.getUpperValue(), 0.0d);
        assertEquals("Bad Dec range.", 45.0d, decRange.getLowerValue(), 0.0d);
        assertEquals("Bad Dec range.", 90.0d, decRange.getUpperValue(), 0.0d);
    }

    @Test
    public void parse2() throws Exception
    {
        setTestSubject(new PositionParser());

        final TargetData targetData =
                getTestSubject().parse("-40:12:10..10:20:30 <-40.00");
        final Range<Double> raRange = targetData.getRaRange();
        final Range<Double> decRange = targetData.getDecRange();

        assertEquals("Bad RA range.", 123.04d, raRange.getLowerValue(), 0.002d);
        assertEquals("Bad RA range.", 155.125d, raRange.getUpperValue(), 0.0d);
        assertEquals("Bad Dec range.", -40.0d, decRange.getUpperValue(), 0.0d);
    }

    @Test
    public void parseFail() throws Exception
    {
        setTestSubject(new PositionParser());

        try
        {
            getTestSubject().parse("-40:12:10 ..10:20:30 45.... 90");
            fail("Should throw PositionParserException for too many periods in "
                 + "range.");
        }
        catch (PositionParserException e)
        {
            // Good!
        }
    }
}
