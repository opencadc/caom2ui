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
 * 15/05/14 - 1:19 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.integration;


import org.junit.Test;


public class AdvancedSearchBrowserTest extends CAOMSearchBrowserTest {

    public AdvancedSearchBrowserTest() {
        super();
    }


    @Test
    public void searchCAOM() throws Exception {
        super.searchCAOM();

        AdvancedSearchFormPage searchFormPage = goToMain(AdvancedSearchFormPage.class);

        // Nav back to query tab for next test
        searchFormPage.reset();
        searchFormPage.uncheckMAQ();

        // Test login and logout
        System.out.println("Test login");
        searchFormPage = loginTest(searchFormPage);

        System.out.println("Test logout");
        searchFormPage = searchFormPage.doLogout();
        verifyFalse(searchFormPage.isLoggedIn());

        System.out.println("searchCAOM test complete.");

        /*
        TODO - Complete for new Page Object model going forward.
        TODO - jenkinsd 2016.02.16
         */

        /*
        verifyEquals("18:03:48.78",
                     find(By.xpath(
                             "//div[@id='resultTable']/div[5]/div[3]/div/div/div[4]/span")).
                             getText());

        // Dec. column
        inputTextValue(DEC_FILTER_BY, "< 70:00");
        waitFor(3);

        verifyText(By.xpath("//div[@id='resultTable']/div[5]/div[3]/div/div/div[5]/span"),
                   "-49:59:07.6");

        waitOneSecond();
        inputTextValue(RA_FILTER_BY, "");
        inputTextValue(DEC_FILTER_BY, "");
        waitOneSecond();

        scrollGridHorizontally("resultTable");
        inputTextValue(FILTER_FILTER_BY, "10", true, false);

        waitFor(2);
        verifyText(By.xpath("//div[@id='resultTable']/div[5]/div[3]/div/div[2]/div[10]/span"),
                   "IRAS-100um");

        inputTextValue(FILTER_FILTER_BY, "");

        click(COLUMN_MANAGER_BUTTON_BY);

        waitOneSecond();

        click(PI_NAME_COLUMN_ADD_BY);
        waitOneSecond();

        if (!getInternetBrowserCommand().equals("*safari"))
        {
            hover(By.xpath("//span[@class='slick-column-name'][contains(text(), \"Collection\")]"));
        }

        waitFor(2);

        verifyElementPresent(By.className("votable_link_votable"));
        verifyElementPresent(By.className("votable_link_csv"));
        verifyElementPresent(By.className("votable_link_tsv"));

        // Next search.
        queryTab();
        resetForm();
        waitFor(2);

        // observerationID of proprietary data
        inputTextValue(OBSERVATION_ID_FORM_FIELD_BY,
                       "Observation.observationID_details", "1692420");

        searchAndWait(true, true);

        // data release of proprietary data
        queryTab();
        resetForm();
        waitFor(2);

        // Open it.
        toggleDetailsItem("Plane.dataRelease_details");
        final By publicCheckboxBy =
                By.id("Plane.dataRelease@PublicTimestampFormConstraint.value");
        checkboxOn(publicCheckboxBy);
        verifyFalse(find(
                By.id("Plane.dataRelease")).isEnabled());
        inputTextValue(OBSERVATION_ID_FORM_FIELD_BY,
                       "Observation.observationID_details", "f008h000");

        searchAndWait(true, true);

        adqlTab();
        verifyTrue(find(
                By.id("query")).getText().contains("Plane.dataRelease <="));

        // Do authenticated stuff.

        login();

        waitFor(5);
        queryTab();
        waitForElementPresent(By.id("Observation.type"));
        resetForm();
        waitFor(2);

        // observationID of proprietary data
        inputTextValue(OBSERVATION_ID_FORM_FIELD_BY,
                       "Observation.observationID_details", "1692420");

        searchAndWait(true, true);

        click(COLUMN_MANAGER_BUTTON_BY);
        waitForElementPresent(
                By.id(COLUMN_PICKER_AVAILABLE_ITEMS_CONTAINER_ID));

        waitOneSecond();

        logout();
        */
    }


    private AdvancedSearchFormPage loginTest(final AdvancedSearchFormPage userPage) throws Exception {
        final AdvancedSearchFormPage authPage = userPage.doLogin(username, password);
        verifyTrue(userPage.isLoggedIn());
        System.out.println("logged in");

        return authPage;
    }


}
