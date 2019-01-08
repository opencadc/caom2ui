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
 * 10/15/13 - 9:17 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.tap;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.nrc.cadc.search.FormData;
import ca.nrc.cadc.search.Templates;
import ca.nrc.cadc.search.form.SearchableFormConstraint;
import ca.nrc.cadc.search.form.Text;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.SyncResponseWriter;

import org.json.JSONWriter;
import org.junit.Test;

import static org.easymock.EasyMock.*;


public class TAPSearcherTest extends AbstractUnitTest<TAPSearcher>
{
    private final SyncResponseWriter mockSyncResponseWriter = createMock(SyncResponseWriter.class);
    private final QueryGenerator mockQueryGenerator = createMock(QueryGenerator.class);
    private final SyncTAPClient mockSyncTAPClient = createMock(SyncTAPClient.class);


    @Test
    public void constructor()
    {
        final Job mockJob = createMock(Job.class);
        replay(mockJob, mockSyncResponseWriter, mockSyncTAPClient);

        setTestSubject(new TAPSearcher(mockSyncResponseWriter, mockSyncTAPClient, mockQueryGenerator));
        verify(mockJob, mockSyncResponseWriter, mockSyncTAPClient);
    }

    @Test
    public void search() throws Exception
    {
        final FormData mockFormData = createMock(FormData.class);
        final List<Parameter> parameters = new ArrayList<>();
        final Writer writer = new StringWriter();

        parameters.add(new Parameter("LANG", "ADQL"));
        parameters.add(new Parameter("FORMAT", "votable"));
        parameters.add(new Parameter("QUERY", "SELECT * FROM TABLE WHERE UTYPE1 = VAL1"));
        parameters.add(new Parameter("REQUEST", "doQuery"));
        parameters.add(new Parameter("MAXREC", "11000"));

        final Job dummyJob = new Job();

        dummyJob.setParameterList(parameters);

        final List<SearchableFormConstraint> constraints = new ArrayList<>();

        constraints.add(new Text("Observation.collection", "VAL1", true));

        final Templates templates = new Templates(constraints);
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT * FROM TABLE WHERE UTYPE1 = VAL1");

        setTestSubject(new TAPSearcher(mockSyncResponseWriter, mockSyncTAPClient, mockQueryGenerator)
        {
            /**
             * Issue a TAP query.
             *
             * @param tapJob       The TAP job to execute.
             * @param outputStream The stream to write out results to.
             * @throws IOException Any writing errors.
             */
            @Override
            void queryTAP(final URI serviceURI, Job tapJob,
                          OutputStream outputStream) throws IOException
            {
                outputStream.write("http://mysite.com/tap/jobs/88/run".getBytes());
            }
        });

        final Job tapJob = new Job(dummyJob)
        {
            /**
             * Why must I override equals here?  Why does the Job class not
             * already have one?
             */
            @Override
            public boolean equals(Object obj)
            {
                return (obj instanceof Job)
                       && dummyJob.getParameterList().equals(((Job) obj).getParameterList());
            }
        };
        tapJob.setResultsList(null);

        expect(mockQueryGenerator.generate(templates))
                .andReturn(stringBuilder).once();

        final JSONWriter jsonWriter = new JSONWriter(writer);

        expect(mockFormData.getFormConstraints()).andReturn(constraints).once();
        expect(mockFormData.getFormValueUnits()).andReturn(new HashMap<String, String>());

        replay(mockFormData, mockQueryGenerator, mockSyncTAPClient);

        jsonWriter.object();
        getTestSubject().runSearch(URI.create("ivo://mysite.com/service"), jsonWriter, dummyJob, null,
                                   mockFormData);
        jsonWriter.endObject();

        verify(mockFormData, mockQueryGenerator, mockSyncTAPClient);
    }
}
