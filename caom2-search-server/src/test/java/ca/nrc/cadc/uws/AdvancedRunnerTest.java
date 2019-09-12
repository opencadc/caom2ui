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
 * 12/12/13 - 1:09 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.uws;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.TransientException;
import ca.nrc.cadc.rest.SyncOutput;
import ca.nrc.cadc.search.Searcher;
import ca.nrc.cadc.uws.server.JobUpdater;


import org.junit.Test;

import static org.easymock.EasyMock.*;


public class AdvancedRunnerTest extends AbstractUnitTest<AdvancedRunner> {
    private static final List<Parameter> EMPTY_PARAMETER_LIST = Collections.emptyList();
    private final Job mockJob = createMock(Job.class);
    private final JobUpdater mockJobUpdater = createMock(JobUpdater.class);
    private final SyncOutput mockSyncOutput = createMock(SyncOutput.class);
    private final Searcher mockSearcher = createMock(Searcher.class);


    @Test
    public void runOK() throws Throwable {
        final SyncResponseWriter mockSyncResponseWriter = createMock(SyncResponseWriter.class);
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        cal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new AdvancedRunner() {
            /**
             * Obtain the current date.  Implementors can override.
             *
             * @return Date instance.
             */
            @Override
            protected Date currentDate() {
                return cal.getTime();
            }

            @Override
            protected SyncResponseWriter wrapSyncOutput() {
                return mockSyncResponseWriter;
            }

            /**
             * Create a default searcher instance when none exists.  This relies on the
             * setters to populate this class first (Or whatever the isInitialized()
             * method returns).
             *
             * @return Searcher instance
             */
            @Override
            Searcher createSearcher() {
                return getMockSearcher();
            }
        });

        expect(getMockJob().getID()).andReturn("88").once();
        expect(getMockJobUpdater().setPhase("88", ExecutionPhase.QUEUED,
                                            ExecutionPhase.EXECUTING,
                                            cal.getTime())).andReturn(
            ExecutionPhase.EXECUTING).once();
        expect(getMockJob().getParameterList()).andReturn(EMPTY_PARAMETER_LIST).times(2);

        getMockSearcher().search(getMockJob(), AdvancedRunner.DEFAULT_TAP_SERVICE_URI, getMockJobUpdater(),
                                 mockSyncResponseWriter);
        expectLastCall().once();

        expect(getMockJobUpdater().setPhase("88", ExecutionPhase.EXECUTING, ExecutionPhase.COMPLETED,
                                            cal.getTime())).andReturn(ExecutionPhase.COMPLETED).once();

        expect(getMockSyncOutput().getOutputStream()).andReturn(new ByteArrayOutputStream()).once();

        replay(getMockJob(), getMockJobUpdater(), getMockSyncOutput(), getMockSearcher(), mockSyncResponseWriter);

        getTestSubject().setJob(getMockJob());
        getTestSubject().setJobUpdater(getMockJobUpdater());
        getTestSubject().setSyncOutput(getMockSyncOutput());

        getTestSubject().run();

        verify(getMockJob(), getMockJobUpdater(), getMockSyncOutput(), getMockSearcher(), mockSyncResponseWriter);
    }

    @Test
    public void runInternalServerError() throws Throwable {
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        cal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new AdvancedRunner() {
            /**
             * Obtain the current date.  Implementors can override.
             *
             * @return Date instance.
             */
            @Override
            protected Date currentDate() {
                return cal.getTime();
            }
        });

        expect(getMockJob().getID()).andReturn("88").times(2);
        expect(getMockJob().getParameterList()).andReturn(EMPTY_PARAMETER_LIST).once();
        expect(getMockJobUpdater().setPhase("88", ExecutionPhase.QUEUED,
                                            ExecutionPhase.EXECUTING,
                                            cal.getTime())).andThrow(
            new TransientException("1")).once();

        getMockSyncOutput().setCode(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        expectLastCall().once();

        // Need to compensate for lack of equals in ErrorSummary.
        final ErrorSummary errorSummary =
            new ErrorSummary("1", ErrorType.TRANSIENT) {
                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    } else {
                        final ErrorSummary es = (ErrorSummary) obj;

                        return (es.getErrorType() == getErrorType())
                            && es.getSummaryMessage().equals(
                            getSummaryMessage());
                    }
                }
            };

        expect(getMockJobUpdater().setPhase("88", ExecutionPhase.EXECUTING,
                                            ExecutionPhase.ERROR, errorSummary,
                                            cal.getTime())).andThrow(
            new TransientException("2")).once();

        getMockSyncOutput().setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        expectLastCall().once();

        expect(getMockSyncOutput().getOutputStream()).andReturn(new ByteArrayOutputStream()).once();
        replay(getMockJob(), getMockJobUpdater(), getMockSyncOutput(), getMockSearcher());
        getTestSubject().setJob(getMockJob());
        getTestSubject().setJobUpdater(getMockJobUpdater());
        getTestSubject().setSyncOutput(getMockSyncOutput());
        getTestSubject().run();
        verify(getMockJob(), getMockJobUpdater(), getMockSyncOutput(), getMockSearcher());
    }


    private Job getMockJob() {
        return mockJob;
    }

    private JobUpdater getMockJobUpdater() {
        return mockJobUpdater;
    }

    private SyncOutput getMockSyncOutput() {
        return mockSyncOutput;
    }

    private Searcher getMockSearcher() {
        return mockSearcher;
    }
}
