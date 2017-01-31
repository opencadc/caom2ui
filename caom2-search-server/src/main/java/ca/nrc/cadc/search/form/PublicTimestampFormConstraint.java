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
 * 29/05/14 - 2:08 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.form;

import java.util.TimeZone;

import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.ParameterUtil;


/**
 * Story 1591, Task 5197
 * Checkbox for public data only
 *
 * jenkinsd 2014.05.29
 */
public class PublicTimestampFormConstraint extends TimestampFormConstraint
{
    public static final String NAME = "@PublicTimestampFormConstraint";
    public static final String VALUE = "@PublicTimestampFormConstraint.value";

    private final boolean publicDataOnlyFlag;


    /**
     * Constructor used by the high level Runner classes.
     *
     * @param job   The UWS Job.
     * @param utype String utype.
     */
    public PublicTimestampFormConstraint(final Job job, final String utype)
    {
        super(job, utype);
        final String isPublicFlagParamter =
                ParameterUtil.findParameterValue(utype + VALUE,
                                                 job.getParameterList());

        publicDataOnlyFlag = StringUtil.hasText(isPublicFlagParamter)
                             && isPublicFlagParamter.equals("on");

        if (isPublicDataOnly())
        {
            final java.util.Date today = new java.util.Date();
            setFormValue("<= " + DateUtil.getDateFormat(
                    DateUtil.ISO_DATE_FORMAT, TimeZone.getTimeZone("UTC")).
                    format(today));
        }
        else
        {
            setFormValue(null);
        }
    }


    public boolean isPublicDataOnly()
    {
        return publicDataOnlyFlag;
    }
}
