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
 * 15/05/14 - 2:20 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.integration;

import ca.nrc.cadc.web.selenium.AbstractTestWebPage;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;


public class ExternalAccessBrowserTest
        extends AbstractAdvancedSearchIntegrationTest
{
    /**
     * @throws Exception
     */
    @Test
    public void externalWalkthrough() throws Exception
    {
        CAOMSearchFormPage searchFormPage =
                goTo(ENGLISH_ENDPOINT, null, CAOMSearchFormPage.class);

        searchFormPage.reset();
        searchFormPage.enterCollection("IRIS");

        SearchResultsPage searchResultsPage = searchFormPage.submitSuccess();

        AbstractTestWebPage canadaSitePage =
                searchResultsPage.clickCanadaSiteLink();

        searchResultsPage = canadaSitePage.goBack(SearchResultsPage.class);

        searchFormPage = searchResultsPage.queryTab();
        searchFormPage.enterCollection("BLAST");
    }
}
