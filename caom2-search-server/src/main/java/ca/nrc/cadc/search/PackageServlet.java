/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2015.                            (c) 2015.
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
import ca.nrc.cadc.caom2.PublisherID;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.web.ConfigurableServlet;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Servlet to redirect a caller to the appropriate place for a single request
 * download of a single CAOM-2 URI.
 */
public class PackageServlet extends ConfigurableServlet {
    /**
     * Only supported method.  This will accept an ID parameter in the request
     * to query on.
     *
     * @param request  The HTTP Request.
     * @param response The HTTP Response.
     * @throws IOException Any other errors.
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        get(request, response, new RegistryClient());
    }


    /**
     * Handle a GET request with the given Registry client to perform the lookup.
     *
     * @param request        The HTTP Request.
     * @param response       The HTTP Response.
     * @param registryClient The RegistryClient to do lookups.
     * @throws IOException        Any request access problems.
     */
    void get(final HttpServletRequest request, final HttpServletResponse response, final RegistryClient registryClient)
        throws IOException {

        // TODO: prior to version 2.5.0, this servlet supported multiple IDs.
        // TODO: Consider how this might be supported in future.
        final String[] idValues = request.getParameterValues("ID");
        if (idValues.length > 1) {
            throw new UnsupportedOperationException("Multiple IDs in package lookup.");
        } else {
            final String IDValue = idValues[0];
            if (IDValue.length() > 0) {
                try {
                    final PublisherID publisherID = new PublisherID(URI.create(IDValue));

                    final URL serviceURL = registryClient.getServiceURL(
                        publisherID.getResourceID(), Standards.PKG_10, AuthMethod.COOKIE);

                    final URIBuilder builder = new URIBuilder(serviceURL.toURI());
                    builder.addParameter("ID", IDValue);

                    response.sendRedirect(builder.build().toURL().toExternalForm());
                } catch (URISyntaxException e) {
                    throw new IOException(String.format("Service URL from %s is invalid.", IDValue), e);
                }
            } else {
                throw new UnsupportedOperationException("Invalid ID in package lookup.");
            }
        }
    }
}
