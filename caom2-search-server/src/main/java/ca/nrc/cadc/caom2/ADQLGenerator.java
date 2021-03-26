package ca.nrc.cadc.caom2;

import ca.nrc.cadc.caom2.types.*;
import ca.nrc.cadc.util.StringUtil;


import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.TreeMap;


/**
 * ADQL generator for CAOM TAP service.
 *
 * @author pdowler
 */
public class ADQLGenerator extends AbstractPersistenceService {

    private static final String CAOM2_ENERGY_UTYPE = "Plane.energy.bounds.samples";
    private static final String CAOM2_TIME_UTYPE = "Plane.time.bounds.samples";
    private static final String OBSCORE_ENERGY_UTYPE = "Char.SpectralAxis.Coverage.Bounds.Limits";
    private static final String OBSCORE_TIME_UTYPE = "Char.TemporalAxis.Coverage.Bounds.Limits";
    private static final String SEARCH_UPLOAD_TABLE = "Upload";


    private static Logger LOGGER = Logger.getLogger(ADQLGenerator.class);

    private final String upload;
    private final String uploadResolver;
    private final String targetNameField;
    private final String targetCoordField;


    ADQLGenerator(final String schema, final String _upload, final String _uploadResolver,
                  final String _targetNameField, final String _targetCoordField) {
        super(null, schema, null);

        this.upload = _upload;
        this.uploadResolver = _uploadResolver;
        this.targetNameField = _targetNameField;
        this.targetCoordField = _targetCoordField;

        init();
    }


    @Override
    public String toSQL(final SpatialSearch s, final String col,
                        final boolean carefulWithNULL) {
        final String CAOM2_POSITION_UTYPE = "Plane.position.bounds";
        final String OBSCORE_POSITION_UTYPE =
                "Char.SpatialAxis.Coverage.Support.Area";

        if (s.getName() == null ||
                (!CAOM2_POSITION_UTYPE.equals(s.getName()) &&
                        !OBSCORE_POSITION_UTYPE.equals(s.getName()))) {
            throw new IllegalArgumentException(
                    "cannot use SpatialSearch with utype=" + s.getName());
        } else if (s.getPosition() == null) {
            throw new IllegalArgumentException(
                    "cannot use SpatialSearch with position=" + s
                            .getPosition());
        }

        String position = CAOM2_POSITION_UTYPE;
        if (s.getName().equals(OBSCORE_POSITION_UTYPE)) {
            position = OBSCORE_POSITION_UTYPE;
        }

        final StringBuilder sb = new StringBuilder();
        if (s.getPosition() instanceof Location) {
            final Location loc = (Location) s.getPosition();
            sb.append("CONTAINS( POINT('ICRS',");
            sb.append(loc.getCenter().cval1);
            sb.append(", ");
            sb.append(loc.getCenter().cval2);
            sb.append("), ");
            sb.append(getColumnName(position));
            sb.append(" ) = 1");
        } else if (s.getPosition() instanceof Circle) {
            final Circle circ = (Circle) s.getPosition();
            sb.append("INTERSECTS( CIRCLE('ICRS',");
            sb.append(circ.getCenter().cval1);
            sb.append(", ");
            sb.append(circ.getCenter().cval2);
            sb.append(", ");
            sb.append(circ.getSize() / 2.0);
            sb.append("), ");
            sb.append(getColumnName(position));
            sb.append(" ) = 1");
        } else if (s.getPosition() instanceof Polygon) {
            // TODO: This will fail if the polygon is not a simple poly, but
            // TODO: should be OK for query... use REGION(<stc>) for general
            // TODO: case.
            final Polygon poly = (Polygon) s.getPosition();
            sb.append("INTERSECTS( POLYGON('ICRS'");

            for (final Vertex v : poly.getSamples().getVertices()) {
                if (v.getType() != SegmentType.CLOSE) {
                    sb.append(", ");
                    sb.append(v.cval1);
                    sb.append(", ");
                    sb.append(v.cval2);
                }
            }

            sb.append("),");
            sb.append(getColumnName(position));
            sb.append(" ) = 1");
        } else {
            throw new IllegalArgumentException(
                    "cannot use SpatialSearch with position=" + s
                            .getPosition());
        }

        return sb.toString();
    }

    /**
     * Use the INTERSECTS() function to determine overlapping values.
     *
     * @param s   The IntervalSearch parameters.
     * @param col The column to search on.
     * @return String SQL fragment.
     */
    private String toIntersectSQL(final IntervalSearch s, final String col) {
        final StringBuilder sb = new StringBuilder();
        final double lowerValue = (s.getLower() == null) ? 0.0 : s.getLower();
        final double upperValue = (s.getUpper() == null) ? Double.MAX_VALUE
                : s.getUpper();

        sb.append("INTERSECTS( INTERVAL( ");
        sb.append(lowerValue);
        sb.append(", ");
        sb.append(upperValue);
        sb.append(" ), ");
        sb.append(col);
        sb.append(" ) = 1");

        return sb.toString();
    }

    /**
     * Perform a lower level range search using <>= operators.
     *
     * @param s    The IntervalSearch parameters.
     * @param col1 The lower bound column to search on.
     * @param col2 The upper bound column to search on.
     * @return String SQL fragment.
     */
    private String toIntervalSQL(final IntervalSearch s, final String lowerBoundColName,
                                 final String upperBoundColName) {
        final StringBuilder sb = new StringBuilder();

        if ((s.getLower() != null) && (s.getUpper() != null)) {
            // contains
            sb.append(lowerBoundColName);
            sb.append(" <= ");
            sb.append(s.getLower());
            sb.append(" AND ");
            sb.append(s.getUpper());
            sb.append(" <= ");
            sb.append(upperBoundColName);
        } else if (s.getUpper() != null) {
            // below
            sb.append(upperBoundColName);
            sb.append(" <= ");
            sb.append(s.getUpper());
        } else if (s.getLower() != null) {
            // above
            sb.append(lowerBoundColName);
            sb.append(" >= ");
            sb.append(s.getLower());
        }

        return sb.toString();
    }

    /**
     * Obtain the SQL fragment for an Interval Search template.
     *
     * @param s               The IntervalSearch parameters.
     * @param col             The column to search on.
     * @param carefulWithNULL Mind the null values (Not used).
     * @return String SQL fragment.
     */
    @Override
    public String toSQL(final IntervalSearch s, final String col,
                        final boolean carefulWithNULL) {
        final String sql;

        if (s.getShift() != null) {
            // fuzzy matching
            throw new UnsupportedOperationException("IntervalSearch shifting");
        } else if (s.getEpsilon() != null) {
            // fuzzy matching
            throw new UnsupportedOperationException(
                    "IntervalSearch to SQL (fuzzy match)");
        } else if (CAOM2_ENERGY_UTYPE.equals(s.getName())) {
            // CADC-9369, CADC-1052: call this version of toIntersectSQL so it's a plain
            // upper & lower bound comparison rather than INTERSECTS, which is returning
            // invalid results
            // parameters below are IntervalSearch, upper bound, lower bound
            sql = toIntervalSQL(s, getColumnName("Plane.energy.bounds.lower"),
                getColumnName("Plane.energy.bounds.upper"));
            // TODO: reinstate this when CADC-9369 changes are reverted, after
            // CADC-9367 is completed
            //            sql = toIntersectSQL(s, getColumnName(CAOM2_ENERGY_UTYPE));
        } else if (CAOM2_TIME_UTYPE.equals(s.getName())) {
            sql = toIntersectSQL(s, getColumnName(CAOM2_TIME_UTYPE));
        } else if (OBSCORE_ENERGY_UTYPE.equals(s.getName())) {
            // When CADC-9367 is done, change this to match CAOM@_ENERGY_UTYPE search type
            sql = toIntervalSQL(s,
                                getColumnName(OBSCORE_ENERGY_UTYPE + ".LoLimit"),
                                getColumnName(OBSCORE_ENERGY_UTYPE + ".HiLimit"));
        } else if (OBSCORE_TIME_UTYPE.equals(s.getName())) {
            sql = toIntervalSQL(s, getColumnName(OBSCORE_TIME_UTYPE + ".StartTime"),
                                getColumnName(OBSCORE_TIME_UTYPE + ".StopTime"));
        } else {
            throw new IllegalArgumentException("cannot use IntervalSearch with utype=" + s.getName());
        }

        return sql;
    }

    @Override
    public String toSQL(final RangeSearch s, final String col,
                        final boolean carefulWithNULL) {
        final String CAOM2_POSITION_UTYPE = "Plane.position.bounds";
        final String OBSCORE_POSITION_UTYPE = "Char.SpatialAxis.Coverage.Support.Area";

        if (s.getName() == null ||
                (!CAOM2_POSITION_UTYPE.equals(s.getName()) &&
                        !OBSCORE_POSITION_UTYPE.equals(s.getName()))) {
            throw new IllegalArgumentException(
                    "cannot use RangeSearch with utype=" + s.getName());
        } else if (s.getLowerRange() == null || s.getUpperRange() == null) {
            throw new IllegalArgumentException("cannot use RangeSearch with null RA or Dec range");
        }

        String position = CAOM2_POSITION_UTYPE;
        if (s.getName().equals(OBSCORE_POSITION_UTYPE)) {
            position = OBSCORE_POSITION_UTYPE;
        }

        return "INTERSECTS( RANGE_S2D(" +
                s.getLowerRange().getLowerValue() +
                ", " +
                s.getLowerRange().getUpperValue() +
                ", " +
                s.getUpperRange().getLowerValue() +
                ", " +
                s.getUpperRange().getUpperValue() +
                "), " +
                getColumnName(position) +
                " ) = 1";
    }

    private void init() {
        // class -> table name
        this.tableMap = new TreeMap<>(new ClassComp());

        // caom
        tableMap.put(SimpleObservation.class, "Observation");
        tableMap.put(Observation.class, "Observation");
        tableMap.put(Plane.class, "Plane");

        // class -> alias, String -> String
        aliasMap = new TreeMap<>(new ClassComp());

        for (final Class c : tableMap.keySet()) {
            if (Observation.class.isAssignableFrom(c)) {
                aliasMap.put(c, "Observation");
            } else {
                aliasMap.put(c, c.getSimpleName());
            }
        }

        aliasMap.put(Observation.class, Observation.class.getSimpleName());
    }

    @Override
    public String getFrom(final Class c, int depth) {
        final String fromClause;

        final String a1 = getAlias(c);
        final String f1 = getFrom(c);

        if (depth <= 1) {
            fromClause = f1;
        } else {
            final StringBuilder query = new StringBuilder();

            query.append(f1);

            if (Plane.class.equals(c)) {
                LOGGER.debug("getFrom: Plane JOIN Observation");

                // join to Observation
                final String a2 = getAlias(Observation.class);
                final String f2 = getFrom(Observation.class, depth - 1);
                final String upload = getUpload();

                // JOIN on the TAP_UPLOAD first for performance.
                if (hasUpload()) {
                    final String table = upload.split(",")[0];

                    query.append(" JOIN TAP_UPLOAD.");
                    query.append(table);
                    query.append(" as " + SEARCH_UPLOAD_TABLE + " on ");

                    if (StringUtil.hasText(getUploadResolver())
                            && getUploadResolver().equals("OBJECT")) {
                        query.append(a2);
                        query.append(".");
                        query.append(getTargetNameField());
                        query.append(" = " + SEARCH_UPLOAD_TABLE + ".target");
                    } else {
                        // search_upload.position is a Circle ( 3 doubles representing
                        // RA, Dec, and radius of the circle.)
                        query.append("INTERSECTS(" + SEARCH_UPLOAD_TABLE + ".position, ");
                        query.append(a1);
                        query.append(".");
                        query.append(getTargetCoordField());
                        query.append(") = 1");

                    }
                }

                query.append(" JOIN ");
                query.append(f2);
                query.append(" ON ");
                query.append(a1);
                query.append(".obsID = ");
                query.append(a2);
                query.append(".obsID");

                fromClause = query.toString();
            } else {
                fromClause = super.getFrom(c, depth);
            }
        }

        return fromClause;
    }


    /**
     * Obtain the sanitized SELECT list.
     *
     * @param utypeSelectList The provided SELECT list.
     * @return String SELECT list.
     */
    String getSelectList(final String utypeSelectList) {
        final StringBuilder sb = new StringBuilder();
        final String[] parts = utypeSelectList.split(",");

        String prefix = "";
        for (final String item : parts) {
            final String trimItem = item.trim();
            final String[] ea = trimItem.split(" ");
            sb.append(prefix);
            if (ea.length == 1) // expression
            {
                final String selectItem = getExpression(ea[0]);
                sb.append(selectItem);

            } else if ((ea.length >= 3) && "AS".equalsIgnoreCase(ea[1])) {
                final String as = " " + ea[1] + " ";
                final int startAlias = trimItem.indexOf(as) + 4;
                final String selectItem = getExpression(ea[0]);

                sb.append(selectItem);
                sb.append(" AS ");
                sb.append(trimItem.substring(startAlias));
            } else {
                throw new IllegalArgumentException(
                    "failed to parse select list: found " + ea.length
                        + " tokens in '" + trimItem + "'");
            }
            prefix = ", ";
        }

        if (hasUpload()) {
            // Upload target, ra and dec come in to the service as part of the
            // list of requested columns. Radius and position need to be added.
            sb.append(prefix);
            sb.append(SEARCH_UPLOAD_TABLE);
            sb.append(".radius");
            sb.append(prefix);
            sb.append(SEARCH_UPLOAD_TABLE);
            sb.append(".position");
        }

        return sb.toString();
    }

    private String getExpression(final String e) {
        final StringBuilder sb = new StringBuilder();

        // Not a function or an array.
        if (e.matches("^.*\\(.*\\).*")) {
            try {
                final String expressionContent = URLDecoder.decode(e, "UTF-8");
                final int argStart = expressionContent.indexOf("(");
                final int argEnd = expressionContent.lastIndexOf(")");
                final String arguments = expressionContent.substring(argStart + 1, argEnd);
                sb.append(expressionContent, 0, argStart + 1);
                sb.append(getExpression(arguments));
                sb.append(getExpression(expressionContent.substring(argEnd)));
            } catch (UnsupportedEncodingException exception) {
                throw new IllegalArgumentException(
                        "Unable to parse out SELECT list.", exception);
            }
        } else if (e.contains(",")) {
            final String[] arrayItems = e.split(",");

            for (final String item : arrayItems) {
                if (!item.contains(".")) {
                    sb.append(item);
                } else {
                    sb.append(getColumnName(item));
                }
                sb.append(", ");
            }

            sb.delete(sb.length() - 2, sb.length());
        } else {
            if (e.contains("Upload") || !e.contains(".")) {
                // Upload classes aren't mapped and added to the query without
                // the extra condition here
                sb.append(e);
            } else {
                LOGGER.debug("Looking up " + e);
                sb.append(getColumnName(e));
            }
        }

        return sb.toString();
    }

    boolean hasUpload() {
        return StringUtil.hasText(getUpload());
    }

    public String getUpload() {
        return upload;
    }

    private String getUploadResolver() {
        return uploadResolver;
    }

    private String getTargetNameField() {
        return targetNameField;
    }

    private String getTargetCoordField() {
        return targetCoordField;
    }
}
