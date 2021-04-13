
package ca.nrc.cadc.caom2;

import java.util.ArrayList;
import java.util.List;

import ca.nrc.cadc.util.StringUtil;


/**
 * Generate an ADQL query for a list of observation-join-plane.
 *
 * @author pdowler
 */
public class ObservationListQueryGenerator
        implements SearchTemplateQueryGenerator
{
    private final ADQLGenerator adqlGenerator;


    public ObservationListQueryGenerator(final String upload,
                                         final String uploadResolver,
                                         final String targetNameField,
                                         final String targetCoordField)
    {
        this.adqlGenerator = new ADQLGenerator("caom2", upload, uploadResolver,
                                               targetNameField, targetCoordField);
    }

    /**
     * Generate an ADQL query with custom select-list.
     *
     * @param templates    The Templates object.
     * @param selectClauseItems The Select column list (Sans SELECT keyword).
     * @return String SQL.
     */
    @Override
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates,
                                      final String selectClauseItems)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(adqlGenerator.getSelectList(selectClauseItems));

        sb.append(" FROM ");
        sb.append(adqlGenerator.getFrom(Plane.class, 2));

        final List<SearchTemplate> ammendedTemplates =
                new ArrayList<>(templates);
        final List<SearchTemplate> junkFlagTemplates =
                new ArrayList<>(2);

        junkFlagTemplates.add(new IsNull("Plane.quality_flag"));
        junkFlagTemplates.add(new TextSearch("Plane.quality_flag", "junk",
                                             "junk", false, false, true));

        ammendedTemplates.add(new Or(junkFlagTemplates));

        final String where = adqlGenerator.getWhere(ammendedTemplates);

        if (StringUtil.hasText(where))
        {
            sb.append(" WHERE ");
            sb.append(where);
        }

        return sb;
    }
}
