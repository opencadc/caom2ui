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

import java.util.List;


import org.apache.log4j.Logger;

import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.TextSearch;
import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;


/**
 * Class to represent an Interva1 form component.
 *
 * @author jburke
 */
public class Text extends AbstractFormConstraint implements SearchableFormConstraint {

    private static Logger log = Logger.getLogger(Text.class);

    // Constants used to construct name for form elements.
    public static final String NAME = "@Text";
    public static final String VALUE = "";

    private boolean ignoreCase;


    /**
     * Constructor to populate necessary fields.  Useful for testing and just
     * to re-use this Text form constraint.
     *
     * @param utype      The UType unique name.
     * @param value      The value.
     * @param ignoreCase Whether to ignore case or not.
     */
    public Text(final String utype, final String value, final boolean ignoreCase) {
        super(utype);
        setFormValue(value);
        setIgnoreCase(ignoreCase);
    }

    /**
     * Text constructor instantiates a new instance with the given parameters.
     *
     * @param job   The UWS Job.
     * @param utype The utype of the form.
     */
    public Text(final Job job, final String utype) {
        super(utype);

        for (final Parameter parameter : job.getParameterList()) {
            if (parameter.getName().equals(utype + VALUE)) {
                setFormValue(parameter.getValue());
            }
        }

        /*
         * This is being created as part of a search, so the case is always
         * ignored.
         *
         * Story 895
         * jenkinsd 2012.05.15
         */
        setIgnoreCase(true);
    }

    // Create a TextSearch to SearchTemplates.
    public SearchTemplate buildSearch(List<FormError> errorList) {
        SearchTemplate template = null;

        try {
            template = new TextSearch(this.getUType(), this.getFormValue(),
                                      ObsModel.isWildcardUtype(this.getUType()),
                                      this.isIgnoreCase());
        } catch (IllegalArgumentException e) {
            errorList.add(new FormError(Text.NAME, e.getMessage()));
            log.debug("Invalid Text parameters: " + e.getMessage() + " "
                              + this.toString());
        }

        return template;
    }

    /**
     * Text is valid if the Text value has been successfully validated.
     *
     * @return boolean true if form value is valid, false otherwise.
     */
    public boolean isValid(final FormErrors formErrors) {
        final String utype = getUType();

        if (!ObsModel.isTextUtype(utype)) {
            addError(new FormError(utype + VALUE, "Invalid utype " + utype));
        }

        formErrors.set(utype + NAME, getErrorList());
        return getErrorList().isEmpty();
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    protected void setIgnoreCase(final boolean igCase) {
        this.ignoreCase = igCase;
    }

    /**
     * Text has processable data if the Text value is not null or empty.
     *
     * @return boolean true if form contains a processable value, false otherwise.
     */
    public boolean hasData() {
        return StringUtil.hasText(getFormValue());
    }


    /**
     * @return String representation of the Interval1 form.
     */
    @Override
    public String toString() {
        return "Text[" + getUType() + ", " + getFormValue() + "]";
    }
}
