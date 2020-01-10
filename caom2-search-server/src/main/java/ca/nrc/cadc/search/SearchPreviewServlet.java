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
import ca.nrc.cadc.caom2.PublisherID;
import ca.nrc.cadc.profiler.Profiler;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.util.JobURLCreator;
import ca.nrc.cadc.web.ConfigurableServlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;

import org.apache.log4j.Logger;


public class SearchPreviewServlet extends ConfigurableServlet {

    private static Logger log = Logger.getLogger(SearchPreviewServlet.class);

    private PreviewRequestHandler previewRequestHandler;

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
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final RegistryClient registryClient = new RegistryClient();
        final String uriStr = req.getParameter("id");
        final Profiler profiler = new Profiler(SearchPreviewServlet.class);
        profiler.checkpoint(String.format("%s doGet() start", uriStr));

        // PublisherID format expected: <resourceID>?<query string>
        if (uriStr.length() > 0) {
            final PublisherID publisherID = new PublisherID(URI.create(uriStr));

            final URL serviceURL = registryClient.getServiceURL(
                    publisherID.getResourceID(), Standards.DATALINK_LINKS_10, AuthMethod.COOKIE);
            log.info("serviceURL to use: " + serviceURL);

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
                    final String IDValue = request.getParameter("id");
                    try {
                        final URIBuilder builder = new URIBuilder(dataServiceURL.toURI());
                        builder.addParameter("ID", IDValue);

                        final URL handlerUrl = builder.build().toURL();
                        log.info("handlerUrl: " + handlerUrl);
                        return handlerUrl;
                    } catch (URISyntaxException e) {
                        throw new IOException(String.format("Service URL from %s is invalid.", IDValue), e);
                    }
                }
            });

            this.previewRequestHandler.get(req, resp);
            profiler.checkpoint(String.format("%s doGet() end", uriStr));
        } else {
            throw new UnsupportedOperationException("Invalid Publisher ID in package lookup.");
        }
    }

}
