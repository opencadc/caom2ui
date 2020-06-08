/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2015.                            (c) 2015.
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

package ca.nrc.cadc.caom2;

import ca.nrc.cadc.AbstractUnitTest;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import ca.nrc.cadc.date.DateUtil;
import org.junit.Test;


public class GroupRowMapperTest extends AbstractUnitTest<GroupRowMapper>
{
    @Test
    public void mapRowFresh() throws Exception
    {
        setTestSubject(new GroupRowMapper());

        final ResultSet mockResultSet = createMock(ResultSet.class);
        final ResultSetMetaData mockResultSetMetaData =
                createMock(ResultSetMetaData.class);
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);

        // Make a date some time in the past, something less
        // than 5 years as instruments are considered stale after that
        Date freshInstrumentDate = new Date();
        long stillFreshDateMillis = freshInstrumentDate.getTime() - 10000000L;

        expect(mockResultSet.getMetaData()).andReturn(
                mockResultSetMetaData).once();

        expect(mockResultSetMetaData.getColumnCount()).andReturn(8).once();
        expect(mockResultSetMetaData.getColumnName(1)).andReturn("1").once();
        expect(mockResultSetMetaData.getColumnName(2)).andReturn("2").once();
        expect(mockResultSetMetaData.getColumnName(3)).
                andReturn("maxLastModified").once();
        expect(mockResultSetMetaData.getColumnName(4)).andReturn("4").once();
        expect(mockResultSetMetaData.getColumnName(5)).andReturn("5").once();
        expect(mockResultSetMetaData.getColumnName(6)).andReturn("6").once();
        expect(mockResultSetMetaData.getColumnName(7)).andReturn("7").once();
        expect(mockResultSetMetaData.getColumnName(8)).andReturn("8").once();

        expect(mockResultSet.getString(1)).andReturn("VAL1").once();
        expect(mockResultSet.getString(2)).andReturn("VAL2").once();
        expect(mockResultSet.getTimestamp(3)).
                andReturn(new Timestamp(stillFreshDateMillis)).once();
        expect(mockResultSet.getString(4)).andReturn("VAL4").once();
        expect(mockResultSet.getString(5)).andReturn("VAL5").once();
        expect(mockResultSet.getString(6)).andReturn("VAL6").once();
        expect(mockResultSet.getString(7)).andReturn("VAL7").once();
        expect(mockResultSet.getString(8)).andReturn("VAL8").once();

        replay(mockResultSet, mockResultSetMetaData);

        @SuppressWarnings("unchecked")
        final List<String> rowResult = (List<String>)
                getTestSubject().mapRow(mockResultSet, 1);

        assertEquals("Should have eight entries", 8, rowResult.size());
        assertEquals("Third item should be a true boolean.", "true",
                     rowResult.get(2));

        verify(mockResultSet, mockResultSetMetaData);
    }

    @Test
    public void mapRowStale() throws Exception
    {
        setTestSubject(new GroupRowMapper());

        final ResultSet mockResultSet = createMock(ResultSet.class);
        final ResultSetMetaData mockResultSetMetaData =
                createMock(ResultSetMetaData.class);
        final Calendar cal = Calendar.getInstance(DateUtil.UTC);

        cal.set(1977, Calendar.NOVEMBER, 25, 3, 12, 21);
        cal.set(Calendar.MILLISECOND, 0);

        expect(mockResultSet.getMetaData()).andReturn(
                mockResultSetMetaData).once();

        expect(mockResultSetMetaData.getColumnCount()).andReturn(8).once();
        expect(mockResultSetMetaData.getColumnName(1)).andReturn("1").once();
        expect(mockResultSetMetaData.getColumnName(2)).andReturn("2").once();
        expect(mockResultSetMetaData.getColumnName(3)).
                andReturn("maxLastModified").once();
        expect(mockResultSetMetaData.getColumnName(4)).andReturn("4").once();
        expect(mockResultSetMetaData.getColumnName(5)).andReturn("5").once();
        expect(mockResultSetMetaData.getColumnName(6)).andReturn("6").once();
        expect(mockResultSetMetaData.getColumnName(7)).andReturn("7").once();
        expect(mockResultSetMetaData.getColumnName(8)).andReturn("8").once();

        expect(mockResultSet.getString(1)).andReturn("VAL1").once();
        expect(mockResultSet.getString(2)).andReturn("VAL2").once();
        expect(mockResultSet.getTimestamp(3)).
                andReturn(new Timestamp(cal.getTime().getTime())).once();
        expect(mockResultSet.getString(4)).andReturn("VAL4").once();
        expect(mockResultSet.getString(5)).andReturn("VAL5").once();
        expect(mockResultSet.getString(6)).andReturn("VAL6").once();
        expect(mockResultSet.getString(7)).andReturn("VAL7").once();
        expect(mockResultSet.getString(8)).andReturn("VAL8").once();

        replay(mockResultSet, mockResultSetMetaData);

        @SuppressWarnings("unchecked")
        final List<String> rowResult = (List<String>)
                getTestSubject().mapRow(mockResultSet, 1);

        assertEquals("Should have eight entries", 8, rowResult.size());
        assertEquals("Third item should be a false boolean.", "false",
                     rowResult.get(2));

        verify(mockResultSet, mockResultSetMetaData);
    }
}
