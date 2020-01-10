/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2019.                            (c) 2019.
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

import ca.nrc.cadc.caom2.IntervalSearch;
import ca.nrc.cadc.caom2.SearchTemplate;

import java.util.List;


import org.apache.log4j.Logger;

import ca.nrc.cadc.search.parser.EnergyParser;
import ca.nrc.cadc.search.validate.EnergyValidator;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;


/**
 * Class to represent an Energy interval form component.
 *
 * @author jburke
 */
public class Energy extends AbstractNumericFormConstraint implements SearchableFormConstraint {

    private static Logger log = Logger.getLogger(Energy.class);

    // Energy values are normalized to meters.
    public static String NORMALIZED_UNITS = "m";
    public static String NORMALIZED_FREQ_UNITS = "Hz";

    // Constants used to construct name for form elements.
    public static final String NAME = "@Energy";
    public static final String VALUE = "@Energy.value";

    /**
     * Energy constructor instantiates a new instance with the given parameters.
     *
     * @param job   The servlet request.
     * @param utype The utype of the form.
     */
    public Energy(final Job job, final String utype) {
        super(utype);
        for (final Parameter parameter : job.getParameterList()) {
            if (parameter.getName().equals(utype + VALUE)) {
                setFormValue(parameter.getValue());
            }
        }
        init();
    }

    // Create and add a IntervalSearch to SearchTemplates.
    public SearchTemplate buildSearch(List<FormError> errorList) {
        SearchTemplate template = null;

        try {
            template = new IntervalSearch(this.getUType(),
                                          ((this.getLowerNumber() == null)
                                                  ? null
                                                  :
                                                  this.getLowerNumber().doubleValue()),
                                          ((this.getUpperNumber() == null)
                                                  ? null
                                                  :
                                                  this.getUpperNumber().doubleValue()),
                                          this.getUnit());
        } catch (IllegalArgumentException e) {
            errorList.add(new FormError(Energy.NAME, e.getMessage()));
            log.debug("Invalid Energy parameters: " + e.getMessage() + " " +
                              this.toString());
        }

        return template;
    }

    public Energy(final String value, final String utype) {
        super(utype);
        setFormValue(value);
        init();
    }

    /**
     * Initialize what needs to be initialized.
     */
    protected void init() {
        unit = null;
    }

    /**
     * Determines whether meter should be used as the unit based on the utype.
     *
     * @param utype The UType to check.
     * @return true if we should use metric, false otherwise.
     */
    public static boolean useMeter(final String utype) {
        // Story 888 - For frequency matches, convert the value
        // to the Hz value for all energy but the Spectral
        // Coverage.  Otherwise, back to metres.
        // jenkinsd 2012.01.31
        //
        // Update
        // Story 1502
        // Adding Rest Frequency follows the same logic.
        return utype.equals("Plane.energy.bounds.samples") ||
                utype.equals("Plane.energy.restwav") ||
                utype.equals("Char.SpectralAxis.Coverage.Bounds.Limits");
    }

    /**
     * Text is valid if the Energy valueFrom and valueTo have been successfully validated.
     *
     * @return boolean true if form value is valid, false otherwise.
     */
    public boolean isValid(final FormErrors formErrors) {
        return new RangeValidation(this, new EnergyParser(),
                                   new EnergyValidator(), NORMALIZED_UNITS,
                                   VALUE).isValid(formErrors);
    }

    public String getUnit() {
        return unit;
    }

    /**
     * Text has processable data if the Time valueFrom is not null.
     *
     * @return boolean true if form contains a processable value, false otherwise.
     */
    @Override
    public boolean hasData() {
        return (getLowerNumber() != null) || (getUpperNumber() != null);
    }

    @Override
    public String resolveUnit(final String forUnit) {
        final String resolvedUnit;

        if (useMeter(getUType())) {
            resolvedUnit = Energy.NORMALIZED_UNITS;
        } else {
            resolvedUnit = forUnit.matches("^.*([Hh]+[Zz]+)")
                    ? Energy.NORMALIZED_FREQ_UNITS
                    : Energy.NORMALIZED_UNITS;
        }

        return resolvedUnit;
    }

    /**
     * @return String representation of the Time form.
     */
    @Override
    public String toString() {
        return "Energy[" + getUType() + ", " + getLowerNumber() + ", "
                + getUpperNumber() + ", " + getUnit() + "]";
    }
}
