/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2012.                         (c) 2012.
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
 * 7/5/12 - 12:51 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.tap.impl;

import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.JobAttribute;
import ca.nrc.cadc.uws.Parameter;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Extension of the TapClient to handle Synchronous access and job creation.
 */
public class SyncTAPClientImpl implements SyncTAPClient
{
    private static final Logger LOGGER =
            Logger.getLogger(SyncTAPClientImpl.class);

    private final URL tapServiceURL;
    private final OutputStream outputStream;
    private final boolean followToResults;


    /**
     * Complete constructor.
     * @param outputStream     The stream for the results of the client call.
     * @param tapServiceURL     The TAP service URL.
     * @param followToResults   Whether to follow redirects to the end result.
     */
    public SyncTAPClientImpl(final OutputStream outputStream,
                             final URL tapServiceURL,
                             final boolean followToResults)
    {
        this.outputStream = outputStream;
        this.tapServiceURL = tapServiceURL;
        this.followToResults = followToResults;
    }


    /**
     * Execute this client's Job.
     *
     * @param job The Job to execute.
     */
    @Override
    public void execute(final Job job)
    {
        try
        {
            if (tapServiceURL == null)
            {
                throw new IllegalStateException(
                        "TAP Service URL not found in CADC Registry.");
            }

            // POST PHASE=RUN to execute on server
            postJob(tapServiceURL, job);
        }
        catch (Exception e)
        {
            final String error = "Error executing job " + job.getID();
            LOGGER.error(error, e);

            try
            {
                getOutputStream().write(error.getBytes());
            }
            catch (IOException we)
            {
                throw new IllegalStateException("Unable to write error > " + e,
                                                we);
            }
        }
    }

    /**
     * Build the payload to POST.
     *
     * @return      Map of Parameter name -> value.
     */
    private Map<String, Object> getQueryPayload(final Job job)
    {
        final Map<String, Object> payload = new HashMap<>();

        if (StringUtil.hasText(job.getRunID()))
        {
            payload.put(JobAttribute.RUN_ID.getAttributeName(), job.getRunID());
        }

        payload.put(JobAttribute.EXECUTION_PHASE.getAttributeName(), "RUN");
        payload.put("REQUEST", "doQuery");

        final List<Parameter> parameters = job.getParameterList();

        for (final Parameter parameter : parameters)
        {
            payload.put(parameter.getName(), parameter.getValue());
        }

        return payload;
    }

    /**
     * Make a POST request to the TAP Service to the given URL with the given
     * parameters.
     *
     * @param job           The job to send.
     * @param url           The URL to POST to.
     * @throws IOException  For any IO errors.
     */
    private void postJob(final URL url, final Job job) throws IOException
    {
        // POST the parameters to the tapServer.
        final HttpPost httpPost = getPoster(url, job);
        httpPost.run();

        if (!followToResults)
        {
            final URL redirectURL = httpPost.getRedirectURL();

            if (redirectURL == null)
            {
                throw new IllegalStateException("No results found.");
            }
            else
            {
                getOutputStream().write(
                        redirectURL.toExternalForm().getBytes());
                LOGGER.debug("Done writing out Response Body.");
            }
        }
    }

    private HttpPost getPoster(final URL url, final Job job) throws IOException
    {
        final HttpPost poster;

        if (followToResults)
        {
            poster = new HttpPost(url, getQueryPayload(job), getOutputStream());
        }
        else
        {
            poster = new HttpPost(url, getQueryPayload(job), false);
        }

        return poster;
    }


    protected final OutputStream getOutputStream()
    {
        return outputStream;
    }
}
