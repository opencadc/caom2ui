
package ca.nrc.cadc.astro;

/**
 * Simple unit conversion interface.
 *
 * @version $Revision$
 * @author $Author: pdowler $
 */
public interface UnitConverter
{
	/**
	 * Get the list of units known to this converter. 
	 * @return
	 */
	String[] getSupportedUnits();
	
	/**
	 * Convert a value expressed in one of the known units to the core unit.
	 * 
	 * @param value
	 * @param units
	 * @return value expressed in core units
	 * @throws IllegalArgumentException if the units are unknown
	 */
	double convert(double value, String units) throws IllegalArgumentException;
}

// end of UnitConverter.java

