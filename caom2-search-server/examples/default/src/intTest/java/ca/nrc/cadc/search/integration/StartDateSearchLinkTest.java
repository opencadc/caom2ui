/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                            (c) 2020.
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
 * @author jeevesh
 * 03/11/20

 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.integration;


import java.net.URI;
import org.junit.Test;
import org.openqa.selenium.By;


public class StartDateSearchLinkTest extends AbstractAdvancedSearchIntegrationTest
{
    private static final String SEARCH_QUERY = "Observation.instrument.name=Newtonian%20Imager&Observation.collection=DAO&Plane.time.bounds.samples=52865..52866";

    public StartDateSearchLinkTest() throws Exception
    {
        super();
    }

    @Test
    public void checkStartDateSearchLink() throws Exception
    {
        String searchStr = "Observation.instrument.name=Newtonian%20Imager&Observation.collection=DAO&Plane.time.bounds.samples=52865..52866";
        CAOMSearchFormPage searchFormPage = goToMain(CAOMSearchFormPage.class);

        searchFormPage = searchFormPage.dismissCookieBanner();

        // This combination usually produces only one result
        searchFormPage.enterCollection("DAO");
        searchFormPage.enterTarget("m17");
        SearchResultsPage searchResultsPage = searchFormPage.submitSuccess();

        String result = searchResultsPage.getStartDateLink();
        URI testHref = new URI(result);
        verifyEquals(testHref.getRawQuery(), searchStr);
    }
}
