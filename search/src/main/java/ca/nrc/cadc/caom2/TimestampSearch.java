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
 * 28/05/14 - 12:50 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;

import java.util.Date;

import ca.nrc.cadc.date.DateUtil;


public class TimestampSearch extends AbstractTemplate
{
    private final Date lower;
    private final Date upper;
    private final boolean closedLower;
    private final boolean closedUpper;


    /**
     * Create a TimestampSearchTemplate. The default is for the ends to be
     * closed (include the specified values).
     *
     * @param name          The name of this search template.
     * @param lower         The lower end of the range.
     * @param upper         The upper end of the range.
     */
    public TimestampSearch(final String name, final Date lower,
                           final Date upper)
    {
        this(name, lower, upper, true, true);
    }

    /**
     * Complete constructor.
     * @param name          The name of this search template.
     * @param lower         The lower end of the range.
     * @param upper         The upper end of the range.
     * @param closedLower   Whether to treat the lower end as &ge; lower.
     * @param closedUpper   Whether to treat the upper end as &le; upper.
     */
    public TimestampSearch(final String name, final Date lower,
                           final Date upper,
                           final boolean closedLower,
                           final boolean closedUpper)
    {
        super(name);

        if ((lower != null) && (upper != null) && lower.after(upper))
        {
            throw new IllegalArgumentException(
                    String.format("Lower date (%s) is after Upper date (%s).",
                                  DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                                         DateUtil.UTC).
                                          format(lower),
                                  DateUtil.getDateFormat(DateUtil.ISO_DATE_FORMAT,
                                                         DateUtil.UTC).
                                          format(upper)));
        }

        this.lower = lower;
        this.upper = upper;
        this.closedLower = closedLower;
        this.closedUpper = closedUpper;
    }

    public Date getLower()
    {
        return lower;
    }

    public Date getUpper()
    {
        return upper;
    }

    public boolean isClosedLower()
    {
        return closedLower;
    }

    public boolean isClosedUpper()
    {
        return closedUpper;
    }
}
