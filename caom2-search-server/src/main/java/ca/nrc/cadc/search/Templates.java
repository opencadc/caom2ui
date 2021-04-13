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

package ca.nrc.cadc.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.Top;
import ca.nrc.cadc.search.form.FormError;
import ca.nrc.cadc.search.form.FormErrors;
import ca.nrc.cadc.search.form.SearchableFormConstraint;


/**
 * Class to build an array of SearchTemplates from a list of Form
 * objects.
 *
 * @author jburke
 */
public class Templates {

    private static final Logger LOGGER = Logger.getLogger(Templates.class);
    private static final int QUERY_LIMIT = 20000;

    public final List<FormError> errorList = new ArrayList<>();
    private final List<SearchTemplate> searchTemplates = new ArrayList<>();


    /**
     * Templates constructor. Takes the list of Forms and attempts
     * to create a SearchTemplate for each Form. Any exceptions
     * thrown creating the template are stored in a Map with the
     * Form attribute as the Map key and the exception message as
     * the Map value.
     * <p>
     * If a ScalarTemplate is not created for an Enumerated Form
     * with a Collection attribute, then a ScalarTemplate is created
     * using the default Collections in the List cadcList.
     * <p>
     * It is expected that the forms in the list have been validated.
     *
     * @param formConstraints List of forms.
     */
    public Templates(final List<SearchableFormConstraint> formConstraints) {
        for (final SearchableFormConstraint formConstraint : formConstraints) {
            final SearchTemplate template =
                    formConstraint.buildSearch(errorList);
            if (template != null) {
                searchTemplates.add(template);
                LOGGER.debug(template);
            } else {
                LOGGER.debug("SearchTemplate is null");
            }
        }

        // Add a top filter to limit search result maximum.
        searchTemplates.add(new Top(QUERY_LIMIT));
    }

    /**
     * Tests if the SearchTemplates are valid.
     * The SearchTemplates are valid if the Templates list
     * searchTemplates has members and if the error map erroMap is empty.
     *
     * @param formErrors The Form Errors.
     * @return boolean true if errorMap is empty.
     */
    public boolean isValid(final FormErrors formErrors) {
        for (final FormError formError : errorList) {
            formErrors.set(formError.name, formError);
        }

        return errorList.isEmpty();
    }

    /**
     * Returns an array of SearchTemplates.
     *
     * @return Array of SearchTemplates.
     */
    public List<SearchTemplate> getSearchTemplates() {
        return searchTemplates;
    }

    /**
     * Obtain only those SearchTemplates whose class matches the given one.
     *
     * @param searchTemplateClass The Class to match.
     * @param <T>                 The SearchTemplate implementation.
     * @return List of T instances, or empty List.
     * Never null.
     */
    @SuppressWarnings("unchecked")
    public <T extends SearchTemplate> List<T> getSearchTemplates(
            final Class<T> searchTemplateClass) {
        final List<T> searchTemplateClasses = new ArrayList<>();

        for (final SearchTemplate searchTemplate : getSearchTemplates()) {
            if (searchTemplate.getClass() == searchTemplateClass) {
                searchTemplateClasses.add((T) searchTemplate);
            }
        }

        return searchTemplateClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final Templates templates = (Templates) o;

        return (Objects.equals(errorList, templates.errorList)) && (Objects.equals(searchTemplates,
                                                                                   templates.searchTemplates));
    }

    @Override
    public int hashCode() {
        return 31 * errorList.hashCode() + searchTemplates.hashCode();
    }
}
