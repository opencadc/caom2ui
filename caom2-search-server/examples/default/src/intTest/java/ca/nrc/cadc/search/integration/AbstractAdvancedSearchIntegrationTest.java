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
 * 12/13/13 - 1:44 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.integration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;


public abstract class AbstractAdvancedSearchIntegrationTest
        extends AbstractWebApplicationIntegrationTest
{
    static final Pattern ROW_COUNT_PATTERN = Pattern.compile("\\d+");
    static final String ENGLISH_ENDPOINT = "/en/search/";

    AbstractAdvancedSearchIntegrationTest()
    {
        super();
        setFailOnTimeout(true);
    }


    String getPagerStatusText() throws Exception
    {
        return find(By.className("grid-header-label")).getText();
    }

    int getCurrentResultsRowCount() throws Exception
    {
        final Matcher matcher = ROW_COUNT_PATTERN.matcher(getPagerStatusText());
        final int count;

        if (matcher.find())
        {
            count = Integer.parseInt(matcher.group());
        }
        else
        {
            count = -1;
        }

        return count;
    }

    boolean isObsCoreEnabled()
    {
        try
        {
            find(By.linkText("ObsCore Search"));
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Verify if the given search results page has results and is proper.
     *
     * @param searchResultsPage     The page to check.
     * @param shouldHaveResults     Whether the rowcount should be > 0.
     * @throws Exception
     */
    void verifyGridHeaderLabelHasIntegerValue(
            final SearchResultsPage searchResultsPage,
            final boolean shouldHaveResults) throws Exception
    {
        final String result = searchResultsPage.getPagerStatusText();
        verifyTrue(result.startsWith("Showing "));
        verifyTrue(result.indexOf(" rows") > 0);

        if (shouldHaveResults)
        {
            final int rowCount = searchResultsPage.getCurrentResultsRowCount();
            verifyTrue(rowCount > 0);
        }
    }

    /**
     * Will throw a NumberFormatException when no integer present.
     *
     * @param checkForResults Whether to check for a row count > 0.
     * @throws Exception
     * @deprecated Use {@link #verifyGridHeaderLabelHasIntegerValue(SearchResultsPage, boolean)}
     *              instead, and adopt the Page Object model.
     */
    void verifyGridHeaderLabelHasIntegerValue(
            final boolean checkForResults) throws Exception
    {
        final String result = getPagerStatusText();
        verifyTrue(result.startsWith("Showing "));
        verifyTrue(result.indexOf(" rows") > 0);

        final int rowCount = getCurrentResultsRowCount();

        if (checkForResults)
        {
            verifyTrue(rowCount > 0);
        }
    }

    /**
     * Open the home page.
     *
     * @throws Exception
     */
    void goToHomePage() throws Exception
    {
        goToHomePage(null);
    }

    /**
     * Open the home page.
     *
     * @throws Exception
     */
    void goToHomePage(final String query) throws Exception
    {
        goToApplication(query);

        waitForElementPresent(By.id("caom2@Hierarchy"));
        waitForElementPresent(By.cssSelector("select[id='Observation.type']"));
        waitOneSecond();
    }

    /**
     * Same as goToHomePage, but doesn't wait for form elements.  This is mainly
     * used for bookmark searches.
     *
     * @param query         The URL Query.
     * @throws Exception
     */
    void goToApplication(final String query) throws Exception
    {
        goTo("/en/search/", query);
    }

    /**
     * Go to the query tab.
     *
     * @throws Exception
     */
    void queryTab() throws Exception
    {
        tab("queryFormTab-link");
    }

    void adqlTab() throws Exception
    {
        tab("queryTab-link");
        verifyElementPresent(By.id("query_holder"));
    }

    /**
     * Go to the results tab.
     *
     * @throws Exception
     */
    void resultsTab() throws Exception
    {
        tab("resultTableTab-link");
    }

    void tab(final String id) throws Exception
    {
        click(By.id(id));
        waitOneSecond();
    }

    void login() throws Exception
    {
        // Click login
        find(By.linkText("Login")).click();
        waitOneSecond();

        inputTextValue(By.id("username"), getUsername());
        inputTextValue(By.id("password"), getPassword());
        find(By.id("login_button")).click();

        waitForTextPresent("Logout");
        waitOneSecond();
    }

    void logout() throws Exception
    {
        click(By.linkText("Sharon Goliath"));
        click(By.linkText("Logout"));

        waitForElementPresent(By.linkText("Login"));
    }

    /**
     * Execute a search and wait for the Results Grid to be displayed.
     *
     * @param expectResultPage Whether the search should succeed to the
     *                         results page.
     * @param expectResults    Whether or not to expect a row count > 0.
     * @throws Exception
     */
    void searchAndWait(final boolean expectResultPage,
                                 final boolean expectResults)
            throws Exception
    {
        find(By.className("submit-query")).click();
        waitForSearch(expectResultPage, expectResults);
    }

    /**
     * Wait for an already started search.
     *
     * @param expectResultPage Whether the search should succeed to the
     *                         results page.
     * @param expectResults    Whether or not to expect a row count > 0.
     * @throws Exception
     * @deprecated Use {@link SearchResultsPage instead}
     */
    void waitForSearch(final boolean expectResultPage,
                                 final boolean expectResults)
            throws Exception
    {
        waitForElementPresent(By.className("grid-header-label"));

        // Allow for streaming to finish.
        waitFor(5);

        if (expectResultPage)
        {
            waitForElementPresent(By.cssSelector("div.slick-viewport"));
            waitForElementPresent(By.id("results_bookmark"));
            waitForElementPresent(By.className("grid-header-icon"));
            waitForElementPresent(
                    By.xpath("//div[@title='Select/Deselect All']"));
            verifyGridHeaderLabelHasIntegerValue(expectResults);
        }
    }

    void closeTooltip() throws Exception
    {
        click(By.className("tooltip-close"));
    }

    void summonTooltip(final String detailLabelID) throws Exception
    {
        final WebElement parentElement =
                find(By.id(detailLabelID)).findElement(By.tagName("summary"));
        hover(parentElement.findElement(By.className("advancedsearch-tooltip")));

        waitForElementPresent(
                By.xpath("//details[@id='" + detailLabelID
                         + "']/summary/span[contains(@class, 'wb-icon-question-alt')]"));
        click(parentElement.findElement(By.className("wb-icon-question-alt")));
    }

    /**
     * Input a text field value.  This should work for text areas as well.
     *
     * @param inputID The locator (e.g. id=fieldID).
     * @param value   The value.
     * @throws Exception
     */
    void inputTextValue(final By inputID, final String detailLabelID,
                        final String value)
            throws Exception
    {
//        summonTooltip(detailLabelID);
        toggleDetailsItem(detailLabelID);
        inputTextValue(inputID, value);
    }

    void checkboxOn(final By locator) throws Exception
    {
        toggleCheckbox(locator);

        final WebElement checkboxElement = find(locator);
        verifyTrue(checkboxElement.isSelected());
    }

    void toggleCheckbox(final By locator) throws Exception
    {
        final WebElement checkboxElement = find(locator);

        checkboxElement.click();
        waitOneSecond();
    }

    /**
     * Toggle a details item
     *
     * @param detailLabelID     The String ide locator.
     */
    void toggleDetailsItem(final String detailLabelID) throws Exception
    {
        final String xpath =
                "//details[@id='" + detailLabelID + "']/summary/span";
        final WebElement element = find(By.xpath(xpath));

        // show the input box
        element.click();
    }

    void hideInputBox(final String detailLabelID, final String locator)
            throws Exception
    {
        final String xpath =
                "//details[@id='" + detailLabelID + "']/summary/span";
        click(By.xpath(xpath));

        waitUntil(ExpectedConditions.invisibilityOfElementLocated(
                By.id(locator)));
    }

    void relocate() throws Exception
    {
        hover(By.id("downloadFormSubmit"));
    }
}
