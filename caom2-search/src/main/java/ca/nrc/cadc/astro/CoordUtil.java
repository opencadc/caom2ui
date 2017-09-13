// Created on 8-Feb-2006

package ca.nrc.cadc.astro;

import java.text.NumberFormat;
import java.util.StringTokenizer;

/**
 * Simple astronomical coordinate conversion utility.
 *
 * @author pdowler
 * @version $Version$
 */
public class CoordUtil
{
    // the unicode degree symbol
    public static String DEGREE_SYMBOL = Character.toString((char) 0x00b0);

    private static String raSeparators = "'\"hmsHMS:";
    private static String decSeparators = "'\"dmsDMS:" + DEGREE_SYMBOL;

    private static NumberFormat raFormat = NumberFormat.getInstance();
    private static NumberFormat decFormat = NumberFormat.getInstance();

    static
    {
        raFormat.setMaximumFractionDigits(1);
        raFormat.setMinimumFractionDigits(1);
        raFormat.setMaximumIntegerDigits(2);
        raFormat.setMinimumIntegerDigits(2);

        decFormat.setMaximumFractionDigits(1);
        decFormat.setMinimumFractionDigits(1);
        decFormat.setMaximumIntegerDigits(2);
        decFormat.setMinimumIntegerDigits(2);
    }

    /*
     * Convert the ra,dec values in degrees to sexigessimal format. This is a
     * convenience method that calls degreesToRA() and degreesToDEC().
     *
     * @return String[2] with ra and dec
     */
    public static String[] degreesToSexigessimal(double ra, double dec)
    {
        return new String[]{degreesToRA(ra), degreesToDEC(dec)};
    }

    public static String degreesToRA(double val)
    {
        // raneg reduction to [0.0,360.0)
        while (val < 0.0)
        {
            val += 360.0;
        }
        while (val >= 360.0)
        {
            val -= 360.0;
        }

        // 24 hours/360 degrees = 15 deg/hour
        int h = (int) (val / 15.0);
        val -= h * 15.0;
        // 15 deg/hour == 0.25 deg/min == 4 min/deg
        int m = (int) (val * 4.0);
        val -= m / 4.0;
        // 4 min/deg == 240 sec/deg
        val *= 240.0;

        String hh = Integer.toString(h);
        String mm = Integer.toString(m);

        if (h < 10)
        {
            hh = "0" + h;
        }
        if (m < 10)
        {
            mm = "0" + m;
        }

        return (hh + ":" + mm + ":") + raFormat.format(val);
    }

    public static String degreesToDEC(double val)
    {
        if (val < -90.0 || val > 90.0)
        {
            throw new IllegalArgumentException("value " + val + " out of bounds: [-90.0, 90.0]");
        }
        String sign = "+";
        if (val < 0.0)
        {
            sign = "-";
            val *= -1.0;
        }
        int deg = (int) (val);
        val -= deg;
        // 60 min/deg
        int m = (int) (val * 60.0);
        val -= m / 60.0;
        // 60 sec/min == 3600 sec/deg
        val *= 3600.0;
        String d = Double.toString(val);

        String degs = Integer.toString(deg);
        if (deg < 10)
        {
            degs = "0" + degs;
        }
        String min = Integer.toString(m);
        if (m < 10)
        {
            min = "0" + m;
        }

        String s = sign + degs + ":" + min + ":";

        return s + decFormat.format(val);
    }


    public static double[] sexigessimalToDegrees(String ra, String dec)
            throws NumberFormatException
    {
        return new double[]{raToDegrees(ra), decToDegrees(dec)};
    }

    /**
     * Convert a string to a right ascension value in degrees. The argument is split
     * into components using a variety of separators (space, colon, some chars).
     * Valid formats include 15h30m45.6s = 15:30:45.6 = 15 30 45.6 ~ 232.69 (degrees).
     * If there is only one component after splitting, it is assumed to be the degrees
     * component (ie. 15 != 15:0:0) unless followed by the character 'h' (ie. 15h = 15:0:0).
     *
     * TODO - This is obscure and can be simplified!
     * TODO - 2007.01.05
     *
     * @param ra        RA Value.
     * @return right ascension in degrees
     * @throws NumberFormatException    if arg cannot be parsed
     * @throws IllegalArgumentException if the resulting value is not in [0,360]
     */
    public static double raToDegrees(final String ra)
            throws NumberFormatException
    {
        StringTokenizer st = new StringTokenizer(ra, raSeparators, true);
        double h = Double.NaN;
        double m = 0.0;
        double s = 0.0;

        if (st.hasMoreTokens())
        {
            h = Double.parseDouble(st.nextToken());
        }
        if (st.hasMoreTokens())
        {
            String str = st.nextToken(); // consume separator
            if (str.equals("h") || str.equals(":"))
            {
                h *= 15.0;
            }
        }
        if (st.hasMoreTokens())
        {
            m = Double.parseDouble(st.nextToken());
        }
        if (st.hasMoreTokens())
        {
            st.nextToken(); // consume separator
        }
        if (st.hasMoreTokens())
        {
            s = Double.parseDouble(st.nextToken());
        }

        if (Double.isNaN(h))
        {
            throw new IllegalArgumentException("empty string (RA)");
        }

        double ret = h + m / 4.0 + s / 240.0;
        while (ret < 0.0)
        {
            ret += 360.0;
        }
        while (ret > 360.0)
        {
            ret -= 360.0;
        }

        return ret;
    }

    /**
     * Obtain the radian value of the given RA string.
     *
     * @param ra
     * @return double radian
     * @throws NumberFormatException
     */
    public static double raToRadians(final String ra)
            throws NumberFormatException
    {
        return (raToDegrees(ra) * Math.PI) / 180.0;
    }

    /**
     * Obtain the String RA of the given Radians.
     *
     * @param raRadians
     * @return String HH mm ss.s
     */
    public static String radiansToRA(final double raRadians)
    {
        return degreesToRA((raRadians * 180) / Math.PI);
    }

    /**
     * Convert a string to a declination value in degrees. The argument is split
     * into components using a variety of separators (space, colon, some chars).
     * Valid formats include 15d30m45.6s = 15:30:45.6 = 15 30 45.6 ~ 15.51267 (degrees).
     * If there is only one component after splitting, it is assumed to be the degrees
     * component (thus, 15 == 15:0:0). Only the degrees component should have a negative
     * sign.
     *
     * @param dec
     * @return declination in degrees
     * @throws NumberFormatException    if arg cannot be parsed
     * @throws IllegalArgumentException if the resulting value is not in [-90,90]
     */
    public static double decToDegrees(String dec)
            throws IllegalArgumentException, NumberFormatException
    {
        StringTokenizer st = new StringTokenizer(dec, decSeparators);
        double d = Double.NaN;
        double m = 0;
        double s = 0;

        if (st.hasMoreTokens())
        {
            d = Double.parseDouble(st.nextToken());
        }
        if (st.hasMoreTokens())
        {
            m = Double.parseDouble(st.nextToken());
        }
        if (st.hasMoreTokens())
        {
            s = Double.parseDouble(st.nextToken());
        }

        if (Double.isNaN(d))
        {
            throw new IllegalArgumentException("empty string (DEC)");
        }

        double ret = d + m / 60.0 + s / 3600.0;
        if (dec.startsWith("-"))
        {
            ret = d - m / 60.0 - s / 3600.0;
        }

        if (-90.0 <= ret && ret <= 90.0)
        {
            return ret;
        }
        throw new IllegalArgumentException("DEC must be in [-90,90]: " + ret);
    }

    /**
     * Obtain the radian value of the String declination.
     *
     * @param dec
     * @return double as radians.
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    public static double decToRadians(final String dec)
            throws IllegalArgumentException, NumberFormatException
    {
        return (decToDegrees(dec) * Math.PI) / 180.0;
    }

    /**
     * Obtain the String declination of the given radians.
     *
     * @param decRadians
     * @return String declination DD mm ss.s
     */
    public static String radiansToDec(final double decRadians)
    {
        return degreesToDEC((decRadians * 180) / Math.PI);
    }
}
