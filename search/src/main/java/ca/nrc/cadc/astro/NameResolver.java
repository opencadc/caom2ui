
package ca.nrc.cadc.astro;

import java.rmi.RemoteException;

/**
 * Simple astronomical name resolver.
 *
 * @version $Revision$
 * @author $Author: pdowler $
 */
public interface NameResolver
{
	/**
	 * Find ICRS coordinates for the named astronomical object
	 * using the default coordinate system. This calls 
	 * <code>resolve(name, "ICRS")</code>.
	 *
	 * @param name name of the object
	 * @return coordinates of the object in degrees
	 */
	public double[] resolve(String name) throws RemoteException;
	
	/**
	 * Find the coordinates of the named astronomical object.
	 *
	 * @param name name of the object
	 * @param coordsys name of the coordinate system to use for returned coordinates
	 * @return coordinates of the object in degrees
	 */
	public double[] resolve(String name, String coordsys) throws RemoteException;
}

// end of NameResolver.java

