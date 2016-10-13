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

/**
 * Simple class to hold the results from a Name Resolver query.
 *
 */
public class NameResolverData
{
    private static final String LF = "\n";
    
    public double ra;
    public double dec;
    public String target;
    public String coordsys;
    public String service;
    public String objectName;
    public String objectType;
    public String morphologyType;
    public int time;
    
    /**
     * Default constructor.
     *
     */
    public NameResolverData()
    {
        this.ra = 0.0;
        this.dec = 0.0;
        this.target = null;
        this.coordsys = null;
        this.service = null;
        this.objectName = null;
        this.objectType = null;
        this.morphologyType = null;
        this.time = 0;
    }
    
    /**
     * 
     * @return String representation of the object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("target=").append(target).append(LF);
        sb.append("service=").append(service).append(LF);
        sb.append("coordsys=").append(coordsys).append(LF);
        sb.append("ra=").append(ra).append(LF);
        sb.append("dec=").append(dec).append(LF);
        sb.append("oname=").append(objectName == null ? "" : objectName).append(LF);
        sb.append("otype=").append(objectType == null ? "" : objectType).append(LF);
        sb.append("mtype=").append(morphologyType == null ? "" : morphologyType).append(LF);
        sb.append("time=").append(time).append(LF);
        return sb.toString();
    }
    
}
