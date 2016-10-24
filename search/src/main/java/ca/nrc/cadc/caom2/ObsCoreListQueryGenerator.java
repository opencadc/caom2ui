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
public class ObsCoreListQueryGenerator
{
    private ADQLImpl adqlImpl;

    public ObsCoreListQueryGenerator(final String upload,
                                     final String uploadResolver,
                                     final String targetNameField,
                                     final String targetCoordField)
    {
        this.adqlImpl = new ADQLImpl("ivoa.obscore", upload, uploadResolver,
                                     targetNameField, targetCoordField);
    }

    /**
     * Obtain the Select clause for the SQL query.
     *
     * @param templates List of SearchTemplate objects.
     * @return complete ADQL query on CAOM Observation(s) at depth=2
     */
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates)
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("access_estsize,");
        sb.append("access_format,");
        sb.append("access_url,");
        sb.append("calib_level,");
        sb.append("dataproduct_type,");
        sb.append("em_max,");
        sb.append("em_min,");
        sb.append("em_res_power,");
        sb.append("facility_name,");
        sb.append("instrument_name,");
        sb.append("obs_collection,");
        sb.append("obs_id,");
        sb.append("obs_publisher_did,");
        sb.append("obs_release_date,");
        sb.append("o_ucd,");
        sb.append("pol_states,");
        sb.append("s_dec,");
        sb.append("s_fov,");
        sb.append("s_ra,");
        sb.append("s_region,");
        sb.append("s_resolution,");
        sb.append("target_name,");
        sb.append("t_exptime,");
        sb.append("t_max,");
        sb.append("t_min,");
        sb.append("t_resolution");

        return getSelectSQL(templates, sb.toString());
    }

    /**
     * Generate an ADQL query with custom select-list.
     *
     * @param templates  The Templates object.
     * @param selectList The Select list.
     * @return String SQL.
     */
    public StringBuilder getSelectSQL(final List<SearchTemplate> templates,
                                      final String selectList)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(adqlImpl.getSelectList(selectList));
        sb.append(" FROM ");
        sb.append(adqlImpl.getSchema());
        sb.append(" ");

        final String where = adqlImpl.getWhere(templates);

        if (where != null)
        {
            sb.append(" WHERE ");
            sb.append(where);
        }

        return sb;
    }

    /**
     * Generate query components that can be augmented manually.
     *
     * @param templates         The Templates object.
     * @param selectList        The Select list.
     * @return                  QueryParts DTO.
     */
    public QueryParts getQueryParts(final List<SearchTemplate> templates,
                                    final String selectList)
    {
        final QueryParts ret = new QueryParts();

        ret.selectList = adqlImpl.getSelectList(selectList);
        ret.fromClause = " " + adqlImpl.getSchema() + " ";
        ret.whereClause = adqlImpl.getWhere(templates);

        return ret;
    }

    public static class QueryParts
    {
        public String selectList;
        public String fromClause;
        public String whereClause;
    }

}
