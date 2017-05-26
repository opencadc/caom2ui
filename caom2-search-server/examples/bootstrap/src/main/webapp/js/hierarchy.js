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

(function ($)
{
  var currentURI = new cadc.web.util.currentURI();

  $.extend(true, window, {
      "ca": {
        "nrc": {
          "cadc": {
            "search": {
              "datatrain": {
                "SELECT_DISPLAY_OPTION_COUNT": 12,
                "SPACER_CHAR": "&#9472;",
                /**
                 * @return {string}
                 */
                "SPACER": function()
                {
                  var val = "";
                  for (var s = 0; s < 20; s++)
                  {
                    val += ca.nrc.cadc.search.datatrain.SPACER_CHAR;
                  }

                  return val;
                },
                "tap":
                {
                  "INSTRUMENT_FRESH_MJD_FIELD_NAME": {
                    "caom2": "max_time_bounds_cval1",
                    "obscore": "max_t_min"
                  },
                  "TABLE": {
                    "caom2": "caom2.enumfield",
                    "obscore": "caom2.obscoreenumfield"
                  },
                  "UTYPE_COLUMN_NAME_KEYS":
                  {
                    "caom2": {
                      "Plane.energy.emBand":
                        {
                          tap_column_name: "energy_emband",
                          size: 1
                        },
                      "Observation.collection":
                        {
                          tap_column_name: "collection",
                          size: 2
                        },
                      "Observation.instrument.name":
                        {
                          tap_column_name: "instrument_name",
                          size: 2
                        },
                      "Plane.energy.bandpassName":
                        {
                          tap_column_name: "energy_bandpassname",
                          size: 2
                        },
                      "Plane.calibrationLevel":
                        {
                          tap_column_name: "calibrationlevel",
                          size: 2
                        },
                      "Plane.dataProductType":
                        {
                          tap_column_name: "dataproducttype",
                          size: 1
                        },
                      "Observation.type":
                        {
                          tap_column_name: "type",
                          size: 2
                        }
                    },
                    "obscore": {
                      "DataID.Collection": "obs_collection",
                      "Provenance.ObsConfig.Facility.name": "facility_name",
                      "Provenance.ObsConfig.Instrument.name": "instrument_name",
                      "ObsDataset.calibLevel": "calib_level",
                      "ObsDataset.dataProductType": "dataproduct_type"
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
                  "Observation.collection": function(val1, val2) {
                    return ca.nrc.cadc.search.datatrain.sortCollections(val1, val2);
                  },
                  "DataID.Collection": function(val1, val2) {
                    return ca.nrc.cadc.search.datatrain.sortCollections(val1, val2);
                  },
                  "Plane.calibrationLevel": function(val1, val2) {
                    return ca.nrc.cadc.search.datatrain.sortNumericDescending(val1, val2);
                  },
                  "Obs.calibLevel": function(val1, val2) {
                    return ca.nrc.cadc.search.datatrain.sortNumericDescending(val1, val2);
                  }
                },
                "ENDPOINT": currentURI.getPath() + "tap/sync",
                "DataTrain": DataTrain,
                "DataTrainUType": DataTrainUType,
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
})(jQuery);

function DataTrainUType()
{
  var _UTYPE_DELIMITER = '/';
  var _UTYPE_VALUE_DELIMITER = ':';

  /**
   * Parse the string into an array of uType names with their hidden values,
   * if any.
   *
   * @param {String} _utypeTrain
   * @returns {Array}
   */
  this.parse = function(_utypeTrain)
  {
    var parsedUTypeValues = [];

    if (_utypeTrain)
    {
      var uTypeTrainItems = _utypeTrain.split(_UTYPE_DELIMITER);

      for (var i  = 0, uttil = uTypeTrainItems.length; i < uttil; i++)
      {
        var uTypeTrainItem = uTypeTrainItems[i];
        var uTypeTrainItemValues = uTypeTrainItem.split(_UTYPE_VALUE_DELIMITER);

        var nextUType = uTypeTrainItemValues[0];
        var nextUTypeObj = {name: nextUType};

        if (uTypeTrainItemValues.length > 1)
        {
          nextUTypeObj.values = parsedUTypeValues.slice(1);
        }

        parsedUTypeValues.push(nextUTypeObj);
      }
    }

    return parsedUTypeValues;
  };
}

function DataTrain(_modelDataSource, _autoInitFlag)
{
  var _self = this;
  var dataTrainUType = new DataTrainUType();

  this.modelDataSource = _modelDataSource;
  this.pageLanguage = $("html").attr("lang");
  this.$dataTrainDOM = $("div[id='" + _self.modelDataSource + "@Hierarchy']");
  this.uTypes = dataTrainUType.parse(_self.$dataTrainDOM.data('utypes'));
  this.groups = [];
  this.freshInstruments = [];


  function init()
  {
    var tapQuery = createTAPQuery();

    $.get(ca.nrc.cadc.search.datatrain.ENDPOINT, {
      REQUEST: "doQuery",
      LANG: "ADQL",
      FORMAT: "CSV",
      QUERY: tapQuery
    }).done(function (data)
            {
              load(data);
              toggleLoading();
              trigger(ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded);
            })
      .fail(function (jqXHR)
            {
              alert("Error while querying TAP to initialize the page: "
                    + jqXHR.responseText);
              toggleLoading();
              trigger(ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail);
            });
  }

  /**
   * Create the TAP query to obtain the Data Train values.
   * @returns {string}
   */
  function createTAPQuery()
  {
    var tapColumns = [];

    for (var i = 0, ul = _self.uTypes.length; i < ul; i++)
    {
      // tapColumns.push(_self.uTypes[i].split('.').slice(1).join("_").toLowerCase());
      tapColumns.push(ca.nrc.cadc.search.datatrain.tap.UTYPE_COLUMN_NAME_KEYS[_self.modelDataSource][_self.uTypes[i].name].tap_column_name);
    }

    var now = new Date();
    var dateThreshold = new Date(now.getFullYear() - 5, now.getMonth(),
                                 now.getDate(), now.getHours(),
                                 now.getMinutes(), now.getSeconds(),
                                 now.getMilliseconds());

    var mjdConverter =
      new ca.nrc.cadc.search.unitconversion.MJDConverter(dateThreshold);
    var mjdCondition = ", CASE WHEN "
                       + ca.nrc.cadc.search.datatrain.tap.INSTRUMENT_FRESH_MJD_FIELD_NAME[_self.modelDataSource]
                       + " >= " + mjdConverter.convert()
                       + " THEN 1 ELSE 0 END ";

    return "SELECT " + tapColumns.join(",") + mjdCondition
           + " FROM "
           + ca.nrc.cadc.search.datatrain.tap.TABLE[_modelDataSource];
  }

  /**
   * Do an initial load of all of the groupings.  This will parse the given
   * CSV data and append the resulting selects tot he given container.
   *
   * @param data                The CSV data from the response.
   */
  function load(data)
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
    group.uTypes = _self.uTypes;

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
        var formattedVal = (val != null) ? val.replace(plus, " ") : "null";
        var freshFlag = (groupValues[gvl - 1] == 1);
        var instrumentName = groupValues[instrumentNameIndex];

        if ((freshFlag === true)
            && (_self.freshInstruments.indexOf(instrumentName) < 0))
        {
          _self.freshInstruments.push(instrumentName);
        }

        group.values[i][j] = formattedVal;
      }
    }

    // Add the group object to the global groups.
    _self.groups.push(group);

    // Build the table with the selects and get the first select.
    var select = buildTable(group);

    updateLists(select, true);
  }

  function buildTable(_group)
  {
    // Keep track of the first non-hidden select.
    var firstSelect;
    var columnManager = new ca.nrc.cadc.search.columns.ColumnManager();

    // Loop through each attribute.
    for (var i = 0, groupUTypesLength = _group.uTypes.length;
         i < groupUTypesLength; i++)
    {
      // Get the JSON text from hidden input and
      // eval into an enumerated object.
      var uType = _group.uTypes[i];
      // var input = document.getElementById(uType + ".json");
      var columnOptionObj = columnManager.getColumnOption(_modelDataSource + ":" + uType.name);
      var enumerated = {utype: uType.name, hidden: false,
        label: getDataTrainHeader(columnOptionObj.label)};
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
        containerElement.className = "col-sm-1";
        select = buildSelect(enumerated, containerElement);

        if (firstSelect == undefined)
        {
          firstSelect = select.childNodes[1];
        }
      }

      // Add <select> to the table cell.
      _self.$dataTrainDOM.append(select);
    }

    // Return first select.
    return firstSelect;
  }

  function toggleLoading()
  {
    // Remove temporary div.
    var $building = _self.$dataTrainDOM.find('.building');
    $building.removeClass();
    $building.addClass('hidden');
  }



  /*
   * Creates a select Input Object, assigning values from the
   * enumeration Object.
   *
   * @enumerated - enumerated Object.
   */
  function buildSelect(enumerated, containerElement)
  {
    var label = document.createElement("label");
    if (_self.pageLanguage === "fr")
    {
      label.className = "advanced_search_hierarchy_select_div_label";
    }

    var labelSpanFieldName = document.createElement("span");

    var select = document.createElement("select");
    select.id = enumerated.utype;
    select.name = select.id;
    select.title = getDataTrainHeader(enumerated.label);

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

    select.onchange = function ()
    {
      updateLists(this);
    };

    select.className = "form-control col-md-1 datatrain-select";

    containerElement.appendChild(label);
    containerElement.appendChild(select);

    return containerElement;
  }

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

  function getDataTrainHeader(name)
  {
    if (_self.pageLanguage == "fr")
    {
      return getFrenchDataTrainHeaderMap()[name];
    }
    else
    {
      return name;
    }
  }

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

  /*
   * Return the group when the group utype contains the given utype,
   * or null if no group utype is found containing the given utype.
   *
   * @param _uType - uType of the select.
   * @returns group object or null if not found.
   */
  function getGroupByUType(_uType)
  {
    // Loop through the group names looking for name.
    for (var i = 0, gl = _self.groups.length; i < gl; i++)
    {
      var group = _self.groups[i];
      var groupUTypes = group.uTypes;
      for (var j = 0; j < groupUTypes.length; j++)
      {
        if (groupUTypes[j].name === _uType)
        {
          return group;
        }
      }
    }

    return null;
  }

  /*
   * Updates the selects. If updateAllOptions is true then all selects are
   * updated. If updateAllOptions is false, then only the given select,
   * and any selects to the right are updated.
   *
   * @select - the select element that triggered this function.
   * @updateAllOptions - boolean, update all selects if true, otherwise update
   * the given select and any selects to the right.
   */
  function updateLists(_select, _updateAllOptionsFlag)
  {
    // Parse out the unique id for the hierarchy and the attribute name.
    var uType = _select.id;

    // Find the group for this single uType.
    var group = getGroupByUType(uType);

    // Find the index in group.utypes array for this enumerated utype,
    // gives index of the select.
    var selectIndex = getSelectIndex(group, uType);

    // Get the selected options.
    var selected;
    if (_updateAllOptionsFlag)
    {
      selected = getSelectedOptions(group, group.uTypes.length);
    }
    else
    {
      selected = getSelectedOptions(group, selectIndex);
    }

    // Get the options for the selects being updated.
    var options;
    if (_updateAllOptionsFlag)
    {
      options = getAllOptions(group, selected);
    }
    else
    {
      options = getOptions(group, selected, selectIndex);
    }

    // Update the selects with new options.
    setOptions(group, selectIndex, selected, options, _updateAllOptionsFlag);
  }

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
      if (group.uTypes[i].name === uType)
      {
        selectIndex = i;
        break;
      }
    }

    // If the attribute is not found in the group.utypes throw an error.
    if (selectIndex == -1)
    {
      throw new Error(uType + " not found in group names[" + group.uTypes + "]");
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
      var select = document.getElementById(group.uTypes[i].name);
      if (select == null)
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
          if ((option.length > 4) && option.substring(3, 0) == "All")
          {
            break;
          }
        }
      }
    }
    else
    {
      if (select.selectedIndex != -1)
      {
        selected[selected.length] = select.options[select.selectedIndex].value;
      }
    }

    // If no option(s) are selected, select the top empty header option.
    if (selected.length == 0)
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
    if (selectIndex == 0)
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
        // selected options to determine which values to display in the current
        // select.
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
            if (sel == "" || sel == value)
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

        // If all group values tested are found in the selected options, add the
        // selectIndex values to the selectIndex options
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

        // Loop through the values that represent the selects not being updated,
        // and use matches between values and selected options to determine
        // which values to display in the selects being updated.
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
            if (sel == "" || sel == value)
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

        // If all group values tested are found in the selected options, add the
        // rest of the group values to the selects for updating.
        if (found)
        {
          // Loop through the remaining values and add to options arrays.
          for (var l = selectIndex + 1, gutl = group.uTypes.length; l < gutl; l++)
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

  /*
   * Updates the selects with the options.
   *
   * @group - group containing names array.
   * @selectIndex - index into the group.utypes array.
   * @selected - 2d array of selected option values.
   * @options - 2d array of option values.
   * @updateAllOptions - if true update all the options, if false update options
   * starting at selectIndex.
   */
  function setOptions(group, selectIndex, selected, options,
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
      if (select != null)
      {
        var customSorter;

        if (ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES.
            hasOwnProperty(group.uTypes[i]))
        {
          customSorter = ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES[
            group.uTypes[i]];
        }
        else if (/Instrument\.name/i.test(id))
        {
          // Obtain only those fresh instruments that are part of the selected items.
          var currFreshInstruments = _self.freshInstruments.sort().filter(function (i)
                                                                          {
                                                                            return selectItems.indexOf(i) >= 0;
                                                                          });
          var staleInstruments = selectItems.filter(function(i) {
            return currFreshInstruments.indexOf(i) < 0;
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

        if (customSorter != "NONE")
        {
          // Use the custom sorter if it's available.
          selectItems.sort(customSorter);
        }

        // Add the new options to the child select.
        setSelectOptions(select, selectItems, selected[i]);
      }
    }
  }

  function setGroups(_groups)
  {
    _self.groups = _groups;
  }

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

  /*
   * Updates the select with the options.
   *
   * @select - select element to update.
   * @options - array of options values for this select.
   * @selected - array of selected option values for this select.
   */
  function setSelectOptions(select, options, selected)
  {
    // Remove all the current options for this select.
    var $select = $(select);
    $select.empty();

    var title = getDataTrainHeader("All");
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
          && (optionValue == "null"))
      {
        optionName = "Other";
      }
      else if ((selectName === "Plane.energy.emBand")
               && (optionValue == "null"))
      {
        optionName = "Unknown";
      }
      else if (/calib.*Level/.test(selectName))
      {
        if (optionValue == "null")
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

      if (ca.nrc.cadc.search.datatrain.SPACER() == optionValue)
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
  }

  /*
   * Searches an array for the given value. Returns true if the value is
   * found in the array, false otherwise.
   *
   * @array - array to search.
   * @value - value to search for.
   * @returns - true if the value exists in the array, false otherwise.
   */
  function ifArrayContains(array, value)
  {
    if (array == undefined)
    {
      return false;
    }

    for (var i = 0; i < array.length; i++)
    {
      if (array[i] == value)
      {
        return true;
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
   */
  function trigger(_event, _args)
  {
    var args = _args || {};
    args.dataTrain = _self;

    return $(_self).trigger(_event, _args);
  }

  /**
   * Subscribe to one of this form's events.
   *
   * @param _event      Event object.
   * @param __handler   Handler function.
   */
  function subscribe(_event, __handler)
  {
    $(_self).on(_event.type, __handler);
  }

  if (_autoInitFlag)
  {
    init();
  }

  $.extend(this, {
    "init": init,

    // Exposed for sorting.
    "setGroups": setGroups,
    "load": load,

    // Event handling.
    "subscribe": subscribe,
    "updateLists": updateLists
  });
}