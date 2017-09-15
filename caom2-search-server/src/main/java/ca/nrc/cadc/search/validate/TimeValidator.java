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

import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.util.StringUtil;


/**
 * Class to validate time values.
 *
 * @author jburke
 */
public class TimeValidator extends NumberValidator
{
    public TimeValidator(final String utype, final Number value, final String unit)
    {
        super(utype, value, unit);
    }

    public TimeValidator()
    {
        // empty
    }


    /**
     * Validates the value as a time and returns a Double as a representation
     * of days for Time utypes, and Seconds (or arcseconds) for all others.
     *
     * @return Double of the validated value.
     * @throws ValidationException if the value cannot be validated.
     */
    public Double validate() throws ValidationException
    {
        // Return null if the value is null.
        if (value == null)
        {
            return null;
        }

        // If there is no unit, return a Double of the value.
        if (!StringUtil.hasLength(unit))
        {
            return value.doubleValue();
        }

        // Check that the unit is supported.
        if (!unit.matches("^[aA]?[rR]?[cC]?[mM|sS]+.*$")
            && !unit.matches("^[dD|hH|sS|mM|yY].*$"))
        {
            throw new ValidationException("Unsupported unit " + unit);
        }

        try
        {
            final Double d;
            if (utype.equals("Plane.time.bounds.width"))
            {
                // RT 50858, Span Time uses 'day' as default unit
                d = validateWithDayAsDefault();
            }
            else
            {
                d = validateWithSecondAsDefault();
            }

            return d;
        }
        catch (NumberFormatException ignore)
        {
        }

        throw new ValidationException("Failed to parse as number " + value);
    }

    /*
     * Unit of value is 'day'.
     */
    private double validateWithDayAsDefault() throws NumberFormatException
    {
        final Double d;

        if (unit.matches("^[yY].*$"))
        {
            d = value.doubleValue() * 365.0;
        }
        else if (unit.matches("^[dD].*$"))
        {
            d = value.doubleValue()
                / (ObsModel.isAngleUtype(utype) ? 24.0 : 1.0);
        }
        else if (unit.matches("^[hH].*$"))
        {
            d = value.doubleValue() / 24;
        }
        else if (unit.matches("^[aA]?[rR]?[cC]?[mM]+.*$"))
        {
            d = value.doubleValue() / (60.0 * 24);
        }
        else if (unit.matches("^[aA]?[rR]?[cC]?[sS]+.*$"))
        {
            d = value.doubleValue() / (60.0 * 60.0 * 24);
        }
        else
        {
            d = value.doubleValue();
        }

        return d;
    }

    /*
     * Unit of value is 'second'.
     */
    private double validateWithSecondAsDefault() throws NumberFormatException
    {
        final Double d;

        if (unit.matches("^[yY].*$"))
        {
            d = value.doubleValue() * 60.0 * 60.0 * 24.0 * 365.0;
        }
        else if (unit.matches("^[dD].*$"))
        {
            d = value.doubleValue() * 60.0 * 60.0
                * (ObsModel.isAngleUtype(utype) ? 1.0 : 24.0);
        }
        else if (unit.matches("^[hH].*$"))
        {
            d = value.doubleValue() * 60 * 60;
        }
        else if (unit.matches("^[aA]?[rR]?[cC]?[mM]+.*$"))
        {
            d = value.doubleValue() * 60.0;
        }
        else
        {
            d = value.doubleValue();
        }

        return d;
    }
}
