/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package ca.nrc.cadc.search.plugins;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

class PluginClassLoader<T> extends ClassLoader
{
    private final File directory;


    /**
     * Creates a new class loader using the <tt>ClassLoader</tt> returned by
     * the method {@link #getSystemClassLoader()
     * <tt>getSystemClassLoader()</tt>} as the parent class loader.
     * <p>
     * <p> If there is a security manager, its {@link
     * SecurityManager#checkCreateClassLoader()
     * <tt>checkCreateClassLoader</tt>} method is invoked.  This may result in
     * a security exception.  </p>
     *
     * @param directory         The Directory to load plugins from.
     * @throws SecurityException If a security manager exists and its
     *                           <tt>checkCreateClassLoader</tt> method doesn't allow creation
     *                           of a new class loader.
     */
    PluginClassLoader(final File directory)
    {
        this.directory = directory;
    }

    /**
     * Convenience constructor.
     *
     * Creates a new class loader using the <tt>ClassLoader</tt> returned by
     * the method {@link #getSystemClassLoader()
     * <tt>getSystemClassLoader()</tt>} as the parent class loader.
     * <p>
     * <p> If there is a security manager, its {@link
     * SecurityManager#checkCreateClassLoader()
     * <tt>checkCreateClassLoader</tt>} method is invoked.  This may result in
     * a security exception.  </p>
     *
     * @throws SecurityException If a security manager exists and its
     *                           <tt>checkCreateClassLoader</tt> method doesn't allow creation
     *                           of a new class loader.
     */
    PluginClassLoader()
    {
        this(new File(System.getProperty("user.home") + File.separator
                      + ".config" + File.separator
                      + "org.opencadc.search.plugins"));
    }

    /**
     * A convenience method that calls the 2-argument form of this method
     *
     * @param name  The Class name.
     */
    public Class<T> loadClass(final String name)
            throws ClassNotFoundException
    {
        return loadClass(name, true);
    }

    /**
     * This is one abstract method of ClassLoader that all subclasses must
     * define. Its job is to load an array of bytes from somewhere and to
     * pass them to defineClass(). If the resolve argument is true, it must
     * also call resolveClass(), which will do things like verify the presence
     * of the superclass. Because of this second step, this method may be called to
     * load superclasses that are system classes, and it must take this into account.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> loadClass(final String classname, final boolean resolve)
            throws ClassNotFoundException
    {
        try
        {
            // Our ClassLoader superclass has a built-in cache of classes it has
            // already loaded. So, first check the cache.
            Class<T> c = (Class<T>) findLoadedClass(classname);

            // After this method loads a class, it will be called again to
            // load the superclasses. Since these may be system classes, we've
            // got to be able to load those too. So try to load the class as
            // a system class (i.e. from the CLASSPATH) and ignore any errors
            if (c == null)
            {
                try
                {
                    c = (Class<T>) findSystemClass(classname);
                }
                catch (Exception ex)
                {
                    // Do nothing
                }
            }

            // If the class wasn't found by either of the above attempts, then
            // try to load it from a file in (or beneath) the directory
            // specified when this ClassLoader object was created. Form the
            // filename by replacing all dots in the class name with
            // (platform-independent) file separators and by adding the
            // ".class" extension.
            if (c == null)
            {
                // Figure out the filename
                final String filename =
                        classname.replace('.', File.separatorChar) + ".class";

                // Create a File object. Interpret the filename relative to the
                // directory specified for this ClassLoader.
                final File f = new File(directory, filename);

                // Get the length of the class file, allocate an array of bytes for
                // it, and read it in all at once.
                final int length = (int) f.length();
                final byte[] classbytes = new byte[length];
                final DataInputStream in =
                        new DataInputStream(new FileInputStream(f));

                in.readFully(classbytes);
                in.close();

                // Now call an inherited method to convert those bytes into a
                // Class instance.
                c = (Class<T>) defineClass(classname, classbytes, 0, length);
            }

            // If the resolve argument is true, call the inherited resolveClass
            // method.
            if (resolve)
            {
                resolveClass(c);
            }

            // And we're done. Return the Class object we've loaded.
            return c;
        }
        // If anything goes wrong, throw a ClassNotFoundException error
        catch (Exception ex)
        {
            throw new ClassNotFoundException(ex.toString());
        }
    }
}
