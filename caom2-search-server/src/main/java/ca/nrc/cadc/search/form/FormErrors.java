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

import ca.nrc.cadc.search.Config;

import java.util.*;

/**
 *
 * @author jburke
 */
public class FormErrors
{
    private Map<String, List<FormError>> formErrors;

    private String errorStyle;


    public FormErrors()
    { 
        this.formErrors = new HashMap<>();
        errorStyle = Config.TEXTBOX_ERROR_STYLE;
    }


    public void set(String formName, FormError formError)
    {
        List<FormError> list = new ArrayList<>();
        list.add(formError);
        formErrors.put(formName, list);
    }

    public void set(String formName, List<FormError> formError)
    {
        formErrors.put(formName, formError);
    }

    public List<FormError> get()
    {
        final List<FormError> list = new ArrayList<>();
        final SortedSet<Map.Entry<String, List<FormError>>> errorMaps =
                new TreeSet<>(new Comparator<Map.Entry<String, List<FormError>>>()
                {
                    /**
                     * Compares its two arguments for order.  Returns a negative integer,
                     * zero, or a positive integer as the first argument is less than, equal
                     * to, or greater than the second.<p>
                     *
                     * In the foregoing description, the notation
                     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
                     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
                     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
                     * <i>expression</i> is negative, zero or positive.<p>
                     *
                     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
                     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
                     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
                     * if <tt>compare(y, x)</tt> throws an exception.)<p>
                     *
                     * The implementor must also ensure that the relation is transitive:
                     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
                     * <tt>compare(x, z)&gt;0</tt>.<p>
                     *
                     * Finally, the implementor must ensure that <tt>compare(x, y)==0</tt>
                     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
                     * <tt>z</tt>.<p>
                     *
                     * It is generally the case, but <i>not</i> strictly required that
                     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
                     * any comparator that violates this condition should clearly indicate
                     * this fact.  The recommended language is "Note: this comparator
                     * imposes orderings that are inconsistent with equals."
                     *
                     * @param o1 the first object to be compared.
                     * @param o2 the second object to be compared.
                     * @return a negative integer, zero, or a positive integer as the
                     * first argument is less than, equal to, or greater than the
                     * second.
                     * @throws NullPointerException if an argument is null and this
                     *                              comparator does not permit null arguments
                     * @throws ClassCastException   if the arguments' types prevent them from
                     *                              being compared by this comparator.
                     */
                    @Override
                    public int compare(Map.Entry<String, List<FormError>> o1, Map.Entry<String, List<FormError>> o2)
                    {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });

        errorMaps.addAll(formErrors.entrySet());

        for (Map.Entry<String, List<FormError>> entry : errorMaps)
        {
            list.addAll(entry.getValue());
        }

        return list;
    }

    public boolean hasErrors()
    {
        return !get().isEmpty();
    }

    /**
     * JSP method.
     * 
     * @param formName      The form error form item name..
     * @return          String error value for the given form item name.
     */
    public String getFormError(final String formName)
    {
        final List<FormError> errorList = formErrors.get(formName);

        if (errorList == null)
        {
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        for (final Iterator<FormError> it = errorList.iterator(); it.hasNext();)
        {
            final FormError formError = it.next();

            sb.append(formError.value);

            if (it.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * JSP method.
     *
     * @param formName      Form field name.
     * @param key           The key to obtain for.
     * @return              String style name.
     */
    public String getStyle(String formName, String key)
    {
        if (formErrors.containsKey(formName))
        {
            final List<FormError> errorlist = formErrors.get(formName);

            for (final FormError formError : errorlist)
            {
                if (formError.name.equals(key))
                {
                    return errorStyle;
                }
            }
        }
        return "";
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        final Set<Map.Entry<String, List<FormError>>> errorMaps =
                formErrors.entrySet();
        for (final Map.Entry<String, List<FormError>> entry : errorMaps)
        {
            if (entry.getValue().isEmpty())
            {
                continue;
            }

            final String formName = entry.getKey();
            sb.append(formName);
            sb.append("[");

            for (final FormError formError : entry.getValue())
            {
                sb.append(formError.value);
                sb.append(", ");
            }

            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("] ");
        }

        return sb.toString();
    }
}
