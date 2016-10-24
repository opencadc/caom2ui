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

import ca.nrc.cadc.search.parser.Operand;
import java.util.List;

/**
 * Interface for all from classes. Implementing classes must provide
 * methods to check the validity of a form, check if a form has data
 * that can be used in a search query, and return a Map of any errors
 * encountered validating the form.
 * 
 * @author jburke
 *
 */
public interface FormConstraint
{
    /**
     * Length of unique id string.
     */
    String FORM_NAME = "Form.name";
    
    /**
     * A form is valid if all form values have been successfully
     * validated. Validated form values can be null. A form
     * containing one or more null values, or all null values,
     * is considered valid.
     *
     * @param formErrors    The FormErrors instance.
     * @return boolean true if all form values are valid, false otherwise.
     */
    boolean isValid(final FormErrors formErrors);
    
    /**
     * A form has data when it has processable values. A form has
     * processable values when all of the form values are valid, 
     * and at least one of the form values is not null. It is up 
     * to the individual implementation to determine what form 
     * values are required for a form to have processable data.
     * 
     * @return boolean true if form contains processable values, false otherwise.
     */
    boolean hasData();
    
    /**
     * List containing errors encountered during form validation. The
     * List contains FormError objects, where the name is the form element name,
     * and the value is the error message displayed on the form.
     * 
     * @return List containing validation errors.
     */
    List<FormError> getErrorList();

    /**
     * Obtain the operand used.
     *
     * @return      Operand instance.  Defaults to EQUALS.
     * @see ca.nrc.cadc.search.parser.Operand
     */
    Operand getOperand();

    /**
     * Returns the utype for this form component.
     * 
     * @return utype of the form component.
     */
    String getUType();

    /**
     * If the form field value contains a unit that isn't the default unit
     * for the field, return that unit, else returns null.
     *
     * @return non-default form value unit, else null.
     */
    String getFormValueUnit();

    /**
     * Obtain the entered String form value.
     * 
     * @return  The String form value as entered by the user.  
     */
    String getFormValue();
}
