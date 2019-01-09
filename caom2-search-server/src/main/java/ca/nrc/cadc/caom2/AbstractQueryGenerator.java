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
 * 10/15/13 - 3:20 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;

import ca.nrc.cadc.search.QueryGenerator;
import ca.nrc.cadc.search.upload.UploadResults;
import ca.nrc.cadc.tap.SyncTAPClient;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.ParameterUtil;

import java.util.List;


public abstract class AbstractQueryGenerator implements QueryGenerator
{
    private final Job job;


    AbstractQueryGenerator(final Job job)
    {
        this.job = job;
    }


    String getSelectClause()
    {
        return getParameterValue("SelectList");
    }

    protected String getUpload()
    {
        return getParameterValue(SyncTAPClient.UPLOAD_JOB_PARAMETER_NAME);
    }

    String getUploadResolver()
    {
        return getParameterValue(UploadResults.UPLOAD_RESOLVER);
    }

    private String getParameterValue(final String key)
    {
        return ParameterUtil.findParameterValue(key, getJob().getParameterList());
    }

    StringBuilder generate(final SearchTemplateQueryGenerator queryGenerator,
                           final List<SearchTemplate> searchTemplates)
    {
        return queryGenerator.getSelectSQL(searchTemplates, getSelectClause());
    }

    protected Job getJob()
    {
        return job;
    }
}
