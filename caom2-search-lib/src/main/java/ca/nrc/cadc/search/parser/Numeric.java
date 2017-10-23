/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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

package ca.nrc.cadc.search.parser;

/**
 * @author jburke
 */
public class Numeric
{
    public Number value;
    public Number tolerance;
    public String unit;


    public Numeric()
    {
        this.value = null;
        this.tolerance = null;
        this.unit = null;
    }


    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("Numeric[");
        sb.append(value);
        sb.append(",");

        if (unit != null)
        {
            sb.append(unit);
        }

        sb.append(",");

        if (tolerance != null)
        {
            sb.append(tolerance);
        }

        sb.append("]");

        return sb.toString();
    }
}
