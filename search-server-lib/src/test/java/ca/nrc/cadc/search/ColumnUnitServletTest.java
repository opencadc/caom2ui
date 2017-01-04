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
 * 1/11/12 - 11:38 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.StringWriter;
import java.io.Writer;

import ca.nrc.cadc.AbstractUnitTest;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class ColumnUnitServletTest extends AbstractUnitTest<ColumnUnitServlet>
{
    private HttpServletRequest mockRequest =
            createMock(HttpServletRequest.class);
    private HttpServletResponse mockResponse =
            createMock(HttpServletResponse.class);


    @Test
    public void writeJSON() throws Exception
    {
        setTestSubject(new ColumnUnitServlet());
        getTestSubject().init();

        final Writer writer1 = new StringWriter();

        getTestSubject().writeJSON(writer1, null);

        writer1.flush();
        writer1.close();

        assertEquals("Output should be empty.", "[]", writer1.toString());


        // TEST 2
        final Writer writer2 = new StringWriter();
        getTestSubject().writeJSON(writer2, new String[]{ "NO_SUCH_COLUMN" });

        writer2.flush();
        writer2.close();

        assertEquals("Output should be empty.",
                     "[{\"column\":\"NO_SUCH_COLUMN\",\"units\":[]}]",
                     writer2.toString());


        // TEST 3
        final Writer writer3 = new StringWriter();
        getTestSubject().writeJSON(writer3, new String[]{ "Integration Time" });

        writer3.flush();
        writer3.close();

        assertEquals("Output should have two items for Integration Time.",
                     "[{\"column\":\"Integration Time\","
                     + "\"units\":[{\"displayValue\":\"Seconds\",\"value\":\"SECONDS\"},"
                     + "{\"displayValue\":\"Minutes\",\"value\":\"MINUTES\"},"
                     + "{\"displayValue\":\"Hours\",\"value\":\"HOURS\"}]}]",
                     writer3.toString());


        // TEST 4
        final Writer writer4 = new StringWriter();
        getTestSubject().writeJSON(writer4, new String[]{ "RA (J2000.0)" });

        writer4.flush();
        writer4.close();

        assertEquals("Output should have two items for RA.",
                     "[{\"column\":\"RA (J2000.0)\","
                     + "\"units\":[{\"displayValue\":\"H:M:S\",\"value\":\"SEXAGESIMAL\"},"
                     + "{\"displayValue\":\"Degrees\",\"value\":\"DEGREES\"}]}]",
                     writer4.toString());



        // TEST 5
        final Writer writer5 = new StringWriter();
        getTestSubject().writeJSON(writer5, new String[]{ "Dec. (J2000.0)" });

        writer5.flush();
        writer5.close();

        assertEquals("Output should have two items for Dec..",
                     "[{\"column\":\"Dec. (J2000.0)\","
                     + "\"units\":[{\"displayValue\":\"D:M:S\",\"value\":\"SEXAGESIMAL\"},"
                     + "{\"displayValue\":\"Degrees\",\"value\":\"DEGREES\"}]}]",
                     writer5.toString());
    }


    public HttpServletRequest getMockRequest()
    {
        return mockRequest;
    }

    public HttpServletResponse getMockResponse()
    {
        return mockResponse;
    }
}
