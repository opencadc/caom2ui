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
 * 12/1/11 - 8:54 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import org.junit.After;
import org.junit.Test;

import ca.nrc.cadc.net.HttpDownload;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;


public class PreviewServletTest {
    private PreviewServlet testSubject;

    private HttpServletRequest mockRequest =
        createMock(HttpServletRequest.class);
    private HttpServletResponse mockResponse =
        createMock(HttpServletResponse.class);

    @After
    public void tearDown() throws Exception {
        setTestSubject(null);
    }


    @Test
    public void doGet() throws Exception {
        final URL dataServiceURL = new URL("http://localhost/data");

        final HttpDownload mockHTTPDownload = createMock(HttpDownload.class);
        final OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public void write(int i) throws IOException {
                byteArrayOutputStream.write((byte) i);
            }

            /**
             * This method can be used to determine if data can be written without blocking.
             *
             * @return <code>true</code> if a write to this <code>ServletOutputStream</code>
             * will succeed, otherwise returns <code>false</code>.
             * @since Servlet 3.1
             */
            @Override
            public boolean isReady() {
                return true;
            }

            /**
             * Instructs the <code>ServletOutputStream</code> to invoke the provided
             * {@link WriteListener} when it is possible to write
             *
             * @param writeListener the {@link WriteListener} that should be notified
             *                      when it's possible to write
             * @throws IllegalStateException if one of the following conditions is true
             *                               <ul>
             *                               <li>the associated request is neither upgraded nor the async started
             *                               <li>setWriteListener is called more than once within the scope of the same request.
             *                               </ul>
             * @throws NullPointerException  if writeListener is null
             * @since Servlet 3.1
             */
            @Override
            public void setWriteListener(WriteListener writeListener) {

            }
        };

        final byte[] data = new byte[10];

        for (int i = 0; i < data.length; i++) {
            data[i] = '8';
        }

        setTestSubject(new PreviewServlet(dataServiceURL) {
            @Override
            HttpDownload createDownloader(URL url, OutputStream outputStream) {
                return mockHTTPDownload;
            }
        });

        expect(getMockRequest().getPathInfo()).andReturn("/mypath").once();

        mockHTTPDownload.setFollowRedirects(true);
        expectLastCall().once();

        mockHTTPDownload.run();
        expectLastCall().once();

        expect(mockHTTPDownload.getResponseCode()).andReturn(200).once();

        expect(getMockResponse().getOutputStream()).andReturn(outputStream).once();

        replay(getMockRequest(), getMockResponse(), mockHTTPDownload);

        getTestSubject().doGet(getMockRequest(), getMockResponse());

        verify(getMockRequest(), getMockResponse(), mockHTTPDownload);
    }

    @Test
    public void createJobURL() throws Exception {
        final URL dataServiceURL = new URL("http://localhost/data/pub");

        setTestSubject(new PreviewServlet(dataServiceURL));

        expect(getMockRequest().getPathInfo()).andReturn(
            "/COLLECTION/OBSID_PREV_256.JPG").once();

        replay(getMockRequest());

        final URL expectedURL =
            new URL("http", "localhost",
                "/data/pub/COLLECTION/OBSID_PREV_256.JPG");
        final URL url = getTestSubject().createJobURL(getMockRequest());
        assertEquals("Expected URL is not what was generated.", expectedURL,
            url);

        verify(getMockRequest());


        // TEST 2
        reset(getMockRequest());

        setTestSubject(new PreviewServlet(dataServiceURL));

        expect(getMockRequest().getPathInfo()).andReturn(
            "/COLLECTION/OBSID_PREV 256.JPG").once();

        replay(getMockRequest());

        final URL expectedURL2 =
            new URL("http", "localhost",
                "/data/pub/COLLECTION/OBSID_PREV+256.JPG");
        final URL url2 = getTestSubject().createJobURL(getMockRequest());
        assertEquals("Expected URL is not what was generated.", expectedURL2,
            url2);

        verify(getMockRequest());
    }


    public PreviewServlet getTestSubject() {
        return testSubject;
    }

    public void setTestSubject(final PreviewServlet testSubject) {
        this.testSubject = testSubject;
    }

    private HttpServletRequest getMockRequest() {
        return mockRequest;
    }

    private HttpServletResponse getMockResponse() {
        return mockResponse;
    }
}
