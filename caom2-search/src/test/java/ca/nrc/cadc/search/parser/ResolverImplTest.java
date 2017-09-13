/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.parser;


import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.parser.resolver.ResolverImpl;
import ca.nrc.cadc.search.parser.resolver.TargetNameResolverClient;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


/**
 * @author jburke
 */
public class ResolverImplTest extends AbstractUnitTest<Resolver>
{
    private final TargetNameResolverClient mockNameResolverClient =
            createMock(TargetNameResolverClient.class);

    @Test
    public final void resolveTargetWithRadius() throws Exception
    {
        final TargetData targetData =
                new TargetData("M4", 88.0d, null, -88.0d, null, null,
                               "COORDSYS1", "SERVICE1", -1, "ONAME",
                               "OTYPE", "MTYPE");

        expect(getMockNameResolverClient().resolve("M4 0.5", "SIMBAD")).
                andReturn(null).once();
        expect(getMockNameResolverClient().resolve("M4", "SIMBAD")).
                andReturn(targetData).once();

        replay(getMockNameResolverClient());

        setTestSubject(new ResolverImpl(getMockNameResolverClient()));

        final TargetData targetData1 =
                getTestSubject().resolveTarget("M4 0.5", "SIMBAD");

        assertEquals("Radius is wrong.", 0.5d, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(getMockNameResolverClient());
    }

    @Test
    public final void resolveTargetWithSpaces() throws Exception
    {
        final TargetData targetData =
                new TargetData("zeta Aurora", 88.0d, null, -88.0d, null, null,
                               "COORDSYS1", "SERVICE1", -1, "", "", "");

        expect(getMockNameResolverClient().resolve("zeta Aurora", "SIMBAD")).
                andReturn(targetData).once();

        replay(getMockNameResolverClient());

        setTestSubject(new ResolverImpl(getMockNameResolverClient()));

        final TargetData targetData1 =
                getTestSubject().resolveTarget("zeta Aurora", "SIMBAD");

        assertEquals("Radius is wrong.", 0.0d, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(getMockNameResolverClient());
    }

    @Test
    public final void resolveTargetWithSpacesAndNumber() throws Exception
    {
        final TargetData targetData =
                new TargetData("HD 19785", 88.0d, null, -88.0d, null, null,
                               "COORDSYS1", "SERVICE1", -1, "", "", "");

        expect(getMockNameResolverClient().resolve("HD 19785", "SIMBAD"))
                .andReturn(targetData).once();

        replay(getMockNameResolverClient());

        setTestSubject(new ResolverImpl(getMockNameResolverClient()));

        final TargetData targetData1 =
                getTestSubject().resolveTarget("HD 19785", "SIMBAD");

        assertEquals("Radius is wrong.", 0.0d, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(getMockNameResolverClient());
    }


    public TargetNameResolverClient getMockNameResolverClient()
    {
        return mockNameResolverClient;
    }
}
