/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 11/22/11 - 2:25 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;

import org.junit.Before;
import org.junit.Test;

import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.date.DateUtil;
import org.apache.log4j.Logger;
import ca.nrc.cadc.util.Log4jInit;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class DateTest extends AbstractNumericFormConstraintTest<Date> {

    private static final String TIME_UTYPE = "Plane.time.bounds.samples";
    private static final String TIME_PRESET_UTYPE = "Plane.time.bounds.samples_PRESET";

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final String START_DATE_STRING = "1977-11-25";
    private static final String END_DATE_STRING = "2007-09-18";

    private static final String START_DATETIME_STRING =
            "1977-11-25 03:21:12.125";
    private static final String END_DATETIME_STRING = "2007-09-18 01:51:12.125";

    private static final String IVOA_START_DATETIME_STRING =
            "1977-11-25T03:21:12.125";
    private static final String IVOA_END_DATETIME_STRING =
            "2007-09-18T01:51:12.125";

    // 1977-11-25 03:21:12
    private static final String START_JD_STRING = "2443472.6";
    // 2007-09-18 01:51:12
    private static final String END_JD_STRING = "2454361.6";

    private static final String START_MJD_STRING = "43472"; // 1977-11-25
    private static final String END_MJD_STRING = "54361"; // 2007-09-18

    private static Calendar startDateCal;
    private static Calendar endDateCal;

    private static Calendar startDateTimeCal;
    private static Calendar endDateTimeCal;

    private static Logger log = Logger.getLogger(DateTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc.search", Level.INFO);
    }

    @Before
    public void before() {
        startDateCal = Calendar.getInstance(DateUtil.UTC);
        startDateCal.clear();
        startDateCal.setTimeZone(UTC);
        startDateCal.set(Calendar.YEAR, 1977);
        startDateCal.set(Calendar.MONTH, Calendar.NOVEMBER);
        startDateCal.set(Calendar.DAY_OF_MONTH, 25);
        startDateCal.set(Calendar.HOUR_OF_DAY, 0);
        startDateCal.set(Calendar.MINUTE, 0);
        startDateCal.set(Calendar.SECOND, 0);
        startDateCal.set(Calendar.MILLISECOND, 0);

        endDateCal = Calendar.getInstance(DateUtil.UTC);
        endDateCal.clear();
        endDateCal.setTimeZone(UTC);
        endDateCal.set(Calendar.YEAR, 2007);
        endDateCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endDateCal.set(Calendar.DAY_OF_MONTH, 18);
        endDateCal.set(Calendar.HOUR_OF_DAY, 0);
        endDateCal.set(Calendar.MINUTE, 0);
        endDateCal.set(Calendar.SECOND, 0);
        endDateCal.set(Calendar.MILLISECOND, 0);

        startDateTimeCal = Calendar.getInstance(DateUtil.UTC);
        startDateTimeCal.clear();
        startDateTimeCal.setTimeZone(UTC);
        startDateTimeCal.set(Calendar.YEAR, 1977);
        startDateTimeCal.set(Calendar.MONTH, Calendar.NOVEMBER);
        startDateTimeCal.set(Calendar.DAY_OF_MONTH, 25);
        startDateTimeCal.set(Calendar.HOUR_OF_DAY, 3);
        startDateTimeCal.set(Calendar.MINUTE, 21);
        startDateTimeCal.set(Calendar.SECOND, 12);
        startDateTimeCal.set(Calendar.MILLISECOND, 125);

        endDateTimeCal = Calendar.getInstance(DateUtil.UTC);
        endDateTimeCal.clear();
        endDateTimeCal.setTimeZone(UTC);
        endDateTimeCal.set(Calendar.YEAR, 2007);
        endDateTimeCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endDateTimeCal.set(Calendar.DAY_OF_MONTH, 18);
        endDateTimeCal.set(Calendar.HOUR_OF_DAY, 1);
        endDateTimeCal.set(Calendar.MINUTE, 51);
        endDateTimeCal.set(Calendar.SECOND, 12);
        endDateTimeCal.set(Calendar.MILLISECOND, 125);
    }


    @Test
    public void isValid() {
        final Job mockJob = createMock(Job.class);
        final FormErrors formErrors = new FormErrors();
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, "1977"));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        assertTrue("Should be valid.", getTestSubject().isValid(formErrors));
        assertFalse("Should be no errors.", formErrors.hasErrors());

        verify(mockJob);
    }

    @Test
    public void searchDateAndDate() {
        final Job mockJob = createMock(Job.class);
        final String query = START_DATE_STRING + ".." + END_DATE_STRING;
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        final Calendar endCal = Calendar.getInstance(DateUtil.UTC);
        endCal.clear();
        endCal.setTimeZone(UTC);
        endCal.set(Calendar.YEAR, 2007);
        endCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endCal.set(Calendar.DAY_OF_MONTH, 18);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("MJD does not match start time.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1d);
        assertEquals("MJD does not match end time.",
                     DateUtil.toModifiedJulianDate(endCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1d);

        verify(mockJob);
    }

    @Test
    public void searchDateTimeAndDate() {
        final Job mockJob = createMock(Job.class);

        final String queryOne = START_DATETIME_STRING + ".." + END_DATE_STRING;
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));

        final Calendar endCal = Calendar.getInstance(DateUtil.UTC);
        endCal.clear();
        endCal.setTimeZone(UTC);
        endCal.set(Calendar.YEAR, 2007);
        endCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endCal.set(Calendar.DAY_OF_MONTH, 18);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start times do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End times do not match.",
                     DateUtil.toModifiedJulianDate(endCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // Test 2 with IVOA DATE TIME.
        reset(mockJob);
        parameters.clear();

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        final String queryTwo =
                IVOA_START_DATETIME_STRING + ".." + END_DATE_STRING;
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start IVOA times do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End IVOA times do not match.",
                     DateUtil.toModifiedJulianDate(endCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateAndDateTime() {
        final Job mockJob = createMock(Job.class);
        final String queryOne = START_DATE_STRING + ".." + END_DATETIME_STRING;
        final String queryTwo = START_DATE_STRING + ".."
                + IVOA_END_DATETIME_STRING;

        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));

        final Calendar startCal = Calendar.getInstance(DateUtil.UTC);
        startCal.clear();
        startCal.setTimeZone(UTC);
        startCal.set(Calendar.YEAR, 1977);
        startCal.set(Calendar.MONTH, Calendar.NOVEMBER);
        startCal.set(Calendar.DAY_OF_MONTH, 25);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start times do not match.",
                     DateUtil.toModifiedJulianDate(startCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End times do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // Test 2 with IVOA DATE TIME.
        reset(mockJob);
        parameters.clear();

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start IVOA times do not match.",
                     DateUtil.toModifiedJulianDate(startCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End IVOA times do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateTimeAndDateTime() {
        final Job mockJob = createMock(Job.class);
        final String queryOne = START_DATETIME_STRING + ".."
                + END_DATETIME_STRING;
        final String queryTwo = IVOA_START_DATETIME_STRING + ".."
                + END_DATETIME_STRING;
        final String queryThree = IVOA_START_DATETIME_STRING + ".."
                + IVOA_END_DATETIME_STRING;
        final String queryFour = START_DATETIME_STRING + ".."
                + IVOA_END_DATETIME_STRING;

        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 2
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start 2 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End 2 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 3
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryThree));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start 3 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End 3 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 4
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryFour));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start 4 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End 4 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateAndJD() {
        final Job mockJob = createMock(Job.class);
        final String query = START_DATE_STRING + ".." + END_JD_STRING;
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateTimeAndJD() {
        final String queryOne = START_DATETIME_STRING + ".." + END_JD_STRING;
        final String queryTwo = IVOA_START_DATETIME_STRING + ".."
                + END_JD_STRING;

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 2
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start 2 DateTimes do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End 2 Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchSingleDate() {
        endDateCal.set(1977, Calendar.NOVEMBER, 25, 0, 0, 0);
        endDateCal.set(Calendar.MILLISECOND, 0);
        endDateCal.add(Calendar.HOUR, 24);

        verifyDateTest(START_DATE_STRING);
    }

    @Test
    public void searchSingleDateTimeYear() {
        final String query = "1977";

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        final Calendar startDateYearCal = Calendar.getInstance(DateUtil.UTC);
        startDateYearCal.clear();
        startDateYearCal.setTimeZone(UTC);
        startDateYearCal.set(Calendar.YEAR, 1977);
        startDateYearCal.set(Calendar.MONTH, 0);
        startDateYearCal.set(Calendar.DAY_OF_MONTH, 1);
        startDateYearCal.set(Calendar.HOUR_OF_DAY, 0);
        startDateYearCal.set(Calendar.MINUTE, 0);
        startDateYearCal.set(Calendar.SECOND, 0);
        startDateYearCal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateYearCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);

        startDateYearCal.add(Calendar.YEAR, 1);

        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateYearCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }


    @Test
    public void searchSingleDateTimeMonth() {
        final String query = "1977-11";

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        final Calendar startDateMonthCal = Calendar.getInstance(DateUtil.UTC);
        startDateMonthCal.clear();
        startDateMonthCal.setTimeZone(UTC);
        startDateMonthCal.set(Calendar.YEAR, 1977);
        startDateMonthCal.set(Calendar.MONTH, Calendar.NOVEMBER);
        startDateMonthCal.set(Calendar.DAY_OF_MONTH, 1);
        startDateMonthCal.set(Calendar.HOUR_OF_DAY, 0);
        startDateMonthCal.set(Calendar.MINUTE, 0);
        startDateMonthCal.set(Calendar.SECOND, 0);
        startDateMonthCal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateMonthCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);

        startDateMonthCal.add(Calendar.MONTH, 1);

        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateMonthCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchSingleDateTimeDay() {
        endDateCal.set(1977, Calendar.NOVEMBER, 25, 0, 0, 0);
        endDateCal.set(Calendar.MILLISECOND, 0);
        endDateCal.add(Calendar.HOUR, 24);

        verifyDateTest(START_DATE_STRING);
    }

    @Test
    public void searchSingleDateTimeHour() {
        final String query = START_DATE_STRING + " 01:00:00";

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        startDateCal.set(Calendar.HOUR_OF_DAY, 1);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);

        startDateCal.add(Calendar.HOUR_OF_DAY, 1);

        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchSingleDateTimeMinute() {
        final String query = START_DATE_STRING + " 01:21:00";

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        startDateCal.set(Calendar.HOUR_OF_DAY, 1);
        startDateCal.set(Calendar.MINUTE, 21);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);

        startDateCal.add(Calendar.MINUTE, 1);

        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchSingleDateTimeSecond() {
        final String query = START_DATE_STRING + " 01:21:12";

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        startDateCal.set(Calendar.HOUR_OF_DAY, 1);
        startDateCal.set(Calendar.MINUTE, 21);
        startDateCal.set(Calendar.SECOND, 12);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);

        startDateCal.add(Calendar.SECOND, 1);

        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateAndMJD() {
        final String query = START_DATE_STRING + ".." + END_MJD_STRING;

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchDateTimeAndMJD() {
        final String queryOne = START_DATETIME_STRING + ".." + END_MJD_STRING;
        final String queryTwo = IVOA_START_DATETIME_STRING + ".."
                + END_MJD_STRING;

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));


        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 2
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateTimeCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchJDAndDate() {
        final Calendar endCal = Calendar.getInstance(DateUtil.UTC);
        final String query = START_JD_STRING + ".." + END_DATE_STRING;

        endCal.clear();
        endCal.setTimeZone(UTC);
        endCal.set(Calendar.YEAR, 2007);
        endCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endCal.set(Calendar.DAY_OF_MONTH, 18);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));


        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchJDAndDateTime() {
        final String queryOne = START_JD_STRING + ".." + END_DATETIME_STRING;
        final String queryTwo = START_JD_STRING + ".."
                + IVOA_END_DATETIME_STRING;

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));


        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Datetimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 2
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start 2 Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End 2 Datetimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchJDAndJD() {
        verifyDateTest(START_JD_STRING + ".." + END_JD_STRING);
    }

    @Test
    public void searchJDAndMJD() {
        verifyDateTest(START_JD_STRING + ".." + END_MJD_STRING);
    }

    @Test
    public void searchMJDAndDate() {
        final String query = START_MJD_STRING + ".." + END_DATE_STRING;
        final Calendar endCal = Calendar.getInstance(DateUtil.UTC);

        endCal.clear();
        endCal.setTimeZone(UTC);
        endCal.set(Calendar.YEAR, 2007);
        endCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        endCal.set(Calendar.DAY_OF_MONTH, 18);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();
        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchMJDAndDateTime() {
        final String queryOne = START_MJD_STRING + ".." + END_DATETIME_STRING;
        final String queryTwo = START_MJD_STRING + ".."
                + IVOA_END_DATETIME_STRING;

        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryOne));


        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Datetimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);


        // TEST 2
        reset(mockJob);
        parameters.clear();

        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, queryTwo));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Datetimes do not match.",
                     DateUtil.toModifiedJulianDate(endDateTimeCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchMJDAndJD() {
        verifyDateTest(START_MJD_STRING + ".." + END_JD_STRING);
    }

    @Test
    public void searchMJDAndMJD() {
        verifyDateTest(START_MJD_STRING + ".." + END_MJD_STRING);
    }

    private void verifyDateTest(final String query) {
        final Job mockJob = createMock(Job.class);
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, query));

        expect(mockJob.getParameterList()).andReturn(parameters).once();
        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        getTestSubject().isValid(new FormErrors());
        assertEquals("Start Dates do not match.",
                     DateUtil.toModifiedJulianDate(startDateCal.getTime()),
                     getTestSubject().getLowerNumber().doubleValue(), 0.1);
        assertEquals("End Dates do not match.",
                     DateUtil.toModifiedJulianDate(endDateCal.getTime()),
                     getTestSubject().getUpperNumber().doubleValue(), 0.1);

        verify(mockJob);
    }

    @Test
    public void searchPreset() {
        final Job mockJob = createMock(Job.class);
        final FormErrors formErrors = new FormErrors();
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_PRESET_UTYPE + Date.VALUE,
                                     DatePreset.PAST_24_HOURS.name()));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        assertTrue("Should be valid.", getTestSubject().isValid(formErrors));
        assertFalse("Should be no errors.", formErrors.hasErrors());
        assertEquals("Utypes should match", getTestSubject().getUType(),
                     TIME_UTYPE);
        assertTrue("Should be range.",
                   getTestSubject().getFormValue().contains(".."));

        verify(mockJob);
    }

    @Test
    public void calculatePast24HourValue() {
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        cal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(DatePreset.PAST_24_HOURS.name(),
                                TIME_PRESET_UTYPE, cal.getTime()));

        assertEquals("Wrong date string calculated",
                     "1977-11-24 03:12:00.000..1977-11-25 03:12:00.000",
                     getTestSubject().calculateValue(DatePreset.PAST_24_HOURS));
    }

    @Test
    public void calculatePastWeekValue() {
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        cal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(DatePreset.PAST_WEEK.name(),
                                TIME_PRESET_UTYPE, cal.getTime()));

        assertEquals("Wrong date string calculated",
                     "1977-11-18 03:12:00.000..1977-11-25 03:12:00.000",
                     getTestSubject().calculateValue(DatePreset.PAST_WEEK));

        // Verify roll-over
        final Calendar cal2 = Calendar.getInstance(DateUtil.UTC);
        cal2.set(1977, Calendar.NOVEMBER, 4, 3, 12, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(DatePreset.PAST_WEEK.name(),
                                TIME_PRESET_UTYPE, cal2.getTime()));

        assertEquals("Wrong date string calculated",
                     "1977-10-28 03:12:00.000..1977-11-04 03:12:00.000",
                     getTestSubject().calculateValue(DatePreset.PAST_WEEK));
    }

    @Test
    public void calculatePastMonthValue() {
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        cal.set(Calendar.MILLISECOND, 0);

        setTestSubject(new Date(DatePreset.PAST_MONTH.name(),
                                TIME_PRESET_UTYPE, cal.getTime()));

        assertEquals("Wrong date string calculated",
                     "1977-10-25 03:12:00.000..1977-11-25 03:12:00.000",
                     getTestSubject().calculateValue(DatePreset.PAST_MONTH));
    }

    @Test
    public void getUtype() {
        final Job mockJob = createMock(Job.class);
        final FormErrors formErrors = new FormErrors();
        final List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, "1977"));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        assertTrue("Should be valid.", getTestSubject().isValid(formErrors));
        assertFalse("Should be no errors.", formErrors.hasErrors());
        assertEquals("Utypes should match", getTestSubject().getUType(),
                     TIME_UTYPE);

        verify(mockJob);
    }

    @Test
    public void getFormValueUnit() {
        Job mockJob = createMock(Job.class);
        FormErrors formErrors = new FormErrors();
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, "1977-12-14"));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        assertTrue("Should be valid.", getTestSubject().isValid(formErrors));
        assertFalse("Should be no errors.", formErrors.hasErrors());
        assertEquals("unit should be IVOA", "IVOA",
                     getTestSubject().getFormValueUnit());

        verify(mockJob);

        mockJob = createMock(Job.class);
        formErrors = new FormErrors();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(TIME_UTYPE + Date.VALUE, "55324"));

        expect(mockJob.getParameterList()).andReturn(parameters).once();

        replay(mockJob);

        setTestSubject(new Date(mockJob, TIME_UTYPE));
        assertTrue("Should be valid.", getTestSubject().isValid(formErrors));
        assertFalse("Should be no errors.", formErrors.hasErrors());
        assertNull("unit should be null", getTestSubject().getFormValueUnit());

        verify(mockJob);
    }

    @Test
    public void testBuildSearches() {
        log.debug("testBuildSearches()...");
        String formValue;
        Double expectedLower;
        Double expectedUpper;

        formValue = "55324..55689";
        expectedLower = 55324.0;
        expectedUpper = 55689.0;
        testBuildSearch(formValue, expectedLower, expectedUpper);

        formValue = "55324";
        expectedLower = 55324.0;
        testBuildSearch(formValue, expectedLower, null);

        formValue = "55324";
        expectedUpper = 55325.0;
        testBuildSearch(formValue, null, expectedUpper);

        formValue = "55324";
        testBuildSearch(formValue, null, null);

        formValue = "55689..55324";
        expectedLower = 55689.0;
        expectedUpper = 55324.0;
        testBuildSearch(formValue, expectedLower, expectedUpper);

        log.debug("testBuildSearches() passed.");

    }

    private void testBuildSearch(final String formValue,
                                 final Double expectedLower,
                                 final Double expectedUpper) {
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);
        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final List<FormError> errorList = new ArrayList<>();
        final Date date = new Date(formValue, TIME_UTYPE, cal.getTime());

        if ((expectedLower != null) && (expectedUpper != null)
                && (expectedLower > expectedUpper)) {
            date.setLowerNumber(expectedLower);
            date.setUpperNumber(expectedUpper);
        } else {
            final boolean valid = date.isValid(new FormErrors());

            log.debug("formValue[" + formValue + "] " + date + " valid: "
                              + valid);
            assertTrue("Validation failed.", valid);
        }

        if (expectedLower == null) {
            date.setLowerNumber(null);
        }

        if (expectedUpper == null) {
            date.setUpperNumber(null);
        }

        IntervalSearch template = (IntervalSearch) date.buildSearch(errorList);
        if ((expectedLower != null) && (expectedUpper != null) && (expectedLower
                > expectedUpper)) {
            assertNull("Expected template to be null.", template);
        } else {
            if ((expectedLower == null) && (expectedUpper == null)) {
                Double value = Double.parseDouble(date.getFormValue());
                assertEquals("Expected lower value is " + date.getFormValue(),
                             value, template.getLower());
                assertEquals("Expected upper value is " + date.getFormValue(),
                             value, template.getUpper());
                assertEquals("Expected errorList to be empty.", 0,
                             errorList.size());
            } else {
                assertEquals("Expected lower value is " + expectedLower,
                             expectedLower, template.getLower());
                assertEquals("Expected upper value is " + expectedUpper,
                             expectedUpper, template.getUpper());
                assertEquals("Expected errorList to be empty.", 0,
                             errorList.size());
            }
        }
    }
}
