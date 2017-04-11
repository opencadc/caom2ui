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

import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Verify form inputs
 */
public class FormInputBrowserTest extends AbstractAdvancedSearchIntegrationTest
{
    static final String OBSERVATION_DATE_U_TYPE = "Plane.time.bounds";
    static final String OBSERVATION_DATE_U_TYPE_DETAILS =
            "Plane.time.bounds_details";
    static final String SPECTRAL_COVERAGE_U_TYPE = "Plane.energy.bounds";
    static final String PIXEL_SCALE_U_TYPE = "Plane.position.sampleSize";
    

    /**
     * Ensure the form inputs put error messages into the tooltips.
     *
     * @throws Exception
     */
    @Test
    public void verifyFormInputs() throws Exception
    {
        goToHomePage();
        queryTab();

//        // Observation Date.
        verifyFormInput(OBSERVATION_DATE_U_TYPE, "BOG", true, "Invalid: BOG");
        verifyFormInput(OBSERVATION_DATE_U_TYPE, "", false, "");

        // Spectral coverage.
        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "BOGUS", true, null);
        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "", false, "");

        verifyFormInput(PIXEL_SCALE_U_TYPE, "aaa", true, null);
        verifyFormInput(PIXEL_SCALE_U_TYPE, "", false, "");

        verifyFormInput(OBSERVATION_DATE_U_TYPE, "aaa", true, null);
        verifyFormInput(OBSERVATION_DATE_U_TYPE, "", false, "");

        toggleDetailsItem(OBSERVATION_DATE_U_TYPE_DETAILS);
        select(By.id(OBSERVATION_DATE_U_TYPE + "_PRESET"), "PAST_WEEK");
        verifyFormInputMessageMatches(OBSERVATION_DATE_U_TYPE,
                                      OBSERVATION_DATE_U_TYPE_DETAILS, false,
                                      "(.*)\\d\\.\\.\\d(.*)");
        // Close it again.
        toggleDetailsItem(OBSERVATION_DATE_U_TYPE_DETAILS);

        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "aaa", true, null);
        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "", false, "");


        // Quantify the unit conversion values.
        verifyFormInput(PIXEL_SCALE_U_TYPE, "0.02..0.05arcmin", false,
                        "(1.2..3.0 arcseconds)");
        verifyFormInput(PIXEL_SCALE_U_TYPE, "", false, "");

        resetForm();
        waitFor(2);

        verifyFormInput(OBSERVATION_DATE_U_TYPE, "> 2010-09-22", false,
                        "(>= 55461.0 MJD)");
        verifyFormInput(OBSERVATION_DATE_U_TYPE, "", false, "");

        verifyFormInput("Plane.time.exposure", "2m..1h", false,
                        "(120.0..3600.0 seconds)");
        verifyFormInput("Plane.time.exposure", "", false, "");

        verifyFormInput("Plane.time.bounds.width", "1y..2y", false,
                        "(365.0..730.0 days)");
        verifyFormInput("Plane.time.bounds.width", "", false, "");

        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "300..400GHz", false,
                        "(7.495E-4..9.993E-4 metres)");
        verifyFormInput(SPECTRAL_COVERAGE_U_TYPE, "", false, "");

        verifyFormInput("Plane.energy.sampleSize", "2..3GHz", false,
                        "(2.0E9..3.0E9 Hz)");
        verifyFormInput("Plane.energy.sampleSize", "", false, "");

        verifyFormInput("Plane.energy.bounds.width", "< 1000A", false,
                        "(< 1.000E-7 metres)");
        verifyFormInput("Plane.energy.bounds.width", "", false, "");
        //*/
    }

    void verifyFormInput(final String inputID, final String entry,
                         final boolean expectError,
                         final String expectedLabelMessage)
            throws Exception
    {
        final String detailsID = inputID + "_details";
        inputTextValue(By.id(inputID), detailsID, entry);
        waitFor(2);

        if (expectError)
        {
            // Empty strings are valid as they represent cleared errors.
            if (expectedLabelMessage != null)
            {
                verifyFormInputMessage(inputID, detailsID, true,
                                       expectedLabelMessage);
            }
            else
            {
                verifyFormInputError(inputID);
            }
        }
        else
        {
            verifyFormInputMessage(inputID, detailsID, false,
                                   expectedLabelMessage);
        }
        hideInputBox(detailsID, inputID);
    }

    void verifyFormInputMessage(final String inputID, final String detailsID,
                                final boolean errorExpected,
                                final String expectedMessage)
            throws Exception
    {
        if (errorExpected)
        {
            verifyFormInputError(inputID);
        }

        final String itemLocator =
                "//details[@id='" + detailsID
                + "']/summary/span[contains(@class,'search_criteria_label_contents')]";

        verifyText(By.xpath(itemLocator), expectedMessage);
    }

    void verifyFormInputMessageMatches(final String inputID,
                                       final String detailsID,
                                       final boolean errorExpected,
                                       final String messageRegex)
            throws Exception
    {
        if (errorExpected)
        {
            verifyFormInputError(inputID);
        }

        final String itemLocator =
                "//details[@id='" + detailsID
                + "']/summary/span[contains(@class,'search_criteria_label_contents')]";

        verifyTextMatches(By.xpath(itemLocator), messageRegex);
    }

    void verifyFormInputError(final String inputID) throws Exception
    {
        verifyElementPresent(
                By.xpath("//div[@id='" + inputID
                         + "_input_decorate'][contains(@class,'form-attention')]"));
    }
}
