package ca.nrc.cadc.search.integration;

import org.junit.Test;

public class PreviewsPageBrowserTest extends AbstractAdvancedSearchIntegrationTest
{
    @Test
    public void test() throws Exception
    {
        final SearchResultsPage searchResultsPage =
                goTo("/search/",
                     "Observation.observationID=2007420&Observation.collection=CFHT&Plane.calibrationLevel=1",
                     SearchResultsPage.class);

        searchResultsPage.clickPreview("_PREVIEW", UnauthorizedPreviewPage.class);
    }
}
