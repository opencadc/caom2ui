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

package ca.nrc.cadc.search.validate;

import ca.nrc.cadc.search.ObsModel;
import ca.nrc.cadc.util.StringUtil;


/**
 * Class to validate Metric values.
 *
 * @author jburke
 *
 */
public class PropertyValidator
{
    private final boolean isNumeric;


    public PropertyValidator(final String utype)
    {
        isNumeric = ObsModel.isNumberUtype(utype);
    }


	/**
	 * Validates the value and returns a Double of the value.
     * If the value is null, or an empty String, then null
     * is returned. If a Double can be created from the value,
     * the Double is returned. Else a ValidationException is thrown.
	 *
	 * @param value The value to validate.
	 * @throws ValidationException if the value cannot be validated.
	 * @return Double of the validated value.
	 */
    public Double validate(final String value) throws ValidationException
    {
        if (!StringUtil.hasLength(value))
        {
            return null;
        }

        if (isNumeric)
        {
            try
            {
                return new Double(value);
            }
            catch (NumberFormatException e)
            {
                throw new ValidationException("Invalid Metric " + value);
            }
        }

        return Double.NaN;
    }
}
