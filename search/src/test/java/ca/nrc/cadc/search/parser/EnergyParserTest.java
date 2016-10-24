/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits r√©serv√©s
*
*  NRC disclaims any warranties,        Le CNRC d√©nie toute garantie
*  expressed, implied, or               √©nonc√©e, implicite ou l√©gale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           √™tre tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou g√©n√©ral,
*  arising from the use of the          accessoire ou fortuit, r√©sultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        √™tre utilis√©s pour approuver ou
*  products derived from this           promouvoir les produits d√©riv√©s
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  pr√©alable et particuli√®re
*                                       par √©crit.
*
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.search.parser;

import org.apache.log4j.Level;
import ca.nrc.cadc.search.parser.exception.EnergyParserException;
import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jburke
 */
public class EnergyParserTest
{
    private static Logger log = Logger.getLogger(EnergyParserTest.class);
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.search.parser", Level.INFO);
    }

    public static final String SPACE = " ";
    public static final Double VALUE = 1.0;
    public static final String UNIT = "um";
    public static final Double TOLERANCE = 2.0;
    
    public EnergyParserTest() { }
    
    @Test
    public void testNullString()
    {
        log.debug("nullString()...");
        try
        {
            new EnergyParser(null);
            fail("Failed to throw EnergyParserException parsing null string");
        }
        catch (EnergyParserException e) { }
        catch (Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("nullString() passed.");
    }

    @Test
    public void testName()
    {
        log.debug("testName()...");
        try
        {
            log.info("not yet implemented.");
        }
        catch (Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testName() passed.");
    }

    @Test
    public void value() throws Exception
    {
        log.debug("value()...");

        final String query = VALUE.toString();
        final EnergyParser parser = new EnergyParser(query);
        final Numeric energy = parser.getResult();

        assertEquals(VALUE, energy.value);
        assertNull(energy.unit);
        assertNull(energy.tolerance);

        log.info("value() passed.");
    }

    @Test
    public void valueUnit() throws Exception
    {
        final String query = VALUE + UNIT;
        final EnergyParser parser = new EnergyParser(query);
        final Numeric energy = parser.getResult();

        assertEquals(VALUE, energy.value);
        assertEquals(UNIT, energy.unit);
        assertNull(energy.tolerance);

        final String query2 = VALUE + SPACE + UNIT;
        final EnergyParser parser2 = new EnergyParser(query2);
        final Numeric energy2 = parser2.getResult();

        assertEquals(VALUE, energy2.value);
        assertEquals(UNIT, energy2.unit);
        assertNull(energy2.tolerance);
    }

    @Test
    public void testValueUnitTolerance()
    {
        log.debug("testValueUnitTolerance()...");
        try
        {
            final String query = VALUE + SPACE + UNIT + SPACE + TOLERANCE;
            final EnergyParser parser = new EnergyParser(query);
            final Numeric energy = parser.getResult();

            assertEquals(VALUE, energy.value);
            assertEquals(UNIT, energy.unit);
            assertEquals(TOLERANCE, energy.tolerance);

            final String query2 = VALUE + UNIT + SPACE + TOLERANCE;
            final EnergyParser parser2 = new EnergyParser(query2);
            final Numeric energy2 = parser2.getResult();

            assertEquals(VALUE, energy2.value);
            assertEquals(UNIT, energy2.unit);
            assertEquals(TOLERANCE, energy2.tolerance);
        }
        catch (Throwable t)
        {
            log.error(t);
            fail(t.getMessage());
        }
        log.info("testValueUnitTolerance() passed.");
    }

}