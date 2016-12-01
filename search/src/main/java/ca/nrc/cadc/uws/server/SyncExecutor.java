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
 * 6/11/12 - 9:13 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.uws.server;

import ca.nrc.cadc.uws.Job;


public class SyncExecutor extends AbstractExecutor
{
    /**
     * Complete constructor.
     *
     * @param jobUpdater            The Job persistence object.
     * @param jobRunnerClass        The Class to instantiate to run the Job.
     */
    public SyncExecutor(final JobUpdater jobUpdater,
                        final Class<? extends JobRunner> jobRunnerClass)
    {
        super(jobUpdater, jobRunnerClass);
    }


    /**
     * Execute the job asynchronously.
     *
     * @param job           The Job to execute.
     * @param jobRunner     The runner class to run the Job.
     */
    @Override
    protected void executeAsync(final Job job, final JobRunner jobRunner)
    {
        throw new IllegalStateException("Asynchronous access prohibited.  "
                                        + "Use the ThreadExecutor instead.");
    }

    @Override
    protected void abortJob(final String jobID)
    {
        throw new IllegalStateException("Asynchronous access prohibited.  "
                                        + "Use the ThreadExecutor instead.");
    }
}
