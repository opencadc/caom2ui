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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.form.*;
import ca.nrc.cadc.util.Log4jInit;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


/**
 * @author jburke
 */
public class FormValidationServletTest
        extends AbstractUnitTest<FormValidationServlet> {

    private static final String CAOM2_TIME_FIELD = "Plane.time.bounds.samples";
    private static final String CAOM2_ENERGY_FIELD = "Plane.energy.bounds.samples";

    private HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
    private HttpServletResponse mockResponse = createMock(HttpServletResponse.class);

    @BeforeClass
    public static void setUpClass() {
        Log4jInit.setLevel("ca.nrc.cadc", Level.INFO);
    }

    @Test
    public void testDoGet() throws Exception {
        testSubject = new FormValidationServlet() {
            protected FormErrors getFormErrors(final Map<String, String[]> parameters) {
                return new FormErrors();
            }
        };
        testSubject.init();

        final Writer sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        expect(mockRequest.getParameterMap()).andReturn(new HashMap<String, String[]>()).once();

        mockResponse.setStatus(200);
        expectLastCall().once();

        mockResponse.setContentType("application/json");
        expectLastCall().once();

        mockResponse.setHeader("Cache-Control", "no-cache");
        expectLastCall().once();

        expect(mockResponse.getWriter()).andReturn(pw).once();

        replay(mockRequest, mockResponse);

        testSubject.doGet(mockRequest, mockResponse);

        assertEquals("Response JSON should be an empty object", "{}", sw.toString());

        verify(mockRequest, mockResponse);
    }

    @Test
    public void testGetFormErrors() throws Exception {
        testSubject = new FormValidationServlet();
        testSubject.init();

        Map<String, String[]> parameters = new HashMap<>();

        FormErrors formErrors;
        try {
            testSubject.getFormErrors(parameters);
            fail("Missing field parameter should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
            // Good.
        }

        parameters.put("field", new String[] {"foo"});
        parameters.put("foo", new String[] {"bar", "baz"});
        try {
            testSubject.getFormErrors(parameters);
            fail("Multiple utype values should throw ServletException");
        } catch (ServletException ignore) {
        }

        parameters.clear();

        parameters.put("field", new String[] {"Observation.observationID"});
        parameters.put("Observation.observationID", new String[] {"bar"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should be empty", formErrors.get().isEmpty());

        parameters.put("field", new String[] {"Plane.position.sampleSize"});
        parameters.put("Plane.position.sampleSize", new String[] {"1"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should be empty", formErrors.get().isEmpty());

        parameters.put("field", new String[] {CAOM2_TIME_FIELD});
        parameters.put(CAOM2_TIME_FIELD, new String[] {"2013-10-02"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should be empty", formErrors.get().isEmpty());

        parameters.put("field", new String[] {CAOM2_ENERGY_FIELD});
        parameters.put(CAOM2_ENERGY_FIELD, new String[] {"1.0"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should be empty", formErrors.get().isEmpty());

        parameters.clear();

        parameters.put("field", new String[] {"Plane.position.sampleSize"});
        parameters.put("Plane.position.sampleSize", new String[] {"qwerty"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should not be empty",
                   formErrors.get().size() > 0);

        parameters.put("field", new String[] {CAOM2_TIME_FIELD});
        parameters.put(CAOM2_TIME_FIELD, new String[] {"qwerty"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should not be empty",
                   formErrors.get().size() > 0);

        parameters.put("field", new String[] {CAOM2_ENERGY_FIELD});
        parameters.put(CAOM2_ENERGY_FIELD, new String[] {"qwerty"});
        formErrors = testSubject.getFormErrors(parameters);
        assertNotNull(formErrors);
        assertTrue("FormErrors should not be empty",
                   formErrors.get().size() > 0);
    }

    @Test
    public void testGetFormConstraint() throws Exception {
        testSubject = new FormValidationServlet();
        testSubject.init();

        FormConstraint form = testSubject.getFormConstraint(null, null);
        assertNull(form);

        form = testSubject.getFormConstraint("foo", null);
        assertNull(form);

        form = testSubject.getFormConstraint(null, "bar");
        assertNull(form);

        form = testSubject.getFormConstraint("foo", "bar");
        assertNull(form);

        form = testSubject
                .getFormConstraint("Observation.observationID", null);
        assertNotNull(form);
        assertTrue("", (form instanceof Text));

        form = testSubject
                .getFormConstraint("Plane.position.sampleSize", null);
        assertNotNull(form);
        assertTrue("", (form instanceof ca.nrc.cadc.search.form.Number));

        form = testSubject.getFormConstraint(CAOM2_TIME_FIELD, null);
        assertNotNull(form);
        assertTrue("", (form instanceof ca.nrc.cadc.search.form.Date));

        form = testSubject.getFormConstraint(CAOM2_ENERGY_FIELD, null);
        assertNotNull(form);
        assertTrue("", (form instanceof Energy));
    }

    @Test
    public void testWriteFormErrors() throws Exception {
        testSubject = new FormValidationServlet();
        testSubject.init();

        String expected = "{\"utype1\":\"error1\",\"utype2\":\"error2\"}";

        FormErrors formErrors = new FormErrors();
        formErrors.set("utype1", new FormError("utype1", "error1"));
        formErrors.set("utype2", new FormError("utype2", "error2"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        testSubject.writeFormErrors(formErrors, pw);
        String actual = sw.toString();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
