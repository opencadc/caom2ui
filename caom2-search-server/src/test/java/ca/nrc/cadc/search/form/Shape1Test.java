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

import ca.nrc.cadc.caom2.RangeSearch;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.TextSearch;
import ca.nrc.cadc.caom2.types.Circle;
import org.apache.log4j.Level;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import org.junit.Test;

import ca.nrc.cadc.util.Log4jInit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class Shape1Test {

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Test
    public void testBuildSearch() {
        // Note: Construction of Circle or Location instance do not throw 
        //       IllegalArgumentException. Although TextSearch() can throw 
        //       an IllegalArgumentException, the constructor used by Text.buildSearch() 
        //       will not throw an IllegalArgumentException. Hence codes in 
        //       catch block are not tested.

        FormErrors formErrors = new FormErrors();
        String uType = "Plane.position.bounds";
        List<FormError> errorList = new ArrayList<>();

        // case 1: formValue is not empty, resolverName != null, 
        //         parser.isQueryInDegrees(getFormValue()) returns true
        final List<Parameter> parameterList = new ArrayList<>();
        String name1 = uType + Shape1.VALUE;
        parameterList.add(new Parameter(name1, "m101"));
        String name2 = uType + Shape1.RESOLVER_VALUE;
        parameterList.add(new Parameter(name2, "value2"));
        Job job = new Job();
        job.setParameterList(parameterList);
        Shape1 shape = new Shape1(job, uType);
        assertNotNull("resolverName should not be null", shape.getResolverName());
        assertEquals("Incorrect resolverName", "value2", shape.getResolverName());
        assertEquals("Incorrect formName", "m101", shape.getFormValue());
        assertTrue("Should be valid", shape.isValid(formErrors));
        assertFalse("Should not have shape data", shape.hasShapeData());
        SearchTemplate searchTemplate = shape.buildSearch(errorList);
        assertNotNull("searchTemplate should not be null", searchTemplate);
        assertTrue("Should be a TextSearch instance", searchTemplate instanceof TextSearch);

        // case 2: formValue is not empty, resolverName != null, radius == 0.0
        //         parser.isQueryInDegrees(getFormValue()) returns false
        parameterList.clear();
        name1 = uType + Shape1.VALUE;
        parameterList.add(new Parameter(name1, "1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0"));
        name2 = uType + Shape1.RESOLVER_VALUE;
        parameterList.add(new Parameter(name2, "ALL"));
        job = new Job();
        job.setParameterList(parameterList);
        shape = new Shape1(job, uType);
        assertNotNull("resolverName should not be null", shape.getResolverName());
        assertEquals("Incorrect resolverName", "ALL", shape.getResolverName());
        assertEquals("Incorrect formName", "1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0", shape.getFormValue());
        assertTrue("Should be valid", shape.isValid(formErrors));
        assertTrue("Should not have shape data", shape.hasShapeData());
        assertEquals("Unit should be SEXIGESIMAL", "SEXIGESIMAL", shape.getFormValueUnit());
        searchTemplate = shape.buildSearch(errorList);
        assertNotNull("searchTemplate should not be null", searchTemplate);
        assertTrue("Should be a SpatialSearch instance", searchTemplate instanceof SpatialSearch);
        SpatialSearch spatialSearch = (SpatialSearch) searchTemplate;
        assertTrue("Should be a Circle instance", spatialSearch.getPosition() instanceof Circle);

        // case 3: formValue is not empty, resolverName != null, radius > 0
        //         parser.isQueryInDegrees(getFormValue()) returns false
        parameterList.clear();
        name1 = uType + Shape1.VALUE;
        parameterList.add(new Parameter(name1, "1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0, 7.0"));
        name2 = uType + Shape1.RESOLVER_VALUE;
        parameterList.add(new Parameter(name2, "ALL"));
        job = new Job();
        job.setParameterList(parameterList);
        shape = new Shape1(job, uType);
        assertNotNull("resolverName should not be null", shape.getResolverName());
        assertEquals("Incorrect resolverName", "ALL", shape.getResolverName());
        assertEquals("Incorrect formName", "1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0, 7.0", shape.getFormValue());
        assertTrue("Should be valid", shape.isValid(formErrors));
        assertTrue("Should not have shape data", shape.hasShapeData());
        assertEquals("Unit should be SEXIGESIMAL", "SEXIGESIMAL", shape.getFormValueUnit());
        searchTemplate = shape.buildSearch(errorList);
        assertNotNull("searchTemplate should not be null", searchTemplate);
        assertTrue("Should be an SpatialSearch instance", searchTemplate instanceof SpatialSearch);
        spatialSearch = (SpatialSearch) searchTemplate;
        assertTrue("Should be a Circle instance", spatialSearch.getPosition() instanceof Circle);

        parameterList.clear();
        name1 = uType + Shape1.VALUE;
        parameterList.add(new Parameter(name1, "<45 <=-15 J2000"));
        name2 = uType + Shape1.RESOLVER_VALUE;
        parameterList.add(new Parameter(name2, "ALL"));
        job = new Job();
        job.setParameterList(parameterList);
        shape = new Shape1(job, uType);
        assertNotNull("resolverName should not be null", shape.getResolverName());
        assertEquals("Incorrect resolverName", "ALL", shape.getResolverName());
        assertEquals("Incorrect formValue", "<45 <=-15 J2000", shape.getFormValue());
        assertTrue("Should be valid", shape.isValid(formErrors));
        assertFalse("Should not have shape data", shape.hasShapeData());
        assertTrue("Should have range data", shape.hasRangeData());
        searchTemplate = shape.buildSearch(errorList);
        assertNotNull("searchTemplate should not be null", searchTemplate);
        assertTrue("Should be an RangeSearch instance", searchTemplate instanceof RangeSearch);
        RangeSearch rangeSearch = (RangeSearch) searchTemplate;
        assertNotNull("Should be a Range instance", rangeSearch.getLowerRange());
        assertNotNull("Should be a Range instance", rangeSearch.getUpperRange());
    }

    @Test
    public void testToString() {
        String uType = "Plane.position.bounds";
        String formValue = "1.0 , 2.0 , 3.0 , 4.0 , 5.0 , 6.0, 7.0";
        final List<Parameter> parameterList = new ArrayList<>();
        String name1 = uType + Shape1.VALUE;
        parameterList.add(new Parameter(name1, formValue));
        String name2 = uType + Shape1.RESOLVER_VALUE;
        parameterList.add(new Parameter(name2, "ALL"));
        Job job = new Job();
        job.setParameterList(parameterList);
        Shape1 shape = new Shape1(job, uType);
        String actualString = shape.toString();
        String expectedString = "Shape1[" + formValue + "]";
        assertEquals("toString() returns incorrect string", expectedString, actualString);
    }
}
