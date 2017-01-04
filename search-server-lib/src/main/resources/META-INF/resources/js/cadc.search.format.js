(function ($)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "HTMLCellFormat": HTMLCellFormat,
            "JSONObservationDetailsFormat": JSONObservationDetailsFormat,
            "UnitConversionFormat": UnitConversionFormat,
            "DateFormat": DateFormat,
            "NumberFormat": NumberFormat,
            "formats": {
              "date": {
                "W3C": "YYYY-MM-DD",
                "ISO": "YYYY-MM-DD HH:mm:ss",
                "ISO_MS": "YYYY-MM-DD HH:mm:ss.SSS",
                "IVOA": "YYYY-MM-DD'T'HH:mm:ss.SSS"
              }
            }
          }
        }
      }
    }
  });

  /**
   * Basic number formatting.  This will handle NaN values.
   *
   * @param _number    Input Number.
   * @constructor
   */
  function NumberFormat(_number)
  {
    var _selfNumberFormat = this;

    this.number = _number;


    /**
     * Ensure input.
     */
    function init()
    {
      if (typeof(getNumber()) != "number")
      {
        throw new Error("Bad input (Not a number) > '" + getNumber() + "'");
      }
    }


    function getNumber()
    {
      return _selfNumberFormat.number;
    }

    /**
     * Turn the number into a String.  NaN numbers are empty strings!
     * @returns {*}
     */
    function format()
    {
      var num = getNumber();
      var formattedValue;

      if (isNaN(num))
      {
        formattedValue = "";
      }
      else
      {
        formattedValue = num.toString();
      }

      return formattedValue;
    }

    init();

    $.extend(this,
             {
               "format": format
             });
  }

  /**
   * Formatter for dates.  If this is to be used as HTML output, use the output
   * from format as the input to the HTMLCellFormat.
   *
   * @param _date         The date object to format.
   * @param _dateFormat   From the static supported layouts in ca.nrc.cadc.search.formats.date.
   * @constructor
   */
  function DateFormat(_date, _dateFormat)
  {
    var _selfDateFormat = this;

    this.date = _date;
    this.formatName = _dateFormat;

    function getDate()
    {
      return _selfDateFormat.date;
    }

    function getDateFormat()
    {
      return _selfDateFormat.formatName;
    }

    /**
     * Format the Date output as String.
     * @returns {String}    The String value.
     */
    function format()
    {
      var formattedValue;

      try
      {
        formattedValue = moment(getDate()).format(getDateFormat());
      }
      catch (e)
      {
        console.debug("Error in formatting date: " + e);
        formattedValue = "";
      }

      return formattedValue;
    }

    $.extend(this,
             {
               "format": format
             });
  }


  /**
   * Formatter class to output HTML Span in the Grid row.
   *
   * @param _string     The value.
   * @param _title      The title of the cell.
   * @param _cssClass   The CSS class to append.
   * @constructor
   */
  function HTMLCellFormat(_string, _title, _cssClass)
  {
    var _selfHTMLCellFormat = this;

    this.cssClass = _cssClass || "";
    this.value = _string;
    this.title = _title || _string;

    function getCSSClass()
    {
      return _selfHTMLCellFormat.cssClass;
    }

    function getValue()
    {
      return _selfHTMLCellFormat.value;
    }

    function getTitle()
    {
      return _selfHTMLCellFormat.title;
    }

    function format()
    {
      var stringUtil = new cadc.web.util.StringUtil(getValue());

      return "<span class='cellValue " + getCSSClass() + "' title='"
                 + getTitle() + "'>" + stringUtil.sanitize() + "</span>";
    }

    $.extend(this,
             {
               "format": format
             });
  }

  /**
   * Format the JSON data for the Observation details JSON data.
   *
   * @param _data       The JSON object.
   * @constructor
   */
  function JSONObservationDetailsFormat(_data)
  {
    var _selfFormat = this;

    _selfFormat.jsonData = _data;

    /**
     * Format this formatter's data into the given container.
     *
     * @return The HTML String.
     */
    function format()
    {
      var tableNode = JsonHuman.format(_selfFormat.jsonData);
      return $(tableNode).get(0).outerHTML;
    }

    $.extend(this,
             {
               "format": format
             });
  }

  /**
   * Formatter to perform the unit conversion.
   *
   * @param _converter  The UnitConverter object.
   * @param _title      The title of the cell.
   * @param _cssClass   The CSS identity class of the cell.
   * @constructor
   */
  function UnitConversionFormat(_converter, _title, _cssClass)
  {
    var _selfUnitConversionFormat = this;

    this.converter = _converter;


    function getConverter()
    {
      return _selfUnitConversionFormat.converter;
    }

    /**
     * Format the value only.
     *
     * @return {*}
     */
    function formatValue()
    {
      var convertedValue;

      try
      {
        var thisConverter = getConverter();

        if (thisConverter)
        {
          convertedValue = thisConverter.convert();
        }
        else
        {
          convertedValue = "";
        }
      }
      catch (e)
      {
        console.debug("Error in formatting: " + e);
        convertedValue = "";
      }

      return convertedValue;
    }

    /**
     * Format the output for this formatter.
     */
    function format()
    {
      var htmlFormat = new HTMLCellFormat(formatValue(), _title, _cssClass);
      return htmlFormat.format();
    }

    $.extend(this,
             {
               "format": format,
               "formatValue": formatValue
             });
  }
})(jQuery);