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
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;

import ca.nrc.cadc.tap.TAPServlet;
import ca.nrc.cadc.web.ConfigurableServlet;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


public class SearchPreviewServlet extends ConfigurableServlet
{
    private static final Logger log  = Logger.getLogger(SearchPreviewServlet.class);
    private static final String CAOM2LINK_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.caom2link-service-id";
    private static final URI DEFAULT_CAOM2LINK_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/caom2ops");
    private static final String SC2LINK_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.maq-datalink-service-id";
    private static final URI SC2LINK_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/sc2links");

    /**
     * Constructor to use the Registry Client to obtain the Data Web Service
     * location.
     */
    public SearchPreviewServlet()
    {
    }

    /**
     * Testing can override at will.
     *
     * @return RegistryClient instance.  Never null.
     */
    RegistryClient getRegistryClient() {
        return new RegistryClient();
    }

    /**
     * Testing can override at will.
     *
     * @return HttpDownload instance.  Never null.
     */
    HttpDownload createDownloader(final URL url, final OutputStream outputStream) {
        return new HttpDownload(url, outputStream);
    }

    /**
     * Form the URL for the job as based on the given parameter.
     *
     * @param request           The HTTP Request.
     * @return                  A URL instance.
     * @throws IOException      If the URL cannot be created.
     */
    protected URL createJobURL(final HttpServletRequest request)
            throws IOException
    {
        final RegistryClient registryClient = getRegistryClient();
        URL dataServiceURL = registryClient.getServiceURL(lookupServiceURI(request), Standards.DATALINK_LINKS_10, AuthMethod.COOKIE);
          return new URL(dataServiceURL + "?" + request.getQueryString());
    }


    /**
     * Return service URI depending on USEMAQ parameter passed in
     * @param request
     * @return
     */
    private URI lookupServiceURI(final HttpServletRequest request) {

        URI tapServiceURI = DEFAULT_CAOM2LINK_SERVICE_URI;
        String tapServiceKey = CAOM2LINK_SERVICE_URI_PROPERTY_KEY ;
        String useAlt = request.getParameter("useMaq");
        if ((request.getParameter("useMaq") != null)
            && (request.getParameter("useMaq").equals("true")) ) {
            log.info("useMaq passed in as true, polling MAQ for tap data.");
            tapServiceURI = SC2LINK_SERVICE_URI;
            tapServiceKey = SC2LINK_SERVICE_URI_PROPERTY_KEY;
        }
        return getServiceID(tapServiceKey, tapServiceURI);
    }


    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final URL jobURL = this.createJobURL(req);
        final OutputStream outputStream = new BufferedOutputStream(resp.getOutputStream());

        try {
            final HttpDownload download = createDownloader(jobURL, outputStream);

            download.setFollowRedirects(true);
            download.run();

            final int responseCode = download.getResponseCode();

            if (responseCode > 400) {
                resp.setStatus(responseCode);
            }
        } finally {
            outputStream.flush();
        }
    }
}
