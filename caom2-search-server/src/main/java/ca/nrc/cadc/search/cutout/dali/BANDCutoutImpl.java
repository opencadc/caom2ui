/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                            (c) 2020.
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

import ca.nrc.cadc.dali.DoubleInterval;
import ca.nrc.cadc.dali.util.DoubleIntervalFormat;
import ca.nrc.cadc.search.cutout.Cutout;
import ca.nrc.cadc.caom2.IntervalSearch;

import ca.nrc.cadc.util.StringUtil;


/**
 * DALI representation of Interval cutout (BAND)
 */
public class BANDCutoutImpl implements Cutout
{
    private final DoubleInterval bandCutout;
    private final IntervalSearch spectralSearch;
    private DoubleIntervalFormat dif = new DoubleIntervalFormat();

    /**
     * ctor
     * @param _spectralSearch used to generate cutout
     */
    public BANDCutoutImpl( final IntervalSearch _spectralSearch )
    {
        this.spectralSearch = _spectralSearch;
        this.bandCutout = createCutout();
    }
    
    /**
     * Format this cutout as a String representation.
     *
     * @return String value.  Should never be null.
     */
    @Override
    public final String format()
    {

        return dif.format(bandCutout);
    }

    /**
     * Obtain the SpectralInterval component to this cutout, if any.
     *
     * @return SpectralInterval instance, or null if none.
     */
    DoubleInterval createCutout()
    {
        final DoubleInterval spectralInterval;

        if (this.spectralSearch == null)
        {
            spectralInterval = null;
        }
        else
        {
            // This is assuming units for the interval have
            // previously been converted to m
            spectralInterval = new DoubleInterval(
                this.spectralSearch.getLower(),
                this.spectralSearch.getUpper());
        }

        return spectralInterval;
    }

}
