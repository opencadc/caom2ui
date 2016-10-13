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

import ca.nrc.cadc.uws.Job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class to represent a drop down or selection list.
 *
 * @author jburke
 */
public class Hierarchy extends AbstractFormConstraint implements FormConstraint
{
    // Constants used to construct name for form elements.
    public static final String NAME = "@Hierarchy";


    // List of enumerated components.
    private final List<Enumerated> enumerated = new ArrayList<>();


    /**
     * Hierarchy constructor instantiates a new instance with the given
     * parameters.
     *
     * @param job   UWS Job.
     * @param uType The String uType.
     */
    public Hierarchy(final Job job, final String uType)
    {
        super(uType);

        final HierarchyUType hierarchyUType = new HierarchyUType(uType);

        final List<String> uTypes = hierarchyUType.getUTypes();

        for (final String type : uTypes)
        {
            final boolean hidden = hierarchyUType.isHidden(type);
            enumerated.add(new Enumerated(job, type, null, hidden));
        }
    }

    /**
     * Form interface requirements.
     */
    public boolean isValid(FormErrors formErrors)
    {
        formErrors.set("hierarchy", getErrorList());
        return getErrorList().isEmpty();
    }

    public boolean hasData()
    {
        return true;
    }

    public List<Enumerated> getEnumerated()
    {
        return enumerated;
    }

    /**
     * @return String representation of the form.
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("Hierarchy[ ");

        for (final Iterator it = enumerated.iterator(); it.hasNext();)
        {
            sb.append(it.next().toString());
            if (it.hasNext())
            {
                sb.append(", ");
            }
        }

        sb.append(" ]");
        return sb.toString();
    }

}
