/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 12/8/11 - 1:10 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.validate;

import ca.nrc.cadc.AbstractUnitTest;
import org.junit.Test;

import static org.junit.Assert.*;


public class EnergyValidatorTest<V extends EnergyValidator> extends AbstractUnitTest<V>
{
    @Test
    @SuppressWarnings("unchecked")
    public void validate() throws Exception
    {
        final String utype = "Plane.energy.bounds.samples";

        setTestSubject((V) new EnergyValidator(utype, null, null){});
        assertNull("Should be null.", getTestSubject().validate());

        setTestSubject((V) new EnergyValidator(utype, 1, "m"){});
        assertEquals("Double values do not match", 1.0, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 1000, "cm"){});
        assertEquals("Double values do not match",  10.0, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 200000, "Hz"){});
        assertEquals("Double values do not match", 1498.9625,
                     getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 8805, "MHz"){});
        assertEquals("Double values do not match", 0.03404798409994322,
                     getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 0.0001, "eV"){});
        assertEquals("Double values do not match",  0.12398, getTestSubject().validate(), 1.0);

        setTestSubject((V) new EnergyValidator(utype, 0., "m"){});
        assertEquals("Double values do not match", 0.0, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 1., "m"){});
        assertEquals("Double values do not match", 1.0, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, .1, "m"){});
        assertEquals("Double values do not match",0.1, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator(utype, 4895, "m"){});
        assertEquals("Double values do not match", 4895.0, getTestSubject().validate(), 0.0);

        setTestSubject((V) new EnergyValidator("Plane.energy.bounds.width", 20000, "nm"){});
        assertEquals("Double values do not match", 2.0E-5, getTestSubject().validate(), 0.0);
    }
}
