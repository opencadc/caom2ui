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

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.ui.server.caom2repo.CaomRepoClient;
import ca.nrc.cadc.caom2.ui.server.caom2repo.ObsLink;
import ca.nrc.cadc.caom2.xml.ObservationParsingException;
import ca.nrc.cadc.caom2.xml.ObservationReader;
import ca.nrc.cadc.config.ApplicationConfiguration;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.net.InputStreamWrapper;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.List;


public class Caom2RepoObservationServlet extends HttpServlet
{
    private final long serialVersionUID = 201708242300L;
    private static Logger log = Logger.getLogger(Caom2RepoObservationServlet.class);
    protected RegistryClient registryClient;
    protected ApplicationConfiguration applicationConfiguration;

    private static final String ERROR_MESSAGE_NOT_FOUND_FORBIDDEN =
            "Observation with URI '%s' not found, or you are "
            + "forbidden from seeing it.  Please login and "
            + "try again. | l'Observation '%s' pas "
            + "trouvé, ou vous n'avez pas permission.  S'il "
            + "vous plaît connecter et essayez à nouveau.";

    static final String CAOM2REPO_SERVICE_URI_PROPERTY_KEY = "org.opencadc.caom2ui.caom2repo-service-id";
    static final String CAOM2REPO_SERVICE_HOST_PORT_PROPERTY_KEY = "org.opencadc.caom2ui.caom2trpo-service-host-port";
    static final URI CAOM2REPO_RESOURCE_ID = URI.create("ivo://cadc.nrc.ca/caom2repo");
    private static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")
                                                       + "/config/org.opencadc.caom2ui.properties";
    private CaomRepoClient repoClient = new CaomRepoClient();

    public Caom2RepoObservationServlet()
    {
        this(new RegistryClient(), new ApplicationConfiguration(PROPERTIES_FILE_PATH));
    }


    Caom2RepoObservationServlet(final RegistryClient registryClient, final ApplicationConfiguration applicationConfiguration)
    {
        this.registryClient = registryClient;
        this.applicationConfiguration = applicationConfiguration;
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
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException
    {
        final long start = System.currentTimeMillis();

        this.registryClient = new RegistryClient();
        this.applicationConfiguration = new ApplicationConfiguration(PROPERTIES_FILE_PATH);

        // Parse number of path elements first
        try
        {
            String requestType = getRequestType(request);

            if (requestType == "list")
            {
                List<String> collections = repoClient.getCollections();
                request.setAttribute("collections", collections);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/collectionslist.jsp");
                dispatcher.forward(request, response);
            } else if (requestType == "collection")
            {
                List<ObsLink> uris = repoClient.getObservations(getCollectionFromRequest(request));
                request.setAttribute("uris", uris);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/obslist.jsp");
                dispatcher.forward(request, response);
            } else if (requestType == "observation")
            {
                final ObservationURI uri = getURI(request);
                // Forward to the Caom2RepoServlet
                final Observation obs =
                        getObservation(getCurrentSubject(), uri);

                request.setAttribute("collection", getCollectionFromRequest(request));
                request.setAttribute("observationID", getObservationIdFromRequest(request));
                request.setAttribute("obs", obs);
                RequestDispatcher disp = request.getRequestDispatcher("/display.jsp");
                disp.forward(request, response);
            }


        } catch (RuntimeException oops)
        {
            Throwable cause = oops.getCause();
            log.error("unexpected exception", oops);
            request.setAttribute("runtimeException", oops);
            request.setAttribute("errorMsg", oops.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        } finally
        {
            log.info("doGet[" + (System.currentTimeMillis() - start) + "ms]");
        }

    }

    private ObservationURI getURI(HttpServletRequest request)
    {
        String sid = request.getPathInfo();
        log.debug("request.getPathInfo(): " + sid);

        if (sid != null)
        {
            sid = sid.substring(1, sid.length()); // strip leading /
            final String[] parts = sid.split("/");

            if (parts.length == 2)
            {
                log.debug("collection: " + parts[0] + " observationID: "
                        + parts[1]);
                return new ObservationURI(parts[0], parts[1]);
            }
        }

        log.debug("collection/observationID not found in path");
        return null;
    }

    private String getRequestType(HttpServletRequest request)
    {
        String sid = request.getPathInfo();
        log.debug("request.getPathInfo(): " + sid);

        if (sid != null)
        {
            sid = sid.substring(1,sid.length()); // strip leading /
            String[] parts = sid.split("/");

            if (parts != null)
            {
                if (parts.length == 1)
                {
                    return "collection";
                }
                else if (parts.length == 2)
                {
                    return "observation";
                }
            }
        }
        else
        {
            return "list";
        }
        log.debug("invalid path");
        return null;
    }

    private String getCollectionFromRequest(HttpServletRequest request)
    {
        String sid = request.getPathInfo();
        log.debug("request.getPathInfo(): " + sid);

        if (sid != null)
        {
            sid = sid.substring(1,sid.length()); // strip leading /
            String[] parts = sid.split("/");

            if (parts != null && parts.length == 1)
            {
                log.debug("collection: " + parts[0]);
                return parts[0];
            }
        }
        log.debug("collection not found in path");
        return null;
    }

    private String getObservationIdFromRequest(HttpServletRequest request)
    {
        String sid = request.getPathInfo();
        log.debug("request.getPathInfo(): " + sid);

        if (sid != null)
        {
            sid = sid.substring(1,sid.length()); // strip leading /
            String[] parts = sid.split("/");

            if (parts != null && parts.length == 2)
            {
                log.debug("observation: " + parts[1]);
                return parts[0];
            }
        }
        log.debug("collection not found in path");
        return null;
    }

    /**
     * Download the Observation for the given URI.
     * @param subject The Subject to download as.
     * @param uri     The Observation URI.
     * @return Observation instance.
     */
    Observation getObservation(final Subject subject, final ObservationURI uri)
    {
        try
        {
            final URL serviceURL = getServiceURL(uri);

            log.debug("GET " + serviceURL);

            final ReadAction ra = getObservationReader();
            final HttpDownload get = getDownloader(serviceURL, ra);

            Subject.doAs(subject, new GetAction(get, uri));

            return ra.getObs();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(
                    "BUG: failed to find cred service in registry");
        }
    }


    /**
     * Place for testers to override.
     *
     * @return ReadAction instance.
     */
    ReadAction getObservationReader()
    {
        return new ReadAction();
    }

    /**
     * Obtain a new instance of a downloader.  Tests can override as needed.
     *
     * @param url        The URL to download from.
     * @param readAction The read action to write to.
     * @return HttpDownload instance.
     */
    HttpDownload getDownloader(final URL url, final ReadAction readAction)
    {
        return new HttpDownload(url, readAction);
    }

    /**
     * Testers or subclasses can override this as needed.
     *
     * @return Subject instance.
     */
    Subject getCurrentSubject()
    {
        return AuthenticationUtil.getCurrentSubject();
    }


    class ReadAction implements InputStreamWrapper
    {
        private Observation obs;


        public void read(final InputStream in) throws IOException
        {
            try
            {
                final ObservationReader r = new ObservationReader(false);
                this.obs = r.read(in);
            }
            catch (ObservationParsingException ex)
            {
                throw new RuntimeException(
                        "Failed to read observation from /caom2repo | "
                                + "Impossible d'obtenir l'observation de /caom2repo.");
            }
        }

        Observation getObs()
        {
            return obs;
        }
    }


    private class GetAction implements PrivilegedAction<Void>
    {
        private final ObservationURI uri;
        private final HttpDownload downloader;


        GetAction(final HttpDownload downloader, final ObservationURI uri)
        {
            this.downloader = downloader;
            this.uri = uri;
        }

        public Void run()
        {
            downloader.run();

            final Throwable e = downloader.getThrowable();

            if (e != null)
            {
                final String message;

                if (e instanceof FileNotFoundException)
                {
                    message = ERROR_MESSAGE_NOT_FOUND_FORBIDDEN;
                }
                else
                {
                    message = "Failed to get observation '%s' from '%s'. "
                            + "| Impossible d'obtenir l'observation '%s' "
                            + "de caom2repo.";
                }

                throw new RuntimeException(
                        String.format(message, uri,
                                downloader.getURL().toExternalForm(),
                                uri));
            }

            return null;
        }
    }

    protected URL getServiceURL(final ObservationURI uri)
            throws IOException
    {

        try
        {
            // Discover caom2repo service URL
            Subject subject = AuthenticationUtil.getCurrentSubject();
            RegistryClient rc = new RegistryClient();

            AuthMethod authMethod = AuthenticationUtil.getAuthMethodFromCredentials(subject);
            if (authMethod == null)
            {
                authMethod = AuthMethod.ANON;
            }

            final URL repoURL = rc.getServiceURL(applicationConfiguration.lookupServiceURI(
                    CAOM2REPO_SERVICE_URI_PROPERTY_KEY, CAOM2REPO_RESOURCE_ID), Standards.CAOM2REPO_OBS_23, authMethod);

            final URIBuilder builder = new URIBuilder(repoURL.toExternalForm() + "/" + uri.getCollection() + "/" + uri.getObservationID());

            final String metaServiceHost = applicationConfiguration.lookup(CAOM2REPO_SERVICE_HOST_PORT_PROPERTY_KEY);

            if (StringUtil.hasText(metaServiceHost))
            {
                final URI metaServiceURI = URI.create(metaServiceHost);

                builder.setHost(metaServiceURI.getHost());
                builder.setPort(metaServiceURI.getPort());
            }


            return builder.build().toURL();
        }
        catch (MalformedURLException me)
        {
            {
                throw new IOException("Can't connect to caom2repo service: " + me.getMessage());
            }
        }
        catch (URISyntaxException me)
        {
            {
                throw new IOException("Can't connect to caom2repo service: " + me.getMessage());
            }
        }

    }

}
