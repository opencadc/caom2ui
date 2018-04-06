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
 * 1/8/13 - 1:46 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.AbstractUnitTest;


import ca.nrc.cadc.caom2.NumericSearch;
import ca.nrc.cadc.caom2.SearchTemplate;
import ca.nrc.cadc.caom2.SpatialSearch;
import ca.nrc.cadc.caom2.TextSearch;
import ca.nrc.cadc.search.form.SearchableFormConstraint;
import ca.nrc.cadc.search.form.Shape1;
import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.easymock.EasyMock.*;


public class TemplatesTest extends AbstractUnitTest<Templates>
{
    private final static List<SearchTemplate> SEARCH_TEMPLATES =
            new ArrayList<SearchTemplate>();

    @Test
    public void getSearchTemplatesByClass() throws Exception
    {
        setTestSubject(new Templates(new ArrayList<SearchableFormConstraint>())
        {
            /**
             * Returns an array of SearchTemplates.
             *
             * @return Array of SearchTemplates.
             */
            @Override
            public List<SearchTemplate> getSearchTemplates()
            {
                return SEARCH_TEMPLATES;
            }
        });

        final List<SpatialSearch> TEST1 =
                getTestSubject().getSearchTemplates(SpatialSearch.class);

        assertTrue("Should be empty.", TEST1.isEmpty());

        //
        // TEST 2
        //
        SEARCH_TEMPLATES.add(new TextSearch(""));
        SEARCH_TEMPLATES.add(new TextSearch(""));
        SEARCH_TEMPLATES.add(new NumericSearch("", 1.0d));

        final List<SpatialSearch> TEST2 =
                getTestSubject().getSearchTemplates(SpatialSearch.class);

        assertTrue("Should be empty.", TEST2.isEmpty());

        //
        // TEST 3
        //
        SEARCH_TEMPLATES.clear();
        SEARCH_TEMPLATES.add(new TextSearch(""));
        SEARCH_TEMPLATES.add(new TextSearch(""));
        SEARCH_TEMPLATES.add(new NumericSearch("", 1.0d));

        final List<NumericSearch> TEST3 =
                getTestSubject().getSearchTemplates(NumericSearch.class);

        assertEquals("Should have one item.", 1, TEST3.size());

        final List<TextSearch> TEST4 =
                getTestSubject().getSearchTemplates(TextSearch.class);

        assertEquals("Should have two items.", 2, TEST4.size());
    }

    @Test
    public void constructWithUnresolvedShape()
    {
        final List<SearchableFormConstraint> constraints = new ArrayList<>();
        final Job mockJob = createMock(Job.class);
        final List<Parameter> jobParameters = new ArrayList<>();

        jobParameters.add(new Parameter("Plane.position.bounds@Shape1.value", "MYVAL"));

        expect(mockJob.getParameterList()).andReturn(jobParameters).once();

        replay(mockJob);

        constraints.add(new Shape1(mockJob, "Plane.position.bounds"));

        verify(mockJob);

        setTestSubject(new Templates(constraints));

        assertEquals("Should transform to target name search.",
                     "TextSearch[Observation.target.name,MYVAL,MYVAL,false,true]",
                     getTestSubject().getSearchTemplates().get(0).toString());
    }

    @Test
    public void constructWithUnresolvedObsCoreShape()
    {
        final List<SearchableFormConstraint> constraints = new ArrayList<>();
        final Job mockJob = createMock(Job.class);
        final List<Parameter> jobParameters = new ArrayList<>();

        jobParameters.add(new Parameter("Char.SpatialAxis.Coverage.Support.Area@Shape1.value", "MYVAL"));

        expect(mockJob.getParameterList()).andReturn(jobParameters).once();

        replay(mockJob);

        constraints.add(new Shape1(mockJob, "Char.SpatialAxis.Coverage.Support.Area"));

        verify(mockJob);

        setTestSubject(new Templates(constraints));

        assertEquals("Should transform to target name search.",
                     "TextSearch[Target.Name,MYVAL,MYVAL,false,true]",
                     getTestSubject().getSearchTemplates().get(0).toString());
    }
}
