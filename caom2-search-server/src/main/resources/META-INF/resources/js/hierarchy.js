/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2019.                            (c) 2019.
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

;(function($, window) {
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            datatrain: {
              URI_MATCH_REGEX: /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/,
              SELECT_DISPLAY_OPTION_COUNT: 12,
              SPACER_CHAR: '&#9472;',
              /**
               * Obtain a spacer formatter.
               *
               * @returns {string}
               * @constructor
               */
              SPACER: function() {
                var val = ''
                for (var s = 0; s < 20; s++) {
                  val += ca.nrc.cadc.search.datatrain.SPACER_CHAR
                }

                return val
              },
              tap: {
                QUERY_TEMPLATE:
                  'SELECT {1}, (CASE WHEN {2} >= {3} THEN 1 ELSE 0 END) as cs FROM {4} GROUP BY {1}, cs',
                INSTRUMENT_FRESH_MJD_FIELD_NAME: {
                  caom2: 'max_time_bounds_cval1',
                  obscore: 'max_t_min'
                },
                TABLE: {
                  caom2: 'caom2.enumfield',
                  obscore: 'caom2.obscoreenumfield'
                }
              },
              CALIBRATION_LEVEL_MAP: {
                0: 'Raw Instrumental',
                1: 'Raw Standard',
                2: 'Calibrated',
                3: 'Product',
                4: 'Analysis Product'
              },
              COLLECTION_ORDER: [
                'CFHT*',
                'HST*',
                'JWST',
                'GEMINI*',
                'JCMT*',
                'DAO*',
                'RACS',
                'WALLABY',
                'SUBARU*'
              ],
              sortCollections: function(val1, val2) {
                /**
                 * Function to look for those collections that encompass a matching group (i.e. end in "*").
                 * @param {*} val The collection to check against the set of known Collections.
                 * @returns int index of matched Collection, or -1 if no match
                 */
                const fuzzyMatchIndex = function(val) {
                  const collectionLength = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER.length
                  for (let i = 0; i < collectionLength; i++) {
                    const nextCollection = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER[i]
                    const fuzzyIndex = nextCollection.indexOf('*')
                    if (fuzzyIndex > 0) {
                      const collection = nextCollection.substring(0, fuzzyIndex)
                      if (val.startsWith(collection)) {
                        return i
                      }
                    }
                  }

                  // Default unmatched value
                  return -1
                }

                // Get exact matches first.
                let val1Index = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER.indexOf(val1)
                let val2Index = ca.nrc.cadc.search.datatrain.COLLECTION_ORDER.indexOf(val2)

                if (val1Index < 0) {
                  val1Index = fuzzyMatchIndex(val1)
                }

                if (val2Index < 0) {
                  val2Index = fuzzyMatchIndex(val2)
                }

                // Put garbage at the bottom
                if (val2Index < 0) {
                  return -1
                } else if (val1Index < 0) {
                  return 1
                } else {
                  return val1Index - val2Index
                }
              },
              sortNumericDescending: function(val1, val2) {
                var descVal

                // Put garbage at the bottom
                if (val1 === null || isNaN(val1)) {
                  descVal = 1
                } else if (val2 === null || isNaN(val2)) {
                  descVal = -1
                } else {
                  descVal = val2 - val1
                }

                return descVal
              },
              CUSTOM_SORT_UTYPES: {
                'Observation.collection': function(val1, val2) {
                  return ca.nrc.cadc.search.datatrain.sortCollections(
                    val1,
                    val2
                  )
                },
                'DataID.Collection': function(val1, val2) {
                  return ca.nrc.cadc.search.datatrain.sortCollections(
                    val1,
                    val2
                  )
                },
                'Plane.calibrationLevel': function(val1, val2) {
                  return ca.nrc.cadc.search.datatrain.sortNumericDescending(
                    val1,
                    val2
                  )
                },
                'Obs.calibLevel': function(val1, val2) {
                  return ca.nrc.cadc.search.datatrain.sortNumericDescending(
                    val1,
                    val2
                  )
                }
              },
              SYNC_ENDPOINT: '/sync',
              DataTrain: DataTrain,
              events: {
                onDataTrainLoaded: new jQuery.Event(
                  'AdvancedSearch:onDataTrainLoaded'
                ),
                onDataTrainLoadFail: new jQuery.Event(
                  'AdvancedSearch:onDataTrainLoadFail'
                )
              }
            }
          }
        }
      }
    }
  })

  /**
   * @param {String} _modelDataSource   Name of the data source [caom2 | obscore]
   * @param {ColumnManager} _columnManager   Column Manager instance.
   * @param {{}} _options   Options to instantiate this DataTrain.
   * @param {boolean} [_options.autoInit=false]   Whether to initialize on creation.
   * @constructor
   */
  function DataTrain(_modelDataSource, _columnManager, _options) {
    var stringUtil = new org.opencadc.StringUtil()
    var _dt = this

    this.modelDataSource = _modelDataSource
    this.pageLanguage = $('html').attr('lang')
    this.$dataTrainDOM = $("div[id='" + this.modelDataSource + "_data_train']")
    this.$dtTableDOM = $('.' + this.modelDataSource + '_dtTableDiv')
    this.uType = this.$dataTrainDOM.find('.hierarchy_utype').text()
    this.groups = []
    this.freshInstruments = []

    this.defaults = {
      autoInit: false,
    }

    this.options = $.extend({}, true, this.defaults, _options)
    // tapClient is available at this.options.tapClient
    this.columnManager = _columnManager

    /**
     * Obtain a column configuration object.
     * @param {String}  _uType    The uType.
     * @return {{}}
     * @private
     */
    this._getColumnConfig = function(_uType) {
      return this.columnManager.getColumnOptions()[
        this.modelDataSource + ':' + _uType
      ]
    }

    /**
     * Initialize this DataTrain.
     */
    this.init = function() {
      this._toggleLoading(true)
      this._attachListeners()
      this._loadDataTrain()
    }

    this._attachListeners = function () {
      this.options.tapClient.subscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientOK, this.loadDataTrainOK)
      this.options.tapClient.subscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientFail, this.loadDataTrainNOK)
      $(".reloadHierarchySubmit").on('click', this._reloadDataTrain)
    }

    this.loadDataTrainOK = function(event, args) {
      var callingId = args.callerId

      if (callingId === _dt.modelDataSource) {
        var data = args.data
        _dt._trigger(
          ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
          {data: data}
        )
      }
    }

    this.loadDataTrainNOK = function(event, args) {
      var callingId = args.callerId

      if (callingId === _dt.modelDataSource) {
        _dt._trigger(
          ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
          {responseText: args.responseText}
        )
      }
    }

    /**
     * Make call to server to get TAP data to load into DataTrain
     * @private
     */
    this._loadDataTrain = function() {
      var tapQuery = this._createTAPQuery()
      this.options.tapClient.postTAPRequest(tapQuery, 'CSV', this.modelDataSource)
    }

    /**
     * Reload the Data Train.
     * @private
     */
    this._reloadDataTrain = function() {
      _dt._setDataTrainDisplayState('loading')
      _dt._loadDataTrain()
    }

    /**
     * Toggle the data train reload button.
     * @private
     */
    this._toggleReloadButton = function(turnOn) {
      var $reloadHierarchyDiv = $('.reloadHierarchy')
      if (turnOn === true) {
        $reloadHierarchyDiv.removeClass('hidden')
      } else {
        $reloadHierarchyDiv.addClass('hidden')
      }
    }

    /**
     * Set state of Data Train display.
     * @private
     */
    this._setDataTrainDisplayState = function(stateName) {
      switch(stateName) {
        case 'loading' :
          this._toggleLoading(true)
          this._toggleReloadButton(false)
          break
        case 'reload' :
          this._toggleLoading(false)
          this._toggleReloadButton(true)
          break
        case 'dataTrain':
          this._toggleLoading(false)
          this._toggleReloadButton(false)
          break
      }
    }


    /**
     * Create the TAP query to obtain the Data Train values.
     * @returns {string}
     * @private
     */
    this._createTAPQuery = function() {
      var uTypes = this.uType.split('/')
      var tapColumns = []

      for (var i = 0, ul = uTypes.length; i < ul; i++) {
        var nextUType = uTypes[i]
        var colOpts = this._getColumnConfig(nextUType)

        if (colOpts.hasOwnProperty('tap_column_name')) {
          tapColumns.push(colOpts.tap_column_name)
        } else {
          tapColumns.push(
            nextUType.substring(nextUType.indexOf('.') + 1).replace('.', '_')
          )
        }
      }

      var now = new Date()
      var dateThreshold = new Date(
        now.getFullYear() - 5,
        now.getMonth(),
        now.getDate(),
        now.getHours(),
        now.getMinutes(),
        now.getSeconds(),
        now.getMilliseconds()
      )
      var mjdConverter = new ca.nrc.cadc.search.unitconversion.MJDConverter(
        dateThreshold
      )

      return stringUtil.format(
        ca.nrc.cadc.search.datatrain.tap.QUERY_TEMPLATE,
        [
          tapColumns.join(','),
          ca.nrc.cadc.search.datatrain.tap.INSTRUMENT_FRESH_MJD_FIELD_NAME[
            this.modelDataSource
          ],
          mjdConverter.convert(),
          ca.nrc.cadc.search.datatrain.tap.TABLE[_modelDataSource]
        ]
      )
    }

    /**
     * Do an initial load of all of the groupings.  This will parse the given
     * CSV data and append the resulting selects to the given container.
     *
     * @param {String} data                The CSV data from the response.
     */
    this.load = function(data) {
      var arrayOfRows = $.csv.toArrays(data)
      var firstRow = arrayOfRows[0]

      // Skip the first row (Start at 1).
      arrayOfRows = arrayOfRows.slice(1)

      // The instrument name is handled separately.
      var instrumentNameIndex = firstRow.indexOf('instrument_name')

      // Add id for this div.
      var group = {}

      // Put an array of uType names into an array.
      group.uTypes = this.uType.split('/')

      // Put an array of uType values into an array.
      group.values = []

      for (var i = 0, al = arrayOfRows.length; i < al; i++) {
        group.values[i] = arrayOfRows[i]
        var groupValues = group.values[i]

        for (var j = 0, gvl = groupValues.length; j < gvl - 1; j++) {
          // Last column is the state.
          var val = $.trim(groupValues[j])
          var formattedVal = val === null || val === '' ? 'null' : val
          var freshFlag = groupValues[gvl - 1] === '1'
          var instrumentName = groupValues[instrumentNameIndex]

          if (
            freshFlag === true &&
            this.freshInstruments.indexOf(instrumentName) < 0
          ) {
            this.freshInstruments.push(instrumentName)
          }

          group.values[i][j] = formattedVal
        }
      }

      // Add the group object to the global groups.
      this.groups.push(group)

      // Build the table with the selects and get the first select.
      var select = this._buildTable(group)

      this.updateLists(select, true)
    }

    /**
     * Clear the existing set of data train tables
     * @private
     */
    this._clearTable = function() {
      if (this.$dtTableDOM.children().length > 0) {
        this.$dtTableDOM.empty()
      }
    }

    /**
     * Construct a DOM of a select and append it.
     * @param _group
     * @returns {*}
     * @private
     */
    this._buildTable = function(_group) {
      // Keep track of the first non-hidden select.
      var firstSelect
      var groupUTypesLength = _group.uTypes.length
      var rightPosIdx = groupUTypesLength / 2

      // Loop through each attribute.
      for (
        var i = 0; i < groupUTypesLength; i++) {
        // Get the JSON text from hidden input and
        // eval into an enumerated object.
        var row = _group.values[i]

        // Create either hidden or select input.
        var select
        var containerElement = document.createElement('div')
        containerElement.className = 'text-left col-md-1 hierarchy'

        // Position for popovers so they don't flop off the side of the screen
        var position
        if (i < rightPosIdx) {
          position = 'right'
        } else {
          position = 'left'
        }

        if (i === 0) {
          containerElement.className += ' row-start'
        } else if (i === groupUTypesLength - 1) {
          // Last item
          containerElement.className += ' row-end'
        }

        select = this._buildSelect(_group.uTypes[i], containerElement, position)

        if (firstSelect === undefined) {
          firstSelect = select.childNodes[1]
        }

        // Add <select> to the data train table div
        this.$dtTableDOM.append(select)
      }

      // Return first select.
      return firstSelect
    }

    /**
     * Toggle the loading icon.
     * @private
     */
    this._toggleLoading = function(turnOn) {
      var building = document.getElementById(this.uType + '.building')
      building.className = turnOn == true ? '' : 'hidden'
    }

    /**
     * Creates a select Input Object, assigning values from the
     * enumeration Object.
     *
     * @param {String} uType   The uType ID for this select.
     * @param {HTMLElement}     containerElement  The containing DOM.
     * @returns {*}
     * @private
     */
    this._buildSelect = function(uType, containerElement, position) {
      var label = document.createElement('label')

      var hidden = document.createElement('input')
      hidden.type = 'hidden'
      hidden.value = uType + '@Enumerated'
      hidden.name = 'Form.name'

      var labelSpanFieldName = document.createElement('span')
      var select = document.createElement('select')
      select.id = uType
      select.name = select.id
      select.title = this._getDataTrainHeader(
        this._getColumnConfig(uType).label
      )

      labelSpanFieldName.className = 'indent-small field-name'
      labelSpanFieldName.innerHTML = select.title + '<div data-toggle="dt-popover" data-utype="' + uType
          + '" data-placement="' + position + '" data-title="' + select.title
          + '" class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue popover-left" data-original-title="" title="">\n' +
          '  </div>'

      label.appendChild(labelSpanFieldName)
      label.setAttribute('for', select.id)

      select.label = label
      select.size = ca.nrc.cadc.search.datatrain.SELECT_DISPLAY_OPTION_COUNT
      select.multiple = true

      select.onchange = function(e) {
        this.updateLists(e.target, false)
      }.bind(this)

      select.className = 'form-control hierarchy_select'

      containerElement.appendChild(label)
      containerElement.appendChild(select)
      containerElement.appendChild(hidden)

      return containerElement
    }

    this.getFrenchDataTrainHeaderMap = function() {
      return {
        All: 'Tout',
        Band: "Domaine d'énergie",
        Collection: 'Collection',
        Instrument: 'Instrument',
        Filter: 'Filtre',
        'Cal. Lev.': 'Niveau de calibration',
        'Data Type': 'Type de donnée',
        'Obs. Type': "Type d'observation",
        obs_collection: 'obs_collection',
        facility_name: 'facility_name',
        instrument_name: 'instrument_name',
        calib_level: 'calib_level',
        dataproduct_type: 'dataproduct_type'
      }
    }

    /**
     * Obtain the header for OLA.
     *
     * @param {String}  name  Name key.
     * @return {String}   Header text.
     * @private
     */
    this._getDataTrainHeader = function(name) {
      return this.pageLanguage === 'fr'
        ? this.getFrenchDataTrainHeaderMap()[name]
        : name
    }

    /**
     * Return the group when the group utype contains the given utype,
     * or null if no group utype is found containing the given utype.
     *
     * @param {Array}   _groups Array of group items.
     * @param {String}  _uType  uType of the select.
     * @returns {Object} group object or null if not found.
     * @private
     */
    this._getGroupByUType = function(_groups, _uType) {
      // Loop through the group names looking for name.
      for (var i = 0, gl = _groups.length; i < gl; i++) {
        var group = _groups[i]
        var groupUTypes = group.uTypes
        for (var j = 0, gutl = groupUTypes.length; j < gutl; j++) {
          if (groupUTypes[j] === _uType) {
            return group
          }
        }
      }

      return null
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
    this.updateLists = function(_select, _updateAllOptionsFlag) {
      // Parse out the unique id for the hierarchy and the attribute name.
      var uType = _select.id

      // Find the group for this single uType.
      var group = this._getGroupByUType(this.groups, uType)

      // Find the index in group.utypes array for this enumerated utype,
      // gives index of the select.
      var selectIndex = this._getSelectIndex(group, uType)

      // Get the selected options.
      var selected = _updateAllOptionsFlag
        ? this._getSelectedOptions(group, group.uTypes.length)
        : this._getSelectedOptions(group, selectIndex)

      // Get the options for the selects being updated.
      var options = _updateAllOptionsFlag
        ? this._getAllOptions(group, selected)
        : this._getOptions(group, selected, selectIndex)

      // Update the selects with new options.
      this._setOptions(
        group,
        selectIndex,
        selected,
        options,
        _updateAllOptionsFlag
      )
    }

    /**
     * Find the index in the group.uTypes array for this uType.
     * Names is a list of hierarchy attributes.
     *
     * @group {{}} group containing uTypes array.
     * @uType {String} uType.
     * @return {number}   index of the uType in group.uTypes array.
     * @private
     */
    this._getSelectIndex = function(group, uType) {
      // Loop through the group names looking for name.
      var selectIndex = -1
      for (var i = 0, gutl = group.uTypes.length; i < gutl; i++) {
        if (group.uTypes[i] === uType) {
          selectIndex = i
          break
        }
      }

      // If the attribute is not found in the group.utypes throw an error.
      if (selectIndex === -1) {
        throw new Error(
          uType + ' not found in group names[' + group.uTypes + ']'
        )
      }

      return selectIndex
    }

    /**
     * Creates and returns an array of the selected options for the group.
     *
     * @group {{}} group data for this div.
     * @selectIndex {number} index into the group.utypes array.
     * @returns {[]} array of all selected options.
     * @private
     */
    this._getSelectedOptions = function(group, selectIndex) {
      // 2D array to hold selected options arrays.
      var selected = []

      // We want to include the selectIndex values as well.
      if (selectIndex < group.uTypes.length) {
        selectIndex++
      }

      // Loop through the selects and get the selected options from each.
      for (var i = 0; i < selectIndex; i++) {
        var select = document.getElementById(group.uTypes[i])

        if (select === null) {
          // If select is null it must be a hidden attribute,
          // selected values are then the attribute group values.
          selected[i] = []
          for (var j = 0; j < group.values.length; j++) {
            var v = group.values[j][i]
            if (!this._arrayContains(selected[i], v)) {
              selected[i][selected[i].length] = v
            }
          }
        } else if (select instanceof HTMLSelectElement) {
          selected[i] = this._getSelected(select)
        }
      }
      return selected
    }

    /**
     * Creates and returns an array of the selected option values
     * for the given select element.
     *
     * @param {HTMLSelectElement} select    select element.
     * @returns {[]} array of selected options.
     */
    this._getSelected = function(select) {
      // Array to hold selected options from this select.
      var selected = []

      // Check if multiple options can be selected.
      var multiple = select.type === 'select-multiple'

      // Read selected options into an array.
      if (multiple) {
        for (var i = 0; i < select.options.length; i++) {
          if (select.options[i].selected) {
            var option = select.options[i].value
            selected[selected.length] = option

            // If top empty option is selected, don't allow
            if (option.length > 4 && option.substring(3, 0) === 'All') {
              break
            }
          }
        }
      } else {
        if (select.selectedIndex !== -1) {
          selected[selected.length] = select.options[select.selectedIndex].value
        }
      }

      // If no option(s) are selected, select the top empty header option.
      if (selected.length === 0) {
        selected[0] = ''
      }

      return selected
    }

    /**
     * Get the options for the selects being updated.
     *
     * @param {{}} group    group containing selects data.
     * @param {[]} selected  2d array of selected option values.
     * @param {number} selectIndex index into the group.utypes array.
     * @returns {[]} 2d array of options.
     * @private
     */
    this._getOptions = function(group, selected, selectIndex) {
      // Arrays to hold the options for the selects to be updated.
      var options = []

      for (var i = selectIndex, gutl = group.uTypes.length; i < gutl; i++) {
        options[i] = []
      }

      // Get the options for the current select.
      this.getCurrentOptions(options, group, selected, selectIndex)

      // Get the options for any child selects.
      this.getChildOptions(options, group, selected, selectIndex)

      return options
    }

    /**
     * Get the options for the current select.
     *
     * @param {[]} options  2d array holding the options for each select.
     * @param {{}} group  Group containing the selects data.
     * @param {[]} selected   2d array of selected option values.
     * @param {number}  selectIndex   index into the group.utypes array.
     */
    this.getCurrentOptions = function(options, group, selected, selectIndex) {
      // The first select should always show all of the select options.
      if (selectIndex === 0) {
        for (var i = 0, gvl = group.values.length; i < gvl; i++) {
          if (!this._arrayContains(options[0], group.values[i][0])) {
            options[0][options[0].length] = group.values[i][0]
          }
        }
      } else {
        // Loop through the group values.
        for (i = 0; i < group.values.length; i++) {
          // Get this group of values.
          var values = group.values[i]

          // Indicates if values found that match selected options.
          var found = false

          // Loop through the values that represent the selects not being
          // updated,
          // including the current select, and use matches between values and
          // selected options to determine which values to display in the
          // current select.
          for (var j = 0; j < selectIndex; j++) {
            // Get one of the possible values for this select.
            var value = values[j]

            // Loop through the selected options and check if this value
            // is one of the selected options.
            for (var k = 0; k < selected[j].length; k++) {
              var sel = selected[j][k]

              // If the header '' is selected, or if the value matches
              // the selected option, no need to check rest of selected options.
              if (sel === '' || sel === value) {
                found = true
                break
              } else {
                found = false
              }
            }

            // Group value doesn't match any selected options,
            // no need to continue with this group.
            if (!found) {
              break
            }
          }

          // If all group values tested are found in the selected options, add
          // the selectIndex values to the selectIndex options
          if (found) {
            value = values[selectIndex]
            if (!this._arrayContains(options[j], value)) {
              options[selectIndex][options[selectIndex].length] = value
            }
          }
        }
      }
    }

    /**
     * Get the options for the child selects of the current select.
     *
     * @param {[]} options  2d array holding the options for each select.
     * @param {{}} group  Group containing the selects data.
     * @param {[]} selected   2d array of selected option values.
     * @param {number}  selectIndex   index into the group.utypes array.
     */
    this.getChildOptions = function(options, group, selected, selectIndex) {
      // If the current select is the last select, no children selects.
      if (selectIndex < group.uTypes.length - 1) {
        // Loop through the group values.
        for (var i = 0, gvl = group.values.length; i < gvl; i++) {
          // Get this group of values.
          var values = group.values[i]

          // Indicates if values found that match selected options.
          var found = false

          // Loop through the values that represent the selects not being
          // updated, and use matches between values and selected options to
          // determine which values to display in the selects being updated.
          for (var j = 0; j <= selectIndex; j++) {
            // Get one of the possible values for this select.
            var value = values[j]

            // Loop through the selected options and check if this value
            // is one of the selected options.
            for (var k = 0; k < selected[j].length; k++) {
              var sel = selected[j][k]

              // If the header '' is selected, or if the value matches
              // the selected option, no need to check rest of selected options.
              if (sel === '' || sel === value) {
                found = true
                break
              } else {
                found = false
              }
            }

            // Group value doesn't match any selected options,
            // no need to continue with this group.
            if (!found) {
              break
            }
          }

          // If all group values tested are found in the selected options, add
          // the rest of the group values to the selects for updating.
          if (found) {
            // Loop through the remaining values and add to options arrays.
            for (
              var l = selectIndex + 1, gutl = group.uTypes.length;
              l < gutl;
              l++
            ) {
              value = values[l]
              if (!this._arrayContains(options[l], value)) {
                options[l][options[l].length] = value
              }
            }
          }
        }
      }
    }

    /**
     * Get the options for all selects.
     *
     * @param {{}} group    group object containing the selects data.
     * @param {[]} selected   2d array of selected option values.
     * @returns {[]} 2d array of options.
     */
    this._getAllOptions = function(group, selected) {
      var options = []
      for (var i = 0, gutl = group.uTypes.length; i < gutl; i++) {
        var o = this._getOptions(group, selected, i)
        options[i] = o[i]
      }

      return options
    }

    /**
     * Updates the selects with the options.
     *
     * @param {{}} group    group containing names array.
     * @param {number}      selectIndex   index into the group.utypes array.
     * @param {[]}          selected    2d array of selected option values.
     * @param {[]}          options  2d array of option values.
     * @param {boolean}     updateAllOptions    If true update all the options, if false update options starting at
     * selectIndex.
     * @private
     */
    this._setOptions = function(
      group,
      selectIndex,
      selected,
      options,
      updateAllOptions
    ) {
      if (updateAllOptions) {
        selectIndex = 0
      }

      // Update the selects with new options.
      for (var i = selectIndex, gul = group.uTypes.length; i < gul; i++) {
        // Build the id for the next select.
        var id = group.uTypes[i]

        // Get the select element.
        var select = document.getElementById(id)
        var selectItems = options[i]

        // If select is null, hidden attribute, can't update.
        if (select !== null) {
          var customSorter

          if (
            ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES.hasOwnProperty(
              group.uTypes[i]
            )
          ) {
            customSorter =
              ca.nrc.cadc.search.datatrain.CUSTOM_SORT_UTYPES[group.uTypes[i]]
          } else if (/Instrument\.name/i.test(id)) {
            // Obtain only those fresh instruments that are part of the
            // selected items.
            var currFreshInstruments = this.freshInstruments
              .sort()
              .filter(function(i) {
                return selectItems.indexOf(i) >= 0
              })
            var staleInstruments = selectItems
              .filter(function(i) {
                return currFreshInstruments.indexOf(i) < 0
              })
              .sort()

            selectItems = []

            selectItems = selectItems.concat(currFreshInstruments)
            if (currFreshInstruments.length > 0) {
              selectItems.push(ca.nrc.cadc.search.datatrain.SPACER())
            }
            selectItems = selectItems.concat(staleInstruments)

            customSorter = 'NONE'
          } else {
            customSorter = undefined
          }

          if (customSorter !== 'NONE') {
            // Use the custom sorter if it's available.
            selectItems.sort(customSorter)
          }

          // Add the new options to the child select.
          this._setSelectOptions(select, selectItems, selected[i])
        }
      }
    }

    this.setGroups = function(_groups) {
      this.groups = _groups
    }

    /**
     * Create a new HTML Option object as a jQuery object.
     *
     * @param {String} _label             The option's display label.
     * @param {*} _value            The option's value.
     * @param {Boolean} _selectedFlag     Boolean selected or not.
     * @returns {*|jQuery|HTMLElement}
     */
    this._createOption = function(_label, _value, _selectedFlag) {
      var $option = $('<option>')

      $option.val(_value)
      if (_label.indexOf('&') === 0) {
        $option.html(_label)
      } else {
        $option.text(_label)
      }

      $option.prop('selected', _selectedFlag)
      $option.attr('selected', _selectedFlag)

      return $option
    }

    /**
     * Updates the select with the options.
     *
     * @param select {Element} - select element to update.
     * @param options - array of options values for this select.
     * @param selected - array of selected option values for this select.
     * @private
     */
    this._setSelectOptions = function(select, options, selected) {
      // Remove all the current options for this select.
      var $select = $(select)
      $select.empty()

      var title = this._getDataTrainHeader('All')
      var name = title + '  (' + options.length + ')'
      var highlight = false
      var isHighlighted = false

      var selectName = $select.attr('name')
      var $allOption = this._createOption(name, '', false)
      $select.append($allOption)

      // Add the new options to the select.
      for (var i = 0, ol = options.length; i < ol; i++) {
        var optionValue = options[i]
        highlight = false
        if (this._arrayContains(selected, options[i])) {
          highlight = true
          isHighlighted = true
        }

        var optionName

        if (
          selectName.indexOf('dataProductType') >= 0 &&
          optionValue === 'null'
        ) {
          optionName = 'Other'
        } else if (
          ca.nrc.cadc.search.datatrain.URI_MATCH_REGEX.test(optionValue)
        ) {
          optionName = new cadc.web.util.URI(optionValue).getHash()
        } else if (
          selectName === 'Plane.energy.emBand' &&
          optionValue === 'null'
        ) {
          optionName = 'Unknown'
        } else if (/calib.*Level/.test(selectName)) {
          if (optionValue === 'null') {
            optionName = 'Unknown'
          } else {
            var calLevelName =
              ca.nrc.cadc.search.datatrain.CALIBRATION_LEVEL_MAP[optionValue]
            if (calLevelName) {
              optionName = '(' + optionValue + ') ' + calLevelName
            } else {
              optionName = 'Unknown (' + optionValue + ')'
            }
          }
        } else {
          optionName = optionValue
        }

        var $opt = this._createOption(optionName, optionValue, highlight)

        if (ca.nrc.cadc.search.datatrain.SPACER() === optionValue) {
          $opt.val('SPACER')
          $opt.attr('id', selectName + '_SPACER')
          $opt.prop('disabled', true)
          $opt.attr('disabled', 'disabled')
        }

        $select.append($opt)
      }

      if (!isHighlighted) {
        $allOption.prop('selected', true)
        $allOption.attr('selected', true)
      }
    }

    /**
     * Searches an array for the given value. Returns true if the value is
     * found in the array, false otherwise.
     *
     * @param {[]} array  Array to search.
     * @param {*}  value  Value to search for.
     * @returns {boolean} true if the value exists in the array, false otherwise.
     */
    this._arrayContains = function(array, value) {
      if (array) {
        for (var i = 0; i < array.length; i++) {
          if (array[i] === value) {
            return true
          }
        }
      }

      return false
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param _event       The Event to fire.
     * @param _args        Arguments to the event.
     * @returns {*}       The event notification result.
     * @private
     */
    this._trigger = function(_event, _args) {
      var args = _args || {}
      args.dataTrain = this

      return $(this).trigger(_event, _args)
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param _event      Event object.
     * @param __handler   Handler function.
     */
    this.subscribe = function(_event, __handler) {
      $(this).on(_event.type, __handler)
    }

    // Subsribe to events before init is called.
    this.subscribe(
      ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
      function(event, args) {
        var dt = args.dataTrain
        dt.load(args.data)
        _dt._setDataTrainDisplayState('dataTrain')
      }
    )

    this.subscribe(
      ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
      function(event, args) {
        console.log(
          'Error while querying TAP to initialize the page: ' +
            args.responseText
        )
        _dt._setDataTrainDisplayState('reload')
      }
    )

    if (this.options.autoInit === true) {
      this.init()
    }
  }
})(jQuery, window)
