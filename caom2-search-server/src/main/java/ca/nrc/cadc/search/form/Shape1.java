/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2008.                            (c) 2008.
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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.form;

import ca.nrc.cadc.caom2.RangeSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.nrc.cadc.caom2.types.*;
import ca.nrc.cadc.search.parser.AbstractPositionParser;
import ca.nrc.cadc.search.parser.resolver.ResolverImpl;

import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.TextSearch;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.parser.CoordSys;
import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.Range;
import ca.nrc.cadc.search.parser.Resolver;
import ca.nrc.cadc.search.parser.TargetParser;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.exception.TargetParserException;
import ca.nrc.cadc.util.CaseInsensitiveStringComparator;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import java.awt.geom.Point2D;

import jsky.coords.wcscon;


import org.apache.log4j.Logger;


/**
 * Class to represent a Shape1 form component.
 *
 * @author jburke
 */
public class Shape1 extends AbstractFormConstraint implements SearchableFormConstraint {

    private static final Logger LOGGER = Logger.getLogger(Shape1.class);

    // Constants used to construct name for form elements
    public static final String RESOLVER_TARGET_NAME_MATCH = "NONE";
    public static final String NAME = "@Shape1";
    public static final String VALUE = "@Shape1.value";
    public static final String RESOLVER_VALUE = "@Shape1Resolver.value";

    // Shape1 ra
    private Double ra;
    private Range<Double> raRange;

    // Shape1 dec
    private Double dec;
    private Range<Double> decRange;

    // Shape1 radius
    private Double radius;

    // Shape1 coordsys
    private String coordsys;

    // Shape1 resolver
    private String resolverName;


    /**
     * Shape1 constructor instantiates a new instance with the request parameter.
     *
     * @param job   The servlet request.
     * @param utype The utype of the form.
     */
    public Shape1(final Job job, final String utype) {
        super(utype);

        for (final Parameter parameter : job.getParameterList()) {
            if (parameter.getName().equals(utype + VALUE)) {
                setFormValue(parameter.getValue());
            } else if (parameter.getName().equals(utype + RESOLVER_VALUE)) {
                resolverName = parameter.getValue();
            }
        }

        if (resolverName != null) {
            resolverName = resolverName.trim();
        }

        ra = null;
        dec = null;
        radius = null;
        raRange = null;
        decRange = null;
    }

    // Create a SpatialSearch to SearchTemplates.
    @Override
    public SearchTemplate buildSearch(List<FormError> errorList) {
        SearchTemplate searchTemplate = null;

        try {
            if (hasShapeData()) {
                final Shape s;

                if ((getRadius() != null) && (getRadius() > 0)) {
                    s = new Circle(new Point(getRA(), getDec()), getRadius());
                } else {
                    s = new Location(new Point(getRA(), getDec()));
                }

                searchTemplate = new SpatialSearch(getUType(), s);
            } else if (hasRangeData()) {
                final Double raLower = normalizeRA(getRARange().getLowerValue());
                final Double raUpper = normalizeRA(getRARange().getUpperValue());
                final Double decLower = normalizeDec(getDecRange().getLowerValue());
                final Double decUpper = normalizeDec(getDecRange().getUpperValue());

                final CaseInsensitiveStringComparator comparator = new CaseInsensitiveStringComparator();
                if ((getCoordsys() != null) &&
                    ((comparator.compare(getCoordsys(), CoordSys.FK4.getValue()) == 0) ||
                     (comparator.compare(getCoordsys(), CoordSys.B1950.getValue()) == 0) ||
                     (comparator.compare(getCoordsys(), CoordSys.B1950_0.getValue()) == 0) ||
                     (comparator.compare(getCoordsys(), CoordSys.GAL.getValue()) == 0))) {
                    final List<Point> vertexPoints = new ArrayList<>();
                    final List<Vertex> vertices = new ArrayList<>();

                    vertices.add(new Vertex(raLower, decLower, SegmentType.MOVE));
                    vertices.add(new Vertex(raUpper, decLower, SegmentType.LINE));
                    vertices.add(new Vertex(raUpper, decUpper, SegmentType.LINE));
                    vertices.add(new Vertex(raLower, decUpper, SegmentType.LINE));
                    vertices.add(new Vertex(0.0, 0.0, SegmentType.CLOSE));

                    Collections.copy(vertexPoints, vertices);

                    Polygon polygon = new Polygon(vertexPoints, new MultiPolygon(vertices));

                    Point2D point;
                    for (final Vertex vertex : vertices) {
                        if (!SegmentType.CLOSE.equals(vertex.getType())) {
                            if (comparator.compare(getCoordsys(), CoordSys.GAL.getValue()) == 0) {
                                point = wcscon.gal2fk5(new Point2D.Double(vertex.cval1, vertex.cval2));
                            } else {
                                point = wcscon.fk425(new Point2D.Double(vertex.cval1, vertex.cval2));
                            }

                            vertex.cval1 = point.getX();
                            vertex.cval2 = point.getY();
                        }
                    }
                    searchTemplate = new SpatialSearch(getUType(), polygon);
                } else {
                    final Operand raOperand = getRARange().getOperand();
                    if (raOperand == Operand.EQUALS) {
                        final Double value = normalizeRA(getRARange().getValue());
                        setRARange(new Range<>(getRARange().getRange(),
                                               getRARange().getValue(),
                                               value, value, raOperand));
                    } else if (raOperand == Operand.GREATER_THAN ||
                               raOperand == Operand.GREATER_THAN_EQUALS) {
                        setRARange(new Range<>(getRARange().getRange(),
                                               getRARange().getValue(),
                                               raLower, 360d, raOperand));
                    } else if (raOperand == Operand.LESS_THAN ||
                               raOperand == Operand.LESS_THAN_EQUALS) {
                        setRARange(new Range<>(getRARange().getRange(),
                                               getRARange().getValue(),
                                               0d, raUpper, raOperand));
                    } else if (raOperand == Operand.RANGE) {
                        setRARange(new Range<>(getRARange().getRange(),
                                               getRARange().getValue(),
                                               raLower, raUpper, raOperand));
                    }

                    final Operand decOperand = getDecRange().getOperand();
                    if (decOperand == Operand.EQUALS) {
                        final Double value = normalizeDec(getDecRange().getValue());
                        setDecRange(new Range<>(getDecRange().getRange(),
                                                getDecRange().getValue(),
                                                value, value, decOperand));
                    } else if (decOperand == Operand.GREATER_THAN ||
                               decOperand == Operand.GREATER_THAN_EQUALS) {
                        setDecRange(new Range<>(getDecRange().getRange(),
                                                getDecRange().getValue(),
                                                decLower, 90d, decOperand));
                    } else if (decOperand == Operand.LESS_THAN ||
                               decOperand == Operand.LESS_THAN_EQUALS) {
                        setDecRange(new Range<>(getDecRange().getRange(),
                                                getDecRange().getValue(),
                                                -90d, decUpper, decOperand));
                    } else if (decOperand == Operand.RANGE) {
                        setDecRange(new Range<>(getDecRange().getRange(),
                                                getDecRange().getValue(),
                                                decLower, decUpper, decOperand));
                    }

                    searchTemplate = new RangeSearch<>(getUType(),
                                                       getRARange(),
                                                       getDecRange());
                }
            } else {
                // Plain target name search.  Mangle the UType to adjust for that.
                final String searchUType = ObsModel.mangleTargetNameUType(getUType());
                searchTemplate = new TextSearch(searchUType, getFormValue(), false,
                                                true);
            }
        } catch (IllegalArgumentException e) {
            errorList.add(new FormError(Shape1.NAME, e.getMessage()));
            LOGGER.debug("Invalid Shape1 parameters: " + e.getMessage() + " " + this);
        }

        return searchTemplate;
    }

    /**
     * Shape1 is valid if the ra, dec, and radius have been
     * successfully parsed or resolved from the form value.
     *
     * @return boolean true if form values are valid, false otherwise.
     */
    @Override
    public boolean isValid(final FormErrors formErrors) {
        if (getFormValue().equals("")) {
            return false;
        }

        setFormValue(getFormValue().trim());

        try {
            if (resolverName != null && !resolverName.equals(Shape1.RESOLVER_TARGET_NAME_MATCH)) {
                final String target = getFormValue();
                final Resolver resolver = new ResolverImpl();
                final TargetParser parser = new TargetParser(resolver);
                final TargetData targetData = parser.parse(target, resolverName);

                // A range creates a polygon which cannot have a radius.
                // Assumes default radius from TargetParser is 0.0d
                if (targetData.getRaRange() != null &&
                    targetData.getDecRange() != null &&
                    targetData.getRadius() != AbstractPositionParser.DEFAULT_RADIUS) {
                    final String message =
                            String.format("A RA and Dec range cannot have a radius " +
                                          "'%s'.", getFormValue());
                    throw new TargetParserException(message);
                }

                // For ranges, both RA and Dec must be a range.
                if ((targetData.getRaRange() != null &&
                     targetData.getDec() != null) ||
                    (targetData.getDecRange() != null &&
                     targetData.getRA() != null)) {
                    final String message =
                            String.format("Both RA and Dec must be a range for a " +
                                          "range search '%s'.", getFormValue());
                    throw new TargetParserException(message);
                }

                if (!parser.isQueryInDegrees(getFormValue())) {
                    setFormValueUnit("SEXIGESIMAL");
                }

                // S1448: default radius from TargetParser is 0.0,
                // change to 1 arcminute (1/60)
                if (targetData.getRadius() == AbstractPositionParser.DEFAULT_RADIUS) {
                    setRadius(1.0d / 60.0d);
                } else {
                    setRadius(targetData.getRadius());
                }
                setRA(targetData.getRA());
                setDec(targetData.getDec());
                setRARange(targetData.getRaRange());
                setDecRange(targetData.getDecRange());
                setCoordsys(targetData.getCoordsys());
            }
            return true;
        } catch (TargetParserException e) {
            if (!e.getExceptionType().equals(
                    TargetParserException.ExceptionType.
                            NAMERESOLVER_TARGET_NOT_FOUND)
                && ((resolverName != null) && resolverName.equals("ALL"))) {
                addError(new FormError(getUType() + VALUE, e.getMessage()));
            } else {
                return true;
            }
        }

        formErrors.set(getUType() + NAME, getErrorList());
        return false;
    }

    /**
     * If the value is positive and greater than max, subtract max until the
     * value is less than max. If the value is negative, add max until the
     * value is greater than a negated max.
     *
     * @param value The actual value.
     * @param min   The minimum (low) value.
     * @param max   The maximum (hi) value.
     * @return The normalized value.
     */
    protected static Double normalize(final Double value, final Double min, final Double max) {
        if (value == null) {
            return null;
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("min, or max cannot be null");
        }
        if (value >= min && value <= max) {
            return value;
        }

        double temp = value;
        while (temp > max) {
            temp -= max;
        }
        while (temp < -max) {
            temp += max;
        }
        return temp;
    }

    protected static Double normalizeRA(final Double value) {
        return normalize(value, 0.0, 360.0);
    }

    protected static Double normalizeDec(final Double value) {
        return normalize(value, -90.0, 90.0);
    }

    public boolean hasShapeData() {
        return (ra != null) && (dec != null) && (radius != null);
    }

    public boolean hasRangeData() {
        return (raRange != null) && (decRange != null);
    }

    public Double getRA() {
        return ra;
    }

    protected void setRA(final Double ra) {
        this.ra = ra;
    }

    public Double getDec() {
        return dec;
    }

    protected void setDec(final Double dec) {
        this.dec = dec;
    }

    public Double getRadius() {
        return radius;
    }

    protected void setRadius(final Double radius) {
        this.radius = radius;
    }

    public Range<Double> getRARange() {
        return raRange;
    }

    protected void setRARange(final Range<Double> raRange) {
        this.raRange = raRange;
    }

    public Range<Double> getDecRange() {
        return decRange;
    }

    protected void setDecRange(final Range<Double> decRange) {
        this.decRange = decRange;
    }

    protected void setCoordsys(final String coordsys) {
        this.coordsys = coordsys;
    }

    public String getCoordsys() {
        return coordsys;
    }

    public String getResolverName() {
        return resolverName;
    }

    /**
     * @return String representation of the Shape1 form.
     */
    @Override
    public String toString() {
        return ("Shape1[" + getFormValue() + "]");
    }
}
