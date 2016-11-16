/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 10/15/13 - 9:18 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.tap.impl;


import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.search.*;
import ca.nrc.cadc.search.cutout.Cutout;
import ca.nrc.cadc.search.cutout.stc.STCCutoutImpl;
import ca.nrc.cadc.search.form.FormErrors;
import ca.nrc.cadc.search.parser.Resolver;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.TargetParser;
import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.search.parser.exception.TargetParserException;
import ca.nrc.cadc.search.parser.resolver.ResolverImpl;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.ErrorSummary;
import ca.nrc.cadc.uws.ErrorType;
import ca.nrc.cadc.uws.ExecutionPhase;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.ParameterUtil;
import ca.nrc.cadc.uws.RegexParameterUtil;
import ca.nrc.cadc.uws.SyncResponseWriter;
import ca.nrc.cadc.uws.server.JobUpdater;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;

import javax.security.auth.Subject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Searcher implementation for TAP queries.
 */
public class TAPSearcherImpl implements Searcher
{
    private static final Logger LOGGER =
            Logger.getLogger(TAPSearcherImpl.class);

    private static final String CAOM2_RESOLVER_VALUE_KEY =
            "Plane.position.bounds@Shape1Resolver.value";
    private static final String CAOM2_TARGET_NAME_VALUE_KEY =
            "Plane.position.bounds@Shape1.value";

    private static final String CAOM2_RESOLVER_VALUE_NONE = "NONE";

    private final SyncResponseWriter syncResponseWriter;
    private final JobUpdater jobUpdater;
    private final QueryGenerator queryGenerator;
    private final SyncTAPClient tapClient;


    /**
     * Full constructor.
     * @param writer         The Sync writer to write out results to.
     * @param jobUpdater     The UWS Job Updater.
     * @param queryGenerator The generator to use to handle queries
     */
    public TAPSearcherImpl(final SyncResponseWriter writer,
                           final JobUpdater jobUpdater,
                           final SyncTAPClient tapClient,
                           final QueryGenerator queryGenerator)
            throws PositionParserException
    {
        this.syncResponseWriter = writer;
        this.jobUpdater = jobUpdater;
        this.tapClient = tapClient;
        this.queryGenerator = queryGenerator;
    }

    /**
     * Determine whether the passed in resolver is set.
     *
     * @param resolverName      The resolver name pulled from the job
     *                          parameters.
     * @return                  True if set, false otherwise.
     */
    private boolean hasSetValidResolver(final String resolverName)
    {
        return StringUtil.hasText(resolverName)
               && !resolverName.equals(CAOM2_RESOLVER_VALUE_NONE);
    }


    /**
     * Execute the search, and write out the results to this implementation's
     * writer.
     *
     * @param job                The Job to execute.
     * @param serviceURI         The Service URI to use.
     * @param syncResponseWriter The writer to write to.
     * @throws Exception Any unforeseen errors.
     */
    @Override
    public void search(final Job job, final URI serviceURI,
                       final SyncResponseWriter syncResponseWriter)
            throws Exception
    {
        // Validate search form.
        final FormData formData = new FormData(job);
        final FormErrors formError = new FormErrors();
        final JSONWriter jsonWriter =
                new JSONWriter(syncResponseWriter.getWriter());

        try
        {
            syncResponseWriter.setResponseHeader("Content-Type",
                                                 "application/json");

            jsonWriter.object();

            // Errors in the form.
            if (!formData.isValid(formError))
            {
                handleError(job, formError.toString(), ErrorType.FATAL);
                jsonWriter.key("errorMessage").value(
                        job.getErrorSummary().getSummaryMessage());
            }
            else
            {
                runSearch(serviceURI, jsonWriter, job, formData);
            }

            jsonWriter.endObject();
        }
        catch (JSONException | IOException e)
        {
            throw new IllegalStateException("Unable to write out response.", e);
        }
        finally
        {
            try
            {
                syncResponseWriter.getWriter().flush();
            }
            catch (IOException e)
            {
                // Do nothing.
            }
        }
    }

    /**
     * Execute the TAP search and write out the JSON results.
     *
     * @param serviceURI    The TAP Service URI.
     * @param jsonWriter The Writer for the JSON results.
     * @param formData   The FormData object.

     * @throws IOException   Any I/O weirdness.
     * @throws JSONException Any weirdness writing out JSON.
     */
    private void runSearch(final URI serviceURI, final JSONWriter jsonWriter,
                           final Job job, final FormData formData)
            throws IOException, JSONException
    {
        // Generate the ADQL query string.
        final Templates templates =
                new Templates(formData.getFormConstraints());
        final FormErrors formError = new FormErrors();

        if (!templates.isValid(formError))
        {
            handleError(job, formError.toString(), ErrorType.FATAL);
        }
        else
        {
            // Just a simple OutputStream to hold the results URL.
            final OutputStream outputStream = new ByteArrayOutputStream();

            // Run the TAP job to do the ADQL query.
            queryTAP(serviceURI, createTAPJob(job, queryGenerator.generate(
                    templates).toString()), outputStream);

            final String resultsURLValue = outputStream.toString();
            final URL tapResultsURL = new URL(resultsURLValue);

            jsonWriter.key("results_url").value(tapResultsURL.toExternalForm());
            jsonWriter.key("job_url").value(
                    new URL(resultsURLValue.substring(0,
                                              resultsURLValue.indexOf("/run")))
                            .toExternalForm());
            jsonWriter.key("run_id").value(job.getID());

            writeFormValueUnits(jsonWriter, formData);

            if (isCutoutSpecified(job))
            {
                final Cutout cutout = getCutout(job, templates);
                final String cutoutValue = cutout.format();

                if (StringUtil.hasText(cutoutValue))
                {
                    jsonWriter.key("cutout").value(cutoutValue);
                }
            }

            // Include the parsed resolver information, if any.
            writeResolverJSON(job, jsonWriter);

            // Include the upload information.
            writeUploadInfoJSON(job, jsonWriter);
        }
    }

    /**
     * If there was an upload file, add the row and error count to json.
     *
     * @param jsonWriter The JSONWriter to append to.
     * @throws JSONException Any JSON Writing errors.
     */
    private void writeUploadInfoJSON(final Job job, final JSONWriter jsonWriter)
            throws JSONException
    {
        final List<Parameter> searchParameters = job.getParameterList();

        final String rowCount =
                ParameterUtil.findParameterValue(UploadResults.UPLOAD_ROW_COUNT,
                                                 searchParameters);
        final String errorCount =
                ParameterUtil.findParameterValue(
                        UploadResults.UPLOAD_ERROR_COUNT, searchParameters);

        final String uploadURLValue =
                ParameterUtil.findParameterValue("UPLOAD", searchParameters);

        if (StringUtil.hasText(rowCount))
        {
            jsonWriter.key(UploadResults.UPLOAD_ROW_COUNT).value(rowCount);
        }

        if (StringUtil.hasText(errorCount))
        {
            jsonWriter.key(UploadResults.UPLOAD_ERROR_COUNT).value(errorCount);
        }

        if (StringUtil.hasText(uploadURLValue))
        {
            jsonWriter.key("upload_url").value(uploadURLValue);
        }
    }

    /**
     * Write out the Form Value Units to the JSON Writer.  This can very well
     * be an empty JSON object in the end.
     *
     * @param jsonWriter The JSON Writer.
     * @param formData   The Form data containing the units.
     * @throws JSONException Any JSON writing errors.
     */
    private void writeFormValueUnits(final JSONWriter jsonWriter,
                                     final FormData formData)
            throws JSONException
    {
        final Map<String, String> formValueUnits =
                formData.getFormValueUnits();

        jsonWriter.key("display_units").object();

        for (final Map.Entry<String, String> entry
                : formValueUnits.entrySet())
        {
            jsonWriter.key(entry.getKey()).value(entry.getValue());
        }

        jsonWriter.endObject();
    }

    /**
     * Obtain the resolved data from the NameResolver service, and print it to
     * the JSON Writer.
     *
     * @param jsonWriter The JSONWriter to write to.
     * @throws JSONException If anything goes wrong with writing JSON.
     */
    private void writeResolverJSON(final Job job, final JSONWriter jsonWriter)
            throws JSONException
    {
        try
        {
            /*
             * Story 959, Task 2915
             * Display resolved results to the user.
             *
             * jenkinsd 2012.05.28
             */
            final List<Parameter> parameters = job.getParameterList();
            final String targetName =
                    ParameterUtil.findParameterValue(CAOM2_TARGET_NAME_VALUE_KEY,
                                                     parameters);
            final String resolverName =
                    ParameterUtil.findParameterValue(CAOM2_RESOLVER_VALUE_KEY,
                                                     parameters);

            if (StringUtil.hasText(targetName)
                && hasSetValidResolver(resolverName))
            {
                final String targetValue = targetName.trim();
                final Resolver resolver =
                        new ResolverImpl(new TargetNameResolverClientImpl());
                final TargetParser targetParser = new TargetParser(resolver);
                final TargetData targetData = targetParser.parse(targetValue,
                                                                 resolverName);
                System.out.println(targetData);

                final String raValue =
                        (targetData.getRA() == null)
                        ? targetData.getRaRange().getRange()
                        : Double.toString(targetData.getRA());

                final String decValue =
                        (targetData.getDec() == null)
                        ? targetData.getDecRange().getRange()
                        : Double.toString(targetData.getDec());

                jsonWriter.key("resolver_data").object();
                jsonWriter.key("ra").value(raValue);
                jsonWriter.key("dec").value(decValue);
                jsonWriter.key("coordsys").value(targetData.getCoordsys());
                jsonWriter.key("service").value(targetData.getService());

                jsonWriter.endObject();
            }
        }
        catch (TargetParserException e)
        {
            LOGGER.info("Unable to write out resolver information.", e);
            jsonWriter.key("error").value(e.getMessage());
            jsonWriter.endObject();
        }
    }

    private Job createTAPJob(final Job baseJob, final String query)
    {
        final List<Parameter> searchJobParameters = baseJob.getParameterList();
        final Job tapJob = new Job();
        tapJob.setRunID(baseJob.getID());

        final String requestedFormat =
                ParameterUtil.findParameterValue("format", searchJobParameters);
        final String uploadParameterValue =
                ParameterUtil.findParameterValue("UPLOAD", searchJobParameters);
        final String maxRecords =
                RegexParameterUtil.findParameterValue("MaxRecords",
                                                      searchJobParameters);
        LOGGER.debug("MaxRecords: " + maxRecords);

        final String format = StringUtil.hasText(requestedFormat)
                              ? requestedFormat : "votable";

        final List<Parameter> tapJobParams = new ArrayList<>();

        tapJobParams.add(new Parameter("LANG", "ADQL"));
        tapJobParams.add(new Parameter("FORMAT", format));
        tapJobParams.add(new Parameter("QUERY", query));
        tapJobParams.add(new Parameter("REQUEST", "doQuery"));
        tapJobParams.add(new Parameter("MAXREC",
                                       StringUtil.hasText(maxRecords)
                                       ? maxRecords
                                       : DEFAULT_MAXREC.toString()));

        if (StringUtil.hasText(uploadParameterValue))
        {
            tapJobParams.add(new Parameter("UPLOAD", uploadParameterValue));
        }

        tapJob.setParameterList(tapJobParams);

        return tapJob;
    }

    /**
     * Issue a TAP query.
     *
     * @param serviceURI    The TAP Service URI.
     * @param tapJob        The TAP job to execute.
     * @param outputStream  The stream to write out results to.
     * @throws IOException  Any writing errors.
     */
    void queryTAP(final URI serviceURI, final Job tapJob,
                  final OutputStream outputStream)
            throws IOException
    {
        final Subject ownerSubject = tapJob.ownerSubject;

        try
        {
            // Run the TAP Job.
            if (ownerSubject != null)
            {
                // To pass on the SSO Cookie credential
                Subject.doAs(ownerSubject, new PrivilegedAction<Void>()
                {
                    @Override
                    public Void run()
                    {
                        tapClient.execute(serviceURI, tapJob, outputStream);
                        return null;
                    }
                });
            }
            else
            {
                tapClient.execute(serviceURI, tapJob, outputStream);
            }
        }
        finally
        {
            outputStream.flush();
        }
    }

    /**
     * Handle an error message.
     *
     * @param message   The error message to set to the job.
     * @param errorType The error type to set to the job.
     */
    private void handleError(final Job job, final String message,
                             final ErrorType errorType)
    {
        final ErrorSummary errorSummary = new ErrorSummary(message, errorType);

        try
        {
            job.setErrorSummary(errorSummary);
            job.setExecutionPhase(ExecutionPhase.ERROR);

            jobUpdater.setPhase(job.getID(), ExecutionPhase.EXECUTING,
                                ExecutionPhase.ERROR, errorSummary, new Date());
        }
        catch (Throwable oops)
        {
            LOGGER.error("failed to set final error status after " + message,
                         oops);
            syncResponseWriter.setResponseCode(500);
        }
    }

    /**
     * Determine whether to go looking for a cutout or not.
     *
     * @return True if seeking out cutout, False otherwise.
     */
    private boolean isCutoutSpecified(final Job job)
    {
        return isSpatialCutoutSpecified(job) || isSpectralCutoutSpecified(job);
    }

    private boolean isSpatialCutoutSpecified(final Job job)
    {
        for (final Parameter parameter : job.getParameterList())
        {
            if (parameter.getName().equals("Plane.position.DOWNLOADCUTOUT"))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isSpectralCutoutSpecified(final Job job)
    {
        for (final Parameter parameter : job.getParameterList())
        {
            if (parameter.getName().equals("Plane.energy.DOWNLOADCUTOUT"))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtain a cutout from the query parameters.
     * @param templates     The Search templates to obtain values from.
     * @return              Cutout instance.
     */
    private Cutout getCutout(final Job job, final Templates templates)
    {
        final List<SpatialSearch> spatialSearches =
                templates.getSearchTemplates(SpatialSearch.class);
        final List<IntervalSearch> spectralSearches =
                templates.getSearchTemplates(IntervalSearch.class);

        return new STCCutoutImpl((spatialSearches.isEmpty()
                                  || !isSpatialCutoutSpecified(job))
                                 ? null : spatialSearches.get(0),
                                 (spectralSearches.isEmpty()
                                  || !isSpectralCutoutSpecified(job))
                                 ? null : spectralSearches.get(0));
    }
}
