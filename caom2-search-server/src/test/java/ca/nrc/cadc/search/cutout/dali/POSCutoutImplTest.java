/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                         (c) 2020.
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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.cutout.dali;

import ca.nrc.cadc.AbstractUnitTest;

import ca.nrc.cadc.caom2.types.Circle;
import ca.nrc.cadc.caom2.types.Point;
import ca.nrc.cadc.dali.Shape;
import ca.nrc.cadc.dali.util.ShapeFormat;
import ca.nrc.cadc.search.cutout.Cutout;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.SpatialSearch;

import org.junit.Test;
import static org.junit.Assert.*;


public class POSCutoutImplTest extends AbstractUnitTest<POSCutoutImpl>
{
    protected static final double ONE_ARC_MIN_AS_DEG = 0.016666666666666666;

    @Test
    public void formatNullCutoutString() throws Exception
    {
        setTestSubject(new POSCutoutImpl(null));

        assertEquals("Should be empty string", "", getTestSubject().format());
    }

    @Test
    public void formatCircleCutoutString() throws Exception
    {
        setTestSubject(
                new POSCutoutImpl(
                        new SpatialSearch("TEST circle",
                                          new Circle(new Point(0.4d, 0.5d), 0.5d))));

        assertEquals("Should be circle 0.4 0.5 0.5",
                     "circle 0.4 0.5 0.5",
                     getTestSubject().format());
    }
}
