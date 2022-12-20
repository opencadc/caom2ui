package ca.nrc.cadc.caom2;

import javax.sql.DataSource;
import java.net.URI;
import java.util.*;


import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.search.ObsModel;


/**
 * @author pdowler
 */
public abstract class AbstractPersistenceService implements PersistenceService {

    private static final Logger LOGGER = Logger.getLogger(AbstractPersistenceService.class);

    protected static final String BASE_PKG = "ca.nrc.cadc.caom2";

    private static final Telescope NULL_TELESCOPE = new Telescope("");
    private static final Instrument NULL_INSTRUMENT = new Instrument("");
    private static final Proposal NULL_PROPOSAL = new Proposal("");
    private static final Target NULL_TARGET = new Target("");

    protected static final String AND = " AND ";
    protected static final String OR = " OR ";

    protected String catalog;
    protected String schema;
    protected Map<Class<?>, String> aliasMap;
    protected Map<Class<?>, String> tableMap;

    private boolean enabled;
    private DataSource ds;
    private JdbcTemplate jdbc;


    protected AbstractPersistenceService(String catalog, String schema, DataSource ds) {
        this.catalog = catalog;
        this.schema = schema;
        this.ds = ds;
        if (ds != null) {
            this.jdbc = new JdbcTemplate(ds);
            this.enabled = true;
        }
    }

    /**
     * This will close the underlying connection if the data source is a
     * Spring SingleConnectiondataSource. Otherwise (JNDI data source): no-op.
     */
    public void close() {
        if ((ds != null) && (ds instanceof SingleConnectionDataSource)) {
            final SingleConnectionDataSource scds = (SingleConnectionDataSource) ds;
            scds.destroy();
            this.ds = null;
            this.jdbc = null;
        }
    }

    private JdbcTemplate getJdbcTemplate() {
        return enabled ? jdbc : null;
    }

    /**
     * Obtain the schema to use for further use.
     *
     * @return String schema.
     */
    @Override
    public String getSchema() {
        return schema;
    }

    /**
     * Query for results, and dump them into the given results.
     *
     * @param sql     The SQL to execute.
     * @param rse     The ResultSetExtractor to use to pull out results.
     * @param results The Collection of results to use.
     * @param <T>     The data type of results to use.
     */
    @Override
    public <T> void query(final String sql, final ResultSetExtractor<Collection<T>> rse, final Collection<T> results) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();

        if (jdbcTemplate != null) {
            final Collection<T> queryResults = jdbcTemplate.query(sql, rse);
            if (queryResults != null) {
                results.addAll(queryResults);
            }
        }
    }

    /**
     * Query for results, and dump them into the given results.
     *
     * @param sql       The SQL to execute.
     * @param rowMapper The Row Mapper to use to map results to Objects.
     * @param results   The Collection of results to write to.
     * @param <T>       The data type of results to use.
     */
    @Override
    public <T> void query(final String sql, final RowMapper<T> rowMapper, final Collection<T> results) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();

        if (jdbcTemplate == null) {
            LOGGER.debug(Util.formatSQL(sql));
        } else {
            results.addAll(jdbcTemplate.query(sql, rowMapper));
        }
    }

    public String literal(Object obj) {
        final String literal;

        if ((obj == null) || (obj.toString().equals("null"))) {
            literal = "NULL";
        } else if (obj instanceof Number) {
            literal = obj.toString();
        } else if ((obj instanceof String) || (obj instanceof StringBuilder)
                || (obj instanceof URI)) {
            String s = Util.escapeChar(obj.toString(), '\'');
            if (s.charAt(s.length() - 1) == '\\') {
                s = s + "\\";
            }

            literal = "'" + s + "'";
        } else if (obj instanceof Telescope) {
            literal = literal((Telescope) obj);
        } else if (obj instanceof Instrument) {
            literal = literal((Instrument) obj);
        } else if (obj instanceof Target) {
            literal = literal((Target) obj);
        } else if (obj instanceof Proposal) {
            literal = literal((Proposal) obj);
        } else {
            throw new IllegalArgumentException("unsupported literal: "
                                                       + obj.getClass().getName());
        }

        return literal;
    }

    protected String literal(final Proposal p) {
        final Proposal proposal;

        if (p == null) {
            proposal = NULL_PROPOSAL;
        } else {
            proposal = p;
        }

        return literal(proposal.getID()) + "," + literal(proposal.pi) + ","
                + literal(proposal.title) + ","
                + literal(proposal.getKeywords());
    }

    protected String literal(final Telescope t) {
        final Telescope telescope;

        if (t == null) {
            telescope = NULL_TELESCOPE;
        } else {
            telescope = t;
        }

        return literal(telescope.getName()) + ","
                + literal(telescope.geoLocationX) + ","
                + literal(telescope.geoLocationY) + ","
                + literal(telescope.geoLocationZ) + ","
                + literal(telescope.getKeywords());
    }

    protected String literal(final Instrument i) {
        final Instrument instrument;

        if (i == null) {
            instrument = NULL_INSTRUMENT;
        } else {
            instrument = i;
        }

        return literal(instrument.getName()) + ","
                + literal(instrument.getKeywords());
    }

    protected String literal(final Target t) {
        final Target target;

        if (t == null) {
            target = NULL_TARGET;
        } else {
            target = t;
        }

        return literal(target.getName()) + ","
                + literal(target.type.getValue()) + ","
                + literal(target.redshift);
    }

    public String toSQL(final List<SearchTemplate> templates, final String op) {
        if (templates == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (final SearchTemplate tmpl : templates) {
            final String sql = toSQL(tmpl);
            if (sql != null) {
                if (!first) {
                    sb.append(op);
                }
                sb.append(sql);
                first = false;
            }
        }

        final String ret = sb.toString().trim();
        if (ret.length() == 0) {
            return null;
        }

        return " ( " + ret + " ) ";
    }


    public String toSQL(SearchTemplate tmpl) {
        return toSQL(tmpl, false);
    }

    /**
     * Convert the SearchTemplate to SQL.
     * TODO - TECHNICAL DEBT: The long if statement should be refactored.  The
     * TODO - SQL is a View on the model (SearchTemplate), and should be
     * TODO - treated as such.
     * TODO -
     * TODO - jenkinsd 2014.05.27
     *
     * @param tmpl            SearchTemplate to convert.
     * @param carefulWithNULL Flag to check for NULL in the SQL.
     * @return String SQL.
     */
    public String toSQL(final SearchTemplate tmpl,
                        final boolean carefulWithNULL) {
        LOGGER.debug("toSQL: " + tmpl);

        final String queryString;

        if (tmpl instanceof And) {
            queryString = toSQL((And) tmpl);
        } else if (tmpl instanceof Or) {
            queryString = toSQL((Or) tmpl);
        } else if (tmpl instanceof Top) {
            queryString = null;
        } else {
            queryString = getColumnSQL(tmpl, getColumnName(tmpl.getName()),
                                       carefulWithNULL);
        }

        return queryString;
    }

    /**
     * Obtain the specific SQL for the given template and column.
     *
     * @param tmpl            The SearchTemplate to query with.
     * @param col             The Column to look up.
     * @param carefulWithNULL Flag to check for NULL in query.
     * @return String ADQL.
     */
    private String getColumnSQL(final SearchTemplate tmpl, final String col,
                                final boolean carefulWithNULL) {
        final String queryString;

        if (tmpl instanceof SpatialSearch) {
            queryString = toSQL((SpatialSearch) tmpl, col, carefulWithNULL);
        } else if (tmpl instanceof IntervalSearch) {
            queryString = toSQL((IntervalSearch) tmpl, col, carefulWithNULL);
        } else if (tmpl instanceof NumericSearch) {
            queryString = toSQL((NumericSearch) tmpl, col, carefulWithNULL);
        } else if (tmpl instanceof TextSearch) {
            queryString = toSQL((TextSearch) tmpl, col, carefulWithNULL);
        } else if (tmpl instanceof RangeSearch) {
            queryString = toSQL((RangeSearch<?>) tmpl, col, carefulWithNULL);
        } else if (tmpl instanceof TimestampSearch) {
            queryString = toSQL((TimestampSearch) tmpl, col,
                                carefulWithNULL);
        } else if (tmpl instanceof IsNull) {
            queryString = toSQL((IsNull) tmpl, col);
        } else {
            throw new RuntimeException(
                    "BUG: unable to convert " + tmpl.getClass()
                                                    .getName() + " to SQL");
        }

        return queryString;
    }

    public String toSQL(SpatialSearch s, String col, boolean carefulWithNULL) {
        throw new UnsupportedOperationException();
    }

    public String toSQL(IntervalSearch s, String col, boolean carefulWithNULL) {
        throw new UnsupportedOperationException();
    }

    public String toSQL(RangeSearch<?> s, String col, boolean carefulWithNULL) {
        throw new UnsupportedOperationException();
    }

    public String toSQL(And and) {
        return toSQL(and.getTemplates(), AND);
    }

    public String toSQL(Or or) {
        return toSQL(or.getTemplates(), OR);
    }

    /**
     * Obtain the query string for a Timestamp search.
     *
     * @param timestampSearchTemplate The timestamp search template object
     *                                containing search criteria.
     * @param column                  The column name to search on.
     * @param carefulWithNULL         Flag to be mindful of NULLs in the
     *                                query.
     * @return Query String in ADQL/SQL
     */
    String toSQL(final TimestampSearch timestampSearchTemplate,
                 final String column, final boolean carefulWithNULL) {
        final StringBuilder queryStringBuilder = new StringBuilder();

        if (carefulWithNULL) {
            queryStringBuilder.append(column);
            queryStringBuilder.append(" IS NOT NULL AND ");
        }

        final Date lowerValue = timestampSearchTemplate.getLower();
        final Date upperValue = timestampSearchTemplate.getUpper();

        if (lowerValue != null) {
            final String lowerDateString =
                    DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                           TimeZone.getTimeZone("UTC")).format(
                            lowerValue);

            queryStringBuilder.append(column).append(" >");

            if (timestampSearchTemplate.isClosedLower()) {
                queryStringBuilder.append("=");
            }

            queryStringBuilder.append(" '").append(lowerDateString).append("'");

            if (upperValue != null) {
                queryStringBuilder.append(" AND ");
            }
        }

        if (upperValue != null) {
            final String upperDateString =
                    DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                           TimeZone.getTimeZone("UTC")).format(
                            upperValue);

            queryStringBuilder.append(column).append(" <");

            if (timestampSearchTemplate.isClosedUpper()) {
                queryStringBuilder.append("=");
            }

            queryStringBuilder.append(" '").append(upperDateString).append("'");
        }

        return queryStringBuilder.toString();
    }

    public String toSQL(TextSearch s, String col, boolean carefulWithNULL) {
        if (s.lower == null && s.upper == null) {
            return col + " IS NOT NULL";
        }

        String targetColumn = col;
        String s1 = s.lower;
        String s2 = s.upper;

        if (s.ignoreCase) {
            targetColumn = "lower(" + targetColumn + ")";
            if (s1 != null) {
                s1 = s1.toLowerCase();
            }
            if (s2 != null) {
                s2 = s2.toLowerCase();
            }
        }

        String ret = "";
        if (carefulWithNULL) {
            ret = col + " IS NOT NULL AND ";
        }

        if (s1 != null && s2 != null) {
            if (s1.equals(s2)) {
                String ss = Util.replaceAll(s1, '*', '%');

                if (s.wild && ss.charAt(0) != '%') {
                    ss = "%" + ss;
                }

                if (s.wild && ss.charAt(ss.length() - 1) != '%') {
                    ss = ss + "%";
                }

                if (s.wild || ss.indexOf('%') >= 0) {
                    ret += targetColumn + (s.isNegated() ? " NOT" : "")
                            + " LIKE " + literal(ss);
                } else if (s.lower.equals(s.upper)) {
                    ret += targetColumn + (s.isNegated() ? " !" : " ")
                            + "= " + literal(ss);
                }
            } else {
                ret += targetColumn + " BETWEEN " + literal(s1) + AND + literal(
                        s2);
            }
        } else if (s1 != null) {
            ret += targetColumn + " >= " + literal(s1);
        } else {
            ret += targetColumn + " <= " + literal(s2);
        }

        return ret;
    }

    public String toSQL(NumericSearch ns, String col, boolean carefulWithNULL) {
        if (ns.lower == null && ns.upper == null) {
            return col + " IS NOT NULL";
        }

        String ret = "";
        if (carefulWithNULL) {
            ret = col + " IS NOT NULL AND";
        }

        if (ns.lower != null && ns.lower.equals(ns.upper)) {
            ret += col + " = " + ns.lower;
        } else {
            if (ns.lower != null && ns.closedLower) {
                ret += " " + col + " >= " + ns.lower;
            }

            if (ns.lower != null && !ns.closedLower) {
                ret += " " + col + " > " + ns.lower;
            }

            if (ns.upper != null && ns.closedUpper) {
                if (ret.length() > 0) {
                    ret += " AND ";
                }
                ret += " " + col + " <= " + ns.upper;
            }

            if (ns.upper != null && !ns.closedUpper) {
                if (ret.length() > 0) {
                    ret += " AND ";
                }
                ret += " " + col + " < " + ns.upper;
            }
        }
        return ret;
    }

    public String toSQL(IsNull s, String col) {
        return col + " IS NULL";
    }

    protected Class<?> getClassFromUtype(String utype)
            throws ClassNotFoundException {
        int i = utype.indexOf('.');
        String simpleName = utype.substring(0, i);
        if (simpleName.startsWith("prop") ||
                simpleName.startsWith("obscore")) {
            return null; // simpleName is the alias
        }
        return Class.forName(BASE_PKG + "." + simpleName);
    }

    public String getColumnName(String utype) {
        LOGGER.debug("getColumnName: " + utype);
        try {
            final String column =
                    ObsModel.getObsCoreName(utype.replaceAll("_", "."));
            if (column != null) {
                return column;
            } else {
                int i = utype.indexOf('.');
                String simpleName = utype.substring(0, i);
                final Class<?> c = getClassFromUtype(utype);
                String alias = simpleName;
                if (c != null) {
                    LOGGER.debug("getColumnName: class = " + c.getName());
                    alias = getAlias(c);
                }
                utype = utype.substring(i + 1);
                utype = Util.replaceAll(utype, '.', '_');
                LOGGER.debug("alias: " + alias + "  utype: " + utype);
                return alias + "." + utype;
            }
        } catch (ClassNotFoundException cex) {
            throw new RuntimeException(
                    "failed to map utype (" + utype + ") -> Class -> alias",
                    cex);
        }
    }

    public String getTable(Class<?> c) {
        LOGGER.debug("getTable: " + c.getName());
        String tabName = tableMap.get(c);
        if (tabName.startsWith("(")) {
            return tabName;
        }
        StringBuilder sb = new StringBuilder();
        if (catalog != null) {
            sb.append(catalog);
            sb.append(".");
        }
        if (schema != null) {
            sb.append(schema);
            sb.append(".");
        }
        sb.append(tabName);
        return sb.toString();
    }

    public String getAlias(Class<?> c) {
        return aliasMap.get(c);
    }

    protected String getFrom(Class<?> c) {
        LOGGER.debug("getFrom: " + c);
        final String classTable = getTable(c);
        final String fromClause;

        if (classTable.startsWith("ObsCore")) {
            fromClause = classTable;
        } else {
            fromClause = classTable + " AS " + getAlias(c);
        }

        return fromClause;
    }

    public String getFrom(Class<?> c, int depth) {
        LOGGER.debug("getFrom: " + c + ", depth = " + depth);

        String a1 = getAlias(c);
        String f1 = getFrom(c);

        if (depth <= 1) {
            return f1;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(f1);

        if (Observation.class.isAssignableFrom(c)) {
            LOGGER.debug("getFrom: observation JOIN plane");
            // join to plane
            String a2 = getAlias(Plane.class);
            String f2 = getFrom(Plane.class, depth - 1); // recursive
            sb.append(" JOIN ");
            sb.append(f2);
            sb.append(" ON ");
            sb.append(a1);
            sb.append(".obsID = ");
            sb.append(a2);
            sb.append(".obsID");
        } else if (Plane.class.equals(c)) {
            LOGGER.debug("getFrom: plane JOIN artifact");
            // join to artifact
            String a2 = getAlias(Artifact.class);
            String f2 = getFrom(Artifact.class, depth - 1);
            sb.append(" LEFT OUTER JOIN ");
            sb.append(f2);
            sb.append(" ON ");
            sb.append(a1);
            sb.append(".planeID = ");
            sb.append(a2);
            sb.append(".planeID");
        }
        return sb.toString();
    }

    public String getWhere(final List<SearchTemplate> constraints) {
        return toSQL(constraints, AND);
    }


    protected static class ClassComp implements Comparator<Class<?>> {

        public int compare(Class c1, Class c2) {
            return c1.getSimpleName().compareTo(c2.getSimpleName());
        }
    }
}
