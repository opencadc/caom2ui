/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2015.                         (c) 2015.
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
 * 08/04/15 - 3:16 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2.ui;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.caom2.Algorithm;
import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.config.ApplicationConfiguration;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import org.junit.Test;

import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URL;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;


public class ObservationViewServletTest
{
    private final HttpServletRequest mockRequest =
            createMock(HttpServletRequest.class);
    private final RequestDispatcher mockErrorDispatcher =
            createMock(RequestDispatcher.class);
    private final RequestDispatcher mockDisplayDispatcher =
            createMock(RequestDispatcher.class);
    private final HttpServletResponse mockResponse =
            createMock(HttpServletResponse.class);
    private final ApplicationConfiguration mockConfiguration =
            createMock(ApplicationConfiguration.class);


    @Test
    public void doGetNullURI() throws Exception
    {
        final Subject currentUser = new Subject();
        final ObservationViewServlet testSubject =
                new ObservationViewServlet()
                {
                    /**
                     * Testers or subclasses can override this as needed.
                     *
                     * @return Subject instance.
                     */
                    @Override
                    Subject getCurrentSubject()
                    {
                        return currentUser;
                    }
                };

        expect(mockRequest.getPathInfo()).andReturn("NOPATH").once();

        mockRequest.setAttribute("errorMsg",
                                 "Must specify collection/observationID in the "
                                 + "path. | Le chemain est incomplet.");
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/error.jsp")).andReturn(
                mockErrorDispatcher).once();
        mockErrorDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();

        replay(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse);

        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse);
    }

    @Test
    public void doGetNullObservation() throws Exception
    {
        final Subject currentUser = new Subject();
        final ObservationViewServlet testSubject =
                new ObservationViewServlet()
                {
                    /**
                     * Testers or subclasses can override this as needed.
                     *
                     * @return Subject instance.
                     */
                    @Override
                    Subject getCurrentSubject()
                    {
                        return currentUser;
                    }

                    /**
                     * Download the Observation for the given URI.
                     *
                     * @param subject   The Subject to download as.
                     * @param uri       The Observation URI.
                     * @return Observation instance.
                     */
                    @Override
                    Observation getObservation(Subject subject,
                                               ObservationURI uri)
                    {
                        return null;
                    }
                };

        expect(mockRequest.getPathInfo()).andReturn("/MYARCHIVE/MYOBSID").
                once();

        mockRequest.setAttribute("collection", "MYARCHIVE");
        expectLastCall().once();

        mockRequest.setAttribute("observationID", "MYOBSID");
        expectLastCall().once();

        mockRequest.setAttribute("errorMsg",
                                 "Observation with URI 'caom:MYARCHIVE/MYOBSID' "
                                 + "not found, or you are forbidden from seeing it.  "
                                 + "Please login and try again. | l'Observation "
                                 + "'caom:MYARCHIVE/MYOBSID' pas trouvé, ou vous "
                                 + "n'avez pas permission.  S'il vous plaît "
                                 + "connecter et essayez à nouveau.");
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/error.jsp")).andReturn(
                mockErrorDispatcher).once();
        mockErrorDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();

        replay(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse);

        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse);
    }

    @Test
    public void doGet() throws Exception
    {
        final RegistryClient mockRegistryClient =
                createMock(RegistryClient.class);
        final Subject currentUser = new Subject();
        final Observation result = new Observation("MYARCHIVE", "MYOBSID",
                                                   new Algorithm("exposure"))
        {
            @Override
            public String toString()
            {
                return super.toString();
            }
        };

        final URI serviceURI = URI.create("ivo://myhost.com/caom2-service");
        final ObservationViewServlet.ReadAction mockObservationReader =
                createMock(ObservationViewServlet.ReadAction.class);
        final URL repoURL = new URL("http://mysite.com/caom2ops/meta");
        final HttpDownload mockDownloader = createMock(HttpDownload.class);
        final ObservationViewServlet testSubject =
                new ObservationViewServlet(mockRegistryClient,
                                           mockConfiguration)
                {
                    /**
                     * Testers or subclasses can override this as needed.
                     *
                     * @return Subject instance.
                     */
                    @Override
                    Subject getCurrentSubject()
                    {
                        return currentUser;
                    }

                    /**
                     * Obtain a new instance of a downloader.  Tests can override as needed.
                     *
                     * @param url        The URL to download from.
                     * @param readAction The read action to write to.
                     * @return HttpDownload instance.
                     */
                    @Override
                    HttpDownload getDownloader(URL url,
                                               ObservationViewServlet.ReadAction readAction)
                    {
                        return mockDownloader;
                    }

                    /**
                     * Place for testers to override.
                     *
                     * @return ReadAction instance.
                     */
                    @Override
                    ReadAction getObservationReader()
                    {
                        return mockObservationReader;
                    }
                };

        expect(mockRequest.getPathInfo()).andReturn("/MYARCHIVE/MYOBSID").
                once();

        expect(mockObservationReader.getObs()).andReturn(result).once();

        expect(mockConfiguration.lookupServiceURI(
                ObservationViewServlet.CAOM2META_SERVICE_URI_PROPERTY_KEY,
                ObservationViewServlet.DEFAULT_CAOM2META_SERVICE_URI))
                .andReturn(serviceURI).once();

        expect(mockConfiguration.lookup(
                ObservationViewServlet.CAOM2META_SERVICE_HOST_PORT_PROPERTY_KEY))
                .andReturn(ObservationViewServlet
                                   .DEFAULT_CAOM2META_SERVICE_HOST_PORT).once();

        expect(mockRegistryClient.getServiceURL(serviceURI,
                                                Standards.CAOM2_OBS_20,
                                                AuthMethod.ANON))
            .andReturn(repoURL).once();

        mockDownloader.run();
        expectLastCall().once();

        expect(mockDownloader.getThrowable()).andReturn(null).once();

        mockRequest.setAttribute("obs", result);
        expectLastCall().once();

        mockRequest.setAttribute("collection", "MYARCHIVE");
        expectLastCall().once();

        mockRequest.setAttribute("observationID", "MYOBSID");
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/display.jsp")).andReturn(
                mockErrorDispatcher).once();
        mockErrorDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();

        replay(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse, mockRegistryClient, mockDownloader,
               mockObservationReader, mockConfiguration);

        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse, mockRegistryClient, mockDownloader,
               mockObservationReader, mockConfiguration);
    }
}
