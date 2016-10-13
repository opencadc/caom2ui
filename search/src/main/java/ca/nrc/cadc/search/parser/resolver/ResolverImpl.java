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

package ca.nrc.cadc.search.parser.resolver;

import ca.nrc.cadc.search.parser.AbstractPositionParser;
import ca.nrc.cadc.search.parser.RadiusParser;
import ca.nrc.cadc.search.parser.Resolver;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.exception.NumericParserException;
import ca.nrc.cadc.util.StringUtil;

import java.io.IOException;
import java.util.Arrays;


public class ResolverImpl implements Resolver
{
    private final TargetNameResolverClient nameResolverClient;


    /**
     * Complete constructor.
     *
     * @param nameResolverClient      NameResolver instance.
     */
    public ResolverImpl(final TargetNameResolverClient nameResolverClient)
    {
        this.nameResolverClient = nameResolverClient;
    }


    /**
     * Attempts to parse a radius and verify it is within allowed limits.
     *
     * @param rad radius to be parsed.
     */
    private Double parseRadius(final String rad)
    {
        Double val;

        try
        {
            final RadiusParser parser = new RadiusParser(rad);
            val = parser.getValue().doubleValue();
        }
        catch (final NumericParserException e)
        {
            val = null;
        }

        return val;
    }

    /**
     * Attempts to parse the last target parameter as a radius,
     * and resolve the remaining leading parameters.
     *
     * @param target            The target name.
     * @param resolverName      The name of the resolver to use.
     *
     * @return boolean true if the target was parsed and resolved,
     *         false otherwise.
     * @throws IOException      For resolver issues.
     */
    @Override
    public TargetData resolveTarget(final String target,
                                    final String resolverName)
            throws IOException
    {
        TargetData result;

        if (StringUtil.hasLength(target) && StringUtil.hasLength(resolverName))
        {
            // Try entire name first.
            result = nameResolverClient.resolve(target, resolverName);

            // Can't resolve as provided.
            if (result == null)
            {
                // Check for a radius at the end.
                final String[] parts = target.split("[, ]+");

                if (parts.length > 1)
                {
                    final String rad = parts[parts.length - 1]; // last part
                    final Double radius = parseRadius(rad);

                    if (radius == null)
                    {
                        result = null;
                    }
                    else
                    {
                        final String targetValue =
                                String.join(" ",
                                            Arrays.copyOfRange(parts, 0,
                                                               (parts.length
                                                                - 1)));

                        String name = targetValue.trim();

                        char c = name.charAt(name.length() - 1);

                        while (c == ',' || c == ' ')
                        {
                            name = name.substring(0, name.length() - 1);
                            c = name.charAt(name.length() - 1);
                        }

                        result = resolveTarget(name, resolverName);

                        if (result != null)
                        {
                            result.setRadius(radius);
                        }
                    }
                }
                else
                {
                    result = null;
                }
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "Target and Resolver name are required.");
        }

        return result;
    }
}
