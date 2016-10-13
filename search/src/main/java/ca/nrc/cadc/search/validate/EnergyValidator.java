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

import ca.nrc.cadc.search.form.Energy;
import ca.nrc.cadc.util.ArrayUtil;
import ca.nrc.cadc.util.StringUtil;

import ca.nrc.cadc.astro.EnergyUnitConverter;


/**
 * Class to validate energy values.
 *
 * @author jburke
 */
public class EnergyValidator extends NumberValidator
{
    public final static String[] FREQUENCY_UNITS =
            new String[]{"Hz", "kHz", "MHz", "GHz"};


    public EnergyValidator(final String utype, final Number value,
                           final String unit)
    {
        super(utype, value, unit);
    }

    public EnergyValidator()
    {
        // empty
    }


    /**
     * Validates the value and returns a Double of the value.
     * If the value is null, then null is returned. If a Double can be created from the value,
     * the Double is returned. Else a ValidationException is thrown.
     *
     * @return Double of the validated value.
     * @throws ValidationException if the value cannot be validated.
     */
    public Double validate() throws ValidationException
    {
        if (value == null)
        {
            return null;
        }

        if (StringUtil.hasLength(unit))
        {
            final EnergyUnitConverter energyUnitConverter =
                    new EnergyUnitConverter();
            final String[] supportedUnits =
                    energyUnitConverter.getSupportedUnits();

            for (final String supportedUnit : supportedUnits)
            {
                if (unit.equalsIgnoreCase(supportedUnit))
                {
                    try
                    {
                        // Story 888 - For frequency matches, convert the value
                        // to the Hz value for all energy but the Spectral
                        // Coverage.  Otherwise, back to metres.
                        // jenkinsd 2012.01.31
                        //
                        // Update
                        // Story 1502
                        // Adding Rest Frequency follows the same logic.
                        if ((ArrayUtil.matches("^" + unit + "$",
                                               FREQUENCY_UNITS, true) >= 0)
                            && (!Energy.useMeter(utype)))
                        {
                            return energyUnitConverter.toHz(
                                    value.doubleValue(), unit);
                        }
                        else
                        {
                            return energyUnitConverter.toMeters(
                                    value.doubleValue(), unit);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        throw new ValidationException("Invalid number "
                                                      + value);
                    }
                }
            }

            throw new ValidationException("Unsupported unit " + unit);
        }

        return value.doubleValue();
    }

}
