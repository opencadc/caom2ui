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

package ca.nrc.cadc.search.web;

import ca.nrc.cadc.net.OutputStreamWrapper;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessControlException;


public class ServerToServerFTPTransfer
{
    // username and password.
    private static final String CREDENTIAL = "WEBTMP";
    private final String hostname;
    private final int port;


    public ServerToServerFTPTransfer(final String host, final int port)
    {
        this.hostname = host;
        this.port = port;
    }


    public void send(final OutputStreamWrapper outputStreamWrapper,
                     final String filename)
            throws IOException
    {
        final FTPClient ftpClient = connect();
        ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        try
        {
            final OutputStream outputStream =
                    ftpClient.storeFileStream(filename);

            outputStreamWrapper.write(outputStream);

            outputStream.flush();
            outputStream.close();
        }
        finally
        {
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                }
                catch (IOException f)
                {
                    // do nothing
                }
            }
        }
    }

    private FTPClient connect() throws IOException
    {
        final FTPClient ftpClient = createFTPClient();
        ftpClient.connect(hostname, port);

        final int reply = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftpClient.disconnect();
            throw new IOException(
                    String.format("FTP Connection failed with error code %d.",
                                  reply));
        }
        else
        {
            return login(ftpClient);
        }
    }

    /**
     *
     * @param ftpClient     The Connected client.
     * @return              The mutated client, now authenticated.
     * @throws AccessControlException    If login fails.
     */
    private FTPClient login(final FTPClient ftpClient)
            throws AccessControlException, IOException
    {
        if (!ftpClient.login(CREDENTIAL, CREDENTIAL))
        {
            ftpClient.logout();
            throw new AccessControlException(
                    String.format("Login failed for %s", CREDENTIAL));
        }
        else
        {
            return ftpClient;
        }
    }

    /**
     * Override me to for testing.
     * @return      FTPClient instance.
     */
    FTPClient createFTPClient()
    {
        return new FTPClient();
    }
}
