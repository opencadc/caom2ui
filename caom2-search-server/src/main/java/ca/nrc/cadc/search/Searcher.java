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
 * 10/15/13 - 9:15 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.SyncResponseWriter;
import ca.nrc.cadc.uws.server.JobUpdater;

import java.net.URI;

public interface Searcher
{
    Integer DEFAULT_MAXREC = 11000;


    /**
     * Execute the search, and write out the results to this implementation's
     * writer.
     *
     * @param job         The Job to execute.
     * @param serviceURI  The Service URI to use.
     * @param jobUpdater    The JobUpdater to use.
     * @param syncResponseWriter        The writer to write to.
     * @throws Exception    Any unforeseen errors.
     */
    void search(final Job job, final URI serviceURI, final JobUpdater jobUpdater,
                final SyncResponseWriter syncResponseWriter) throws Exception;
}
