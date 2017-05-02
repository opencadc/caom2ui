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
 * 11/14/13 - 1:50 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.upload;

import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.uws.server.RandomStringGenerator;
import ca.nrc.cadc.uws.server.StringIDGenerator;


/**
 * Implementation to use a random alphanumeric filename generator.
 */
public class TAPUploadRandomAlphanumericFilenameGenerator implements TAPUploadFilenameGenerator
{
    private final static StringIDGenerator RANDOM_ALPHA_GENERATOR =
            new RandomStringGenerator(16);

    private final String prefix;
    private final String suffix;


    /**
     * Full contstructor.
     *
     * @param prefix        String to prepend to the generated filename.
     * @param suffix        String to append to the generated filename.
     */
    public TAPUploadRandomAlphanumericFilenameGenerator(final String prefix, final String suffix)
    {
        this.prefix = prefix;
        this.suffix = suffix;
    }


    /**
     * Generate a new filename for upload to TAP.
     *
     * @return String filename.
     */
    @Override
    public String generate()
    {
        final StringBuilder filenameStringBuilder = new StringBuilder();

        if (StringUtil.hasText(getPrefix()))
        {
            filenameStringBuilder.append(getPrefix());
        }

        filenameStringBuilder.append(RANDOM_ALPHA_GENERATOR.getID());

        if (StringUtil.hasText(getSuffix()))
        {
            filenameStringBuilder.append(getSuffix());
        }

        return filenameStringBuilder.toString();
    }

    public String getSuffix()
    {
        return suffix;
    }

    public String getPrefix()
    {
        return prefix;
    }
}
