/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.				(c) 2013.
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

/**
 * Simple class to hold the results from parsing a Target.
 */
public class TargetData {
    private String target;
    private Double ra;
    private Range<Double> raRange;
    private Double dec;
    private Range<Double> decRange;
    private Double radius;
    private String coordsys;
    private String service;
    private Integer time;
    private String objectName;
    private String objectType;
    private String morphologyType;

    /**
     * No-arg constructor.
     */
    public TargetData() {
    }

    /**
     * Full constructor.
     *
     * @param target         the target to resolve.
     * @param ra             the target RA.
     * @param raRange        the RA range.
     * @param dec            the target DEC.
     * @param decRange       the Dec range.
     * @param radius         the target radius.
     * @param coordsys       the target coordinate system.
     * @param service        the service that resolved the target.
     * @param time           the time to resolve the target.
     * @param objectName     long-form name of target from service.
     * @param objectType     Object type.
     * @param morphologyType Morph type from resolver
     */
    public TargetData(final String target,
                      final Double ra,
                      final Range<Double> raRange,
                      final Double dec,
                      final Range<Double> decRange,
                      final Double radius,
                      final String coordsys,
                      final String service,
                      final Integer time,
                      final String objectName,
                      final String objectType,
                      final String morphologyType) {
        this.target = target;
        this.ra = ra;
        this.raRange = raRange;
        this.dec = dec;
        this.decRange = decRange;
        this.radius = (radius == null) ? AbstractPositionParser.DEFAULT_RADIUS
            : radius;
        this.coordsys = coordsys;
        this.service = service;
        this.time = time;
        this.objectName = objectName;
        this.objectType = objectType;
        this.morphologyType = morphologyType;
    }

    /**
     * Creates a String of the R.A, Dec., and radius, space delimited.
     *
     * @return String of coordinates.
     */
    public String getCoordinates() {
        return String.valueOf(ra) + " " + dec + " " + radius;
    }

    /**
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return "TargetResult[target=" +
            target +
            ",ra=" +
            (ra == null ? "" : ra) +
            ",raRange=" +
            (raRange == null ? "" : raRange.getRange()) +
            ",dec=" +
            (dec == null ? "" : dec) +
            ",decRange=" +
            (decRange == null ? "" : decRange.getRange()) +
            ",radius=" +
            radius +
            ",coordsys=" +
            coordsys +
            ",service=" +
            service +
            ",time=" +
            time +
            ",objectName=" +
            objectName +
            ",objectType=" +
            objectType +
            ",morphologyType=" +
            morphologyType +
            "]";
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Double getRA() {
        return ra;
    }

    public void setRA(Double ra) {
        this.ra = ra;
    }

    public Range<Double> getRaRange() {
        return raRange;
    }

    public void setRaRange(Range<Double> raRange) {
        this.raRange = raRange;
    }

    public Double getDec() {
        return dec;
    }

    public void setDec(Double dec) {
        this.dec = dec;
    }

    public Range<Double> getDecRange() {
        return decRange;
    }

    public void setDecRange(Range<Double> decRange) {
        this.decRange = decRange;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public String getCoordsys() {
        return coordsys;
    }

    public void setCoordsys(String coordsys) {
        this.coordsys = coordsys;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getMorphologyType() {
        return morphologyType;
    }

    public void setMorphologyType(String morphologyType) {
        this.morphologyType = morphologyType;
    }

}
