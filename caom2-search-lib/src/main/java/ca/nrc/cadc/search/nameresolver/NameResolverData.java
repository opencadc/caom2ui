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

package ca.nrc.cadc.search.nameresolver;


import ca.nrc.cadc.astro.CoordUtil;
import ca.nrc.cadc.search.nameresolver.exception.ClientException;
import ca.nrc.cadc.stc.CoordPair;

import java.util.Properties;

/**
 * Simple class to hold the results from a Name Resolver query.
 */
public class NameResolverData {

    private static final String LF = "\n";

    private double ra;
    private double dec;
    private String target;
    private String coordsys;
    private String service;
    private String objectName;
    private String objectType;
    private String morphologyType;
    private int time;


    public NameResolverData(final Properties properties) throws ClientException {
        this.ra = CoordUtil.raToDegrees(getProperty(properties, NameResolverDataKey.RA));
        this.dec = CoordUtil.decToDegrees(getProperty(properties, NameResolverDataKey.DEC));
        this.service = getProperty(properties, NameResolverDataKey.SERVICE);
        this.coordsys = getProperty(properties, NameResolverDataKey.COORDSYS);
        this.time = Integer.parseInt(getProperty(properties, NameResolverDataKey.TIME));
        this.target = getProperty(properties, NameResolverDataKey.TARGET);

        this.objectName = getProperty(properties, NameResolverDataKey.ONAME);
        this.objectType = getProperty(properties, NameResolverDataKey.OTYPE);
        this.morphologyType = getProperty(properties, NameResolverDataKey.MTYPE);
    }

    private String getProperty(final Properties properties, final NameResolverDataKey nameResolverDataKey)
            throws ClientException {
        if (properties.containsKey(nameResolverDataKey.getKeyLabel())) {
            return properties.getProperty(nameResolverDataKey.getKeyLabel());
        } else if (nameResolverDataKey.isRequired()) {
            throw new ClientException(nameResolverDataKey.getKeyLabel() + " not found in query results.");
        } else {
            return null;
        }
    }


    public NameResolverData(double ra, double dec, String target, String coordsys, String service,
                            String objectName, String objectType, String morphologyType, int time) {
        this.ra = ra;
        this.dec = dec;
        this.target = target;
        this.coordsys = coordsys;
        this.service = service;
        this.objectName = objectName;
        this.objectType = objectType;
        this.morphologyType = morphologyType;
        this.time = time;
    }

    public double getRa() {
        return ra;
    }

    public double getDec() {
        return dec;
    }

    public String getTarget() {
        return target;
    }

    public String getCoordsys() {
        return coordsys;
    }

    public String getService() {
        return service;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getMorphologyType() {
        return morphologyType;
    }

    public int getTime() {
        return time;
    }

    /**
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return "target=" + target + LF +
                "service=" + service + LF +
                "coordsys=" + coordsys + LF +
                "ra=" + ra + LF +
                "dec=" + dec + LF +
                "oname=" + (objectName == null ? "" : objectName) + LF +
                "otype=" + (objectType == null ? "" : objectType) + LF +
                "mtype=" + (morphologyType == null ? "" : morphologyType) + LF +
                "time=" + time + LF;
    }
}
