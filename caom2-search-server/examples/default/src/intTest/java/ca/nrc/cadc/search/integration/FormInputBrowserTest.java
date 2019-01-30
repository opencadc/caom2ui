/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2014.                         (c) 2014.
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
 * 15/05/14 - 1:05 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.integration;

import ca.nrc.cadc.util.StringUtil;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Verify form inputs
 */
public class FormInputBrowserTest extends AbstractAdvancedSearchIntegrationTest {
    public FormInputBrowserTest() {
        super();
    }

    /**
     * Ensure the form inputs put error messages into the tooltips.
     *
     * @throws Exception Any errors.
     */
    @Test
    public void verifyFormInputs() throws Exception {
        final CAOMSearchFormPage caomSearchFormPage = goToMain(CAOMSearchFormPage.class);

        caomSearchFormPage.reset();

//      Observation Date.
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "BOG", true,
                        "Invalid: BOG");
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "", false,
                        "");

        // Spectral coverage.
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "BOGUS", true, null);
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "", false, "");

        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.PIXEL_SCALE_INPUT_ID, "aaa", true, null);
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.PIXEL_SCALE_INPUT_ID, "", false, "");

        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "aaa", true, null);
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "", false, "");

        caomSearchFormPage.showInputField(CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID);
        caomSearchFormPage.select(By.id(CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID + "_PRESET"), "PAST_WEEK");
        caomSearchFormPage.verifyFormInputMessageMatches(CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, false, "(.*)" +
            "\\d\\.\\.\\d(.*)");

        // Close it again.
        caomSearchFormPage.hideInputField(CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID);

        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "aaa", true, null);
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "", false, "");

        // Quantify the unit conversion values.
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.PIXEL_SCALE_INPUT_ID, "0.02..0.05arcmin", false,
                        "(1.2..3.0 arcseconds)");
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.PIXEL_SCALE_INPUT_ID, "", false, "");

        resetForm(CAOMSearchFormPage.RESET_BUTTON_SELECTOR);
        waitFor(2);

        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "> 2010-09-22", false,
                        "(>= 55461.0 MJD)");
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.OBSERVATION_DATE_INPUT_ID, "", false, "");

        verifyFormInput(caomSearchFormPage, "Plane.time.exposure", "2m..1h", false,
                        "(120.0..3600.0 seconds)");
        verifyFormInput(caomSearchFormPage, "Plane.time.exposure", "", false, "");

        verifyFormInput(caomSearchFormPage, "Plane.time.bounds.width", "1y..2y", false,
                        "(365.0..730.0 days)");
        verifyFormInput(caomSearchFormPage, "Plane.time.bounds.width", "", false, "");

        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "300..400GHz", false,
                        "(7.495E-4..9.993E-4 metres)");
        verifyFormInput(caomSearchFormPage, CAOMSearchFormPage.SPECTRAL_COVERAGE_INPUT_ID, "", false, "");

        verifyFormInput(caomSearchFormPage, "Plane.energy.sampleSize", "2..3GHz", false,
                        "(2.0E9..3.0E9 Hz)");
        verifyFormInput(caomSearchFormPage, "Plane.energy.sampleSize", "", false, "");

        verifyFormInput(caomSearchFormPage, "Plane.energy.bounds.width", "< 1000A", false,
                        "(< 1.000E-7 metres)");
        verifyFormInput(caomSearchFormPage, "Plane.energy.bounds.width", "", false, "");
        //*/

        verifyFormInput(caomSearchFormPage, "Plane.position.sampleSize", "10..20", false,
                        "(10.0..20.0 arcseconds)");
        verifyFormInput(caomSearchFormPage, "Plane.position.sampleSize", "", false, "");
    }

    private void verifyFormInput(final CAOMSearchFormPage caomSearchFormPage, final String inputID, final String entry,
                                 final boolean expectError, final String expectedLabelMessage)
        throws Exception {
        if (StringUtil.hasText(entry)) {
            caomSearchFormPage.waitForElementPresent(By.id(inputID));
            caomSearchFormPage.enterInputValue(caomSearchFormPage.find(By.id(inputID)), entry);
        } else {
            caomSearchFormPage.clearInputValue(inputID);
        }

        if (expectError) {
            // Empty strings are valid as they represent cleared errors.
            if (expectedLabelMessage != null) {
                caomSearchFormPage.verifyFormInputMessage(inputID, true, expectedLabelMessage);
            } else {
                caomSearchFormPage.verifyFormInputError(inputID);
            }
        } else {
            caomSearchFormPage.verifyFormInputMessage(inputID, false, expectedLabelMessage);
        }
    }

}
