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

import ca.nrc.cadc.ApplicationConfiguration;
import ca.nrc.cadc.search.upload.VOTableUploader;
import org.apache.commons.configuration2.Configuration;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class VOTableUploaderFactory
{
    static final String VOTABLE_UPLOADER_CLASSNAME_KEY =
            "org.opencadc.search.uploader";
    private static final String DEFAULT_VOTABLE_UPLOADER =
            CADCHTTPDataVOTableUploaderImpl.class.getName();
    private final Configuration configuration = new ApplicationConfiguration();
    private final PluginClassLoader<? extends VOTableUploader>
            pluginClassLoader;


    public VOTableUploaderFactory()
    {
        this(new PluginClassLoader<CADCHTTPDataVOTableUploaderImpl>());
    }

    VOTableUploaderFactory(
            final PluginClassLoader<? extends VOTableUploader> pluginClassLoader)
    {
        this.pluginClassLoader = pluginClassLoader;
    }

    /**
     * Create a new configured instance of a VOTable Uploader.
     *
     * @return VOTable
     * @throws IllegalArgumentException If the class cannot be found or
     *                                  created.
     */
    public VOTableUploader createUploader() throws IllegalArgumentException
    {
        final String voTableUploaderClassName =
                configuration.getString(VOTABLE_UPLOADER_CLASSNAME_KEY);

        try
        {
            final Class<? extends VOTableUploader> clazz =
                    pluginClassLoader.loadClass(voTableUploaderClassName);

            return clazz.newInstance();
        }
        catch (Exception e)
        {
            try
            {
                return (VOTableUploader) Class.forName(
                        DEFAULT_VOTABLE_UPLOADER).newInstance();
            }
            catch (Exception e2)
            {
                throw new IllegalArgumentException(
                        "No suitable plugins for VOTableUploader could be found.",
                        e2);
            }
        }
    }
}
