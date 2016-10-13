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
 * 9/24/13 - 3:48 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;


import ca.nrc.cadc.AbstractUnitTest;

import org.junit.Test;

import static org.junit.Assert.*;


public class AbstractPersistenceServiceTest
        extends AbstractUnitTest<AbstractPersistenceService>
{
    @Test
    public void literal() throws Exception
    {
        setTestSubject(new AbstractPersistenceService(null, "caom2", null)
        {

        });

        assertEquals("Should be NULL", "NULL",
                     getTestSubject().literal((Object) null));
        assertEquals("Should be NULL", "NULL",
                     getTestSubject().literal("null"));
        assertEquals("Should be 'STUFF'", "'STUFF'",
                     getTestSubject().literal("STUFF"));
        assertEquals("Should be 88", "88", getTestSubject().literal(88));
        assertEquals("Should be 88.0", "88.0", getTestSubject().literal(88.0));
        assertEquals("Should be '88'", "'88'", getTestSubject().literal("88"));
    }
}
