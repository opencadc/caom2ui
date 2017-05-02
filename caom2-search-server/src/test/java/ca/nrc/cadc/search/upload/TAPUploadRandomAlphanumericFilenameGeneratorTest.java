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
 * 11/14/13 - 1:49 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.upload;

import org.junit.Test;
import static org.junit.Assert.*;

import ca.nrc.cadc.AbstractUnitTest;


public class TAPUploadRandomAlphanumericFilenameGeneratorTest
        extends AbstractUnitTest<TAPUploadRandomAlphanumericFilenameGenerator>
{
    @Test
    public void generate()
    {
        setTestSubject(
                new TAPUploadRandomAlphanumericFilenameGenerator(null,
                                                                 null));

        final String generated1 = getTestSubject().generate();
        assertEquals("Should only be 16 characters long.", 16,
                     generated1.length());

        // TEST 2
        //

        setTestSubject(
                new TAPUploadRandomAlphanumericFilenameGenerator("PREF--",
                                                                 null));

        final String generated2 = getTestSubject().generate();
        assertEquals("Should only be 22 characters long.", 22,
                     generated2.length());
        assertTrue("Should start with PREF--.",
                   generated2.startsWith("PREF--"));

        // TEST 3
        //

        setTestSubject(
                new TAPUploadRandomAlphanumericFilenameGenerator("",
                                                                 ".SUF"));

        final String generated3 = getTestSubject().generate();
        assertEquals("Should only be 20 characters long.", 20,
                     generated3.length());
        assertTrue("Should end with .SUF.",
                   generated3.endsWith(".SUF"));

        // TEST 4
        //

        setTestSubject(
                new TAPUploadRandomAlphanumericFilenameGenerator("MYPREF",
                                                                 ".file"));

        final String generated4 = getTestSubject().generate();
        assertEquals("Should only be 27 characters long.", 27,
                     generated4.length());
        assertTrue("Should start with MYPREF.",
                   generated4.startsWith("MYPREF"));
        assertTrue("Should end with .file.", generated4.endsWith(".file"));
    }
}
