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

import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.auth.HTTPIdentityManager;
import ca.nrc.cadc.auth.IdentityManager;
import ca.nrc.cadc.caom2.CAOMQueryGenerator;
import ca.nrc.cadc.caom2.ObsCoreQueryGenerator;
import ca.nrc.cadc.config.ApplicationConfiguration;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.profiler.Profiler;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.DefaultNameResolverClient;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.search.upload.StreamingVOTableWriter;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.DefaultSyncTAPClient;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.tap.TAPSearcher;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.AdvancedRunner;
import ca.nrc.cadc.uws.ExecutionPhase;
import ca.nrc.cadc.uws.HTTPResponseSyncOutput;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.SyncResponseWriterImpl;
import ca.nrc.cadc.uws.server.DatabaseJobPersistence;
import ca.nrc.cadc.uws.server.JobManager;
import ca.nrc.cadc.uws.server.JobNotFoundException;
import ca.nrc.cadc.uws.server.JobPersistenceException;
import ca.nrc.cadc.uws.server.JobRunner;
import ca.nrc.cadc.uws.server.JobUpdater;
import ca.nrc.cadc.uws.server.SyncOutput;
import ca.nrc.cadc.uws.server.SyncServlet;
import ca.nrc.cadc.uws.server.impl.PostgresJobPersistence;
import ca.nrc.cadc.uws.web.JobCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;


/**
 * Main servlet to handle an actual search request from the Search Form.
 */
public class SearchJobServlet extends SyncServlet {
    private static final String TAP_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.tap-service-id";
    private static final String ALT_TAP_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.maq-tap-service-id";
    private static final URI DEFAULT_TAP_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/tap");
    private static final URI ALTERNATE_TAP_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/sc2tap");

    private JobManager jobManager;
    private JobUpdater jobUpdater;
    private ApplicationConfiguration applicationConfiguration;


    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        final DatabaseJobPersistence jobPersistence = createJobPersistence(new HTTPIdentityManager());

        jobManager = createJobManager(config);
        jobManager.setJobPersistence(jobPersistence);

        jobUpdater = jobPersistence;

        applicationConfiguration = new ApplicationConfiguration(Configuration.DEFAULT_CONFIG_FILE_PATH);
    }

    /**
     * Override as needed.
     *
     * @param identityManager The Identity Manager to pass to the persistence.
     * @return DatabasePersistence instance.
     */
    DatabaseJobPersistence createJobPersistence(final IdentityManager identityManager) {
        return new PostgresJobPersistence(identityManager);
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
     * When overriding this method, read the request data,
     * write the response headers, get the response's writer or output
     * stream object, and finally, write the response data. It's best
     * to include content type and encoding. When using a
     * <code>PrintWriter</code> object to return the response, set the
     * content type before accessing the <code>PrintWriter</code> object.
     * <p>
     * The servlet container must write the headers before committing the
     * response, because in HTTP the headers must be sent before the
     * response body.
     * <p>
     * Where possible, set the Content-Length header (with the
     * {@link ServletResponse#setContentLength} method),
     * to allow the servlet container to use a persistent connection
     * to return its response to the client, improving performance.
     * The content length is automatically set if the entire response fits
     * inside the response buffer.
     * <p>
     * When using HTTP 1.1 chunked encoding (which means that the response
     * has a Transfer-Encoding header), do not set the Content-Length header.
     * <p>
     * This method does not need to be either safe or idempotent.
     * Operations requested through POST can have side effects for
     * which the user can be held accountable, for example,
     * updating stored data or buying items online.
     * <p>
     * If the HTTP POST request is incorrectly formatted,
     * <code>doPost</code> returns an HTTP "Bad Request" message.
     *
     * @param request  an {@link HttpServletRequest} object that
     *                 contains the request the client has made
     *                 of the servlet
     * @param response an {@link HttpServletResponse} object that
     *                 contains the response the servlet sends
     *                 to the client
     * @throws IOException if an input or output error is
     *                     detected when the servlet handles
     *                     the request
     * @see ServletOutputStream
     * @see ServletResponse#setContentType
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            processRequest(request, response);
        } catch (TransientException ex) {
            // OutputStream not open, write an error response
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.addHeader("Retry-After", Integer.toString(ex.getRetryDelay()));
            response.setContentType("text/plain");

            final PrintWriter w = response.getWriter();
            w.println("failed to get or persist job state.");
            w.println("   reason: " + ex.getMessage());
            w.close();
        } catch (JobPersistenceException ex) {
            // OutputStream not open, write an error response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            PrintWriter w = response.getWriter();
            w.println("failed to get or persist job state.");
            w.println("   reason: " + ex.getMessage());
            w.close();

            throw new RuntimeException(ex);
        } catch (Throwable t) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            PrintWriter w = response.getWriter();
            w.println("Unable to proceed with job execution.\n");
            w.println("Reason: " + t.getMessage());
            w.close();

            throw new RuntimeException(t);
        }
    }

    /**
     * Obtain the current date in UTC.
     *
     * @return The current Date in UTC.
     */
    private Date currentDateUTC() {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        return calendar.getTime();
    }

    private void processRequest(final HttpServletRequest request, final HttpServletResponse response)
        throws JobPersistenceException, TransientException, FileUploadException, IOException,
        JobNotFoundException {

        final Set<String> userIDs = AuthenticationUtil.getUseridsFromSubject();
        final String userIDCheckpoint = userIDs.isEmpty() ? "Anonymous" : userIDs.toString();
        final String checkpointID = userIDCheckpoint + "/" + request.getRequestURI();
        final Profiler profiler = new Profiler(SearchJobServlet.class);
        final Map<String, Object> uploadPayload = new HashMap<>();
        final List<Parameter> extraJobParameters = new ArrayList<>();

        final JobCreator jobCreator = new JobCreator(getInlineContentHandler()) {
            @Override
            protected void processStream(final String name, final String contentType, final InputStream inputStream) {
                try {
                    final String[] nameParts = name.split("\\.");
                    final String paramName = nameParts[0];
                    final File uploadFile = new File(paramName);
                    final FileOutputStream fos = new FileOutputStream(uploadFile);
                    final String resolver = (nameParts.length == 2) ? nameParts[1] : "ALL";
                    final UploadResults uploadResults = new UploadResults(resolver, 0, 0);
                    final StreamingVOTableWriter tableWriter =
                        new StreamingVOTableWriter(uploadResults, new DefaultNameResolverClient());

                    tableWriter.write(inputStream, fos);
                    extraJobParameters.add(new Parameter(UploadResults.UPLOAD_RESOLVER, resolver));

                    fos.flush();
                    fos.close();

                    if (uploadFile.length() > 0) {
                        uploadPayload.put(paramName, uploadFile);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        // Create the audit job.
        final Job auditJob = jobManager.create(jobCreator.create(request));
        auditJob.getParameterList().addAll(extraJobParameters);

        profiler.checkpoint(String.format("%s processRequest() Create Audit Job", checkpointID));

        final SyncOutput syncOutput = new HTTPResponseSyncOutput(response);
        final SyncTAPClient tapClient =
            new DefaultSyncTAPClient(false, new RegistryClient()) {
                /**
                 * Build the payload to POST.
                 *
                 * @param job       The Job to get the payload for.
                 * @return Map of Parameter name -> value.
                 */
                @Override
                protected Map<String, Object> getQueryPayload(Job job) {
                    final Map<String, Object> queryPayload = super.getQueryPayload(job);
                    queryPayload.putAll(uploadPayload);

                    return queryPayload;
                }
            };

        jobUpdater.setPhase(auditJob.getID(), ExecutionPhase.PENDING, ExecutionPhase.QUEUED, currentDateUTC());

        // Create the TAP job to prepare to be executed.
        // Check to see if this should return MAQ data

        final URI tapServiceURI;
        final String tapServiceKey;
        final String maqActivatedParam = request.getParameter("activateMAQ");

        if (StringUtil.hasText(maqActivatedParam) && (maqActivatedParam.equals("on") || maqActivatedParam.equals
            ("true"))) {
            tapServiceURI = ALTERNATE_TAP_SERVICE_URI;
            tapServiceKey = ALT_TAP_SERVICE_URI_PROPERTY_KEY;
        } else {
            tapServiceURI = DEFAULT_TAP_SERVICE_URI;
            tapServiceKey = TAP_SERVICE_URI_PROPERTY_KEY;
        }

        final JobRunner runner =
            new AdvancedRunner(auditJob, jobUpdater, syncOutput,
                               new TAPSearcher(new SyncResponseWriterImpl(syncOutput), jobUpdater, tapClient,
                                               getQueryGenerator(auditJob)),
                               applicationConfiguration.lookupServiceURI(tapServiceKey, tapServiceURI));

        runner.run();
        response.setStatus(HttpServletResponse.SC_OK);
        profiler.checkpoint(String.format("%s processRequest()", checkpointID));
    }

    /**
     * Obtain the appropriate query generator.
     *
     * @return QueryGenerator instance.
     */
    private QueryGenerator getQueryGenerator(final Job job) {
        // Look for parameters starting with obscore to determine if
        // querying CAOM2 or ObsCore.
        for (final Parameter parameter : job.getParameterList()) {
            if (ObsModel.isObsCore(parameter.getName())) {
                return new ObsCoreQueryGenerator(job);
            }
        }

        return new CAOMQueryGenerator(job);
    }
}
