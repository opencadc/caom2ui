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

import ca.nrc.cadc.util.Log4jInit;
import java.util.TimeZone;
import ca.nrc.cadc.search.parser.exception.DateParserException;
import java.util.Calendar;
import org.junit.BeforeClass;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author jburke
 */
public class DateParserTest
{
    private static Logger LOGGER = Logger.getLogger(DateParserTest.class);
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.search.parser",
                           org.apache.log4j.Level.INFO);
    }

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static Calendar dateCal;
    public static Calendar dateTimeCal;


    @BeforeClass
    public static void before()
    {
        dateCal = Calendar.getInstance();
        dateCal.clear();
        dateCal.setTimeZone(UTC);
        dateCal.set(Calendar.YEAR, 1910);
        dateCal.set(Calendar.MONTH, 1);
        dateCal.set(Calendar.DAY_OF_MONTH, 11);

        dateTimeCal = Calendar.getInstance();
        dateTimeCal.clear();
        dateTimeCal.setTimeZone(UTC);
        dateTimeCal.set(Calendar.YEAR, 1910);
        dateTimeCal.set(Calendar.MONTH, 1);
        dateTimeCal.set(Calendar.DAY_OF_MONTH, 11);
        dateTimeCal.set(Calendar.HOUR_OF_DAY, 21);
        dateTimeCal.set(Calendar.MINUTE, 45);
        dateTimeCal.set(Calendar.SECOND, 30);
        dateTimeCal.set(Calendar.MILLISECOND, 125);
    }

    @Test
    public void nullString()
    {
        LOGGER.debug("nullString()...");

        try
        {
            new DateParser(null);
            fail("Failed to throw TimeParserException parsing null string");
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("nullString() passed.");
    }

    @Test
    public void parseYear() throws Exception
    {
        LOGGER.debug("parseYear()...");

        final String query = "2010";
        final Calendar startCal = Calendar.getInstance();

        startCal.clear();
        startCal.setTimeZone(UTC);
        startCal.set(Calendar.YEAR, 2010);

        final DateParser parser = new DateParser(query);
        assertEquals(startCal.getTime(), parser.getDate());

        LOGGER.info("parseYear() passed.");
    }
    
    @Test
    public void parseYearBeforeMinDate() throws Exception
    {
        LOGGER.debug("parseYearBeforeMinDate()...");

        final String query = "1810";

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseYearBeforeMinDate() passed.");
    }

    @Test
    public void parseYearAfterMaxDate() throws Exception
    {
        LOGGER.debug("parseYearAfterMaxDate()...");

        final String query = "2110";

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseYearAfterMaxDate() passed.");
    }

    @Test
    public void parseYearMonth() throws Exception
    {
        LOGGER.debug("parseYearMonth()...");

        final String query = "2010-09";
        Calendar startCal = Calendar.getInstance();
        startCal.clear();
        startCal.setTimeZone(UTC);
        startCal.set(Calendar.YEAR, 2010);
        startCal.set(Calendar.MONTH, 8); // zero based month

        final DateParser parser = new DateParser(query);
        assertEquals(startCal.getTime(), parser.getDate());

        LOGGER.info("parseYearMonth() passed.");
    }

    @Test
    public void parseYearMonthBeforeMinDate() throws Exception
    {
        LOGGER.debug("parseYearMonthBeforeMinDate()...");

        final String query = "1810-09";

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseYearMonthBeforeMinDate() passed.");
    }

    @Test
    public void parseYearMonthAfterMaxDate() throws Exception
    {
        LOGGER.debug("parseYearMonthAfterMaxDate()...");

        final String query = "2110-09";
        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseYearMonthAfterMaxDate() passed.");
    }

    @Test
    public void parseYearMonthDay() throws Exception
    {
        LOGGER.debug("parseYearMonthDay()...");

        final String query = "2010-09-22";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseYearMonthDay() passed.");
    }

    @Test
    public void parseYearMonthDayBeforeMinDate() throws Exception
    {
        LOGGER.debug("parseYearMonthDayBeforeMinDate()...");

        final String query = "1810-09-22";

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseYearMonthDayBeforeMinDate() passed.");
    }

    @Test
    public void parseYearMonthDayAfterMaxDate() throws Exception
    {
        LOGGER.debug("parseYearMonthDayAfterMaxDate()...");
        final String query = "2110-09-22";

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range date "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }


        LOGGER.info("parseYearMonthDayAfterMaxDate() passed.");
    }


    @Test
    public void parseDateTimeIsoSepT() throws Exception
    {
        LOGGER.debug("parseDateTimeIsoSepT()...");

        final String query = "2010-09-22T05:15:05.015";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);
        cal.set(Calendar.HOUR_OF_DAY, 5);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 5);
        cal.set(Calendar.MILLISECOND, 15);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseDateTimeIsoSepT() passed.");
    }

    @Test
    public void parseDateTimeIsoSepSpace() throws Exception
    {
        LOGGER.debug("parseDateTimeIsoSepSpace()...");

        final String query = "2010-09-22 05:15:05.015";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);
        cal.set(Calendar.HOUR_OF_DAY, 5);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 5);
        cal.set(Calendar.MILLISECOND, 15);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseDateTimeIsoSepSpace() passed.");
    }

    @Test
    public void parseDateHourIsoSepT() throws Exception
    {
        LOGGER.debug("parseDateHourIsoSepT()...");

        final String query = "2010-09-22T05";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);
        cal.set(Calendar.HOUR_OF_DAY, 5);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseDateHourIsoSepT() passed.");
    }

    @Test
    public void parseDateHourIsoSepSpace() throws Exception
    {
        LOGGER.debug("parseDateHourIsoSepSpace()...");

        final String query = "2010-09-22 05";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);
        cal.set(Calendar.HOUR_OF_DAY, 5);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseDateHourIsoSepSpace() passed.");
    }

    @Test
    public void parseJD() throws Exception
    {
        LOGGER.debug("parseJD()...");

        final String query = "2455461.5";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseJD() passed.");
    }

    @Test
    public void parseJDBeforeMinDate() throws Exception
    {
        LOGGER.debug("parseJDBeforeMinDate()...");

        final String query = "2382412.5"; // 1810-09-22

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range JD "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseJDBeforeMinDate() passed.");
    }

    @Test
    public void parseJDAFterMaxDate() throws Exception
    {
        LOGGER.debug("parseJDAFterMaxDate()...");

        final String query = "2491985.5"; // 2110-09-22

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range JD "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseJDAFterMaxDate() passed.");
    }

    @Test
    public void parseMJD() throws Exception
    {
        LOGGER.debug("parseMJD()...");

        final String query = "55461";
        final Calendar cal = Calendar.getInstance();

        cal.clear();
        cal.setTimeZone(UTC);
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 22);

        final DateParser parser = new DateParser(query);
        assertEquals(cal.getTime(), parser.getDate());

        LOGGER.info("parseMJD() passed.");
    }

    @Test
    public void parseMJDBeforeMinDate() throws Exception
    {
        LOGGER.debug("parseMJDBeforeMinDate()...");

        final String query = "-17588"; // 1810-09-22

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range MJD "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseMJDBeforeMinDate() passed.");
    }

    @Test
    public void parseMJDAfterMaxDate() throws Exception
    {
        LOGGER.debug("parseMJDAfterMaxDate()...");

        final String query = "91985"; // 2110-09-22

        try
        {
            new DateParser(query);
            fail("Failed to throw TimeParserException parsing out-of-range MJD "
                 + query);
        }
        catch (DateParserException e)
        {
            // Good.
        }

        LOGGER.info("parseMJDAfterMaxDate() passed.");
    }

    @Test
    public void testIsJulianDate()
    {
        LOGGER.debug("testIsJulianDate()...");
        String query = "";

        try
        {
            query = "55461";
            DateParser dateParser = new DateParser(query);
            assertTrue(query + " should return true", dateParser.isJulianDate());

            query = "2010-09-22";
            dateParser = new DateParser(query);
            assertFalse(query + " should return false", dateParser.isJulianDate());

        }
        catch (DateParserException e)
        {
            fail("Failed to parse " + query);
        }

        LOGGER.info("testIsJulianDate() passed.");
    }
    
}
