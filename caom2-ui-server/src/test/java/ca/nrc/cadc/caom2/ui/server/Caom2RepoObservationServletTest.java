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

package ca.nrc.cadc.caom2.ui.server;

import ca.nrc.cadc.caom2.Algorithm;
import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.ui.server.client.Caom2MetaClient;
import ca.nrc.cadc.caom2.ui.server.client.Caom2RepoClient;
import ca.nrc.cadc.caom2.ui.server.client.ObsLink;
import ca.nrc.cadc.config.ApplicationConfiguration;

import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

import ca.nrc.cadc.net.HttpDownload;
import org.junit.Test;


public class Caom2RepoObservationServletTest {
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
    public void doGetCollectionsList() throws Exception {

        final URL repoURL = new URL("http://mysite.com/caom2repo");
        final Subject currentUser = new Subject();
        final List<String> collectionList = new ArrayList<>();
        collectionList.add("TEST");
        collectionList.add("SANDBOX");

        final Caom2RepoClient testClient =
            new Caom2RepoClient() {
                @Override
                public Subject getCurrentSubject() {
                    return currentUser;
                }


                @Override
                public List<String> getCollections() {
                    return collectionList;
                }

                @Override
                public URL getServiceURL() {
                    return repoURL;
                }
            };

        expect(mockRequest.getPathInfo()).andReturn("/").anyTimes();

        mockRequest.setAttribute("collections", collectionList);
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/collectionslist.jsp")).andReturn(
            mockDisplayDispatcher).once();
        mockDisplayDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();


        replay(mockRequest, mockDisplayDispatcher,
               mockResponse);

        final Caom2RepoObservationServlet testSubject =
            new Caom2RepoObservationServlet(testClient);
        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockDisplayDispatcher,
               mockResponse);
    }

    @Test
    public void doObservationList() throws Exception {

        final URL repoURL = new URL("http://mysite.com/caom2repo");
        final Subject currentUser = new Subject();
        final List<ObsLink> observationList = new ArrayList<>();

        ObsLink obs1 = new ObsLink();
        obs1.lastModified = new Date();
        obs1.type = "test";
        obs1.uri = null;
        observationList.add(obs1);

        final Caom2RepoClient testClient =
            new Caom2RepoClient() {
                @Override
                public Subject getCurrentSubject() {
                    return currentUser;
                }


                @Override
                public List<ObsLink> getObservations(String collection) {
                    return observationList;
                }

                @Override
                public URL getServiceURL() {
                    return repoURL;
                }
            };

        expect(mockRequest.getPathInfo()).andReturn("/MYCOLLECTION/").anyTimes();

        mockRequest.setAttribute("uris", observationList);
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/obslist.jsp")).andReturn(
            mockDisplayDispatcher).once();
        mockDisplayDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();

        replay(mockRequest, mockDisplayDispatcher,
               mockResponse);

        final Caom2RepoObservationServlet testSubject =
            new Caom2RepoObservationServlet(testClient);
        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockDisplayDispatcher,
               mockResponse);
    }


    @Test
    public void doGetNullObservation() throws Exception {
        final Subject currentUser = new Subject();
        final Caom2RepoClient testClient =
            new Caom2RepoClient() {

                @Override
                public Subject getCurrentSubject() {
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
                public Observation getObservation(Subject subject,
                                                  ObservationURI uri) {
                    return null;
                }
            };

        expect(mockRequest.getPathInfo()).andReturn("/MYARCHIVE/MYOBSID").anyTimes();

        mockRequest.setAttribute("errorMsg",
                                 "Observation with URI 'caom:MYARCHIVE/MYOBSID' "
                                     + "not found, or you are forbidden from seeing it.  "
                                     + "Please login and try again. | l'Observation "
                                     + "'caom:MYARCHIVE/MYOBSID' pas trouvé, ou vous "
                                     + "n'avez pas permission.  S'il vous plaît "
                                     + "connecter et essayez à nouveau.");
        expectLastCall().once();

        expect(mockRequest.getRequestDispatcher("/error.jsp")).andReturn(mockErrorDispatcher).once();
        mockErrorDispatcher.forward(mockRequest, mockResponse);
        expectLastCall().once();

        replay(mockRequest, mockErrorDispatcher, mockDisplayDispatcher, mockResponse);


        final Caom2RepoObservationServlet testSubject = new Caom2RepoObservationServlet(testClient);

        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockErrorDispatcher, mockDisplayDispatcher, mockResponse);
    }


    @Test
    public void doGetObservation() throws Exception {

        final Subject currentUser = new Subject();
        final Observation result = new Observation("MYARCHIVE", "MYOBSID",
                                                   new Algorithm("exposure")) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        final Caom2RepoClient.ReadAction mockObservationReader =
            createMock(Caom2RepoClient.ReadAction.class);
        final URL repoURL = new URL("http://mysite.com/caom2repo");
        final HttpDownload mockDownloader = createMock(HttpDownload.class);

        final Caom2RepoClient testClient =
            new Caom2RepoClient() {

                /**
                 * Testers or subclasses can override this as needed.
                 *
                 * @return Subject instance.
                 */
                @Override
                public Subject getCurrentSubject() {
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
                public HttpDownload getDownloader(URL url,
                                                  Caom2MetaClient.ReadAction readAction) {
                    return mockDownloader;
                }

                /**
                 * Place for testers to override.
                 *
                 * @return ReadAction instance.
                 */
                @Override
                public Caom2MetaClient.ReadAction getObservationReader() {
                    return mockObservationReader;
                }

                @Override
                public URL getServiceURL() {
                    return repoURL;
                }
            };

        expect(mockRequest.getPathInfo()).andReturn("/MYARCHIVE/MYOBSID").
            anyTimes();

        expect(mockObservationReader.getObs()).andReturn(result).once();

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
               mockResponse, mockDownloader,
               mockObservationReader, mockConfiguration);

        final Caom2RepoObservationServlet testSubject = new Caom2RepoObservationServlet(testClient);

        testSubject.doGet(mockRequest, mockResponse);

        verify(mockRequest, mockErrorDispatcher, mockDisplayDispatcher,
               mockResponse, mockDownloader,
               mockObservationReader, mockConfiguration);
    }
}
