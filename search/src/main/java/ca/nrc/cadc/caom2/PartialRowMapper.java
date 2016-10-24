
package ca.nrc.cadc.caom2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author pdowler
 */
public interface PartialRowMapper extends RowMapper
{
    /**
     * 
     * @return the number of columns this RowMapper consumes
     */
    public int getColumnCount();
    
    /**
     * 
     * @param rs
     * @param row
     * @param offset the first column from which to get domain object state
     * @return the domain object constructed from the columns of the current row,
     *         with just the ID set if it is the same as the last object mapped
     * @throws java.sql.SQLException
     */
    public Object mapRow(ResultSet rs, int row, int offset)
        throws SQLException;
}
