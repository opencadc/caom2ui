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
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;


public class ObsCoreQueryGeneratorImpl extends AbstractQueryGeneratorImpl
{
    public ObsCoreQueryGeneratorImpl(final Job job)
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
        final StringBuilder query;
        final String customSelectList = getCustomSelectList();
        final String upload = getUpload();

        final ObsCoreListQueryGenerator queryGenerator =
                new ObsCoreListQueryGenerator(upload, getUploadResolver(),
                                              "target_name", "s_fov");

        /*if (StringUtil.hasText(upload))
        {
            query = new StringBuilder(256);
            final String uploadResolver = getUploadResolver();

            // Parse the table name from the UPLOAD parameter.
            final String table = upload.split(",")[0];
            final ObsCoreListQueryGenerator.QueryParts parts =
                    queryGenerator.getQueryParts(templates.getSearchTemplates(),
                                                 customSelectList);

            query.append("SELECT ");
            query.append(parts.selectList);
            query.append(", f.target, f.ra, f.dec, f.radius FROM ");
            query.append(parts.fromClause);
            query.append(" JOIN TAP_UPLOAD.");
            query.append(table);
            query.append(" as f on ");

            if (StringUtil.hasText(uploadResolver)
                && uploadResolver.equals("OBJECT"))
            {
                query.append("target_name = f.target ");
            }
            else
            {
                query.append("INTERSECTS(POINT('ICRS',f.ra,f.dec), ");
                query.append("s_fov) = 1 ");
            }

            if (parts.whereClause != null)
            {
                query.append(" WHERE ");
                query.append(parts.whereClause);
            }

        }
        else*/ if (customSelectList != null)
        {
            query = queryGenerator.getSelectSQL(
                    templates.getSearchTemplates(),
                    customSelectList);
        }
        else
        {
            query = queryGenerator.getSelectSQL(
                    templates.getSearchTemplates());
        }

        return query;
    }
}
