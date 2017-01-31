
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
    private ADQLImpl adqlImpl;


    public ObservationListQueryGenerator(final String upload,
                                         final String uploadResolver,
                                         final String targetNameField,
                                         final String targetCoordField)
    {
        this.adqlImpl = new ADQLImpl("caom2", upload, uploadResolver,
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
        sb.append(adqlImpl.getSelectList(selectClauseItems));

        if (adqlImpl.hasUpload())
        {
            sb.append(",f.target,f.ra,f.dec,f.radius");
        }

        sb.append(" FROM ");
        sb.append(adqlImpl.getFrom(Plane.class, 2));

        final List<SearchTemplate> ammendedTemplates =
                new ArrayList<>(templates);
        final List<SearchTemplate> junkFlagTemplates =
                new ArrayList<>(2);

        junkFlagTemplates.add(new IsNull("Plane.quality_flag"));
        junkFlagTemplates.add(new TextSearch("Plane.quality_flag", "junk",
                                             "junk", false, false, true));

        ammendedTemplates.add(new Or(junkFlagTemplates));

        final String where = adqlImpl.getWhere(ammendedTemplates);

        if (StringUtil.hasText(where))
        {
            sb.append(" WHERE ");
            sb.append(where);
        }

        return sb;
    }
}
