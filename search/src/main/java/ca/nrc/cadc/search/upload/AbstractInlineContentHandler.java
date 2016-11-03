/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
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

package ca.nrc.cadc.search.upload;

import ca.nrc.cadc.auth.SSLUtil;
import ca.nrc.cadc.net.OutputStreamWrapper;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.TargetNameResolverClientImpl;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.JobInfo;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.web.InlineContentException;
import ca.nrc.cadc.uws.web.InlineContentHandler;

import javax.security.auth.Subject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractInlineContentHandler
        implements InlineContentHandler
{
    private final Map<String, URL> uploadCache = new HashMap<>();
    private final List<Parameter> parameterList = new ArrayList<>();
    private final TAPUploadFilenameGenerator filenameGenerator;


    public AbstractInlineContentHandler(
            final TAPUploadFilenameGenerator filenameGenerator)
    {
        this.filenameGenerator = filenameGenerator;
    }

    /**
     * Obtain a new instance of the VOTableUploader.  Useful for overriding in
     * testing.
     *
     * @param registryClient The RegistryClient for service lookups.
     * @return VOTableUploader implementation instance.
     */
    protected abstract VOTableUploader createVOTableUploader(
            final RegistryClient registryClient);

    RegistryClient createRegistryClient()
    {
        return new RegistryClient();
    }


    @Override
    public void setParameterList(final List<Parameter> parameterList)
    {
        final List<Parameter> nullSafeList = new ArrayList<>();

        if (parameterList != null)
        {
            nullSafeList.addAll(parameterList);
        }

        this.parameterList.clear();
        this.parameterList.addAll(nullSafeList);
    }

    /**
     * @return List of Parameters.
     */
    @Override
    public List<Parameter> getParameterList()
    {
        sanitizeParameters();
        return this.parameterList;
    }

    /**
     * @return JobInfo.
     */
    @Override
    public JobInfo getJobInfo()
    {
        return null;
    }

    @Override
    public URL accept(final String name, final String contentType,
                      final InputStream inputStream)
            throws InlineContentException, IOException
    {
        if (inputStream == null)
        {
            throw new IOException("InputStream cannot be null");
        }

        // Parse out the selected resolver name from the name.
        final String resolver;
        final String[] tokens = name.split("\\.");

        if (tokens.length == 2)
        {
            resolver = tokens[1];
        }
        else
        {
            resolver = null;
        }

        // Upload the file to the data webservice.
        final RegistryClient registryClient = createRegistryClient();
        final UploadResults uploadResults = new UploadResults(resolver, 0, 0);
        final URL retURL = upload(inputStream, uploadResults, registryClient);

        addJobParameter(UploadResults.UPLOAD_RESOLVER, resolver);
        addJobParameter(UploadResults.UPLOAD_ROW_COUNT,
                        String.valueOf(uploadResults.getRowCount()));
        addJobParameter(UploadResults.UPLOAD_ERROR_COUNT,
                        String.valueOf(uploadResults.getErrorCount()));

        // Add the parameter name and url to the cache map.
        addUploadCache(retURL);

        return retURL;
    }

    private void addUploadCache(final URL url)
    {
        uploadCache.put("targetList", url);
    }

    private void addJobParameter(final String paramName,
                                 final String paramValue)
    {
        this.parameterList.add(new Parameter(paramName, paramValue));
    }

    /**
     * Perform the Upload.
     *
     * @param inputStream    The InputStream of the file to PUT.
     * @param uploadResults  The UploadResults to write to.
     * @param registryClient The registry clietn to use for lookups.
     * @return The URL to obtain the results.
     * @throws IOException If the return URL cannot be obtained.
     */
    private URL upload(final InputStream inputStream,
                       final UploadResults uploadResults,
                       final RegistryClient registryClient) throws IOException
    {
        final VOTableUploader voTableUploader =
                createVOTableUploader(registryClient);
        final OutputStreamWrapper stream =
                new VOTableOutputStream(inputStream, uploadResults,
                                        new TargetNameResolverClientImpl());
        return secureUpload(voTableUploader, stream);
    }

    protected URL secureUpload(final VOTableUploader voTableUploader,
                               final OutputStreamWrapper streamWrapper)
            throws IOException
    {
        final Subject uploadAuth = createInternalUploadAuth();

        try
        {
            return Subject.doAs(uploadAuth, new PrivilegedExceptionAction<URL>()
            {
                @Override
                public URL run() throws Exception
                {
                    return voTableUploader.upload(streamWrapper,
                                                  filenameGenerator.generate());
                }
            });
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    private Subject createInternalUploadAuth()
    {
        return SSLUtil.createSubject(new File(System.getProperty("user.home")
                                              + "/.ssl/cadcproxy.pem"));
    }

    /**
     * Root through the parameters, and sanitize the Upload one.
     */
    private void sanitizeParameters()
    {
        for (final Parameter p : this.parameterList)
        {
            sanitizeUploadParameter(p);
        }
    }

    /**
     * Ensure the UPLOAD value is parsed out correctly in the parameter.
     *
     * @param jobParameter The Parameter to sanitize.
     */
    private void sanitizeUploadParameter(final Parameter jobParameter)
    {
        final String upload = jobParameter.getValue();

        if (StringUtil.hasText(upload)
            && jobParameter.getName().equals("UPLOAD"))
        {
            final StringBuilder sb = new StringBuilder();
            final String[] tables = upload.split(";");

            for (final String table : tables)
            {
                if (table.isEmpty())
                {
                    sb.append(";");
                    continue;
                }

                final String[] nameURI = table.split(",");

                if (nameURI.length == 1)
                {
                    sb.append(table);
                    sb.append(";");
                    continue;
                }

                final String[] paramURI = nameURI[1].split(":");

                if ((paramURI.length == 2)
                    && uploadCache.containsKey(paramURI[1]))
                {
                    sb.append(nameURI[0]);
                    sb.append(",");

                    final URL url = uploadCache.get(paramURI[1]);
                    sb.append(url.toString());
                    sb.append(";");
                }
                else
                {
                    sb.append(table);
                    sb.append(";");
                }
            }

            if (sb.length() > 0)
            {
                if (sb.charAt(sb.length() - 1) == ';')
                {
                    sb.deleteCharAt(sb.length() - 1);
                }

                jobParameter.setValue(sb.toString());
            }
        }
    }
}
