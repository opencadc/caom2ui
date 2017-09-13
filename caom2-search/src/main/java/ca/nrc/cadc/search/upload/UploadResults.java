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

package ca.nrc.cadc.search.upload;

public class UploadResults
{
    public static final String UPLOAD_RESOLVER = "uploadResolver";
    public static final String UPLOAD_ROW_COUNT = "uploadRowCount";
    public static final String UPLOAD_ERROR_COUNT = "uploadErrorCount";
//    public static final String UPLOAD_VOTABLE_URL = "uploadFileVOTableUrl";

    private String resolver;
    private int rowCount;
    private int errorCount;


    public UploadResults(final String resolver, final int rowCount,
                         final int errorCount)
    {
        this.resolver = resolver;
        this.rowCount = rowCount;
        this.errorCount = errorCount;
    }


    public String getResolver()
    {
        return resolver;
    }

    public int getRowCount()
    {
        return rowCount;
    }

    public int incrementRowCount()
    {
        return rowCount++;
    }

    public int getErrorCount()
    {
        return errorCount;
    }

    public int incrementErrorCount()
    {
        return errorCount++;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("UploadResults[");
        sb.append("resolver=");
        sb.append(resolver);
        sb.append(", rowCount=");
        sb.append(rowCount);
        sb.append(", errorCount=");
        sb.append(errorCount);
        sb.append("]");
        return sb.toString();
    }
    
}
