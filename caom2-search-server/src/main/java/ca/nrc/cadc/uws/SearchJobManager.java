
package ca.nrc.cadc.uws;

import ca.nrc.cadc.uws.server.SimpleJobManager;


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

        super.setMaxExecDuration(MAX_EXEC_DURATION);
        super.setMaxDestruction(MAX_DESTRUCTION);
        super.setMaxQuote(MAX_QUOTE);
    }
}
