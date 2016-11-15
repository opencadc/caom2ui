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

package ca.nrc.cadc.web;

import ca.nrc.cadc.ApplicationConfiguration;
import ca.nrc.cadc.auth.ACIdentityManager;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.caom2.CAOMQueryGeneratorImpl;
import ca.nrc.cadc.caom2.ObsCoreQueryGeneratorImpl;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.search.TargetNameResolverClientImpl;
import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.search.upload.StreamingVOTableWriter;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.tap.impl.SyncTAPClientImpl;
import ca.nrc.cadc.tap.impl.TAPSearcherImpl;
import ca.nrc.cadc.uws.*;
import ca.nrc.cadc.uws.server.*;
import ca.nrc.cadc.uws.server.impl.PostgresJobPersistence;
import ca.nrc.cadc.uws.web.JobCreator;
import org.apache.commons.fileupload.FileUploadException;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.PrivilegedExceptionAction;
import java.util.*;


public class SearchJobServlet extends SyncServlet
{
    private JobManager jobManager;
    private ApplicationConfiguration applicationConfiguration;


    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);
        jobManager = createJobManager(config);
        applicationConfiguration = new ApplicationConfiguration();
    }

    /**
     * Called by the server (via the <code>service</code> method)
     * to allow a servlet to handle a POST request.
     * <p>
     * The HTTP POST method allows the client to send
     * data of unlimited length to the Web server a single time
     * and is useful when posting information such as
     * credit card numbers.
     * <p>
     * <p>When overriding this method, read the request data,
     * write the response headers, get the response's writer or output
     * stream object, and finally, write the response data. It's best
     * to include content type and encoding. When using a
     * <code>PrintWriter</code> object to return the response, set the
     * content type before accessing the <code>PrintWriter</code> object.
     * <p>
     * <p>The servlet container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the
     * response body.
     * <p>
     * <p>Where possible, set the Content-Length header (with the
     * {@link ServletResponse#setContentLength} method),
     * to allow the servlet container to use a persistent connection
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     * <p>
     * <p>When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     * <p>
     * <p>This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example,
     * updating stored data or buying items online.
     * <p>
     * <p>If the HTTP POST request is incorrectly formatted,
     * <code>doPost</code> returns an HTTP "Bad Request" message.
     *
     * @param request  an {@link HttpServletRequest} object that
     *                 contains the request the client has made
     *                 of the servlet
     * @param response an {@link HttpServletResponse} object that
     *                 contains the response the servlet sends
     *                 to the client
     * @throws IOException      if an input or output error is
     *                          detected when the servlet handles
     *                          the request
     * @throws ServletException if the request for the POST
     *                          could not be handled
     * @see ServletOutputStream
     * @see ServletResponse#setContentType
     */
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException
    {
        try
        {
            final Subject subject = AuthenticationUtil.getSubject(request);

            if (subject == null)
            {
                processRequest(request, response);
            }
            else
            {
                Subject.doAs(subject, new PrivilegedExceptionAction<Object>()
                {
                    @Override
                    public Object run() throws Exception
                    {
                        processRequest(request, response);
                        return null;
                    }
                });
            }
        }
        catch (TransientException ex)
        {
            // OutputStream not open, write an error response
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.addHeader("Retry-After",
                               Integer.toString(ex.getRetryDelay()));
            response.setContentType("text/plain");
            PrintWriter w = response.getWriter();
            w.println("failed to get or persist job state.");
            w.println("   reason: " + ex.getMessage());
            w.close();
        }
        catch (JobPersistenceException ex)
        {
            // OutputStream not open, write an error response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            PrintWriter w = response.getWriter();
            w.println("failed to get or persist job state.");
            w.println("   reason: " + ex.getMessage());
            w.close();
        }
        catch (Throwable t)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            PrintWriter w = response.getWriter();
            w.println("Unable to proceed with job execution.\n");
            w.println("Reason: " + t.getMessage());
            w.close();
        }
    }

    /**
     * Obtain the current date in UTC.
     *
     * @return      The current Date in UTC.
     */
    private Date currentDateUTC()
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        return calendar.getTime();
    }

    private void processRequest(final HttpServletRequest request,
                                final HttpServletResponse response)
            throws JobPersistenceException, TransientException,
                   FileUploadException, IOException, PositionParserException,
                   JobNotFoundException
    {
        final Map<String, Object> uploadPayload = new HashMap<>();
        final List<Parameter> extraJobParameters = new ArrayList<>();

        final JobUpdater jobUpdater =
                new PostgresJobPersistence(new ACIdentityManager());

        final JobCreator jobCreator = new JobCreator(getInlineContentHandler())
        {
            @Override
            protected void processStream(final String name,
                                         final String contentType,
                                         final InputStream inputStream)
                    throws IOException
            {
                try
                {
                    final String[] nameParts = name.split("\\.");
                    final String paramName = nameParts[0];
                    final File uploadFile = new File(paramName);
                    final FileOutputStream fos =
                            new FileOutputStream(uploadFile);
                    final String resolver = (nameParts.length == 2)
                                            ? nameParts[1] : "ALL";
                    final UploadResults uploadResults =
                            new UploadResults(resolver, 0, 0);
                    final StreamingVOTableWriter tableWriter =
                            new StreamingVOTableWriter(uploadResults,
                                                       new TargetNameResolverClientImpl());

                    tableWriter.write(inputStream, fos);
                    extraJobParameters.add(
                            new Parameter(UploadResults.UPLOAD_RESOLVER,
                                          resolver));

                    fos.flush();
                    fos.close();

                    if (uploadFile.length() > 0)
                    {
                        uploadPayload.put(paramName, uploadFile);
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        // Create the audit job.
        final Job auditJob = jobManager.create(jobCreator.create(request));
        auditJob.getParameterList().addAll(extraJobParameters);

        final SyncOutput syncOutput = new HTTPResponseSyncOutput(response);
        final SyncTAPClient tapClient =
                new SyncTAPClientImpl(false, new RegistryClient())
                {
                    /**
                     * Build the payload to POST.
                     *
                     * @param job       The Job to get the payload for.
                     * @return Map of Parameter name -> value.
                     */
                    @Override
                    protected Map<String, Object> getQueryPayload(Job job)
                    {
                        final Map<String, Object> queryPayload =
                                super.getQueryPayload(job);
                        queryPayload.putAll(uploadPayload);

                        return queryPayload;
                    }
                };

        jobUpdater.setPhase(auditJob.getID(), ExecutionPhase.PENDING,
                            ExecutionPhase.QUEUED, currentDateUTC());

        // Create the TAP job to prepare to be executed.
        final JobRunner runner =
                new AdvancedRunner(auditJob, jobUpdater, syncOutput,
                                   new TAPSearcherImpl(
                                           new SyncResponseWriterImpl(
                                                   syncOutput),
                                           jobUpdater, tapClient,
                                           getQueryGenerator(auditJob)),
                                   applicationConfiguration.lookupServiceURI(
                                           ApplicationConfiguration.TAP_SERVICE_URI_PROPERTY_KEY,
                                           ApplicationConfiguration.DEFAULT_TAP_SERVICE_URI));

        runner.run();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Obtain the appropriate query generator.
     *
     * @return QueryGenerator instance.
     */
    private QueryGenerator getQueryGenerator(final Job job)
    {
        // Look for parameters starting with obscore to determine if
        // querying CAOM2 or ObsCore.
        for (final Parameter parameter : job.getParameterList())
        {
            if (ObsModel.isObsCore(parameter.getName()))
            {
                return new ObsCoreQueryGeneratorImpl(job);
            }
        }

        return new CAOMQueryGeneratorImpl(job);
    }
}
