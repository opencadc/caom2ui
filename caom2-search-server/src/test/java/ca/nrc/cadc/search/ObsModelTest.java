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
 * 12/12/13 - 2:07 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;


import org.junit.Test;
import static org.junit.Assert.*;


public class ObsModelTest
{
    @Test
    public void getUTypeLabel() throws Exception
    {
        assertEquals("Plane.position.bounds is wrong.", "Target",
                     ObsModel.getUtypeLabel("Plane.position.bounds"));

        assertEquals("Plane.time.bounds is wrong.", "Observation Date",
                     ObsModel.getUtypeLabel("Plane.time.bounds"));

        assertEquals("Null should be empty string.", "",
                     ObsModel.getUtypeLabel(null));

        assertEquals("Calibration Level.", "Calibration Level",
                     ObsModel.getUtypeLabel("Plane.calibrationLevel"));

        assertEquals("Calibration Level.", "Calibration Level",
                     ObsModel.getUtypeLabel("Plane.calibrationLevel"));

        assertEquals("Resolving Power.", "energy resolving power",
                     ObsModel.getUtypeLabel("Plane.energy.resolvingPower"));
    }
}
