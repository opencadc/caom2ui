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
package ca.nrc.cadc.tap.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.Templates;
import ca.nrc.cadc.search.form.FormConstraint;
import ca.nrc.cadc.search.form.SearchableFormConstraint;
import ca.nrc.cadc.search.form.Text;
import ca.nrc.cadc.tap.SyncTAPClient;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;
import ca.nrc.cadc.uws.Result;
import ca.nrc.cadc.uws.SyncResponseWriter;

import org.junit.Test;
import static org.easymock.EasyMock.*;



public class TAPSearcherImplTest extends AbstractUnitTest<TAPSearcherImpl>
{
    private final SyncResponseWriter mockSyncResponseWriter =
            createMock(SyncResponseWriter.class);
    private final QueryGenerator mockQueryGenerator =
            createMock(QueryGenerator.class);


    @Test
    public void constructor() throws Exception
    {
        final Job mockJob = createMock(Job.class);
        replay(mockJob, mockSyncResponseWriter);

        setTestSubject(new TAPSearcherImpl(mockSyncResponseWriter, null,
                                           URI.create("ivo://tap/place"),
                                           mockQueryGenerator));
        verify(mockJob, mockSyncResponseWriter);
    }

    @Test
    public void search() throws Exception
    {
        final List<Parameter> parameters = new ArrayList<>();
        final Writer writer = new StringWriter();

        parameters.add(new Parameter("LANG", "ADQL"));
        parameters.add(new Parameter("FORMAT", "votable"));
        parameters.add(new Parameter("QUERY",
                                     "SELECT * FROM TABLE WHERE UTYPE1 = VAL1"));
        parameters.add(new Parameter("REQUEST", "doQuery"));
        parameters.add(new Parameter("MAXREC", "11000"));

        final Job dummyJob = new Job();

        dummyJob.setParameterList(parameters);

        final List<SearchableFormConstraint> constraints = new ArrayList<>();

        constraints.add(new Text("Observation.collection", "VAL1", true));

        final SyncTAPClient mockTAPClient = createMock(SyncTAPClient.class);
        final Templates templates = new Templates(constraints);
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SELECT * FROM TABLE WHERE UTYPE1 = VAL1");

        setTestSubject(new TAPSearcherImpl(mockSyncResponseWriter, null,
                                           URI.create("ivo://tap/place"),
                                           mockQueryGenerator)
        {
            /**
             * Obtain an appropriate TAP Client instance.
             *
             * @param registryClient An initialized Registry Client.
             * @param outputStream   The stream to write out the redirect URL to.
             * @param tapJob         The Job to execute.
             * @return TapClient instance.  Never null.
             */
            @Override
            SyncTAPClient getTAPClient(RegistryClient registryClient,
                                       OutputStream outputStream,
                                       Job tapJob)
            {
                try
                {
                    outputStream.write("http://results.com/go/run".getBytes());
                }
                catch (IOException e)
                {
                    // Oh well...
                }

                return mockTAPClient;
            }
        });

        final Job tapJob = new Job(dummyJob)
        {

            /**
             * Why must I override equals here?  Why does the Job class not
             * already have one?
             *
             * @param obj
             * @return
             */
            @Override
            public boolean equals(Object obj)
            {
                return dummyJob.getParameterList().equals(((Job) obj).
                        getParameterList());
            }
        };
        tapJob.setResultsList(null);

        mockTAPClient.execute(tapJob);
        expectLastCall().once();

        expect(mockQueryGenerator.generate(templates))
                .andReturn(stringBuilder).once();

        expect(mockSyncResponseWriter.getWriter()).andReturn(writer).times(2);

        mockSyncResponseWriter.setResponseHeader("Content-Type",
                                                 "application/json");
        expectLastCall().once();

        replay(mockSyncResponseWriter, mockQueryGenerator, mockTAPClient);
        getTestSubject().search(dummyJob);
        verify(mockSyncResponseWriter, mockQueryGenerator, mockTAPClient);
    }
}
