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

import ca.nrc.cadc.caom2.NumericSearch;

import ca.nrc.cadc.util.StringUtil;

import org.apache.log4j.Level;


import ca.nrc.cadc.uws.Job;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import ca.nrc.cadc.util.Log4jInit;


public class NumberTest extends AbstractNumericFormConstraintTest<Number> {

    private static Logger log = Logger.getLogger(NumberTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Test
    public void isValid() {
        final Number number = new Number("BOGUS", "Plane.position.sampleSize");
        final Job mockJob = createMock(Job.class);
        reset(mockJob);

        setTestSubject(number);
        replay(mockJob);

        FormErrors formErrors1 = new FormErrors();
        final boolean v = getTestSubject().isValid(formErrors1);
        assertFalse("Should not be valid.", v);

        verify(mockJob);

        assertTrue(formErrors1.hasErrors());
    }

    @Test
    public void isValidSingleNumber() {
        setTestSubject(new Number("88.0", "Plane.time.exposure"));

        final boolean success1 = getTestSubject().isValid(new FormErrors());

        assertTrue("Should be valid.", success1);
        assertNotNull("Lower number should not be null.",
                      getTestSubject().getLowerNumber());
        assertNotNull("Upper number should not be null.",
                      getTestSubject().getUpperNumber());
        assertEquals("Form value should be 88.0.", "88.0",
                     getTestSubject().getFormValue());
        assertEquals("Lower number should be 88.0.", 88.0,
                     getTestSubject().getLowerNumber());
        assertEquals("Upper number should be 88.0.", 88.0,
                     getTestSubject().getUpperNumber());
    }

    @Test
    public void swapTrueValuesIfNecessary() {
        setTestSubject(new Number("UTYPE") {
            /**
             * Number is valid if the Number lower and upper values
             * have been successfully validated.
             *
             * @return boolean true if form values are valid, false otherwise.
             */
            @Override
            public boolean isValid(FormErrors formErrors) {
                setLowerNumber(2.3);
                setUpperNumber(4.9);

                return true;
            }
        });

        getTestSubject().isValid(null);
        getTestSubject().swapTrueValuesIfNecessary();

        assertEquals("Lower value should be 2.3", 2.3,
                     getTestSubject().getLowerNumber().doubleValue(), 0.0);
        assertEquals("Upper value should be 4.9", 4.9,
                     getTestSubject().getUpperNumber().doubleValue(), 0.0);


        // TEST 2

        setTestSubject(new Number("UTYPE") {
            /**
             * Number is valid if the Number lower and upper values
             * have been successfully validated.
             *
             * @return boolean true if form values are valid, false otherwise.
             */
            @Override
            public boolean isValid(final FormErrors formErrors) {
                setLowerNumber(0.258);
                setUpperNumber(0.005484);

                return true;
            }
        });

        getTestSubject().isValid(null);
        getTestSubject().swapTrueValuesIfNecessary();

        assertEquals("Lower value should be 0.005484", 0.005484,
                     getTestSubject().getLowerNumber().doubleValue(), 0.0);
        assertEquals("Upper value should be 0.258", 0.258,
                     getTestSubject().getUpperNumber().doubleValue(), 0.0);
    }

    @Test
    public void testEnergyRange() {
        setTestSubject(new Number("UTYPE") {
            /**
             * Number is valid if the Number lower and upper values
             * have been successfully validated.
             *
             * @return boolean true if form values are valid, false otherwise.
             */
            @Override
            public boolean isValid(FormErrors formErrors) {
                setLowerNumber(2.3);
                setUpperNumber(4.9);

                return true;
            }
        });

        getTestSubject().isValid(null);
        getTestSubject().swapTrueValuesIfNecessary();

        assertEquals("Lower value should be 2.3", 2.3,
                     getTestSubject().getLowerNumber().doubleValue(), 0.0);
        assertEquals("Upper value should be 4.9", 4.9,
                     getTestSubject().getUpperNumber().doubleValue(), 0.0);
    }

    /**
     * Test of isValid method, of class Number.
     */
    @Test
    public void testIsValidEnergy() {
        String utype = "Plane.energy.bounds.samples";
        String formValue;
        Double expectedLower;
        Double expectedUpper;
        String expectedUnit;

        testNumber(utype, null, null, null, null, null);

        formValue = "";
        testNumber(utype, formValue, null, null, null, null);

        formValue = "100";
        expectedLower = 99.5;
        expectedUpper = 100.5;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper,
                   expectedUnit, "m");

        formValue = "100mm";
        expectedLower = 0.0995;
        expectedUpper = 0.1005;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper,
                   expectedUnit, "mm");

        formValue = "100..200";
        expectedLower = 100.0;
        expectedUpper = 200.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "m");

        formValue = "100mm..200";
        expectedLower = 0.1;
        expectedUpper = 0.2;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "mm");

        formValue = "100 mm..200";
        expectedLower = 0.1;
        expectedUpper = 0.2;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "mm");

        formValue = "100..200mm";
        expectedLower = 0.1;
        expectedUpper = 0.2;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "mm");

        formValue = "100..200 mm";
        expectedLower = 0.1;
        expectedUpper = 0.2;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "mm");

        formValue = "100mm..200m";
        expectedLower = 0.1;
        expectedUpper = 200.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100 mm..200m";
        expectedLower = 0.1;
        expectedUpper = 200.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100mm..200 m";
        expectedLower = 0.1;
        expectedUpper = 200.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100 mm..200 m";
        expectedLower = 0.1;
        expectedUpper = 200.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "> 100";
        expectedLower = 100.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, null, expectedUnit, "m");

        formValue = "> 100mm";
        expectedLower = 0.1;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, null, expectedUnit, "mm");

        formValue = "<100";
        expectedUpper = 100.0;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, null, expectedUpper, expectedUnit, "m");

        formValue = "< 100 mm";
        expectedUpper = 0.1;
        expectedUnit = Energy.NORMALIZED_UNITS;
        testNumber(utype, formValue, null, expectedUpper, expectedUnit, "mm");

        log.info("testIsValidEnergy() passed.");
    }

    /**
     * Test of isValid method, of class Number.
     */
    @Test
    public void testIsValidTime() {
        log.debug("testIsValidTime()...");
        String utype = "Plane.time.exposure";
        String formValue;
        Double expectedLower;
        Double expectedUpper;
        String expectedUnit;

        testNumber(utype, null, null, null, null, null);

        formValue = "";
        testNumber(utype, formValue, null, null, null, null);

        formValue = "100";
        expectedUpper = 100.0;
        expectedLower = 100.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   Number.TIME_NORMALIZED_UNITS);

        formValue = "100s";
        expectedLower = 100.0;
        expectedUpper = 100.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   Number.TIME_NORMALIZED_UNITS);

        formValue = "100m";
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        expectedLower = 6000.0;
        expectedUpper = 6000.0;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "m");

        formValue = "100..200";
        expectedLower = 100.0;
        expectedUpper = 200.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   Number.TIME_NORMALIZED_UNITS);

        formValue = "100h..200";
        expectedLower = 360000.0;
        expectedUpper = 720000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "h");

        formValue = "100 h..200";
        expectedLower = 360000.0;
        expectedUpper = 720000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "h");

        formValue = "100..200m";
        expectedLower = 6000.0;
        expectedUpper = 12000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "m");

        formValue = "100..200 m";
        expectedLower = 6000.0;
        expectedUpper = 12000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   "m");

        formValue = "100s..200h";
        expectedLower = 100.0;
        expectedUpper = 720000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100 s..200h";
        expectedLower = 100.0;
        expectedUpper = 720000.0;
        expectedUnit = "s";
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100s..200 h";
        expectedLower = 100.0;
        expectedUpper = 720000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        formValue = "100 s..200 h";
        expectedLower = 100.0;
        expectedUpper = 720000.0;
        expectedUnit = Number.TIME_NORMALIZED_UNITS;
        testNumber(utype, formValue, expectedLower, expectedUpper, expectedUnit,
                   null);

        log.debug("testIsValidTime() passed.");
    }

    private void testNumber(final String utype, final String formValue,
                            final Double expectedLower,
                            final Double expectedUpper,
                            final String expectedUnit,
                            final String formValueUnit) {
        final Number number = new Number(formValue, utype);
        final boolean valid = number.isValid(new FormErrors());

        log.debug("formValue[" + formValue + "] " + number + " valid: "
                          + valid);

        assertTrue("Validation failed.", valid);
        assertEquals("Expected lower value is " + expectedLower,
                     expectedLower, number.getLowerNumber());
        assertEquals("Expected upper value is " + expectedUpper,
                     expectedUpper, number.getUpperNumber());
        assertEquals("Expected unit is " + expectedUnit,
                     expectedUnit, number.getUnit());

        if (formValueUnit == null) {
            assertFalse(String.format(
                    "FormValueUnit should be null or empty, but was '%s'",
                    number.getFormValueUnit()),
                        StringUtil.hasLength(number.getFormValueUnit()));
        } else {
            assertEquals(String.format("FormValueUnit should be %s, but was %s",
                                       formValueUnit,
                                       number.getFormValueUnit()),
                         formValueUnit, number.getFormValueUnit());
        }
    }

    @Test
    public void testBuildSearches() {
        log.debug("testBuildSearches()...");
        String utype = "Plane.time.exposure";
        String formValue;
        Double expectedLower;
        Double expectedUpper;

        formValue = "100 s..200 h";
        expectedLower = 100.0;
        expectedUpper = 720000.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "100 s";
        expectedLower = 100.0;
        expectedUpper = 100.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = ">= 100 s";
        expectedLower = 100.0;
        expectedUpper = null;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "<= 100 s";
        expectedLower = null;
        expectedUpper = 100.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "100 s";
        expectedLower = null;
        expectedUpper = null;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "";
        expectedLower = null;
        expectedUpper = null;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = null;
        expectedLower = null;
        expectedUpper = null;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        log.debug("testBuildSearches() passed.");

    }

    private void testBuildSearch(final String utype, final String formValue,
                                 final Double expectedLower, final Double expectedUpper) {
        final List<FormError> errorList = new ArrayList<>();
        final Number number = new Number(formValue, utype);
        final boolean valid = number.isValid(new FormErrors());

        log.debug("formValue[" + formValue + "] " + number + " valid: " + valid);
        assertTrue("Validation failed.", valid);

        number.setLowerNumber(expectedLower);
        number.setUpperNumber(expectedUpper);
        NumericSearch template = (NumericSearch) number.buildSearch(errorList);
        if (StringUtil.hasText(formValue)) {
            if ((expectedLower == null) && (expectedUpper == null)) {
                Double value = Double.parseDouble(number.getFormValue());
                assertEquals("Expected lower value is " + number.getFormValue(),
                             value, template.lower);
                assertEquals("Expected upper value is " + number.getFormValue(),
                             value, template.upper);
                assertEquals("Expected errorList to be empty.", 0, errorList.size());
            } else {
                assertEquals("Expected lower value is " + expectedLower,
                             expectedLower, template.lower);
                assertEquals("Expected upper value is " + expectedUpper,
                             expectedUpper, template.upper);
                assertEquals("Expected errorList to be empty.", 0, errorList.size());
            }
        } else {
            assertNull("Expected template to be null.", template);
        }
    }
}
