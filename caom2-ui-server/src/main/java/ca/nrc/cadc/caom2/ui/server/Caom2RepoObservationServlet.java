/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
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
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.caom2.ui.server;


import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.ui.server.client.Caom2RepoClient;
import ca.nrc.cadc.caom2.ui.server.client.ObsLink;
import ca.nrc.cadc.caom2.ui.server.client.ObservationUtil;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class Caom2RepoObservationServlet extends HttpServlet {
    private static final long serialVersionUID = 201708242300L;
    private static final Logger LOGGER = Logger.getLogger(Caom2RepoObservationServlet.class);

    private static final String ERROR_MESSAGE_NOT_FOUND_FORBIDDEN =
        "Observation with URI '%s' not found, or you are "
            + "forbidden from seeing it.  Please login and "
            + "try again. | l'Observation '%s' pas "
            + "trouvé, ou vous n'avez pas permission.  S'il "
            + "vous plaît connecter et essayez à nouveau.";


    private static final int COLLECTION_LIST = 1;
    private static final int OBSERVATION_LIST = 2;
    private static final int OBSERVATION_VIEW = 3;
    private static final int NOT_SUPPORTED = 0;

    private final Caom2RepoClient repoClient;

    public Caom2RepoObservationServlet() {
        this(new Caom2RepoClient());
    }

    public Caom2RepoObservationServlet(Caom2RepoClient repoClient) {
        this.repoClient = repoClient;
    }

    /**
     * Gets a complete Observation from a database and renders it in HTML or XML.
     * The HTML display is handled by forwarding to observation.jsp for rendering.
     * TODO: XML rendering.
     *
     * @param request  The servlet request.
     * @param response The servlet response.
     * @throws ServletException If servlet exception.
     * @throws IOException      If IO exception.
     */
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        final long start = System.currentTimeMillis();

        try {
            int requestType = getRequestType(request);

            switch (requestType) {
                case COLLECTION_LIST: {
                    List<String> collections = repoClient.getCollections();
                    request.setAttribute("collections", collections);
                    forward(request, response, "/collectionslist.jsp");
                    break;
                }
                case OBSERVATION_LIST: {
                    List<ObsLink> uris = repoClient.getObservations(getCollectionFromRequest(request));
                    request.setAttribute("uris", uris);
                    forward(request, response, "/obslist.jsp");
                    break;
                }
                case OBSERVATION_VIEW: {
                    final ObservationURI uri = ObservationUtil.extractObservationURIFromPath(request);
                    final Observation obs;
                    if (uri == null) {
                        obs = null;
                    } else {
                        obs = repoClient.getObservation(repoClient.getCurrentSubject(), uri);
                    }

                    if (obs == null) {
                        String errMsg = String.format(ERROR_MESSAGE_NOT_FOUND_FORBIDDEN, uri, uri);
                        LOGGER.error(errMsg);
                        request.setAttribute("errorMsg", errMsg);
                        forward(request, response, "/error.jsp");
                    } else {
                        request.setAttribute("collection", uri.getCollection());
                        request.setAttribute("observationID", uri.getObservationID());
                        request.setAttribute("obs", obs);
                        forward(request, response, "/display.jsp");
                    }

                    break;
                }
                case NOT_SUPPORTED:
                default: {
                    request.setAttribute("errorMsg", "Request type not supported");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                    dispatcher.forward(request, response);
                    break;
                }
            }
        } catch (RuntimeException oops) {
            LOGGER.error("unexpected runtime exception", oops);
            request.setAttribute("runtimeException", oops);
            request.setAttribute("errorMsg", oops.getMessage());
            forward(request, response, "/error.jsp");
        } finally {
            LOGGER.info("doGet[" + (System.currentTimeMillis() - start) + "ms]");
        }

    }

    private int getRequestType(HttpServletRequest request) {
        String sid = request.getPathInfo();
        final int requestType;

        if (sid == null) {
            requestType = COLLECTION_LIST;
        } else {
            sid = sid.substring(1); // strip leading /
            String[] parts = sid.split("/");

            if (parts.length == 1) {
                if (parts[0].isEmpty()) {
                    requestType = COLLECTION_LIST;
                } else {
                    requestType = OBSERVATION_LIST;
                }
            } else if (parts.length == 2) {
                requestType = OBSERVATION_VIEW;
            } else {
                requestType = NOT_SUPPORTED;
            }
        }

        LOGGER.error("Invalid path : " + sid);
        return requestType;
    }

    private String getCollectionFromRequest(HttpServletRequest request) {
        String sid = request.getPathInfo();

        if (sid != null) {
            sid = sid.substring(1); // strip leading /
            final String[] parts = sid.split("/");

            if (parts.length == 1) {
                return parts[0];
            }
        }

        String errMsg = "Collection name not found: " + sid;
        LOGGER.error(errMsg);
        throw new RuntimeException(errMsg);
    }


    private void forward(final HttpServletRequest request, final HttpServletResponse response, final String path)
        throws ServletException, IOException {
        final RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }

}
