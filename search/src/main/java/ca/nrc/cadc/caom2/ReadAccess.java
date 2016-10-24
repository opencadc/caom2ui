
package ca.nrc.cadc.caom2;

import java.util.Date;


/**
 *
 * @author pdowler
 */
public class ReadAccess 
{
    public Long assetID;

    public Long groupID;
    
    public Date lastModified;

    public Date getLastModified()
    {
        return lastModified;
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() 
                + "["
                + assetID + ","
                + groupID + ","
                + lastModified
                + "]";
    }
}
