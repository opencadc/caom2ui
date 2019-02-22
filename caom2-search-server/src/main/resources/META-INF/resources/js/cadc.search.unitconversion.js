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

;(function($) {
  'use strict'
  // register namespace
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            unitconversion: {
              PREVIEW_URL_PREFIX: 'preview',

              // Month lengths in days.
              mtab: [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],

              // Angle units.
              angle: {
                milliarcsec: {
                  factor: 1.0e3
                },
                arcsec: {
                  factor: 1.0
                },
                arcmin: {
                  factor: 1.0 / 60.0
                },
                deg: {
                  factor: 1.0 / 3600.0
                },
                milliarcsecond: {
                  factor: 1.0e3
                },
                arcsecond: {
                  factor: 1.0
                },
                arcminute: {
                  factor: 1.0 / 60.0
                },
                degrees: {
                  factor: 1.0 / 3600.0
                }
              },

              // Area units.
              area: {
                'millisec**2': {
                  factor: 1.0e6 * 3600 * 3600
                },
                'sec**2': {
                  factor: 3600 * 3600
                },
                'min**2': {
                  factor: 3600.0
                },
                'deg**2': {
                  factor: 1.0
                },
                'millisecond**2': {
                  factor: 1.0e6 * 3600 * 3600
                },
                'second**2': {
                  factor: 3600 * 3600
                },
                'minute**2': {
                  factor: 3600.0
                },
                'degrees**2': {
                  factor: 1.0
                }
              },

              // Temporal units.
              time: {
                s: {
                  factor: 1.0
                },
                sec: {
                  factor: 1.0
                },
                second: {
                  factor: 1.0
                },
                seconds: {
                  factor: 1.0
                },
                m: {
                  factor: Number(1.0 / 60.0)
                },
                min: {
                  factor: Number(1.0 / 60.0)
                },
                minute: {
                  factor: Number(1.0 / 60.0)
                },
                minutes: {
                  factor: Number(1.0 / 60.0)
                },
                h: {
                  factor: Number(1.0 / 3600.0)
                },
                hour: {
                  factor: Number(1.0 / 3600.0)
                },
                hours: {
                  factor: Number(1.0 / 3600.0)
                },
                d: {
                  factor: Number(1.0 / 3600.0 / 24.0)
                },
                day: {
                  factor: Number(1.0 / 3600.0 / 24.0)
                },
                days: {
                  factor: Number(1.0 / 3600.0 / 24.0)
                }
              },

              // Spectral units.
              energy: {
                c: Number(2.997925e8), // m/sec
                h: Number(6.6262e-27), // erg/sec
                eV: Number(1.602192e-12), // erg

                energyUnits: {
                  ev: {
                    factor: Number(1.0)
                  },
                  kev: {
                    factor: Number(1.0e3)
                  },
                  mev: {
                    factor: Number(1.0e6)
                  },
                  gev: {
                    factor: Number(1.0e9)
                  }
                },
                freqUnits: {
                  hz: {
                    factor: Number(1.0)
                  },
                  khz: {
                    factor: Number(1.0e3)
                  },
                  mhz: {
                    factor: Number(1.0e6)
                  },
                  ghz: {
                    factor: Number(1.0e9)
                  }
                },
                waveUnits: {
                  a: {
                    factor: Number(1.0e-10)
                  },
                  nm: {
                    factor: Number(1.0e-9)
                  },
                  um: {
                    factor: Number(1.0e-6)
                  },
                  mm: {
                    factor: Number(1.0e-3)
                  },
                  cm: {
                    factor: Number(1.0e-2)
                  },
                  m: {
                    factor: Number(1.0)
                  }
                }
              },

              // Spatial units.
              position: {
                degUnits: {
                  deg: {},
                  degree: {},
                  degrees: {}
                },
                sexiUnits: {
                  hms: {}, // For RA.
                  dms: {} // For DEC.
                }
              },

              // Date units.
              date: {
                ivoaUnits: {
                  ivoa: {}
                },
                mjdUnits: {
                  mjd: {}
                }
              },
              AngleConverter: AngleConverter,
              AreaConverter: AreaConverter,
              RAConverter: RAConverter,
              DECConverter: DECConverter,
              WavelengthConverter: WavelengthConverter,
              EnergyConverter: EnergyConverter,
              FrequencyConverter: FrequencyConverter,
              TimeConverter: TimeConverter,
              DateConverter: DateConverter,
              MJDConverter: MJDConverter
            }
          }
        }
      }
    }
  })

  /**
   * Sanitize the input value.
   *
   * @param _val    The value to sanitize.
   * @returns {*}   The sanitized value.
   */
  function sanitizeInput(_val) {
    var stringUtil = new cadc.web.util.StringUtil(_val)
    return stringUtil.sanitize()
  }

  /**
   * Format numerical fixation.  This is different from precision.
   *
   * @param value     The number value.
   * @param fixation  The fixation value.
   * @returns {*}
   */
  function formatNumericFixation(value, fixation) {
    var numberFormat = new cadc.web.util.NumberFormat(value, fixation)
    return numberFormat.formatFixation()
  }

  function formatNumericPrecision(value, precision) {
    var numberFormat = new cadc.web.util.NumberFormat(value, precision)
    return numberFormat.formatPrecision()
  }

  /**
   * Converter for DEC values.
   *
   * @param _value    The value, in metres, to convert.
   * @param _toUnit   The target unit of the conversion.
   * @constructor
   */
  function DECConverter(_value, _toUnit) {
    var _selfDECConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfDECConverter.value
    }

    function getToUnit() {
      return _selfDECConverter.toUnit
    }

    /**
     * Convert the DEC value from degrees to sexigesimal.
     */
    function degreesToDEC() {
      var degrees = Number(getValue())
      if (degrees < -90.0 || degrees > 90.0) {
        return Number.NaN
      }

      var sign
      if (degrees < 0.0) {
        sign = '-'
        degrees *= -1.0
      } else {
        sign = '+'
      }

      var deg = Math.floor(degrees)
      degrees -= deg

      // 60 min/deg
      var m = Math.floor(degrees * 60.0)
      degrees -= m / 60.0

      // 60 sec/min == 3600 sec/deg
      degrees *= 3600.0
      //  var d = degrees.toString();

      var dd = deg.toString()
      if (deg < 10) {
        dd = '0' + dd
      }

      var mm = Math.floor(m).toString()
      if (m < 10) {
        mm = '0' + mm
      }

      // for seconds, show 3 chars, which is up to ##.#
      var prependZeroToSecond = degrees < 10
      var s = sign + dd + ':' + mm + ':'

      return (
        s + (prependZeroToSecond ? '0' : '') + formatNumericFixation(degrees, 1)
      )
    }

    /**
     * Convert a DEC between degrees and sexigesimal.
     *
     * @return          Dec value converted, or null if unable to convert.
     */
    function convertDEC() {
      var convertedDec

      if (isSexigesimalToUnit()) {
        convertedDec = degreesToDEC()
      } else {
        convertedDec = formatNumericFixation(getValue(), 6)
      }

      return convertedDec
    }

    /**
     * Test if the unit is a Position DEC unit.
     *
     */
    function isSexigesimalToUnit() {
      return getToUnit() in ca.nrc.cadc.search.unitconversion.position.sexiUnits
    }

    function convert() {
      return convertDEC()
    }

    $.extend(this, {
      convert: convert
    })
  }

  /**
   * Convert from degrees to science notation.
   *
   * @param _value    The value in degrees.
   * @param _toUnit   The unit to convert to.
   * @constructor
   */
  function RAConverter(_value, _toUnit) {
    var _selfRAConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfRAConverter.value
    }

    function getToUnit() {
      return _selfRAConverter.toUnit
    }

    /**
     * Test if the unit is a Position RA unit.
     *
     */
    function isSexigesimalToUnit() {
      return getToUnit() in ca.nrc.cadc.search.unitconversion.position.sexiUnits
    }

    /**
     * Convert the value from degrees to sexigesimal.
     */
    function degreesToRA() {
      var degrees = Number(getValue())
      var returnVal

      if (degrees < 0.0 || degrees >= 360.0) {
        returnVal = Number.NaN
      } else {
        // 24 hours/360 degrees = 15 deg/hour
        var h = Math.floor(degrees / 15.0)
        degrees -= h * 15.0

        // 15 deg/hour == 0.25 deg/min == 4 min/deg
        var m = Math.floor(degrees * 4.0)
        degrees -= m / 4.0

        // 4 min/deg == 240 sec/deg
        degrees *= 240.0

        var hh = h.toString()
        var mm = m.toString()
        if (h < 10) {
          hh = '0' + h
        }
        if (m < 10) {
          mm = '0' + m
        }

        var s = hh + ':' + mm + ':'

        // for seconds, show 4 chars, which is up to ##.##
        var prependZeroToSecond = degrees < 10

        returnVal =
          s +
          (prependZeroToSecond ? '0' : '') +
          formatNumericFixation(degrees, 2)
      }

      return returnVal
    }

    /**
     * Convert a RA between degrees and sexigesimal.
     *
     * @return          RA value converted, empty string, or null if unable to convert.
     */
    function convertRA() {
      var displayValue
      var convertedRA

      if (isSexigesimalToUnit()) {
        convertedRA = degreesToRA()
      } else {
        displayValue = Number(getValue())

        if (displayValue !== Number.NaN) {
          displayValue = formatNumericFixation(getValue(), 6)
        }

        convertedRA = displayValue
      }

      return convertedRA
    }

    function convert() {
      return convertRA()
    }

    $.extend(this, {
      convert: convert
    })
  }

  /**
   * Simple conversion of time values.
   *
   * @param _value    The value, in seconds, to convert.
   * @param _toUnit   The unit to convert to.
   * @constructor
   */
  function TimeConverter(_value, _toUnit) {
    var _selfTimeConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfTimeConverter.value
    }

    function getToUnit() {
      return _selfTimeConverter.toUnit
    }

    /**
     * Obtain the time multiplication factor for the given time unit.
     *
     * @param _unit   The unit to get the factor for.
     * @returns {*}
     */
    function getTimeFactor(_unit) {
      var unitEntry =
        ca.nrc.cadc.search.unitconversion.time[_unit.toLowerCase()]
      var factor

      if (unitEntry) {
        factor = unitEntry.factor
      } else {
        factor = null
      }

      return factor
    }

    /**
     * Convert a Time value between the fromUnit and toUnit.
     *
     * @return        Number to two decimals, or null if unable to convert.
     */
    function convertTime() {
      var time = Number(getValue())
      var toFactor = getTimeFactor(getToUnit())
      var convertedTime

      if (toFactor) {
        convertedTime = Number(toFactor * time)
      } else {
        convertedTime = null
      }

      return convertedTime
    }

    function convert() {
      return formatNumericFixation(convertTime().valueOf(), 3)
    }

    /**
     * Rebase this Converter's value to the base unit; meaning convert the given
     * value back to the base unit, which is SECONDS for Time.
     *
     * Note that this will only work if the toUnit is set to the base unit.
     *
     * @param _fromUnit   The unit that the given value is currently known as.
     * @returns {number}  Number in seconds.
     */
    function rebase(_fromUnit) {
      var factor = getTimeFactor(_fromUnit)
      var currTime = Number(getValue())

      if (factor) {
        return Number(currTime / factor)
      } else {
        throw new Error('No time factor available for ' + _fromUnit)
      }
    }

    $.extend(this, {
      convert: convert,
      convertValue: convertTime,
      rebase: rebase
    })
  }

  /**
   * Convert from metres to other energy values.
   *
   * @param _value    The value in metres to convert.
   * @param _toUnit   The unit to convert to.
   * @constructor
   */
  function WavelengthConverter(_value, _toUnit) {
    var _selfWavelengthConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfWavelengthConverter.value
    }

    function getToUnit() {
      return _selfWavelengthConverter.toUnit
    }

    /**
     * Test if the given unit is an Energy unit.
     *
     * @param _unit   The unit to check.
     */
    function isEnergyUnit(_unit) {
      return _unit in ca.nrc.cadc.search.unitconversion.energy.energyUnits
    }

    /**
     * Test if the given unit is a Frequency unit.
     *
     * @param _unit   The unit to check.
     */
    function isFrequencyUnit(_unit) {
      return _unit in ca.nrc.cadc.search.unitconversion.energy.freqUnits
    }

    /**
     * Test if the given unit is a Wavelength unit.
     *
     * @param _unit   The unit to check.
     */
    function isWavelengthUnit(_unit) {
      return _unit in ca.nrc.cadc.search.unitconversion.energy.waveUnits
    }

    /**
     * Convert an Energy value from the fromUnit to the toUnit.
     *
     */
    function convertEnergy() {
      // Always use the base value from the VOTable, which is in the val
      // attribute.
      var energy = Number(getValue())

      // Wavelength to Wavelength.
      if (isWavelengthUnit(getToUnit())) {
        energy = normalizeWavelength()
      }

      // Wavelength to Energy.
      else if (isEnergyUnit(getToUnit())) {
        // Convert Wavelength to Energy in eV.
        var evValue = Number(
          (ca.nrc.cadc.search.unitconversion.energy.h *
            ca.nrc.cadc.search.unitconversion.energy.c) /
            (ca.nrc.cadc.search.unitconversion.energy.eV * getValue())
        )

        // Normalize Energy to desired unit from eV.
        var tmpFromEVToEnergyUnitConverter = new EnergyConverter(
          evValue,
          getToUnit()
        )
        energy = tmpFromEVToEnergyUnitConverter.convert()
      }

      // Wavelength to Frequency.
      else if (isFrequencyUnit(getToUnit())) {
        // Convert Wavelength to Frequency.
        var hzValue = Number(
          ca.nrc.cadc.search.unitconversion.energy.c / getValue()
        )

        // Normalize Frequency to desired unit from Hz.
        var tmpFromHzToEnergyUnitConverter = new FrequencyConverter(
          hzValue,
          getToUnit()
        )
        energy = tmpFromHzToEnergyUnitConverter.convert()
      }

      return energy
    }

    /**
     * Obtain the energy wavelength multiplication factor for the given time
     * unit.
     *
     * @param unit    The unit.
     * @returns {*}
     */
    function getWavelengthFactor(unit) {
      var unitEntry = ca.nrc.cadc.search.unitconversion.energy.waveUnits[unit]
      return unitEntry ? unitEntry.factor : null
    }

    /**
     * Normalize a Wavelength value from the fromUnit to the toUnit.
     *
     * @return  {Number}  Normalized wavelength value.
     */
    function normalizeWavelength() {
      var toFactor = getWavelengthFactor(getToUnit())
      return Number(getValue() * (1.0 / toFactor))
    }

    function convert() {
      var energyValue = convertEnergy()
      return formatNumericPrecision(energyValue, 12)
    }

    /**
     * Rebase this wavelength to the base unit, which is metres.
     *
     * @param _fromUnit   The unit being rebased FROM.
     */
    function rebase(_fromUnit) {
      var rebasedValue
      var fromUnit = _fromUnit.toLowerCase()

      if (isEnergyUnit(fromUnit)) {
        // Rebase to the energy unit (eV).
        var energyRebaseConverter = new EnergyConverter(
          Number(getValue()),
          'eV'
        )

        // Should be value in eV.
        var rebasedEnergy = energyRebaseConverter.rebase(fromUnit)
        rebasedValue = Number(
          (ca.nrc.cadc.search.unitconversion.energy.h *
            ca.nrc.cadc.search.unitconversion.energy.c) /
            (ca.nrc.cadc.search.unitconversion.energy.eV * rebasedEnergy)
        )
      } else if (isFrequencyUnit(fromUnit)) {
        // Rebase to the frequency unit (Hz).
        var frequencyRebaseConverter = new FrequencyConverter(
          Number(getValue()),
          'Hz'
        )

        var rebasedFrequency = frequencyRebaseConverter.rebase(fromUnit)

        // Convert back to metres.
        rebasedValue = Number(
          ca.nrc.cadc.search.unitconversion.energy.c / rebasedFrequency
        )
      } else if (isWavelengthUnit(fromUnit)) {
        var factor = getWavelengthFactor(fromUnit)
        rebasedValue = Number(getValue() * factor)
      } else {
        throw new Error(
          'No wavelength, energy, or frequency entry for unit ' + _fromUnit
        )
      }

      return rebasedValue
    }

    $.extend(this, {
      convert: convert,
      convertValue: convertEnergy,
      rebase: rebase
    })
  }

  /**
   * Convert from eV to other energy values.
   *
   * @param _value    The value, in eV, to convert.
   * @param _toUnit   The target unit of the conversion.
   * @constructor
   */
  function EnergyConverter(_value, _toUnit) {
    var _selfEnergyConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfEnergyConverter.value
    }

    function getToUnit() {
      return _selfEnergyConverter.toUnit
    }

    /**
     * Obtain the energy multiplication factor for the given energy unit.
     *
     * @param unit
     * @returns {*}
     */
    function getEnergyFactor(unit) {
      var unitEntry = ca.nrc.cadc.search.unitconversion.energy.energyUnits[unit]

      return unitEntry ? unitEntry.factor : null
    }

    /**
     * Normalize an Energy value from the fromUnit to the toUnit.
     *
     * @return  {Number}  Normalized energy value.
     */
    function normalizeEnergy() {
      return getValue() * (1.0 / getEnergyFactor(getToUnit()))
    }

    function convert() {
      return normalizeEnergy()
    }

    function rebase(_fromUnit) {
      var factor = getEnergyFactor(_fromUnit.toLowerCase())
      var energy = Number(getValue())

      if (factor) {
        return Number(energy / (1.0 / factor))
      } else {
        throw new Error('No energy factor available for ' + _fromUnit)
      }
    }

    $.extend(this, {
      convert: convert,
      convertValue: normalizeEnergy,
      rebase: rebase
    })
  }

  /**
   * Convert from metres to frequency units.
   *
   * @param _value      The value, in metres, to convert.
   * @param _toUnit     The unit to convert to.
   * @constructor
   */
  function FrequencyConverter(_value, _toUnit) {
    var _selfFrequencyConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()

    function getValue() {
      return _selfFrequencyConverter.value
    }

    function getToUnit() {
      return _selfFrequencyConverter.toUnit
    }

    /**
     * Obtain the energy frequency multiplication factor for the given time
     * unit.
     *
     * @param unit    The unit.
     * @returns {*}
     */
    function getFrequencyFactor(unit) {
      var unitEntry = ca.nrc.cadc.search.unitconversion.energy.freqUnits[unit]
      return unitEntry ? unitEntry.factor : null
    }

    /**
     * Normalize a Frequency value from the fromUnit to the toUnit.
     *
     * @return  {Number}  Normalized frequency value.
     */
    function normalizeFrequency() {
      var toFactor = getFrequencyFactor(getToUnit())
      return Number(getValue() * (1.0 / toFactor))
    }

    function convert() {
      return normalizeFrequency()
    }

    function rebase(_fromUnit) {
      var factor = getFrequencyFactor(_fromUnit.toLowerCase())
      var frequency = Number(getValue())

      if (factor) {
        return Number(frequency * factor)
      } else {
        throw new Error('No frequency factor available for ' + _fromUnit)
      }
    }

    $.extend(this, {
      convert: convert,
      convertValue: normalizeFrequency,
      rebase: rebase
    })
  }

  /**
   * Convert from a Date to MJD.
   * @param _date   Date input.
   * @constructor
   */
  function MJDConverter(_date) {
    var _selfMJDConverter = this
    this.value = _date

    /**
     * Convert from Date to MJD double.
     *
     * @returns {number}
     */
    function convert() {
      var givenDate = _selfMJDConverter.value
      return (
        givenDate / 86400000 -
        givenDate.getTimezoneOffset() / 1440 +
        2440587.5 -
        2400000.5
      )
    }

    $.extend(this, {
      convert: convert
    })
  }

  /**
   * Date converter between MJD and IVOA calendar.
   *
   * @param _value    The value, in MJD, to convert.
   * @param _toUnit   The target unit of the conversion.
   * @constructor
   */
  function DateConverter(_value, _toUnit) {
    var _selfDateConverter = this

    this.value = sanitizeInput(_value)
    this.toUnit = _toUnit.toLowerCase()
    this.includeTime = true

    function getValue() {
      return _selfDateConverter.value
    }

    function getToUnit() {
      return _selfDateConverter.toUnit
    }

    function includeTime() {
      return _selfDateConverter.includeTime
    }

    function excludeTime() {
      _selfDateConverter.includeTime = false
    }

    /**
     * Format the given value from MJD to IVOA (Gregorian calendar date).
     *
     * @return {String}  Converted value, or null if unable.
     */
    function formatMJD() {
      var MJD = Number(getValue())

      // Julian day
      var jd = Math.floor(MJD) + 2400000.5

      // Integer Julian day
      var jdi = Math.floor(jd)

      // Fractional part of day
      var jdf = Number(jd - jdi + 0.5)

      // Really the next calendar day?
      if (jdf >= 1.0) {
        //    jdf = jdf - 1.0;
        jdi = jdi + 1
      }

      var fraction = Number(MJD - Math.floor(MJD))
      var hours = Math.floor(Number(fraction * 24.0))
      fraction = fraction * 24.0 - hours

      var minutes = Math.floor(Number(fraction * 60.0))
      fraction = fraction * 60.0 - minutes

      var seconds = Math.floor(Number(fraction * 60.0))
      fraction = fraction * 60.0 - seconds

      var milliseconds = Number(fraction * 1000.0).toFixed(0)

      var l = jdi + 68569
      var n = Math.floor((4 * l) / 146097)

      l = Math.floor(l) - Math.floor((146097 * n + 3) / 4)
      var year = Math.floor((4000 * (l + 1)) / 1461001)

      l = l - Math.floor((1461 * year) / 4) + 31
      var month = Math.floor((80 * l) / 2447)

      var day = l - Math.floor((2447 * month) / 80)

      l = Math.floor(month / 11)

      month = Math.floor(month + 2 - 12 * l)
      year = Math.floor(100 * (n - 49) + year + l)

      // Verification step.  Month needs to be zero-based.
      var date = new Date(
        year,
        month - 1,
        day,
        hours,
        minutes,
        seconds,
        milliseconds
      )

      var dateFormat

      if (includeTime()) {
        dateFormat = ca.nrc.cadc.search.formats.date.ISO
      } else {
        dateFormat = ca.nrc.cadc.search.formats.date.W3C
      }

      var dateFormatter = new ca.nrc.cadc.search.DateFormat(date, dateFormat)

      return dateFormatter.format()
    }

    /**
     * Test if the to unit is an IVOA date unit.
     */
    function isIVOAToUnit() {
      return getToUnit() in ca.nrc.cadc.search.unitconversion.date.ivoaUnits
    }

    /**
     * Convert between IVOA and MJD date formats.
     *
     * @return          String value.
     */
    function convertDate() {
      var formattedValue

      if (isIVOAToUnit()) {
        formattedValue = formatMJD()
      } else {
        formattedValue = formatNumericFixation(getValue(), 5)
      }

      return formattedValue
    }

    function convert() {
      return convertDate()
    }

    $.extend(this, {
      convert: convert,
      excludeTime: excludeTime
    })
  }

  function AngleConverter(_value, _toUnit) {
    var _selfAngleConverter = this

    this.value = _value
    this.toUnit = _toUnit

    function getValue() {
      return _selfAngleConverter.value
    }

    function getToUnit() {
      return _selfAngleConverter.toUnit
    }

    /**
     * Obtain the factor for conversion for the given unit.
     *
     * @param _angleUnit    The Angular unit obtain the factor for.
     * @return {Number}     Numeric factor.
     */
    function getAngleFactor(_angleUnit) {
      var unitEntry = ca.nrc.cadc.search.unitconversion.angle[_angleUnit]
      var factor

      if (unitEntry) {
        factor = unitEntry.factor
      } else {
        factor = null
      }

      return factor
    }

    /**
     * Test if the unit is an Angle unit.
     */
    function isToAngleUnit() {
      return getToUnit() in ca.nrc.cadc.search.unitconversion.angle
    }

    /**
     * Convert an Angle value from the fromUnit to the toUnit.
     *
     * @return  Number value, or null if unable to convert.
     */
    function convertAngle() {
      var angle = Number(getValue())
      var convertedAngle
      var toFactor = getAngleFactor(getToUnit())

      if (toFactor) {
        var returned = Number(toFactor * angle)
        convertedAngle = returned.valueOf()
      } else {
        convertedAngle = null
      }

      return convertedAngle
    }

    /**
     * Rebase this Converter's value to the base unit; meaning convert the given
     * value back to the base unit, which is Arc seconds for Angle.
     *
     * Note that this will only work if the toUnit is set to the base unit.
     *
     * @param _fromUnit   The unit that the given value is currently known as.
     * @returns {number}  Number in arc seconds.
     */
    function rebase(_fromUnit) {
      var factor = getAngleFactor(_fromUnit)
      var area = Number(getValue())

      if (factor) {
        return Number(area / factor)
      } else {
        throw new Error('No angular factor available for ' + _fromUnit)
      }
    }

    /**
     * Convert this degree value to the given toUnit.
     *
     * @returns {*}   Converted value.
     */
    function convert() {
      var res = isToAngleUnit() ? convertAngle() : null

      return formatNumericFixation(res, 4)
    }

    $.extend(this, {
      convert: convert,
      convertValue: convertAngle,
      rebase: rebase
    })
  }

  /**
   * Convert area values.
   * @param _value    The value to convert.  In base units.
   * @param _toUnit   The unit to convert to.
   * @constructor
   */
  function AreaConverter(_value, _toUnit) {
    var _selfAreaConverter = this

    this.value = _value
    this.toUnit = _toUnit

    function getValue() {
      return _selfAreaConverter.value
    }

    function getToUnit() {
      return _selfAreaConverter.toUnit
    }

    /**
     * The AREA factor for the given unit
     *
     * @param _areaUnit   The unit to use.
     * @return {*}
     */
    function getAreaFactor(_areaUnit) {
      var unitEntry = ca.nrc.cadc.search.unitconversion.area[_areaUnit]
      var factor

      if (unitEntry) {
        factor = unitEntry.factor
      } else {
        factor = null
      }

      return factor
    }

    /**
     * Test if the unit is an Area unit.
     */
    function isToAreaUnit() {
      return getToUnit() in ca.nrc.cadc.search.unitconversion.area
    }

    /**
     * Convert an Area value from the fromUnit to the toUnit.
     *
     * @return  Number value, or null if unable to convert.
     */
    function convertArea() {
      var area = Number(getValue())
      var convertedArea
      var factor = getAreaFactor(getToUnit())

      if (factor) {
        var returned = Number(factor * area)
        convertedArea = returned.valueOf()
      } else {
        convertedArea = null
      }

      return convertedArea
    }

    /**
     * Rebase this Converter's value to the base unit; meaning convert the given
     * value back to the base unit, which is Arc degrees for Area.
     *
     * Note that this will only work if the toUnit is set to the base unit.
     *
     * @param _fromUnit   The unit that the given value is currently known as.
     * @returns {number}  Number in arc degrees.
     */
    function rebase(_fromUnit) {
      var factor = getAreaFactor(_fromUnit)
      var area = Number(getValue())

      if (factor) {
        return Number(area / factor)
      } else {
        throw new Error('No area factor available for ' + _fromUnit)
      }
    }

    /**
     * Convert this degree value to the given toUnit.
     *
     * @returns {*}   Converted value.
     */
    function convert() {
      var res = isToAreaUnit() ? convertArea() : null
      return formatNumericFixation(res, 4)
    }

    $.extend(this, {
      convert: convert,
      convertValue: convertArea,
      rebase: rebase
    })
  }
})(jQuery)
