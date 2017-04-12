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
 * Simple interval search template. The template always specifies a lower
 * and upper spectral bound. Optionally, it can contain a threshold value (epsilon).
 * If epsilon is null, the template is interpreted to mean <em>intersection</em>.
 * If epsilon is non null, the template is interpreted as a fuzzy matching template.
 * See the constructor comments for the exact meanings.
 *
 * @version $Revision: 166 $
 * @author $Author: jburke $
 */
public class IntervalSearch extends AbstractTemplate
{
    private static final long serialVersionUID = 200602221500L;

    private Double lower;
    private Double upper;
    private Double epsilon;
    private String shift;
    private String units;


    /**
     * Creates an interval inclusion template. An interval matches this template
     * if it includes the specified value.
     *
     * <code>i.lower &le; value and value &le; i.upper</code>
     *
     * @param name      This search's name.
     * @param value     The Double value to treat as a range value.
     * @param units     The units to search on.
     */
    public IntervalSearch(String name, Double value, String units)
    { 
        this(name, value, value, units);
    }
    
    /**
     * Creates an interval intersection template. An interval matches this
     * template if it overlaps the specified bounds.
     *
     * <code>i.lower &le; upper and lower &le; i.upper</code>
     *
     * @param name      This search's name.
     * @param lower     The lower value of the range (inclusive).
     * @param upper     The upper value of the range (inclusive).
     * @param units     The units to search on.
     */
    public IntervalSearch(String name, Double lower, Double upper, String units)
    {
        this(name, lower, upper, null, null, units);
    }

    /**
     * Creates an interval intersection template. An interval matches this
     * template if it overlaps the specified bounds.
     *
     *
     * <code>i.lower &le; upper and lower &le; i.upper</code>
     *
     *
     * @param name      This search's name.
     * @param lower     The lower value of the range (inclusive).
     * @param upper     The upper value of the range (inclusive).
     * @param epsilon   An optional Epsilon (threshold) value
     * @param shift     An optional shift value.
     * @param units     The units to interpret.
     */
    private IntervalSearch(String name, Double lower, Double upper,
                           Double epsilon, String shift, String units)
    {
        super(name);
        this.lower = lower;
        this.upper = upper;
        this.epsilon = epsilon;
        this.shift = shift;
        this.units = units;
        
        if ((lower != null) && (upper != null) && (lower > upper))
        {
            throw new IllegalArgumentException("lower > upper");
        }
    }


    public Double getLower()
    {
        return lower;
    }

    public Double getUpper()
    {
        return upper;
    }

    public Double getEpsilon()
    {
        return epsilon;
    }

    public String getShift()
    {
        return shift;
    }

    public String getUnits()
    {
        return units;
    }

    public String toString()
    {
        return "IntervalSearch[" + getName() + "," + lower + "," + upper + ","
               + epsilon + "," + shift + "]";
    }
    
}

// end of SpectralSearch.java