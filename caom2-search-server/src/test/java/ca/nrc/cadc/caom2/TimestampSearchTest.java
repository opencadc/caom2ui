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
 * 28/05/14 - 1:14 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;

import java.util.Calendar;
import java.util.Date;

import ca.nrc.cadc.date.DateUtil;
import org.junit.Test;

import ca.nrc.cadc.AbstractUnitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class TimestampSearchTest
        extends AbstractUnitTest<TimestampSearch>
{
    @Test
    public void constructorBadDates() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 12, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final Date upperValue = calendar.getTime();

        calendar.add(Calendar.MINUTE, 10);

        final Date lowerValue = calendar.getTime();

        try
        {
            setTestSubject(new TimestampSearch("NAME", lowerValue, upperValue));
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Wrong message.",
                         "Lower date (1977-11-25 03:22:00.000) is after Upper "
                         + "date (1977-11-25 03:12:00.000).",
                         e.getMessage());
        }
    }
}
