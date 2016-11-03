/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2013.                         (c) 2013.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 11/14/13 - 2:45 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.plugins;

import ca.nrc.cadc.net.OutputStreamWrapper;
import ca.nrc.cadc.search.upload.VOTableUploader;
import ca.nrc.cadc.search.web.ServerToServerFTPTransfer;
import ca.nrc.cadc.uws.web.InlineContentException;

import java.io.IOException;
import java.net.URL;


/**
 * Default implementation of a VOTableUploader.
 */
public class FTPVOTableUploaderImpl implements VOTableUploader
{
    private final ServerToServerFTPTransfer ftpTransfer;


    /**
     * Default empty constructor.
     */
    FTPVOTableUploaderImpl()
    {
        this(new ServerToServerFTPTransfer("ftp", 21));
    }

    FTPVOTableUploaderImpl(final ServerToServerFTPTransfer ftpTransfer)
    {
        this.ftpTransfer = ftpTransfer;
    }

    /**
     * Perform the upload.
     *
     * @param stream            The OutputStreamWrapper
     * @param filename          The filename to use.
     * @return The URL of where to get the upload.
     * @throws InlineContentException If the upload fails.
     * @throws IOException            If the return URL cannot be obtained.
     */
    @Override
    public URL upload(final OutputStreamWrapper stream, final String filename)
            throws InlineContentException, IOException
    {
        ftpTransfer.send(stream, filename);
        return new URL(String.format("ftp://ftp:21/%s", filename));
    }
}
