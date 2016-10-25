(function ($)
{
  $.extend(true, window, {
    "cadc": {
      "vot": {
        "ResultState": {
          "COLUMN_PARAMETER_NAME": "col_",
          "SORT_COLUMN": "sortCol",
          "SORT_DIRECTION": "sortDir",
          "SORT_ASCENDING": "asc",
          "SORT_DESCENDING": "dsc"
        },
        "ResultStateSerializer": ResultStateSerializer,
        "ResultStateDeserializer": ResultStateDeserializer
      }
    }
  });


  /**
   * A library to serialize and deserialize a query and the state of the
   * results table.
   *
   * @param {String} _baseUrl The base url.
   * @param {String} _sortColumn Column the table is sorted on.
   * @param {String} _sortDirection Sort direction, ascending or descending.
   * @param {Array} _columns Columns that are actually in the Grid.
   * @param {Object} _widths Column id's and widths.
   * @param {Object} _filters Column id's and filter values.
   * @param {Object} _units Column id's and select values.
   */
  function ResultStateSerializer(_baseUrl, _sortColumn, _sortDirection,
                                 _columns, _widths, _filters, _units)
  {
    var _self = this;
    this.baseUrl = _baseUrl;
    this.sortColumn = _sortColumn;
    this.sortDirection = _sortDirection;
    this.columns = _columns || [];
    this.widths = _widths || {};
    this.filters = _filters || {};
    this.units = _units || {};


    /**
     * @return {String}  URI string as given by the caller.
     */
    function getBaseURL()
    {
      return _self.baseUrl;
    }

    /**
     * Returns a url from the given arguments that contains the query parameters
     * and optionally parameters that describe the state of the results table.
     */
    function getResultStateUrl()
    {
      var url = [];
      url.push(getBaseURL());

      if (_self.sortColumn)
      {
        if (url.join().indexOf("#") < 0)
        {
          url.push("#");
        }

        url.push(cadc.vot.ResultState.SORT_COLUMN);
        url.push("=");
        url.push(encodeURIComponent(_self.sortColumn));

        url.push("&");
      }

      if (_self.sortDirection)
      {
        if (url.join().indexOf("#") < 0)
        {
          url.push("#");
        }

        url.push(cadc.vot.ResultState.SORT_DIRECTION);
        url.push("=");
        if (_self.sortDirection === cadc.vot.ResultState.SORT_ASCENDING)
        {
          url.push(cadc.vot.ResultState.SORT_ASCENDING);
        }
        else
        {
          url.push(cadc.vot.ResultState.SORT_DESCENDING);
        }

        url.push("&");
      }

      if ((_self.columns.length > 0) && (url.join().indexOf("#") < 0))
      {
        url.push("#");
      }

      $.each(_self.columns, function (index, column)
      {
        url.push(getColumnParameter(index + 1, column.id));
        url.push("&");
      });

      // Replace end ampersand, if present.
      return url.join("").replace(/\&$/, "");
    }

    /**
     * Returns an url parameter from the given column attributes.
     *
     * @param {type} _index
     * @param {type} _columnID
     * @returns {String}
     */
    function getColumnParameter(_index, _columnID)
    {
      var parameter = [];
      parameter.push(cadc.vot.ResultState.COLUMN_PARAMETER_NAME);
      parameter.push(_index);
      parameter.push("=");
      parameter.push(encodeURIComponent(_columnID));
      parameter.push(";");
      parameter.push(_self.widths[_columnID] ? encodeURIComponent(_self.widths[_columnID]) : "");
      parameter.push(";");
      parameter.push(_self.filters[_columnID] ? encodeURIComponent(_self.filters[_columnID]) : "");
      parameter.push(";");
      parameter.push(_self.units[_columnID] ? _self.units[_columnID] : "");
      return parameter.join("");
    }

    $.extend(this,
             {
               // Methods
               "getResultStateUrl": getResultStateUrl
             });

  }

  /**
   * Parses the query url into just the query url part, removing any
   * result state parameters.
   *
   * @param {String} _url The query url.
   * @returns {String}
   */
  function ResultStateDeserializer(_url)
  {
    var _self = this;

    this.url = new cadc.web.util.URI(_url);


    function getURL()
    {
      return _self.url;
    }

    /**
     * Sort the array by the number attribute.
     *
     * @param {Object} a
     * @param {Object} b
     * @returns {Number}
     */
    function compare(a, b)
    {
      if (Number(a.number) < Number(b.number))
      {
        return -1;
      }

      if (Number(a.number) > Number(b.number))
      {
        return 1;
      }

      return 0;
    }

    /**
     * Parse the result table state parameters into an voviewer options
     * object containing the viewer options from the url.
     *
     * @returns {Object} Voviewer options.
     */
    function getViewerOptions()
    {
      var options = {};
      var columns = [];
      var columnOptions = {};
      var columnFilters = {};
      var hashString = getURL().getHash();

      if (hashString)
      {
        $.each(hashString.split("&"), function (index, hashStringItem)
        {
          var hashStringItems = hashStringItem.split("=");
          var key = hashStringItems[0];
          var value = hashStringItems[1];

          if (key === cadc.vot.ResultState.SORT_COLUMN)
          {
            options.sortColumn = value ? decodeURIComponent(value) : '';
          }
          else if (key === cadc.vot.ResultState.SORT_DIRECTION)
          {
            options.sortDir = value ? decodeURIComponent(value) : '';
          }
          else if (key.slice(0, cadc.vot.ResultState.COLUMN_PARAMETER_NAME.length)
              === cadc.vot.ResultState.COLUMN_PARAMETER_NAME)
          {
            var parts = value.split(";");
            var id = parts[0] ? decodeURIComponent(parts[0]) : '';
            var width = parts[1] ? parts[1] : '';
            var filter = parts[2] ? decodeURIComponent(parts[2]) : '';
            var unit = parts[3] ? decodeURIComponent(parts[3]) : '';
            if (filter)
            {
              columnFilters[id] = filter;
            }
            if (width || unit)
            {
              var option = {};
              if (width)
              {
                option.width = width;
              }
              if (unit)
              {
                var header = {};
                header.units = [
                  {"label": unit, "value": unit, "default": true}
                ];
                option.header = header;
              }
              columnOptions[id] = option;
            }

            // Track column order and column id for displayColumns.
            var columnNumber = key.substring(cadc.vot.ResultState.COLUMN_PARAMETER_NAME.length);
            columns.push({"number": columnNumber, "id": id});
          }
        });
      }

      if (!$.isEmptyObject(columnOptions))
      {
        options.columnOptions = columnOptions;
      }

      if (!$.isEmptyObject(columnFilters))
      {
        options.columnFilters = columnFilters;
      }

      // Sort the columns by column number.
      if (columns.length > 0)
      {
        columns.sort(compare);

        // Create an array of column id's.
        options.defaultColumnIDs = [];
        $.each(columns, function (index, column)
        {
          options.defaultColumnIDs.push(column.id);
        });
      }

      return options;
    }

    $.extend(this,
             {
               // Methods
               "getViewerOptions": getViewerOptions
             });
  }
})(jQuery);
