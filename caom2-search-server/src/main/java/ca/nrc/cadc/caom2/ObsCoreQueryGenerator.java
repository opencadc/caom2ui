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
 * 10/15/13 - 3:15 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;

import ca.nrc.cadc.search.Templates;
import ca.nrc.cadc.uws.Job;


/**
 * Query generator for ObsCore fields.
 */
public class ObsCoreQueryGenerator extends AbstractQueryGenerator
{
    /**
     * Only available constructor.
     *
     * @param job       The job being submitted.
     */
    public ObsCoreQueryGenerator(final Job job)
    {
        super(job);
    }

    /**
     * Create a query for the given items.
     *
     * @param templates The Search templates from the Form.
     * @return StringBuilder of the query, or empty
     *         StringBuilder.
     */
    @Override
    public StringBuilder generate(final Templates templates)
    {
        final ObsCoreListQueryGenerator queryGenerator =
                new ObsCoreListQueryGenerator(getUpload(), getUploadResolver(), "target_name",
                                              "s_fov");

        return generate(queryGenerator, templates.getSearchTemplates());
    }
}
