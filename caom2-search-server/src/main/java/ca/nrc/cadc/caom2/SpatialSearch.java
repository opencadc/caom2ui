/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2007.                            (c) 2007.
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

package ca.nrc.cadc.caom2;

import ca.nrc.cadc.caom2.types.Shape;


/**
 * Simple spatial search template. This class can be used to specify a circular
 * (radius is not null) or pointed (radius is null) spatial search. This search
 * implicitly puts a constraint on values of the extent described by the
 * observation.spatial object. This search implicitly puts a constraint on values
 * of the extent described by the observation.spatial object.
 *
 * @author $Author: jburke $
 * @version $Revision: 165 $
 */
public class SpatialSearch extends AbstractTemplate
{
    private static final long serialVersionUID = 200602221500L;

    private Shape position;

    public SpatialSearch()
    {
        super();
    }


    /**
     * @param name
     * @param pos
     */
    public SpatialSearch(final String name, final Shape pos)
    {
        super(name);
        this.position = pos;
    }


    public Shape getPosition()
    {
        return position;
    }

    public String toString()
    {
        return "SpatialSearch[" + getName() + "," + position + "]";
    }
}

// end of SpatialSearch.java

