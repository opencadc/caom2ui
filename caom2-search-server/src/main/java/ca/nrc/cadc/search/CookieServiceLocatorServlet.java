/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2021.                            (c) 2021.
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

package ca.nrc.cadc.search;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.web.ConfigurableServlet;
import org.apache.log4j.Logger;
import org.json.JSONWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Locate a service URL from the configured server's Registry.  This only supports looking up HTTP access services
 * with Cookie authentication.
 */
public class CookieServiceLocatorServlet extends ConfigurableServlet {
    private static final Logger LOGGER = Logger.getLogger(CookieServiceLocatorServlet.class);

    static final String SERVICE_ID_REQ_PARAMETER = "serviceID";
    static final String STANDARD_ID_REQ_PARAMETER = "standardID";

    static final String SERVICE_URL_JSON_KEY = "serviceURL";
    static final String ERROR_JSON_KEY = "error";
    static final String ERROR_MESSAGE_JSON_KEY = "message";
    static final String ERROR_PARAMETER_JSON_KEY = "parameter";

    /**
     * Called by the server (via the <code>service</code> method) to
     * allow a servlet to handle a GET request.
     *
     * <p>Overriding this method to support a GET request also
     * automatically supports an HTTP HEAD request. A HEAD
     * request is a GET request that returns no body in the
     * response, only the request header fields.
     *
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or
     * output stream object, and finally, write the response data.
     * It's best to include content type and encoding. When using
     * a <code>PrintWriter</code> object to return the response,
     * set the content type before accessing the
     * <code>PrintWriter</code> object.
     *
     * <p>The servlet container must write the headers before
     * committing the response, because in HTTP the headers must be sent
     * before the response body.
     *
     * <p>Where possible, set the Content-Length header (with the
     * {@link ServletResponse#setContentLength} method),
     * to allow the servlet container to use a persistent connection
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     *
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     *
     * <p>The GET method should be safe, that is, without
     * any side effects for which users are held responsible.
     * For example, most form queries have no side effects.
     * If a client request is intended to change stored data,
     * the request should use some other HTTP method.
     *
     * <p>The GET method should also be idempotent, meaning
     * that it can be safely repeated. Sometimes making a
     * method safe also makes it idempotent. For example,
     * repeating queries is both safe and idempotent, but
     * buying a product online or modifying data is neither
     * safe nor idempotent.
     *
     * <p>If the request is incorrectly formatted, <code>doGet</code>
     * returns an HTTP "Bad Request" message.
     *
     * @param req  an {@link HttpServletRequest} object that
     *             contains the request the client has made
     *             of the servlet
     * @param resp an {@link HttpServletResponse} object that
     *             contains the response the servlet sends
     *             to the client
     * @throws IOException      if an input or output error is
     *                          detected when the servlet handles
     *                          the GET request
     * @throws ServletException if the request for the GET
     *                          could not be handled
     * @see ServletResponse#setContentType
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        LOGGER.debug(getClass().getSimpleName() + ".doGet()");
        final String serviceIDString = req.getParameter(SERVICE_ID_REQ_PARAMETER);
        final String standardIDString = req.getParameter(STANDARD_ID_REQ_PARAMETER);

        LOGGER.debug("setContentType -> json");
        resp.setContentType("application/json");

        URI serviceID = null;
        URI standardID;

        final JSONWriter jsonWriter = new JSONWriter(resp.getWriter());
        jsonWriter.object();

        try {
            if (!StringUtil.hasText(serviceIDString)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonWriter.key(ERROR_JSON_KEY).object();
                jsonWriter.key(ERROR_MESSAGE_JSON_KEY).value("Missing parameter.");
                jsonWriter.key(ERROR_PARAMETER_JSON_KEY).value(SERVICE_ID_REQ_PARAMETER);
                jsonWriter.endObject(); // End error object.
            } else if (!StringUtil.hasText(standardIDString)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonWriter.key(ERROR_JSON_KEY).object();
                jsonWriter.key(ERROR_MESSAGE_JSON_KEY).value("Missing parameter.");
                jsonWriter.key(ERROR_PARAMETER_JSON_KEY).value(STANDARD_ID_REQ_PARAMETER);
                jsonWriter.endObject(); // End error object.
            } else {
                serviceID = new URI(serviceIDString);
                standardID = new URI(standardIDString);
                final URL serviceURL = locateCookieService(serviceID, standardID);

                LOGGER.info("Located service URL for {serviceID=" + serviceID + ",standardID=" + standardID
                            + ",serviceURL=" + serviceURL + "}");

                if (serviceURL == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    jsonWriter.key(ERROR_JSON_KEY).object();
                    jsonWriter.key(ERROR_MESSAGE_JSON_KEY).value("No service URL found for serviceID=" + serviceID
                                                                 + " and standardID=" + standardID + ".");
                    jsonWriter.endObject(); // End error object.
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);

                    jsonWriter.key(SERVICE_URL_JSON_KEY).value(serviceURL);
                }
            }
        } catch (URISyntaxException uriSyntaxException) {
            // If the provided IDs aren't proper URIs.
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonWriter.key(ERROR_JSON_KEY).object();
            jsonWriter.key(ERROR_MESSAGE_JSON_KEY).value(uriSyntaxException.getMessage());
            jsonWriter.key(ERROR_PARAMETER_JSON_KEY).value(serviceID == null ? SERVICE_ID_REQ_PARAMETER
                                                                             : STANDARD_ID_REQ_PARAMETER);
            jsonWriter.endObject(); // End error object.
        } finally {
            jsonWriter.endObject();
        }
        LOGGER.debug(getClass().getSimpleName() + ".doGet(): OK");
    }

    /**
     * Obtain the Service URL for the given service URI.
     * @param serviceID     The Service ID.
     * @param standardID     The Standard ID.
     * @return  URL of the service.
     */
    URL locateCookieService(final URI serviceID, final URI standardID) {
        final RegistryClient registryClient = new RegistryClient();
        return registryClient.getServiceURL(serviceID, standardID, AuthMethod.COOKIE, Standards.INTERFACE_PARAM_HTTP);
    }
}
