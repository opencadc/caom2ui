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
 * 8/31/11 - 1:12 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.form;

import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.TextSearch;
import org.apache.log4j.Level;
import ca.nrc.cadc.util.Log4jInit;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class TextTest {

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Test
    public void testConstruction() {
        // case 1: job has no parameters
        String uType = "Observation.project";
        Job job = new Job();

        Text text = new Text(job, uType);
        assertTrue("ignoreCase should be true", text.isIgnoreCase());
        text.setIgnoreCase(false);
        assertFalse("ignoreCase should be false", text.isIgnoreCase());
        assertFalse("Should not have data", text.hasData());

        // case 2: job has a parameter
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(uType, "value1"));
        job = new Job();
        job.setParameterList(parameterList);
        text = new Text(job, uType);
        assertTrue("Should have data", text.hasData());

        // case 3: job has parameter with incorrect utype
        parameterList = new ArrayList<>();
        parameterList.add(new Parameter(uType + "extra", "value1"));
        job = new Job();
        job.setParameterList(parameterList);
        text = new Text(job, uType);
        assertFalse("Should not have data", text.hasData());

        // case 4: has utype and formValue
        String formValue = "someFormValue";
        boolean ignoreCase = true;
        text = new Text(uType, formValue, ignoreCase);
        assertTrue("ignoreCase should be true", text.isIgnoreCase());
        assertTrue("Should have data", text.hasData());
    }

    @Test
    public void testIsValid() {
        FormErrors formErrors = new FormErrors();

        // case 1: incorrect utype
        String uType = "Observation.proj";
        String formValue = "someFormValue";
        boolean ignoreCase = true;
        Text text = new Text(uType, formValue, ignoreCase);
        assertFalse("isValid() should return false", text.isValid(formErrors));
        String error = formErrors.getFormError(uType + Text.NAME);
        assertTrue("Should be an invalid utype error", error.contains("Invalid utype"));

        // case 2: correct utype
        uType = "Observation.project";
        text = new Text(uType, formValue, ignoreCase);
        assertTrue("isValid() should return true", text.isValid(formErrors));
    }

    @Test
    public void testToString() {
        String formValue = "someFormValue";
        boolean ignoreCase = true;
        String uType = "Observation.project";
        Text text = new Text(uType, formValue, ignoreCase);
        String actualString = text.toString();
        String expectedString = "Text[" + uType + ", " + formValue + "]";
        assertEquals("toString() returns incorrect string", expectedString, actualString);
    }

    @Test
    public void testBuildSearch() {
        // Note: Although TextSearch() can throw an IllegalArgumentException, 
        //       the constructor used by Text.buildSearch() will not
        //       throw an IllegalArgumentException. Hence codes in catch block are not tested.

        List<FormError> errorList = new ArrayList<>();
        String formValue = "someFormValue";
        boolean ignoreCase = true;
        String uType = "Observation.project";
        Text text = new Text(uType, formValue, ignoreCase);
        SearchTemplate searchTemplate = text.buildSearch(errorList);
        assertNotNull("searchTemplate should not be null", searchTemplate);
        assertTrue("Should be an TextSearch instance", searchTemplate instanceof TextSearch);
    }
}
