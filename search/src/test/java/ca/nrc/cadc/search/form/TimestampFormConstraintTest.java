/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2014.                         (c) 2014.
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
 * 28/05/14 - 10:56 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.form;

import java.util.*;

import org.junit.Test;

import ca.nrc.cadc.caom2.TimestampSearch;
import ca.nrc.cadc.date.DateUtil;

import static org.junit.Assert.*;


public class TimestampFormConstraintTest
        extends AbstractFormConstraintTest<TimestampFormConstraint>
{
    final FormErrors formErrors = new FormErrors();
    final List<FormError> errorList = new ArrayList<FormError>();
    

    @Test
    public void isValidBogusUType() throws Exception
    {
        setTestSubject(new TimestampFormConstraint("", "BOGUS"));

        final FormError expectedError =
                new FormError("BOGUS@TimestampFormConstraint.value",
                              "Invalid utype: BOGUS");

        assertFalse("Should not be valid for wrong utype.",
                    getTestSubject().isValid(getFormErrors()));
        assertEquals("Form errors should be equal.",
                     expectedError, getFormErrors().get().get(0));
    }

    @Test
    public void isValidEmptyEntry() throws Exception
    {
        setTestSubject(new TimestampFormConstraint("", "Plane.dataRelease"));
        assertTrue("Should be valid.",
                   getTestSubject().isValid(getFormErrors()));
        assertTrue("Form errors should be empty.",
                   getFormErrors().get().isEmpty());
    }

    @Test
    public void isValidBadEntry() throws Exception
    {
        setTestSubject(new TimestampFormConstraint("BOGUS",
                                                   "Plane.dataRelease"));

        final FormError expectedError =
                new FormError("Plane.dataRelease@TimestampFormConstraint.value",
                              "Unable to parse BOGUS");

        assertFalse("Should not be valid for bad entry.",
                    getTestSubject().isValid(getFormErrors()));
        assertEquals("Form errors should be equal.",
                     expectedError, getFormErrors().get().get(0));
    }

    @Test
    public void isValidCalendarEntry() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        setTestSubject(new TimestampFormConstraint(
                DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                       TimeZone.getTimeZone("UTC")).format(
                        calendar.getTime()),
                "Plane.dataRelease"));

        assertTrue("Should be valid.",
                   getTestSubject().isValid(getFormErrors()));
        assertEquals("Lower date is wrong.", calendar.getTime(),
                     getTestSubject().getLowerDate());

        calendar.add(Calendar.MILLISECOND, 1);
        assertEquals("Upper date is wrong.", calendar.getTime(),
                     getTestSubject().getUpperDate());
    }

    @Test
    public void isValidCalendarRangeEntry() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);

        final java.util.Date lowerDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);

        final java.util.Date upperDate = calendar.getTime();

        final String dateRange =
                String.format("%s..%s",
                              DateUtil.getDateFormat(
                                      DateUtil.ISO_DATE_FORMAT,
                                      TimeZone.getTimeZone("UTC")).format(
                                      lowerDate),
                              DateUtil.getDateFormat(
                                      DateUtil.ISO_DATE_FORMAT,
                                      TimeZone.getTimeZone("UTC")).format(
                                      upperDate));

        setTestSubject(new TimestampFormConstraint(dateRange,
                                                   "Plane.dataRelease"));

        assertTrue("Should be valid.",
                   getTestSubject().isValid(getFormErrors()));

        assertEquals("Lower date is wrong.", lowerDate,
                     getTestSubject().getLowerDate());
        assertEquals("Upper date is wrong.", upperDate,
                     getTestSubject().getUpperDate());
    }

    @Test
    public void isValidMJDEntry() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final double mjdValue =
                DateUtil.toModifiedJulianDate(calendar.getTime());

        setTestSubject(new TimestampFormConstraint(Double.toString(mjdValue),
                                                   "Plane.dataRelease"));

        assertTrue("Should be valid.",
                   getTestSubject().isValid(getFormErrors()));
        assertEquals("Lower date is wrong.", calendar.getTime().getTime(),
                     getTestSubject().getLowerDate().getTime(), 1.0d);

        calendar.add(Calendar.MINUTE, 1);
        assertEquals("Upper date is wrong.", calendar.getTime().getTime(),
                     getTestSubject().getUpperDate().getTime(), 1.0d);
    }

    @Test
    public void isValidMJDCalendarMixedEntry() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final java.util.Date lowerDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);

        final java.util.Date upperDate = calendar.getTime();

        final String dateRange =
                String.format("%s..%s",
                              DateUtil.toModifiedJulianDate(lowerDate),
                              DateUtil.getDateFormat(
                                      DateUtil.ISO_DATE_FORMAT,
                                      TimeZone.getTimeZone("UTC")).format(
                                      upperDate));

        setTestSubject(new TimestampFormConstraint(dateRange,
                                                   "Plane.dataRelease"));

        assertTrue("Should be valid.",
                   getTestSubject().isValid(getFormErrors()));

        assertEquals("Lower date is wrong.", lowerDate.getTime(),
                     getTestSubject().getLowerDate().getTime(), 1.0d);
        assertEquals("Upper date is wrong.", upperDate.getTime(),
                     getTestSubject().getUpperDate().getTime(), 1.0d);
    }

    @Test
    public void buildSearchEmptyValue() throws Exception
    {
        setTestSubject(new TimestampFormConstraint("", "Plane.dataRelease"));

        // Required before buildSearch()
        getTestSubject().isValid(getFormErrors());

        final TimestampSearch timestampSearchTemplate =
                getTestSubject().buildSearch(getErrorList());

        assertTrue("Should be no errors.", getErrorList().isEmpty());
        assertNull("Should be no template.", timestampSearchTemplate);
    }

    @Test
    public void buildSearch() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final java.util.Date lowerDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);

        final java.util.Date upperDate = calendar.getTime();

        final String dateRange =
                String.format("%s..%s",
                              DateUtil.toModifiedJulianDate(lowerDate),
                              DateUtil.getDateFormat(
                                      DateUtil.ISO_DATE_FORMAT,
                                      TimeZone.getTimeZone("UTC")).format(
                                      upperDate));

        setTestSubject(new TimestampFormConstraint(dateRange,
                                                   "Plane.dataRelease"));

        // Required before buildSearch()
        getTestSubject().isValid(getFormErrors());

        final TimestampSearch timestampSearchTemplate =
                getTestSubject().buildSearch(getErrorList());

        assertEquals("Lower date is wrong.", lowerDate.getTime(),
                     timestampSearchTemplate.getLower().getTime(), 1.0d);
        assertEquals("Upper date is wrong.", upperDate.getTime(),
                     timestampSearchTemplate.getUpper().getTime(), 1.0d);
        assertTrue("Should be closed lower.",
                   timestampSearchTemplate.isClosedLower());
        assertTrue("Should be closed upper.",
                   timestampSearchTemplate.isClosedUpper());
    }

    protected FormErrors getFormErrors()
    {
        return formErrors;
    }

    protected List<FormError> getErrorList()
    {
        return errorList;
    }
}
