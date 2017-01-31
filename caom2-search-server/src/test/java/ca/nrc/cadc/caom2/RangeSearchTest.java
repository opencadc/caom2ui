/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2014.                         (c) 2014.
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
 * 28/05/14 - 9:43 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;


import org.junit.Test;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.Range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class RangeSearchTest extends AbstractUnitTest<RangeSearch<?>>
{
    @Test
    public void constructor() throws Exception
    {
        try
        {
            new RangeSearch<String>("NAME", null, null);
            fail("Should throw Exception");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Wrong exception message.", "null RA range or value",
                         e.getMessage());
        }

        try
        {
            new RangeSearch<Double>("NAME",
                                    new Range<Double>("1.0..4.0", null, 1.0,
                                                      4.0, Operand.RANGE),
                                    null
            );
            fail("Should throw Exception");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Wrong exception message.", "null Dec range or value",
                         e.getMessage());
        }

        try
        {
            new RangeSearch<Double>("NAME",
                                    new Range<Double>("5.0..4.0", null, 5.0,
                                                      4.0, Operand.RANGE),
                                    new Range<Double>("1.0..4.0", null, 1.0,
                                                      4.0,
                                                      Operand.RANGE)
            );
            fail("Should throw Exception");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Wrong exception message.",
                         "Lower > Upper in lower range.", e.getMessage());
        }

        try
        {
            new RangeSearch<Double>("NAME",
                                    new Range<Double>("1.0..4.0", null, 1.0,
                                                      4.0, Operand.RANGE),
                                    new Range<Double>("7.0..4.0", null, 7.0,
                                                      4.0,
                                                      Operand.RANGE)
            );
            fail("Should throw Exception");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Wrong exception message.",
                         "Lower > Upper in upper range.", e.getMessage());
        }

        // Should be good.
        new RangeSearch<Double>("NAME", new Range<Double>("1.0..4.0", null, 1.0,
                                                          4.0, Operand.RANGE),
                                new Range<Double>("3.5..4.0", null, 3.5, 4.0,
                                                  Operand.RANGE)
        );
    }
}
