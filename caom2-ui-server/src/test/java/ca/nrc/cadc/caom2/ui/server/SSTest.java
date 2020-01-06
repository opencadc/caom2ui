
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

package ca.nrc.cadc.caom2.ui.server;

import ca.nrc.cadc.caom2.CompositeObservation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.caom2.PlaneURI;
import ca.nrc.cadc.caom2.Provenance;

import java.net.URI;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


public class SSTest {

    @Test
    public void toStringProvenance() {
        final Provenance provenance = new Provenance("TESTPROV");

        // November 25th, 1977.
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1977, Calendar.NOVEMBER, 25, 1, 15, 0);
        cal.set(Calendar.MILLISECOND, 0);

        provenance.reference = URI.create("http://mysite.com/reference");
        provenance.project = "TESTPROJ";
        provenance.producer = "TESTPRODUCER";
        provenance.lastExecuted = cal.getTime();
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE1/123")));
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE2/123")));
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE2/456")));

        final String out = SS.toString(provenance);
        final String expected = "name: TESTPROV<br>version: null<br>producer: TESTPRODUCER<br>project: TESTPROJ<br>" +
            "reference: <a class=\"provenance-reference\" href=\"http://mysite.com/reference\">http://mysite" +
            ".com/reference</a><br>runID: null" +
            "<br>lastExecuted: 1977-11-25 01:15:00.000<br>inputs: caom:COLL1/PLANE1/123 caom:COLL1/PLANE2/123 " +
            "caom:COLL1/PLANE2/456 <br>keywords: ";

        assertEquals("Wrong output.", expected, out);

        provenance.reference = null;

        final String out2 = SS.toString(provenance);
        final String expected2 = "name: TESTPROV<br>version: null<br>producer: TESTPRODUCER<br>project: TESTPROJ<br>" +
            "reference: null<br>runID: null" +
            "<br>lastExecuted: 1977-11-25 01:15:00.000<br>inputs: caom:COLL1/PLANE1/123 caom:COLL1/PLANE2/123 " +
            "caom:COLL1/PLANE2/456 <br>keywords: ";

        assertEquals("Wrong output.", expected2, out2);
    }

    @Test
    public void toStringProvenanceIncompleteReferenceURL() {
        final Provenance provenance = new Provenance("TESTPROV");

        // November 25th, 1977.
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1977, Calendar.NOVEMBER, 25, 1, 15, 0);
        cal.set(Calendar.MILLISECOND, 0);

        provenance.reference = URI.create("mysite.com/reference");
        provenance.project = "TESTPROJ";
        provenance.producer = "TESTPRODUCER";
        provenance.lastExecuted = cal.getTime();
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE1/123")));
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE2/123")));
        provenance.getInputs().add(new PlaneURI(URI.create("caom:COLL1/PLANE2/456")));

        final String out = SS.toString(provenance);
        final String expected = "name: TESTPROV<br>version: null<br>producer: TESTPRODUCER<br>project: TESTPROJ<br>" +
            "reference: <a class=\"provenance-reference\" href=\"http://mysite.com/reference\">http://mysite" +
            ".com/reference</a><br>runID: null" +
            "<br>lastExecuted: 1977-11-25 01:15:00.000<br>inputs: caom:COLL1/PLANE1/123 caom:COLL1/PLANE2/123 " +
            "caom:COLL1/PLANE2/456 <br>keywords: ";

        assertEquals("Wrong output.", expected, out);

        provenance.reference = null;

        final String out2 = SS.toString(provenance);
        final String expected2 = "name: TESTPROV<br>version: null<br>producer: TESTPRODUCER<br>project: TESTPROJ<br>" +
            "reference: null<br>runID: null" +
            "<br>lastExecuted: 1977-11-25 01:15:00.000<br>inputs: caom:COLL1/PLANE1/123 caom:COLL1/PLANE2/123 " +
            "caom:COLL1/PLANE2/456 <br>keywords: ";

        assertEquals("Wrong output.", expected2, out2);
    }

    @Test
    public void toMemberString() {
        final CompositeObservation mockCompositeObservation = createMock(CompositeObservation.class);
        final Set<ObservationURI> members = new HashSet<>();

        members.add(new ObservationURI(URI.create("caom:CFHT/2069333")));
        members.add(new ObservationURI(URI.create("caom:CFHT/2069334")));

        expect(mockCompositeObservation.getMembers()).andReturn(members).once();

        replay(mockCompositeObservation);

        final String out = SS.toMemberString("/caom2ui", mockCompositeObservation, "ivo://cadc.nrc" +
            ".ca/CFHTMEGAPIPE?G025.045.358+41.104");

        final String expected = "<ul><li>caom:CFHT/2069334</li><li>caom:CFHT/2069333</li></ul>";

        assertEquals("Wrong member output", expected, out);

        verify(mockCompositeObservation);
    }
}
