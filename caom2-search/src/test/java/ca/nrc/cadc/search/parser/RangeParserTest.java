/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.nrc.cadc.search.parser;

import org.junit.Test;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.search.parser.exception.RangeParserException;

import static org.junit.Assert.*;


/**
 * @author jburke
 */
public class RangeParserTest extends AbstractUnitTest<RangeParser>
{
    public RangeParserTest()
    {
    }

    /**
     * Test of parse method, of class RangeParser.
     */
    @Test
    public void parseNullString() throws Exception
    {
        setTestSubject(new RangeParser(null, null));

        try
        {
            getTestSubject().parse();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseEmptyString() throws Exception
    {
        setTestSubject(new RangeParser("", null));

        try
        {
            getTestSubject().parse();
            fail("empty range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseSingleValue() throws Exception
    {
        setTestSubject(new RangeParser("100", null));

        try
        {
            getTestSubject().parse();
            fail("no range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }

    }

    @Test
    public void parseRangeValues() throws Exception
    {
        setTestSubject(new RangeParser("1..100", null));

        final Range<String> range1 = getTestSubject().parse();

        assertNotNull(range1);
        assertNull(range1.getValue());
        assertNotNull(range1.getRange());
        assertNotNull(range1.getLowerValue());
        assertNotNull(range1.getUpperValue());
        assertNotNull(range1.getOperand());
        assertEquals("1..100", range1.getRange());
        assertEquals("1", range1.getLowerValue());
        assertEquals("100", range1.getUpperValue());
        assertEquals(Operand.RANGE, range1.getOperand());

        setTestSubject(new RangeParser("<100", null));

        final Range<String> range2 = getTestSubject().parse();

        assertNotNull(range2);
        assertNull(range2.getValue());
        assertNull(range2.getLowerValue());
        assertNotNull(range2.getRange());
        assertNotNull(range2.getUpperValue());
        assertNotNull(range2.getOperand());
        assertEquals("<100", range2.getRange());
        assertEquals("100", range2.getUpperValue());
        assertEquals(Operand.LESS_THAN, range2.getOperand());


        setTestSubject(new RangeParser("<=100", null));

        final Range<String> range3 = getTestSubject().parse();

        assertNotNull(range3);
        assertNull(range3.getValue());
        assertNull(range3.getLowerValue());
        assertNotNull(range3.getRange());
        assertNotNull(range3.getUpperValue());
        assertNotNull(range3.getOperand());
        assertEquals("<=100", range3.getRange());
        assertEquals("100", range3.getUpperValue());
        assertEquals(Operand.LESS_THAN_EQUALS, range3.getOperand());


        setTestSubject(new RangeParser(">100", null));

        final Range<String> range4 = getTestSubject().parse();

        assertNotNull(range4);
        assertNull(range4.getValue());
        assertNull(range4.getUpperValue());
        assertNotNull(range4.getRange());
        assertNotNull(range4.getLowerValue());
        assertNotNull(range4.getOperand());
        assertEquals(">100", range4.getRange());
        assertEquals("100", range4.getLowerValue());
        assertEquals(Operand.GREATER_THAN, range4.getOperand());


        setTestSubject(new RangeParser(">=100", null));

        final Range<String> range5 = getTestSubject().parse();

        assertNotNull(range5);
        assertNull(range5.getValue());
        assertNull(range5.getUpperValue());
        assertNotNull(range5.getRange());
        assertNotNull(range5.getLowerValue());
        assertNotNull(range5.getOperand());
        assertEquals(">=100", range5.getRange());
        assertEquals("100", range5.getLowerValue());
        assertEquals(Operand.GREATER_THAN_EQUALS, range5.getOperand());


        setTestSubject(new RangeParser("=100", null));

        final Range<String> range6 = getTestSubject().parse();

        assertNotNull(range6);
        assertNull(range6.getLowerValue());
        assertNull(range6.getUpperValue());
        assertNotNull(range6.getRange());
        assertNotNull(range6.getValue());
        assertNotNull(range6.getOperand());
        assertEquals("=100", range6.getRange());
        assertEquals("100", range6.getValue());
        assertEquals(Operand.EQUALS, range6.getOperand());
    }

    @Test
    public void parseStringWithOperandNullRange() throws Exception
    {
        setTestSubject(new RangeParser(null, Operand.EQUALS));

        try
        {
            getTestSubject().parse();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseStringWithNullOperandNullRange() throws Exception
    {
        setTestSubject(new RangeParser(null, null));

        try
        {
            getTestSubject().parse();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseStringWithMismatchedRange() throws Exception
    {
        setTestSubject(new RangeParser("1..100", Operand.EQUALS));

        try
        {
            getTestSubject().parse();
            fail("operand mismatch should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseRangeStringWithOperand() throws Exception
    {
        setTestSubject(new RangeParser("1..100", Operand.RANGE));

        final Range<String> range = getTestSubject().parse();

        assertNotNull(range);
        assertNull(range.getValue());
        assertNotNull(range.getRange());
        assertNotNull(range.getLowerValue());
        assertNotNull(range.getUpperValue());
        assertNotNull(range.getOperand());
        assertEquals("1..100", range.getRange());
        assertEquals("1", range.getLowerValue());
        assertEquals("100", range.getUpperValue());
        assertEquals(Operand.RANGE, range.getOperand());
    }

    /**
     * Test of parseDouble method, of class RangeParser.
     */
    @Test
    public void parseNullDouble() throws Exception
    {
        setTestSubject(new RangeParser(null, null));

        try
        {
            getTestSubject().parseDouble();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseEmptyDouble() throws Exception
    {
        setTestSubject(new RangeParser("", null));

        try
        {
            getTestSubject().parseDouble();
            fail("empty range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseRangelessDouble() throws Exception
    {
        setTestSubject(new RangeParser("100", null));

        try
        {
            getTestSubject().parseDouble();
            fail("no range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseRangelessDouble2() throws Exception
    {
        setTestSubject(new RangeParser("100=", null));

        try
        {
            getTestSubject().parseDouble();
            fail("invalid range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void parseRangeDouble() throws Exception
    {
        setTestSubject(new RangeParser("1..100", null));
        final Range<Double> range = getTestSubject().parseDouble();

        assertNotNull(range);
        assertNull(range.getValue());
        assertNotNull(range.getRange());
        assertNotNull(range.getLowerValue());
        assertNotNull(range.getUpperValue());
        assertNotNull(range.getOperand());
        assertEquals("1..100", range.getRange());
        assertEquals(1, range.getLowerValue(), 0.0);
        assertEquals(100, range.getUpperValue(), 0.0);
        assertEquals(Operand.RANGE, range.getOperand());

        setTestSubject(new RangeParser("<100", null));
        final Range<Double> range1 = getTestSubject().parseDouble();
        assertNotNull(range1);
        assertNull(range1.getValue());
        assertNull(range1.getLowerValue());
        assertNotNull(range1.getRange());
        assertNotNull(range1.getUpperValue());
        assertNotNull(range1.getOperand());
        assertEquals("<100", range1.getRange());
        assertEquals(100, range1.getUpperValue(), 0.0);
        assertEquals(Operand.LESS_THAN, range1.getOperand());

        setTestSubject(new RangeParser("<=100", null));
        final Range<Double> range2 = getTestSubject().parseDouble();
        assertNotNull(range2);
        assertNull(range2.getValue());
        assertNull(range2.getLowerValue());
        assertNotNull(range2.getRange());
        assertNotNull(range2.getUpperValue());
        assertNotNull(range2.getOperand());
        assertEquals("<=100", range2.getRange());
        assertEquals(100, range2.getUpperValue(), 0.0);
        assertEquals(Operand.LESS_THAN_EQUALS, range2.getOperand());

        setTestSubject(new RangeParser(">100", null));
        final Range<Double> range3 = getTestSubject().parseDouble();
        assertNotNull(range3);
        assertNull(range3.getValue());
        assertNull(range3.getUpperValue());
        assertNotNull(range3.getRange());
        assertNotNull(range3.getLowerValue());
        assertNotNull(range3.getOperand());
        assertEquals(">100", range3.getRange());
        assertEquals(100, range3.getLowerValue(), 0.0);
        assertEquals(Operand.GREATER_THAN, range3.getOperand());

        setTestSubject(new RangeParser(">=100", null));
        final Range<Double> range4 = getTestSubject().parseDouble();
        assertNotNull(range4);
        assertNull(range4.getValue());
        assertNull(range4.getUpperValue());
        assertNotNull(range4.getRange());
        assertNotNull(range4.getLowerValue());
        assertNotNull(range4.getOperand());
        assertEquals(">=100", range4.getRange());
        assertEquals(100, range4.getLowerValue(), 0.0);
        assertEquals(Operand.GREATER_THAN_EQUALS, range4.getOperand());

        setTestSubject(new RangeParser("=100", null));
        final Range<Double> range5 = getTestSubject().parseDouble();
        assertNotNull(range5);
        assertNull(range5.getLowerValue());
        assertNull(range5.getUpperValue());
        assertNotNull(range5.getRange());
        assertNotNull(range5.getValue());
        assertNotNull(range5.getOperand());
        assertEquals("=100", range5.getRange());
        assertEquals(100, range5.getValue(), 0.0);
        assertEquals(Operand.EQUALS, range5.getOperand());
    }

    /**
     * Test of parseDouble method, of class RangeParser.
     */
    @Test
    public void testParseDoubleWithOperandNullRange() throws Exception
    {
        try
        {
            setTestSubject(new RangeParser(null, Operand.EQUALS));
            getTestSubject().parseDouble();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void testParseDoubleWithOperandNullOperand() throws Exception
    {
        try
        {
            setTestSubject(new RangeParser(null, null));
            getTestSubject().parseDouble();
            fail("null range should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void testParseDoubleWithOperandMismatchedOperand() throws Exception
    {
        try
        {
            setTestSubject(new RangeParser("1..100", Operand.EQUALS));
            getTestSubject().parseDouble();
            fail("operand mismatch should throw RangeParserException");
        }
        catch (RangeParserException ignore)
        {
        }
    }

    @Test
    public void testParseDoubleWithOperand() throws Exception
    {
        setTestSubject(new RangeParser("1..100", Operand.RANGE));
        final Range<Double> range = getTestSubject().parseDouble();

        assertNotNull(range);
        assertNull(range.getValue());
        assertNotNull(range.getRange());
        assertNotNull(range.getLowerValue());
        assertNotNull(range.getUpperValue());
        assertNotNull(range.getOperand());
        assertEquals("1..100", range.getRange());
        assertEquals(1, range.getLowerValue(), 0.0);
        assertEquals(100, range.getUpperValue(), 0.0);
        assertEquals(Operand.RANGE, range.getOperand());
    }

    /**
     * Test of trimRange method, of class RangeParser.
     */
    @Test
    public void testTrimRange()
    {
        String actual = RangeParser.trimRange(null);
        assertNull(actual);

        actual = RangeParser.trimRange("");
        assertNotNull(actual);
        assertEquals("", actual);

        actual = RangeParser.trimRange("");
        assertNotNull(actual);
        assertEquals("", actual);

        actual = RangeParser.trimRange("1..100");
        assertNotNull(actual);
        assertEquals("1..100", actual);

        actual = RangeParser.trimRange("1 ..100");
        assertNotNull(actual);
        assertEquals("1..100", actual);

        actual = RangeParser.trimRange("1.. 100");
        assertNotNull(actual);
        assertEquals("1..100", actual);

        actual = RangeParser.trimRange("1 .. 100");
        assertNotNull(actual);
        assertEquals("1..100", actual);

        actual = RangeParser.trimRange("0.1..0.2");
        assertNotNull(actual);
        assertEquals("0.1..0.2", actual);

        actual = RangeParser.trimRange("0.1 ..0.2");
        assertNotNull(actual);
        assertEquals("0.1..0.2", actual);

        actual = RangeParser.trimRange("0.1.. 0.2");
        assertNotNull(actual);
        assertEquals("0.1..0.2", actual);

        actual = RangeParser.trimRange("0.1 .. 0.2");
        assertNotNull(actual);
        assertEquals("0.1..0.2", actual);

        actual = RangeParser.trimRange(".1...2");
        assertNotNull(actual);
        assertEquals(".1...2", actual);

        actual = RangeParser.trimRange(".1 ...2");
        assertNotNull(actual);
        assertEquals(".1...2", actual);

        actual = RangeParser.trimRange(".1.. .2");
        assertNotNull(actual);
        assertEquals(".1...2", actual);

        actual = RangeParser.trimRange(".1 .. .2");
        assertNotNull(actual);
        assertEquals(".1...2", actual);
    }

    /**
     * Test of parseOperand method, of class RangeParser.
     */
    @Test
    public void testParseOperand()
    {
        setTestSubject(new RangeParser(null, null));
        Operand operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("100=", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("100<", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("100<=", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("100>", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("100>=", null));
        operand = getTestSubject().parseOperand();
        assertNull(operand);

        setTestSubject(new RangeParser("1..100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.RANGE, operand);

        setTestSubject(new RangeParser("1 .. 100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.RANGE, operand);

        setTestSubject(new RangeParser("=100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.EQUALS, operand);

        setTestSubject(new RangeParser("<100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.LESS_THAN, operand);

        setTestSubject(new RangeParser("<=100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.LESS_THAN_EQUALS, operand);

        setTestSubject(new RangeParser(">100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.GREATER_THAN, operand);

        setTestSubject(new RangeParser(">=100", null));
        operand = getTestSubject().parseOperand();
        assertNotNull(operand);
        assertEquals(Operand.GREATER_THAN_EQUALS, operand);
    }

}
