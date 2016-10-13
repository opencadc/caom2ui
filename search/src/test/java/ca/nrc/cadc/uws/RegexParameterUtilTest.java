/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
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
 * 9/6/13 - 1:30 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.uws;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import ca.nrc.cadc.AbstractUnitTest;


public class RegexParameterUtilTest extends AbstractUnitTest<RegexParameterUtil>
{
    @Test
    public void findParameterValue() throws Exception
    {
        final List<Parameter> params = new ArrayList<Parameter>();

        params.add(new Parameter("ONE", "1"));
        params.add(new Parameter("TWO", "2"));
        params.add(new Parameter("THREE", "3"));
        params.add(new Parameter("FOUR", "4"));
        params.add(new Parameter("FIVE", "5"));
        params.add(new Parameter("SIX", "6"));

        final String val1 = RegexParameterUtil.findParameterValue("(.*)WO$",
                                                                 params);
        assertEquals("Wrong value returned.", "2", val1);

        final String val2 = RegexParameterUtil.findParameterValue("(.*)EVEN$",
                                                                  params);
        assertNull("Should be null.", val2);
    }

    @Test
    public void findParameterValues() throws Exception
    {
        final List<Parameter> params = new ArrayList<Parameter>();

        params.add(new Parameter("ONE", "1"));
        params.add(new Parameter("TWO", "2"));
        params.add(new Parameter("THREE", "3"));
        params.add(new Parameter("FOUR", "4"));
        params.add(new Parameter("FIVE", "5"));
        params.add(new Parameter("SIX", "6"));

        final List<String> res1 =
                RegexParameterUtil.findParameterValues("(.*)E$", params);

        assertEquals("First item should be 1.", "1", res1.get(0));
        assertEquals("First item should be 3.", "3", res1.get(1));
        assertEquals("First item should be 5.", "5", res1.get(2));

        final List<String> res2 =
                RegexParameterUtil.findParameterValues("(.*)BOGUS$", params);

        assertTrue("Should be empty list.", res2.isEmpty());
    }
}
