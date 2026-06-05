
/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2019.                            (c) 2019.
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

package ca.nrc.cadc.uws;

import ca.nrc.cadc.dali.tables.votable.*;
import ca.nrc.cadc.rest.InlineContentHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class SearchInlineContentHandlerTest {
    @Test
    public void accept() throws Exception {
        final URL putURL = URI.create("http://mysite.com/store-stuff/votable.xml").toURL();
        final SearchInlineContentHandler testSubject = new SearchInlineContentHandler() {
            @Override
            URL getPutURL(String filename) {
                return putURL;
            }

            @Override
            void upload(URL putURL, String contentType, final byte[] uploadBytes) {
                // Do not upload anything.
            }
        };

        final String input = "M31\nm101\n12:34:56 -43:21:00";
        final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        final InlineContentHandler.Content content =
            testSubject.accept("targetList.SIMBAD", "text/plain", inputStream);

        Assert.assertEquals("Wrong name.", "target-list-upload", content.name);
        Assert.assertEquals("Wrong list.", "search_upload," + putURL.toExternalForm(), content.value);
    }

    @Test
    public void uploadBytes() throws Exception {
        final String[] expectedFields = new String[] {
                "LineNumber", "Target", "RA", "DEC", "radius", "target_position", "TargetError"
        };

        final SearchInlineContentHandler testSubject = new SearchInlineContentHandler();
        final String input = "M31\nm101\n12:34:56 -43:21:00";
        final InputStream inputStream = new ByteArrayInputStream(input.getBytes());

        final byte[] voTableBytes = testSubject.toByteArray("ALL", inputStream);
        final VOTableReader voTableReader = new VOTableReader();
        final VOTableDocument voTableDocument = voTableReader.read(new String(voTableBytes));

        final VOTableResource resource = voTableDocument.getResourceByType("results");
        final VOTableTable table = resource.getTable();
        final List<VOTableField> tableFields = table.getFields();

        Arrays.stream(expectedFields)
              .forEach(fieldName -> tableFields.stream()
                                               .filter(field -> field.getName().equals(fieldName))
                                               .findFirst()
                                               .orElseThrow(() -> new AssertionError("Field not found: "
                                                                                             + fieldName)));
    }
}
