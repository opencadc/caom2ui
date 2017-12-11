/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2017.                            (c) 2017.
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

package ca.nrc.cadc.caom2.ui.server.client;


import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.net.HttpDownload;

import javax.security.auth.Subject;
import java.net.URI;
import java.net.URL;

import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class Caom2MetaClientTest
{
    @Test
    public void getObservation() throws Exception
    {
        final HttpDownload mockDownload = createMock(HttpDownload.class);

        final Caom2MetaClient testSubject = new Caom2MetaClient()
        {
            /**
             * Place for testers to override.
             *
             * @return URL instance.
             */
            @Override
            public URL getServiceURL()
            {
                try
                {
                    return new URL("http://mysite.com");
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Obtain a new instance of a downloader.  Tests can override as needed.
             *
             * @param url        The URL to download from.
             * @param readAction The read action to write to.
             * @return HttpDownload instance.
             */
            @Override
            public HttpDownload getDownloader(URL url, ReadAction readAction)
            {
                return mockDownload;
            }
        };

        final Subject subject = new Subject();
        final ObservationURI observationURI = new ObservationURI(URI.create("caom:ARCHIVE/G024.143.732+17.167"));


        mockDownload.run();
        expectLastCall().once();

        expect(mockDownload.getThrowable()).andReturn(null).once();

        replay(mockDownload);

        // Doesn't return anything.
        testSubject.getObservation(subject, observationURI);

        assertEquals("ID was not re-encoded.", "?ID=caom%3AARCHIVE%2FG024.143.732%2B17.167",
                     testSubject.path);

        verify(mockDownload);
    }
}
