
package ca.nrc.cadc.caom2;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collection;
import java.util.List;


/**
 *
 * @author pdowler
 */
public interface PersistenceService 
{
    /**
     * Obtain the schema to use for further use.
     *
     * @return      String schema.
     */
    String getSchema();

    /**
     * Close all resources (e.g. DataSource(s), File(s), etc).
     */
    void close();

    // query-building support methods
    String literal(Object obj);
    String getTable(Class<?> c);
    String getColumnName(String utype);
    String getFrom(Class<?> c, int depth);
    String getWhere(final List<SearchTemplate> constraints);

    /**
     * Query for results, and dump them into the given results.
     *
     * @param sql           The SQL to execute.
     * @param rse           The ResultSetExtractor to use to pull out results.
     * @param results       The Collection of results to use.
     * @param <T>           The data type of results to use.
     */
    <T> void query(final String sql, final ResultSetExtractor<Collection<T>> rse,
                   final Collection<T> results);

    /**
     * Query for results, and dump them into the given results.
     *
     * @param sql           The SQL to execute.
     * @param rowMapper     The Row Mapper to use to map results to Objects.
     * @param results       The Collection of results to write to.
     * @param <T>           The data type of results to use.
     */
    <T> void query(final String sql, final RowMapper<T> rowMapper,
                   final Collection<T> results);

    // SQL fragment generation (predicates in where clause)
    String toSQL(SpatialSearch s, String col, boolean carefulWithNULL);
    String toSQL(IntervalSearch s, String col, boolean carefulWithNULL);
    String toSQL(NumericSearch s, String col, boolean carefulWithNULL);
    String toSQL(TextSearch s, String col, boolean carefulWithNULL);
    String toSQL(RangeSearch<?> s, String col, boolean carefulWithNULL);
    String toSQL(IsNull s, String col);
}