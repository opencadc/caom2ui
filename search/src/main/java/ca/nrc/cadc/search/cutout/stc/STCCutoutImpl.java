/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
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
 *
 * @author jenkinsd
 * 3/4/13 - 1:54 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.cutout.stc;

import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.types.Circle;
import ca.nrc.cadc.caom2.types.Shape;
import ca.nrc.cadc.search.cutout.Cutout;
import ca.nrc.cadc.search.form.Energy;
import ca.nrc.cadc.stc.*;
import ca.nrc.cadc.util.StringUtil;


/**
 * Spact-time Coordinate representation of a cutout.
 * <p></p>
 * http://www.ivoa.net/Documents/latest/STC-S.html
 * <p></p>
 * For documentation details.
 */
public class STCCutoutImpl implements Cutout
{
    private final SpatialSearch spatialSearch;
    private final IntervalSearch spectralSearch;


    /**
     * Complete constructor.
     */
    public STCCutoutImpl(final SpatialSearch _spatialSearch,
                         final IntervalSearch _spectralSearch)
    {
        this.spatialSearch = _spatialSearch;
        this.spectralSearch = _spectralSearch;
    }


    /**
     * Format this cutout as a String representation.
     *
     * @return String value.  Should never be null.
     */
    @Override
    public final String format()
    {
        final Region region = createRegion();
        final SpectralInterval spectralInterval = createSpectralInterval();
        final AstroCoordArea astroCoordArea =
                new AstroCoordArea(region, spectralInterval);

        return STC.format(astroCoordArea);
    }


    public SpatialSearch getSpatialSearch()
    {
        return spatialSearch;
    }

    public IntervalSearch getSpectralSearch()
    {
        return spectralSearch;
    }

    /**
     * Obtain the Regional aspect to this cutout, if any.
     *
     * @return Region instance, or null if none.
     */
    Region createRegion()
    {
        final Region region;

        if (getSpatialSearch() == null)
        {
            region = null;
        }
        else
        {
            final Shape position = getSpatialSearch().getPosition();
            region = new ca.nrc.cadc.stc.Circle(Frame.ICRS, null, null,
                                                position.getCenter().cval1,
                                                position.getCenter().cval2,
                                                getRadius(position));
        }

        return region;
    }

    /**
     * Obtain the SpectralInterval component to this cutout, if any.
     *
     * @return SpectralInterval instance, or null if none.
     */
    SpectralInterval createSpectralInterval()
    {
        final SpectralInterval spectralInterval;

        if (getSpectralSearch() == null)
        {
            spectralInterval = null;
        }
        else
        {
            final IntervalSearch intervalSearchTemplate = getSpectralSearch();
            final String units = intervalSearchTemplate.getUnits();
            spectralInterval = new SpectralInterval(
                    intervalSearchTemplate.getLower(),
                    intervalSearchTemplate.getUpper(),
                    SpectralUnit.valueOf(
                            !StringUtil.hasText(units)
                            ? Energy.NORMALIZED_UNITS
                            : units));
        }

        return spectralInterval;
    }

    /**
     * Get the radius from the given Spatial entity.  This implementation will
     * happily use the radius in the case of a circle, or default to One (1)
     * Arc Minute otherwise.
     *
     * @param position The Shape object.
     * @return Radius value as double.
     */
    double getRadius(final Shape position)
    {
        final double radius;
        if (position instanceof Circle)
        {
            radius = ((Circle) position).getRadius();
        }
        else
        {
            radius = ONE_ARC_MIN_AS_DEG;
        }

        return radius;
    }
}
