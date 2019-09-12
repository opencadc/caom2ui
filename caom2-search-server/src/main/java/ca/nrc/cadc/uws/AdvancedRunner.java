/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
 *
 *  (c) 2019.                            (c) 2019.
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
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.uws;

import ca.nrc.cadc.caom2.CAOMQueryGenerator;
import ca.nrc.cadc.caom2.ObsCoreQueryGenerator;
import ca.nrc.cadc.config.ApplicationConfiguration;
import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.rest.SyncOutput;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.search.Searcher;
import ca.nrc.cadc.search.form.FormConstraint;
import ca.nrc.cadc.search.form.Shape1;
import ca.nrc.cadc.search.form.Text;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.DefaultSyncTAPClient;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.tap.TAPSearcher;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.server.*;
import ca.nrc.cadc.web.Configuration;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;


/**
 * Job runner for the Advanced Search.
 */
public class AdvancedRunner implements JobRunner {
    private static final String TAP_SERVICE_URI_PROPERTY_KEY = "org.opencadc.search.tap-service-id";
    static final URI DEFAULT_TAP_SERVICE_URI = URI.create("ivo://cadc.nrc.ca/argus");
    private static final Logger LOGGER = LogManager.getLogger(AdvancedRunner.class);
    private final ApplicationConfiguration applicationConfiguration;


    private Job job;
    private JobUpdater jobUpdater;
    private SyncOutput syncOutput;


    public AdvancedRunner() {
        this(new ApplicationConfiguration(Configuration.DEFAULT_CONFIG_FILE_PATH));
    }

    AdvancedRunner(final ApplicationConfiguration configuration) {
        this.applicationConfiguration = configuration;
    }


    @Override
    public void setJob(final Job job) {
        this.job = job;

//        Initialize the Job parameter list.
//        If this is a QuickSearch query, the Job has target and optionally
//        collection parameters.
        final List<Parameter> parameters = this.job.getParameterList();
        final String target = RegexParameterUtil.findParameterValue("target", parameters);

        if (StringUtil.hasText(target)) {
            final String shape1UType = "Plane.position.bounds";
            parameters.add(new Parameter(FormConstraint.FORM_NAME, shape1UType + Shape1.NAME));
            parameters.add(new Parameter(shape1UType + Shape1.VALUE, target));
        }

        final String collection = RegexParameterUtil.findParameterValue("collection", parameters);

        if (StringUtil.hasText(collection)) {
            final String textUType = "Observation.collection";
            parameters.add(new Parameter(FormConstraint.FORM_NAME, textUType + Text.NAME));
            parameters.add(new Parameter(textUType + Text.VALUE, collection));
        }
    }

    @Override
    public void setJobUpdater(JobUpdater ju) {
        this.jobUpdater = ju;
    }

    @Override
    public void setSyncOutput(SyncOutput so) {
        this.syncOutput = so;
    }


    URI getTapServiceURI() {
        final URI tapServiceURI;
        final String tapServiceKey;
        tapServiceURI = DEFAULT_TAP_SERVICE_URI;
        tapServiceKey = TAP_SERVICE_URI_PROPERTY_KEY;
        return applicationConfiguration.lookupServiceURI(tapServiceKey, tapServiceURI);
    }

    /*
     * (non-Javadoc)
     * @see ca.nrc.cadc.uws.server.JobRunner#run()
     */
    public void run() {
        LOGGER.debug("START");

        try {
            final String jobID = job.getID();

            final ExecutionPhase ep = jobUpdater.setPhase(jobID, ExecutionPhase.QUEUED, ExecutionPhase.EXECUTING,
                                                          currentDate());

            if (!ExecutionPhase.EXECUTING.equals(ep)) {
                LOGGER.error(jobID + ": QUEUED -> EXECUTING [FAILED] -- DONE");
            } else {
                LOGGER.info(jobID + ": QUEUED -> EXECUTING [OK]");

                if (hasUploadError()) {
                    writeUploadError();
                } else {
                    createSearcher().search(job, getTapServiceURI(), jobUpdater, wrapSyncOutput());

                    jobUpdater.setPhase(jobID, ExecutionPhase.EXECUTING, ExecutionPhase.COMPLETED, currentDate());
                    LOGGER.info(jobID + ": EXECUTING -> COMPLETED [OK]");
                }

                LOGGER.debug("DONE");
            }
        } catch (TransientException e) {
            syncOutput.setCode(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            handleError(e, ErrorType.TRANSIENT);
        } catch (Exception e) {
            handleError(e, ErrorType.FATAL);
        } finally {
            if (syncOutput != null) {
                try {
                    syncOutput.getOutputStream().flush();
                } catch (IOException e) {
                    LOGGER.warn("Unable to close syncOutput's OutputStream.",
                                e);
                }
            }
        }
    }

    /**
     * Create a default searcher instance when none exists.  This relies on the
     * setters to populate this class first (Or whatever the isInitialized()
     * method returns).
     *
     * @return Searcher instance
     * @throws IOException Error writing
     */
    Searcher createSearcher() throws IOException {
        final RegistryClient registryClient = new RegistryClient();
        final SyncTAPClient tapClient = new DefaultSyncTAPClient(false, registryClient);
        return new TAPSearcher(new SyncResponseWriterImpl(syncOutput), tapClient, getQueryGenerator());
    }

    /**
     * Obtain the appropriate query generator.
     *
     * @return QueryGenerator instance.
     */
    private QueryGenerator getQueryGenerator() {
        final Job j = job;

        // Look for parameters starting with obscore to determine if
        // querying CAOM2 or ObsCore.
        for (final Parameter parameter : j.getParameterList()) {
            if (ObsModel.isObsCore(parameter.getName())) {
                return new ObsCoreQueryGenerator(j);
            }
        }

        return new CAOMQueryGenerator(j);
    }

    /**
     * Handle ane exception.
     *
     * @param throwable The throwable error being handled.
     * @param errorType The error type to set to the job.
     */
    private void handleError(final Throwable throwable,
                             final ErrorType errorType) {
        LOGGER.error("BUG - unexpected failure", throwable);
        final ErrorSummary errorSummary =
            new ErrorSummary(throwable.getMessage(), errorType);
        try {
            jobUpdater.setPhase(job.getID(),
                                ExecutionPhase.EXECUTING,
                                ExecutionPhase.ERROR, errorSummary,
                                currentDate());
        } catch (JobNotFoundException | TransientException | JobPersistenceException e) {
            LOGGER.error("failed to set final error status after " + throwable,
                         e);
            syncOutput.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check for an error with an uploaded file.
     *
     * @return True if an error is present, false otherwise.
     */
    private boolean hasUploadError() {
        // Check if the file upload failed to parse.
        final String parameter =
            RegexParameterUtil.findParameterValue(
                UploadResults.UPLOAD_ERROR_COUNT,
                job.getParameterList());

        try {
            return StringUtil.hasText(parameter)
                && (Integer.valueOf(parameter) > 0);
        } catch (NumberFormatException e) {
            LOGGER.error("Unable to parse parameter to Integer "
                             + UploadResults.UPLOAD_ERROR_COUNT);
            return false;
        }
    }

    private void writeUploadError() {
        final ErrorSummary errorSummary = new ErrorSummary("Errors processing upload file", ErrorType.FATAL);
        try {
            jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING, ExecutionPhase.ERROR, errorSummary,
                                currentDate());
            final String uploadParameterValue =
                ParameterUtil.findParameterValue(SyncTAPClient.UPLOAD_JOB_PARAMETER_NAME, job.getParameterList());

            if (StringUtil.hasText(uploadParameterValue)) {
                final String[] uploadParameterItems = uploadParameterValue.split(",");
                final String output;

                syncOutput.setCode(HttpServletResponse.SC_NOT_ACCEPTABLE);

                if (uploadParameterItems.length > 1) {
                    syncOutput.setHeader("Content-Type", "text/plain");
                    output = uploadParameterItems[1];
                } else {
                    syncOutput.setHeader("Content-Type", "text/xml");
                    output = generateErrorVOTable("Uploaded file invalid.");
                }

                LOGGER.debug("Writing error: " + output);
                syncOutput.getOutputStream().write(output.getBytes());
            }
        } catch (Throwable oops) {
            LOGGER.error("failed to set final error status after file upload error", oops);
            syncOutput.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate a String error VOTable.
     *
     * @param extraInfo What to tell the user.
     * @return String XML votable.
     */
    private String generateErrorVOTable(final String extraInfo) {
        return "<?xml version=\"1.0\"?>"
            + "<VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.2\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "version=\"1.2\">"
            + "<INFO name=\"EXTRA_INFO\" value=\"" + extraInfo + "\"/>"
            + "</VOTABLE>";
    }


    /**
     * Obtain the current date.  Implementors can override.
     *
     * @return Date instance.
     */
    protected Date currentDate() {
        return new Date();
    }

    SyncResponseWriter wrapSyncOutput() throws IOException {
        return new SyncResponseWriterImpl(syncOutput);
    }
}
