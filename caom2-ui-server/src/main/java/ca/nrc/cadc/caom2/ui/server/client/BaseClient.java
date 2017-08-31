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

package ca.nrc.cadc.caom2.ui.server.client;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.xml.ObservationParsingException;
import ca.nrc.cadc.caom2.xml.ObservationReader;
import ca.nrc.cadc.config.ApplicationConfiguration;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.net.InputStreamWrapper;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author hjeeves
 */
public class BaseClient
{
    private static final Logger log = Logger.getLogger(BaseClient.class);
    protected URI resourceId;
    protected URI standardsURI;
    protected String path;
    protected String propertyKey;

    private static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")
            + "/config/org.opencadc.caom2ui.properties";

    private static final String ERROR_MESSAGE_NOT_FOUND_FORBIDDEN =
            "Observation with URI '%s' not found, or you are "
                    + "forbidden from seeing it.  Please login and "
                    + "try again. | l'Observation '%s' pas "
                    + "trouvé, ou vous n'avez pas permission.  S'il "
                    + "vous plaît connecter et essayez à nouveau.";


    public BaseClient() {}

    /**
     * Testers or subclasses can override this as needed.
     *
     * @return Subject instance.
     */
    public Subject getCurrentSubject()
    {
        return AuthenticationUtil.getCurrentSubject();
    }

    public URL getServiceURL()
    {
        try
        {
            // Discover client service URL
            Subject subject = getCurrentSubject();
            RegistryClient rc = new RegistryClient();
            ApplicationConfiguration ac = new ApplicationConfiguration(PROPERTIES_FILE_PATH);

            AuthMethod authMethod = AuthenticationUtil.getAuthMethodFromCredentials(subject);
            if (authMethod == null)
            {
                authMethod = AuthMethod.ANON;
            }


            final URL repoURL = rc.getServiceURL(ac.lookupServiceURI(propertyKey, resourceId), standardsURI, authMethod);

            final URIBuilder builder = new URIBuilder(repoURL.toExternalForm() + path);

            final String metaServiceHost = ac.lookup(propertyKey);

            if (StringUtil.hasText(metaServiceHost))
            {
                final URI metaServiceURI = URI.create(metaServiceHost);

                builder.setHost(metaServiceURI.getHost());
                builder.setPort(metaServiceURI.getPort());
            }

            return builder.build().toURL();
        }
        catch (URISyntaxException | MalformedURLException urie)
        {
            String errMsg = "Can't get service URL for resource " + resourceId.toString() + " " + standardsURI.toString();
            log.error(errMsg);
            throw new RuntimeException(errMsg, urie);
        }

    }


    /**
     * Place for testers to override.
     *
     * @return ReadAction instance.
     */
    public BaseClient.ReadAction getObservationReader()
    {
        return new BaseClient.ReadAction();
    }

    /**
     * Obtain a new instance of a downloader.  Tests can override as needed.
     *
     * @param url        The URL to download from.
     * @param readAction The read action to write to.
     * @return HttpDownload instance.
     */
    public HttpDownload getDownloader(final URL url, final BaseClient.ReadAction readAction)
    {
        return new HttpDownload(url, readAction);
    }


    public class ReadAction implements InputStreamWrapper
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
                        "Failed to read observation from /client | "
                                + "Impossible d'obtenir l'observation de /client.");
            }
        }

        public Observation getObs()
        {
            return obs;
        }
    }


    protected class GetAction implements PrivilegedAction<Void>
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
                            + "de client.";
                }

                throw new RuntimeException(
                        String.format(message, uri,
                                downloader.getURL().toExternalForm(),
                                uri));
            }

            return null;
        }
    }

}
