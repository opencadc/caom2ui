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

package ca.nrc.cadc.tap;


import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.JobAttribute;
import ca.nrc.cadc.uws.Parameter;

import org.apache.http.client.utils.URIBuilder;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Extension of the TapClient to handle Synchronous access and job creation.
 */
public class DefaultSyncTAPClient implements SyncTAPClient {

    private static final Logger LOGGER = Logger.getLogger(DefaultSyncTAPClient.class);


    private final boolean followToResults;
    private final RegistryClient registryClient;


    public DefaultSyncTAPClient(final boolean followToResults, final RegistryClient registryClient) {
        this.followToResults = followToResults;
        this.registryClient = registryClient;
    }

    private URL lookupServiceURL(final URI serviceURI) throws IOException, URISyntaxException {
        final URL serviceURL = registryClient.getServiceURL(
                serviceURI,
                Standards.TAP_10,
                AuthMethod.ANON,
                Standards.INTERFACE_PARAM_HTTP);
        final URIBuilder builder = new URIBuilder(serviceURL.toURI());
        builder.setPath(serviceURL.getPath() + "/sync");
        final URL tapServiceURL = builder.build().toURL();

        LOGGER.info("Configured TAP Service URL: " + tapServiceURL);
        return tapServiceURL;
    }

    /**
     * Execute the given Job.
     *
     * @param serviceURI   The TAP Service URI.
     * @param job          The Job to execute.
     * @param outputStream The OutputStream to write out results.
     */
    @Override
    public void execute(final URI serviceURI, final Job job, final OutputStream outputStream) {
        try {
            final URL tapServiceURL = lookupServiceURL(serviceURI);
            if (tapServiceURL == null) {
                throw new IllegalStateException("TAP Service URL not found in CADC Registry.");
            }

            // POST PHASE=RUN to execute on server
            postJob(tapServiceURL, job, outputStream);
        } catch (Exception e) {
            final String error = "Error executing job " + job.getID();
            LOGGER.error(error, e);

            try {
                outputStream.write(error.getBytes());
            } catch (IOException we) {
                throw new IllegalStateException("Unable to write error > " + e, we);
            }
        }
    }

    /**
     * Build the payload to POST.
     *
     * @param job The Job to get the payload for.
     * @return Map of Parameter name:value.
     */
    Map<String, Object> getQueryPayload(final Job job) {
        final Map<String, Object> payload = new HashMap<>();

        if (StringUtil.hasText(job.getRunID())) {
            payload.put(JobAttribute.RUN_ID.getValue(), job.getRunID());
        }

        final List<Parameter> parameters = job.getParameterList();

        for (final Parameter parameter : parameters) {
            payload.put(parameter.getName(), parameter.getValue());
        }

        return payload;
    }

    /**
     * Make a POST request to the TAP Service to the given URL with the given
     * parameters.
     *
     * @param job The job to send.
     * @param url The URL to POST to.
     * @throws IOException For any IO errors.
     */
    private void postJob(final URL url, final Job job,
                         final OutputStream outputStream) throws IOException {
        // POST the parameters to the tapServer.
        final HttpPost httpPost = getPoster(url, job, outputStream);
        httpPost.setFollowRedirects(followToResults);
        httpPost.run();

        if (!followToResults) {
            final URL redirectURL = httpPost.getRedirectURL();

            if (redirectURL == null) {
                throw new IllegalStateException("No results found.");
            } else {
                outputStream.write(redirectURL.toExternalForm().getBytes());
                LOGGER.debug("Done writing out Response Body.");
            }
        }
    }

    HttpPost getPoster(final URL url, final Job job, final OutputStream outputStream) {
        return followToResults ? new HttpPost(url, getQueryPayload(job), outputStream)
                : new HttpPost(url, getQueryPayload(job), false);
    }
}
