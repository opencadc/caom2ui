/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                            (c) 2013.
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

package ca.nrc.cadc.search.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jburke
 */
public class RangeTest
{    
    public RangeTest() { }

    @Test
    public void testStringRange()
    {
        Range<String> range = new Range<String>("range", "value", "lower", "upper",
                                                Operand.EQUALS);
        
        assertNotNull(range);
        assertEquals("range", range.getRange());

        assertNotNull(range.getValue());
        assertEquals("value", range.getValue());

        assertNotNull(range.getLowerValue());
        assertEquals("lower", range.getLowerValue());

        assertNotNull(range.getUpperValue());
        assertEquals("upper", range.getUpperValue());
        
        assertEquals(Operand.EQUALS, range.getOperand());
    }

    @Test
    public void testDoubleRange()
    {
        Range<Double> range = new Range<Double>("range", 1.0, 2.0, 3.0,
                                                Operand.EQUALS);
        
        assertNotNull(range);
        assertEquals("range", range.getRange());
        
        assertNotNull(range.getValue());
        assertEquals(1.0, range.getValue(), 0.0);

        assertNotNull(range.getLowerValue());
        assertEquals(2.0, range.getLowerValue(), 0.0);

        assertNotNull(range.getUpperValue());
        assertEquals(3.0, range.getUpperValue(), 0.0);
        
        assertEquals(Operand.EQUALS, range.getOperand());
    }
    
}
