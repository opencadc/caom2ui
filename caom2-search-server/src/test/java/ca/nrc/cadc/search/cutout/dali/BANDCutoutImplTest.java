/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                         (c) 2013.
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
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.cutout.dali;

import ca.nrc.cadc.AbstractUnitTest;

import ca.nrc.cadc.caom2.IntervalSearch;

import org.junit.Test;
import static org.junit.Assert.*;


public class BANDCutoutImplTest extends AbstractUnitTest<BANDCutoutImpl>
{
    protected static final double ONE_ARC_MIN_AS_DEG = 0.016666666666666666;

    @Test
    public void formatNullCutoutString() throws Exception
    {
        setTestSubject(new BANDCutoutImpl(null));

        assertEquals("Should be empty string", "", getTestSubject().format());
    }

    @Test
    public void formatSpectralIntervalCutoutString() throws Exception
    {
        setTestSubject(
                new BANDCutoutImpl( new IntervalSearch("TEST Interval", 88.1d,
                                                     88.9d, "Hz")));

        assertEquals("Should be 88.1 88.9",
                     "88.1 88.9",
                     getTestSubject().format());
    }
}
