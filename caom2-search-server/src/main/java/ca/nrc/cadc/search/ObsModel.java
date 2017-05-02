/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2008.                            (c) 2008.
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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc.search;

import ca.nrc.cadc.util.StringUtil;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jburke
 */
public abstract class ObsModel
{
     // Map of ObsCore utype to column name.
    private static final Map<String, String> OBS_CORE_UTYPE_NAMES = new TreeMap<>();
    static
    {
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpatialAxis.Coverage.Bounds.Extent.diameter", "s_fov");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpatialAxis.Coverage.Support.Area", "s_region");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpatialAxis.Resolution.refval.value", "s_resolution");
        OBS_CORE_UTYPE_NAMES.put("Char.SpatialAxis.numBins1", "s_xel1");
        OBS_CORE_UTYPE_NAMES.put("Char.SpatialAxis.numBins2", "s_xel2");
        OBS_CORE_UTYPE_NAMES
                .put("Char.TimeAxis.Coverage.Bounds.Limits.StartTime", "t_min");
        OBS_CORE_UTYPE_NAMES
                .put("Char.TimeAxis.Coverage.Bounds.Limits.StopTime", "t_max");
        OBS_CORE_UTYPE_NAMES.put("Curation.PublisherDID", "obs_publisher_did");
        OBS_CORE_UTYPE_NAMES.put("DataID.Collection", "obs_collection");
        OBS_CORE_UTYPE_NAMES
                .put("Provenance.ObsConfig.Facility.name", "facility_name");
        OBS_CORE_UTYPE_NAMES
                .put("Provenance.ObsConfig.Instrument.name", "instrument_name");
        OBS_CORE_UTYPE_NAMES.put("DataID.observationID", "obs_id");
        OBS_CORE_UTYPE_NAMES
                .put("ObsDataset.dataProductType", "dataproduct_type");
        OBS_CORE_UTYPE_NAMES.put("ObsDataset.calibLevel", "calib_level");
        OBS_CORE_UTYPE_NAMES.put("Curation.releaseDate", "obs_release_date");
        OBS_CORE_UTYPE_NAMES.put("Target.Name", "target_name");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1", "s_ra");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2", "s_dec");
        OBS_CORE_UTYPE_NAMES
                .put("Char.TimeAxis.Coverage.Support.Extent", "t_exptime");
        OBS_CORE_UTYPE_NAMES
                .put("Char.TimeAxis.Resolution.refval.value", "t_resolution");
        OBS_CORE_UTYPE_NAMES.put("Char.TimeAxis.numBins", "t_xel");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit", "em_min");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit", "em_max");
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpectralAxis.Resolution.ResolPower.refval", "em_res_power");
        OBS_CORE_UTYPE_NAMES.put("Char.SpectralAxis.numBins", "em_xel");
        OBS_CORE_UTYPE_NAMES.put("Char.SpectralAxis.ucd", "em_ucd");
        OBS_CORE_UTYPE_NAMES
                .put("Char.PolarizationAxis.stateList", "pol_states");
        OBS_CORE_UTYPE_NAMES.put("Char.PolarizationAxis.numBins", "pol_xel");
        OBS_CORE_UTYPE_NAMES.put("Char.ObservableAxis.ucd", "o_ucd");
        OBS_CORE_UTYPE_NAMES.put("Access.Reference", "access_url");
        OBS_CORE_UTYPE_NAMES.put("Access.Format", "access_format");
        OBS_CORE_UTYPE_NAMES.put("Access.Size", "access_estsize");

        // For em_coverage and t_coverage fields.
        OBS_CORE_UTYPE_NAMES
                .put("Char.SpectralAxis.Coverage.Bounds.Limits", "em_coverage");
        OBS_CORE_UTYPE_NAMES
                .put("Char.TimeAxis.Coverage.Bounds.Limits", "t_coverage");
    }


    public static String getObsCoreName(final String _columnUType)
    {
        return OBS_CORE_UTYPE_NAMES.get(_columnUType);
    }

    public static boolean isObsCore(final String _uType)
    {
        return OBS_CORE_UTYPE_NAMES.containsKey(_uType);
    }

    /**
     * Possible Interval CAOM attributes, the corresponding CVO
     * database name, default label, and unit.
     */
    public static final String[] INTERVAL = new String[]
    {
        "Plane.energy.bounds",
        "Plane.time.bounds"
    };
    public static final String[] INTERVAL_LABELS = new String[]
    {
        "Spectral Coverage",
        "Observation Date"
    };
    
    /**
     * Possible Number CAOM attributes, the corresponding CVO
     * database name, default label, and unit.
     */
    public static final String[] NUMBER = new String[]
    {
        //CAOM2
        "Observation.telescope.geoLocationX",
        "Observation.telescope.geoLocationy",
        "Observation.telescope.geoLocationZ",
        "Observation.target.redshift",
        "Plane.calibrationLevel",
        "Plane.metrics.value",
        "Plane.position.sampleSize",
        "Plane.position.dimension1",
        "Plane.position.dimension2",
        "Plane.position.bounds.center.cval1",
        "Plane.position.bounds.center.cval2",
        "Plane.position.bounds.radius",
        "Plane.position.bounds.area",
        "Plane.energy.sampleSize",
        "Plane.energy.resolvingPower",
        "Plane.energy.dimension",
        "Plane.energy.bounds.lower",
        "Plane.energy.bounds.upper",
        "Plane.energy.restwav",
        "Plane.time.sampleSize",
        "Plane.time.dimension",
        "Plane.time.bounds.lower",
        "Plane.time.bounds.upper",
        "Plane.time.bounds.width",
        "Plane.time.exposure",
        "Plane.polarization.dimension",
        "Metric.value",
        "Metric.error",

        // ObsCore

        "Access.Size",
        "ObsDataset.calibLevel",
        "Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1",
        "Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2",
        "Char.SpatialAxis.Coverage.Bounds.Extent.diameter",
        "Char.SpatialAxis.Resolution.refval.value",
        "Char.TimeAxis.Coverage.Bounds.Limits",
        "Char.TimeAxis.Coverage.Bounds.Limits.StopTime",
        "Char.TimeAxis.Coverage.Bounds.Limits.StartTime",
        "Char.TimeAxis.Coverage.Support.Extent",
        "Char.TimeAxis.Resolution.refVal",
        "Char.SpatialAxis.numBins1",
        "Char.SpatialAxis.numBins2",
        "Char.TimeAxis.numBins",
        "Char.SpectralAxis.numBins",
        "Char.PolarizationAxis.numBins"
    };
    public static final String[] NUMBER_LABELS = new String[]
    {
        //CAOM2
        "geolocation x",
        "geolocation y",
        "geolocation z",
        "redshift",
        "Calibration Level",
        "metric",
        "position sample size",
        "position dimension 1",
        "position dimension 2",
        "RA",
        "DEC",
        "radius",
        "area",
        "energy sample size",
        "energy resolving power",
        "energy dimension",
        "lower wavelength",
        "upper wavelength",
        "rest frame spectral coverage",
        "time sample size",
        "time dimension",
        "start time",
        "end time",
        "time span",
        "exposure time",
        "polarization dimension",
        "metric value",
        "metric error",
        //ObsCore
        "access_estsize",
        "calib_level",
        "s_ra",
        "s_dec",
        "s_fov",
        "s_resolution",
        "em_max",
        "em_min",
        "em_res_power",
        "t_max",
        "t_min",
        "t_exptime",
        "t_resolution"
    };
    
    /**
     * Possible Shape CAOM attributes, the corresponding CVO
     * database name, default label, and unit.
     */
    public static final String[] SHAPE = new String[]
    {
        // CAOM2
        "Plane.position.bounds",
        "Plane.position.bounds.center",
        // ObsCore
        "Char.SpatialAxis.Coverage.Support.Area"
    };
    public static final String[] SHAPE_LABELS = new String[]
    {
        // CAOM2
        "Target",
        "Target",
        // ObsCore
        "s_region"
    };
    
    /**
     * Possible Text CAOM attributes, the corresponding CVO
     * database name, default label, and unit.
     */
    public static final String[] TEXT = new String[]
    {
        // CAOM2
        "Observation.collection",
        "Observation.observationID",
        "Observation.project",
        "Observation.proposal.id",
        "Observation.proposal.pi",
        "Observation.proposal.title",
        "Observation.proposal.keywords",
        "Observation.proposal.project",
        "Observation.intent",
        "Observation.telescope.name",
        "Observation.telescope.keywords",
        "Observation.instrument.name",
        "Observation.instrument.keywords",
        "Observation.target.name",
        "Observation.target.classification",
        "Observation.Algorithm.name",
        "Observation.type",
        "Plane.project",
        "Metric.observable.ctype",
        "Metric.observable.cunit",
        "Plane.observable.cunit",
        "Plane.energy.bandpassName",
        "Plane.energy.emBand",
        "Plane.dataProductType",

        // ObsCore
        "Access.Format",
        "Access.Reference",
        "ObsDataset.dataProductType",
        "Provenance.ObsConfig.Facility.name",
        "Provenance.ObsConfig.Instrument.name",
        "DataID.Collection",
        "DataID.observationID",
        "Curation.PublisherDID",
        "Char.ObservableAxis.ucd",
        "Char.PolarizationAxis.stateList",
        "Char.SpatialAxis.Coverage.Support.Area",
        "Target.Name"
    };
    public static final String[] TEXT_LABELS = new String[]
    {
        // CAOM2
        "Collection",
        "Observation ID",
        "Project",
        "Proposal ID",
        "Proposal P.I.",
        "Proposal Title",
        "Proposal Keywords",
        "Proposal Project",
        "Intent",
        "Telescope",
        "Telescope Keywords",
        "Instrument",
        "Instrument Keywords",
        "Target",
        "Target Classification",
        "Algorithm",
        "Observation Type",
        "project (product)",
        "",
        "",
        "product unit",
        "Filter",
        "Band",
        "Data Type",

        // ObsCore
        "access_format",
        "access_url",
        "dataproduct_type",
        "facility_name",
        "instrument_name",
        "obs_collection",
        "obs_id",
        "obs_publisher_did",
        "o_ucd",
        "pol_states",
        "s_region",
        "target_name"
    };
    
    /**
     * Energy and time attributes.
     */
    public static final String[] ANGLE = new String[]
    {
        "Plane.position.bounds.radius",
        "Plane.position.sampleSize"
    };
    public static final String[] ENERGY = new String[]
    {
        // CAOM2
        "Plane.energy.bounds",
        "Plane.energy.bounds.width",
        "Plane.energy.bounds.cval1",
        "Plane.energy.bounds.cval2",
        "Plane.energy.sampleSize",
        "Plane.energy.freqWidth",
        "Plane.energy.freqSampleSize",
        "Plane.energy.restwav",
        // ObsCore
        "Char.SpectralAxis.Coverage.Bounds.Limits",
        "Char.SpectralAxis.Resolution.ResolPower.refval"
    };
    public static final String[] TIME = new String[]
    {
        // CAOM2
        "Plane.time.exposure",
        "Plane.time.bounds.width",
        "Plane.time.sampleSize",
        // ObsCore
        "Char.TimeAxis.Coverage.Support.Extent",
        "Char.TimeAxis.Coverage.Bounds.Limits",
        "Char.TimeAxis.Resolution.refval.value"
    };
    public static final String[] MJD_UTC = new String[]
    {
        // CAOM2
        "Plane.time.bounds",
        "Plane.time.bounds_PRESET",
        "Plane.time.bounds.cval1",
        "Plane.time.bounds.cval2",
        // ObsCore
        "Char.TimeAxis.Coverage.Bounds.Limits",
        "Char.TimeAxis.Coverage.Bounds.Limits_PRESET",
        "Char.TimeAxis.Coverage.Bounds.Limits.StartTime",
        "Char.TimeAxis.Coverage.Bounds.Limits.StartTime_PRESET",
        "Char.TimeAxis.Coverage.Bounds.Limits.StopTime",
        "Char.TimeAxis.Coverage.Bounds.Limits.StopTime_PRESET"
    };
    public static final String[] DATE_UTC = new String[]
    {
        "Plane.metaRelease",
        "Plane.dataRelease",
        "Curation.releaseDate"
    };
    public static final String[] DATE_LOCAL = new String[]
    {
        "Observation.lastModified",
        "Plane.lastModified"
    };

    public static final String[] WILDCARD_UTYPES = new String[]
    {
        // CAOM2
        "Observation.proposal.pi",
        "Observation.proposal.id",
        "Observation.proposal.title",
        "Observation.proposal.keywords", 
        "Observation.instrument.keywords",
        "Observation.telescope.keywords",
        "Observation.target.name",
        // ObsCore
        "Target.Name"
    };

    /**
     * Verifies that the utype is valid by checking each of
     * the Enumerated, Interval, Number, and Shape utype
     * arrays for the utype.
     * 
     * @param utype the utype to verify.
     * @return true if the utype is valid, false otherwise.
     */
    public static boolean isValidUtype(String utype)
    {
        if (!StringUtil.hasText(utype))
        {
            return false;
        }

        for (String interval : INTERVAL)
        {
            if (interval.equalsIgnoreCase(utype))
            {
                return true;
            }
        }

        for (String number : NUMBER)
        {
            if (number.equalsIgnoreCase(utype))
            {
                return true;
            }
        }

        for (String shape : SHAPE)
        {
            if (shape.equalsIgnoreCase(utype))
            {
                return true;
            }
        }

        for (String text : TEXT)
        {
            if (text.equalsIgnoreCase(utype))
            {
                return true;
            }
        }

        return false;
    }

        /**
     * Verifies that the utypes are valid by checking each of
     * the Enumerated, Interval, Number, and Shape utype
     * arrays for each of the utypes.
     * 
     * @param utypes the array of utypes to verify.
     * @return true if all the utypes are valid, false otherwise.
     */
    public static boolean isValidUtypes(String[] utypes)
    {
        if (utypes == null)
        {
            return false;
        }

        for (String utype : utypes)
        {
            if (!isValidUtype(utype))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * For the given utype, return the default label.
     * 
     * @param utype the utype.
     * @return the default label, or an empty string if 
     * the utype doesn't have a default label.
     */
    public static String getUtypeLabel(String utype)
    {
        if (StringUtil.hasText(utype))
        {
            for (int i = 0; i < INTERVAL.length; i++)
            {
                if (INTERVAL[i].equals(utype))
                {
                    return INTERVAL_LABELS[i];
                }
            }
            for (int i = 0; i < NUMBER.length; i++)
            {
                if (NUMBER[i].equals(utype))
                {
                    return NUMBER_LABELS[i];
                }
            }
            for (int i = 0; i < SHAPE.length; i++)
            {
                if (SHAPE[i].equals(utype))
                {
                    return SHAPE_LABELS[i];
                }
            }
            for (int i = 0; i < TEXT.length; i++)
            {
                if (TEXT[i].equals(utype))
                {
                    return TEXT_LABELS[i];
                }
            }
        }
        
        return "";
    }
    
   /**
     * Test if the given utype is a valid energy utype.
     * 
     * @param utype     The utype.
     * @return boolean true if the utype is an energy utype, false otherwise.
     */
    public static boolean isEnergyUtype(final String utype)
    {
        return arrayContains(utype, ENERGY);
    }

    /**
     * Test if the given utype is a valid time utype.
     * 
     * @param utype     The utype.
     * @return boolean true if the utype is a time utype, false otherwise.
     */
    public static boolean isTimeUtype(String utype)
    {
        return arrayContains(utype, TIME);
    }

    /**
     * Test if the given utype is a valid text utype.
     * 
     * @param utype     The utype.
     * @return boolean true if the utype is a text utype, false otherwise.
     */
    public static boolean isTextUtype(String utype)
    {
        return arrayContains(utype, TEXT);
    }

    /**
     * Test if the given utype is a valid number utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a text utype, false otherwise.
     */
    public static boolean isNumberUtype(String utype)
    {
        return arrayContains(utype, NUMBER);
    }

    /**
     * Test if the given utype is a valid angle utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a angle utype, false otherwise.
     */
    public static boolean isAngleUtype(final String utype)
    {
        return arrayContains(utype, ANGLE);
    }

    /**
     * Test if the given utype is a valid wildcard utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a wildcard utype, false otherwise.
     */
    public static boolean isWildcardUtype(String utype)
    {
        return arrayContains(utype, WILDCARD_UTYPES);
    }

    /**
     * Test if the given utype is a valid UTC date utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a date utype, false otherwise.
     */
    public static boolean isUTCDateUtype(String utype)
    {
        return arrayContains(utype, DATE_UTC);
    }

    /**
     * Test if the given utype is a valid local date utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a date utype, false otherwise.
     */
    public static boolean isLocalDateUtype(final String utype)
    {
        return arrayContains(utype, DATE_LOCAL);
    }

        /**
     * Test if the given utype is a valid local date utype.
     *
     * @param utype     The utype.
     * @return boolean true if the utype is a date utype, false otherwise.
     */
    public static boolean isMJDUtype(final String utype)
    {
        return arrayContains(utype, MJD_UTC);
    }

    private static boolean arrayContains(String value, String[] values)
    {
        for (final String val : values)
        {
            if (val.equals(value))
            {
                return true;
            }
        }
        return false;
    }
}
