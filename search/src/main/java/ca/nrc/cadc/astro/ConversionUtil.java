package ca.nrc.cadc.astro;

import java.util.Calendar;
import java.util.Date;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.StringTokenizer;

/**
 * Conversion methods.
 *
 * @version $Revision$
 * @author $Author: pdowler $
 */
public class ConversionUtil
{

	public static void main(String[] args)
	{
		try
		{
			String ra = args[0];
			String dec = args[1];
			String sexiRA = ConversionUtil.degreesToRA(Double.parseDouble(ra));
			String sexiDEC = ConversionUtil.degreesToDEC(Double.parseDouble(dec));
			System.out.println(sexiRA + "    " + sexiDEC); 
		}
		catch(Exception ex) 
		{
			ex.printStackTrace(); 
		}
	}
	
	private static NumberFormat raFormat = NumberFormat.getInstance();
	private static NumberFormat decFormat = NumberFormat.getInstance();
	static {
	   raFormat.setMaximumFractionDigits(1);
	   raFormat.setMinimumFractionDigits(1);
	   raFormat.setMaximumIntegerDigits(2);
	   raFormat.setMinimumIntegerDigits(2);

	   decFormat.setMaximumFractionDigits(1);
	   decFormat.setMinimumFractionDigits(1);
	   decFormat.setMaximumIntegerDigits(2);
	   decFormat.setMinimumIntegerDigits(2);
	}

	// milliarcseconds per degree
	private static int MAS_PER_DEGREE = 60*60*1000;
	
	// the unicode degree symbol
	private static String degreeSymbol = new Character((char) 0x00b0).toString();

	private static String raSeparators = " '\"hmsHMS:";
	private static String decSeparators = " '\"dmsHMS:" + degreeSymbol;


	public static int degreesToMilliArcSeconds(double deg)
	{
		return (int) (deg * MAS_PER_DEGREE);
	}
	public static double milliArcSecondsToDegrees(int mas)
	{
		return ((double) mas) / ((double) MAS_PER_DEGREE);
	}	

	public static String[] degreesToSexigessimal(double ra, double dec)
	{
		return new String[] { degreesToRA(ra), degreesToDEC(dec) };
	}
	public static String degreesToRA(double val)
	{
		if (val < 0.0 || val >= 360.0)
			throw new IllegalArgumentException("value "+val+" out of bounds: [0.0, 360.0)");
		// 24 hours/360 degrees = 15 deg/hour
		int h = (int) (val / 15.0);
		val -= h * 15.0;
		// 15 deg/hour == 0.25 deg/min == 4 min/deg
		int m = (int) (val * 4.0);
		val -= m / 4.0;
		// 4 min/deg == 240 sec/deg
		val *= 240.0;
		String d = Double.toString(val);
		String s = null;
		String hh = Integer.toString(h);
		String mm = Integer.toString(m);
		if (h < 10)
		   hh = "0" + h;
		if(m < 10)
		   mm = "0" + m;

		s = hh + ":" + mm + ":";
		return s + raFormat.format(val);

		//if (h < 10)
		//	s = "0" + h + "h " + m + "m ";
		//else
		//	s = h + "h " + m + "m ";
		// for seconds, show 5 chars, which is up to ##.##
		//if (d.length() < 5)
		//	return s + d + "s";
		//return s + d.substring(0, 5) + "s";
	}
	public static String degreesToDEC(double val)
	{
		if (val < -90.0 || val > 90.0)
			throw new IllegalArgumentException("value "+val+" out of bounds: [-90.0, 90.0]");
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
		if(deg < 10)
		   degs = "0" + degs;
		String min = Integer.toString(m);
		if(m < 10)
		   min = "0" + m;

		String s = sign + degs + ":" + min + ":";

		return s + decFormat.format(val);

		//String s = sign + deg + degreeSymbol + " " + m + "' ";
		//if (d.length() < 5)
		//	return s + d + "\"";
		//return s + d.substring(0, 5) + "\"";
	}

	

	public static double[] sexigessimalToDegrees(String ra, String dec)
		throws NumberFormatException
	{
		return new double[] { raToDegrees(ra), decToDegrees(dec) };
	}
	
	/**
	 * Convert a string to a right ascension value in degrees. The argument is split
	 * into components using a variety of separators (space, colon, some chars). 
	 * Valid formats include 15h30m45.6s = 15:30:45.6 = 15 30 45.6 ~ 232.69 (degrees).
	 * If there is only one component after splitting, it is assumed to be the degrees
	 * component (ie. 15 != 15:0:0) unless followed by the character 'h' (ie. 15h = 15:0:0). 
	 * 
	 * @param dec
	 * @return declination in degrees
	 * @throws NumberFormatException if arg cannot be parsed
	 * @throws IllegalArgumentException if the resulting value is not in [0,360)
	 */
	public static double raToDegrees(String ra)
		throws NumberFormatException
	{
		StringTokenizer st = new StringTokenizer(ra, raSeparators, true);
		double h = Double.NaN;
		double m = 0.0;
		double s = 0.0;
		
		if ( st.hasMoreTokens() )
			h = Double.parseDouble(st.nextToken());
		if ( st.hasMoreTokens() )
		{
			String str = st.nextToken(); // consume separator
			if (str.equals("h") || str.equals(":"))
				h *= 15.0;
		}
		if ( st.hasMoreTokens() )
			m = Double.parseDouble(st.nextToken());
		if ( st.hasMoreTokens() )
			st.nextToken(); // consume separator
		if ( st.hasMoreTokens() )
			s = Double.parseDouble(st.nextToken());
		
		if ( Double.isNaN(h) )
			throw new IllegalArgumentException("empty string (RA)");
		
		double ret = h + m/4.0 + s/240.0;
		if (0.0 <= ret && ret < 360.0)
			return ret;
		throw new IllegalArgumentException("RA must be in [0,360): " + ret);
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
	 * @throws NumberFormatException if arg cannot be parsed
	 * @throws IllegalArgumentException if the resulting value is not in [-90,90]
	 */
	public static double decToDegrees(String dec)
		throws IllegalArgumentException, NumberFormatException
	{
		StringTokenizer st = new StringTokenizer(dec, decSeparators);
		double d = Double.NaN;
		double m = 0;
		double s = 0;
		if ( st.hasMoreTokens() )
			d = Double.parseDouble(st.nextToken());
		if ( st.hasMoreTokens() )
			m = Double.parseDouble(st.nextToken());
		if ( st.hasMoreTokens() )
			s = Double.parseDouble(st.nextToken());
		
		if ( Double.isNaN(d) )
			throw new IllegalArgumentException("empty string (DEC)");
		
		if (d < 0.0)
			return d - m/60.0 - s/3600.0;
		double ret = d + m/60.0 + s/3600.0;
		if (-90.0 <= ret && ret <= 90.0)
			return ret;
		throw new IllegalArgumentException("DEC must be in [-90,90]: " + ret);
	}

	/**
	 * Convert a date in the UTC timezone to Modified Julian Date.
	 */
	public static double toModifiedJulianDate(Date date)
	{
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.clear();
		cal.setTime(date);
		int yr = cal.get(Calendar.YEAR);
		int mo = cal.get(Calendar.MONTH) + 1; // Calendar is 0-based
		int dy = cal.get(Calendar.DAY_OF_MONTH);
		double days = slaCldj(yr, mo, dy);

		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);
		int ms = cal.get(Calendar.MILLISECOND);
		//System.out.println("hh: " + hh);
		//System.out.println("mm: " + mm);
		//System.out.println("ss: " + ss);
		//System.out.println("ms: " + ms);
		double seconds = hh*3600.0 + mm*60.0 + ss + ms/1000.0;

		return days + seconds/86400.0;
	}

	/**
	 * Convert a Modified Julian Date to a date in the UTC timezone.
	 */
	public static Date toDate(double mjd)
	{
		int[] ymd = slaDjcl(mjd);

		// fraction of a day
		double frac = mjd - ((double) (long) mjd);
		System.out.println("parsing fraction of a day: " + frac);
		int hh = (int) (frac*24);
		// fraction of an hour
		frac = frac*24.0 - hh;
		System.out.println("hh: " + hh + "\tfrac: " + frac);
		int mm = (int) (frac*60);
		// fraction of a minute
		frac = frac*60.0 - mm;
		System.out.println("mm: " + mm + "\tfrac: " + frac);
		int ss = (int) (frac*60);
		// fraction of a second
		frac = frac*60.0 - ss;
		System.out.println("ss: " + ss + "\tfrac: " + frac);
		int ms = (int) (frac*1000);
		frac = frac*1000.0 - ms;
		System.out.println("ms: " + ms + "\tfrac: " + frac);

		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.YEAR, ymd[0]);
		cal.set(Calendar.MONTH, ymd[1] - 1); // Calendar is 0-based
		cal.set(Calendar.DAY_OF_MONTH, ymd[2]);
		cal.set(Calendar.HOUR_OF_DAY, hh);
		cal.set(Calendar.MINUTE, mm);
		cal.set(Calendar.SECOND, ss);
		cal.set(Calendar.MILLISECOND, ms);
		return cal.getTime();
	}
	
	/* Month lengths in days */
	private static int mtab[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	//private static double slaCldj( int iy, int im, int id, double *djm, int *j )
	private static double slaCldj(int iy, int im, int id)
		/*
		**  - - - - - - - -
		**   s l a C l d j
		**  - - - - - - - -
		**
		**  Gregorian calendar to Modified Julian Date.
		**
		**  Given:
		**     iy,im,id     int    year, month, day in Gregorian calendar
		**
		**  Returned:
		**     *djm         double Modified Julian Date (JD-2400000.5) for 0 hrs
		**     *j           int    status:
		**                           0 = OK
		**                           1 = bad year   (MJD not computed)
		**                           2 = bad month  (MJD not computed)
		**                           3 = bad day    (MJD computed)
		**
		**  The year must be -4699 (i.e. 4700BC) or later.
		**
		**  The algorithm is derived from that of Hatcher 1984 (QJRAS 25, 53-55).
		**
		**  Last revision:   29 August 1994
		**
		**  Copyright P.T.Wallace.  All rights reserved.
		*/
	{
		//System.out.println("[slaCldj] " + iy + ", " + im + ", " + id);

	    long iyL, imL;

		/* Validate year */
	    //if ( iy < -4699 ) { *j = 1; return; }
	    if ( iy < -4699 ) { throw new IllegalArgumentException("bad year"); }

		/* Validate month */
	    //if ( ( im < 1 ) || ( im > 12 ) ) { *j = 2; return; }
	    if ( ( im < 1 ) || ( im > 12 ) ) { throw new IllegalArgumentException("bad month"); }

		/* Allow for leap year */
	    mtab[1] = ( ( ( iy % 4 ) == 0 ) &&
	              ( ( ( iy % 100 ) != 0 ) || ( ( iy % 400 ) == 0 ) ) ) ?
	              29 : 28;

		/* Validate day */
	    //*j = ( id < 1 || id > mtab[im-1] ) ? 3 : 0;
	    if ( id < 1 || id > mtab[im-1] )
	    	throw new IllegalArgumentException("bad day");

		/* Lengthen year and month numbers to avoid overflow */
	    iyL = (long) iy;
	    imL = (long) im;

		/* Perform the conversion */
	    return (double)
	         ( ( 1461L * ( iyL - ( 12L - imL ) / 10L + 4712L ) ) / 4L
	         + ( 306L * ( ( imL + 9L ) % 12L ) + 5L ) / 10L
	         - ( 3L * ( ( iyL - ( 12L - imL ) / 10L + 4900L ) / 100L ) ) / 4L
	         + (long) id - 2399904L );
	}

	//void slaDjcl ( double djm, int *iy, int *im, int *id, double *fd, int *j)
	private static int[] slaDjcl (double djm)
		/*
		**  - - - - - - - -
		**   s l a D j c l
		**  - - - - - - - -
		**
		**  Modified Julian Date to Gregorian year, month, day,
		**  and fraction of a day.
		**
		**  Given:
		**     djm      double     Modified Julian Date (JD-2400000.5)
		**
		**  Returned:
		**     *iy      int        year
		**     *im      int        month
		**     *id      int        day
		**     *fd      double     fraction of day
		**     *j       int        status:
		**                      -1 = unacceptable date (before 4701BC March 1)
		**
		**  The algorithm is derived from that of Hatcher 1984 (QJRAS 25,
		53-55).
		**
		**  Defined in slamac.h:  dmod
		**
		**  Last revision:   12 March 1998
		**
		**  Copyright P.T.Wallace.  All rights reserved.
		*/
	{
		//System.out.println("[slaDjcl] " + djm);
		//double f, d;
		double f;
		long ld, jd, n4, nd10;

		/* Check if date is acceptable */
		if ( ( djm <= -2395520.0 ) || ( djm >= 1e9 ) )
		//{
			//*j = -1;
			//return;
			throw new IllegalArgumentException("MJD out of valid range");
		//}
	    //else
	    //{
		//	*j = 0;

			/* Separate day and fraction */
			//f = dmod ( djm, 1.0 );
			//if ( f < 0.0 ) f += 1.0;
			//d = djm - f;
			//d = dnint ( d );
			ld = (long) djm;

			/* Express day in Gregorian calendar */
			//jd = (long) dnint ( d ) + 2400001;
			jd = ld + 2400001L;
			n4 = 4L*(jd+((6L*((4L*jd-17918L)/146097L))/4L+1L)/2L-37L);
			nd10 = 10L*(((n4-237L)%1461L)/4L)+5L;
			//*iy = (int) (n4/1461L-4712L);
			//*im = (int) (((nd10/306L+2L)%12L)+1L);
			//*id = (int) ((nd10%306L)/10L+1L);
			//*fd = f;
			int[] ret = new int[3];
			ret[0] = (int) (n4/1461L-4712L);
			ret[1] = (int) (((nd10/306L+2L)%12L)+1L);
			ret[2] = (int) ((nd10%306L)/10L+1L);

			//*j = 0;
			return ret;
		//}
	}


}

// end of ConversionUtil.java
