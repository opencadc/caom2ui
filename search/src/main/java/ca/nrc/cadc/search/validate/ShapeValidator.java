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

package ca.nrc.cadc.search.validate;


import ca.nrc.cadc.astro.ConversionUtil;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.astro.AngleUnitConverter;
import java.awt.geom.Point2D;
import jsky.coords.wcscon;


/**
 * Class to validate Shape forms.
 * 
 * @author jburke
 *
 */
public class ShapeValidator
{
    /**
     * Allowed coordinate systems.
     */
    private static final String GAL = "Galactic II";
    private static final String B1950 = "B1950";

    private final ConversionUtil conversionUtil;


    public ShapeValidator()
    {
        this(new ConversionUtil());
    }

    public ShapeValidator(final ConversionUtil conversionUtil)
    {
        this.conversionUtil = conversionUtil;
    }

    /**
     * Validates the value and returns a Double of the value
     * converted to degrees. If the value is null, or an empty 
     * String, null is returned. A Double is created from the 
     * value, then converted to degress and returned.
     * 
     * @param raForm        The form value of the RA.
     * @param decForm       The form value of the DEC.
     * @param coordsys      The Coordinate system.
     *
     * @throws ValidationException if the value cannot be validated.
     * @return Double of the value in degrees.
     */
    public Point2D.Double validateCoordinates(final String raForm,
                                              final String decForm,
                                              final String coordsys)
            throws ValidationException
    {
        double ra;
        double dec;

        try
        {
            ra = conversionUtil.raToDegrees(raForm);
        }
        catch (IllegalArgumentException e)
        {
            throw new ValidationException("Invalid RA[" + raForm + "] "
                                          + e.getMessage());
        }

        try
        {
            dec = conversionUtil.decToDegrees(decForm);
        }
        catch (IllegalArgumentException e)
        {
            throw new ValidationException("Invalid DEC[" + decForm + "] "
                                          + e.getMessage());
        }

        final Point2D.Double point = new Point2D.Double(ra, dec);

        switch (coordsys)
        {
            case GAL:
                return wcscon.gal2fk5(point);
            case B1950:
                return wcscon.fk425(point);
            default:
                return point;
        }
    }
    
    /**
     * Validates the value and returns a Double of the value 
     * converted to degrees. If the value is null, or an empty 
     * String, null is returned. A Double is created from the 
     * value, then converted to degrees and returned.
     * 
     * @param value         The value to validate.
     * @param coordsys      The Coordinate System.
     * @throws ValidationException if the value cannot be validated.
     * @return Double of the value in degrees.
     */
    public Double validateDEC(String value, String coordsys)
        throws ValidationException
    {
        try
        {
            return conversionUtil.decToDegrees(value);
        }
        catch (IllegalArgumentException e)
        {
            throw new ValidationException(e.getMessage());
        }
    }
    
    /**
     * Validates the value and returns a Double of the value 
     * based on the given unit. If the value is null, or an empty 
     * String, null is returned. A Double is created from the 
     * value, then converted to the given unit and returned.
     * 
     * @param value The value to validate.
     * @param unit The unit for the value.
     * @throws ValidationException if the value cannot be validated.
     * @return Double of the validated value.
     */
    public Double validateRadius(final String value, final String unit)
        throws ValidationException
    {
        final Double d = validate(value);

        if (d == null)
        {
            return null;
        }

        try
        {
            final AngleUnitConverter converter = new AngleUnitConverter();
            return converter.convert(d, unit);
        }
        catch (IllegalArgumentException e)
        {
            throw new ValidationException(e.getMessage());
        }
    }
    
    private Double validate(final String value) throws ValidationException
    {
        if (!StringUtil.hasLength(value))
        {
            return null;
        }

        try
        {
            return new Double(value);
        }
        catch (NumberFormatException e)
        {
            throw new ValidationException("Invalid number " + value);
        }
    }
    
}
