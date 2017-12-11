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

package ca.nrc.cadc.caom2.ui.server;

import ca.nrc.cadc.web.selenium.AbstractTestWebPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.ArrayList;
import java.util.List;


public class ObservationViewPage extends AbstractTestWebPage {
    private static final int PAGE_TIMEOUT_SECONDS = 5;

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(css = "body > div.main > div.observation > div.plane")
    private WebElement firstPlane;

    /**
     * Constructors need to be public for reflection to find them.
     *
     * @param driver WebDriver instance.
     */
    public ObservationViewPage(final WebDriver driver) {
        super(driver, PAGE_TIMEOUT_SECONDS);

        PageFactory.initElements(driver, this);

        verifyTrue(title.getText().equals("Common Archive Observation Model (CAOM2)"));
    }


    public void ensureLoaded() throws Exception {
        final List<String> h3headers = new ArrayList<>();
        h3headers.add("Chunk");

        final List<String> h2headers = new ArrayList<>();
        h2headers.add("SimpleObservation");
        h2headers.add("Plane");
        h2headers.add("Artifact");
        h2headers.add("Part");

        for (final String header : h3headers) {
            final String xpath = "//h3[contains(text(),'" + header + "')]";
            find(By.xpath(xpath));
        }

        for (final String h2header : h2headers) {
            final String xpath = "//h2[contains(text(),'" + h2header + "')]";
            find(By.xpath(xpath));
        }
    }

    public void ensureProvenanceReferenceLink() throws Exception {
        final WebElement table = firstPlane.findElement(By.cssSelector("table.content"));
        final WebElement provenanceTableRow = table.findElement(By.cssSelector("tr.provenance"));
        waitForElementClickable(provenanceTableRow.findElement(
            By.cssSelector("td:nth-child(2) > a.provenance-reference")));
    }
}
