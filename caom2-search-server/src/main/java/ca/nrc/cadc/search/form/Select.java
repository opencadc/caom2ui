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


/**
 * Class to represent a drop down or selection list.
 *
 * @author jburke
 *
 */
public class Select extends AbstractScalarFormConstraint
{
    // Constants used to construct name for form elements.
    public static final String NAME = "@Select";
    static final String UTYPE = "@Select.utype";


    /**
     * Select constructor instantiates a new instance with the given parameters.
     *
     * @param job               The UWS Job.
     * @param utype             The utype of the form.
     * @param selectedValues    The selected values from the user.
     * @param hidden            Whether this item is hidden on the Form.
     */
    public Select(final Job job, final String utype, final String[] selectedValues, final boolean hidden)
    {
        super(job, utype, selectedValues, hidden);
    }

    /**
     * @return String representation of the form.
     */
    @Override
    public String toString()
    {
        return super.toString("Selected");
    }

    protected String getName()
    {
        return NAME;
    }
}
