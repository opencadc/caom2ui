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

import java.util.Iterator;
import java.util.NoSuchElementException;

import ca.nrc.cadc.search.parser.resolver.ResolverImpl;
import org.jdom2.Element;
import org.jdom2.Namespace;

import ca.nrc.cadc.dali.Circle;
import ca.nrc.cadc.dali.util.CircleFormat;
import ca.nrc.cadc.dali.Point;
import ca.nrc.cadc.search.parser.Resolver;
import ca.nrc.cadc.search.parser.TargetData;
import ca.nrc.cadc.search.parser.TargetParser;


public class StreamingIterator implements Iterator<Element>
{
    // Iterator
    private final Iterator<String> innerIterator;

    // Namespace of the element.
    private final Namespace namespace;

    // Counts of table rows and processing errors.
    private final UploadResults uploadResults;

    private final CircleFormat cf = new CircleFormat();


    /**
     * Constructor.
     *
     * @param innerIterator The Iterator to stream data from.
     * @param namespace     The Namespace to write for.
     * @param uploadResults The results of the File Upload.
     */
    public StreamingIterator(final Iterator<String> innerIterator,
                             final Namespace namespace,
                             final UploadResults uploadResults)
    {
        this.innerIterator = innerIterator;
        this.namespace = namespace;
        this.uploadResults = uploadResults;
    }

    /**
     * @return true if the iteration has more elements.
     */
    public boolean hasNext()
    {
        return innerIterator.hasNext();
    }

    /**
     * Gets the next String from the Iterator, and builds a JDOM Element
     * containing a single HTML table row, with table data elements for
     * each token in the String.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException iteration has no more elements.
     */
    public Element next() throws NoSuchElementException
    {
        // Check that we have another element.
        final String line = innerIterator.next();
        uploadResults.incrementRowCount();

        // Create the TR element.
        final Element tableRow = new Element("TR", namespace);

        // Add the row count.
        tableRow.addContent(createTableData(uploadResults.getRowCount()));

        // The position.
        tableRow.addContent(createTableData(line));

        Double ra = null;
        Double dec = null;
        Double radius = null;
        Circle position = null;
        String error = "";
        try
        {
            // Attempt to parse the element into a position.
            final Resolver resolver = new ResolverImpl();
            final TargetParser parser = new TargetParser(resolver);
            final TargetData result = parser.parse(line, uploadResults.getResolver());

            ra = result.getRA();
            dec = result.getDec();
            radius = result.getRadius();
            position = new Circle(new Point(ra, dec), radius);
        }
        catch (Throwable t)
        {
            uploadResults.incrementErrorCount();
            error = t.getMessage();
        }

        tableRow.addContent(createTableData(ra));
        tableRow.addContent(createTableData(dec));
        tableRow.addContent(createTableData(radius));
        tableRow.addContent(createTableData(position));
        tableRow.addContent(createTableData(error));

        return tableRow;
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    private Element createTableData(int i)
    {
        Element element = new Element("TD", namespace);
        element.setText(String.valueOf(i));
        return element;
    }

    private Element createTableData(Double d)
    {
        final Element element = new Element("TD", namespace);
        element.setText((d == null) ? "" : d.toString());
        return element;
    }

    private Element createTableData(String s)
    {
        final Element element = new Element("TD", namespace);
        element.setText((s == null) ? "" : s);

        return element;
    }

    private Element createTableData(Circle s)
    {
        final Element element = new Element("TD", namespace);
        element.setText((s == null) ? "" : cf.format(s));

        return element;
    }
}
