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

import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Level;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class EnumeratedTest
{
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Test
    public void testConstruction() throws Exception
    {
        // case 1: null job, null selectedValues, hidden
        String[] selectedValues = {"someValue"};

        Enumerated select = new Enumerated(null, Select.UTYPE, selectedValues,
                                           true);
        assertTrue("hidden should be true", select.hidden);
        assertTrue("isHidden() should return true", select.isHidden());
        assertNull("getSelected() should return null", select.getSelected());
        assertTrue("selectedValues should be non-empty",
                   select.getSelectedValues().length == 1);
        assertTrue("getSelectedValues() should return non-empty list",
                   select.getSelectedValues().length == 1);
        assertFalse("Should not have any data", select.hasData());
        assertTrue("Label should be an empty String",
                   select.getLabel().equals(""));
        List<String> selected = new ArrayList<>();
        selected.add("aValue");
        select.setSelected(selected);
        assertTrue("Should have some data", select.hasData());
        select.setSelectedValues(null);
        assertNull("selectedValues should be null", select.getSelectedValues());

        // case 2: null job, null selectedValues, not hidden 
        select = new Enumerated(null, Select.UTYPE, selectedValues, false);
        String expectedLabel = ObsModel.getUtypeLabel(Select.UTYPE);
        assertEquals("Label should be an empty String", expectedLabel, select
                .getLabel());
    }

    @Test
    public void testToString() throws Exception
    {
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(Enumerated.UTYPE + Select.NAME,
                                        "value1"));
        Job job = new Job();
        job.setParameterList(parameterList);

        // case 1: selectedValues != null and has text, 
        String[] selectedValues = {"someValue"};
        Enumerated enumerated = new Enumerated(job, Enumerated.UTYPE, selectedValues, false);
        String selectString = enumerated.toString();
        String expectedString = "Enumerated[ @Enumerated.utype, someValue  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);

        // case 2: selectedValues != null, but is empty
        selectedValues = new String[1];
        enumerated = new Enumerated(job, Enumerated.UTYPE, selectedValues, false);
        selectString = enumerated.toString();
        expectedString = "Enumerated[ @Enumerated.utype,  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);

        // case 3: selectedValues == null
        enumerated = new Enumerated(null, Enumerated.UTYPE, null, false);
        selectString = enumerated.toString();
        expectedString = "Enumerated[ @Enumerated.utype,  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);
    }

    @Test
    public void testGetErrorMessage() throws Exception
    {
        String[] selectedValues = {"someValue"};

        Enumerated enumerated = new Enumerated(null, Select.UTYPE,
                                               selectedValues, true);
        assertTrue("Error message should be an empty String",
                   enumerated.getErrorMessage().equals(""));
    }

    @Test
    public void testGetName() throws Exception
    {
        String[] selectedValues = {"someValue"};
        Enumerated enumerated = new Enumerated(null, Select.UTYPE,
                                               selectedValues, true);
        assertEquals("Error message should be an empty String",
                     Enumerated.NAME, enumerated.getName());
    }

    @Test
    public void testToJSONString() throws Exception
    {
        // Note: Was not able to generate a test case to result in a JSONException
        //       when Enumerated.toJSONString() is invoked.

        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(Enumerated.UTYPE + Select.NAME,
                                        "value1"));
        Job job = new Job();
        job.setParameterList(parameterList);

        // case 1: selectedValues != null and has text, 
        String[] selectedValues = {"someValue"};
        Enumerated enumerated = new Enumerated(job, Enumerated.UTYPE,
                                               selectedValues, false);
        String enumeratedString = enumerated.toJSONString();
        String[] kvPairs = enumeratedString.split(",");
        for (final String kvPair : kvPairs)
        {
            final String[] kv = kvPair.split(":");
            if (kv[0].contains("selected"))
            {
                assertTrue("selected should contain 'someValue'", kv[1]
                        .contains("someValue"));
            }
            else if (kv[0].contains("error"))
            {
                assertEquals("error value is empty", "\"\"", kv[1]);
            }
            else if (kv[0].contains("hidden"))
            {
                assertTrue("hidden value should be false", kv[1]
                        .contains("false"));
            }
            else if (kv[0].contains("utype"))
            {
                assertTrue("utype should be " + Enumerated.UTYPE, kv[1]
                        .contains(Enumerated.UTYPE));
            }
        }
    }

    // Note: For buildSearch() test, refer to to SelectTest.testBuildSearch().

}
