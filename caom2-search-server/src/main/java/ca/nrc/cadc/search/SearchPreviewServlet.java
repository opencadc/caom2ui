/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 12/1/11 - 8:51 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.profiler.Profiler;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.util.JobURLCreator;
import ca.nrc.cadc.web.ConfigurableServlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;


public class SearchPreviewServlet extends ConfigurableServlet {
    private static Logger log = Logger.getLogger(SearchPreviewServlet.class);

    private static final String CAOM2LINK_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.caom2link-service-id";
    private static final URI DEFAULT_CAOM2LINK_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/caom2ops");

    private PreviewRequestHandler previewRequestHandler;
    private Profiler profiler = null;

    /**
     * Complete constructor.
     *
     * @param previewRequestHandler Request handler for Preview requests.
     */
    public SearchPreviewServlet(final PreviewRequestHandler previewRequestHandler) {
        this.previewRequestHandler = previewRequestHandler;
    }

    /**
     * Constructor to use the Registry Client to obtain the Data Web Service
     * location.
     */
    public SearchPreviewServlet() {
        this.profiler = new Profiler(SearchPreviewServlet.class);
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final RegistryClient registryClient = new RegistryClient();
        final String uriStr = req.getParameter("id");
        URL serviceURL = null;
        profiler.checkpoint(String.format("%s doGet() start", uriStr));

        // PublisherID format expected: <resourceID>?<query string>
        if (uriStr.length() > 0) {
            try {
                // split the ID parameter on '?' to pull off the query string.
                // use the first part (resourceid) in the getServiceURL.
                String[] uri_parts = uriStr.split("\\?");
                if (uri_parts.length < 2) {
                    throw new UnsupportedOperationException("Invalid Publisher ID in package lookup.");
                }

                serviceURL = registryClient.getServiceURL(
                    new URI(uri_parts[0]),
                    Standards.DATALINK_LINKS_10,
                    AuthMethod.COOKIE);
                log.info("serviceURL to use: " + serviceURL);
            } catch (URISyntaxException use) {
                throw new UnsupportedOperationException(use);
            }
        } else {
            throw new UnsupportedOperationException("Invalid Publisher ID in package lookup.");
        }

        previewRequestHandler = new PreviewRequestHandler(serviceURL, new JobURLCreator() {
            /**
             * Create a Job URL.
             *
             * @param dataServiceURL The URL for the Data service.
             * @param request        The HTTP Servlet Request.
             * @return URL instance.  Never null.
             * @throws IOException For any IO errors.
             */
            @Override
            public URL create(final URL dataServiceURL, final HttpServletRequest request) throws IOException {
                URL handlerUrl = new URL(dataServiceURL + "?id=" + request.getParameter("id"));
                log.info("handlerUrl: " + handlerUrl);
//                return new URL(dataServiceURL + "?id=" + request.getParameter("id"));
                return handlerUrl;
            }
        });

        this.previewRequestHandler.get(req, resp);
        profiler.checkpoint(String.format("%s doGet() end", uriStr));
    }

}
