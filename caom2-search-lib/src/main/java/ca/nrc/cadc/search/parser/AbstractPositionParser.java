/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits r√©serv√©s
 *
 *  NRC disclaims any warranties,        Le CNRC d√©nie toute garantie
 *  expressed, implied, or               √©nonc√©e, implicite ou l√©gale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           √™tre tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou g√©n√©ral,
 *  arising from the use of the          accessoire ou fortuit, r√©sultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        √™tre utilis√©s pour approuver ou
 *  products derived from this           promouvoir les produits d√©riv√©s
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  pr√©alable et particuli√®re
 *                                       par √©crit.
 *
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.search.parser;

import java.awt.geom.Point2D;

import jsky.coords.wcscon;
import org.apache.log4j.Logger;

import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.search.parser.exception.PositionParserException;
import ca.nrc.cadc.search.parser.exception.RangeParserException;
import ca.nrc.cadc.util.CaseInsensitiveStringComparator;
import ca.nrc.cadc.util.StringUtil;


/**
 * ra [sep] dec [sep] [radius[unit]] [sep] [coordsys]
 * <p>
 * Class takes a String target value and attempts to parse
 * the target into R.A., Dec., and radius values.
 * <p>
 * 1 token is resolved to coordinates.
 * 2 tokens are a RA and DEC.
 * 3 tokens are a RA, DEC, and radius or coordinate system.
 * 4 tokens are a RA, DEC, radius and coordinate system. TODO: 4 tokens could be 2 sexagesimal
 * coordinates missing the seconds.
 * 5 tokens are ambiguous and not parsed.
 * Chris: TODO list: 5 tokens as 2 sexagesimal coordinates missing the seconds and a radius or coordinate system.
 * 6 tokens are a RA and DEC in sexagesimal.
 * 7 tokens are a RA and DEC in sexagesimal and a radius or coordinate system.
 * 8 tokens are a RA and DEC in sexagesimal and a radius and a coordinate system.
 *
 * @author jburke
 */
public abstract class AbstractPositionParser {
    private static final Logger log = Logger
                                          .getLogger(AbstractPositionParser.class);

    // In degrees
    public static final double DEFAULT_RADIUS = 0.0166667D;

    /**
     * No-arg constructor.
     */
    public AbstractPositionParser() {
    }

    /**
     * Attempts to determine the coordinates of the query.
     *
     * @param target The spatial target.
     * @return Parsed target data.
     *
     * @throws PositionParserException If the target is null or cannot be parsed.
     */
    public TargetData parse(final String target) throws PositionParserException {
        if (!StringUtil.hasText(target)) {
            throw new PositionParserException("Null or empty target");
        }

        final TargetData targetData = new TargetData();
        targetData.setTarget(target);
        targetData.setRadius(DEFAULT_RADIUS);

        parseCoordinates(target, targetData);

        return targetData;
    }

    /**
     * Clean up the input for processing.
     *
     * @param target The target value entered.
     * @return The sanitized value.
     */
    protected String sanitizeTarget(final String target) {
        return target.replace(",", " ").replaceAll("(\\s*)(\\.{2,})(\\s*)", "$2");
    }

    String[] partition(final String target) {
        final String sanitizedTarget = sanitizeTarget(target);
        return sanitizedTarget.trim().split("\\s+");
    }

    /**
     * Attempts to parse the target by checking if the target
     * consists of four values consisting of RA, DEC, radius,
     * and a coordinate system, space, comma or tab delimited.
     *
     * @param target to be parsed.
     * @throws PositionParserException If the query is null or an error in
     *                                 parsing occurs.
     */
    private void parseCoordinates(final String target, TargetData targetData)
        throws PositionParserException {
        try {
            boolean parsed = false;
            final String[] parts = partition(target);

            if (parts.length > 0 && parts[0] != null) {
                try {
                    Range<Double> range = getRARange(parts[0]);
                    targetData.setRaRange(range);
                } catch (RangeParserException ignore) {
                }
                if (targetData.getRaRange() == null) {
                    targetData.setRA(raToDegrees(parts[0].trim()));
                }
            }

            if (parts.length > 1 && parts[1] != null) {
                try {
                    Range<Double> range = getDecRange(parts[1]);
                    targetData.setDecRange(range);
                } catch (RangeParserException ignore) {
                }
                if (targetData.getDecRange() == null) {
                    targetData.setDec(decToDegrees(parts[1].trim()));
                }
                parsed = true;
            }

            if (parts.length > 2 && parts[2] != null) {
                boolean parsedRadius = parseRadius(parts[2], targetData);
                if (!parsedRadius) {
                    parseCoordsys(parts[2], targetData);
                }
            }

            if (parts.length > 3 && parts[3] != null) {
                parsed = parseCoordsys(parts[3], targetData);
            }

            if (parts.length > 5 && parts[4] != null && parts[5] != null) {
                // Check the first 6 values are parseable as Doubles.
                StringBuilder RA = new StringBuilder();
                StringBuilder DEC = new StringBuilder();
                try {
                    for (int i = 0; i < 6; i++) {
                        final String nextPart = parts[i];
                        if (StringUtil.hasText(nextPart)) {
                            double d = Double.valueOf(nextPart);
                            if (i < 3) {
                                RA.append(d).append(":");
                            } else {
                                DEC.append(d).append(":");
                            }
                        }
                    }
                    targetData.setRA(raToDegrees(RA.toString()));
                    targetData.setDec(decToDegrees(DEC.toString()));
                    targetData.setRadius(DEFAULT_RADIUS);
                    parsed = true;
                } catch (NumberFormatException ignore) {
                }
            }

            // Check if 7th value is radius or coordsys.
            if (parsed && parts.length > 6 && parts[6] != null) {
                parsed = parseRadius(parts[6], targetData);
                if (!parsed) {
                    parsed = parseCoordsys(parts[6], targetData);
                }
            }

            if (parsed && parts.length > 7 && parts[7] != null) {
                parsed = parseCoordsys(parts[7], targetData);
            }

            if (!parsed) {
                throw new PositionParserException("Unable to parse: " + target);
            }
        } catch (NumberFormatException nfe) {
            final String message = "Unable to parse '" + target + "' because " +
                                       nfe.getMessage();
            throw new PositionParserException(message);
        }
    }

    /**
     * Attempts to parse a radius and verify it is within allowed limits.
     *
     * @param radius radius to be parsed.
     * @return boolean true if the radius was successfully parsed, false otherwise.
     */
    boolean parseRadius(final String radius, TargetData targetData) {
        try {
            final RadiusParser radiusParser = new RadiusParser(radius.trim());
            final boolean parseSuccessful = radiusParser.getValue() != null;

            if (parseSuccessful) {
                targetData.setRadius(radiusParser.getValue().doubleValue());
            }
            return parseSuccessful;
        } catch (final NumericParserException e) {
            log.debug("Parsing error for radius " + radius + "\n"
                          + e.getMessage());
            return false;
        }
    }

    private boolean parseCoordsys(final String coordsys, TargetData targetData) {
        final String value = coordsys.trim();
        final CaseInsensitiveStringComparator comparator =
            new CaseInsensitiveStringComparator();

        // Single return value.
        final boolean success;

        if ((comparator.compare(value, CoordSys.ICRS.getValue()) == 0) ||
                (comparator.compare(value, CoordSys.FK5.getValue()) == 0) ||
                (comparator.compare(value, CoordSys.J2000.getValue()) == 0) ||
                (comparator.compare(value, CoordSys.J2000_0.getValue()) == 0)) {
            targetData.setCoordsys(value);
            success = true;
        } else if (comparator.compare(value, CoordSys.GAL.getValue()) == 0) {
            if (targetData.getRA() != null && targetData.getDec() != null) {
                Point2D.Double point = new Point2D.Double(targetData.getRA(),
                                                          targetData.getDec());
                point = wcscon.gal2fk5(point);
                targetData.setRA(point.x);
                targetData.setDec(point.y);
            }
            targetData.setCoordsys(value);
            success = true;
        } else if ((comparator.compare(value, CoordSys.B1950.getValue()) == 0) ||
                       (comparator.compare(value, CoordSys.B1950_0.getValue()) == 0) ||
                       (comparator.compare(value, CoordSys.FK4.getValue()) == 0)) {
            if (targetData.getRA() != null && targetData.getDec() != null) {
                Point2D.Double point = new Point2D.Double(targetData.getRA(),
                                                          targetData.getDec());
                point = wcscon.fk425(point);
                targetData.setRA(point.x);
                targetData.setDec(point.y);
            }
            targetData.setCoordsys(value);
            success = true;
        } else {
            success = false;
        }

        return success;
    }

    protected Range<Double> getRARange(final String query)
        throws RangeParserException {
        RangeParser rangeParser = new RangeParser(query, null);
        Range<String> sRange = rangeParser.parse();

        // Assume degrees first.
        Double value = parseRA(sRange.getValue());
        Double lower = parseRA(sRange.getLowerValue());
        Double upper = parseRA(sRange.getUpperValue());

        return new Range<>(sRange.getRange(), value, lower, upper,
                           sRange.getOperand());
    }

    protected Double parseRA(final String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        Double ra;
        try {
            ra = Double.valueOf(query);
        } catch (NumberFormatException ignore) {
            ra = raToDegrees(query);
        }

        return ra;
    }

    protected Range<Double> getDecRange(final String query)
        throws RangeParserException {
        RangeParser rangeParser = new RangeParser(query, null);
        Range<String> sRange = rangeParser.parse();

        // Assume degrees first.
        Double value = parseDec(sRange.getValue());
        Double lower = parseDec(sRange.getLowerValue());
        Double upper = parseDec(sRange.getUpperValue());

        return new Range<>(sRange.getRange(), value, lower, upper,
                           sRange.getOperand());
    }

    protected Double parseDec(final String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        Double dec;
        try {
            dec = Double.valueOf(query);
        } catch (NumberFormatException ignore) {
            dec = decToDegrees(query);
        }

        return dec;
    }

    protected abstract Double raToDegrees(final String ra);

    protected abstract Double decToDegrees(final String dec);

}
