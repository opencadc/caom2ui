
package ca.nrc.cadc.caom2.ui;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.xml.ObservationParsingException;
import ca.nrc.cadc.caom2.xml.ObservationReader;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.net.InputStreamWrapper;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivilegedAction;


/**
 * @author jburke
 */
public class ObservationViewServlet extends HttpServlet
{
    private static final long serialVersionUID = -917406909288899339L;

    private static Logger log = Logger.getLogger(ObservationViewServlet.class);
    private static final String ERROR_MESSAGE_NOT_FOUND_FORBIDDEN =
            "Observation with URI '%s' not found, or you are "
            + "forbidden from seeing it.  Please login and "
            + "try again. | l'Observation '%s' pas "
            + "trouvé, ou vous n'avez pas permission.  S'il "
            + "vous plaît connecter et essayez à nouveau.";

    static final String CAOM2OPS_ID = "ivo://cadc.nrc.ca/caom2ops";

    private final RegistryClient registryClient;

    public ObservationViewServlet()
    {
        this(new RegistryClient());
    }

    ObservationViewServlet(final RegistryClient registryClient)
    {
        this.registryClient = registryClient;
    }


    /**
     * Gets a complete Observation from a database and renders it in HTML or XML.
     * The HTML display is handled by forwarding to observation.jsp for rendering.
     * TODO: XML rendering.
     *
     * @param request  The servlet request.
     * @param response The servlet response.
     * @throws ServletException If servlet exception.
     * @throws IOException      If IO exception.
     */
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException
    {
        final long start = System.currentTimeMillis();

        try
        {
            // Parse the parameters given in the url.
            final ObservationURI uri = getURI(request);
            if (uri == null)
            {
                // error handling
                throw new RuntimeException(
                        "Must specify collection/observationID in the path. | "
                        + "Le chemain est incomplet.");
            }
            else
            {
                request.setAttribute("collection", uri.getCollection());
                request.setAttribute("observationID", uri.getObservationID());

                final Observation obs =
                        getObservation(getCurrentSubject(), uri);

                if (obs == null)
                {
                    throw new RuntimeException(
                            String.format(ERROR_MESSAGE_NOT_FOUND_FORBIDDEN,
                                          uri, uri));
                }
                else
                {
                    request.setAttribute("obs", obs);
                    forward(request, response, "/display.jsp");
                }
            }
        }
        catch (NumberFormatException nex)
        {
            // no obsID == malformed request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (RuntimeException oops)
        {
            log.error("unexpected exception", oops);
            request.setAttribute("errorMsg", oops.getMessage());
            forward(request, response, "/error.jsp");
        }
        finally
        {
            log.info("doGet[" + (System.currentTimeMillis() - start) + "ms]");
        }
    }

    private void forward(final HttpServletRequest request,
                 final HttpServletResponse response, final String path)
            throws ServletException, IOException
    {
        final RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward(request, response);
    }

    /**
     * Download the Observation for the given URI.
     *
     * @param subject The Subject to download as.
     * @param uri     The Observation URI.
     * @return Observation instance.
     */
    Observation getObservation(final Subject subject, final ObservationURI uri)
    {
        try
        {
            final URL serviceURL = getServiceURL(uri);

            log.debug("GET " + serviceURL);

            final ReadAction ra = getObservationReader();
            final HttpDownload get = getDownloader(serviceURL, ra);

            Subject.doAs(subject, new GetAction(get, uri));

            return ra.getObs();
        }
        catch (MalformedURLException | URISyntaxException ex)
        {
            throw new RuntimeException(
                    "BUG: failed to find cred service in registry");
        }
    }

    private URL getServiceURL(final ObservationURI uri)
            throws URISyntaxException, MalformedURLException
    {
        final Subject subject = getCurrentSubject();
        final AuthMethod authMethod =
                ((subject == null)
                 || subject.getPrincipals(X500Principal.class).isEmpty())
                ? AuthMethod.ANON : AuthMethod.COOKIE;

        final URL repoURL = registryClient.getServiceURL(
                URI.create(CAOM2OPS_ID), Standards.CAOM2_OBS_20,
                authMethod);

        final URIBuilder builder = new URIBuilder(repoURL.toURI());

        builder.addParameter("ID", uri.getURI().toString());

        return builder.build().toURL();
    }

    /**
     * Place for testers to override.
     *
     * @return ReadAction instance.
     */
    ReadAction getObservationReader()
    {
        return new ReadAction();
    }

    /**
     * Obtain a new instance of a downloader.  Tests can override as needed.
     *
     * @param url        The URL to download from.
     * @param readAction The read action to write to.
     * @return HttpDownload instance.
     */
    HttpDownload getDownloader(final URL url, final ReadAction readAction)
    {
        return new HttpDownload(url, readAction);
    }

    /**
     * Testers or subclasses can override this as needed.
     *
     * @return Subject instance.
     */
    Subject getCurrentSubject()
    {
        return AuthenticationUtil.getCurrentSubject();
    }


    private class GetAction implements PrivilegedAction<Void>
    {
        private final ObservationURI uri;
        private final HttpDownload downloader;


        GetAction(final HttpDownload downloader, final ObservationURI uri)
        {
            this.downloader = downloader;
            this.uri = uri;
        }


        public Void run()
        {
            downloader.run();

            final Throwable e = downloader.getThrowable();

            if (e != null)
            {
                final String message;

                if (e instanceof FileNotFoundException)
                {
                    message = ERROR_MESSAGE_NOT_FOUND_FORBIDDEN;
                }
                else
                {
                    message = "Failed to get observation '%s' from caom2meta. "
                              + "| Impossible d'obtenir l'observation '%s' "
                              + "de caom2meta.";
                }

                throw new RuntimeException(String.format(message, uri, uri));
            }

            return null;
        }
    }

    class ReadAction implements InputStreamWrapper
    {
        private Observation obs;


        public void read(final InputStream in) throws IOException
        {
            try
            {
                final ObservationReader r = new ObservationReader(false);
                this.obs = r.read(in);
            }
            catch (ObservationParsingException ex)
            {
                throw new RuntimeException(
                        "Failed to read observation from /caom2meta | "
                        + "Impossible d'obtenir l'observation de /caom2meta.");
            }
        }

        Observation getObs()
        {
            return obs;
        }
    }

    private ObservationURI getURI(HttpServletRequest request)
    {
        String sid = request.getPathInfo();
        log.debug("request.getPathInfo(): " + sid);

        if (sid != null)
        {
            sid = sid.substring(1, sid.length()); // strip leading /
            final String[] parts = sid.split("/");

            if (parts.length == 2)
            {
                log.debug("collection: " + parts[0] + " observationID: "
                          + parts[1]);
                return new ObservationURI(parts[0], parts[1]);
            }
        }

        log.debug("collection/observationID not found in path");
        return null;
    }
}
