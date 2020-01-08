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

package ca.nrc.cadc.caom2.ui.server;


import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.util.Map;


public class WalkthroughTest extends AbstractWebApplicationIntegrationTest {

    public WalkthroughTest() throws Exception {
        super();
    }

    @Test
    public void observationViewTest() throws Exception {
        // TODO: need an observation that exists in dev, production and (beta?)
        final ObservationViewPage observationViewPage =
                goTo("/view", String.format("ID=%s", NetUtil.encode("ivo://cadc.nrc.ca/IRIS?f008h000")),
                     ObservationViewPage.class);
        observationViewPage.ensureLoaded();
        observationViewPage.ensureProvenanceReferenceLink();
    }

    @Test
    public void observationViewTestCFHTMEGAPIPE() throws Exception {
        // TODO: need an observation that exists in dev, production and (beta?)
        final ObservationViewPage observationViewPage =
                goTo("/view", String.format("ID=%s", NetUtil.encode("ivo://cadc.nrc.ca/CFHTMEGAPIPE?MegaPipe.189.210")),
                     ObservationViewPage.class);
        observationViewPage.ensureLoaded();
        observationViewPage.ensureProvenanceReferenceLink();
        observationViewPage.ensureMemberLinkCount(136);
    }

    @Test
    public void observationViewTestHST() throws Exception {
        // TODO: need an observation that exists in dev, production and (beta?)
        final ObservationViewPage observationViewPage =
                goTo("/view", String.format("ID=%s", NetUtil.encode("ivo://cadc.nrc.ca/mirror/HST?jbeoft020")),
                     ObservationViewPage.class);
        observationViewPage.ensureLoaded();
        observationViewPage.ensureProvenanceReferenceLink();
        observationViewPage.ensureMemberLinkCount(2);

        final Map<URI, URL> memberLinkMap = observationViewPage.getMemberLinks();
        final URI firstKey = URI.create("caom:HST/jbeoftneq");
        final URL firstURL = memberLinkMap.get(firstKey);

        Assert.assertTrue(
                String.format("Expected to see %s but got %s.", NetUtil.encode("/mirror/HST?jbeoftneq"),
                              firstURL.toExternalForm()),
                firstURL.toExternalForm().contains(NetUtil.encode("/mirror/HST?jbeoftneq")));

        final URI secondKey = URI.create("caom:HST/jbeoftnhq");
        final URL secondURL = memberLinkMap.get(secondKey);

        Assert.assertTrue(
                String.format("Expected to see %s but got %s.", NetUtil.encode("/mirror/HST?jbeoftnhq"),
                              secondURL.toExternalForm()),
                secondURL.toExternalForm().contains(NetUtil.encode("/mirror/HST?jbeoftnhq")));
    }
}
