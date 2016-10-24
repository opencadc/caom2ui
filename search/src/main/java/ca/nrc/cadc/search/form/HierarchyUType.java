/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2008.                            (c) 2008.
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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.form;

import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.util.ArrayUtil;
import ca.nrc.cadc.util.StringUtil;

import java.util.*;


/**
 * @author jburke
 */
public class HierarchyUType
{
    public static final String MAX_LAST_MODIFIED_COLUMN_NAME =
            "maxLastModified";

    public static final String MAX_LAST_MODIFIED_UTYPE =
            "Observation." + MAX_LAST_MODIFIED_COLUMN_NAME;

    private static final String UTYPE_DELIMITER = "/";
    private static final String UTYPE_VALUE_DELIMITER = ":";

    protected final List<String> uTypes = new ArrayList<>();
    protected final Map<String, List<String>> values = new HashMap<>();


    /**
     * Constructs a new instance using the specifed hierarchyUType.  The
     * hierarchyUType will be parsed into a List of uTypes, and into a Map
     * containing the hierarchyUType and a List of hierarchyUType values.
     *
     * @param hierarchyUType The hierarchyUType.
     */
    public HierarchyUType(final String hierarchyUType)
    {
        parse(hierarchyUType);
    }


    /**
     * @return List of utype's.
     */
    public List<String> getUTypes()
    {
        return uTypes;
    }

    /**
     * Returns a list of values for the uType, or null if no
     * values exist for the uType.
     *
     * @param uType The uType to search for.
     * @return List of values for this uType, or null if none exist.
     */
    public List<String> getValues(final String uType)
    {
        return values.get(uType);
    }

    /**
     * Checks if a uType is a hidden form component. A hidden form component
     * is defined as a uType with a single value.
     *
     * @param uType The uType to search for.
     * @return true if uType is a hidden form component, false otherwise.
     */
    public boolean isHidden(final String uType)
    {
        return (values.containsKey(uType) && (getValues(uType).size() == 1));
    }

    /**
     * Validates the utypes against the valid utypes defined in the ObsModel.
     *
     * @return true if all utypes are valid utypes as defined in the ObsModel,
     * false otherwise.
     */
    public boolean isValid()
    {
        final String[] uTypeArray = uTypes.toArray(new String[uTypes.size()]);

        return (uTypeArray.length > 0);
    }

    /**
     * Parse the hierarchyUType into a List of hierarchyUType's, and if the
     * hierarchyUType has values, add the hierarchyUType to the Map as the key,
     * and the hierarchyUType values as the value.
     *
     * @param hierarchyUType The hierarchy uType of values.
     */
    private void parse(final String hierarchyUType)
    {
        if (StringUtil.hasLength(hierarchyUType))
        {
            final String[] uTypeArray = hierarchyUType.split(UTYPE_DELIMITER);

            for (final String token : uTypeArray)
            {
                final String[] valueArray = token.split(UTYPE_VALUE_DELIMITER);
                final String nextUType = valueArray[0];

                if (valueArray.length == 1)
                {
                    uTypes.add(nextUType);
                }
                else
                {
                    final List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(valueArray).
                            subList(1, valueArray.length));
                    uTypes.add(nextUType);
                    values.put(nextUType, list);
                }
            }
        }
    }

}
