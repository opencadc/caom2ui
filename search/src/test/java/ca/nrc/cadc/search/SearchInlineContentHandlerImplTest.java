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
 * 11/14/13 - 1:19 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ca.nrc.cadc.AbstractUnitTest;

import ca.nrc.cadc.net.OutputStreamWrapper;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.search.SearchInlineContentHandlerImpl;
import ca.nrc.cadc.search.upload.VOTableUploader;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


public class SearchInlineContentHandlerImplTest
        extends AbstractUnitTest<SearchInlineContentHandlerImpl>
{
    private final static byte[] UPLOAD_BYTES = "m101 m87 TARGET".getBytes();


    @Test
    public void accept() throws Exception
    {
        final URL returnURL = new URL("http://mysite.com/my/tap/upload");

        setTestSubject(new SearchInlineContentHandlerImpl()
        {
            @Override
            protected URL secureUpload(VOTableUploader voTableUploader,
                                       OutputStreamWrapper streamWrapper)
                    throws IOException
            {
                return returnURL;
            }
        });

        final InputStream bytesIn = new ByteArrayInputStream(UPLOAD_BYTES);
        final URL returnURLFound =
                getTestSubject().accept("MYUPLOAD", "text/xml", bytesIn);

        assertEquals("URLs should match.", returnURL, returnURLFound);
    }
}
