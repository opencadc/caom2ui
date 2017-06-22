/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package ca.nrc.cadc.search.integration;

import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.web.selenium.AbstractTestWebPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


abstract class AbstractSearchFormPage extends AbstractTestWebPage
{
    private static final String DETAILS_LOCATOR_XPATH = "//details[@id='%s']/summary/label/span";
    private static final String CONTENT_LOCATOR_XPATH = "//*[@id='%s']/summary/label/span";

    @FindBy(className = "submit-query")
    private WebElement topSubmitButton;

    @FindBy(className = "reset-query-form")
    private WebElement topResetButton;


    AbstractSearchFormPage(final WebDriver driver) throws Exception
    {
        super(driver);

        PageFactory.initElements(driver, this);
    }

    AbstractSearchFormPage(WebDriver driver, int timeoutInSeconds)
    {
        super(driver, timeoutInSeconds);

        PageFactory.initElements(driver, this);
    }


    void enterInputValue(final WebElement inputElement, final String value) throws Exception
    {
        final String inputID = inputElement.getAttribute("id");
        final String detailElementID = inputID + "_details";
        //TODO: uncomment when tooltip implementation is complete
        //summonTooltip(detailElementID);
        showInputField(inputID);
        sendKeys(inputElement, value);
        //TODO: uncomment when tooltip implementation is complete
        //closeTooltip();
    }

    void clearInputValue(final String inputID) throws Exception
    {
        final WebElement inputElement = find(By.id(inputID));

        if (inputElement.isDisplayed())
        {
            sendKeys(inputElement, "");
            //TODO: uncomment when tooltip implementation is complete
            //hideInputField(inputID);
        }
        else
        {
            throw new IllegalStateException("Input element " + inputID + " is not displayed.");
        }
    }

    void verifyFormInputError(final String inputID) throws Exception
    {
        waitForElementPresent(By.xpath("//div[@id='" + inputID
                                       + "_input_decorate'][contains(@class,'has-error')]"));
    }

    void verifyFormInputMessage(final String inputID, final boolean errorExpected, final String expectedMessage)
            throws Exception
    {
        if (errorExpected)
        {
            verifyFormInputError(inputID);
        }

        final String itemLocator = "//*[@id='" + inputID
                + "_details']/summary/label/span[contains(@class,'search_criteria_label_contents')]";

        final By contents = By.xpath(itemLocator);

        if (expectedMessage != "")
        {
            waitForTextPresent(contents, expectedMessage);
        }
        else
        {
            WebElement contentEl = find(contents);
            if (contentEl.getText().equals("") == false)
            {
                throw new Exception();
            }
        }
    }

    void verifyFormInputMessageEmpty(final String inputID)
            throws Exception
    {

        WebElement contents = find(By.xpath(String.format(CONTENT_LOCATOR_XPATH, (inputID + "_details"))));


    }


    void verifyFormInputMessageMatches(final String inputID, final boolean errorExpected,
                                       final String messageRegex) throws Exception
    {
        if (errorExpected)
        {
            verifyFormInputError(inputID);
        }

        final String itemLocator = "//details[@id='" + inputID
                + "_details')]/summary/label/span[contains(@class,'search_criteria_label_contents')]";

        verifyTextMatches(By.xpath(itemLocator), messageRegex);
    }

    void hideInputBox(final String inputID) throws Exception
    {
        final String xpath = "//details[@id='" + (inputID + "_details") + "']/summary/label/span";
        click(By.xpath(xpath));

        waitForElementInvisible(By.id(inputID));
    }

    void summonTooltip(final String baseID) throws Exception
    {

        final By tooltipIconTriggerBy = By.xpath("//div[@id='" + baseID
                                                 + "_formgroup']/div[contains(@class, 'advancedsearch-tooltip')]");

        waitForElementPresent(tooltipIconTriggerBy);
        click(tooltipIconTriggerBy);
    }

    void closeTooltip(final String baseID) throws Exception
    {
        String tooltipID = baseID + "_close";
        click(By.id(tooltipID));
        waitForElementNotPresent(By.id(tooltipID));
    }


    /**
     * Ensure the field is open.
     *
     * @param inputID The String ID locator.
     * @throws Exception Any error.
     */
    void showInputField(final String inputID) throws Exception
    {
        final WebElement element = find(By.xpath(String.format(DETAILS_LOCATOR_XPATH, (inputID + "_details"))));

        if (!find(By.id(inputID)).isDisplayed())
        {
            // show the input box
            click(element);
        }
    }

    /**
     * Ensure the field is open.
     *
     * @param inputID The String ID locator.
     * @throws Exception Any error.
     */
    void hideInputField(final String inputID) throws Exception
    {
        final WebElement element = find(By.xpath(String.format(DETAILS_LOCATOR_XPATH, (inputID + "_details"))));

        if (find(By.id(inputID)).isDisplayed())
        {
            // show the input box
            click(element);
        }
    }

    SearchResultsPage submitSuccess() throws Exception
    {
        click(topSubmitButton);

        return new SearchResultsPage(driver);
    }

    void reset() throws Exception
    {
        click(topResetButton);
    }
}
