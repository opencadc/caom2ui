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

import ca.nrc.cadc.search.parser.Range;


/**
 * Simple range search template for a RA and Dec. The template always specifies
 * a lower and upper bound for an RA and Dec.
 *
 * @author jburke $
 */
public class RangeSearch<C extends Comparable<C>> extends AbstractTemplate
{
    private static final long serialVersionUID = 201405150845L;

    private final Range<C> lowerRange;
    private final Range<C> upperRange;

    /**
     * Creates an range template for a range of RA and Dec values.
     *
     * @param name       This search's name.
     * @param lowerRange The lower value of the range (inclusive).
     * @param upperRange The upper value of the range (inclusive).
     */
    public RangeSearch(final String name, final Range<C> lowerRange,
                       final Range<C> upperRange)
    {
        super(name);
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;

        if ((lowerRange == null) || (lowerRange.getLowerValue() == null)
            || (lowerRange.getUpperValue() == null))
        {
            throw new IllegalArgumentException("null RA range or value");
        }
        else if ((upperRange == null) || (upperRange.getLowerValue() == null)
            || (upperRange.getUpperValue() == null))
        {
            throw new IllegalArgumentException("null Dec range or value");
        }
        else if (lowerRange.getLowerValue().compareTo(
                 lowerRange.getUpperValue()) > 0)
        {
            throw new IllegalArgumentException("Lower > Upper in lower range.");
        }
        else if (upperRange.getLowerValue().compareTo(
                upperRange.getUpperValue()) > 0)
        {
            throw new IllegalArgumentException("Lower > Upper in upper range.");
        }
    }

    public Range<C> getLowerRange()
    {
        return lowerRange;
    }

    public Range<C> getUpperRange()
    {
        return upperRange;
    }

    @Override
    public String toString()
    {
        return "RangeSearch[" + getName() + "," + lowerRange.getLowerValue()
               + "," + lowerRange.getOperand() + ","
               + lowerRange.getUpperValue() + "," + upperRange.getLowerValue()
               + "," + upperRange.getOperand() + ","
               + upperRange.getUpperValue() + "]";
    }

}
