// Created on 19-Feb-2006

package ca.nrc.cadc.astro;

import ca.nrc.cadc.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Convert wavelength, frequency, and energy to wavelength in meters.
 *
 * @author pdowler
 * @version $Version$
 */
public class EnergyUnitConverter implements UnitConverter
{
    private final static String[] ALL_UNITS;

    private final static String[] FREQ_UNITS = new String[]{"Hz", "kHz", "MHz",
                                                            "GHz"};
    private final static double[] FREQ_MULT = new double[]{1.0, 1.0e3, 1.0e6,
                                                           1.0e9};

    private final static String[] EN_UNITS = new String[]{"eV", "keV", "MeV",
                                                          "GeV"};
    private final static double[] EN_MULT = new double[]{1.0, 1.0e3, 1.0e6,
                                                         1.0e9};

    private final static String[] WAVE_UNITS = new String[]{"m", "cm", "mm",
                                                            "um", "µm", "nm",
                                                            "A"};
    private final static double[] WAVE_MULT = new double[]{1.0, 1.0e-2, 1.0e-3,
                                                           1.0e-6, 1.0e-6,
                                                           1.0e-9, 1.0e-10};

    // Lay out the actual units only once, then coalesce them.
    static
    {
        final List<String> allUnitList =
                new ArrayList<>(Arrays.asList(FREQ_UNITS));
        allUnitList.addAll(Arrays.asList(EN_UNITS));
        allUnitList.addAll(Arrays.asList(WAVE_UNITS));

        ALL_UNITS = allUnitList.toArray(new String[allUnitList.size()]);
    }

    private static final double c = 2.9979250e8;    // m/sec
    private static final double h = 6.62620e-27;    // erg/sec
    private static final double eV = 1.602192e-12;    // erg


    public String[] getSupportedUnits()
    {
        return ALL_UNITS;
    }

    /**
     * Convert the supplied value/units to a value expressed in core energy
     * units.
     *
     * @param value     The value to convert.
     * @param units     The unit to convert to.
     * @return          double conversion to metres.
     */
    public double convert(double value, String units)
    {
        return toMetres(value, units);
    }

    /**
     * Convert the energy value d from the specified units to wavelength in meters.
     *
     * @param d
     * @param units
     * @return wavelength in meters
     */
    public double toMetres(double d, String units)
    {
        int i = ArrayUtil.matches("^" + units + "$", FREQ_UNITS, true);
        if (i != -1)
        {
            return freqToMetres(d, i);
        }

        i = ArrayUtil.matches("^" + units + "$", EN_UNITS, true);
        if (i != -1)
        {
            return energyToMetres(d, i);
        }

        i = ArrayUtil.matches("^" + units + "$", WAVE_UNITS, true);
        if (i != -1)
        {
            return wavelengthToMetres(d, i);
        }

        throw new IllegalArgumentException("Unknown units: " + units);
    }

    /**
     * Convert the energy value d from the specified units to frequency in Hz.
     *
     * @param d
     * @param units
     * @return frequency in Hz
     */
    public double toHz(double d, String units)
    {
        int i = ArrayUtil.matches("^" + units + "$", FREQ_UNITS, true);
        if (i != -1)
        {
            return freqToHz(d, i);
        }

        i = ArrayUtil.matches("^" + units + "$", EN_UNITS, true);
        if (i != -1)
        {
            return energyToHz(d, i);
        }

        i = ArrayUtil.matches("^" + units + "$", WAVE_UNITS, true);
        if (i != -1)
        {
            return wavelengthToHz(d, i);
        }

        throw new IllegalArgumentException("unknown units: " + units);
    }

    /**
     * Compute the range of energy values to a wavelength width in meters.
     *
     * @param d1
     * @param d2
     * @param units
     * @return delta lambda in meters
     */
    public double toDeltaMeters(double d1, double d2, String units)
    {
        double w1 = toMetres(d1, units);
        double w2 = toMetres(d2, units);
        return Math.abs(w2 - w1);
    }

    /**
     * Compute the range of energy values to a frequency width in Hz.
     *
     * @param d1
     * @param d2
     * @param units
     * @return delta nu in Hz
     */
    public double toDeltaHz(double d1, double d2, String units)
    {
        double f1 = toHz(d1, units);
        double f2 = toHz(d2, units);
        return Math.abs(f2 - f1);
    }

    /**
     * Frequency to metre conversion.
     *
     * @param d
     * @param i
     * @return
     */
    private double freqToMetres(double d, int i)
    {
        final double nu = d * FREQ_MULT[i];
        return c / nu;
    }

    /**
     * Any energy value to metres.
     *
     * @param d
     * @param i
     * @return
     */
    private double energyToMetres(double d, int i)
    {
        final double e = eV * d * EN_MULT[i];
        return c * h / e;
    }

    private double wavelengthToMetres(double d, int i)
    {
        return d * WAVE_MULT[i];
    }

    private double freqToHz(double d, int i)
    {
        return d * FREQ_MULT[i];
    }

    private double energyToHz(double d, int i)
    {
        double w = energyToMetres(d, i);
        return c / w;
    }

    private double wavelengthToHz(double d, int i)
    {
        double w = d * WAVE_MULT[i];
        return c / w;
    }

    public static void main(String[] args)
    {
        EnergyUnitConverter euc = new EnergyUnitConverter();

        for (String u : WAVE_UNITS)
        {
            System.out.println("absolute: 5" + u + " = " + euc
                    .toMetres(5.0, u) + "m == " + euc.toHz(5.0, u) + "Hz");
            System.out.println("relative: 4-6" + u + " = " + euc
                    .toDeltaMeters(4.0, 6.0, u) + "m == " + euc
                                       .toDeltaHz(4.0, 6.0, u) + "Hz");
        }

        System.out.println("========");
        for (String u : FREQ_UNITS)
        {
            System.out.println("absolute: 5" + u + " = " + euc
                    .toMetres(5.0, u) + "m == " + euc.toHz(5.0, u) + "Hz");
            System.out.println("relative: 4-6" + u + " = " + euc
                    .toDeltaMeters(4.0, 6.0, u) + "m == " + euc
                                       .toDeltaHz(4.0, 6.0, u) + "Hz");
        }

        System.out.println("========");
        for (String u : EN_UNITS)
        {
            System.out.println("absolute: 5" + u + " = " + euc
                    .toMetres(5.0, u) + "m == " + euc.toHz(5.0, u) + "Hz");
            System.out.println("relative: 4-6" + u + " = " + euc
                    .toDeltaMeters(4.0, 6.0, u) + "m == " + euc
                                       .toDeltaHz(4.0, 6.0, u) + "Hz");
        }
    }
}
