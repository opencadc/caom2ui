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

/**
 * Created on 14-Feb-2006.
 *
 * @author pdowler
 * @version $Version$
 */
public class NumericSearch extends AbstractTemplate
{
    private static final long serialVersionUID = 200602221500L;

    public Number lower;
    public Number upper;
    public boolean closedLower = true;
    public boolean closedUpper = true;

    public NumericSearch(final String name, final Number value)
    {
        this(name, value, value);
    }


    /**
     * Create a NumericSearch. The default is for the ends to be closed (include
     * the specfied values).
     *
     * @param name
     * @param lower
     * @param upper
     */
    public NumericSearch(String name, Number lower, Number upper)
    {
        this(name, lower, upper, true, true);
    }

    public NumericSearch(String name, Number lower, Number upper,
                         boolean closedLower, boolean closedUpper)
    {
        super(name);

        if (lower != null && upper != null && lower.doubleValue() > upper
                .doubleValue())
        {
            throw new IllegalArgumentException("lower > upper");
        }
        this.lower = lower;
        this.upper = upper;
        this.closedLower = closedLower;
        this.closedUpper = closedUpper;
    }

    /**
     * Returns String representation of class.
     */
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("NumericSearch[");
        sb.append(getName());
        sb.append(",");

        if (closedLower)
        {
            sb.append("[");
        }
        else
        {
            sb.append("(");
        }

        sb.append(lower);
        sb.append(",");
        sb.append(upper);

        if (closedUpper)
        {
            sb.append("]");
        }
        else
        {
            sb.append(")");
        }

        sb.append("]");

        return sb.toString();
    }
}
