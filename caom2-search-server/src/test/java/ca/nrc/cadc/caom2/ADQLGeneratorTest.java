/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2021.                            (c) 2021.
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
 * 7/18/12 - 10:09 AM
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.caom2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.caom2.types.*;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.search.parser.Operand;
import ca.nrc.cadc.search.parser.Range;

import org.junit.Test;

import static org.junit.Assert.*;


public class ADQLGeneratorTest extends AbstractUnitTest<ADQLGenerator>
{
    @Test
    public void getCAOM2SelectList() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final String selectList2 =
                getTestSubject().getSelectList(
                        "CONCAT(Observation.arg1), "
                        + "Plane.planeID, Plane.position.bounds");

        assertEquals("SELECT list is wrong.",
                     "CONCAT(Observation.arg1), "
                     + "Plane.planeID, Plane.position_bounds", selectList2);

        final String selectList3 =
                getTestSubject().getSelectList(
                        "CONCAT(Observation.arg1%2C''%2C'7'%2C'9'%2CObservation.arg2.2) AS TEST, "
                        + "Plane.planeID, Plane.position.bounds");

        assertEquals("SELECT list is wrong.",
                     "CONCAT(Observation.arg1, '', '7', '9', "
                     + "Observation.arg2_2) AS TEST, "
                     + "Plane.planeID, Plane.position_bounds", selectList3);

        final String selectList4 =
                getTestSubject().getSelectList(
                        "CONCAT(Observation.arg1%2C''%2C'7'%2C'9'%2CObservation.arg2.2) AS TEST, "
                        + "COORD1(CENTROID(Plane.position.bounds)) AS TEST2");

        assertEquals("SELECT list is wrong.",
                     "CONCAT(Observation.arg1, '', '7', '9', "
                     + "Observation.arg2_2) AS TEST, "
                     + "COORD1(CENTROID(Plane.position_bounds)) AS TEST2",
                     selectList4);

        final String selectList5 =
                getTestSubject().getSelectList(
                        "CONCAT(CONCAT(Observation.arg1%2C''%2C'7'%2C'9'%2CObservation.arg2.2)%2CObservation.arg3.3) AS TEST, "
                        + "COORD1(CENTROID(Plane.position.bounds)) AS TEST2");

        assertEquals("SELECT list is wrong.",
                     "CONCAT(CONCAT(Observation.arg1, '', '7', '9', "
                     + "Observation.arg2_2), Observation.arg3_3) AS TEST, "
                     + "COORD1(CENTROID(Plane.position_bounds)) AS TEST2",
                     selectList5);
    }

    @Test
    public void getObsCoreSelectList() throws Exception
    {
        setTestSubject(new ADQLGenerator("ivoa.obscore", null, null, null, null));

        final String selectList =
                getTestSubject().getSelectList(
                        "CONCAT(obs_id), "
                        + "s_region, s_fov");

        assertEquals("SELECT list is wrong.",
                     "CONCAT(obs_id), "
                     + "s_region, s_fov", selectList);
    }

    @Test
    public void toSQLIntervalEnergySearch() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final IntervalSearch intervalSearch1 = new IntervalSearch("TESTIS",
                                                                  88.0d,
                                                                  888.0d, "m");

        try
        {
            getTestSubject().toSQL(intervalSearch1, null, false);
            fail("Should be unknown name for TESTIS");
        }
        catch (IllegalArgumentException e)
        {
            // Good!
        }

        final IntervalSearch intervalSearch =
                new IntervalSearch("Plane.energy.bounds.samples", 88.0d, 888.0d, "m");
        final String sql = getTestSubject().toSQL(intervalSearch, null,
                                                  false);


        System.out.println(sql);
        assertEquals("SQL doesn't match.",
            "Plane.energy_bounds_lower <= 88.0 AND 888.0 <= Plane.energy_bounds_upper",
            sql);

        final IntervalSearch intervalSearch2 =
            new IntervalSearch("Plane.energy.bounds.samples", null, 888.0d, "m");
        final String sql2 = getTestSubject().toSQL(intervalSearch2, null,
            false);
        System.out.println(sql2);
        assertEquals("SQL doesn't match.",
            "Plane.energy_bounds_upper <= 888.0",
            sql2);

    }

    @Test
    public void toSQLIntervalTimeSearch() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final IntervalSearch intervalSearch1 = new IntervalSearch("TESTIS",
                                                                  88.0d,
                                                                  888.0d, "m");

        try
        {
            getTestSubject().toSQL(intervalSearch1, null, false);
            fail("Should be unknown name for TESTIS");
        }
        catch (IllegalArgumentException e)
        {
            // Good!
        }

        final IntervalSearch intervalSearch =
                new IntervalSearch("Plane.time.bounds.samples", 88.0d, 888.0d, "s");
        final String sql = getTestSubject().toSQL(intervalSearch, null, false);
        assertEquals("SQL doesn't match.",
                     "INTERSECTS( INTERVAL( 88.0, 888.0 ), Plane.time_bounds_samples ) = 1",
                     sql);


        final IntervalSearch intervalSearch2 =
                new IntervalSearch("Plane.time.bounds.samples", 88.0d, null, "s");
        final String sql2 = getTestSubject()
                .toSQL(intervalSearch2, null, false);
        assertEquals("SQL doesn't match.",
                     String.format("INTERSECTS( INTERVAL( 88.0, %s ), Plane.time_bounds_samples ) = 1",
                                   Double.MAX_VALUE),
                     sql2);
    }

    @Test
    public void toSQLSpatialSearch() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final SpatialSearch spatialSearch1 =
                new SpatialSearch("TESTSS", new Shape()
                {
                    /**
                     * Obtain the coordinate of the center of this Shape.  Each implementation
                     * will have a unique methodology to calculate the center.
                     *
                     * @return center
                     */
                    @Override
                    public Point getCenter()
                    {
                        return new Point(88.0, 88.0);
                    }

                    /**
                     * Calculate the surface area of this Shape. Each implementation will have
                     * their own unique formulae to calculate the area, some are more
                     * complicated than others.
                     *
                     * @return area
                     */
                    @Override
                    public double getArea()
                    {
                        return 88.0;
                    }

                    /**
                     * Calculate the size of this Shape.  The size is the diameter of the minimum
                     * bounding circle. Each implementation will have
                     * their own unique formulae to calculate the area, some are more
                     * complicated than others.
                     *
                     * @return area
                     */
                    @Override
                    public double getSize()
                    {
                        return 88.0;
                    }
                });

        try
        {
            getTestSubject().toSQL(spatialSearch1, null, false);
            fail("Bad shape instance.");
        }
        catch (IllegalArgumentException e)
        {
            // Good!
        }

        final SpatialSearch spatialSearch2 = new SpatialSearch("TESTSS", new Shape()
        {
            @Override
            public Point getCenter()
            {
                return null;
            }

            @Override
            public double getArea()
            {
                return 0;
            }

            @Override
            public double getSize()
            {
                return 0;
            }
        });

        try
        {
            getTestSubject().toSQL(spatialSearch2, null, false);
            fail("Bad utype.");
        }
        catch (IllegalArgumentException e)
        {
            // Good!
        }

        final SpatialSearch spatialSearch3 =
                new SpatialSearch("Plane.position.bounds",
                                  new Location(new Point(88.0, 88.0)));
        final String sql3 = getTestSubject().toSQL(spatialSearch3, null, false);
        assertEquals("SpatialSearch SQL doesn't match.",
                     "CONTAINS( POINT('ICRS',88.0, 88.0), "
                     + "Plane.position_bounds ) = 1",
                     sql3);
    }

    @Test
    public void toSQLTimestampSearch() throws Exception
    {
        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final java.util.Date lowerDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);

        final java.util.Date upperDate = calendar.getTime();

        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final SearchTemplate timestampSearchTemplate =
                new TimestampSearch("Plane.dataRelease", lowerDate,
                                    upperDate);

        final String queryString =
                getTestSubject().toSQL(timestampSearchTemplate, true);

        assertEquals("Query string is wrong for timestamp.",
                     "Plane.dataRelease IS NOT NULL "
                     + "AND Plane.dataRelease >= '1977-11-25 03:21:00.000' "
                     + "AND Plane.dataRelease <= '1977-12-25 03:21:00.000'",
                     queryString);

        final String queryString2 =
                getTestSubject().toSQL(timestampSearchTemplate, false);

        assertEquals("Query string is wrong for timestamp.",
                     "Plane.dataRelease >= '1977-11-25 03:21:00.000' "
                     + "AND Plane.dataRelease <= '1977-12-25 03:21:00.000'",
                     queryString2);

        // Test 2
        final SearchTemplate timestampSearchTemplate2 =
                new TimestampSearch("Plane.dataRelease", lowerDate, null);

        final String queryString3 =
                getTestSubject().toSQL(timestampSearchTemplate2, false);

        assertEquals("Query string is wrong for timestamp.",
                     "Plane.dataRelease >= '1977-11-25 03:21:00.000'",
                     queryString3);
    }

    @Test
    public void toSQLRangeSearch() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", null, null, null, null));

        final Range<Double> raRange = new Range<>("110..115", null, 110d, 115d,
                                                        Operand.RANGE);
        final Range<Double> decRange = new Range<>("-10..25", null, -10d, 25d,
                                                         Operand.RANGE);

        final RangeSearch rangeSearch1 =
                new RangeSearch<>(null, raRange, decRange);
        try
        {
            getTestSubject().toSQL(rangeSearch1, null, false);
            fail("Null name for instance.");
        }
        catch (IllegalArgumentException ignore)
        {
        }

        final RangeSearch rangeSearch2 =
                new RangeSearch<>("TESTSS", raRange, decRange);
        try
        {
            getTestSubject().toSQL(rangeSearch2, null, false);
            fail("Bad utype.");
        }
        catch (IllegalArgumentException ignore)
        {
        }

        final RangeSearch rangeSearch3 = new RangeSearch<>("Plane.position.bounds",
                                                                 raRange, decRange);
        final String sql = getTestSubject().toSQL(rangeSearch3, null, false);
        assertEquals("RangeSearch SQL doesn't match.",
                     "INTERSECTS( RANGE_S2D(110.0, 115.0, -10.0, 25.0), "
                     + "Plane.position_bounds ) = 1", sql);
    }

    @Test
    public void getFromWithUploadCAOM() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", "search_upload,param:targetList",
                                         "ALL", "target_name", "position_bounds"));

        final String fromClause = getTestSubject().getFrom(Plane.class, 2);

        assertEquals("From clause is wrong.",
                     "caom2.Plane AS Plane "
                     + "JOIN TAP_UPLOAD.search_upload as Upload on INTERSECTS(Upload.position, Plane.position_bounds) = 1 "
                     + "JOIN caom2.Observation AS Observation ON Plane.obsID = Observation.obsID",
                     fromClause);
    }

    @Test
    public void getFromWithUploadObsCore() throws Exception
    {
        setTestSubject(new ADQLGenerator("ivoa.obscore",
                                         "search_upload,param:targetList",
                                         "ALL", "target_name", "s_fov"));

        final String fromClause = getTestSubject().getFrom(Plane.class, 2);

        assertEquals("From clause is wrong.",
                     "ivoa.obscore.Plane AS Plane "
                     + "JOIN TAP_UPLOAD.search_upload as Upload on INTERSECTS(Upload.position, Plane.s_fov) = 1 "
                     + "JOIN ivoa.obscore.Observation AS Observation ON Plane.obsID = Observation.obsID",
                     fromClause);
    }

    @Test
    public void getWhere() throws Exception
    {
        setTestSubject(new ADQLGenerator("caom2", "search_upload,param:targetList",
                                         "ALL", "target_name", "position_bounds"));

        final Calendar calendar = Calendar.getInstance(DateUtil.UTC);
        calendar.set(1977, Calendar.NOVEMBER, 25, 3, 21, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final java.util.Date lowerDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);

        final java.util.Date upperDate = calendar.getTime();
        final SearchTemplate timestampSearchTemplate =
                new TimestampSearch("Plane.dataRelease", lowerDate,
                                    upperDate);

        final SearchTemplate numberTemplate =
                new NumericSearch("Plane.energy.resolvingPower", 5000, null);

        final List<SearchTemplate> templateList = new ArrayList<>(2);
        templateList.add(timestampSearchTemplate);
        templateList.add(numberTemplate);

        final String whereClause = getTestSubject().getWhere(templateList);

        assertEquals("Wrong where clause.",
                     " ( Plane.dataRelease >= '1977-11-25 03:21:00.000' AND Plane.dataRelease <= '1977-12-25 03:21:00.000' "
                     + "AND  Plane.energy_resolvingPower >= 5000 ) ",
                     whereClause);
    }
}
