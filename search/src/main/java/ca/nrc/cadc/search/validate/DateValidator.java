/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.validate;

import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.util.StringUtil;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;


/**
 *
 * @author jburke
 */
public class DateValidator
{
    public static String ISO_DATE_FORMAT_ALT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
	 * Validates the value as a time and returns the Modified Julian Date
     * for the value. If the value is null, or an empty String, null is
     * returned. A Date in ISO date format is created from the value,
     * converted to a Modified Julian Date, and returned. If a Modified Julian
     * Date can not be created from the value, a Double is created and returned.
	 *
	 * @throws ValidationException if the value cannot be validated.
	 * @return Double of the validated value.
	 */
    public Double validate(final String value)
        throws ValidationException
    {
        if (!StringUtil.hasLength(value))
        {
            return null;
        }

        try
        {
            final Date date = toDate(value, new String[]
            {
                DateUtil.ISO_DATE_FORMAT,
                ISO_DATE_FORMAT_ALT,
                DateUtil.ISO_DATE_FORMAT_TZ
            });
            return DateUtil.toModifiedJulianDate(date);
        }
        catch (final ParseException ignore) { }

        throw new ValidationException("Failed to parse as MJD or number " + value);
    }

	/**
	 * Validates the value as a Date and returns the Date.
     * If the value is null, or an empty String, null is returned.
     * A Date in ISO date format is created from the value.
	 *
	 * @param value The value to validate.
     * @param utc   Whether to parse as UTC.  The alternative is local.
	 * @throws ValidationException if the value cannot be validated.
	 * @return Date of the validated value.
	 */
    public Date validate(final String value, final boolean utc)
        throws ValidationException
    {
        final Date d;

        if (StringUtil.hasText(value))
        {
            try
            {
                d = DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                           (utc ? DateUtil.UTC : DateUtil.LOCAL))
                        .parse(value);
            }
            catch (ParseException ignore)
            {
                throw new ValidationException(
                        "Failed to parse as Date MJD " + value);
            }
        }
        else
        {
            d = null;
        }

        return d;
    }

    public Date toDate(final String source, final String[] formats)
            throws ParseException
    {
        if (!StringUtil.hasText(source) || (formats == null))
        {
            throw new ParseException(
                    "No usable formats or source date is null", -1);
        }

        for (final String format : formats)
        {
            if (!StringUtil.hasLength(format))
            {
                continue;
            }

            try
            {
                return DateUtil.getDateFormat(format, DateUtil.UTC)
                        .parse(source.trim());
            }
            catch (ParseException e)
            {
                // We're just ignoring it as we just want the first one to
                // match!
            }
        }

        throw new ParseException("No usable formats found in >> "
                                 + Arrays.toString(formats), -1);
    }

}
