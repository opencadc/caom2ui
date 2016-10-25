/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
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
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.search;

import ca.nrc.cadc.caom2.CAOMQueryGeneratorImpl;
import ca.nrc.cadc.caom2.ObsCoreQueryGeneratorImpl;
import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.tap.impl.*;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.*;
import ca.nrc.cadc.uws.server.*;

import java.io.*;
import java.net.URI;
import java.util.Date;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;


/**
 * Job runner for the Advanced Search.
 */
public class AdvancedRunner implements JobRunner
{
    private static final Logger LOGGER = Logger.getLogger(AdvancedRunner.class);

    private Job job;
    private JobUpdater jobUpdater;
    private SyncOutput syncOutput;
    private Searcher searcher;
    private Configuration configuration;


    public AdvancedRunner()
    {
    }


    /**
     * Complete constructor.  Useful for testing.
     *
     * @param _job        The AdvancedSearch Job.
     * @param _jobUpdater The JobUpdater.
     * @param _syncOutput The Syncronous writer output.
     * @param _searcher   The base searcher.
     */
    AdvancedRunner(final Job _job, final JobUpdater _jobUpdater,
                   final SyncOutput _syncOutput, final Searcher _searcher)
    {
        this.job = _job;
        this.jobUpdater = _jobUpdater;
        this.syncOutput = _syncOutput;
        this.searcher = _searcher;
    }


    public void setJob(final Job job)
    {
        this.job = job;
    }

    public void setJobUpdater(JobUpdater ju)
    {
        this.jobUpdater = ju;
    }

    public void setSyncOutput(SyncOutput so)
    {
        this.syncOutput = so;
    }


    /**
     * Initialize those items that require the setters to complete first.
     */
    private void init() throws IOException, PositionParserException
    {
        // Force a reload each time.
        configuration = new SystemConfiguration();

        if (searcher == null)
        {
            createSearcher();
        }
    }

    /*
    * (non-Javadoc)
    * @see ca.nrc.cadc.uws.server.JobRunner#run()
    */
    public void run()
    {
        LOGGER.debug("START");

        try
        {
            init();
            final String jobID = job.getID();

            final ExecutionPhase ep =
                    jobUpdater.setPhase(jobID, ExecutionPhase.QUEUED,
                                        ExecutionPhase.EXECUTING,
                                        currentDate());

            if (!ExecutionPhase.EXECUTING.equals(ep))
            {
                LOGGER.error(jobID
                             + ": QUEUED -> EXECUTING [FAILED] -- DONE");
            }
            else
            {
                LOGGER.info(jobID + ": QUEUED -> EXECUTING [OK]");

                if (hasUploadError())
                {
                    writeUploadError();
                }
                else
                {
                    searcher.search(job);

                    jobUpdater.setPhase(jobID,
                                        ExecutionPhase.EXECUTING,
                                        ExecutionPhase.COMPLETED,
                                        currentDate());
                    LOGGER.info(jobID + ": EXECUTING -> COMPLETED [OK]");
                }

                LOGGER.debug("DONE");
            }
        }
        catch (TransientException e)
        {
            syncOutput.setResponseCode(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            handleError(e, ErrorType.TRANSIENT);
        }
        catch (Throwable t)
        {
            handleError(t, ErrorType.FATAL);
        }
        finally
        {
            if (syncOutput != null)
            {
                try
                {
                    syncOutput.getOutputStream().flush();
                }
                catch (IOException e)
                {
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
     * @throws IOException             Error writing
     * @throws PositionParserException Error parsing a target
     */
    private void createSearcher() throws IOException, PositionParserException
    {
        this.searcher = new TAPSearcherImpl(
                new SyncResponseWriterImpl(syncOutput),
                jobUpdater, lookupServiceURI(), getQueryGenerator());
    }

    private URI lookupServiceURI()
    {
        return URI.create(configuration.getString(
                SyncTAPClient.TAP_SERVICE_URI_PROPERTY_KEY,
                SyncTAPClient.DEFAULT_TAP_SERVICE_URI_VALUE));
    }

    /**
     * Obtain the appropriate query generator.
     *
     * @return QueryGenerator instance.
     */
    private QueryGenerator getQueryGenerator()
    {
        final Job j = job;

        // Look for parameters starting with obscore to determine if
        // querying CAOM2 or ObsCore.
        for (final Parameter parameter : j.getParameterList())
        {
            if (ObsModel.isObsCore(parameter.getName()))
            {
                return new ObsCoreQueryGeneratorImpl(j);
            }
        }

        return new CAOMQueryGeneratorImpl(j);
    }

    /**
     * Handle ane exception.
     *
     * @param throwable The throwable error being handled.
     * @param errorType The error type to set to the job.
     */
    private void handleError(final Throwable throwable,
                             final ErrorType errorType)
    {
        LOGGER.error("BUG - unexpected failure", throwable);
        final ErrorSummary errorSummary =
                new ErrorSummary(throwable.getMessage(), errorType);
        try
        {
            jobUpdater.setPhase(job.getID(),
                                ExecutionPhase.EXECUTING,
                                ExecutionPhase.ERROR, errorSummary,
                                currentDate());
        }
        catch (JobNotFoundException | TransientException
                | JobPersistenceException e)
        {
            LOGGER.error("failed to set final error status after " + throwable,
                         e);
            syncOutput.setResponseCode(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Check for an error with an uploaded file.
     *
     * @return True if an error is present, false otherwise.
     */
    private boolean hasUploadError()
    {
        // Check if the file upload failed to parse.
        final String parameter =
                RegexParameterUtil.findParameterValue(
                        UploadResults.UPLOAD_ERROR_COUNT,
                        job.getParameterList());

        try
        {
            return StringUtil.hasText(parameter)
                   && (Integer.valueOf(parameter) > 0);
        }
        catch (NumberFormatException e)
        {
            LOGGER.error("Unable to parse parameter to Integer "
                         + UploadResults.UPLOAD_ERROR_COUNT);
            return false;
        }
    }

    private void writeUploadError()
    {
        final ErrorSummary errorSummary =
                new ErrorSummary("Errors processing upload file",
                                 ErrorType.FATAL);
        try
        {
            jobUpdater.setPhase(job.getID(),
                                ExecutionPhase.EXECUTING,
                                ExecutionPhase.ERROR,
                                errorSummary, currentDate());
            final String uploadParameterValue =
                    ParameterUtil.findParameterValue("UPLOAD",
                                                     job.getParameterList());

            if (StringUtil.hasText(uploadParameterValue))
            {
                final String[] uploadParameterItems =
                        uploadParameterValue.split(",");
                final String output;

                syncOutput.setResponseCode(
                        HttpServletResponse.SC_NOT_ACCEPTABLE);

                if (uploadParameterItems.length > 1)
                {
                    syncOutput.setHeader("Content-Type", "text/plain");
                    output = uploadParameterItems[1];
                }
                else
                {
                    syncOutput.setHeader("Content-Type", "text/xml");
                    output = generateErrorVOTable("Uploaded file invalid.");
                }

                LOGGER.debug("Writing error: " + output);
                syncOutput.getOutputStream().write(output.getBytes());
            }
        }
        catch (Throwable oops)
        {
            LOGGER.error("failed to set final error status after "
                         + "file upload error", oops);
            syncOutput.setResponseCode(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate a String error VOTable.
     *
     * @param extraInfo What to tell the user.
     * @return String XML votable.
     */
    private String generateErrorVOTable(final String extraInfo)
    {
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
    protected Date currentDate()
    {
        return new Date();
    }


    /**
     * Implementation of the Synchronous writer.
     */
    private final class SyncResponseWriterImpl implements SyncResponseWriter
    {
        final Writer writer;
        final SyncOutput syncOutput;


        private SyncResponseWriterImpl(final SyncOutput so)
                throws IOException
        {
            this.syncOutput = so;
            this.writer = new BufferedWriter(
                    new OutputStreamWriter(syncOutput.getOutputStream()));
        }


        final SyncOutput getSynchronousOutput()
        {
            return this.syncOutput;
        }

        @Override
        public final Writer getWriter()
        {
            return writer;
        }


        /**
         * Set the HTTP response code. Calls to this method that occur after the
         * OutputStream is opened are silently ignored.
         *
         * @param code The desired Response code
         */
        @Override
        public void setResponseCode(final int code)
        {
            getSynchronousOutput().setResponseCode(code);
        }

        /**
         * Set an HTTP header parameter. Calls to this method that occur after the
         * OutputStream is opened are silently ignored.
         *
         * @param key   header key.
         * @param value header value.
         */
        @Override
        public void setResponseHeader(final String key, final String value)
        {
            getSynchronousOutput().setHeader(key, value);
        }
    }

}
