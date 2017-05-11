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

(function ($, window)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "datatrain": {
              "SELECT_DISPLAY_OPTION_COUNT": 12,
              "SPACER_CHAR": "&#9472;",
              /**
               * Obtain a spacer formatter.
               *
               * @returns {string}
               * @constructor
               */
              "SPACER": function ()
              {
                var val = "";
                for (var s = 0; s < 20; s++)
                {
                  val += ca.nrc.cadc.search.datatrain.SPACER_CHAR;
                }

                return val;
              },
              "tap": {
                "INSTRUMENT_FRESH_MJD_FIELD_NAME": {
                  "caom2": "max_time_bounds_cval1",
                  "obscore": "max_t_min"
                },
                "TABLE": {
                  "caom2": "caom2.enumfield",
                  "obscore": "caom2.obscoreenumfield"
                },
                "UTYPE_COLUMN_NAMES": {
                  "caom2": {
                    "Plane.energy.emBand": {
                      tap_column_name: "energy_emband",
                      size: 1
                    },
                    "Observation.collection": {
                      tap_column_name: "collection",
                      size: 2
                    },
                    "Observation.instrument.name": {
                      tap_column_name: "instrument_name",
                      size: 2
                    },
                    "Plane.energy.bandpassName": {
                      tap_column_name: "energy_bandpassname",
                      size: 2
                    },
                    "Plane.calibrationLevel": {
                      tap_column_name: "calibrationlevel",
                      size: 2
                    },
                    "Plane.dataProductType": {
                      tap_column_name: "dataproducttype",
                      size: 1
                    },
                    "Observation.type": {
                      tap_column_name: "type",
                      size: 2
                    }
                  },
                  "obscore": {
                    "DataID.Collection": {
                      tap_column_name: "obs_collection",
                      size: 2
                    },
                    "Provenance.ObsConfig.Facility.name": {
                      tap_column_name: "facility_name",
                      size: 1
                    },
                    "Provenance.ObsConfig.Instrument.name": {
                      tap_column_name: "instrument_name",
                      size: 2
                    },
                    "ObsDataset.calibLevel": {
                      tap_column_name: "calib_level",
                      size: 1
                    },
                    "ObsDataset.dataProductType": {
                      tap_column_name: "dataproduct_type",
                      size: 1
                    }
                  }
                }
              },
              "CALIBRATION_LEVEL_MAP": {
                0: "Raw Instrumental",
                1: "Raw Standard",
                2: "Calibrated",
                3: "Product"
              },
              "COLLECTION_ORDER": [
                "CFHT",
                "CFHTMEGAPIPE",
                "CFHTTERAPIX",
                "CFHTWIRWOLF",
                "HST",
                "HSTHLA",
                "GEMINI",
                "JCMT",
                "JCMTLS",
                "DAO",
                "DAOPLATES"
              ],
              "sortCollections": function (val1, val2)
              {
                var val1Index = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER.indexOf(val1);
                var val2Index = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER.indexOf(val2);

                var placement;

                // Put garbage at the bottom
                if (val2Index < 0)
                {
                  placement = -1;
                }
                else if (val1Index < 0)
                {
                  placement = 1;
                }
                else
                {
                  placement = val1Index - val2Index;
                }

                return placement;
              },
              "sortNumericDescending": function (val1, val2)
              {
                var descVal;

                // Put garbage at the bottom
                if ((val1 === null) || isNaN(val1))
                {
                  descVal = 1;
                }
                else if ((val2 === null) || isNaN(val2))
                {
                  descVal = -1;
                }
                else
                {
                  descVal = val2 - val1;
                }

                return descVal;
              },
              "CUSTOM_SORT_UTYPES": {
                "Observation.collection": function (val1, val2)
                {
                  return ca.nrc.cadc.search.datatrain.sortCollections(val1, val2);
                },
                "DataID.Collection": function (val1, val2)
                {
                  return ca.nrc.cadc.search.datatrain.sortCollections(val1, val2);
                },
                "Plane.calibrationLevel": function (val1, val2)
                {
                  return ca.nrc.cadc.search.datatrain.sortNumericDescending(val1, val2);
                },
                "Obs.calibLevel": function (val1, val2)
                {
                  return ca.nrc.cadc.search.datatrain.sortNumericDescending(val1, val2);
                }
              },
              "ENDPOINT": "/tap/sync",
              "DataTrain": DataTrain,
              "events": {
                "onDataTrainLoaded": new jQuery.Event("AdvancedSearch:onDataTrainLoaded"),
                "onDataTrainLoadFail": new jQuery.Event("AdvancedSearch:onDataTrainLoadFail")
              }
            }
          }
        }
      }
    }
  });

  /**
   * @param {String} _modelDataSource   Name of the data source [caom2 | obscore]
   * @param {{}} _options   Options to this DataTrain.
   * @param {boolean} [_options.autoInit=false]   Whether to initialize on creation.
   * @param {String} [_options.tapSyncEndpoint=/search/tap/sync]    TAP Endpoint.
   * @constructor
   */
  function DataTrain(_modelDataSource, _options)
  {
    this.modelDataSource = _modelDataSource;
    this.pageLanguage = $("html").attr("lang");
    this.$dataTrainDOM = $("div[id='" + this.modelDataSource + "@Hierarchy']");
    this.uType = this.$dataTrainDOM.find(".hierarchy_utype").text();
    this.groups = [];
    this.freshInstruments = [];

    this.defaults = {
      autoInit: false,
      tapSyncEndpoint: "/search/tap/sync"
    };

    this.options = $.extend({}, true, this.defaults, _options);

    /**
     * Initialize this DataTrain.
     */
    this.init = function ()
    {
      var tapQuery = this._createTAPQuery();
      var myself = this;

      $.get(this.options.tapSyncEndpoint, {
        "LANG": "ADQL",
        "FORMAT": "CSV",
        "QUERY": tapQuery
      }).done(function (data)
              {
                myself._trigger(ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
                                {data: data});
              })
        .fail(function (jqXHR)
              {
                trigger(ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
                        {responseText: jqXHR.responseText});
              });
    };

    /**
     * Create the TAP query to obtain the Data Train values.
     * @returns {string}
     * @private
     */
    this._createTAPQuery = function ()
    {
      var uTypes = this.uType.split("/");
      var tapColumns = [];

      for (var i = 0, ul = uTypes.length; i < ul; i++)
      {
        tapColumns.push(ca.nrc.cadc.search.datatrain.tap.UTYPE_COLUMN_NAMES[
                          this.modelDataSource][uTypes[i]].tap_column_name);
      }

      var now = new Date();
      var dateThreshold = new Date(now.getFullYear() - 5, now.getMonth(),
                                   now.getDate(), now.getHours(),
                                   now.getMinutes(), now.getSeconds(),
                                   now.getMilliseconds());

      var mjdConverter =
        new ca.nrc.cadc.search.unitconversion.MJDConverter(dateThreshold);
      var mjdCondition = ", CASE WHEN "
                         +
                         ca.nrc.cadc.search.datatrain.tap.INSTRUMENT_FRESH_MJD_FIELD_NAME[
                           this.modelDataSource]
                         + " >= " + mjdConverter.convert()
                         + " THEN 1 ELSE 0 END ";

      return "SELECT " + tapColumns.join(",") + mjdCondition
             + " FROM "
             + ca.nrc.cadc.search.datatrain.tap.TABLE[_modelDataSource];
    };

    /**
     * Do an initial load of all of the groupings.  This will parse the given
     * CSV data and append the resulting selects tot he given container.
     *
     * @param {String} data                The CSV data from the response.
     */
    this.load = function (data)
    {
      var arrayOfRows = $.csv.toArrays(data);
      var firstRow = arrayOfRows[0];

      // Skip the first row (Start at 1).
      arrayOfRows = arrayOfRows.slice(1);

      // The instrument name is handled separately.
      var instrumentNameIndex = firstRow.indexOf("instrument_name");

      // RegEx for '+' character.
      var plus = /\+/g;

      // Add id for this div.
      var group = {};

      // Put an array of uType names into an array.
      group.uTypes = this.uType.split("/");

      // Put an array of uType values into an array.
      group.values = [];

      for (var i = 0, al = arrayOfRows.length; i < al; i++)
      {
        group.values[i] = arrayOfRows[i];
        var groupValues = group.values[i];

        for (var j = 0, gvl = groupValues.length; j < (gvl - 1); j++)
        {
          // Last column is the state.
          var val = $.trim(groupValues[j]);
          var formattedVal = ((val === null) || (val === '')) ? "null" : groupValues[j].replace(plus, " ");

          var freshFlag = (groupValues[gvl - 1] === "1");
          var instrumentName = groupValues[instrumentNameIndex];

          if ((freshFlag === true) && (this.freshInstruments.indexOf(instrumentName) < 0))
          {
            this.freshInstruments.push(instrumentName);
          }

          group.values[i][j] = formattedVal;
        }
      }

      // Add the group object to the global groups.
      this.groups.push(group);

      // Build the table with the selects and get the first select.
      var select = this._buildTable(group);

      this.updateLists(select, true);
    };

    /**
     * Construct a DOM of a select and append it.
     * @param _group
     * @returns {*}
     * @private
     */
    this._buildTable = function (_group)
    {
      // Keep track of the first non-hidden select.
      var firstSelect;

      // Loop through each attribute.
      for (var i = 0, groupUTypesLength = _group.uTypes.length; i < groupUTypesLength; i++)
      {
        // Get the JSON text from hidden input and
        // eval into an enumerated object.
        var uType = _group.uTypes[i];
        var input = document.getElementById(uType + ".json");
        var enumerated = JSON.parse(input.value);
        var row = _group.values[i];

        // Create either hidden or select input.
        var select;
        if (enumerated.hidden)
        {
          select = buildHidden(enumerated, row);
        }
        else
        {
          var containerElement = document.createElement("div");
          containerElement.className = "align-left advanced_search_hierarchy_select_div";

          if (i === 0)
          {
            containerElement.className += " row-start";
          }
          // Last item
          else if (i === (groupUTypesLength - 1))
          {
            containerElement.className += " row-end";
          }

          select = this._buildSelect(enumerated, containerElement);

          if (firstSelect === undefined)
          {
            firstSelect = select.childNodes[1];
          }
        }

        // Add <select> to the table cell.
        this.$dataTrainDOM.append(select);
      }

      // Return first select.
      return firstSelect;
    };

    /**
     * Toggle the loading icon.
     * @private
     */
    this._toggleLoading = function ()
    {
      // Remove temporary div.
      var building = document.getElementById(this.uType + ".building");
      building.className = (building.className.indexOf("wb-invisible") >= 0)
        ? ""
        : "wb-invisible";
    };

    /**
     * Creates a select Input Object, assigning values from the
     * enumeration Object.
     *
     * @param enumerated        The enumerated row object.
     * @param containerElement  The containing DOM.
     * @returns {*}
     * @private
     */
    this._buildSelect = function (enumerated, containerElement)
    {
      var label = document.createElement("label");
      if (this.pageLanguage === "fr")
      {
        label.className = "advanced_search_hierarchy_select_div_label";
      }

      var labelSpanFieldName = document.createElement("span");

      var select = document.createElement("select");
      select.id = enumerated.utype;
      select.name = select.id;
      select.title = this.getDataTrainHeader(enumerated.label);

      labelSpanFieldName.className = "indent-small field-name";
      labelSpanFieldName.innerHTML = select.title;

      label.appendChild(labelSpanFieldName);
      label.setAttribute("for", select.id);

      select.label = label;

      if (enumerated.size)
      {
        select.size = enumerated.size;
      }
      else
      {
        select.size = ca.nrc.cadc.search.datatrain.SELECT_DISPLAY_OPTION_COUNT;
        select.multiple = true;
      }

      select.onchange = function (e)
      {
        this.updateLists(e.target, false);
      }.bind(this);

      select.className = "hierarchy_select";

      containerElement.appendChild(label);
      containerElement.appendChild(select);

      return containerElement;
    };

    /*
     * Creates a hidden Input Object, assigning values from the
     * enumerated Object.
     *
     * @enumerated - enumerated Object.
     * @values - array of select option values.
     *
     */
    function buildHidden(enumerated, values)
    {
      var hidden = document.createElement("input");
      hidden.type = "hidden";
      hidden.name = enumerated.utype;
      hidden.value = values[0];
      return hidden;
    }

    this.getDataTrainHeader = function (name)
    {
      if (this.pageLanguage === "fr")
      {
        return getFrenchDataTrainHeaderMap()[name];
      }
      else
      {
        return name;
      }
    };

    function getFrenchDataTrainHeaderMap()
    {
      return {
        "All": "Tout",
        "Band": "Domaine d'énergie",
        "Collection": "Collection",
        "Instrument": "Instrument",
        "Filter": "Filtre",
        "Calibration Level": "Niveau de calibration",
        "Data Type": "Type de donnée",
        "Observation Type": "Type d'observation"
      }
    }

    /**
     * Return the group when the group utype contains the given utype,
     * or null if no group utype is found containing the given utype.
     *
     * @param {String}  _uType  uType of the select.
     * @param {Array}   _groups Array of group items.
     * @returns {Object} group object or null if not found.
     */
    function getGroupByUType(_groups, _uType)
    {
      // Loop through the group names looking for name.
      for (var i = 0, gl = _groups.length; i < gl; i++)
      {
        var group = _groups[i];
        var groupUTypes = group.uTypes;
        for (var j = 0; j < groupUTypes.length; j++)
        {
          if (groupUTypes[j] === _uType)
          {
            return group;
          }
        }
      }

      return null;
    }

    /**
     * Updates the selects. If updateAllOptions is true then all selects are
     * updated. If updateAllOptions is false, then only the given select,
     * and any selects to the right are updated.
     *
     * @param _select {HTMLSelectElement} the select element that triggered this
     * function.
     * @param _updateAllOptionsFlag {boolean} update all selects if true,
     * otherwise update the given select and any selects to the right.
     */
    this.updateLists = function (_select, _updateAllOptionsFlag)
    {
      // Parse out the unique id for the hierarchy and the attribute name.
      var uType = _select.id;

      // Find the group for this single uType.
      var group = getGroupByUType(this.groups, uType);

      // Find the index in group.utypes array for this enumerated utype,
      // gives index of the select.
      var selectIndex = getSelectIndex(group, uType);

      // Get the selected options.
      var selected = _updateAllOptionsFlag
        ? getSelectedOptions(group, group.uTypes.length)
        : getSelectedOptions(group, selectIndex);

      // Get the options for the selects being updated.
      var options = _updateAllOptionsFlag
        ? getAllOptions(group, selected)
        : getOptions(group, selected, selectIndex);

      // Update the selects with new options.
      this._setOptions(group, selectIndex, selected, options,
                       _updateAllOptionsFlag);
    };

    /*
     * Find the index in the group.uTypes array for this uType.
     * Names is a list of hierarchy attributes.
     *
     * @group - group containing uTypes array.
     * @uType - uType.
     * @return - index of the uType in group.uTypes array.
     */
    function getSelectIndex(group, uType)
    {
      // Loop through the group names looking for name.
      var selectIndex = -1;
      for (var i = 0, gutl = group.uTypes.length; i < gutl; i++)
      {
        if (group.uTypes[i] === uType)
        {
          selectIndex = i;
          break;
        }
      }

      // If the attribute is not found in the group.utypes throw an error.
      if (selectIndex === -1)
      {
        throw new Error(uType + " not found in group names[" + group.uTypes +
                        "]");
      }

      return selectIndex;
    }

    /*
     * Creates and returns an array of the selected options for the group.
     *
     * @group - group data for this div.
     * @selectIndex - index into the group.utypes array.
     * @returns - array of all selected options.
     */
    function getSelectedOptions(group, selectIndex)
    {

      // 2D array to hold selected options arrays.
      var selected = [];

      // We want to include the selectIndex values as well.
      if (selectIndex < group.uTypes.length)
      {
        selectIndex++;
      }

      // Loop through the selects and get the selected options from each.
      for (var i = 0; i < selectIndex; i++)
      {
        var select = document.getElementById(group.uTypes[i]);
        if (select === null)
        {
          // If select is null it must be a hidden attribute,
          // selected values are then the attribute group values.
          selected[i] = [];
          for (var j = 0; j < group.values.length; j++)
          {
            var v = group.values[j][i];
            if (!ifArrayContains(selected[i], v))
            {
              selected[i][selected[i].length] = v;
            }
          }
        }
        else
        {
          selected[i] = getSelected(select);
        }
      }
      return selected;
    }

    /*
     * Creates and returns an array of the selected option values
     * for the given select element.
     *
     * @select - select element.
     * @returns - array of selected options.
     */
    function getSelected(select)
    {

      // Array to hold selected options from this select.
      var selected = [];

      // Check if multiple options can be selected.
      var multiple = (select.type === "select-multiple");

      // Read selected options into an array.
      if (multiple)
      {
        for (var i = 0; i < select.options.length; i++)
        {
          if (select.options[i].selected)
          {
            var option = select.options[i].value;
            selected[selected.length] = option;

            // If top empty option is selected, don't allow
            if ((option.length > 4) && option.substring(3, 0) === "All")
            {
              break;
            }
          }
        }
      }
      else
      {
        if (select.selectedIndex !== -1)
        {
          selected[selected.length] =
            select.options[select.selectedIndex].value;
        }
      }

      // If no option(s) are selected, select the top empty header option.
      if (selected.length === 0)
      {
        selected[0] = "";
      }

      return selected;
    }


    /*
     * Get the options for the selects being updated.
     *
     * @group - group containing selects data.
     * @selected - 2d array of selected option values.
     * @selectIndex - index into the group.utypes array.
     * @returns - 2d array of options.
     */
    function getOptions(group, selected, selectIndex)
    {
      // Arrays to hold the options for the selects to be updated.
      var options = [];

      for (var i = selectIndex, gutl = group.uTypes.length; i < gutl; i++)
      {
        options[i] = [];
      }

      // Get the options for the current select.
      getCurrentOptions(options, group, selected, selectIndex);

      // Get the options for any child selects.
      getChildOptions(options, group, selected, selectIndex);

      return options;
    }

    /*
     * Get the options for the current select.
     *
     * @options - 2d array holding the options for each select.
     * @group - group containing the selects data.
     * @selected - 2d array of selected option values.
     * @selectedIndex - index into the group.utypes array.
     */
    function getCurrentOptions(options, group, selected, selectIndex)
    {

      // The first select should always show all of the select options.
      if (selectIndex === 0)
      {
        for (var i = 0, gvl = group.values.length; i < gvl; i++)
        {
          if (!ifArrayContains(options[0], group.values[i][0]))
          {
            options[0][options[0].length] = group.values[i][0];
          }
        }
      }
      else
      {

        // Loop through the group values.
        for (i = 0; i < group.values.length; i++)
        {

          // Get this group of values.
          var values = group.values[i];

          // Indicates if values found that match selected options.
          var found = false;

          // Loop through the values that represent the selects not being
          // updated,
          // including the current select, and use matches between values and
          // selected options to determine which values to display in the
          // current select.
          for (var j = 0; j < selectIndex; j++)
          {

            // Get one of the possible values for this select.
            var value = values[j];

            // Loop through the selected options and check if this value
            // is one of the selected options.
            for (var k = 0; k < selected[j].length; k++)
            {
              var sel = selected[j][k];

              // If the header '' is selected, or if the value matches
              // the selected option, no need to check rest of selected options.
              if (sel === "" || sel === value)
              {
                found = true;
                break;
              }
              else
              {
                found = false;
              }
            }

            // Group value doesn't match any selected options,
            // no need to continue with this group.
            if (!found)
            {
              break;
            }
          }

          // If all group values tested are found in the selected options, add
          // the selectIndex values to the selectIndex options
          if (found)
          {
            value = values[selectIndex];
            if (!ifArrayContains(options[j], value))
            {
              options[selectIndex][options[selectIndex].length] = value;
            }
          }
        }
      }
    }

    /*
     * Get the options for the child selects of the current select.
     *
     * @options - 2d array holding the options for each select.
     * @group - group containing the selects data.
     * @selected - 2d array of selected option values.
     * @selectedIndex - index into the group.utypes array.
     */
    function getChildOptions(options, group, selected, selectIndex)
    {
      // If the current select is the last select, no children selects.
      if (selectIndex < (group.uTypes.length - 1))
      {
        // Loop through the group values.
        for (var i = 0, gvl = group.values.length; i < gvl; i++)
        {
          // Get this group of values.
          var values = group.values[i];

          // Indicates if values found that match selected options.
          var found = false;

          // Loop through the values that represent the selects not being
          // updated, and use matches between values and selected options to
          // determine which values to display in the selects being updated.
          for (var j = 0; j <= selectIndex; j++)
          {

            // Get one of the possible values for this select.
            var value = values[j];

            // Loop through the selected options and check if this value
            // is one of the selected options.
            for (var k = 0; k < selected[j].length; k++)
            {
              var sel = selected[j][k];

              // If the header '' is selected, or if the value matches
              // the selected option, no need to check rest of selected options.
              if (sel === "" || sel === value)
              {
                found = true;
                break;
              }
              else
              {
                found = false;
              }
            }

            // Group value doesn't match any selected options,
            // no need to continue with this group.
            if (!found)
            {
              break;
            }
          }

          // If all group values tested are found in the selected options, add
          // the rest of the group values to the selects for updating.
          if (found)
          {
            // Loop through the remaining values and add to options arrays.
            for (var l = selectIndex + 1, gutl = group.uTypes.length; l < gutl;
                 l++)
            {
              value = values[l];
              if (!ifArrayContains(options[l], value))
              {
                options[l][options[l].length] = value;
              }
            }
          }
        }
      }
    }

    /*
     * Get the options for all selects.
     *
     * @group - group containing the selects data.
     * @selected - 2d array of selected option values.
     * @returns - 2d array of options.
     */
    function getAllOptions(group, selected)
    {
      var options = [];
      for (var i = 0, gutl = group.uTypes.length; i < gutl; i++)
      {
        var o = getOptions(group, selected, i);
        options[i] = o[i];
      }
      return options;
    }

    /**
     * Updates the selects with the options.
     *
     * @group - group containing names array.
     * @selectIndex - index into the group.utypes array.
     * @selected - 2d array of selected option values.
     * @options - 2d array of option values.
     * @updateAllOptions - if true update all the options, if false update
     *   options starting at selectIndex.
     * @private
     */
    this._setOptions = function (group, selectIndex, selected, options,
                                 updateAllOptions)
    {
      if (updateAllOptions)
      {
        selectIndex = 0;
      }

      // Update the selects with new options.
      for (var i = selectIndex, gul = group.uTypes.length; i < gul; i++)
      {
        // Build the id for the next select.
        var id = group.uTypes[i];

        // Get the select element.
        var select = document.getElementById(id);
        var selectItems = options[i];

        // If select is null, hidden attribute, can't update.
        if (select !== null)
        {
          var customSorter;

          if (ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES.hasOwnProperty(group.uTypes[i]))
          {
            customSorter = ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES[
              group.uTypes[i]];
          }
          else if (/Instrument\.name/i.test(id))
          {
            // Obtain only those fresh instruments that are part of the
            // selected items.
            var currFreshInstruments = this.freshInstruments.sort().filter(function (i)
                                                                           {
                                                                             return selectItems.indexOf(i) >=
                                                                                    0;
                                                                           });
            var staleInstruments = selectItems.filter(function (i)
                                                      {
                                                        return currFreshInstruments.indexOf(i)
                                                               < 0;
                                                      }).sort();

            selectItems = [];

            selectItems = selectItems.concat(currFreshInstruments);
            if (currFreshInstruments.length > 0)
            {
              selectItems.push(ca.nrc.cadc.search.datatrain.SPACER());
            }
            selectItems = selectItems.concat(staleInstruments);

            customSorter = "NONE";
          }
          else
          {
            customSorter = undefined;
          }

          if (customSorter !== "NONE")
          {
            // Use the custom sorter if it's available.
            selectItems.sort(customSorter);
          }

          // Add the new options to the child select.
          this._setSelectOptions(select, selectItems, selected[i]);
        }
      }
    };

    this.setGroups = function (_groups)
    {
      this.groups = _groups;
    };

    /**
     * Create a new HTML Option object as a jQuery object.
     *
     * @param _label             The option's display label.
     * @param _value            The option's value.
     * @param _selectedFlag     Boolean selected or not.
     * @returns {*|jQuery|HTMLElement}
     */
    function createOption(_label, _value, _selectedFlag)
    {
      var $option = $("<option>");

      $option.val(_value);
      if (_label.indexOf("&") === 0)
      {
        $option.html(_label);
      }
      else
      {
        $option.text(_label);
      }

      $option.prop("selected", _selectedFlag);
      $option.attr("selected", _selectedFlag);

      return $option;
    }

    /**
     * Updates the select with the options.
     *
     * @param select {Element} - select element to update.
     * @param options - array of options values for this select.
     * @param selected - array of selected option values for this select.
     * @private
     */
    this._setSelectOptions = function(select, options, selected)
    {
      // Remove all the current options for this select.
      var $select = $(select);
      $select.empty();

      var title = this.getDataTrainHeader("All");
      var name = title + "  (" + options.length + ")";
      var highlight = false;
      var isHighlighted = false;

      if (ifArrayContains(selected, ""))
      {
        highlight = true;
        isHighlighted = true;
      }

      var selectName = $select.attr("name");
      var $allOption = createOption(name, "", false);
      $select.append($allOption);

      // Add the new options to the select.
      for (var i = 0, ol = options.length; i < ol; i++)
      {
        var optionValue = options[i];
        highlight = false;
        if (ifArrayContains(selected, options[i]))
        {
          highlight = true;
          isHighlighted = true;
        }

        var optionName;

        if ((selectName.indexOf("dataProductType") >= 0)
            && (optionValue === "null"))
        {
          optionName = "Other";
        }
        else if ((selectName === "Plane.energy.emBand")
                 && (optionValue === "null"))
        {
          optionName = "Unknown";
        }
        else if (/calib.*Level/.test(selectName))
        {
          if (optionValue === "null")
          {
            optionName = "Unknown";
          }
          else
          {
            var calLevelName =
              ca.nrc.cadc.search.datatrain.CALIBRATION_LEVEL_MAP[optionValue];
            if (calLevelName)
            {
              optionName = "(" + optionValue + ") " + calLevelName;
            }
          }
        }
        else
        {
          optionName = optionValue;
        }

        var $opt = createOption(optionName, optionValue, highlight);

        if (ca.nrc.cadc.search.datatrain.SPACER() === optionValue)
        {
          $opt.val("SPACER");
          $opt.attr("id", selectName + "_SPACER");
          $opt.prop("disabled", true);
          $opt.attr("disabled", "disabled");
        }

        $select.append($opt);
      }

      if (!isHighlighted)
      {
        $allOption.prop("selected", true);
        $allOption.attr("selected", true);
      }
    };

    /**
     * Searches an array for the given value. Returns true if the value is
     * found in the array, false otherwise.
     *
     * @array - array to search.
     * @value - value to search for.
     * @returns {boolean} true if the value exists in the array, false
     *   otherwise.
     */
    function ifArrayContains(array, value)
    {
      if (array)
      {
        for (var i = 0; i < array.length; i++)
        {
          if (array[i] === value)
          {
            return true;
          }
        }
      }

      return false;
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param _event       The Event to fire.
     * @param _args        Arguments to the event.
     * @returns {*}       The event notification result.
     * @private
     */
    this._trigger = function (_event, _args)
    {
      var args = _args || {};
      args.dataTrain = this;

      return $(this).trigger(_event, _args);
    };

    /**
     * Subscribe to one of this form's events.
     *
     * @param _event      Event object.
     * @param __handler   Handler function.
     */
    this.subscribe = function (_event, __handler)
    {
      $(this).on(_event.type, __handler);
    };

    // Subsribe to events before init is called.
    this.subscribe(ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
                   function (event, args)
                   {
                     var dt = args.dataTrain;
                     dt.load(args.data);
                     dt._toggleLoading();
                   });

    this.subscribe(ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
                   function (event, args)
                   {
                     alert("Error while querying TAP to initialize the page: "
                           + args.responseText);
                     var dt = args.dataTrain;
                     dt._toggleLoading();
                   });

    if (this.options.autoInit === true)
    {
      this.init();
    }
  }

})(jQuery, window);
