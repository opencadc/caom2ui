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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ca.nrc.cadc.caom2.TimestampSearch;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.search.parser.DateParser;
import ca.nrc.cadc.search.parser.exception.DateParserException;
import ca.nrc.cadc.util.StringUtil;

import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.ParameterUtil;


public class TimestampFormConstraint extends AbstractFormConstraint
        implements SearchableFormConstraint
{
    // Constants used to construct name for form elements.
    public static final String NAME = "@TimestampFormConstraint";
    public static final String VALUE = "@TimestampFormConstraint.value";


    private java.util.Date lowerDate;
    private java.util.Date upperDate;

    // Normalized unit value
    private String unit;


    /**
     * Constructor used by the high level Runner classes.
     * @param job       The UWS Job.
     * @param utype     String utype.
     */
    public TimestampFormConstraint(final Job job, final String utype)
    {
        this(ParameterUtil.findParameterValue(utype,
                                              job.getParameterList()), utype);
    }


    /**
     * Constructor.
     *
     * @param value     String user-entered value.
     * @param utype     String utype.
     */
    public TimestampFormConstraint(final String value, final String utype)
    {
        super(utype);
        setFormValue(value);
    }


    public java.util.Date getLowerDate()
    {
        return lowerDate;
    }

    public void setLowerDate(java.util.Date lowerDate)
    {
        this.lowerDate = lowerDate;
    }

    public java.util.Date getUpperDate()
    {
        return upperDate;
    }

    public void setUpperDate(java.util.Date upperDate)
    {
        this.upperDate = upperDate;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    /**
     * Create a search template to use in the query.
     *
     * @param errorList     The error list so far.  This method should append
     *                      to it as needed.
     */
    @Override
    public TimestampSearch buildSearch(
            final List<FormError> errorList)
    {
        TimestampSearch searchTemplate;

        if ((getLowerDate() == null) && (getUpperDate() == null))
        {
            searchTemplate = null;
        }
        else
        {
            try
            {
                searchTemplate = new TimestampSearch(getUType(),
                                                                 getLowerDate(),
                                                                 getUpperDate());
            }
            catch (IllegalArgumentException e)
            {
                errorList.add(new FormError(NAME, e.getMessage()));
                searchTemplate = null;
            }
        }

        return searchTemplate;
    }

    /**
     * A form is valid if all form values have been successfully
     * validated. Validated form values can be null. A form
     * containing one or more null values, or all null values,
     * is considered valid.
     *
     * @param formErrors The FormErrors instance.
     * @return boolean true if all form values are valid, false otherwise.
     */
    @Override
    public boolean isValid(final FormErrors formErrors)
    {
        final String utype = getUType();

        if (ObsModel.isUTCDateUtype(utype))
        {
            try
            {
                load();
            }
            catch (DateParserException e)
            {
                addError(new FormError(utype + VALUE, e.getMessage()));
            }
        }
        else
        {
            addError(new FormError(utype + VALUE, "Invalid utype: " + utype));
        }

        formErrors.set(utype + NAME, getErrorList());
        return getErrorList().isEmpty();
    }

    /**
     * Populate the necessary values.
     * @throws DateParserException      If the given date cannot be used.
     */
    final void load() throws DateParserException
    {
        if (StringUtil.hasText(getLowerValue()))
        {
            final DateParser lowerDateParser =
                    new DateParser(getLowerValue());

            if (lowerDateParser.getDate() != null)
            {
                setLowerDate(lowerDateParser.getDate());
            }
        }

        if (StringUtil.hasText(getUpperValue()))
        {
            final DateParser upperDateParser =
                    new DateParser(getUpperValue());

            if (upperDateParser.getDate() != null)
            {
                setUpperDate(upperDateParser.getDate());
            }
        }

        // Single value entered.  Treat it as a range.
        if ((getLowerDate() == null) && (getUpperDate() == null)
            && super.hasData())
        {
            final DateParser dateParser =
                    new DateParser(getFormValue());
            final java.util.Date enteredDate = dateParser.getDate();
            final Calendar lowerCalendar = Calendar.getInstance(
                    TimeZone.getTimeZone("UTC"));
            final Calendar upperCalendar = Calendar.getInstance(
                    TimeZone.getTimeZone("UTC"));

            lowerCalendar.setTime(enteredDate);
            upperCalendar.setTime(enteredDate);

            if (dateParser.getLastParsedField() == Calendar.MILLISECOND)
            {
                upperCalendar.add(Calendar.MILLISECOND, 1);
            }
            else if (dateParser.getLastParsedField() == Calendar.SECOND)
            {
                upperCalendar.add(Calendar.SECOND, 1);
            }
            else if (dateParser.getLastParsedField() == Calendar.MINUTE)
            {
                upperCalendar.add(Calendar.MINUTE, 1);
            }
            else if (dateParser.getLastParsedField()
                     == Calendar.HOUR_OF_DAY)
            {
                upperCalendar.add(Calendar.HOUR_OF_DAY, 1);
            }
            else if (dateParser.getLastParsedField()
                     == Calendar.DAY_OF_MONTH)
            {
                upperCalendar.add(Calendar.HOUR_OF_DAY, 24);
            }
            else if (dateParser.getLastParsedField() == Calendar.MONTH)
            {
                upperCalendar.add(Calendar.MONTH, 1);
            }
            else
            {
                upperCalendar.add(Calendar.YEAR, 1);
            }

            setLowerDate(lowerCalendar.getTime());
            setUpperDate(upperCalendar.getTime());
        }
    }
}
