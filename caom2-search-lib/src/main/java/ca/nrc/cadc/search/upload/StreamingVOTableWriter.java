/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONN√âES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits r√©serv√©s
 *
 *  NRC disclaims any warranties,        Le CNRC d√©nie toute garantie
 *  expressed, implied, or               √©nonc√©e, implicite ou l√©gale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           √™tre tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou g√©n√©ral,
 *  arising from the use of the          accessoire ou fortuit, r√©sultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        √™tre utilis√©s pour approuver ou
 *  products derived from this           promouvoir les produits d√©riv√©s
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  pr√©alable et particuli√®re
 *                                       par √©crit.
 *
 *  $Revision: 4 $
 *
 ************************************************************************
 */

package ca.nrc.cadc.search.upload;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

import ca.nrc.cadc.dali.tables.votable.VOTableWriter;
import org.apache.commons.io.LineIterator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class StreamingVOTableWriter extends VOTableWriter {
    // Counts of table rows and processing errors.
    private UploadResults uploadResults;

    public StreamingVOTableWriter(final UploadResults uploadResults) {
        super();
        this.uploadResults = uploadResults;
    }


    public void write(final InputStream in, final OutputStream out) throws IOException {
        // Get an Iterator to the text of the InputStream.
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        final Iterator iterator = new LineIterator(reader);

        write(iterator, out);
    }

    public void write(final Iterator iterator, final OutputStream out) throws IOException {
        Document document = createDocument();
        Element root = document.getRootElement();
        Namespace namespace = root.getNamespace();

        // Create the RESOURCE element and add to the VOTABLE element.
        Element resource = new Element("RESOURCE", namespace);
        resource.setAttribute("type", "results");
        root.addContent(resource);

        // Create the TABLE element and add to the RESOURCE element.
        Element table = new Element("TABLE", namespace);
        resource.addContent(table);

        // Write out FIELD for row number.
        table.addContent(getField("LineNumber", "int", namespace));

        // FIELD element for the original target or coordinates.
        table.addContent(getField("Target", "char", namespace));

        // FIELD elements for RA, DEC, and radius.
        table.addContent(getField("RA", "double", namespace));
        table.addContent(getField("DEC", "double", namespace));
        table.addContent(getField("radius", "double", namespace));

        // FIELD element for any errors encountered parsing the position.
        table.addContent(getField("TargetError", "char", namespace));

        // Create the DATA element and add to the TABLE element.
        Element data = new Element("DATA", namespace);
        table.addContent(data);

        // Create the TABLEDATA element and add the to DATA element.
        Element tableData = new TableDataElement(iterator, namespace, uploadResults);
        data.addContent(tableData);

        write(document, out);
    }

    /**
     * Write out the Document to the given OutputStream.  Useful to overwrite
     * when testing.
     *
     * @param document The JDOM2 Document.
     * @param out      The OutputStream to write to.
     * @throws IOException For any unforeseen error(s).
     */
    protected void write(final Document document, final OutputStream out)
        throws IOException {
        // Write out the VOTABLE.
        final XMLOutputter outputter =
            new XMLOutputter(Format.getPrettyFormat(),
                             new TableDataXMLOutputProcessor(
                                 Integer.MAX_VALUE));
        outputter.output(document, out);
    }

    private Element getField(final String name, final String datatype,
                             final Namespace namespace) {
        final Element element = new Element("FIELD", namespace);
        element.setAttribute("name", name);
        element.setAttribute("datatype", datatype);

        if (datatype.equalsIgnoreCase("char")) {
            element.setAttribute("arraysize", "*");
        }

        return element;
    }
}
