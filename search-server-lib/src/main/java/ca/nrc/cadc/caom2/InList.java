/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2007.                            (c) 2007.
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

package ca.nrc.cadc.caom2;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple list of values specification. The value can be a list of constants (literals)
 * or a sub-query.
 * Created on 21-Jun-2005
 *
 * @author pdowler
 * @version $Version$
 */
public class InList extends AbstractTemplate
{
    private static final long serialVersionUID = 200602171500L;

    private final List<String> values = new ArrayList<String>();
    private final String subquery;


    public InList(final String name, final List<String> values)
    {
        this(name, values, null);
    }

    public InList(final String name, final List<String> values,
                  final String subquery)
    {
        super(name);

        if (values != null)
        {
            getValues().addAll(values);
        }

        this.subquery = subquery;
    }


    public boolean hasValues()
    {
        return !getValues().isEmpty();
    }

    public List<String> getValues()
    {
        return values;
    }

    public String getSubquery()
    {
        return subquery;
    }

    /**
     * Return String representation of class.
     */
    public String toString()
    {
        return "InList[" + getName() + "," + getValues() + "," + getSubquery()
               + "]";
    }
}
