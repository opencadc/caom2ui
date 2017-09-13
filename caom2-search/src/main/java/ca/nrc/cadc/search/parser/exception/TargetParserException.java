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
package ca.nrc.cadc.search.parser.exception;

/**
 *
 * @author jburke
 */
public class TargetParserException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Reason for the exception being thrown.
     * GENERAL - The TargetParser
     * NAMERESOLVER_CLIENT - The Name Resolver web client was unable to parse
     *                       the Name Resolver web service query results.
     * NAMERESOLVER_WEB_SERVICE - The Name Resolver web service is not responding
     *                            or is unable to process a query.
     * NAMERESOLVER_TARGET_NOT_FOUND - The Name Resolver web service was unable
     *                                 to resolve the target.
     * NULL_RESOLVER_AND_TARGET_NOT_FOUND - The given Name Resolver was either
     *                                      empty or null, and the target was
     *                                      not resolved.
     *
     */
    public enum ExceptionType
    {
        GENERAL,
        NAMERESOLVER_CLIENT,
        NAMERESOLVER_WEB_SERVICE,
        NAMERESOLVER_TARGET_NOT_FOUND,
        RESOLVER_NOT_SPECIFIED_AND_TARGET_NOT_FOUND
    }

    private ExceptionType type;

    public TargetParserException(String message)
    {
        super(message);
        this.type = ExceptionType.GENERAL;
    }

    public TargetParserException(ExceptionType t)
    {
        this.type = t;
    }

    public TargetParserException(String message, ExceptionType t)
    {
        super(message);
        this.type = t;
    }

    /**
     * @param cause
     */
    public TargetParserException(Throwable cause, ExceptionType t)
    {
        super(cause);
        this.type = t;
    }

    /**
     * @param message
     * @param cause
     */
    public TargetParserException(String message, Throwable cause, ExceptionType t)
    {
        super(message, cause);
        this.type = t;
    }

    public ExceptionType getExceptionType()
    {
        return this.type;
    }
    
}
