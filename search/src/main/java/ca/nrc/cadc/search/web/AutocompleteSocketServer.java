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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Writer;


@ServerEndpoint("/autocomplete")
public class AutocompleteSocketServer
{
    private static final String JSON_RESPONSE_PARAM = "wt=json";
    private static final String DEFAULT_SOLR_URL = "http://solr:8983/solr";
    private static final String DEFAULT_SOLR_URL_ENV = "SOLR_URL";

    private AutocompleteSocketSessionManager autocompleteSocketSessionManager =
            new AutocompleteSocketSessionManager();

    private SolrClient _solrClient;


    public AutocompleteSocketServer()
    {

    }


    /**
     * For testing.
     *
     * @param _solrClient The client to use.
     */
    AutocompleteSocketServer(final SolrClient _solrClient)
    {
        this._solrClient = _solrClient;
    }


    @OnOpen
    public void onOpen(final Session session,
                       final EndpointConfig endpointConfig)
    {
        autocompleteSocketSessionManager.addSession(session);
        final String configuredSolrURL = System.getenv(DEFAULT_SOLR_URL_ENV);
        final String solrURL;

        if (configuredSolrURL == null)
        {
            solrURL = DEFAULT_SOLR_URL;
        }
        else
        {
            solrURL = configuredSolrURL;
        }

        _solrClient = new HttpSolrClient.Builder(solrURL).build();
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason)
    {
        autocompleteSocketSessionManager.removeSession(session);
    }


    @OnMessage
    public void handleMessage(final String message, final Session session)
    {
        final JSONObject jsonMessage = new JSONObject(message);

        try
        {
            search(AutocompleteArea.valueOf(jsonMessage.getString("area")
                                                    .toUpperCase()),
                   jsonMessage.getString("term"),
                   session.getBasicRemote().getSendWriter());
        }
        catch (IOException e)
        {
            autocompleteSocketSessionManager.removeSession(session);
            onError(e);
        }
    }

    @OnError
    public void onError(final Throwable error)
    {
        Logger.getLogger(AutocompleteSocketServer.class.getName()).
                log(Level.FATAL, null, error);
    }

    /**
     * Perform a text search and write the results.
     *
     * @param autocompleteArea The area (core) to search within.
     * @param term             The term to search for.
     * @param writer           To write out the results.
     * @throws IOException For any writing errors.
     */
    void search(final AutocompleteArea autocompleteArea,
                       final String term, final Writer writer)
            throws IOException
    {
        final SolrQuery solrQuery = new SolrQuery("*" + term + "*");
        try
        {
            querySolr(solrQuery, autocompleteArea.name().toLowerCase(),
                      createResponseCallback(writer));
        }
        finally
        {
            writer.flush();
            writer.close();
        }
    }

    void querySolr(final SolrQuery solrQuery, final String coreName,
                   final StreamingResponseCallback responseCallback)
            throws IOException
    {
        try
        {
            Logger.getLogger(AutocompleteSocketServer.class).info("Looking up: " + solrQuery.getQuery());
            _solrClient.queryAndStreamResponse(coreName, solrQuery,
                                               responseCallback);
        }
        catch (SolrServerException e)
        {
            throw new IOException(e);
        }
    }

    StreamingResponseCallback createResponseCallback(final Writer writer)
    {
        return new ResponseCallbackHandler(writer);
    }
}
