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
 * 11/14/13 - 2:45 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.RunnableAction;
import ca.nrc.cadc.auth.SSLUtil;
import ca.nrc.cadc.net.HttpUpload;
import ca.nrc.cadc.net.OutputStreamWrapper;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.upload.TAPUploadFilenameGenerator;
import ca.nrc.cadc.search.upload.VOTableUploader;
import ca.nrc.cadc.tap.impl.WebTmpUtil;
import ca.nrc.cadc.uws.web.InlineContentException;

import javax.security.auth.Subject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


/**
 * Default implementation of a VOTableUploader.
 */
public class VOTableUploaderImpl implements VOTableUploader
{
    static final String DATA_URI = "ivo://cadc.nrc.ca/data";


    private final HttpUpload httpUpload;

    // Used to determine the HTTP Port of a service.
    private final RegistryClient registryClient;


    /**
     * Complete constructor.
     *  @param outputStreamWrapper      The OutputStreamWrapper to write out to.
     * @param filenameGenerator         The filename generator.
     * @param registryClient            The RegistryClient to lookup services.
     */
    public VOTableUploaderImpl(final OutputStreamWrapper outputStreamWrapper,
                               final TAPUploadFilenameGenerator filenameGenerator,
                               final RegistryClient registryClient)
    {
        this(new HttpUpload(outputStreamWrapper,
                            WebTmpUtil.getURL(filenameGenerator.generate(),
                                              null)), registryClient);
    }

    /**
     * Alternate complete constructor if an HTTPUpload is already in hand.
     *
     * @param _httpUpload       The HTTP Upload object.
     * @param registryClient    The RegistryClient to lookup services.
     */
    public VOTableUploaderImpl(final HttpUpload _httpUpload,
                               final RegistryClient registryClient)
    {
        this.httpUpload = _httpUpload;
        this.registryClient = registryClient;
    }


    /**
     * Perform the upload.
     *
     * @return The URL of where to get the upload.
     * @throws ca.nrc.cadc.uws.web.InlineContentException
     *                                        If the upload fails.
     * @throws java.net.MalformedURLException If the return URL cannot be obtained.
     */
    @Override
    public URL upload() throws InlineContentException, MalformedURLException
    {
        final HttpUpload upload = getHttpUpload();
        upload.setContentType("text/xml");

        /*
         * Make a secure upload to WEBTMP, since we cannot simply allow public
         * uploads.
         */
        secureUpload(upload);

        final Throwable uploadError = upload.getThrowable();

        // Error during the upload, throw an exception.
        if (uploadError != null)
        {
            throw new InlineContentException("Failed to store upload",
                                             uploadError);
        }
        else
        {
            /*
             * Make a public download, however.  This requires asking the
             * Registry Client for the service URL of an HTTP only download.
             */
            return toHTTPURL(upload.getURL());
        }
    }

    /**
     * Construct a new URL using the HTTP Protocol to download the newly
     * uploaded file.
     *
     * @param httpsURL      The HTTPS URL to convert.
     * @return              The HTTP URL.
     *
     * @throws MalformedURLException    If the new URL is poorly formed.
     */
    URL toHTTPURL(final URL httpsURL) throws MalformedURLException
    {
        final URL dataHTTPURL = registryClient.getServiceURL(URI.create(DATA_URI),
                                                             Standards.DATA_10,
                                                             AuthMethod.COOKIE);

        return new URL("http", httpsURL.getHost(), dataHTTPURL.getPort(), httpsURL.getFile());
    }

    /**
     * Execute the upload over HTTPS.
     *
     * @param upload        The upload Runnable class.
     */
    protected void secureUpload(final Runnable upload)
    {
        Subject.doAs(getSubject(), new RunnableAction(upload));
    }

    private Subject getSubject()
    {
        return SSLUtil.createSubject(WebTmpUtil.getCertFile());
    }

    public HttpUpload getHttpUpload()
    {
        return httpUpload;
    }
}
