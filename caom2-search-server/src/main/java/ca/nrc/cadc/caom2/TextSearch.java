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
public class TextSearch extends AbstractTemplate
{
    private static final long serialVersionUID = 200602151500L;

    public String lower;
    public String upper;
    public boolean wild;
    public boolean ignoreCase;
    private final boolean negateFlag;


    /**
     * TextSearch which matches any (non-null) value.
     *
     * @param name The name of this search field.
     */
    public TextSearch(String name)
    {
        super(name);
        this.negateFlag = false;
    }

    /**
     * TextSearch which matches the specified value. Wildcard characters (*) are supported.
     *
     * @param name  The name of the search field.
     * @param value The entered value.
     */
    public TextSearch(String name, String value)
    {
        this(name, value, value, false, false, false);

    }

    /**
     * TextSearch which matches the specified value with optional additional
     * wildcard. If the wild argument is true, additional wildcards before and after
     * are included.
     *
     * @param name  The name of the search field.
     * @param value The entered value.
     * @param wild  Treat as wild card search.
     */
    public TextSearch(String name, String value, boolean wild)
    {
        this(name, value, value, wild, false, false);
    }

    /**
     * TextSearch which matches the specified value with wildcard and case-sensitivity
     * control. If <code>wild</code> is true, additional wildcards before and after
     * are included. If <code>ignoreCase</code> is true, then all string comparisons are
     * case-insensitive.
     *
     * @param name       The name of the search field.
     * @param value      The entered value.
     * @param wild       Treat as wild card search.
     * @param ignoreCase Flag to indicate case insensitivity.
     */
    public TextSearch(String name, String value, boolean wild,
                      boolean ignoreCase)
    {
        this(name, value, value, wild, ignoreCase, false);
    }

    /**
     * TextSearch which matches a range of strings.
     *
     * @param name  The name of the search field.
     * @param lower The left side of the range.
     * @param upper The right side of the range.
     */
    public TextSearch(String name, String lower, String upper)
    {
        this(name, lower, upper, false, false, false);
    }

    protected TextSearch(String name, String lower, String upper, boolean wild,
                         boolean ignoreCase, final boolean negateFlag)
    {
        super(name);

        if ((lower != null) && (upper != null) && (lower.compareTo(upper) > 0))
        {
            throw new IllegalArgumentException("lower > upper");
        }

        this.lower = lower;
        this.upper = upper;
        this.wild = wild;
        this.ignoreCase = ignoreCase;
        this.negateFlag = negateFlag;
    }

    /**
     * Obtain whether to treat this condition as a negation condition; meaning
     * not equals.
     *
     * @return True if negated, false otherwise.
     */
    public boolean isNegated()
    {
        return negateFlag;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        final TextSearch that = (TextSearch) o;

        return (getName().equals(((TextSearch) o).getName()))
               && (wild == that.wild) && (negateFlag == that.negateFlag)
               && !((lower != null) ? !lower.equals(that.lower) :
                    (that.lower != null))
               && !(upper != null ? !upper.equals(that.upper) :
                    (that.upper != null));

    }

    @Override
    public int hashCode()
    {
        int result = lower != null ? lower.hashCode() : 0;
        result = 31 * result + (upper != null ? upper.hashCode() : 0);
        result = 31 * result + (wild ? 1 : 0);
        result = 31 * result + (negateFlag ? 1 : 0);
        return result;
    }

    /**
     * Return String representation of class.
     */
    @Override
    public String toString()
    {
        return "TextSearch[" + getName() + "," + lower + "," + upper + ","
               + wild + "," + ignoreCase + "]";
    }
}
