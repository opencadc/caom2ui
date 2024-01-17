
package ca.nrc.cadc.uws;

import ca.nrc.cadc.ac.ACIdentityManager;
import ca.nrc.cadc.auth.IdentityManager;
import ca.nrc.cadc.uws.server.DatabaseJobPersistence;
import ca.nrc.cadc.uws.server.JobExecutor;
import ca.nrc.cadc.uws.server.SimpleJobManager;
import ca.nrc.cadc.uws.server.SyncJobExecutor;
import ca.nrc.cadc.uws.server.impl.PostgresJobPersistence;


/**
 * Job Manager implementation to handle searching of the Persistence Layer.
 *
 * @author pdowler
 */
public class SearchJobManager extends SimpleJobManager {
    private static final long MAX_EXEC_DURATION = 600L;
    private static final long MAX_DESTRUCTION = 7L * 24L * 3600L; // 1 week
    private static final long MAX_QUOTE = 600L; // same as exec since we don't queue

    public SearchJobManager() {
        super();

        final DatabaseJobPersistence jobPersistence = createJobPersistence();
        final JobExecutor jobExecutor = new SyncJobExecutor(jobPersistence, AdvancedRunner.class);

        super.setMaxExecDuration(MAX_EXEC_DURATION);
        super.setMaxDestruction(MAX_DESTRUCTION);
        super.setMaxQuote(MAX_QUOTE);
        super.setJobExecutor(jobExecutor);
        super.setJobPersistence(jobPersistence);
    }

    IdentityManager createIdentityManager() {
        return new ACIdentityManager();
    }

    /**
     * Override as needed.
     *
     * @return DatabasePersistence instance.
     */
    DatabaseJobPersistence createJobPersistence() {
        return new PostgresJobPersistence(createIdentityManager());
    }
}
