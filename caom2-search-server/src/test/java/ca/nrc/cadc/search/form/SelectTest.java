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

import ca.nrc.cadc.caom2.IsNull;
import ca.nrc.cadc.caom2.Or;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.TextSearch;
import org.apache.log4j.Level;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import org.junit.Test;
import ca.nrc.cadc.util.Log4jInit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class SelectTest {

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Test
    public void testConstruction() {
        // case1: null job, null selectedValues, hidden
        String[] selectedValues = {"someValue"};

        Select select = new Select(null, Select.UTYPE, selectedValues, true);
        assertTrue("hidden should be true", select.hidden);
        assertTrue("isHidden() should return true", select.isHidden());
        assertNull("getSelected() should return null", select.getSelected());
        assertEquals("selectedValues should be non-empty", 1, select.getSelectedValues().length);
        assertEquals("getSelectedValues() should return non-empty list", 1, select.getSelectedValues().length);
        assertFalse("Should not have any data", select.hasData());
        List<String> selected = new ArrayList<>();
        selected.add("aValue");
        select.setSelected(selected);
        assertTrue("Should have some data", select.hasData());
        select.resetSelectedValues();
        assertEquals("selectedValues should be empty", select.getSelectedValues().length, 0);

        // case 2: null job, null selectedValues, hidden
        select = new Select(null, Select.UTYPE, null, true);
        assertTrue("hidden should be true", select.hidden);
        assertTrue("isHidden() should return true", select.isHidden());
        assertNull("getSelected() should return null", select.getSelected());
        assertNull("selectedValues should be null", select.getSelectedValues());
        assertNull("getSelectedValues() should return non-empty list", select.getSelectedValues());
        assertFalse("Should not have any data", select.hasData());

        // case 3: empty job parameter, null selectedValues, not hidden
        Job job = new Job();
        select = new Select(job, Select.UTYPE, null, false);
        assertFalse("hidden should be false", select.hidden);
        assertFalse("isHidden() should return false", select.isHidden());
        assertNull("getSelected() should return null", select.getSelected());
        assertEquals("selectedValues should be empty", 0, select.getSelectedValues().length);
        assertEquals("getSelectedValues() should return null", 0, select.getSelectedValues().length);
        assertFalse("Should not have any data", select.hasData());

        // case 4: non-empty job parameter, null selected values
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(Select.UTYPE + Select.NAME, "value1"));
        job = new Job();
        job.setParameterList(parameterList);
        select = new Select(job, Select.UTYPE, selectedValues, false);
        assertNull("getSelected() should return null", select.getSelected());
        assertEquals("selectedValues should be non-empty", 1, select.getSelectedValues().length);
        assertEquals("getSelectedValues() should return non-empty", 1, select.getSelectedValues().length);
        assertFalse("Should not have any data", select.hasData());
    }

    @Test
    public void testToString() {
        List<Parameter> parameterList = new ArrayList<>();
        parameterList.add(new Parameter(Select.UTYPE + Select.NAME, "value1"));
        Job job = new Job();
        job.setParameterList(parameterList);

        // case 1: selectedValues != null and has text, 
        String[] selectedValues = {"someValue"};
        Select select = new Select(job, Select.UTYPE, selectedValues, false);
        String selectString = select.toString();
        String expectedString = "Selected[ @Select.utype, someValue  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);

        // case 2: selectedValues != null, but is empty
        selectedValues = new String[1];
        select = new Select(job, Select.UTYPE, selectedValues, false);
        selectString = select.toString();
        expectedString = "Selected[ @Select.utype,  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);

        // case 3: selectedValues == null
        select = new Select(null, Select.UTYPE, null, false);
        selectString = select.toString();
        expectedString = "Selected[ @Select.utype,  ]";
        assertEquals("toString() returns incorrect value", expectedString, selectString);
    }

    @Test
    public void isValid() {
        FormErrors formErrors = new FormErrors();

        // case 1: empty selectedValues
        String[] selectedValues = {};
        Select select = new Select(null, Select.UTYPE, selectedValues, false);
        assertFalse("isValid() should return false", select.isValid(formErrors));

        // case 2: non-empty selectedValues, but no element is the array
        selectedValues = new String[1];
        select = new Select(null, Select.UTYPE, selectedValues, false);
        assertTrue("isValid() should return true", select.isValid(formErrors));

        // case 3: null selectedValues
        select = new Select(null, Select.UTYPE, null, false);
        assertFalse("isValid() should return false", select.isValid(formErrors));

        // case 4: selectedValues is not empty
        selectedValues = new String[1];
        selectedValues[0] = "value1";
        select = new Select(null, Select.UTYPE, selectedValues, false);
        assertTrue("isValid() should return true", select.isValid(formErrors));
        String actualValue = select.getSelected().toArray(new String[0])[0];
        assertEquals("getSelected() should contain the value1", actualValue,
                     selectedValues[0]);

        // case 5: selectedValues is not empty, selected is not empty
        List<String> selected = new ArrayList<>();
        selected.add("value0");
        select.setSelected(selected);
        assertTrue("isValid() should return true", select.isValid(formErrors));
        String[] actualValues = select.getSelected().toArray(new String[0]);
        actualValue = actualValues[0];
        assertEquals("getSelected() should contain the value0", "value0", actualValue);
        actualValue = actualValues[1];
        assertEquals("getSelected() should contain the value1", "value1", actualValue);
    }

    @Test
    public void testBuildSearch() {
        // Note: In AbstractScalarFormConstraint.buildScalarSearch(), 
        //       when list.size() == 1 and value not equal to "null"
        //       TextSearch() is instantiated. Although TextSearch() can throw an
        //       IllegalArgumentException, the constructor being used will not
        //       throw an IllegalArgumentException. Hence codes in catch block are not tested.

        List<FormError> errorList = new ArrayList<>();
        String[] selectedValues = {};

        // case 1: selected == null
        Select select = new Select(null, Select.UTYPE, selectedValues, false);
        assertNull("SearchTemplate should be null", select.buildSearch(errorList));

        // case 2: selected is empty
        List<String> selected = new ArrayList<>();
        select.setSelected(selected);
        assertNull("SearchTemplate should be null", select.buildSearch(errorList));

        // case 3: selected has one null element
        selected.add("null");
        select.setSelected(selected);
        SearchTemplate searchTemplate = select.buildSearch(errorList);
        assertTrue("Should be an IsNull instance", searchTemplate instanceof IsNull);

        // case 4: selected has one non-null element
        selected = new ArrayList<>();
        selected.add("value1");
        select.setSelected(selected);
        searchTemplate = select.buildSearch(errorList);
        assertTrue("Should be an TextSearch instance", searchTemplate instanceof TextSearch);

        // case 5: selected has more than one element
        selected = new ArrayList<>();
        selected.add("value1");
        selected.add("value2");
        select.setSelected(selected);
        searchTemplate = select.buildSearch(errorList);
        assertTrue("Should be an Or instance", searchTemplate instanceof Or);
        Or orClause = (Or) searchTemplate;
        final SearchTemplate[] orTemplates = orClause.getTemplates().toArray(new SearchTemplate[]{});
        assertEquals("Should have two elements", 2, orTemplates.length);
        assertEquals("Should be TextSearch", "TextSearch[@Select.utype,value1,value1,false,false]",
                     orTemplates[0].toString());
        assertEquals("Should be TextSearch", "TextSearch[@Select.utype,value2,value2,false,false]",
                     orTemplates[1].toString());
    }
}
