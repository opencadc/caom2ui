package ca.nrc.cadc.search.integration;


import org.junit.Test;

public class QuickSearchBrowserTest extends AbstractAdvancedSearchIntegrationTest
{
    @Test
    public void quickSearchURLPass() throws Exception
    {
        goToApplication("Plane.position.bounds=m101");

        waitForSearch(true, true);

        goToApplication("collection=IRIS");

        waitForSearch(true, true);
    }
}
