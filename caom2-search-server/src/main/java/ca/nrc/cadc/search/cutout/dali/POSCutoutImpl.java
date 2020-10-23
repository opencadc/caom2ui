/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                            (c) 2020.
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
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.cutout.dali;

import ca.nrc.cadc.dali.Circle;
import ca.nrc.cadc.dali.Point;
import ca.nrc.cadc.dali.Shape;
import ca.nrc.cadc.dali.util.ShapeFormat;
import ca.nrc.cadc.search.cutout.Cutout;
import ca.nrc.cadc.caom2.SpatialSearch;

import ca.nrc.cadc.util.StringUtil;


/**
 * DALI representation of POS cutout
 */
public class POSCutoutImpl implements Cutout
{
    private final ca.nrc.cadc.dali.Shape posCutout;
    private final SpatialSearch spatialSearch;
    private ShapeFormat sf = new ShapeFormat();

    /**
     * ctor
     * @param _spatialSearch used to generate cutout
     */
    public POSCutoutImpl(final SpatialSearch _spatialSearch)
    {
        this.spatialSearch = _spatialSearch;
        this.posCutout = createCutout();
    }

    /**
     * Format this cutout as a String representation.
     *
     * @return String value.  Should never be null.
     */
    @Override
    public final String format()
    {
        return sf.format(posCutout);
    }

    /**
     * Obtain the Shape aspect to this cutout, if any.
     *
     * @return Shape instance, or null if none.
     */
    Shape createCutout()
    {
        final Shape cutout;

        if (this.spatialSearch == null)
        {
            cutout = null;
        }
        else
        {
            // Translate from caom2 Shape to dali Shape (Circle)
            cutout = new Circle(
                new Point(this.spatialSearch.getPosition().getCenter().cval1,  this.spatialSearch.getPosition().getCenter().cval2),
                getRadius()
            );
        }

        return cutout;
    }


    /**
     * Get the radius from the given Spatial entity.  This implementation will
     * happily use the radius in the case of a circle, or default to One (1)
     * Arc Minute otherwise.
     *
     * @param position The Shape object.
     * @return Radius value as double.
     */
    double getRadius()
    {
        final double radius;
        if ( this.spatialSearch.getPosition() instanceof ca.nrc.cadc.caom2.types.Circle )
        {
            radius = ((ca.nrc.cadc.caom2.types.Circle) this.spatialSearch.getPosition()).getRadius();
        }
        else
        {
            radius = ONE_ARC_MIN_AS_DEG;
        }

        return radius;
    }
}
