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
import ca.nrc.cadc.profiler.Profiler;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.util.DefaultJobURLCreator;
import ca.nrc.cadc.web.ConfigurableServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URL;


public class PreviewServlet extends ConfigurableServlet {
    private static final String DATA_URI = "ivo://cadc.nrc.ca/data";

    private final PreviewRequestHandler previewRequestHandler;
    private final Profiler profiler;


    /**
     * Complete constructor.
     *
     * @param previewRequestHandler     Request handler for Preview requests.
     * @param profiler                  The checkpoint profiler.
     */
    public PreviewServlet(final PreviewRequestHandler previewRequestHandler, final Profiler profiler) {
        this.previewRequestHandler = previewRequestHandler;
        this.profiler = profiler;
    }

    /**
     * Constructor to use the Registry Client to obtain the Data Web Service
     * location.
     */
    public PreviewServlet() {
        final RegistryClient registryClient = new RegistryClient();
        final URL dataServiceURL = registryClient.getServiceURL(URI.create(DATA_URI), Standards.DATA_10,
            AuthMethod.COOKIE);
        this.previewRequestHandler = new PreviewRequestHandler(dataServiceURL, new DefaultJobURLCreator());
        this.profiler = new Profiler(PreviewServlet.class);
    }


    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String requestPath = req.getPathInfo();
        profiler.checkpoint(String.format("%s doGet() start", requestPath));
        this.previewRequestHandler.get(req, resp);
        profiler.checkpoint(String.format("%s doGet() end", requestPath));
    }
}
