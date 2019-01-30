/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2014.                         (c) 2014.
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
 * 23/05/14 - 8:57 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.integration;


import org.junit.Test;


public class BookmarkBrowserTest extends AbstractAdvancedSearchIntegrationTest
{
    private static final String BOOKMARK_QUERY_STRING_1 =
            "Plane.position.bounds=M17#sortCol=Start%20Date&sortDir=dsc&col_1=_checkbox_selector;;;&col_2=Preview;;;&col_3=Obs.%20ID;;;&col_4=Filter;;;&col_5=RA%20(J2000.0);;;&col_6=Dec.%20(J2000.0);;;&col_7=Target%20Name;;;&col_8=Int.%20Time;;;&col_9=Collection;;;&col_10=Start%20Date;;;&col_11=IQ;;;&col_12=Instrument;;;";
    private static final String BOOKMARK_QUERY_STRING_2 =
            "Observation.proposal.id=M11BU16&Observation.collection=JCMT#sortCol=Start%20Date&sortDir=dsc&col_1=_checkbox_selector;;;&col_2=Rest-frame%20Energy;;;&col_3=Target%20Name;;;&col_4=RA%20(J2000.0);;;&col_5=Dec.%20(J2000.0);;;&col_6=Proposal%20ID;;;&col_7=Start%20Date;;;&col_8=Sequence%20Number;;;&col_9=Instrument;;;&col_10=Preview;;;&col_11=Molecule;;;&col_12=Transition;;;&col_13=Filter;;;&col_14=Int.%20Time;;;&col_15=Field%20of%20View;;;&col_16=Instrument%20Keywords;;;&col_17=Obs.%20Type;;;&col_18=Intent;;;&col_19=Moving%20Target;;;&col_20=Algorithm%20Name;;;&col_21=Product%20ID;;;&col_22=Data%20Type;;;";

    // Querying non-form input fields for Story 2107.
    private static final String BOOKMARK_QUERY_STRING_3 = "Observation.proposal.project=OSSOS";


    public BookmarkBrowserTest() throws Exception
    {
        super();
    }

    //TODO: uncomment when implementation is complete
    //@Test
    public void bookmarkTestM17() throws Exception
    {
        final SearchResultsPage searchResultsPage = bookmarkSearch(BOOKMARK_QUERY_STRING_1);

        // Ensure presence.
        verifyFalse(searchResultsPage.getIQColumnHeader() == null);

        searchResultsPage.queryTab();
    }

    //TODO: uncomment when implementation is complete
    //@Test
    public void bookmarkTestM11BU16() throws Exception
    {
        final SearchResultsPage searchResultsPage = bookmarkSearch(BOOKMARK_QUERY_STRING_2);

        // Ensure presence.
        verifyFalse(searchResultsPage.getRestFrameEnergyColumnHeader() == null);

        searchResultsPage.queryTab();
    }

    //TODO: uncomment when implementation is complete
    //@Test
    public void bookmarkTestProposalProject() throws Exception
    {
        final SearchResultsPage searchResultsPage = bookmarkSearch(BOOKMARK_QUERY_STRING_3);

        searchResultsPage.includeHiddenColumn("caom2:Observation.proposal.project");
        searchResultsPage.confirmProposalProjectColumnHeader();

        // Ensure presence.

        searchResultsPage.queryTab();
    }

    //TODO: uncomment when implementation is complete
    //@Test
    public void quickSearchTarget() throws Exception
    {
        final SearchResultsPage resolvedSearchResultsPage = bookmarkSearch("Plane.position.bounds=m101");
        resolvedSearchResultsPage.verifyGridHeaderLabelHasIntegerValue(true);
    }

    //TODO: uncomment when implementation is complete
    //@Test
    public void quickSearchCollection() throws Exception
    {
        final SearchResultsPage collectionSearchResultsPage = bookmarkSearch("collection=IRIS");
        collectionSearchResultsPage.verifyGridHeaderLabelHasIntegerValue(true);
    }

    private SearchResultsPage bookmarkSearch(final String requestQuery) throws Exception
    {
        final SearchResultsPage searchResultsPage = goTo(webURL, "", requestQuery, SearchResultsPage.class);

        searchResultsPage.verifyGridHeaderLabelHasIntegerValue(true);

        return searchResultsPage;
    }
}
