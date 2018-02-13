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
import ca.nrc.cadc.caom2.PublisherID;
import ca.nrc.cadc.caom2.ui.server.client.Caom2MetaClient;
import ca.nrc.cadc.caom2.ui.server.client.ObservationUtil;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author jeevesh
 */
public class Caom2MetaObservationServlet extends HttpServlet {
    private static final long serialVersionUID = -917406909288899339L;
    private static Logger log = Logger.getLogger(Caom2MetaObservationServlet.class);

    private static final String ERROR_MESSAGE_NOT_FOUND_FORBIDDEN =
        "Observation with Observation URI '%s' not found, or you are "
            + "forbidden from seeing it.  Please login and "
            + "try again. | l'Observation avec le URI '%s' pas "
            + "trouvé, ou vous n'avez pas permission.  S'il "
            + "vous plaît connecter et essayez à nouveau.";


    private final Caom2MetaClient metaClient;


    public Caom2MetaObservationServlet() {
        this(new Caom2MetaClient());
    }


    public Caom2MetaObservationServlet(Caom2MetaClient metaClient) {
        this.metaClient = metaClient;
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
        final String errMsg;

        try {
            final ObservationURI observationURI = ObservationUtil.getURI(request);

            // Parse the parameters given in the url.
            final PublisherID publisherID = ObservationUtil.getPublisherID(request);
            if ((publisherID == null) || (observationURI == null)) {
                errMsg = "Must specify observationID/productID in the path, and the publisherID in the query. | "
                    + "Le chemain manque le observationID/productID dans le chemin, ou le publisherID dans le "
                    + "query.";
                log.error(errMsg);
                request.setAttribute("errorMsg", errMsg);
                forward(request, response, "/error.jsp");
            } else {
//                request.setAttribute("collection", publisherID.getCollection());
//                request.setAttribute("observationID", publisherID.getObservationID());

                final Observation obs = metaClient.getObservation(metaClient.getCurrentSubject(), publisherID,
                                                                  observationURI);

                if (obs == null) {
                    errMsg = String.format(ERROR_MESSAGE_NOT_FOUND_FORBIDDEN, publisherID.getURI().toString(),
                                           publisherID.getURI().toString());
                    log.error(errMsg);
                    request.setAttribute("errorMsg", errMsg);
                    forward(request, response, "/error.jsp");
                } else {
                    request.setAttribute("obs", obs);
                    forward(request, response, "/display.jsp");
                }
            }
        } catch (RuntimeException oops) {
            log.error("unexpected exception", oops);
            request.setAttribute("errorMsg", oops.getMessage());
            forward(request, response, "/error.jsp");
        } finally {
            log.info("doGet[" + (System.currentTimeMillis() - start) + "ms]");
        }
    }

    private void forward(final HttpServletRequest request,
                         final HttpServletResponse response, final String path)
        throws ServletException, IOException {
        final RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }
}
