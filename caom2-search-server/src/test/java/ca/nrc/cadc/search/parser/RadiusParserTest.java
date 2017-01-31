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
 * 5/24/12 - 3:19 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.parser;

import ca.nrc.cadc.search.parser.exception.NumericParserException;
import org.junit.Test;
import static org.junit.Assert.*;


public class RadiusParserTest
{
    private RadiusParser testSubject;


    @Test
    public void parse() throws Exception
    {
        setTestSubject(new RadiusParser("30arcmin"));

        assertEquals("Radius should be 30.0 / 60.0", 30.0 / 60.0,
                     getTestSubject().getValue());

        // TEST 2
        setTestSubject(new RadiusParser("40'"));

        assertEquals("Radius should be 40.0 / 60.0", 40.0 / 60.0,
                     getTestSubject().getValue());

        // TEST 3
        setTestSubject(new RadiusParser("10''"));

        assertEquals("Radius should be 10.0 / 3600.0", 10.0 / 3600.0,
                     getTestSubject().getValue());

        // TEST 4
        setTestSubject(new RadiusParser("15arcS"));

        assertEquals("Radius should be 15.0 / 3600.0", 15.0 / 3600.0,
                     getTestSubject().getValue());

        // TEST 5
        try
        {
            setTestSubject(new RadiusParser("210"));
            fail("Should throw NumericParserException.");
        }
        catch (NumericParserException e)
        {
            // Good.
        }

        // TEST 6
        setTestSubject(new RadiusParser("65"));

        assertEquals("Radius should be 65", 65.0, getTestSubject().getValue());

        // TEST 7
        setTestSubject(new RadiusParser("88deg"));

        assertEquals("Radius should be 88.0", 88.0,
                     getTestSubject().getValue());
    }


    public RadiusParser getTestSubject()
    {
        return testSubject;
    }

    public void setTestSubject(final RadiusParser testSubject)
    {
        this.testSubject = testSubject;
    }
}
