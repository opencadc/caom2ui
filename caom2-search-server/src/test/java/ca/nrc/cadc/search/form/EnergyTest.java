/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.form;

import java.util.ArrayList;
import java.util.List;

import ca.nrc.cadc.caom2.IntervalSearch;

import org.apache.log4j.Level;

import org.apache.log4j.Logger;

import ca.nrc.cadc.util.Log4jInit;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author jburke
 */
public class EnergyTest {

    private static final String ENERGY_FIELD = "Plane.energy.bounds.samples";
    private static Logger log = Logger.getLogger(EnergyTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    public EnergyTest() {
    }

    /**
     * Test of isValid method, of class Energy.
     */
    @Test
    public void testIsValid() {
        log.debug("testIsValid()...");

        FormErrors formErrors = new FormErrors();
        String formValue;
        Energy energy;
        boolean valid;

        formValue = null;
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertNull(energy.getLowerNumber());
        assertNull(energy.getUpperNumber());
        assertNull(energy.getUnit());
        //assertNull(energy.getTolerance());
        assertNull(energy.getFormValueUnit());

        formValue = "";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertNull(energy.getLowerNumber());
        assertNull(energy.getUpperNumber());
        assertNull(energy.getUnit());
        //assertNull(energy.getTolerance());
        assertNull(energy.getFormValueUnit());

        formValue = "100";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(100.0, energy.getLowerNumber());
        assertEquals(100.0, energy.getUpperNumber());
        assertNotNull(energy.getUnit());
        //assertNull(energy.getTolerance());
        assertNotNull(energy.getFormValueUnit());
        assertEquals("m", energy.getFormValueUnit());

        formValue = "100mm";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(0.1, energy.getUpperNumber());
        assertNotNull(energy.getUnit());
        //assertNull(energy.getTolerance());
        assertEquals("mm", energy.getFormValueUnit());

        formValue = "100mm 2.0";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        // TODO - assertEquals(0.1001, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        // TODO        assertEquals(2.0, energy.getTolerance());
        assertEquals("mm", energy.getFormValueUnit());

        formValue = "100..200";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(100.0, energy.getLowerNumber());
        assertEquals(200.0, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        //assertNull(energy.getTolerance());
        assertNotNull(energy.getFormValueUnit());
        assertEquals("m", energy.getFormValueUnit());

        formValue = "100..200mm";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(0.2, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        //assertNull(energy.getTolerance());
        assertEquals("mm", energy.getFormValueUnit());

        formValue = "100mm..200m 2.0";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(200.0, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        // TODO - assertEquals(2.0, energy.getTolerance());
        assertNull(energy.getFormValueUnit());

        formValue = "100mm..200m";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(200.0, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        //assertNull(energy.getTolerance());

        formValue = "100..200mm 2.0";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(0.2, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        // TODO assertEquals(2.0, energy.getTolerance());

        formValue = "100mm..200m 2.0";
        energy = new Energy(formValue, ENERGY_FIELD);
        valid = energy.isValid(formErrors);
        log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
        assertTrue(valid);
        assertEquals(0.1, energy.getLowerNumber());
        assertEquals(200.0, energy.getUpperNumber());
        assertEquals("m", energy.getUnit());
        // TODO assertEquals(2.0, energy.getTolerance());

        log.info("testIsValid() passed.");
    }

    @Test
    public void testBuildSearches() {
        log.debug("testBuildSearches()...");

        String utype = ENERGY_FIELD;
        String formValue;
        Double expectedLower;
        Double expectedUpper;

        formValue = "100..200";
        expectedLower = 100.0;
        expectedUpper = 200.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "100";
        expectedLower = 100.0;
        expectedUpper = 100.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        formValue = "100";
        expectedLower = 100.0;
        testBuildSearch(utype, formValue, expectedLower, expectedLower);

        formValue = "100";
        expectedUpper = 100.0;
        testBuildSearch(utype, formValue, expectedUpper, expectedUpper);

        formValue = "100";
        testBuildSearch(utype, formValue, 100.0, 100.0);

        formValue = "200..100";
        expectedLower = 200.0;
        expectedUpper = 100.0;
        testBuildSearch(utype, formValue, expectedLower, expectedUpper);

        log.debug("testBuildSearches() passed.");
    }

    private void testBuildSearch(final String utype, final String formValue,
                                 final Double expectedLower, final Double expectedUpper) {
        List<FormError> errorList = new ArrayList<>();
        final Energy energy = new Energy(formValue, utype);

        if ((expectedLower != null) && (expectedUpper != null)
                && (expectedLower > expectedUpper)) {
            energy.setLowerNumber(expectedLower);
            energy.setUpperNumber(expectedUpper);
        } else {
            final boolean valid = energy.isValid(new FormErrors());

            log.debug("formValue[" + formValue + "] " + energy + " valid: " + valid);
            assertTrue("Validation failed.", valid);
        }

        IntervalSearch template = (IntervalSearch) energy
                .buildSearch(errorList);
        if ((expectedLower != null) && (expectedUpper != null)
                && (expectedLower > expectedUpper)) {
            assertNull("Expected template to be null.", template);
        } else {
            assertEquals("Expected lower value is " + expectedLower,
                         expectedLower, template.getLower());
            assertEquals("Expected upper value is " + expectedUpper,
                         expectedUpper, template.getUpper());
            assertEquals("Expected errorList to be empty.", 0, errorList
                    .size());
        }
    }

    @Test
    public void testUseMeter() {
        log.debug("testUseMeter()...");

        String utype = ENERGY_FIELD;
        assertTrue("utype=" + utype + " should use meter", Energy
                .useMeter(utype));

        utype = "Plane.energy.restwav";
        assertTrue("utype=" + utype + " should use meter", Energy
                .useMeter(utype));

        utype = "Char.SpectralAxis.Coverage.Bounds.Limits";
        assertTrue("utype=" + utype + " should use meter", Energy
                .useMeter(utype));

        // test a couple of other utypes that do not use meter
        utype = "Plane.energy.sampleSize";
        assertFalse("utype=" + utype + " should use meter", Energy
                .useMeter(utype));

        utype = "Plane.energy.dimension";
        assertFalse("utype=" + utype + " should use meter", Energy
                .useMeter(utype));

        log.debug("testUseMeter() passed.");
    }
}
