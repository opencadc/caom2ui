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

import java.util.AbstractList;
import java.util.Iterator;

import org.jdom2.Element;
import org.jdom2.Namespace;


public class StreamingIteratorList extends AbstractList<Element> {

    // Iterator the List is wrapping.
    private Iterator<String> innerIterator;

    // Namespace for the element.
    private Namespace namespace;

    // Counts of table rows and processing errors.
    private UploadResults uploadResults;


    /**
     * Constructor.
     *
     * @param innerIterator String iterator.
     * @param namespace     Namespace of the items.
     * @param uploadResults The UploadResults instance.
     */
    public StreamingIteratorList(final Iterator<String> innerIterator,
                                 final Namespace namespace,
                                 final UploadResults uploadResults) {
        super();
        this.innerIterator = innerIterator;
        this.namespace = namespace;
        this.uploadResults = uploadResults;
    }

    /**
     * Returns a StreamingIterator which can iterate through
     * an InputStream.
     *
     * @return a StreamingIterator
     */
    @Override
    public Iterator<Element> iterator() {
        return new StreamingIterator(innerIterator, namespace, uploadResults);
    }


    /**
     * Random access to the Stream which the List is wrapping
     * is not supported, can only move forward through the Stream,
     * therefore throws an UnsupportedOperationException.
     *
     * @param index The index of the desired item.
     * @throws UnsupportedOperationException
     */
    @Override
    public Element get(final int index) {
        throw new UnsupportedOperationException("get method not supported");
    }

    /**
     * Can't find the size of the Stream which the List is wrapping
     * without reading the entire Stream, therefore throws an UnsupportedOperationException.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public int size() {
        throw new UnsupportedOperationException("size method not supported");
    }
}
