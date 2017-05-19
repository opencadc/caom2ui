/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.util;

import ca.nrc.cadc.uws.Parameter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


public class ParameterUtil
{
    public enum ParameterName
    {
        QUERY, LANG, REQUEST, FORMAT
    }

    public ParameterUtil()
    {
    }


    public String getValue(String name, List<Parameter> parameters)
    {
        for (final Parameter parameter : parameters)
        {
            if (parameter.getName().equals(name))
            {
                return parameter.getValue();
            }
        }

        return null;
    }

    /**
     * Obtain a list of all parameters whose name matches the given one in the parameter list.
     * @param name              The name to search for.
     * @param parameters        The list of parameters.
     * @return                  List of values, or empty list.  Never null.
     */
    public List<String> getValues(String name, List<Parameter> parameters)
    {
        final List<String> values = new ArrayList<>();

        for (final Parameter parameter : parameters)
        {
            if (parameter.getName().equals(name))
            {
                values.add(parameter.getValue());
            }
        }

        return values;
    }

    public String[] getValuesAsArray(final String name, final List<Parameter> parameters)
    {
        final List<String> list = getValues(name, parameters);
        return list.toArray(new String[list.size()]);
    }

    /**
     * Pull the Job related parameters from the given request.
     * @param request           The request to pull from.
     * @return                  Set of Parameters, or empty Set.  Never null.
     * @throws NullPointerException     If request is null.
     */
    public Set<Parameter> asParameterSet(final HttpServletRequest request)
    {
        final Set<Parameter> parameters = new HashSet<>();

        for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet())
        {
            final String nextKey = entry.getKey().toUpperCase();

            for (final ParameterName parameterName : ParameterName.values())
            {
                if (parameterName.name().equals(nextKey))
                {
                    parameters.add(new Parameter(nextKey, entry.getValue()[0]));
                    break;
                }
            }
        }

        return parameters;
    }
}
