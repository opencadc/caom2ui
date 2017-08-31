/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
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
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.caom2.ui.server.client;

import ca.nrc.cadc.caom2.Observation;
import ca.nrc.cadc.caom2.ObservationURI;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.HttpDownload;
import ca.nrc.cadc.reg.Standards;
import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Get collections and observation information from caom2repo
 * 
 * @author hjeeves
 */
public class Caom2RepoClient extends BaseClient
{
    private static final Logger log = Logger.getLogger(Caom2RepoClient.class);
    public static final URI CAOM_REPO_RESOURCE_ID = URI.create("ivo://cadc.nrc.ca/caom2repo");
    private static String CANT_GET_COLLECTIONS_LIST = "Unable to get collection list.";
    private static String CANT_GET_OBSERVATION_LIST = "Unable to get observation list.";
    static final String CAOM2REPO_SERVICE_URI_PROPERTY_KEY = "org.opencadc.caom2ui.client-service-id";

    public Caom2RepoClient() {
        resourceId = CAOM_REPO_RESOURCE_ID;
        propertyKey = CAOM2REPO_SERVICE_URI_PROPERTY_KEY;
        standardsURI = Standards.CAOM2REPO_OBS_23;
        // Set path at a point where collection and/or observation information is available
    }


    /**
     * Call client for list of collections.
     * @return list of collections
     */
    public List<String> getCollections()
    {
        List<String> ret = new ArrayList<>();
        String errMsg = "";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // no path for get collections list
        path = "";
        URL url = getServiceURL();
        HttpDownload get = new HttpDownload(url, bos);
        get.run();

        int response = get.getResponseCode();

        if (response == 200)
        {
            String message = bos.toString().trim();
            String[] lines = message.split("\\r?\\n");

            for (int i = 0; i < lines.length; i++)
            {
                ret.add(lines[i]);
            }
        }
        else
        {
            errMsg = CANT_GET_COLLECTIONS_LIST + " Response code: " + response;
            log.error(errMsg);
            throw new RuntimeException(errMsg);
        }

        Collections.sort(ret);
        return ret;
    }


    /**
     * Get list of observations for named collection. Limited to 100.
     * NOTE: when 'order' parameter is supported by underlying client call,
     * the latest 100 will be displayed
     * @param collection: collection name
     * @return list of ObsLink objects
     */
    public List<ObsLink> getObservations(String collection)
    {
        String dataSourceName = null;
        String errMsg = "";
        String urlStr = "";
        List<ObsLink> obsUriList = new ArrayList<>();
        try
        {

            path = "/" + collection + "?maxrec=100&order=desc";
            URL collectionURL = getServiceURL();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HttpDownload get = new HttpDownload(collectionURL, bos);
            get.run();

            int response = get.getResponseCode();

            if (response == 200)
            {
                String message = bos.toString().trim();
                String[] lines = message.split("\\r?\\n");

                for (int i = 0; i < lines.length; i++)
                {
                    String [] obsLinkData = lines[i].split("\\t");
                    // Create a new ObsLink object from first 3 columns of each row
                    ObsLink nextObs = new ObsLink();
                    nextObs.type = "not set";
                    nextObs.uri = new ObservationURI(obsLinkData[0], obsLinkData[1]);
                    nextObs.lastModified = DateUtil.flexToDate(obsLinkData[2],DateUtil.getDateFormat(DateUtil.ISO8601_DATE_FORMAT_LOCAL,DateUtil.UTC));
                    obsUriList.add(nextObs);
                }
            }
            else
            {
                errMsg = CANT_GET_OBSERVATION_LIST + " Response code: " + response;
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            return obsUriList;
        }
        catch(ParseException pe)
        {
            errMsg = CANT_GET_OBSERVATION_LIST + " Unable to parse datestamp for observation";
            log.error(errMsg);
            throw new RuntimeException(errMsg, pe);
        }
    }

    /**
     * Download the Observation for the given URI.
     * @param subject The Subject to download as.
     * @param uri     The Observation URI.
     * @return Observation instance.
     */
    public Observation getObservation(final Subject subject, final ObservationURI uri)
    {
        path = "/" + uri.getCollection() + "/" + uri.getObservationID();
        URL serviceURL = getServiceURL();

        final BaseClient.ReadAction ra = getObservationReader();
        final HttpDownload get = getDownloader(serviceURL, ra);

        Subject.doAs(subject, new BaseClient.GetAction(get, uri));

        return ra.getObs();
    }

}
