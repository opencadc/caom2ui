;(function($) {
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            ignore_fields: ['collection', 'noexec'],
            CAOM2_TARGET_NAME_FIELD_ID: 'Plane.position.bounds',
            OBSCORE_TARGET_NAME_FIELD_ID:
              'Char.SpatialAxis.Coverage.Support.Area',
            FORM_LABEL_INPUT_LENGTH: 12,
            TARGET_FORM_LABEL_INPUT_LENGTH: 24,
            CHECKBOX_CHECKED_REGEX: /^true|on$/g,
            AUTOCOMPLETE_TAP_REQUEST_DATA: {
              payload: {
                LANG: 'ADQL',
                FORMAT: 'CSV',
                QUERY:
                  "select {1} from caom2.distinct_{1} where lower({1}) like '%{2}%' order by {1}"
              },
              fields: {
                'Observation.proposal.pi': {
                  tap_column: 'proposal_pi'
                },
                'Observation.proposal.title': {
                  tap_column: 'proposal_title'
                },
                'Observation.proposal.id': {
                  tap_column: 'proposal_id'
                }
              }
            },
            CAOM2: {
              FormConfiguration: CAOM2FormConfiguration,
              config: {
                id: 'CAOM2',
                download_access_key: 'caom2:Plane.publisherID.downloadable',
                default_sort_column: 'caom2:Plane.time.bounds.lower',
                collection_select_id: 'Observation.collection',
                footprint_column_id: 'caom2:Plane.position.bounds',
                ra_column_id: 'caom2:Plane.position.bounds.cval1',
                dec_column_id: 'caom2:Plane.position.bounds.cval2',
                fov_column_id: 'caom2:Plane.position.bounds.area',
                uri_column_id: 'caom2:Plane.uri'
              }
            },
            ObsCore: {
              FormConfiguration: ObsCoreFormConfiguration,
              config: {
                id: 'ObsCore',
                download_access_key:
                  'obscore:Curation.PublisherDID.downloadable',
                default_sort_column:
                  'obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime',
                collection_select_id: 'DataID.Collection',
                footprint_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Support.Area',
                ra_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1',
                dec_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2',
                fov_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter'
              }
            },
            types: {
              CAOM2: {
                id: 'CAOM2',
                download_access_key: 'caom2:Plane.publisherID.downloadable',
                default_sort_column: 'caom2:Plane.time.bounds.lower',
                collection_select_id: 'Observation.collection',
                footprint_column_id: 'caom2:Plane.position.bounds',
                ra_column_id: 'caom2:Plane.position.bounds.cval1',
                dec_column_id: 'caom2:Plane.position.bounds.cval2',
                fov_column_id: 'caom2:Plane.position.bounds.area'
              },
              ObsCore: {
                id: 'ObsCore',
                download_access_key:
                  'obscore:Curation.PublisherDID.downloadable',
                default_sort_column:
                  'obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime',
                collection_select_id: 'DataID.Collection',
                footprint_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Support.Area',
                ra_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1',
                dec_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2',
                fov_column_id:
                  'obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter'
              }
            },
            SearchForm: SearchForm,
            FormConfiguration: FormConfiguration,
            events: {
              onValid: new jQuery.Event('AS:formValid'),
              onInvalid: new jQuery.Event('AS:formInvalid'),
              onSubmitComplete: new jQuery.Event('AS:submitComplete'),
              onReset: new jQuery.Event('AS:formReset'),
              onCancel: new jQuery.Event('AS:formCancel'),
              onSearchCriteriaChanged: new jQuery.Event(
                'AS:searchCriteriaChanged'
              ),
              onInit: new jQuery.Event('AS:formInitComplete'),
              onTargetNameResolved: new jQuery.Event('AS:onTargetNameResolved'),
              onTargetNameUnresolved: new jQuery.Event(
                'AS:onTargetNameUnresolved'
              )
            }
          }
        }
      }
    }
  })

  /**
   * Metadata for a form in the application.
   *
   * @param {ObsCoreFormConfiguration|CAOM2FormConfiguration} _config   Configuration for concrete instance.
   *   object.
   * @param {{}}  _options    Options for the form config.
   * @param {String}  [_options.tapSyncEndpoint="/search/tap/sync"]   TAP endpoint.
   * @param {String}  [_options.searchEndpoint="/search/find"]   Form submission endpoint.
   * @param {String}  [_options.validatorEndpoint="/search/validate"]   Form validator endpoint.
   * @param {String}  [_options.autocompleteEndpoint="/search/unitconversion"]   Autocomplete (units, Observation
   *                      constraint autocompletion) endpoint.
   * @param {String}  [_options.targetResolverEndpoint="/cadc-target-resolver/find"]   Resolver endpoint
   *
   * @constructor
   */
  function FormConfiguration(_config, _options) {
    var stringUtil = new org.opencadc.StringUtil()

    this.config = _config
    this.options = _options
    this.maqToggleEnabled = false

    /**
     * @type {Metadata|cadc.vot.Metadata}
     */
    this.tableMetadata = new cadc.vot.Metadata(
      null,
      null,
      null,
      null,
      null,
      null
    )

    this.columnManager = new ca.nrc.cadc.search.columns.ColumnManager()
    this.columnOptions = this.columnManager.getColumnOptions()

    this.getDownloadAccessKey = function() {
      return this.config.getConfig().download_access_key
    }

    /**
     * Obtain the full default metadata.
     *
     * @return {Metadata|cadc.vot.Metadata}
     */
    this.getTableMetadata = function() {
      return this.tableMetadata
    }

    /**
     * Get this form configuration's metadata.  It will self-reconfigure based on current search values.  To get the
     * default metadata (Full set), use #getTableMetadata().
     *
     * @return {Metadata|cadc.vot.Metadata}
     */
    this.getResultsTableMetadata = function() {
      // Current order of column IDs.
      var columnIDs = this.config.getAllColumnIDs()
      var currentMetadata = new cadc.vot.Metadata(
        null,
        null,
        null,
        null,
        null,
        null
      )

      for (var ci = 0, cl = columnIDs.length; ci < cl; ci++) {
        var colID = columnIDs[ci]
        var field = this.tableMetadata.getField(colID)

        if (field) {
          currentMetadata.addField(field)
        }
      }

      return currentMetadata
    }

    /**
     * Obtain the column options for this configuration.
     * @return {{}}   Hash of all columns and their options.
     */
    this.getColumnOptions = function() {
      return this.columnOptions
    }

    /**
     * Create a field for the given row.  This field will be set in the result
     * grid's metadata.
     *
     * This method exists here to set the Field's ID properly to the select
     * list's alias value.
     *
     * @param _row    The vot Row object.
     * @return {{}}   Plain object of items for metadata.
     * @private
     */
    this._rowData = function(_row) {
      var cells = _row.getCells()

      var rowData = {}

      for (var ci = 0; ci < cells.length; ci++) {
        var nextCell = cells[ci]
        var nextFieldName = nextCell.getField().getName()

        if (nextFieldName === 'table_name') {
          rowData.tableName = nextCell.getValue()
        } else if (nextFieldName === 'column_name') {
          rowData.fieldName = nextCell.getValue()
        } else if (nextFieldName === 'ucd') {
          rowData.ucd = nextCell.getValue()
        } else if (nextFieldName === 'utype') {
          rowData.utype = nextCell.getValue()
        } else if (nextFieldName === 'unit') {
          rowData.unit = nextCell.getValue()
        } else if (nextFieldName === 'description') {
          rowData.description = nextCell.getValue()
        } else if (nextFieldName === 'datatype') {
          rowData.datatype = nextCell.getValue()
        } else if (nextFieldName === 'size') {
          rowData.arraysize = nextCell.getValue()
        } else if (nextFieldName === 'xtype') {
          rowData.xtype = nextCell.getValue()
        }
      }

      return rowData
    }

    /**
     *
     * @param {cadc.vot.Row|Row} _row   Row object for a row in the VOTV grid.
     * @return {{}}   Plain hash of items.
     */
    this.addField = function(_row) {
      var rowData = this._rowData(_row)
      var uType = rowData.utype
      var ucd = rowData.ucd
      var unit = rowData.unit
      var datatype = rowData.datatype
      var arraySize = rowData.arraysize
      var description = rowData.description
      var xtype = rowData.xtype
      var order

      if (uType in this.getColumnOptions()) {
        var allColumnIDs = this.config.getAllColumnIDs()
        order = allColumnIDs.indexOf(uType)

        this._addFieldsForUType(
          uType,
          ucd,
          unit,
          datatype,
          arraySize,
          description,
          xtype,
          order
        )

        // Hack to include non-standard UTypes into the mix.
        if (uType === this.getFootprintColumnID()) {
          var raColumnID = this.getRAColumnID()
          order = allColumnIDs.indexOf(raColumnID)
          this._addFieldsForUType(
            raColumnID,
            ucd,
            unit,
            datatype,
            arraySize,
            description,
            xtype,
            order
          )

          var decColumnID = this.getDecColumnID()
          order = allColumnIDs.indexOf(decColumnID)
          this._addFieldsForUType(
            decColumnID,
            ucd,
            unit,
            datatype,
            arraySize,
            description,
            xtype,
            order
          )

          var areaFOVColumnID = this.getFOVColumnID()
          order = allColumnIDs.indexOf(areaFOVColumnID)
          this._addFieldsForUType(
            areaFOVColumnID,
            ucd,
            unit,
            datatype,
            arraySize,
            description,
            xtype,
            order
          )
        } else if (uType === 'caom2:Plane.publisherID') {
          order = allColumnIDs.indexOf('caom2:Plane.publisherID.downloadable')
          this._addFieldsForUType(
            'caom2:Plane.publisherID.downloadable',
            ucd,
            unit,
            datatype,
            arraySize,
            description,
            xtype,
            order
          )
        } else if (uType === 'obscore:Curation.PublisherDID') {
          order = allColumnIDs.indexOf(
            'obscore:Curation.PublisherDID.downloadable'
          )
          this._addFieldsForUType(
            'obscore:Curation.PublisherDID.downloadable',
            ucd,
            unit,
            datatype,
            arraySize,
            description,
            xtype,
            order
          )
        }
      }

      return rowData
    }

    /**
     * Iterate through all of the individual columns for the given UType, and
     * add them to the table metadata, if appropriate.
     *
     * @param {string} _uType
     * @param {string} _ucd
     * @param {string} _unit
     * @param {cadc.vot.Datatype|Datatype}  _datatype
     * @param {String|Number} _arraySize
     * @param {String} _description
     * @param {Number} _order
     * @private
     */
    this._addFieldsForUType = function(
      _uType,
      _ucd,
      _unit,
      _datatype,
      _arraySize,
      _description,
      _xtype,
      _order
    ) {
      var utypeFields = this.columnOptions[_uType]
      var tableMD = this.tableMetadata

      if (utypeFields != null && tableMD.hasFieldWithID(_uType) === false && !utypeFields.extended) {
        tableMD.insertField(
          _order,
          new cadc.vot.Field(
            utypeFields.label,
            _uType,
            _ucd,
            _uType,
            utypeFields.unit ? utypeFields.unit : _unit,
            _xtype,
            utypeFields.datatype ? utypeFields.datatype : _datatype,
            _arraySize,
            _description,
            utypeFields.label
          )
        )
      }
    }

    /**
     * Create the ADQL select clause.
     *
     * @param {boolean} _includeExtendedColumns   Flag to indicate whether extended (hidden) columns are to be included.
     * @returns {string}    ADQL Select clause, or empty string.  Never null.
     */
    this.getSelectListString = function(_includeExtendedColumns) {
      var selectColumnIDs = this.config.getAllColumnIDs()
      var thisColumnOptions = this.getColumnOptions()
      var lowercaseName = this.getName().toLowerCase()
      var selectListString = ''

      for (var i = 0; i < selectColumnIDs.length; i++) {
        var columnID = selectColumnIDs[i]
        if (columnID.indexOf(lowercaseName) === 0) {
          var field = thisColumnOptions[columnID]
          if (field) {
            if (!field.extended || _includeExtendedColumns) {
              var selector = this._getSelect(columnID, field)
              var selectorSplit = selector.split(/\.(.+)/)
              var selectorValue

              if (selectorSplit.length > 1) {
                var selectorValuePrefix = selectorSplit[0]
                var selectorValueSuffix = selectorSplit[1].replace(/\./g, '_')

                selectorValue = selectorValuePrefix + '.' + selectorValueSuffix
              } else {
                selectorValue = selector
              }

              selectListString += selectorValue + ' AS "' + field.label + '", '
            }
          } else {
            throw new Error('No such field ' + columnID)
          }
        }
      }

      return stringUtil.hasText(selectListString)
        ? selectListString.substring(0, selectListString.length - 2)
        : ''
    }

    /**
     * Get select value for the given uType.
     *
     * @param {String}  _uType    UType value.
     * @param {{}}  _field    Column option object.
     * @param {String} _field.tap_column_name    TAP Column name.
     * @private
     */
    this._getSelect = function(_uType, _field) {
      return _field.tap_column_name
        ? _field.tap_column_name
        : _uType.slice(_uType.indexOf(':') + 1)
    }

    /**
     * Obtain the column ID of the column containing footprint vales.
     * @return {string}
     */
    this.getFootprintColumnID = function() {
      return this.config.getConfig().footprint_column_id
    }

    /**
     * Obtain the column ID of the column containing RA vales.
     * @return {string}
     */
    this.getRAColumnID = function() {
      return this.config.getConfig().ra_column_id
    }

    /**
     * Obtain the column ID of the column containing Dec vales.
     * @return {string}
     */
    this.getDecColumnID = function() {
      return this.config.getConfig().dec_column_id
    }

    /**
     * Obtain the column ID of the column containing FOV (Field of View) vales.
     * @return {string}
     */
    this.getFOVColumnID = function() {
      return this.config.getConfig().fov_column_id
    }

    /**
     * Obtain the column ID of the column to sort by default.
     * @return {string}
     */
    this.getDefaultSortColumnID = function() {
      return this.config.getConfig().default_sort_column
    }

    /**
     * Obtain the name of this form.
     * @return {string}
     */
    this.getName = function() {
      return this.config.getConfig().id
    }

    /**
     * Obtain an array of default column IDs.
     * @return {[]}
     * @deprecated    Use FormConfiguration.getDefaultColumnIDs().
     */
    this.getDefaultColumnIDs = function() {
      return this.config.getDefaultColumnIDs()
    }

    /**
     * Obtain a hash of default unit types.
     * @return {{}}
     * @deprecated    Use FormConfiguration.getDefaultUnitTypes().
     */
    this.getDefaultUnitTypes = function() {
      return this.config.getDefaultUnitTypes()
    }

    /**
     * All column IDs.
     *
     * @return {[]}
     * @deprecated    use FormConfiguration.getAllColumnIDs().
     */
    this.getAllColumnIDs = function() {
      return this.config.getAllColumnIDs()
    }
  }

  /**
   * CAOM-2 configuration.
   *
   * @constructor
   */
  function CAOM2FormConfiguration() {
    this.config = ca.nrc.cadc.search.CAOM2.config
    this.columnBundleManager = new ca.nrc.cadc.search.ColumnBundleManager()

    /**
     * Return the selected collections from the data train.
     * @return {String}
     * @private
     */
    this._getSelectedCollections = function() {
      return $("select[id='" + this.config.collection_select_id + "']").val()
    }

    /**
     * Obtain the default column IDs for this form configuration.
     *
     * @return {[]} column ids, or empty array.
     */
    this.getDefaultColumnIDs = function() {
      var selectedCollections = this._getSelectedCollections()
      return this.columnBundleManager.getDefaultColumnIDs(selectedCollections)
    }

    /**
     * Obtain the full set of column IDs that will be in the select list, based
     * on some conditions at search time.
     *
     * @return {[]} Column IDs.
     */
    this.getAllColumnIDs = function() {
      var selectedCollections = this._getSelectedCollections()
      return this.columnBundleManager.getAllColumnIDs(selectedCollections)
    }

    /**
     * Obtain an object mapping of unit types.
     *
     * @return {Object}  Of Column ID to unit type mappings.
     */
    this.getDefaultUnitTypes = function() {
      var selectedCollections = this._getSelectedCollections()
      return this.columnBundleManager.getDefaultUnitTypes(selectedCollections)
    }

    /**
     * Obtain the concrete (base) config.
     *
     * @return {true.ca.nrc.cadc.search.CAOM2.config|{id, download_access_key, default_sort_column,
     *     collection_select_id, footprint_column_id, ra_column_id, dec_column_id, fov_column_id, uri_column_id}|*}
     */
    this.getConfig = function() {
      return this.config
    }
  }

  /**
   * ObsCore configuration.
   *
   * @constructor
   */
  function ObsCoreFormConfiguration() {
    this.config = ca.nrc.cadc.search.ObsCore.config
    this.columnBundleManager = new ca.nrc.cadc.search.ColumnBundleManager()

    /**
     * Obtain the default column IDs for this form configuration.
     *
     * @return {[]} column ids, or empty array.
     */
    this.getDefaultColumnIDs = function() {
      return this.columnBundleManager.getDefaultColumnIDs([this.config.id])
    }

    /**
     * Obtain the full set of column IDs that will be in the select list, based on some conditions at search time.
     *
     * @return {[]} Column IDs.
     */
    this.getAllColumnIDs = function() {
      return this.columnBundleManager.getAllColumnIDs([this.config.id])
    }

    /**
     * Obtain an object mapping of unit types.
     *
     * @return {{}}  Of Column ID to unit type mappings.
     */
    this.getDefaultUnitTypes = function() {
      return this.columnBundleManager.getDefaultUnitTypes([this.config.id])
    }

    /**
     * Obtain the concrete (base) config.
     *
     * @return {true.ca.nrc.cadc.search.ObsCore.config|{id, download_access_key, default_sort_column,
     *     collection_select_id, footprint_column_id, ra_column_id, dec_column_id, fov_column_id}|*}
     */
    this.getConfig = function() {
      return this.config
    }
  }

  /**
   * Should be an existing form in the document.
   *
   * @param {String}  _id               The unique identifier to find the Form Element.
   * @param {Boolean} _autoInitFlag     Whether to automatically initialize immediately or not.
   * @param {ca.nrc.cadc.search.FormConfiguration|FormConfiguration} _configuration    Specific configuration for this
   *     form.
   * @constructor
   */
  function SearchForm(_id, _autoInitFlag, _configuration) {
    var stringUtil = new org.opencadc.StringUtil()

    this.id = _id
    this.configuration = _configuration
    this.$form = $('form#' + _id)
    this.currentRequest = null

    /**
     * @type {number}
     */
    this.currentTimeoutID = null

    this.targetNameFieldID = null

    /**
     * The data train at the bottom of the form.
     *
     * @type {ca.nrc.cadc.search.datatrain.DataTrain|DataTrain}
     */
    this.dataTrain = new ca.nrc.cadc.search.datatrain.DataTrain(
      this.configuration.getName().toLowerCase(),
      this.configuration.columnManager,
      {
        tapSyncEndpoint: this.configuration.options.tapSyncEndpoint
      }
    )

    var VALIDATOR_TIMER_DELAY = 500

    var tooltipIconCSS = 'advancedsearch-tooltip'

    this.validator = new ca.nrc.cadc.search.Validator(
      this.configuration.options.validatorEndpoint,
      VALIDATOR_TIMER_DELAY
    )

    /**
     * Initialize this form.
     */
    this.init = function(useMaqDataTrain) {
      var $currForm = this.$form
      this.targetNameFieldID = $currForm
        .find("input[name$='@Shape1.value']")
        .prop('id')

      $currForm.find('.search_criteria_input').on(
        'change keyup',
        function(event) {
          this._searchCriteriaChanged($(event.target))
        }.bind(this)
      )

      $currForm
        .find("input:file[id$='_targetList']")
        .change(
          function(event) {
            if ($(event.target).val() !== '') {
              $('.targetList_clear').show()
              this.toggleDisabled(
                $(
                  '#' + this.id + " input[id='" + this.targetNameFieldID + "']"
                ),
                true
              )
            } else {
              this.toggleDisabled(
                $(
                  '#' + this.id + " input[id='" + this.targetNameFieldID + "']"
                ),
                false
              )
            }
          }.bind(this)
        )
        .change()

      // Those items with associated fields that will be disabled as an 'OR'
      // field.
      // jenkinsd 2015.01.05
      //
      $('*[data-assoc-field]')
        .on(
          'change keyup',
          function(event) {
            var $thisElement = $(event.target)
            var thisValue = $thisElement.val()

            this.toggleDisabled(
              $("[id='" + $thisElement.data('assoc-field') + "']"),
              stringUtil.hasText(thisValue)
            )
          }.bind(this)
        )
        .change()

      $('input.ui-autocomplete-input').each(
        function(key, input) {
          var id = $(input).attr('id')
          var config = this.configuration

          // Create arrays for response objects.
          var suggestionKeys = []

          $(input).autocomplete({
            // Define the minimum search string length
            // before the suggested values are shown.
            minLength: 2,

            // Define callback to format results
            source: function(req, callback) {
              // Reset each time as they type.
              suggestionKeys.length = 0

              var field =
                ca.nrc.cadc.search.AUTOCOMPLETE_TAP_REQUEST_DATA.fields[id]
              var defaultData =
                ca.nrc.cadc.search.AUTOCOMPLETE_TAP_REQUEST_DATA.payload
              var payload = $.extend({}, defaultData, {
                QUERY: stringUtil.format(defaultData.QUERY, [
                  field.tap_column,
                  req.term.toLowerCase()
                ])
              })
              // Does anything need to be done differently here for MAQ support?
              $.get(config.options.tapSyncEndpoint, payload).done(function(
                csvData
              ) {
                var csvArray = csvData.split('\n')
                if (csvArray.length > 1) {
                  suggestionKeys = csvArray.slice(1)
                  callback(suggestionKeys)
                }
              })
            },
            select: function(event, ui) {
              var val = ui.item.value
              var index = $.inArray(val, suggestionKeys)
              ui.item.value = suggestionKeys[index]
            }
          })
        }.bind(this)
      )

      // Click on the tooltip example, and update the representative field.
      $(document).on(
        'click',
        'a.advanced_search_tooltip_example',
        function(event) {
          var $thisLink = $(event.target)
          var uTypeID = $thisLink.prop('name')

          // Set the <select> option
          if ($thisLink.data('select-id')) {
            this.setSelectValue(
              uTypeID,
              $thisLink.data('select-id'),
              $thisLink.data('select-option')
            )
          } else {
            this.setInputValue(uTypeID, $thisLink.text())
          }

          return false
        }.bind(this)
      )

      // All of those checkboxes that will disable something when checked.
      $currForm.find('[data-disable-to]').change(
        function(event) {
          var $checkbox = $(event.target)
          var dataItem = $checkbox.data('disable-to')

          this.getForm()
            .find("[id='" + dataItem + "']")
            .prop('disabled', $checkbox.is(':checked'))
        }.bind(this)
      )

      $currForm.find('select.resolver-select').change(
        function(event) {
          var $resolverSelectName = $(event.target).prop('name')
          var $fieldID = $resolverSelectName.substring(
            0,
            $resolverSelectName.indexOf('@')
          )
          this._searchCriteriaChanged($("input[id='" + $fieldID + "']"))
        }.bind(this)
      )

      $currForm.find('.targetList_clear').click(
        function() {
          this._clearTargetList()
        }.bind(this)
      )

      $currForm.find('.useMaq').change(
        function(event) {
          // This sets up or removes the MAQ mode display items
          // in the form and results panel
          if (this.maqToggleEnabled === true) {
            // toggle results table header element
            if (
              event.currentTarget.checked === 'true' ||
              event.currentTarget.checked === true
            ) {
              this.setResultsMaqMode(true)
            } else {
              this.setResultsMaqMode(false)
            }

            this.disableMaqToggle()
            this.dataTrain.setMaqMode(event.currentTarget.checked)
          }
        }.bind(this)
      )

      // Prevent closing details when a value is present.
      $currForm.find("details[id$='_details'] summary").click(function(event) {
        var $detailsElement = $(this).parent('details')
        var $inputElements = $detailsElement.find('input.search_criteria_input')
        var isOpen = $detailsElement.prop('open')

        if (isOpen) {
          var canProceed = true

          $.each($inputElements, function(inputElementKey, inputElement) {
            var $inputElement = $(inputElement)

            if (
              $inputElement &&
              $inputElement.val() &&
              $inputElement.val() !== ''
            ) {
              // Disallow
              // closure when
              // value
              // present.
              event.preventDefault()
              canProceed = false

              // Break out of
              // the loop.
              return false
            } else {
              // Keep going.
              return true
            }
          })

          return canProceed
        } else {
          return true
        }
      })

      // Bind form input validation function.
      $currForm.find('input.ui-form-input-validate').each(
        function(key, value) {
          var $input = $(value)
          var thisSearchForm = this
          var callbackFunction = function(jsonError) {
            thisSearchForm._decorate($input, jsonError)
          }
          $input.bind('keydown', function() {
            thisSearchForm
              .getValidator()
              .inputKeyPressed($input, callbackFunction)
          })
        }.bind(this)
      )

      // Bind the form's submission.
      $currForm.submit(this._formSubmit.bind(this))

      this._getTargetNameResolutionStatusObject().popover({
        html: true,
        placement: 'auto left',
        template:
          '<div class="popover resolver-popover" role="tooltip"><h3 class="popover-title"></h3><div class="popover-content"></div></div>'
      })

      this.subscribe(
        ca.nrc.cadc.search.events.onTargetNameResolved,
        this.targetAccepted
      )

      this.subscribe(
        ca.nrc.cadc.search.events.onTargetNameUnresolved,
        function() {
          var $targetNameResolutionStatus = this._getTargetNameResolutionStatusObject()
          var resolverPopover = $targetNameResolutionStatus.data('bs.popover')

          resolverPopover.options.title = ''
          resolverPopover.options.content = ''
          resolverPopover.hide()
          resolverPopover.$tip.find('.popover-title').hide()

          $targetNameResolutionStatus.addClass('target_not_found')
          this._decorate($targetNameResolutionStatus, { status: 'NOT_FOUND' })

          $targetNameResolutionStatus.removeClass('busy')
        }
      )

      // Toggling the MAQ switch will trigger datatrain load
      // "" can mean it's not in the URL, or it's not enabled for this app
      if (useMaqDataTrain === '') {
        this.disableMaqToggle()
        this.dataTrain.init()
      } else {
        this.maqToggleEnabled = true
        this.setMaqToggle(useMaqDataTrain)
        this.setResultsMaqMode(useMaqDataTrain)
      }

      try {
        this._trigger(ca.nrc.cadc.search.events.onInit, {})
      } catch (err) {
        console.error('Error found.\n' + err)
      }
    }

    this.setMaqToggle = function(setOn) {
      if (setOn === 'true') {
        this.$form.find('.useMaq').bootstrapToggle('on')
      } else {
        this.$form.find('.useMaq').bootstrapToggle('off')
      }
      this.disableMaqToggle()
    }

    this.disableMaqToggle = function() {
      this.maqToggleEnabled = false
      var $useMaqEl = this.$form.find('.useMaq')
      $useMaqEl.bootstrapToggle('disable')
    }

    this.enableMaqToggle = function() {
      this.maqToggleEnabled = true
      var $useMaqEl = this.$form.find('.useMaq')
      $useMaqEl.bootstrapToggle('enable')
    }

    this.setResultsMaqMode = function(setOn) {
      if (setOn === true || setOn === 'true') {
        $('#resultsMaqEnabled').removeClass('cadc-display-none')
        this.$form.find('.useMaqValue').val(true)
      } else {
        $('#resultsMaqEnabled').addClass('cadc-display-none')
        this.$form.find('.useMaqValue').val(false)
      }
    }

    /**
     * User entered target is acceptable; meaning it passes name resolution and/or coordinate parsing.
     *
     * @param {jQuery.Event|Event} event      The Event object.
     * @param {{}} args   The argumements for this event.
     * @param {{}} args.data    Data to display
     * @param {boolean} [args.resolved=true]   Whether the resolver resolved a name.  This affects the tooltip being
     * shown.
     */
    this.targetAccepted = function(event, args) {
      var $targetNameResolutionStatus = this._getTargetNameResolutionStatusObject()
      $targetNameResolutionStatus.addClass('target_ok')
      var tooltipCreator = new ca.nrc.cadc.search.TooltipCreator()
      tooltipCreator.extractResolverValue(args.data.resolveValue)
      var $resolverTooltip = this.$form.find('.resolver-result-tooltip')
      var $tooltipContainer = tooltipCreator.getContent(
        $resolverTooltip.html(),
        '', // title blank
        'resolver-result-tooltip-text',
        $targetNameResolutionStatus
      )
      var $tooltipHeaderDiv = tooltipCreator.getHeader(
        'Resolver output',
        'resolver-result'
      )

      var resolverPopover = $targetNameResolutionStatus.data('bs.popover')

      resolverPopover.options.title = $tooltipHeaderDiv
      resolverPopover.options.content = $tooltipContainer[0].innerHTML
      resolverPopover.show()
      resolverPopover.$tip.find('.popover-title').show()
      $targetNameResolutionStatus.removeClass('busy')
    }

    /**
     * Handle loading a single tooltip.
     *
     * @param {{}} tipJSON    Object hash of tooltips
     * @param {String} tipJSON.tipHTML  HTML String of content.
     * @param {Number} [tipJSON.horizontalOffset]  Offset on the x axis.
     * @param {Number} [tipJSON.verticalOffset=0]  Offset on the y axis.
     * @param {ca.nrc.cadc.search.TooltipCreator|TooltipCreator}  tooltipCreator
     * @param {jQuery} $liItem
     * @param {String} inputID
     * @param {String} tooltipHeaderText
     */
    this.handleTooltipLoad = function(
      tipJSON,
      tooltipCreator,
      $liItem,
      inputID,
      tooltipHeaderText
    ) {
      if (tipJSON && tipJSON.tipHTML) {
        var tipMarkup = tipJSON.tipHTML

        var offsetY = tipJSON.verticalOffset ? tipJSON.verticalOffset : 0

        var $tooltipDiv = tooltipCreator.getContent(
          tipMarkup,
          tooltipHeaderText,
          null,
          null
        )

        var $tooltipHeaderDiv = tooltipCreator.getHeader(
          tooltipHeaderText,
          inputID
        )

        $liItem.popover({
          title: $tooltipHeaderDiv,
          content: $tooltipDiv[0].innerHTML,
          html: true,
          placement: $liItem[0].dataset.placement
        })

        // todo: can popovers be draggable?
        // $ttIconImg.on("click", function (e)
        // {
        //   e.preventDefault();
        //   $ttIconImg.tooltipster("show");
        //
        //   // Make them
        //   // draggable.
        //   $(".tooltipster-advanced-search").draggable(
        //       {
        //         handle: ".tooltip_header",
        //         snap: true,
        //         revert: false
        //       });
        //
        //   return false;
        // });
      }
    }

    /**
     * Given the JSON data, load the tooltips for those fields.
     * @param {{}}  jsonData    JSON data from external tooltips.
     */
    this.loadTooltips = function(jsonData) {
      var tooltipCreator = new ca.nrc.cadc.search.TooltipCreator()
      this.$form.find('[data-toggle="popover"]').each(
        function(key, element) {
          var $liItem = $(element)
          this.handleTooltipLoad(
            jsonData[element.dataset.utype],
            tooltipCreator,
            $liItem,
            element.dataset.utype,
            element.dataset.title
          )
        }.bind(this)
      )

      // Manage closing popovers, and maintaining that only one is
      // open at a time.
      $(document).on('click', function(e) {
        if ($(e.target).hasClass('glyphicon-remove-circle')) {
          $('[data-toggle="popover"],[data-original-title]').each(function() {
            ;(
              (
                $(this)
                  .popover('hide')
                  .data('bs.popover') || {}
              ).inState || {}
            ).click = false // fix for BS
            // 3.3.6
          })
        }

        if ($(e.target).hasClass('glyphicon-question-sign')) {
          $('[data-toggle="popover"]').each(function() {
            if (
              !$(this).is(e.target) &&
              $(this).has(e.target).length === 0 &&
              $('.popover').has(e.target).length === 0
            ) {
              ;(
                (
                  $(this)
                    .popover('hide')
                    .data('bs.popover') || {}
                ).inState || {}
              ).click = false // fix for BS 3.3.6
            }
          })

          // reposition popover so it doesn't cover input field for left-side display
          if ($('.popover').hasClass('left')) {
            $('.popover').css('left', '-480px')
          }
        }
      })
    }

    /**
     * Action to perform when the given criteria (form element) has changed.
     * @param {jQuery} $node
     */
    this._searchCriteriaChanged = function($node) {
      var id = $node.attr('id')
      var value = $node.val()
      var autocompleteURL =
        this.configuration.options.autocompleteEndpoint + '/' + id
      var hasValue = stringUtil.hasText(value)

      if (id === this.targetNameFieldID) {
        var resolver = this.$form
          .find('select.resolver-select option:selected')
          .val()

        // input text field disabled implies file has been chosen
        if (!$("input[id='" + id + "']").prop('disabled')) {
          this._indicateInputPresence(hasValue, id, value)
        }

        this.toggleDisabled($("input[id='" + id + "_targetList']"), hasValue)

        if (hasValue && resolver !== 'NONE') {
          this.clearTimeout()

          // Give the user a little more time to type stuff in.
          this.currentTimeoutID = window.setTimeout(
            function() {
              this._clearTargetNameResolutionStatusOnly()

              var $targetNameResolutionStatus = this._getTargetNameResolutionStatusObject()

              $targetNameResolutionStatus.addClass('busy')

              $.ajax({
                url:
                  this.configuration.options.targetResolverEndpoint + '/' + id,
                data: {
                  term: value,
                  resolver: resolver.toLowerCase()
                },
                method: 'GET',
                dataType: 'json'
              })
                .done(
                  /**
                   * @param {{}} data   Response JSON
                   * @param {String}  data.resolveStatus  Status text.
                   */
                  function(data) {
                    // Was input text cleared before the event arrived?
                    if ($.trim($("input[id='" + id + "']").val()).length > 0) {
                      var arg = {
                        data: data,
                        id: id
                      }

                      // no, check resolve status return value
                      if (data.resolveStatus === 'GOOD') {
                        this._trigger(
                          ca.nrc.cadc.search.events.onTargetNameResolved,
                          arg
                        )
                      } else {
                        this._trigger(
                          ca.nrc.cadc.search.events.onTargetNameUnresolved,
                          arg
                        )
                      }
                    }
                  }.bind(this)
                )
                .fail(
                  function(jqXHR) {
                    if (jqXHR.status === 425) {
                      this._trigger(
                        ca.nrc.cadc.search.events.onTargetNameUnresolved,
                        { id: id, target: value }
                      )
                    }
                  }.bind(this)
                )
            }.bind(this),
            700
          )
        } else {
          this._clearTargetNameResolutionStatus()
        }
      } else if ($node.hasClass('ui_unitconversion_input')) {
        // Pass request to server
        $.getJSON(
          autocompleteURL,
          { term: value },
          function(data) {
            var elementID

            if (id.indexOf('_targetList') > 0) {
              elementID = this.targetNameFieldID
            } else if (id.indexOf('_PRESET') > 0) {
              elementID = id.substr(0, id.indexOf('_PRESET'))
            } else {
              elementID = id
            }

            var $label = this.$form.find(
              "label[for='" + elementID + "'] .search_criteria_label_contents"
            )

            if ($label) {
              $label.empty()
              var searchCriteriaLabel =
                value !== '' && JSON.stringify(data).indexOf('NaN') < 0
                  ? data
                  : ''

              $label.text(searchCriteriaLabel)
            } else {
              console.warn('Unable to reset text for ' + elementID)
            }
          }.bind(this)
        ).error(function(jqXHR, status, message) {
          console.log('Error: ' + message)
        })
      } else if (id.match('^Observation.')) {
        this._indicateInputPresence(hasValue, id, value)
      } else if (id.match('Plane.position.bounds_targetList')) {
        // On chrome, 'value' contains the full path, e.g. C:\fakepath\test.txt.
        // Just use the file name instead.
        var mPos = value.lastIndexOf('\\')

        if (mPos === -1) {
          mPos = value.lastIndexOf('/')
        }

        var mFilename = value.substring(mPos + 1, value.length)
        this._indicateInputPresence(
          hasValue,
          'Plane.position.bounds',
          mFilename
        )
      }

      this._trigger(ca.nrc.cadc.search.events.onSearchCriteriaChanged, {
        formItem: $node
      })
    }

    /**
     *
     * @param {boolean} hasValue    Whether to set it.
     * @param {String} elementID    The id of the Element to set.
     * @param {String} elementValue The value to set.
     * @private
     */
    this._indicateInputPresence = function(hasValue, elementID, elementValue) {
      var $label = this.$form.find(
        "label[for='" + elementID + "'] .search_criteria_label_contents"
      )

      if ($label.length > 0) {
        $label.empty()

        if (hasValue) {
          var mText = elementValue
          $label.text(function() {
            var maxLength

            if (
              elementID === ca.nrc.cadc.search.CAOM2_TARGET_NAME_FIELD_ID ||
              elementID === ca.nrc.cadc.search.OBSCORE_TARGET_NAME_FIELD_ID
            ) {
              maxLength = ca.nrc.cadc.search.TARGET_FORM_LABEL_INPUT_LENGTH
            } else {
              maxLength = ca.nrc.cadc.search.FORM_LABEL_INPUT_LENGTH
            }

            if (elementValue.length > maxLength) {
              mText = elementValue.substring(0, maxLength) + '...'
            }

            return '(' + mText + ')'
          })
        }
      }
    }

    /**
     * Obtain the jQuery Form object.
     * @return {jQuery}
     */
    this.getForm = function() {
      return this.$form
    }

    /**
     * Obtain this Form's ID.
     * @return {String}
     */
    this.getID = function() {
      return this.id
    }

    /**
     * Obtain the Data Train instance.
     * @return {ca.nrc.cadc.search.datatrain.DataTrain|DataTrain}
     */
    this.getDataTrain = function() {
      return this.dataTrain
    }

    /**
     * This form's name.
     * @return {String}
     */
    this.getName = function() {
      return this.configuration.getName()
    }

    /**
     * This form's download access key column.
     * @return {String}
     */
    this.getDownloadAccessKey = function() {
      return this.configuration.getDownloadAccessKey()
    }

    this.getConfiguration = function() {
      return this.configuration
    }

    /**
     * Obtain this form's form configuration metadata.
     *
     * @returns {cadc.vot.Metadata|*}
     */
    this.getResultsTableMetadata = function() {
      return this.configuration.getResultsTableMetadata()
    }

    /**
     * @param {String}  _formID   Assess whether this form is active.
     * @return {boolean}
     */
    this.isActive = function(_formID) {
      return _formID === this.id
    }

    this.getValidator = function() {
      return this.validator
    }

    /**
     * Decorate the appropriate fields with error messages.
     *
     * @param {jQuery} $input      The jQuery input object.
     * @param {{}} [jsonError]   The JSON object of error messages.
     * @private
     */
    this._decorate = function($input, jsonError) {
      var $inputParent = $input.parent()

      if (!jsonError || $.isEmptyObject(jsonError)) {
        $inputParent.removeClass('has-error')
      } else {
        $inputParent.addClass('has-error')
      }
    }

    /**
     * Clear any errors.
     * @private
     */
    this.clearErrors = function() {
      this.$form.find('.has-error').each(
        function(key, value) {
          this._decorate($(value).find('input.search_criteria_input'), null)
        }.bind(this)
      )
    }

    /**
     * Perform a basic validation of this form.
     *
     * @returns {boolean}   True if valid, False otherwise.
     * @private
     */
    this._validate = function() {
      var valid = false
      var $thisForm = this.$form

      $thisForm.find('input:text').each(function() {
        if ($(this).val() !== '') {
          valid = true
        }
      })

      if (!valid) {
        $thisForm.find('input.form-extra').each(function() {
          if ($(this).val() !== '') {
            valid = true
          }
        })
      }

      if (!valid) {
        $thisForm.find('select.hierarchy_select :selected').each(function() {
          if (
            !$(this)
              .text()
              .match(/^All/)
          ) {
            valid = true
          }
        })
      }

      if (!valid) {
        $thisForm.find('input:hidden#target').each(function() {
          if ($(this).val() !== '') {
            valid = true
          }
        })
      }

      if (!valid) {
        $thisForm.find('input:hidden#collection').each(function() {
          if ($(this).val() !== '') {
            valid = true
          }
        })
      }

      if (!valid) {
        $thisForm.find('select.preset-date').each(function() {
          if ($(this).val() !== '') {
            valid = true
          }
        })
      }

      $thisForm.find('input:file').each(function() {
        if ($(this).val() !== '') {
          valid = true
        }
      })

      if (valid) {
        this._trigger(ca.nrc.cadc.search.events.onValid, {})
      } else {
        this._trigger(ca.nrc.cadc.search.events.onInvalid, {})
      }

      return valid
    }

    /**
     * Toggle a field's disabled attribute.
     *
     * @param {jQuery} node      The node to set.
     * @param {boolean} disable   The disabled flag to set.
     */
    this.toggleDisabled = function(node, disable) {
      node.prop('disabled', disable)

      if (disable === false) {
        node.removeAttr('disabled')
      }
    }

    /**
     * Disable searches from this form.
     */
    this.disable = function() {
      this.$form
        .prop('disabled', true)
        .find('input:submit')
        .prop('disabled', true)
    }

    /**
     * Enable searches from this form.
     */
    this.enable = function() {
      this.$form
        .prop('disabled', false)
        .find('input:submit')
        .prop('disabled', false)
    }

    /**
     * @return {jQuery}   Target list object.
     * @private
     */
    this._clearTargetList = function() {
      var $targetList = this.$form.find("input:file[id$='_targetList']")
      $targetList.val('')

      var targetListID = $targetList.attr('id')
      var uTypeValue = targetListID.substring(
        0,
        targetListID.indexOf('_targetList')
      )

      this.toggleDisabled($("input[id='" + uTypeValue + "']"), false)
      this.toggleDisabled($targetList, false)

      return $targetList
    }

    /**
     * @return {jQuery}
     * @private
     */
    this._getTargetNameResolutionStatusObject = function() {
      return this.$form.find('span.target_name_resolution_status')
    }

    /**
     * Return those checkboxes that disable other fields to unchecked.
     * @private
     */
    this._clearDisablingCheckboxes = function() {
      // Force issue a change().
      this.$form
        .find('[data-disable-to]:checked')
        .prop('checked', false)
        .change()
    }

    /**
     * Clear the target name resolution image.
     * @private
     */
    this._clearTargetNameResolutionStatusOnly = function() {
      var targetNameResolutionStatus = this._getTargetNameResolutionStatusObject()

      targetNameResolutionStatus.removeClass('busy')
      targetNameResolutionStatus.removeClass('target_ok')
      targetNameResolutionStatus.removeClass('target_not_found')

      // Clear errors
      this.clearErrors()
    }

    /**
     * Clear the target name resolution image.
     * @private
     */
    this._clearTargetNameResolutionStatus = function() {
      this._closeResolverPopover()
      this._clearTargetNameResolutionStatusOnly()
    }

    /**
     * Attempt at cross-browser HTTPRequest creation.
     * @returns {*} Request
     */
    this._createRequest = function() {
      var _thisRequest

      try {
        _thisRequest = new XMLHttpRequest()
      } catch (tryMicroSoft) {
        try {
          _thisRequest = new ActiveXObject('Msxml2.XMLHTTP')
        } catch (tryMicroSoftOther) {
          try {
            _thisRequest = new ActiveXObject('Microsoft.XMLHTTP')
          } catch (failed) {
            _thisRequest = null
          }
        }
      }

      this.currentRequest = _thisRequest
      return this.currentRequest
    }

    /**
     * Submit the form.
     */
    this.submit = function() {
      this.$form.submit()
    }

    /**
     * Cancel the current form submission.
     */
    this.cancel = function() {
      this.$form.stop(true, true)

      if (this.currentRequest) {
        this.currentRequest.abort()
      }

      this._trigger(ca.nrc.cadc.search.events.onCancel, {})
    }

    /**
     * Hide all of the tooltips.  This is used when the form is submitted.
     * @private
     */
    this._closeAllTooltips = function() {
      this.$form.find('.' + tooltipIconCSS).popover('hide')

      // This popover is associated with a stateful DOM element,
      // close it explicitly
      this._clearTargetNameResolutionStatus()
    }

    this._closeResolverPopover = function() {
      var resolverPopover = this.$form.find('.target_name_resolution_status')
      resolverPopover.popover('hide')
    }

    /**
     * Action to perform before form serialization begins.
     * @private
     */
    this._beforeSerialize = function() {
      $('#UPLOAD').remove()

      var inputFile = this.$form.find("input:file[name='targetList']")

      if (
        inputFile.length > 0 &&
        !inputFile.prop('disabled') &&
        inputFile.val() !== ''
      ) {
        var upload = $('<input>')
        upload.prop('type', 'hidden')
        upload.prop('name', 'UPLOAD')
        upload.prop('id', 'UPLOAD')
        upload.prop('value', 'search_upload,param:targetList')

        this.$form.append(upload)

        // Update the file input name with the value from the target list select.
        var resolverSelect = this.$form.find('select.resolver-select')

        // Renaming the field is a terrible idea, but cloning it doesn't work in some browsers.
        // jenkinsd 2017.07.10
        //
        inputFile.prop('name', 'targetList.' + resolverSelect.val())
      }

      // Save the form to sessionStorage.
      sessionStorage.setItem('form_data', this.$form.serialize())
      sessionStorage.setItem('isReload', false)
    }

    $(window).ready(function() {
      // if time between unload and ready is short (1 second or
      // less), page is reloaded
      if ($.now() - sessionStorage.getItem('unloadTime') < 1000) {
        sessionStorage.setItem('isReload', true)
      }
    })

    $(window).unload(function() {
      // when page is unloaded, save the selected tab
      sessionStorage.setItem('unloadTime', $.now())
      sessionStorage.setItem('isReload', false)
    })

    /**
     * Submit the form.
     *
     * @param {jQuery.Event|Event} event
     * @private
     */
    this._formSubmit = function(event) {
      event.preventDefault()

      this._closeAllTooltips()

      // Clear old session storage form_data.
      sessionStorage.removeItem('form_data')

      if (this._validate()) {
        var inputFile = this.$form.find('input:file')
        var isUpload = inputFile && inputFile.val() !== ''
        this.toggleDisabled(this.$form.find("input[name='targetList']"), false)

        var netStart = new Date().getTime()

        try {
          this.$form
            .find('input.' + this.configuration.getName() + '_selectlist')
            .val(this.configuration.getSelectListString(false))
        } catch (e) {
          this.cancel()
          alert('Error: ' + e.message)
        }

        /**
         * Cheating...  Oh well.  This is here to alleviate all of the otherwise necessary bind() calls.
         *
         * @type {SearchForm}
         */
        var myself = this

        this.$form.ajaxSubmit({
          url: this.configuration.options.searchEndpoint,
          target: '#file_upload_response',
          dataType: 'json',
          beforeSubmit: this._beforeSerialize.bind(this),
          success: function(json) {
            myself.$form.find('input:hidden#target').remove()
            myself.$form.find('input:hidden#collection').remove()

            myself.$form.currentRequest = null

            var args = {
              data: json,
              success: true,
              startDate: netStart,
              cadcForm: myself
            }

            myself._trigger(ca.nrc.cadc.search.events.onSubmitComplete, args)
          },
          error: function(request) {
            console.error('Error: ' + request.responseText)

            myself._trigger(ca.nrc.cadc.search.events.onSubmitComplete, {
              error_url: request.responseText,
              success: false,
              startDate: netStart,
              cadcForm: myself
            })
          },
          complete: function(request, textStatus) {
            // Remove non-form inputs to prevent confusion with further queries.
            myself.$form.find('input.form-extra').remove()

            if (textStatus === 'timeout') {
              alert(
                'The search took too long to return.\n' +
                  'Please refine your search or try again later.'
              )

              myself._trigger(ca.nrc.cadc.search.events.onSubmitComplete, {
                success: false,
                startDate: netStart,
                cadcForm: myself,
                error: {
                  status: request.status,
                  message: textStatus
                }
              })
            }
          },
          iframe: isUpload,
          xhr: this._createRequest
        })
      }
    }

    /**
     * Reset all of the form fields.
     */
    this.resetFields = function() {
      // function that resets all fields to default values
      this.$form.find('input:text').val('')
      $('#UPLOAD').remove()

      $('#include_proprietary').removeAttr('checked')

      this._clearTargetList()

      this.$form.find('select.search_criteria_input').each(
        function(key, value) {
          var $selectCriteria = $(value)
          $selectCriteria.val('')
          this.toggleDisabled($selectCriteria, false)
        }.bind(this)
      )

      this._closeAllTooltips()
      this._clearTargetNameResolutionStatus()
      this._clearDisablingCheckboxes()

      $('.hierarchy select').each(function() {
        $(this).val('')
      })

      $("input[name$='.DOWNLOADCUTOUT']").prop('checked', false)

      this.$form.find('input.search_criteria_input').each(
        function(key, value) {
          var $formItem = $(value)
          this.$form
            .find(
              "label[for='" +
                $formItem.attr('id') +
                "'] .search_criteria_label_contents"
            )
            .text('')
          this.toggleDisabled($formItem, false)
          this.closeDetailsItem($formItem.parents('details'))
        }.bind(this)
      )

      this.clearErrors()

      // This needs to be specific to the form
      var firstSelect = $('#' + this.id + ' .hierarchy select:eq(0)')

      // Convert to DOM Element object.
      var jsFirstSelect = document.getElementById(firstSelect.prop('id'))
      if (jsFirstSelect !== null) {
        this.dataTrain.updateLists(jsFirstSelect, true)
      }

      this._trigger(ca.nrc.cadc.search.events.onReset, {})
    }

    /**
     * Set a value in an option of a select (pull-down/multiselect) element.
     *
     * @param {String} _uTypeID   The uType (ID) of the <detail> surrounding element.
     * @param {String}  _selectID The ID of the <select> element.
     * @param {String} _optionValue    The value of the <option> to set.
     */
    this.setSelectValue = function(_uTypeID, _selectID, _optionValue) {
      var $detailsItem = this.$form.find(
        "details[id='" + _uTypeID + "_details']"
      )

      // Only proceed if a valid input ID was passed in.
      if ($detailsItem.length > 0) {
        var $select = $detailsItem.find("select[id='" + _selectID + "']")
        $select.val(_optionValue).change()

        if (_optionValue) {
          this.openDetailsItem($detailsItem)
        } else {
          this.closeDetailsItem($detailsItem)
        }
      }
    }

    /**
     * Populate a form <input> value that is encased in a <details> item.  If
     * the value is empty or null, then close the <details> item.
     *
     * @param {String} _inputID      The ID of the <input> field.
     * @param {String} _inputValue   The value to set.
     */
    this.setInputValue = function(_inputID, _inputValue) {
      var $inputItem = this.$form.find("input[id='" + _inputID + "']")
      var $formItem = this.$form.find("[id='" + _inputID + "']")

      // Classname used instead of id for this element because it exists on both caom2 and obscore
      // search forms
      if (_inputID === 'useMaq') {
        // Note that changing this box will kick off a data train update as well,
        // which may affect timing on Additional Constraints (hierarchy.js) fields that need to be updated
        $inputItem = this.$form.find("input[class='" + _inputID + "']")
      }

      if ($inputItem.length > 0) {
        if ($inputItem.is(':checkbox')) {
          // Default value is checked.
          var checkedFlag =
            _inputValue === true ||
            ca.nrc.cadc.search.CHECKBOX_CHECKED_REGEX.test(
              $.trim(_inputValue)
            ) ||
            !_inputValue
          $inputItem.prop('checked', checkedFlag)
        } else {
          $inputItem.val(_inputValue).change()
        }

        var $detailsItem = this.$form.find(
          "details[id='" + _inputID + "_details']"
        )

        if (_inputValue) {
          this.openDetailsItem($detailsItem)
        } else {
          this.closeDetailsItem($detailsItem)
        }
      } else if (
        $formItem.length === 0 &&
        ca.nrc.cadc.search.ignore_fields.indexOf(_inputID) < 0
      ) {
        // The "collection" word is grandfathered in, so ignore it...
        // If there is no input, then create one and assume the user knows what they're doing.
        var $newHidden = $('<input>')
          .attr('type', 'hidden')
          .attr('name', _inputID)
          .val(_inputValue)
          .addClass('form-extra')
        var $newHiddenFormName = $('<input>')
          .attr('type', 'hidden')
          .attr('name', 'Form.name')
          .val(_inputID + '@Text')
          .addClass('form-extra')

        this.$form.append($newHidden)
        this.$form.append($newHiddenFormName)
      }
    }

    this.toggleMaqMode = function() {
      // check if toggle is enabled for application first
      this.$form.find('.useMaq').click()
    }

    /**
     * Update the select element with the select value. Return true if the select is updated, false otherwise.
     *
     * @param {jQuery} _$select     The select element to up updated.
     * @param {[]} _selectValues    The selected value array.
     * @returns {Boolean}
     */
    this.setDataTrainValue = function(_$select, _selectValues) {
      var $options = _$select
        .find('option')
        .filter(function() {
          return _selectValues.indexOf($(this).val()) >= 0
        })
        .prop('selected', true)

      _$select.val(_selectValues)
      _$select.change()

      // WebRT #55941: adjust scrollbar to expose selected option
      var selected = _$select[0]
      var size = selected.size
      var index = selected.selectedIndex + 1
      var ratio = selected.scrollHeight / selected.length
      var tail = selected.length - selected.selectedIndex

      // scrollbar is needed?
      if (index > size) {
        // yes, selected option on last 'page'?
        if (tail > size) {
          // no, scroll to selected option
          // use (index - 1) so that it works on Firefox browsers as well
          // otherwise can just use (index)
          _$select.scrollTop(ratio * (index - 1))
        } else {
          // yes, scroll to option that displays the last 'page'
          _$select.scrollTop(ratio * (index - (size - tail)))
        }
      }

      return $options && $options.length > 0
    }

    /**
     * Open a one of the hidden items on the form.  This is used by the tooltip
     * examples to set a value.
     *
     * @param {jQuery} $detailsItem    The <details> item to open.
     */
    this.openDetailsItem = function($detailsItem) {
      $detailsItem.prop('open', true)
    }

    /**
     * Close a one of the hidden items on the form.  This is used by the tooltip
     * examples to set a value.
     *
     * @param {jQuery} $detailsItem    The <details> item to open.
     */
    this.closeDetailsItem = function($detailsItem) {
      $detailsItem.prop('open', false)
    }

    /**
     * Clear any existing timeouts.
     */
    this.clearTimeout = function() {
      if (this.currentTimeoutID) {
        window.clearTimeout(this.currentTimeoutID)
      }
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param {jQuery.Event}  _event       The Event to fire.
     * @param {{}}  _args        Arguments to the event.
     * @returns {*}       The event notification result.
     * @private
     */
    this._trigger = function(_event, _args) {
      var args = _args || {}
      args.cadcForm = this

      return $(this).trigger(_event, _args)
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param {jQuery.Event}  _event      Event object.
     * @param {function}  __handler   Handler function.
     */
    this.subscribe = function(_event, __handler) {
      $(this).on(_event.type, __handler)
    }

    if (_autoInitFlag === true) {
      this.init()
    }
  }
})(jQuery)
