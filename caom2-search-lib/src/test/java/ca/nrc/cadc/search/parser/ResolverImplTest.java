/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.parser;


import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.nameresolver.NameResolver;
import ca.nrc.cadc.search.nameresolver.NameResolverData;
import ca.nrc.cadc.search.nameresolver.exception.TargetNotFoundException;
import ca.nrc.cadc.search.parser.resolver.ResolverImpl;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * @author jburke
 */
public class ResolverImplTest extends AbstractUnitTest<ResolverImpl> {

    private final NameResolver mockNameResolverClient = mock(NameResolver.class);

    @Test
    public final void resolveTargetWithRadius() throws Exception {
        final NameResolverData nameResolverData = new NameResolverData(88.0d, -88.0d, "M4", "COORDSYS1", "SERVICE1",
                                                                       "ONAME", "OTYPE", "MTYPE", -1);
        when(mockNameResolverClient.resolve("M4 0.5", "SIMBAD", false, true)).thenThrow(new TargetNotFoundException());
        when(mockNameResolverClient.resolve("M4", "SIMBAD", false, true)).thenReturn(nameResolverData);

        testSubject = new ResolverImpl(mockNameResolverClient);

        final TargetData targetData1 = testSubject.resolveTarget("M4 0.5", "SIMBAD");

        assertEquals("Radius is wrong.", 0.5d, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(mockNameResolverClient, times(1)).resolve("M4 0.5", "SIMBAD", false, true);
        verify(mockNameResolverClient, times(1)).resolve("M4", "SIMBAD", false, true);
    }

    @Test
    public final void resolveTargetWithSpaces() throws Exception {
        final NameResolverData nameResolverData = new NameResolverData(88.0d, -88.0d, "M4", "COORDSYS1", "SERVICE1",
                                                                       "ONAME", "OTYPE", "MTYPE", -1);

        when(mockNameResolverClient.resolve("zeta Aurora", "SIMBAD", false, true)).thenReturn(nameResolverData);

        testSubject = new ResolverImpl(mockNameResolverClient);

        final TargetData targetData1 = testSubject.resolveTarget("zeta Aurora", "SIMBAD");

        assertEquals("Radius is wrong.", AbstractPositionParser.DEFAULT_RADIUS, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(mockNameResolverClient, times(1)).resolve("zeta Aurora", "SIMBAD", false, true);
    }

    @Test
    public final void resolveTargetWithSpacesAndNumber() throws Exception {
        final NameResolverData nameResolverData = new NameResolverData(88.0d, -88.0d, "M4", "COORDSYS1", "SERVICE1",
                                                                       "ONAME", "OTYPE", "MTYPE", -1);

        when(mockNameResolverClient.resolve("HD 19785", "SIMBAD", false, true)).thenReturn(nameResolverData);

        testSubject = new ResolverImpl(mockNameResolverClient);

        final TargetData targetData1 = testSubject.resolveTarget("HD 19785", "SIMBAD");

        assertEquals("Radius is wrong.", AbstractPositionParser.DEFAULT_RADIUS, targetData1.getRadius(), 0.0d);
        assertEquals("RA is wrong.", 88.0d, targetData1.getRA(), 0.0d);

        verify(mockNameResolverClient, times(1)).resolve("HD 19785", "SIMBAD", false, true);
    }
}
