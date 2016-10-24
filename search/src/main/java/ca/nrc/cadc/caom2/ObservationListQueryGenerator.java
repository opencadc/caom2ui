
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
     * Obtain the Select clause for the SQL query.
     *
     * @param templates         List of SearchTemplate objects.
     * @return complete ADQL query on CAOM Observation(s) at depth=2
     */
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates)
    {
        return getSelectSQL(templates,
                            adqlImpl.getObservationSelectList().toString());
    }

    /**
     * Generate an ADQL query with custom select-list.
     *
     * @param templates         The Templates object.
     * @param selectList        The Select list.
     * @return                  String SQL.
     */
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates,
                                      final String selectList)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(adqlImpl.getSelectList(selectList));

        if (adqlImpl.hasUpload())
        {
            sb.append(",f.target,f.ra,f.dec,f.radius");
        }

        sb.append(" FROM ");
        sb.append(adqlImpl.getFrom(Plane.class, 2));

        final List<SearchTemplate> ammendedTemplates =
                new ArrayList<SearchTemplate>(templates);
        final List<SearchTemplate> junkFlagTemplates =
                new ArrayList<SearchTemplate>(2);

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
