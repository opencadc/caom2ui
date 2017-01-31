/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
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
 * 9/6/13 - 1:23 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.uws;

import java.util.ArrayList;
import java.util.List;

import ca.nrc.cadc.util.StringUtil;


public class RegexParameterUtil
{
    /**
     * Use regex to find a parameter value.
     *
     * @param regex         The Regex to search on.
     * @param paramList     The list of parameters.
     * @return              String item, or null.
     */
    public static String findParameterValue(final String regex,
                                            final List<Parameter> paramList)
    {
        for (final Parameter parameter : paramList)
        {
            if (parameter.getName().matches(regex))
            {
                return parameter.getValue();
            }
        }

        return null;
    }

    /**
     * Find all values for the specified parameter regex.
     *
     * @param regex         The Regex to search on.
     * @param paramList     The list of parameters.
     * @return List of values, possibly empty, never null.
     */
    public static List<String> findParameterValues(final String regex,
                                                   final List<Parameter> paramList)
    {
        final List<String> ret = new ArrayList<String>();
        for (final Parameter parameter : paramList)
        {
            if (parameter.getName().matches(regex))
            {
                final String str = parameter.getValue();
                if (!StringUtil.hasText(str))
                {
                    ret.add("");
                }
                else
                {
                    ret.add(str);
                }
            }
        }

        return ret;
    }
}
