/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2012.                         (c) 2012.
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
 * 1/27/12 - 9:31 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.form.Date;
import ca.nrc.cadc.search.form.FormConstraint;
import ca.nrc.cadc.search.form.Number;
import ca.nrc.cadc.search.form.SearchableFormConstraint;
import ca.nrc.cadc.search.parser.Operand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.nrc.cadc.uws.Job;
import ca.nrc.cadc.uws.Parameter;

import org.junit.Test;
import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;


/**
 * Test for FormData class.
 */
public class FormDataTest extends AbstractUnitTest<FormData>
{
    @Test
    public void addFrequencyWidthConstraints_Equals() throws Exception
    {
        final Number mockFormConstraint = createMock(Number.class);

        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn(null).once();
        expect(mockFormConstraint.getUpperValue()).andReturn(null).once();
        expect(mockFormConstraint.getFormValue()).andReturn("1.0Hz").once();
        expect(mockFormConstraint.getValue()).andReturn("1.0Hz").once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.EQUALS).
                once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").once();

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints =
                getTestSubject().getFormConstraints();

        assertEquals("One item.", 1, formConstraints.size());
        assertEquals("First item is 1.0Hz Number.", "1.0Hz",
                     formConstraints.get(0).getFormValue());
        assertEquals("First item UType was altered.", "Plane.energy.freqWidth",
                     formConstraints.get(0).getUType());

        verify(mockFormConstraint);


        // TEST 2
        reset(mockFormConstraint);
        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn(null).once();
        expect(mockFormConstraint.getUpperValue()).andReturn(null).once();
        expect(mockFormConstraint.getFormValue()).andReturn("= 1.4MHz").once();
        expect(mockFormConstraint.getValue()).andReturn("1.4MHz").once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.EQUALS).
                once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").once();

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints2 =
                getTestSubject().getFormConstraints();

        assertEquals("One item.", 1, formConstraints2.size());
        assertEquals("First item is 1.4MHz Number.", "1.4MHz",
                     formConstraints2.get(0).getFormValue());
        assertEquals("First item UType was altered.", "Plane.energy.freqWidth",
                     formConstraints2.get(0).getUType());

        verify(mockFormConstraint);


        // TEST 3
        reset(mockFormConstraint);
        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn(null).once();
        expect(mockFormConstraint.getUpperValue()).andReturn(null).once();
        expect(mockFormConstraint.getFormValue()).andReturn("1.0um").times(2);
        expect(mockFormConstraint.getValue()).andReturn("1.0um").once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.EQUALS).
                once();
        expect(mockFormConstraint.hasData()).andReturn(true).once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").once();

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints3 =
                getTestSubject().getFormConstraints();

        assertEquals("One item.", 1, formConstraints3.size());
        assertEquals("First item is 1.0um Number.", "1.0um",
                     formConstraints3.get(0).getFormValue());
        assertEquals("First item UType was not altered.",
                     "Plane.energy.bounds.width",
                     formConstraints3.get(0).getUType());

        verify(mockFormConstraint);
    }

    @Test
    public void addFrequencyWidthConstraints_Range() throws Exception
    {
        final Number mockFormConstraint = createMock(Number.class);

        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn("1.0Hz").once();
        expect(mockFormConstraint.getUpperValue()).andReturn("2.0MHz").once();
        expect(mockFormConstraint.getFormValue()).andReturn("1.0Hz..2.0MHz").
                once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.RANGE).
                once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").once();

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints =
                getTestSubject().getFormConstraints();

        assertEquals("One item.", 1, formConstraints.size());
        assertEquals("First item is 1.0Hz..2.0MHz Number.", "1.0Hz..2.0MHz",
                     formConstraints.get(0).getFormValue());
        assertEquals("First item UType was altered.", "Plane.energy.freqWidth",
                     formConstraints.get(0).getUType());

        verify(mockFormConstraint);


        // TEST 2
        reset(mockFormConstraint);
        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn("1.0m").once();
        expect(mockFormConstraint.getUpperValue()).andReturn("2.0MHz").once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.RANGE).
                once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").times(2);

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints2 =
                getTestSubject().getFormConstraints();

        assertEquals("Two items.", 2, formConstraints2.size());
        assertEquals("First item is >= 1.0m Number.", ">= 1.0m",
                     formConstraints2.get(0).getFormValue());
        assertEquals("First item UType was not altered.",
                     "Plane.energy.bounds.width",
                     formConstraints2.get(0).getUType());
        assertEquals("Second item is <= 2.0MHz Number.", "<= 2.0MHz",
                     formConstraints2.get(1).getFormValue());
        assertEquals("Second item UType was altered.", "Plane.energy.freqWidth",
                     formConstraints2.get(1).getUType());

        verify(mockFormConstraint);


        // TEST 3
        reset(mockFormConstraint);
        setTestSubject(new FormData());

        expect(mockFormConstraint.getLowerValue()).andReturn("1.0Hz").once();
        expect(mockFormConstraint.getUpperValue()).andReturn("2.0m").once();
        expect(mockFormConstraint.getOperand()).andReturn(Operand.RANGE).
                once();
        expect(mockFormConstraint.getUType()).andReturn(
                "Plane.energy.bounds.width").times(2);

        replay(mockFormConstraint);

        getTestSubject().addFrequencyConstraints(mockFormConstraint);
        final List<SearchableFormConstraint> formConstraints3 =
                getTestSubject().getFormConstraints();

        assertEquals("Two items.", 2, formConstraints3.size());
        assertEquals("First item is >= 1.0Hz Number.", ">= 1.0Hz",
                     formConstraints3.get(0).getFormValue());
        assertEquals("First item UType was altered.", "Plane.energy.freqWidth",
                     formConstraints3.get(0).getUType());
        assertEquals("Second item is <= 2.0m Number.", "<= 2.0m",
                     formConstraints3.get(1).getFormValue());
        assertEquals("Second item UType was not altered.",
                     "Plane.energy.bounds.width",
                     formConstraints3.get(1).getUType());

        verify(mockFormConstraint);
    }

    @Test
    public void addEnumeratedFormConstraints() throws Exception
    {
        setTestSubject(new FormData());

        final List<Parameter> parameterList = new ArrayList<Parameter>();
        final Job mockJob = createMock(Job.class);
        final String utype = "Observation.collection";

        parameterList.add(new Parameter("Observation.collection","COLLECTION"));

        expect(mockJob.getParameterList()).andReturn(parameterList).once();

        replay(mockJob);

        getTestSubject().addEnumeratedFormConstraints(mockJob, utype);

        final Collection<FormConstraint> formConstraints =
                getTestSubject().getAllFormConstraints();

        assertEquals("One items.", 1, formConstraints.size());

        verify(mockJob);
    }

    @Test
    public void addFormConstraintWithSpaces() throws Exception
    {
        final String userEnteredValue = " > 1977-11-25";
        final Date formConstraint = new Date(userEnteredValue,
                                             "Plane.time.bounds.samples", null);

        setTestSubject(new FormData());

        getTestSubject().addFormConstraint(formConstraint);
        final List<SearchableFormConstraint> formConstraints =
                getTestSubject().getFormConstraints();

        assertEquals("One item.", 1, formConstraints.size());
        assertEquals("First item should be trimmed Date string '"
                     + userEnteredValue.trim() + "'", userEnteredValue.trim(),
                     formConstraints.get(0).getFormValue());
    }

    @Test
    public void addObservationIntentCalibrationText() throws Exception
    {
        final Job mockJob = createMock(Job.class);

        final List<Parameter> jobParams = new ArrayList<Parameter>();

        jobParams.add(new Parameter(FormConstraint.FORM_NAME,
                                    "Observation.intent@Text"));
        jobParams.add(new Parameter("Observation.intent", "calibration"));

        expect(mockJob.getParameterList()).andReturn(jobParams).times(3);

        replay(mockJob);

        setTestSubject(new FormData(mockJob));

        assertEquals("Form data is wrong.", "calibration",
                     getTestSubject().getFormConstraints().get(0).
                             getFormValue());

        verify(mockJob);
    }

    @Test
    public void addObservationIntentAll() throws Exception
    {
        final Job mockJob = createMock(Job.class);

        final List<Parameter> jobParams = new ArrayList<Parameter>();

        jobParams.add(new Parameter(FormConstraint.FORM_NAME,
                                    "Observation.intent@Text"));
        jobParams.add(new Parameter("Observation.intent", ""));

        expect(mockJob.getParameterList()).andReturn(jobParams).times(3);

        replay(mockJob);

        setTestSubject(new FormData(mockJob));

        assertTrue("Form data should be empty.",
                   getTestSubject().getFormConstraints().isEmpty());

        verify(mockJob);
    }

    @Test
    public void addResolvingPower() throws Exception
    {
        final Job mockJob = createMock(Job.class);

        final List<Parameter> jobParams = new ArrayList<Parameter>();

        jobParams.add(new Parameter(FormConstraint.FORM_NAME,
                                    "Plane.energy.resolvingPower@Number"));
        jobParams.add(new Parameter("Plane.energy.resolvingPower", ">500"));

        expect(mockJob.getParameterList()).andReturn(jobParams).times(3);

        replay(mockJob);

        setTestSubject(new FormData(mockJob));

        assertEquals("Form data should be empty.", "500",
                     ((Number) getTestSubject().getFormConstraints().get(0))
                             .getLowerValue());

        verify(mockJob);
    }
}
