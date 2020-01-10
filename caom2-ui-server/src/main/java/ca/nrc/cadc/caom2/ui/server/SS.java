/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2019.                            (c) 2019.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *  $Revision: 5 $
 *  (First Published prior to 2011)
 *  Last update: Dec 2019
 *
 ************************************************************************
 */
package ca.nrc.cadc.caom2.ui.server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import ca.nrc.cadc.caom2.*;
import ca.nrc.cadc.caom2.Observable;
import ca.nrc.cadc.caom2.wcs.*;
import ca.nrc.cadc.caom2.types.Point;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.util.StringUtil;


/**
 * String Serializer.
 *
 * @author pdowler
 */
public class SS {

    private static final DateFormat FORMAT_UTC = DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT, DateUtil.UTC);

    public static String toString(Number s) {
        return (s == null) ? "" : s.toString();
    }

    public static String toString(String s) {
        return (s == null) ? "" : s.trim();
    }

    public static String toString(Date d) {
        return (d == null) ? "" : FORMAT_UTC.format(d);
    }

    public static String getPlanePosition(Plane p) {
        final StringBuilder sb = new StringBuilder();

        try {
            final Position comp = p.position;

            if (comp != null) {
                sb.append("bounds: ").append(comp.bounds);
                sb.append("<br>").append("dimension: ").append(comp.dimension);
                sb.append("<br>").append("resolution: ").append(comp.resolution);
                sb.append("<br>").append("resolutionBounds: ").append(comp.resolutionBounds);
                sb.append("<br>").append("sampleSize: ").append(comp.sampleSize);
                sb.append("<br>").append("timeDependent: ").append(comp.timeDependent);
            }
        } catch (Exception ex) {
            sb.append("<span class=\"error\">ERROR: failed to compute: </span>");
            sb.append(ex);
        }

        return sb.toString();
    }

    public static String getPlaneEnergy(Plane p) {
        StringBuilder sb = new StringBuilder();
        try {
            final Energy comp = p.energy;

            if (comp != null) {
                sb.append("bandpassName: ").append(comp.bandpassName);
                sb.append("<br>").append("bounds: ").append(comp.bounds);
                sb.append("<br>").append("dimension: ").append(comp.dimension);
                sb.append("<br>").append("resolvingPower: ")
                    .append(comp.resolvingPower);
                sb.append("<br>").append("resolvingPowerBounds: ")
                    .append(comp.resolvingPowerBounds);
                sb.append("<br>").append("sampleSize: ").append(comp.sampleSize);

                sb.append("<br>").append("energyBands: ");
                Set<EnergyBand> eb = comp.getEnergyBands();
                Iterator i = eb.iterator();
                while (i.hasNext()) {
                    sb.append(i.next());
                }
                sb.append("<br>").append("transition: ").append(comp.transition);
                sb.append("<br>").append("restwav: ").append(comp.restwav);
            }
        } catch (Exception ex) {
            sb.append(
                    "<span class=\"error\">ERROR: failed to compute: </span>");
            sb.append(ex);
        }
        return sb.toString();
    }

    public static String getPlaneTime(Plane p) {
        StringBuilder sb = new StringBuilder();
        try {
            final Time comp = p.time;

            if (comp != null) {
                sb.append("bounds: ").append(comp.bounds);
                sb.append("<br>").append("dimension: ").append(comp.dimension);
                sb.append("<br>").append("resolution: ").append(comp.resolution);
                sb.append("<br>").append("resolutionBounds: ").append(comp.resolutionBounds);
                sb.append("<br>").append("sampleSize: ").append(comp.sampleSize);
                sb.append("<br>").append("exposure: ").append(comp.exposure);
            }
        } catch (Exception ex) {
            sb.append(
                    "<span class=\"error\">ERROR: failed to compute: </span>");
            sb.append(ex);
        }
        return sb.toString();
    }

    public static String getPlanePolarization(Plane p) {
        StringBuilder sb = new StringBuilder();
        try {
            Polarization comp = p.polarization;
            if (comp != null) {
                sb.append("states: ");
                if (comp.states != null) {
                    for (PolarizationState ps : comp.states) {
                        sb.append(ps.stringValue()).append(" ");
                    }
                }
                sb.append("<br>").append("dimension: ").append(comp.dimension);
            }
        } catch (Exception ex) {
            sb.append(
                    "<span class=\"error\">ERROR: failed to compute: </span>");
            sb.append(ex);
        }
        return sb.toString();
    }

    public static String getPlaneCustom(Plane p) {
        final StringBuilder sb = new StringBuilder();

        try {
            final CustomAxis customAxis = p.custom;

            if (customAxis != null) {
                sb.append("ctype: ").append(customAxis.getCtype());
                sb.append("<br>bounds: ").append(customAxis.bounds);
                sb.append("<br>dimension: ").append(customAxis.dimension);
            }
        } catch (Exception ex) {
            sb.append("<span class=\"error\">ERROR: failed to get custom axis: </span>");
            sb.append(ex);
        }

        return sb.toString();
    }

    public static String toString(Requirements t) {
        return (t == null) ? "" : "flag: " + t.getFlag().getValue();
    }

    public static String toString(DataQuality t) {
        return (t == null) ? "" : "flag: " + t.getFlag().getValue();
    }

    public static String toString(Observable o) {
        return (o == null) ? "" : "ucd: " + o.getUCD();
    }

    public static String toString(DataProductType t) {
        return (t == null) ? "" : t.getValue();
    }

    public static String toString(ObservationIntentType t) {
        return (t == null) ? "" : t.getValue();
    }

    public static String toString(ProductType t) {
        return (t == null) ? "" : t.getValue();
    }

    public static String toString(ReleaseType t) {
        if (t == null) {
            return "";
        }
        return t.getValue();
    }

    public static String toString(CalibrationLevel c) {
        return (c == null) ? "" : c.getValue() + " (" + c.stringValue() + ")";
    }

    public static String toString(Proposal p) {
        return (p == null) ? "" : "ID: " +
                                  p.getID() +
                                  "<br>project: " +
                                  p.project +
                                  "<br>PI: " +
                                  p.pi +
                                  "<br>title: " +
                                  p.title +
                                  "<br>keywords: " +
                                  encodeListString(p.getKeywords());
    }

    public static String toString(Telescope t) {
        return (t == null) ? "" : "name: " +
                                  t.getName() +
                                  "<br>geocentric location: " +
                                  t.geoLocationX +
                                  "," +
                                  t.geoLocationY +
                                  "," +
                                  t.geoLocationZ +
                                  "<br>keywords: " +
                                  encodeListString(t.getKeywords());
    }

    public static String toString(Instrument i) {
        return (i == null) ? "" : "name: " + i.getName() + "<br>keywords: "
                                  + encodeListString(i.getKeywords());
    }

    public static String toString(Target t) {
        final StringBuilder sb = new StringBuilder();

        if (t != null) {
            sb.append("name: ");
            sb.append(t.getName());
            sb.append("<br>type: ");

            if (t.type != null) {
                sb.append(t.type.getValue());
            }

            sb.append("<br>redshift: ");
            sb.append(t.redshift);
            sb.append("<br>standard: ");
            sb.append(t.standard);
            sb.append("<br>moving: ");
            sb.append(t.moving);
            sb.append("<br>keywords: ");
            sb.append(encodeListString(t.getKeywords()));
            sb.append("<br>targetID:  ");

            if (t.targetID != null) {
                sb.append(t.targetID);
            }
        }

        return sb.toString();
    }

    public static String toString(TargetPosition tp) {
        final StringBuilder sb = new StringBuilder();

        if (tp != null) {
            final Point coords = tp.getCoordinates();
            sb.append("coordsys: ");
            sb.append(tp.getCoordsys());
            sb.append("<br>coordinates: ");
            sb.append(coords.cval1 + ", " + coords.cval2);
        }

        return sb.toString();
    }

    public static String toString(Algorithm a) {
        return (a == null) ? "" : "name: " + a.getName();
    }

    public static String toString(Environment e) {
        return (e == null) ? "" :
               "ambientTemp: " + e.ambientTemp + "<br>elevation: "
               + e.elevation + "<br>humidity: " + e.humidity + "<br>seeing: "
               + e.seeing + "<br>tau: " + e.tau + "<br>wavelengthTau: "
               + e.wavelengthTau + "<br>photometric: " + e.photometric;
    }

    public static String toString(Metrics m) {
        return (m == null) ? "" : "sourceNumberDensity: " +
            m.sourceNumberDensity +
            "<br>background: " +
            m.background +
            "<br>backgroundStddev: " +
            m.backgroundStddev +
            "<br>fluxDensityLimit: " +
            m.fluxDensityLimit +
            "<br>magLimit: " +
            m.magLimit +
            "<br>sampleSNR: " +
            m.sampleSNR;
    }
    
    public static String toMemberString(final String contextPath, final Observation o, final String parentID) {
        final StringBuilder sb = new StringBuilder();

        if ((o instanceof DerivedObservation)) {
            final DerivedObservation dObs = (DerivedObservation) o;
            final URI parentURI = URI.create(parentID);
            final String parentPath = parentURI.getPath();
            final String extraParentPath;

            if (StringUtil.hasLength(parentPath)) {
                final String trimmedPath;
                if (parentPath.trim().endsWith("/")) {
                    trimmedPath = parentPath.trim().substring(0, parentPath.length() - 1);
                } else {
                    trimmedPath = parentPath.trim();
                }

                extraParentPath = trimmedPath.substring(0, trimmedPath.lastIndexOf("/"));
            } else {
                extraParentPath = null;
            }

            for (final ObservationURI u : dObs.getMembers()) {
                final URI observationURI = u.getURI();
                final String schemeSpecificPart = observationURI.getSchemeSpecificPart();
                final String[] collectionObsID = schemeSpecificPart.split("/");
                final String linkIDQueryParameter;
                if (StringUtil.hasLength(extraParentPath)) {
                    linkIDQueryParameter = String.format("%s://%s%s/%s?%s", parentURI.getScheme(),
                                                         parentURI.getAuthority(), extraParentPath,
                                                         collectionObsID[0], collectionObsID[1]);
                } else {
                    linkIDQueryParameter = String.format("%s://%s/%s?%s", parentURI.getScheme(),
                                                         parentURI.getAuthority(),
                                                         collectionObsID[0], collectionObsID[1]);
                }

                sb.append("<a href=\"").append(contextPath).append("/view");
                sb.append("?ID=").append(NetUtil.encode(linkIDQueryParameter));
                sb.append("\">");
                sb.append(observationURI.toASCIIString());
                sb.append("</a> ");
            }
        }

        return sb.toString();
    }

    public static String toString(final URI uri) throws MalformedURLException {
        final MessageFormat format = new MessageFormat("<a class=\"provenance-reference\" href=\"{0}\">{0}</a>");
        //Default is to return an empty string if the uri is null
        String uriString = "";
        if (uri != null ) {
            final URL url = uri.isAbsolute() ? uri.toURL() : new URL("http://" + uri.toString());
            uriString = format.format(new Object[] {url.toExternalForm()});
        }
        return uriString;
    }

    // URIs for checksums start with md5: which fails the toString(URI) above with a MalformedURLException
    // This function guards against nulls in the data set which cause a Null Pointer Exception
    // if/when encountered during jsp processing.
    public static String getChecksum(final URI checksumUri) {
        return (checksumUri == null) ? "" : checksumUri.toString();
    }

    public static String toString(Provenance p) {
        final StringBuilder sb = new StringBuilder();

        if (p != null) {
            sb.append("name: ");
            sb.append(p.getName());
            sb.append("<br>reference: ");
            try {
                sb.append((p.reference == null) ? "null" : SS.toString(p.reference));
            } catch (MalformedURLException e) {
                sb.append(String.format("Unable to display Reference URL %s", p.reference.toString()));
            }
            sb.append("<br>version: ");
            sb.append(p.version);
            sb.append("<br>project: ");
            sb.append(p.project);
            sb.append("<br>producer: ");
            sb.append(p.producer);
            sb.append("<br>runID: ");
            sb.append(p.runID);

            sb.append("<br>lastExecuted: ");
            if (p.lastExecuted != null) {
                sb.append(FORMAT_UTC.format(p.lastExecuted));
            } else {
                sb.append("null");
            }

            sb.append("<br>keywords: ");
            sb.append(encodeListString(p.getKeywords()));

            sb.append("<br>inputs: ");
            for (PlaneURI pu : p.getInputs()) {
                sb.append(pu.getURI().toASCIIString()).append(" ");
            }
        }

        return sb.toString();
    }

    private static String encodeListString(Collection<String> slist) {
        StringBuilder sb = new StringBuilder();
        for (String s : slist) {
            sb.append(s).append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // trailing space
        }
        return sb.toString();
    }

    public static String toString(SpatialWCS wcs) {
        final StringBuilder sb = new StringBuilder();

        if (wcs != null) {
            sb.append("coordsys: ");
            sb.append(wcs.coordsys);
            sb.append("<br>equinox: ");
            sb.append(wcs.equinox);
            sb.append("<br>resolution: ");
            sb.append(wcs.resolution);

            sb.append("<br>ctype1 ctype2: ");
            sb.append(wcs.getAxis().getAxis1().getCtype());
            sb.append(" ");
            sb.append(wcs.getAxis().getAxis2().getCtype());
            sb.append("<br>cunit1 cunit2: ");
            sb.append(wcs.getAxis().getAxis1().getCunit());
            sb.append(" ");
            sb.append(wcs.getAxis().getAxis2().getCunit());
            if (wcs.getAxis().error1 != null) {
                sb.append("<br>csyer1 crder1: ");
                sb.append(wcs.getAxis().error1.syser);
                sb.append(" ");
                sb.append(wcs.getAxis().error1.rnder);
            }
            if (wcs.getAxis().error2 != null) {
                sb.append("<br>csyer2 crder2: ");
                sb.append(wcs.getAxis().error2.syser);
                sb.append(" ");
                sb.append(wcs.getAxis().error2.rnder);
            }

            if (wcs.getAxis().range != null) {
                sb.append("<br>range: (pix) ");
                sb.append(wcs.getAxis().range.getStart().getCoord1().pix);
                sb.append(",");
                sb.append(wcs.getAxis().range.getStart().getCoord2().pix);
                sb.append(" -> ");
                sb.append(wcs.getAxis().range.getEnd().getCoord1().pix);
                sb.append(",");
                sb.append(wcs.getAxis().range.getEnd().getCoord2().pix);
                sb.append("<br>range: (sky) ");
                sb.append(wcs.getAxis().range.getStart().getCoord1().val);
                sb.append(",");
                sb.append(wcs.getAxis().range.getStart().getCoord2().val);
                sb.append(" -> ");
                sb.append(wcs.getAxis().range.getEnd().getCoord1().val);
                sb.append(",");
                sb.append(wcs.getAxis().range.getEnd().getCoord2().val);
            } else {
                sb.append("<br>range: null");
            }

            if (wcs.getAxis().bounds != null) {
                if (wcs.getAxis().bounds instanceof CoordCircle2D) {
                    sb.append("<br>bounds: (sky) ");
                    CoordCircle2D cc = (CoordCircle2D) wcs.getAxis().bounds;
                    sb.append(cc.getCenter().coord1);
                    sb.append(",");
                    sb.append(cc.getCenter().coord2);
                    sb.append(", r=");
                    sb.append(cc.getRadius());
                } else {
                    StringBuilder sky = new StringBuilder();
                    CoordPolygon2D cp = (CoordPolygon2D) wcs.getAxis().bounds;
                    Iterator<ValueCoord2D> i = cp.getVertices().iterator();
                    while (i.hasNext()) {
                        ValueCoord2D v = i.next();
                        sky.append(v.coord1);
                        sky.append(",");
                        sky.append(v.coord2);
                        if (i.hasNext()) {
                            sky.append("  ");
                        }
                    }
                    sb.append("<br>bounds: (sky) ");
                    sb.append(sky.toString());
                }
            } else {
                sb.append("<br>bounds: null");
            }

            if (wcs.getAxis().function != null) {
                sb.append("<br>naxis1: ");
                sb.append(wcs.getAxis().function.getDimension().naxis1);
                sb.append("<br>naxis2: ");
                sb.append(wcs.getAxis().function.getDimension().naxis2);
                sb.append("<br>crpix1: ");
                sb.append(wcs.getAxis().function.getRefCoord().getCoord1().pix);
                sb.append("<br>crpix2: ");
                sb.append(wcs.getAxis().function.getRefCoord().getCoord2().pix);
                sb.append("<br>crval1: ");
                sb.append(wcs.getAxis().function.getRefCoord().getCoord1().val);
                sb.append("<br>crval2: ");
                sb.append(wcs.getAxis().function.getRefCoord().getCoord2().val);
                sb.append("<br>cd11 cd12: ");
                sb.append(wcs.getAxis().function.getCd11());
                sb.append(" ");
                sb.append(wcs.getAxis().function.getCd12());
                sb.append("<br>cd21 cd22: ");
                sb.append(wcs.getAxis().function.getCd21());
                sb.append(" ");
                sb.append(wcs.getAxis().function.getCd22());
            }
        }

        return sb.toString();
    }

    public static String toString(SpectralWCS wcs) {
        final StringBuilder sb = new StringBuilder();

        if (wcs != null) {
            sb.append("bandpassName: ");
            sb.append(wcs.bandpassName);
            sb.append("<br>specsys: ");
            sb.append(wcs.getSpecsys());
            sb.append("<br>ssysobs: ");
            sb.append(wcs.ssysobs);
            sb.append("<br>ssyssrc: ");
            sb.append(wcs.ssyssrc);
            sb.append("<br>restfrq: ");
            sb.append(wcs.restfrq);
            sb.append("<br>restwav: ");
            sb.append(wcs.restwav);
            sb.append("<br>velosys: ");
            sb.append(wcs.velosys);
            sb.append("<br>zsource: ");
            sb.append(wcs.zsource);
            sb.append("<br>velang: ");
            sb.append(wcs.velang);
            sb.append("<br>transition: ");
            sb.append(wcs.transition);
            sb.append("<br>resolvingPower: ");
            sb.append(wcs.resolvingPower);

            fillBuffer(wcs.getAxis(), sb);
        }

        return sb.toString();
    }

    public static String toString(TemporalWCS wcs) {
        final StringBuilder sb = new StringBuilder();

        if (wcs != null) {
            sb.append("<br>timesys: ");
            sb.append(wcs.timesys);
            sb.append("<br>trefpos: ");
            sb.append(wcs.trefpos);
            sb.append("<br>mjdref: ");
            sb.append(wcs.mjdref);
            sb.append("exposure: ");
            sb.append(wcs.exposure);
            sb.append("<br>resolution: ");
            sb.append(wcs.resolution);
            fillBuffer(wcs.getAxis(), sb);
        }

        return sb.toString();
    }

    public static String toString(PolarizationWCS wcs) {
        final StringBuilder sb = new StringBuilder();

        if (wcs != null) {
            fillBuffer(wcs.getAxis(), sb);
        }

        return sb.toString();
    }

    public static String toString(CustomWCS wcs) {
        final StringBuilder sb = new StringBuilder();

        if (wcs != null) {
            fillBuffer(wcs.getAxis(), sb);
        }

        return sb.toString();
    }


    // todo: caom24 add: CustomWCS, similar function to PolarizationWCS toString above...

    public static String toString(ObservableAxis observable) {
        final StringBuilder sb = new StringBuilder();

        if (observable != null) {
            sb.append("ctype: ");
            sb.append(observable.getDependent().getAxis().getCtype());
            sb.append("<br>cunit: ");
            sb.append(observable.getDependent().getAxis().getCunit());
            sb.append("<br>bin: ");
            sb.append(observable.getDependent().getBin());

            if (observable.independent != null) {
                sb.append("<br>independent ctype: ");
                sb.append(observable.independent.getAxis().getCtype());
                sb.append("<br>independent cunit: ");
                sb.append(observable.independent.getAxis().getCunit());
                sb.append("<br>independent bin: ");
                sb.append(observable.independent.getBin());
            }
        }

        return sb.toString();
    }

    private static void fillBuffer(CoordAxis1D axis, StringBuilder sb) {
        sb.append("<br>ctype: ");
        sb.append(axis.getAxis().getCtype());
        sb.append("<br>cunit: ");
        sb.append(axis.getAxis().getCunit());
        sb.append("<br>syser: ");
        if (axis.error != null) {
            sb.append(axis.error.syser);
        }
        sb.append("<br>rnder: ");
        if (axis.error != null) {
            sb.append(axis.error.rnder);
        }

        if (axis.function != null) {
            sb.append("<br>naxis: ");
            sb.append(axis.function.getNaxis());
            sb.append("<br>crpix: ");
            sb.append(axis.function.getRefCoord().pix);
            sb.append("<br>crval: ");
            sb.append(axis.function.getRefCoord().val);
            sb.append("<br>cdelt: ");
            sb.append(axis.function.getDelta());
        } else {
            sb.append("<br>function: null ");
        }

        if (axis.bounds != null) {
            StringBuilder pix = new StringBuilder();
            StringBuilder sky = new StringBuilder();

            for (final Iterator<CoordRange1D> i
                 = axis.bounds.getSamples().iterator(); i.hasNext(); ) {
                CoordRange1D r = i.next();
                pix.append(r.getStart().pix);
                pix.append(" -> ");
                pix.append(r.getEnd().pix);

                if (i.hasNext()) {
                    pix.append(", ");
                }

                sky.append(r.getStart().val);
                sky.append(" -> ");
                sky.append(r.getEnd().val);

                if (i.hasNext()) {
                    sky.append(", ");
                }
            }
            sb.append("<br>bounds: (pix) ");
            sb.append(pix.toString());
            sb.append("<br>bounds: (sky) ");
            sb.append(sky.toString());
        } else {
            sb.append("<br>bounds: null ");
        }

        if (axis.range != null) {
            sb.append("<br>range: (pix) ");
            sb.append(axis.range.getStart().pix);
            sb.append(" -> ");
            sb.append(axis.range.getEnd().pix);
            sb.append("<br>range: (sky) ");
            sb.append(axis.range.getStart().val);
            sb.append(" -> ");
            sb.append(axis.range.getEnd().val);
        } else {
            sb.append("<br>range: null ");
        }
    }

    public static String getMetaReadGroups(final Observation o) throws MalformedURLException {
        Set<URI> metaReadGroups = o.getMetaReadGroups();
        final StringBuilder sb = new StringBuilder();

        if (metaReadGroups != null) {
            Iterator tsi = o.getMetaReadGroups().iterator();
            while (tsi.hasNext()) {
                sb.append(tsi.next().toString());
                sb.append("&nbsp;");
            }
        }
        return sb.toString();
    }

    public static String getMetaReadGroups(final Plane p) throws MalformedURLException {
        Set<URI> metaReadGroups = p.getMetaReadGroups();
        final StringBuilder sb = new StringBuilder();

        if (metaReadGroups != null) {
            Iterator rgIter = p.getMetaReadGroups().iterator();
            while (rgIter.hasNext()) {
                sb.append(rgIter.next().toString());
                sb.append("&nbsp;");
            }
        }
        return sb.toString();
    }

    public static String serializeURISet(Set<URI> uriSet) {
        final StringBuilder sb = new StringBuilder();

        if (uriSet != null) {
            Iterator iter = uriSet.iterator();
            while (iter.hasNext()) {
                sb.append(iter.next().toString());
                sb.append("&nbsp;");
            }
        }
        return sb.toString();
    }

    public static String getCaomEntityID(CaomEntity ce) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td>ID</td> ");
        sb.append("<td>" + ce.getID() + " aka " + ce.getID().getLeastSignificantBits() +  "</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    public static String getCaomEntityPortion(CaomEntity ce) {
        final StringBuilder sb = new StringBuilder();

        sb.append("<tr>");
        sb.append("<td>lastModified</td> ");
        sb.append("<td>" + ce.getLastModified() +  "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>maxLastModified</td> ");
        sb.append("<td>" + ce.getMaxLastModified() +  "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>metaChecksum</td> ");
        sb.append("<td>" + ce.getMetaChecksum() +  "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>accMetaChecksum</td> ");
        sb.append("<td>" + ce.getAccMetaChecksum() +  "</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>metaProducer</td> ");
        sb.append("<td>" + ce.metaProducer +  "</td>");
        sb.append("</tr>");

        return sb.toString();
    }

}
