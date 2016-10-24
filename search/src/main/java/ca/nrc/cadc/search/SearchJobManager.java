
package ca.nrc.cadc.search;

import ca.nrc.cadc.auth.ACIdentityManager;

import ca.nrc.cadc.uws.server.DatabaseJobPersistence;
import ca.nrc.cadc.uws.server.JobExecutor;
import ca.nrc.cadc.uws.server.SimpleJobManager;
import ca.nrc.cadc.uws.server.SyncExecutor;
import ca.nrc.cadc.uws.server.impl.PostgresJobPersistence;


/**
 * Job Manager implementation to handle searching of the Persistence Layer.
 *
 * @author pdowler
 */
public class SearchJobManager extends SimpleJobManager
{
    private static final long MAX_EXEC_DURATION = 600L;
    private static final long MAX_DESTRUCTION = 7L * 24L * 3600L; // 1 week
    private static final long MAX_QUOTE = 600L; // same as exec since we don't queue

    public SearchJobManager()
    {
        super();

        // Persist UWS jobs to PostgreSQL: HACK: hard-coded runtime config now
        // that PostgresJobPersistence is used in multiple back-end servers
        // with different config.
        final DatabaseJobPersistence jobPersist =
                new PostgresJobPersistence(new ACIdentityManager());
        final JobExecutor jobExec = new SyncExecutor(jobPersist,
                                                     AdvancedRunner.class)
        {

        };

        super.setJobPersistence(jobPersist);
        super.setJobExecutor(jobExec);
        super.setMaxExecDuration(MAX_EXEC_DURATION);
        super.setMaxDestruction(MAX_DESTRUCTION);
        super.setMaxQuote(MAX_QUOTE);
    }
}
