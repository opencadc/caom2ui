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
import java.util.List;

import ca.nrc.cadc.search.parser.resolver.TargetNameResolverClient;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Wrapper around a JDOM Element that builds a TABLEDATA Element
 * from a Iterator.
 */
public class TableDataElement extends Element
{
    // Iterator used to populate the TABLEDATA element.
    private Iterator iterator;

    // Counts of table rows and processing errors.
    UploadResults uploadResults;

    private final TargetNameResolverClient targetNameResolverClient;


    /**
     * Constructor.
     *
     * @param iterator          The iterator for content.
     * @param namespace         XML Namespace.
     * @param uploadResults     Upload Results of the uploaded file.
     */
    public TableDataElement(Iterator iterator, Namespace namespace,
                            final TargetNameResolverClient targetNameResolverClient,
                            UploadResults uploadResults)
    {
        super("TABLEDATA", namespace);
        this.iterator = iterator;
        this.namespace = namespace;
        this.targetNameResolverClient = targetNameResolverClient;
        this.uploadResults = uploadResults;
    }

    /**
     * Returns a ResultSetList, which wraps a List
     * around a ResultSet.
     * @return a ResultSetList
     */
    @Override
    @SuppressWarnings("unchecked")
    public List getContent()
    {
        return new StreamingIteratorList(iterator, namespace,
                                         targetNameResolverClient, uploadResults);
    }

}
