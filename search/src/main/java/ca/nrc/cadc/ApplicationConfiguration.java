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

package ca.nrc.cadc;

import ca.nrc.cadc.util.StringUtil;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.UnionCombiner;

import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;


/**
 * Configuration for this Application.  This will combine the System Properties
 * with the CADC File-based configuration in $HOME/config directory.
 */
public class ApplicationConfiguration
{
    public static final String TAP_SERVICE_URI_PROPERTY_KEY =
            "org.opencadc.search.tap-service-id";
    public static final String TAP_SERVICE_HOST_PORT_PROPERTY_KEY =
            "org.opencadc.search.tap-service-host-port";
    public static final URI DEFAULT_TAP_SERVICE_URI =
            URI.create("ivo://cadc.nrc.ca/tap");

    public static final String CAOM2OPS_SERVICE_URI_PROPERTY_KEY =
            "org.opencadc.search.caom2ops-service-id";
    public static final String CAOM2OPS_SERVICE_HOST_PORT_PROPERTY_KEY =
            "org.opencadc.search.caom2ops-service-host-port";
    public static final URI DEFAULT_CAOM2OPS_SERVICE_URI =
            URI.create("ivo://cadc.nrc.ca/caom2ops");

    public final static String CAOM2_UI_PROPERTY_KEY =
            "org.opencadc.search.caom2ui-host";
    public final static String DEFAULT_CAOM2_UI_HOST =
            "www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca";

    private static final Logger LOGGER =
            Logger.getLogger(ApplicationConfiguration.class);

    private static final String PROPERTIES_FILE_PATH =
            System.getProperty("user.home") + File.separator
            + "config/org.opencadc.search.properties";

    // Internally uses the Apache configurations.
    // Make package private to allow tests to override.
    final CombinedConfiguration configuration = new CombinedConfiguration();


    /**
     * Creates a new instance of {@code CombinedConfiguration} that uses
     * a union combiner.
     *
     * @see UnionCombiner
     */
    public ApplicationConfiguration()
    {
        configuration.addConfiguration(new SystemConfiguration());

        final Parameters parameters = new Parameters();
        final FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(
                        PropertiesConfiguration.class).configure(
                                parameters.properties().setFileName(
                                        PROPERTIES_FILE_PATH));

        try
        {
            configuration.addConfiguration(builder.getConfiguration());
        }
        catch (ConfigurationException e)
        {
            LOGGER.warn(String.format(
                    "No configuration found at %s.\nUsing defaults.",
                    PROPERTIES_FILE_PATH));
        }
    }


    public URI lookupServiceURI(final String key, final URI defaultValue)
    {
        final String value = lookup(key);
        return StringUtil.hasText(value) ? URI.create(value) : defaultValue;
    }

    public String lookup(final String key)
    {
        return configuration.getString(key);
    }

    public int lookupInt(final String key, final int defaultValue)
    {
        return configuration.getInt(key, defaultValue);
    }

    public boolean lookupBoolean(final String key)
    {
        return configuration.getBoolean(key, true);
    }

    public String lookup(final String key, final String defaultValue)
    {
        return configuration.getString(key, defaultValue);
    }
}
