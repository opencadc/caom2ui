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
 *
 * @author jenkinsd
 * 12/15/11 - 1:46 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ca.nrc.cadc.caom2.IsNull;
import ca.nrc.cadc.caom2.Or;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.TextSearch;
import ca.nrc.cadc.search.util.ParameterUtil;
import ca.nrc.cadc.util.ArrayUtil;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.Job;


abstract class AbstractScalarFormConstraint extends AbstractFormConstraint implements SearchableFormConstraint {

    private static final Logger LOGGER = Logger.getLogger(AbstractScalarFormConstraint.class);

    // Array of selected values from the drop down list.
    private String[] selectedValues;

    // List of (not currently) validated selected values from the drop down list.
    private List<String> selected;

    // TODO: change this attribute to private
    // boolean true if component is displayed as a hidden input.
    public boolean hidden;

    abstract String getName();

    AbstractScalarFormConstraint(final Job job, final String utype, final String[] selectedValues,
                                 final boolean hidden) {
        super(utype);
        this.hidden = hidden;

        if ((selectedValues == null) && (job != null)) {
            final List<String> list = new ParameterUtil().getValues(utype + this.getName(),
                                                                    job.getParameterList());
            this.selectedValues = list.toArray(new String[0]);
        } else {
            this.selectedValues = selectedValues;
        }
    }

    // Create a TextSearch or "Or" to SearchTemplates.
    private SearchTemplate buildScalarSearch(final List<String> list, final String tableColumn,
                                             final List<FormError> errorList) {
        if (list.size() == 1) {
            try {
                final String value = list.get(0);

                if (value.equals("null")) {
                    return new IsNull(tableColumn);
                } else {
                    return new TextSearch(tableColumn, value, value);
                }
            } catch (IllegalArgumentException e) {
                errorList.add(new FormError(this.getUType(), e.getMessage()));
                LOGGER.debug("Invalid parameters: " + e.getMessage() + " " + this);

                return null;
            }
        } else {
            return new Or(list.stream().map(val -> {
                if (val == null || val.trim().equalsIgnoreCase("null")) {
                    return new IsNull(tableColumn);
                } else {
                    return new TextSearch(tableColumn, val);
                }
            }).collect(Collectors.toList()));
        }
    }

    // Create a ScalarSearch to SearchTemplates.
    public SearchTemplate buildSearch(List<FormError> errorList) {
        final List<String> selected = this.getSelected();

        if ((selected != null) && !selected.isEmpty()) {
            return buildScalarSearch(selected, this.getUType(), errorList);
        } else {
            return null;
        }
    }

    /**
     * Enumerated is considered to have processable data
     * if the list of selected values contains a validated values.
     *
     * @return boolean true if form contains processable values, false otherwise.
     */
    public boolean hasData() {
        return getSelected() != null;
    }

    public List<String> getSelected() {
        return selected;
    }

    protected void setSelected(final List<String> selected) {
        this.selected = selected;
    }

    void resetSelectedValues() {
        this.selectedValues = new String[0];
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * Validates all selected values in the drop down list against the
     * valid values for this utype.
     *
     * @return boolean true if all form values are valid, false otherwise.
     */
    public boolean isValid(FormErrors formErrors) {
        if (ArrayUtil.isEmpty(selectedValues)) {
            return false;
        }

        for (final String selectedValue : selectedValues) {
            if (StringUtil.hasLength(selectedValue)) {
                if (this.getSelected() == null) {
                    final List<String> list = new ArrayList<>();
                    this.setSelected(list);
                }

                this.getSelected().add(selectedValue);
            }
        }

        // Not currently validating select values.
        formErrors.set(getUType() + this.getName(), getErrorList());
        return getErrorList().isEmpty();
    }

    /*
     * @return String representation of the form.
     */
    protected String toString(final String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("[ ");
        sb.append(getUType());
        sb.append(", ");

        if (selectedValues != null) {
            for (final String select : selectedValues) {
                if (StringUtil.hasText(select)) {
                    sb.append(select);
                    sb.append(" ");
                }
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    String[] getSelectedValues() {
        return selectedValues;
    }
}
