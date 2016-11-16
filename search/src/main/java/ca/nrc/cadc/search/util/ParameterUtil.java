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
        if ((name == null) || (parameters == null))
        {
            return null;
        }
        else
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
    }

    public List<String> getValues(String name, List<Parameter> parameters)
    {
        if ((name == null) || (parameters == null))
        {
            return null;
        }
        else
        {
            List<String> list = null;
            for (final Parameter parameter : parameters)
            {
                if (parameter.getName().equals(name))
                {
                    if (list == null)
                    {
                        list = new ArrayList<>();
                    }

                    list.add(parameter.getValue());
                }
            }
            return list;
        }
    }

    public String[] getValuesAsArray(final String name,
                                            final List<Parameter> parameters)
    {
        List<String> list = getValues(name, parameters);
        if (list == null)
        {
            return null;
        }

        return list.toArray(new String[list.size()]);
    }

    public Map<String, Object> asMap(final List<Parameter> parameters)
    {
        final Map<String, Object> map = new HashMap<>();

        if (parameters != null)
        {
            for (final Parameter parameter : parameters)
            {
                map.put(parameter.getName(), parameter.getValue());
            }
        }

        return map;
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

        for (final Map.Entry<String, String[]> entry
                : request.getParameterMap().entrySet())
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
