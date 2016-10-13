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

import ca.nrc.cadc.search.parser.exception.DateParserException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ca.nrc.cadc.util.StringUtil;
import org.apache.log4j.Logger;

/**
 *       yyyy ∈ [1990, 2100]
 *         HH ∈ [0, 23]
 *        mjd ∈ [toMJD(1900), toMJD(2100)]
 *         jd ∈ [toJD(1900), toJD(2100)]
 *      <sep> ∈ (space, comma)
 *  <iso_sep> ∈ (space, T)
 *
 *     <date> ::= <yyyy>[-<MM>[-<dd>]]
 *     <time> ::= <HH>[:<mm>[:<ss>]]
 *  <iso8601> ::= <date>[<iso_sep><time>]
 *
 * <timespec> ::= <timespec>[<sep><timespec>]
 * <timespec> ::= <jd>|<mjd>|<iso8601>
 *
 * @author jburke
 */
public class DateParser
{
    private static Logger LOGGER = Logger.getLogger(DateParser.class);

    // Allow only dates between 1900 and 2100.
    public static Double MIN_YEAR = 1900.0;
    public static Double MAX_YEAR = 2100.0;

    // Julian Dates 1900 to 2100.
    public static Double MIN_JD = 2415020.5;
    public static Double MAX_JD = 2488069.5;

    // Modified Julian Dates 1900 to 2100.
    public static Double MIN_MJD = 15020.0;
    public static Double MAX_MJD = 88069.0;

    public static Double MJD_DIFFERENTIAL = 2400000.5;

    public static Integer MIN_HOUR = 0;
    public static Integer MAX_HOUR = 23;

//    public static String QUERY_SEPARATER_REGEX = "[\\s]+";
    public static String ISO_SEPARATER_REGEX = "[\\sT]+";
    public static String DATE_SEPARATER = "[-]";
    public static String TIME_SEPARATER = "[:.]";

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private Date date;

    private Calendar calendar;

    private boolean calendarIsJD;

    private int lastParsedField;


    public DateParser(final String query) throws DateParserException
    {
        lastParsedField = Calendar.YEAR;

        if (!StringUtil.hasText(query))
        {
            throw new DateParserException("Given String is empty.");
        }

        parse(query);
    }

    public boolean isJulianDate()
    {
        return calendarIsJD;
    }

    private void parse(final String query) throws DateParserException
    {
        LOGGER.debug("parse: " + query);

        String[] tokens = query.split(ISO_SEPARATER_REGEX);

        if (tokens.length == 1)
        {
            parseOneToken(query, new String[]{query});
        }
        else if(tokens.length == 2)
        {
            parseTwoTokens(query, tokens);
        }
        else
        {
            throw new DateParserException("Unable to parse given input.");
        }

        normalize();

        date = calendar.getTime();
    }

    /*
     * Possible combinations:
     * date
     * datetime
     * JD
     * MJD
     */
    protected void parseOneToken(final String query, final String[] tokens)
        throws DateParserException
    {
        if (tokens[0] == null || tokens[0].trim().length() == 0)
            throw new DateParserException("Null or zero length string");

        // Try and parse as a MJD.
        calendar = parseMJD(tokens[0]);

        // Try and parse as a JD.
        if (calendar != null)
        {
            calendarIsJD = true;
        }
        else
        {
            calendar = parseJD(tokens[0]);
        }

        // Try and parse as a date and time.
        if (calendar != null)
        {
            calendarIsJD = true;
        }
        else
        {
            calendar = parseDateAndTime(tokens[0]);
        }
        
        // Set lastParsedField for JD or MJD queries.
        if (calendarIsJD)
        {
            // Try to determine number of decimal places used.
            int dot = tokens[0].indexOf(".");
            if (dot == -1)
            {
                // No decimal places given so day percision.
                lastParsedField = Calendar.DAY_OF_MONTH;
            }
            else
            {
                // Make a bad guess based on number of decimal places given
                // what percision to use for the JD or MJD.
                int decimals = tokens[0].length() - (dot + 1);
                if (decimals >= 3)
                {
                     lastParsedField = Calendar.MINUTE;
                }
                else if (decimals >= 1)
                {
                    lastParsedField = Calendar.HOUR_OF_DAY;
                }
                else
                {
                    lastParsedField = Calendar.DAY_OF_MONTH;
                }
            }
        }

        // Unable to parse, throw exception.
        if (calendar == null)
        {
            throw new DateParserException("Unable to parse " + query);
        }
    }

    /*
     * Possible combinations:
     * date time
     * date date
     * date JD
     * date MJD
     * JD date
     * JD JD
     * JD MJD
     * MJD date
     * MJD JD
     * MJD MJD
     */
    protected void parseTwoTokens(String query, String[] tokens)
        throws DateParserException
    {
        // Try and parse as a MJD.
        calendar = parseMJD(tokens[0]);

        // Try and parse as a JD.
        if (calendar != null)
        {
            calendarIsJD = true;
        }
        else
        {
            calendar = parseJD(tokens[0]);
        }

        // Try and parse as a date.
        if (calendar != null)
            calendarIsJD = true;
        else
        {
            calendar = parseDate(tokens[0]);
            if (calendar != null)
            {
                // Try next token as a time.
                Calendar startTime = parseTime(tokens[1]);
                if (startTime != null)
                {
                    // Add time to date and return.
                    setDateTime(calendar, startTime);
                    return;
                }
            }
        }

        if (calendar == null)
        {
            calendar = parseDateAndTime(tokens[0]);
        }

        // Unable to parse, throw exception.
        if (calendar == null)
        {
            throw new DateParserException("Unable to parse " + query);
        }
    }

    protected void normalize()
    {
        LOGGER.debug("normalize date[" + calToString(calendar) + "]");

        if (!calendarIsJD)
        {
            LOGGER.debug("lastParsedStartField " + lastParsedField);

            if (lastParsedField < Calendar.MILLISECOND)
                calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
            if (lastParsedField < Calendar.SECOND)
                calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
            if (lastParsedField < Calendar.MINUTE)
                calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
            if (lastParsedField < Calendar.HOUR_OF_DAY)
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
            if (lastParsedField < Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            if (lastParsedField < Calendar.MONTH)
                calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
        }

        LOGGER.debug("date[" + calToString(calendar) + "]");
    }

    private void setDateTime(Calendar date, Calendar time)
    {
        if (time.isSet(Calendar.HOUR_OF_DAY))
            date.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        if (time.isSet(Calendar.MINUTE))
            date.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        if (time.isSet(Calendar.SECOND))
            date.set(Calendar.SECOND, time.get(Calendar.SECOND));
        if (time.isSet(Calendar.MILLISECOND))
            date.set(Calendar.MILLISECOND, time.get(Calendar.MILLISECOND));
    }

    private Calendar parseMJD(String s)
    {
        // Try and create a Double from the string.
        final Double d = parseDouble(s);
        if (d == null)
        {
            LOGGER.debug("parseMJD[" + s + "] cannot parse as double");
            return null;
        }

        // Check the Double is within the min and max range.
        if ((d.compareTo(MIN_MJD) >= 0) && (d.compareTo(MAX_MJD) <= 0))
        {
            // Create and return a new Date.
            LOGGER.debug("parseMJD[" + s + "] " + d);
            return toCalendar(d);
        }

        // Unable to parse as a MJD, return null.
        LOGGER.debug("parseMJD[" + s + "] value out of range");
        return null;
    }

    private Calendar parseJD(String s)
    {
        // Try and create a Double from the string.
        final Double d = parseDouble(s);
        if (d == null)
        {
            LOGGER.debug("parseJD[" + s + "] cannot parse as double");
            return null;
        }

        // Check the Double is within the min and max range.
        if (d.compareTo(MIN_JD) >= 0 && d.compareTo(MAX_JD) <= 0)
        {
            // Create and return a new Date.
            // Subtract 2400000.5 from a JD to get a MJD.
            LOGGER.debug("parseJD[" + s + "] " + d);
            return toCalendar(d - MJD_DIFFERENTIAL);
        }

        // Unable to parse as a Julian Date, return null.
        LOGGER.debug("parseJD[" + s + "] value out of range");
        return null;
    }

    private Calendar parseDateAndTime(String s)
    {
        // Try and split on the iso separaters to see if we have a date and time.
        final String[] tokens = s.split(ISO_SEPARATER_REGEX);

        // Only a date, no time.
        Calendar cal = null;
        if (tokens.length == 1)
        {
            cal = parseDate(s);
        }

        // Date and a time.
        else if (tokens.length == 2)
        {
            cal = parseDateTime(tokens[0], tokens[1]);
        }

        // Unable to parse.
        LOGGER.debug("parseDateAndTime[" + s + "] " + calToString(cal));
        return cal;
    }

    private Calendar parseDateTime(String date, String time)
    {
        // Get the date.
        final Calendar calDate = parseDate(date);
        if (calDate != null)
        {
            Calendar calTime = parseTime(time);
            if (calTime != null)
            {
                setDateTime(calDate, calTime);
            }
        }
        LOGGER.debug("parseDateTime[" + date + "," + time + "] " + calToString(calDate));
        return calDate;
    }

    private Calendar parseDate(final String s)
    {
        // Check for a possible negative number, which splits
        // into 2 values, the first of which is empty. First
        // character must be a digit.
        if ((s.length() != 0) && !Character.isDigit(s.charAt(0)))
        {
            LOGGER.debug("parseDate[" + s + "] invalid start to value");
            return null;
        }

        // Try to split the string on the date separaters.
        String[] tokens = s.split(DATE_SEPARATER);

        // Calendar values.
        Double year = null;
        Double month = null;
        Double day = null;

        // Didn't split, string should be yyyy.
        if (tokens.length >= 1)
        {
            // Try and create a Double from the year.
            year = parseDouble(tokens[0]);
            if (year == null)
            {
                LOGGER.debug("parseDate[" + s + "] invalid year");
                return null;
            }

            // Check the year is within the min and max range.
            if (year.compareTo(MIN_YEAR) < 0 || year.compareTo(MAX_YEAR) > 0)
            {
                LOGGER.debug("parseDate[" + s + "] value of of range");
                return null;
            }

            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.YEAR;
            }
        }

        // 2 tokens, string should be yyyy-MM.
        if (tokens.length >= 2)
        {
            month = parseDouble(tokens[1]);
            if (month == null)
            {
                LOGGER.debug("parseDate[" + s + "] invalid month");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.MONTH;
            }
        }

        // 3 tokens, string should be yyyy-MM-dd.
        if (tokens.length == 3)
        {
            day = parseDouble(tokens[2]);
            if (day == null)
            {
                LOGGER.debug("parseDate[" + s + "] invalid day");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.DAY_OF_MONTH;
            }
        }

        // Build a calendar with the date.
        Calendar cal = null;
        if (year != null)
        {
            cal = Calendar.getInstance();
            cal.clear();
            cal.setTimeZone(UTC);
            cal.set(Calendar.YEAR, year.intValue());
            if (month != null)
            {
                cal.set(Calendar.MONTH, month.intValue() - 1); // zero based month.
                if (day != null)
                {
                    cal.set(Calendar.DAY_OF_MONTH, day.intValue());
                }
            }
        }
        LOGGER.debug("parseDate[" + s + "] " + calToString(cal));
        return cal;
    }

    private Calendar parseTime(final String s)
    {
        // Try to split the string on the time separater.
        final String[] tokens = s.split(TIME_SEPARATER);

        // Calendar values.
        Integer hour = null;
        Integer minute = null;
        Integer second = null;
        Integer milli = null;

        // 1st token, should be hour.
        if (tokens.length >= 1)
        {
            hour = parseInteger(tokens[0]);
            if (hour == null)
            {
                LOGGER.debug("parseTime[" + s + "] invalid hour");
                return null;
            }

            // Check the hour is within the min and max range.
            if (hour.compareTo(MIN_HOUR) < 0 || hour.compareTo(MAX_HOUR) > 0)
            {
                LOGGER.debug("parseTime[" + s + "] value out of range");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.HOUR_OF_DAY;
            }
        }

        // 2nd token, should be minute.
        if (tokens.length >= 2)
        {
            minute = parseInteger(tokens[1]);
            if (minute == null)
            {
                LOGGER.debug("parseTime[" + s + "] invalid minute");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.MINUTE;
            }
        }

        // 3rd token, should be second.
        if (tokens.length >= 3)
        {
            second = parseInteger(tokens[2]);
            if (second == null)
            {
                LOGGER.debug("parseTime[" + s + "] invalid second");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.SECOND;
            }
        }

        // 4th token, should be millisecond.
        if (tokens.length == 4)
        {
            milli = parseInteger(tokens[3]);
            if (milli == null)
            {
                LOGGER.debug("parseTime[" + s + "] invalid milli");
                return null;
            }
            // Update the last known parsed field.
            else
            {
                lastParsedField = Calendar.MILLISECOND;
            }
        }

        // Build a calendar with the time.
        Calendar cal = null;
        if (hour != null)
        {
            cal = Calendar.getInstance();
            cal.clear();
            cal.setTimeZone(UTC);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            if (minute != null)
            {
                cal.set(Calendar.MINUTE, minute);
                if (second != null)
                {
                    cal.set(Calendar.SECOND, second);
                    if (milli != null)
                    {
                        cal.set(Calendar.MILLISECOND, milli);
                    }
                }
            }
        }
        LOGGER.debug("parseTime[" + s + "] " + calToString(cal));
        return cal;
    }

    private Integer parseInteger(String s)
    {
        try
        {
            return new Integer(s);
        }
        catch (NumberFormatException nfe)
        {
            return null;
        }
    }

    private Double parseDouble(String s)
    {
        try
        {
            return new Double(s);
        }
        catch (NumberFormatException nfe)
        {
            return null;
        }
    }

    private String calToString(Calendar cal)
    {
        if (cal == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.YEAR));
        sb.append("-");
        sb.append(cal.get(Calendar.MONTH) + 1);
        sb.append("-");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append("T");
        sb.append(cal.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(cal.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(cal.get(Calendar.SECOND));
        sb.append(".");
        sb.append(cal.get(Calendar.MILLISECOND));
        return sb.toString();
    }

    /**
     * Convert a Modified Julian Date to a date in the UTC time zone.
     *
     * @param mjd the MJD value
     * @return a Date in the UTC time zone
     */
    public static Calendar toCalendar(double mjd)
    {
        int[] ymd = slaDjcl(mjd);

        // fraction of a day
        double frac = mjd - ((double) (long) mjd);
        int hh = (int) (frac * 24);
        // fraction of an hour
        frac = frac * 24.0 - hh;
        int mm = (int) (frac * 60);
        // fraction of a minute
        frac = frac * 60.0 - mm;
        int ss = (int) (frac * 60);
        // fraction of a second
        frac = frac * 60.0 - ss;
        int ms = (int) (frac * 1000);
        //frac = frac*1000.0 - ms;

        Calendar cal = Calendar.getInstance(UTC);
        cal.set(Calendar.YEAR, ymd[0]);
        cal.set(Calendar.MONTH, ymd[1] - 1); // Calendar is 0-based

        cal.set(Calendar.DAY_OF_MONTH, ymd[2]);
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);
        cal.set(Calendar.SECOND, ss);
        cal.set(Calendar.MILLISECOND, ms);

        return cal;
    }

    //void slaDjcl ( double djm, int *iy, int *im, int *id, double *fd, int *j)
    private static int[] slaDjcl(double djm)
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
     **  The algorithm is derived from that of Hatcher 1984 (QJRAS 25, 53-55).
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
        //double f;
        long ld, jd, n4, nd10;

        /* Check if date is acceptable */
        if ((djm <= -2395520.0) || (djm >= 1e9))
        //{
        //*j = -1;
        //return;
        {
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
        }
        ld = (long) djm;

        /* Express day in Gregorian calendar */
        //jd = (long) dnint ( d ) + 2400001;
        jd = ld + 2400001L;
        n4 = 4L * (jd + ((6L * ((4L * jd - 17918L) / 146097L)) / 4L + 1L) / 2L - 37L);
        nd10 = 10L * (((n4 - 237L) % 1461L) / 4L) + 5L;
        //*iy = (int) (n4/1461L-4712L);
        //*im = (int) (((nd10/306L+2L)%12L)+1L);
        //*id = (int) ((nd10%306L)/10L+1L);
        //*fd = f;
        int[] ret = new int[3];
        ret[0] = (int) (n4 / 1461L - 4712L);
        ret[1] = (int) (((nd10 / 306L + 2L) % 12L) + 1L);
        ret[2] = (int) ((nd10 % 306L) / 10L + 1L);

        //*j = 0;
        return ret;
    //}
    }

    public Date getDate()
    {
        return date;
    }

    public int getLastParsedField()
    {
        return lastParsedField;
    }
}
