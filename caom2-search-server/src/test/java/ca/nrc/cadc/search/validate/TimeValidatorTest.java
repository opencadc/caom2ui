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
 *
 * @author jenkinsd
 * 11/29/11 - 2:46 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.validate;

import ca.nrc.cadc.AbstractUnitTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class TimeValidatorTest extends AbstractUnitTest<TimeValidator>
{
    @Test
    public void validate() throws Exception
    {
        final String utype = "Plane.time.bounds.samples";

        setTestSubject(new TimeValidator(utype, null, null));
        assertNull("Should be null.", getTestSubject().validate());

        setTestSubject(new TimeValidator(utype, null, null));
        assertNull("Should be null.", getTestSubject().validate());

        setTestSubject(new TimeValidator(utype, 88.9, "m"));
        assertEquals("Should be " + 88.9 * 60.0, 88.9 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 88.9, "am"));
        assertEquals("Should be " + 88.9 * 60.0, 88.9 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 88.9, "arcmin"));
        assertEquals("Should be " + 88.9 * 60.0, 88.9 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 88.9, "ArcMinute"));
        assertEquals("Should be " + 88.9 * 60.0, 88.9 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 88, "m"));
        assertEquals("Should be " + 88.0 * 60.0, 88.0 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 0.2, "h"));
        assertEquals("Should be " + 0.2 * 60.0 * 60, 0.2 * 60.0 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 0.2, "degrees"));
        assertEquals("Should be " + 0.2 * 60.0 * 60.0 * 24,
                     0.2 * 60.0 * 60.0 * 24, getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator("Plane.position.sampleSize", 0.2,
                                         "deg"));
        assertEquals("Should be " + 0.2 * 60.0 * 60.0, 0.2 * 60.0 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, .2, "hm"));
        assertEquals("Should be " + 0.2 * 60.0 * 60, 0.2 * 60.0 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 4.6, "mh"));
        assertEquals("Should be " + 4.6 * 60.0, 4.6 * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 9., "mh"));
        assertEquals("Should be " + 9. * 60.0, 9. * 60.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator(utype, 1.2, "y"));
        assertEquals("Should be " + 1.2 * 60.0 * 60.0 * 24.0 * 365.0,
                     1.2 * 60.0 * 60.0 * 24.0 * 365.0,
                     getTestSubject().validate(), 0.0);

        setTestSubject(new TimeValidator("Plane.time.bounds.exposure", 1000, "sec"));
        assertEquals("Should be " + 1000.0, 1000.0, getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.exposure", 1, "min"));
        assertEquals("Should be " + 1.0 * 60, 1.0 * 60, getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.exposure", 1, "hour"));
        assertEquals("Should be " + 1.0 * 60 * 60, 1.0 * 60 * 60, getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.width", 1000, "sec"));
        assertEquals("Should be " + 1000.0 / 60.0 / 60.0 / 24.0,
                     1000.0 / 60.0 / 60.0 / 24.0,
                     getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.width", 1, "min"));
        assertEquals("Should be " + 1.0 / 24 / 60, 1.0 / 24 / 60, 
                getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.width", 1, "hour"));
        assertEquals("Should be " + 1.0 / 24, 1.0 / 24, 
                getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.width", 1, "day"));
        assertEquals("Should be " + 1.0, 1.0, getTestSubject().validate(), 1e-15);

        setTestSubject(new TimeValidator("Plane.time.bounds.width", 1, "year"));
        assertEquals("Should be " + 1.0 * 365, 1.0 * 365, getTestSubject().validate(), 1e-15);
    }
}
