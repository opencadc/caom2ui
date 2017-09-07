/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 12/1/11 - 8:51 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


public class PreviewServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(PreviewServlet.class);
    public static final String DATA_URI = "ivo://cadc.nrc.ca/data";

    private URL dataServiceURL;


    /**
     * Complete constructor.
     *
     * @param dataServiceURL     The URL of the Data Web Service.
     */
    public PreviewServlet(final URL dataServiceURL)
    {
        this.dataServiceURL = dataServiceURL;
    }

    /**
     * Constructor to use the Registry Client to obtain the Data Web Service
     * location.
     */
    public PreviewServlet()
    {
        final RegistryClient registryClient = new RegistryClient();
        this.dataServiceURL = registryClient.getServiceURL(URI.create(DATA_URI),
                                                           Standards.DATA_10,
                                                           AuthMethod.COOKIE);
    }


    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException
    {
        final URL jobURL = createJobURL(req);

        LOGGER.info("Retreving preview from " + jobURL.toExternalForm());

        final HttpURLConnection connection = connect(jobURL);

        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setInstanceFollowRedirects(true);

        if (connection.getResponseCode() > 400)
        {
            resp.setStatus(connection.getResponseCode());
        }
        else
        {
            final OutputStream outputStream = resp.getOutputStream();

            resp.setContentType(connection.getContentType());

            try (final InputStream inputStream = connection.getInputStream())
            {
                final byte[] buffer = new byte[1024 * 8];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) >= 0)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            finally
            {
                outputStream.flush();
            }
        }
    }

    /**
     * Form the URL for the job as based on the given parameter.
     *
     * @param request           The HTTP Request.
     * @return                  A URL instance.
     * @throws IOException      If the URL cannot be created.
     */
    protected URL createJobURL(final HttpServletRequest request)
            throws IOException
    {
        final String path = request.getPathInfo();
        final URL currentDataServiceURL = getDataServiceURL();
        final URL jobURL = new URL(currentDataServiceURL + path);

        return encodeURL(jobURL);
    }

    /**
     * Encode the URL to be hit for the Preview.
     *
     * @param url               The URL to encode the individual items for.
     * @return                  URL encoded.
     * @throws IOException      If the URL cannot be read or encoded.
     */
    private URL encodeURL(final URL url) throws IOException
    {
        final StringBuilder urlPathAndQueryString =
                new StringBuilder(url.toExternalForm().length());

        final String[] pathItems = url.getPath().split("/");

        for (final String s : pathItems)
        {
            urlPathAndQueryString.append(NetUtil.encode(s));
            urlPathAndQueryString.append("/");
        }

        urlPathAndQueryString.replace(urlPathAndQueryString.lastIndexOf("/"),
                                      urlPathAndQueryString.length(), "");

        return new URL(url, urlPathAndQueryString.toString());
    }

    /**
     * Open a connection to the given URL.  This is here to simplify testing.
     *
     * @param url               The URL to connect to.
     * @return                  A connection to the given URL.
     * @throws IOException      If anything went wrong.
     */
    protected HttpURLConnection connect(final URL url) throws IOException
    {
        return (HttpURLConnection) url.openConnection();
    }

    public URL getDataServiceURL()
    {
        return dataServiceURL;
    }
}
