/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.nrc.cadc.caom2;

import java.util.List;


/**
 * Generate an ADQL query for a ObsCore list.
 *
 * @author pdowler
 */
public class ObsCoreListQueryGenerator implements SearchTemplateQueryGenerator
{
    private ADQLGenerator adqlGenerator;

    ObsCoreListQueryGenerator(final String upload,
                              final String uploadResolver,
                              final String targetNameField,
                              final String targetCoordField)
    {
        this.adqlGenerator = new ADQLGenerator("ivoa.obscore", upload,
                                               uploadResolver, targetNameField,
                                               targetCoordField);
    }

    /**
     * Generate an ADQL query with custom select-list.
     *
     * @param templates    The Templates object.
     * @param selectClause The Select list.
     * @return String SQL.
     */
    @Override
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates,
                                      final String selectClause)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(adqlGenerator.getSelectList(selectClause));
        sb.append(" FROM ");
        sb.append(adqlGenerator.getSchema());
        sb.append(" ");

        final String where = adqlGenerator.getWhere(templates);

        if (where != null)
        {
            sb.append(" WHERE ");
            sb.append(where);
        }

        return sb;
    }
}
