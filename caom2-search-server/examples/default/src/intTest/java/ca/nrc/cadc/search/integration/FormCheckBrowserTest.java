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
 * 15/05/14 - 2:19 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.integration;

import org.junit.Test;


public class FormCheckBrowserTest extends AbstractAdvancedSearchIntegrationTest
{
    public FormCheckBrowserTest() throws Exception
    {
        super();
    }

    /**
     * Verify the form page (Query Tab)'s items.
     *
     * @throws Exception
     */
    @Test
    public void verifyForm() throws Exception
    {
        final CAOMSearchFormPage caomSearchFormPage = goTo(ENGLISH_ENDPOINT, "", CAOMSearchFormPage.class);
        verifyTooltips(caomSearchFormPage);
    }

    /**
     * Ensure tooltips show, and stay open when values are typed in.
     *
     * @throws Exception
     */
    private void verifyTooltips(final CAOMSearchFormPage searchFormPage) throws Exception
    {
        searchFormPage.summonTooltip("Plane.position.bounds_details");
        searchFormPage.closeTooltip();

        searchFormPage.summonTooltip("Plane.position.sampleSize_details");
        searchFormPage.closeTooltip();

        searchFormPage.summonTooltip("Plane.time.bounds_details");
        searchFormPage.closeTooltip();
    }
}
