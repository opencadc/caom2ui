/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
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
 * 8/31/11 - 1:05 PM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.search.form;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import org.junit.Ignore;


public abstract class AbstractFormConstraintTest<F extends FormConstraint>
{
    private F testSubject;
    private FormErrors mockFormErrors = createMock(FormErrors.class);


    @After
    public void tearDown() throws Exception
    {
        setTestSubject(null);
    }

    @Before
    public void setUp() throws Exception
    {
        // empty
    }

    protected F getTestSubject()
    {
        if (testSubject == null)
        {
            throw new RuntimeException("Test Subject has not been set.");
        }

        return testSubject;
    }

    protected void setTestSubject(final F testSubject)
    {
        this.testSubject = testSubject;
    }

    protected FormErrors getFormErrors()
    {
        return mockFormErrors;
    }
}
