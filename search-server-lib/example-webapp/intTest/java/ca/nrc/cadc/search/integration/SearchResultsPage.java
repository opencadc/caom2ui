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


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.nrc.cadc.web.selenium.AbstractTestWebPage;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SearchResultsPage extends AbstractTestWebPage
{
    static final Pattern ROW_COUNT_PATTERN = Pattern.compile("\\d+");

    static final By IQ_COLUMN_HEADER =
            By.cssSelector("div[id$='caom2:Plane.position.resolution']");
    static final By REST_FRAME_COLUMN_HEADER =
            By.cssSelector("div[id$='caom2:Plane.energy.restwav']");
    static final By REST_FRAME_ENERGY_UNIT_SELECT_LOCATOR =
            By.id("caom2:Plane.energy.restwav_unitselect");
    static final By IQ_UNIT_SELECT_LOCATOR =
            By.id("caom2:Plane.position.resolution_unitselect");
    private static final By FILTER_FILTER_BY =
            By.id("caom2:Plane.energy.bandpassName_filter");
    private static final By RA_FILTER_BY =
            By.id("caom2:Plane.position.bounds.cval1_filter");
    private static final By DEC_FILTER_BY =
            By.id("caom2:Plane.position.bounds.cval2_filter");

    static final String ICON_BUSY_SRC = "/cadcVOTV/images/PleaseWait-small.gif";
    static final String ICON_IDLE_SRC = "/cadcVOTV/images/transparent-20.png";

    static final By GRID_LOCATOR = By.id("resultTable");
    static final By GRID_HEADER_LOCATOR = By.id("results-grid-header");
    static final By GRID_HEADER_LABEL_LOCATOR =
            By.className("grid-header-label");
    static final String OBSERVATION_DETAILS_LINK_LOCATOR =
            "caom2:Observation.observationID_%d_observation_details";
    static final By FIRST_QUICKSEARCH_TARGET_LINK =
            By.cssSelector("a.quicksearch_link:nth-child(1)");
    static final By QUERY_TAB_LOCATOR = By.id("queryFormTab-link");

    // Switches between busy and transparent (idle).
    static final By GRID_HEADER_ICON = By.className("grid-header-icon");

    @FindBy(className = "grid-container")
    private WebElement gridContainer;


    public SearchResultsPage(final WebDriver driver) throws Exception
    {
        super(driver);

        waitForGridToLoad();

        PageFactory.initElements(driver, this);
    }


    void waitForGridToLoad() throws Exception
    {
        waitForElementPresent(GRID_LOCATOR);
        waitForElementPresent(GRID_HEADER_LOCATOR);
        waitForElementPresent(GRID_HEADER_ICON);

        waitUntil(new ExpectedCondition<Boolean>()
        {
            @Override
            public Boolean apply(final WebDriver webDriver)
            {
                final String srcAttribute =
                        webDriver.findElement(GRID_HEADER_ICON)
                                .getAttribute("src");
                return srcAttribute.endsWith(ICON_IDLE_SRC);
            }
        });

        waitForTextPresent(GRID_HEADER_LABEL_LOCATOR, "Showing");
        waitForElementPresent(FIRST_QUICKSEARCH_TARGET_LINK);
    }

    SearchResultsPage quickSearchTarget() throws Exception
    {
        click(FIRST_QUICKSEARCH_TARGET_LINK);
        return new SearchResultsPage(driver);
    }

    WebElement getGridHeader() throws Exception
    {
        return gridContainer.findElement(GRID_HEADER_LOCATOR);
    }

    WebElement getGrid() throws Exception
    {
        return gridContainer.findElement(GRID_LOCATOR);
    }

    String getPagerStatusText() throws Exception
    {
        return getGridHeader().findElement(GRID_HEADER_LABEL_LOCATOR).getText();
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

    CAOMObservationDetailsPage openObservationDetails(final int rowNumber)
            throws Exception
    {
        click(By.id(String.format(OBSERVATION_DETAILS_LINK_LOCATOR,
                                  rowNumber)));

        return new CAOMObservationDetailsPage(driver);
    }

    CAOMSearchFormPage queryTab() throws Exception
    {
        click(QUERY_TAB_LOCATOR);
        return new CAOMSearchFormPage(driver);
    }

    WebElement getIQColumnHeader() throws Exception
    {
        return getGrid().findElement(IQ_COLUMN_HEADER);
    }

    WebElement getRestFrameEnergyColumnHeader() throws Exception
    {
        return getGrid().findElement(REST_FRAME_COLUMN_HEADER);
    }

    String getSelectedRestFrameEnergyUnit() throws Exception
    {
        final Select rfUnitSelect = new Select(gridContainer.findElement(
                REST_FRAME_ENERGY_UNIT_SELECT_LOCATOR));
        final WebElement rfUnitSelectedOption =
                rfUnitSelect.getFirstSelectedOption();

        return rfUnitSelectedOption.getText();
    }

    String getSelectIQUnit() throws Exception
    {
        final Select iqUnitSelectElement = new Select(gridContainer.findElement(
                IQ_UNIT_SELECT_LOCATOR));
        return iqUnitSelectElement.getFirstSelectedOption().getText();
    }

    void filterOnRA(final String value) throws Exception
    {
        sendKeys(find(RA_FILTER_BY), value);
    }
}
