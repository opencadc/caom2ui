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
 * 12/8/11 - 1:25 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.astro;

import org.junit.Test;
import static org.junit.Assert.*;


public class EnergyUnitConverterTest
{
    @Test
    public void convert() throws Exception
    {
        final EnergyUnitConverter testSubject = new EnergyUnitConverter();

        assertEquals("Nanometres don't match.", 0.9 * 1.0e-9,
                     testSubject.convert(0.9, "nm"), 0.0);

        assertEquals("Metres don't match.", 0.9, testSubject.convert(0.9, "M"),
                     0.0);

        assertEquals("Angstroms don't match.", 0.256 * 1.0e-10,
                     testSubject.convert(0.256, "A"), 0.0);

        assertEquals("Kilohertz don't match.", 2.9979250e8 / (4.118 * 1.0e3),
                     testSubject.convert(4.118, "kHz"), 0.0);

        assertEquals("Kilohertz don't match.", 2.9979250e8 / (1.668 * 1.0e3),
                     testSubject.convert(1.668, "KHZ"), 0.0);

        assertEquals("GeV don't match.",
                     (2.9979250e8 * 6.62620e-27)
                     / (1.602192e-12 * 0.0241 * 1.0e9),
                     testSubject.convert(0.0241, "gev"), 0.0);

        assertEquals("MeV don't match.",
                     (2.9979250e8 * 6.62620e-27)
                     / (1.602192e-12 * 1.99 * 1.0e6),
                     testSubject.convert(1.99, "MEv"), 0.0);
    }
}
