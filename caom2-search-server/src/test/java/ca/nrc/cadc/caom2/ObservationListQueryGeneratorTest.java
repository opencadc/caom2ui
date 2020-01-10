
package ca.nrc.cadc.caom2;

import ca.nrc.cadc.AbstractUnitTest;
import ca.nrc.cadc.util.Log4jInit;
import org.apache.log4j.Level;

import org.apache.log4j.Logger;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author pdowler
 */
public class ObservationListQueryGeneratorTest extends AbstractUnitTest<ObservationListQueryGenerator> {

    private static final Logger log = Logger.getLogger(ObservationListQueryGeneratorTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc.caom2", Level.INFO);
    }

    public ObservationListQueryGeneratorTest() {
    }

    @Test
    public void detail1() {
        setTestSubject(new ObservationListQueryGenerator(null, null, null, null));

        String sql;

        log.debug(" IN: Observation");
        sql = getTestSubject().getSelectSQL(new ArrayList<SearchTemplate>(), "A, B").toString();
        log.debug("OUT: " + sql);

        Assert.assertNotNull(sql);
        Assert.assertTrue((sql.length() > 0));
    }

    @Test
    public void detail2() {
        String sql;
        setTestSubject(new ObservationListQueryGenerator(null, null, null, null));

        log.debug(" IN: Observation");
        sql = getTestSubject().getSelectSQL(new ArrayList<SearchTemplate>(), "A, B").toString();
        log.debug("OUT: " + sql);

        Assert.assertNotNull(sql);
        Assert.assertTrue((sql.length() > 0));
    }

    @Test
    public void freqWidthQuery() {
        setTestSubject(
                new ObservationListQueryGenerator(null, null, null, null));

        final List<SearchTemplate> tmpl = new ArrayList<>();
        tmpl.add(new NumericSearch("Plane.energy.freqWidth", 100e6, 200e6)); // 100-200MHz
        tmpl.add(new NumericSearch("Plane.energy.freqSampleSize", 10e6, 20e6)); // 10-20MHz

        log.debug(" IN: Observation");
        final String sql = getTestSubject().getSelectSQL(tmpl, "Observation.previewURL AS Preview, "
                + "Observation.collection AS Collection, "
                + "Observation.observationID AS \"Collection ID\", "
                + "Observation.telescope_name AS Telescope, "
                + "Observation.instrument_name AS Instrument, "
                + "Plane.energy_emBand AS \"EM band\", "
                + "Plane.dataProductType AS \"Data Product Type\", "
                + "Plane.calibrationLevel AS \"Calibration Level\", "
                + "Observation.target_name AS Target, "
                + "x(Plane.position_bounds) AS RA, "
                + "y(Plane.position_bounds) AS DEC, "
                + "Plane.time_exposure AS \"Exposure Time\", "
                + "Plane.energy_bandpassName AS Filter, "
                + "Observation.proposal_id AS \"Proposal ID\", "
                + "Plane.energy_bounds_cval1 AS \"Min Wavelength\", "
                + "Plane.energy_bounds_cval2 AS \"Max Wavelength\", "
                + "Plane.time_bounds_cval1 AS \"Start Time\", "
                + "Plane.time_bounds_cval2 AS \"End Time\", "
                + "Plane.position_bounds AS \"Position Bounds\", "
                + "AREA(Plane.position_bounds) AS Area, "
                + "Plane.position_dimension1 AS \"Position Dimension1\", "
                + "Plane.position_dimension2 AS \"Position Dimension2\", "
                + "Plane.position_sampleSize AS \"Position Sample Size\", "
                + "Plane.energy_dimension AS \"Energy dimension\", "
                + "Plane.energy_sampleSize AS \"Energy sample size\", "
                + "Plane.time_dimension AS \"Time dimension\", "
                + "Plane.time_sampleSize AS \"Time sample size\", "
                + "Plane.polarization AS \"Polarization\", "
                + "Plane.polarization_dimension AS \"Polarization dimension\", "
                + "Observation.proposal_title AS \"Proposal Title\", "
                + "Observation.project AS \"Observation Project\", "
                + "Plane.project AS \"Plane Project\", "
                + "Plane.planeID ").toString();
        log.debug("OUT: " + sql);

        Assert.assertEquals("SQL is wrong.",
                            "SELECT Observation.previewURL AS Preview, "
                                    + "Observation.collection AS Collection, "
                                    + "Observation.observationID AS \"Collection ID\", "
                                    + "Observation.telescope_name AS Telescope, "
                                    + "Observation.instrument_name AS Instrument, "
                                    + "Plane.energy_emBand AS \"EM band\", "
                                    + "Plane.dataProductType AS \"Data Product Type\", "
                                    + "Plane.calibrationLevel AS \"Calibration Level\", "
                                    + "Observation.target_name AS Target, "
                                    + "x(Plane.position_bounds) AS RA, "
                                    + "y(Plane.position_bounds) AS DEC, "
                                    + "Plane.time_exposure AS \"Exposure Time\", "
                                    + "Plane.energy_bandpassName AS Filter, "
                                    + "Observation.proposal_id AS \"Proposal ID\", "
                                    + "Plane.energy_bounds_cval1 AS \"Min Wavelength\", "
                                    + "Plane.energy_bounds_cval2 AS \"Max Wavelength\", "
                                    + "Plane.time_bounds_cval1 AS \"Start Time\", "
                                    + "Plane.time_bounds_cval2 AS \"End Time\", "
                                    + "Plane.position_bounds AS \"Position Bounds\", "
                                    + "AREA(Plane.position_bounds) AS Area, "
                                    + "Plane.position_dimension1 AS \"Position Dimension1\", "
                                    + "Plane.position_dimension2 AS \"Position Dimension2\", "
                                    + "Plane.position_sampleSize AS \"Position Sample Size\", "
                                    + "Plane.energy_dimension AS \"Energy dimension\", "
                                    + "Plane.energy_sampleSize AS \"Energy sample size\", "
                                    + "Plane.time_dimension AS \"Time dimension\", "
                                    + "Plane.time_sampleSize AS \"Time sample size\", "
                                    + "Plane.polarization AS \"Polarization\", "
                                    + "Plane.polarization_dimension AS \"Polarization dimension\", "
                                    + "Observation.proposal_title AS \"Proposal Title\", "
                                    + "Observation.project AS \"Observation Project\", "
                                    + "Plane.project AS \"Plane Project\", "
                                    + "Plane.planeID "
                                    + "FROM caom2.Plane AS Plane "
                                    + "JOIN caom2.Observation AS Observation "
                                    + "ON Plane.obsID = Observation.obsID "
                                    + "WHERE  ( Plane.energy_freqWidth >= 1.0E8 "
                                    + "AND  Plane.energy_freqWidth <= 2.0E8 "
                                    + "AND  Plane.energy_freqSampleSize >= 1.0E7 "
                                    + "AND  Plane.energy_freqSampleSize <= 2.0E7 "
                                    + "AND  ( Plane.quality_flag IS NULL OR Plane.quality_flag != 'junk' ) ) ", sql);
    }
}
