(function ($)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "CAOM2_TARGET_NAME_FIELD_ID": "Plane.position.bounds",
            "OBSCORE_TARGET_NAME_FIELD_ID": "Char.SpatialAxis.Coverage.Support.Area",
            "FORM_LABEL_INPUT_LENGTH": 12,
            "TARGET_FORM_LABEL_INPUT_LENGTH": 24,
            "CHECKBOX_CHECKED_REGEX": /^true|on$/g,
            "AUTOCOMPLETE_ENDPOINT": "/AdvancedSearch/unitconversion/",
            "AUTOCOMPLETE_TAP_REQUEST_DATA": {
              "endpoint": "/tap/sync",
              "payload": {
                "LANG": "ADQL",
                "FORMAT": "CSV",
                "QUERY": "select {0} from caom2.distinct_{0} where lower({0}) like '%{1}%' order by {0}"
              },
              "fields": {
                "Observation.proposal.pi": {
                  "tap_column": "proposal_pi"
                },
                "Observation.proposal.title": {
                  "tap_column": "proposal_title"
                },
                "Observation.proposal.id": {
                  "tap_column": "proposal_id"
                }
              }
            },
            "CAOM2": {
              "FormConfiguration": CAOM2FormConfiguration,
              "config": {
                "id": "CAOM2",
                "download_access_key": "caom2:Plane.uri.downloadable",
                "default_sort_column": "caom2:Plane.time.bounds.lower",
                "collection_select_id": "Observation.collection",
                "footprint_column_id": "caom2:Plane.position.bounds",
                "ra_column_id": "caom2:Plane.position.bounds.cval1",
                "dec_column_id": "caom2:Plane.position.bounds.cval2",
                "fov_column_id": "caom2:Plane.position.bounds.area",
                "uri_column_id": "caom2:Plane.uri"
              }
            },
            "ObsCore": {
              "FormConfiguration": ObsCoreFormConfiguration,
              "config": {
                "id": "ObsCore",
                "download_access_key": "obscore:Curation.PublisherDID.downloadable",
                "default_sort_column": "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime",
                "collection_select_id": "DataID.Collection",
                "footprint_column_id": "obscore:Char.SpatialAxis.Coverage.Support.Area",
                "ra_column_id": "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1",
                "dec_column_id": "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2",
                "fov_column_id": "obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter"
              }
            },
            "types": {
              "CAOM2": {
                "id": "CAOM2",
                "download_access_key": "caom2:Plane.uri.downloadable",
                "default_sort_column": "caom2:Plane.time.bounds.lower",
                "collection_select_id": "Observation.collection",
                "footprint_column_id": "caom2:Plane.position.bounds",
                "ra_column_id": "caom2:Plane.position.bounds.cval1",
                "dec_column_id": "caom2:Plane.position.bounds.cval2",
                "fov_column_id": "caom2:Plane.position.bounds.area"
              },
              "ObsCore": {
                "id": "ObsCore",
                "download_access_key": "obscore:Curation.PublisherDID.downloadable",
                "default_sort_column": "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime",
                "collection_select_id": "DataID.Collection",
                "footprint_column_id": "obscore:Char.SpatialAxis.Coverage.Support.Area",
                "ra_column_id": "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1",
                "dec_column_id": "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2",
                "fov_column_id": "obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter"
              }
            },
            "SearchForm": SearchForm,
            "FormConfiguration": FormConfiguration,
            "events": {
              "onValid": new jQuery.Event("AS:formValid"),
              "onInvalid": new jQuery.Event("AS:formInvalid"),
              "onSubmitComplete": new jQuery.Event("AS:submitComplete"),
              "onReset": new jQuery.Event("AS:formReset"),
              "onCancel": new jQuery.Event("AS:formCancel"),
              "onSearchCriteriaChanged": new jQuery.Event("AS:searchCriteriaChanged"),
              "onInit": new jQuery.Event("AS:formInitComplete"),
              "onTargetNameResolved": new jQuery.Event("AS:onTargetNameResolved"),
              "onTargetNameUnresolved": new jQuery.Event("AS:onTargetNameUnresolved")
            }
          }
        }
      }
    }
  });


  /**
   * Metadata for a form in the application.
   *
   * @param _config {ObsCoreFormConfiguration|CAOM2FormConfiguration}  Configuration
   *   object.
   * @constructor
   */
  function FormConfiguration(_config)
  {
    var _selfFormConfiguration = this;

    this._config = _config;

    // Full metadata set.
    this.tableMetadata = new cadc.vot.Metadata(null, null, null, null, null,
      null);

    this.columnManager = new ca.nrc.cadc.search.columns.ColumnManager();
    this.columnOptions = getColumnManager().getOptions().columnOptions;


    function getDownloadAccessKey()
    {
      return _selfFormConfiguration._config.getConfig().download_access_key;
    }

    /**
     * Obtain the full default metadata.
     *
     * @return {cadc.vot.Metadata|*}
     */
    function getTableMetadata()
    {
      return _selfFormConfiguration.tableMetadata;
    }

    /**
     * Get this form configuration's metadata.  It will self-reconfigure based
     * on current search values.  To get the default metadata (Full set), use
     * getTableMetadata().
     *
     * @return {cadc.vot.Metadata|*}
     */
    function getResultsTableMetadata()
    {
      // Current order of column IDs.
      var columnIDs = _selfFormConfiguration._config.getAllColumnIDs();
      var currentMetadata =
        new cadc.vot.Metadata(null, null, null, null, null, null);

      for (var ci = 0; ci < columnIDs.length; ci++)
      {
        var colID = columnIDs[ci];
        var field = _selfFormConfiguration.tableMetadata.getField(colID);

        if (field)
        {
          currentMetadata.addField(field);
        }
      }

      return currentMetadata;
    }

    function getColumnOptions()
    {
      return _selfFormConfiguration.columnOptions;
    }

    function getColumnManager()
    {
      return _selfFormConfiguration.columnManager;
    }

    /**
     * Create a field for the given row.  This field will be set in the result
     * grid's metadata.
     *
     * This method exists here to set the Field's ID properly to the select
     * list's alias value.
     *
     * @param _row    The vot Row object.
     */
    function _rowData(_row)
    {
      var cells = _row.getCells();

      var rowData = {};

      for (var ci = 0; ci < cells.length; ci++)
      {
        var nextCell = cells[ci];
        var nextFieldName = nextCell.getField().getName();

        if (nextFieldName === 'table_name')
        {
          rowData.tableName = nextCell.getValue();
        }
        else if (nextFieldName === 'column_name')
        {
          rowData.fieldName = nextCell.getValue();
        }
        else if (nextFieldName === 'ucd')
        {
          rowData.ucd = nextCell.getValue();
        }
        else if (nextFieldName === 'utype')
        {
          rowData.utype = nextCell.getValue();
        }
        else if (nextFieldName === 'unit')
        {
          rowData.unit = nextCell.getValue();
        }
        else if (nextFieldName === 'description')
        {
          rowData.description = nextCell.getValue();
        }
        else if (nextFieldName === 'datatype')
        {
          rowData.datatype = nextCell.getValue();
        }
        else if (nextFieldName === 'size')
        {
          rowData.arraysize = nextCell.getValue();
        }
        else if (nextFieldName === 'xtype')
        {
          rowData.xtype = nextCell.getValue();
        }
      }

      return rowData;
    }

    function addField(_row)
    {
      var rowData = _rowData(_row);
      var utype = rowData.utype;
      var ucd = rowData.ucd;
      var unit = rowData.unit;
      var datatype = rowData.datatype;
      var arraysize = rowData.arraysize;
      var description = rowData.description;
      var order;

      if (utype in _selfFormConfiguration.getColumnOptions())
      {
        var allColumnIDs = _selfFormConfiguration._config.getAllColumnIDs();
        order = allColumnIDs.indexOf(utype);

        addFieldsForUType(utype, ucd, unit, datatype, arraysize, description,
                          order);

        // Hack to include non-standard UTypes into the mix.
        if (utype === getFootprintColumnID())
        {
          var raColumnID = getRAColumnID();
          order = allColumnIDs.indexOf(raColumnID);
          addFieldsForUType(raColumnID, ucd, unit, datatype, arraysize,
                            description, order);

          var decColumnID = getDecColumnID();
          order = allColumnIDs.indexOf(decColumnID);
          addFieldsForUType(decColumnID, ucd, unit, datatype, arraysize,
                            description, order);

          var areaFOVColumnID = getFOVColumnID();
          order = allColumnIDs.indexOf(areaFOVColumnID);
          addFieldsForUType(areaFOVColumnID, ucd, unit, datatype, arraysize,
                            description, order);
        }
        else if (utype === "caom2:Plane.uri")
        {
          order = allColumnIDs.indexOf("caom2:Plane.uri.downloadable");
          addFieldsForUType("caom2:Plane.uri.downloadable", ucd, unit,
                            datatype, arraysize, description, order);
        }
      }

      return rowData;
    }

    /**
     * Iterate through all of the individual columns for the given UType, and
     * add them to the table metadata, if appropriate.
     *
     * @param _utype
     * @param _ucd
     * @param _unit
     * @param _datatype
     * @param _arraysize
     * @param _description
     * @param _order
     */
    function addFieldsForUType(_utype, _ucd, _unit, _datatype, _arraysize,
                               _description, _order)
    {
      var utypeFields = getColumnOptions()[_utype];
      var tableMD = getTableMetadata();

      if ((tableMD.hasFieldWithID(_utype) === false) && !utypeFields.extended)
      {
        var strUtil = new cadc.web.util.StringUtil();

        tableMD.insertField(_order,
                            new cadc.vot.Field(
                              utypeFields.label,
                              _utype,
                              _ucd,
                              _utype,
                              utypeFields.unit ? utypeFields.unit :
                              _unit,
                              strUtil.contains(_datatype,
                                               "INTERVAL")
                                ? "INTERVAL" : null,// xtype not
                              // normally
                              // available
                              new cadc.vot.Datatype(utypeFields.datatype ?
                                                    utypeFields.datatype :
                                                    _datatype),
                              _arraysize,
                              _description,
                              utypeFields.label));
      }
    }

    /**
     *
     * @param _includeExtendedColumns
     * @returns {string}
     */
    function getSelectListString(_includeExtendedColumns)
    {
      var selectColumnIDs = _selfFormConfiguration._config.getAllColumnIDs();
      var thisColumnOptions = getColumnOptions();
      var lowercaseName = getName().toLowerCase();
      var selectListString = "";

      for (var i = 0; i < selectColumnIDs.length; i++)
      {
        var columnID = selectColumnIDs[i];
        if (columnID.indexOf(lowercaseName) === 0)
        {
          var field = thisColumnOptions[columnID];
          if (field)
          {
            if (!field.extended || _includeExtendedColumns)
            {
              var selector = getSelect(columnID, field);
              var selectorSplit = selector.split(/\.(.+)/);
              var selectorValue;

              if (selectorSplit.length > 1)
              {
                var selectorValuePrefix = selectorSplit[0];
                var selectorValueSuffix = selectorSplit[1].replace(/\./g, "_");

                selectorValue = selectorValuePrefix + "." + selectorValueSuffix;
              }
              else
              {
                selectorValue = selector;
              }

              selectListString +=
                selectorValue + " AS \"" + field.label + "\", ";
            }
          }
          else
          {
            throw new Error("No such field " + columnID);
          }
        }
      }

      return selectListString.substring(0, selectListString.length - 2);
    }

    function getSelect(_utype, _field)
    {
      if (_field.select)
      {
        return _field.select;
      }
      return _utype.slice(_utype.indexOf(":") + 1);
    }

    function getFootprintColumnID()
    {
      return _selfFormConfiguration._config.getConfig().footprint_column_id;
    }

    function getRAColumnID()
    {
      return _selfFormConfiguration._config.getConfig().ra_column_id;
    }

    function getDecColumnID()
    {
      return _selfFormConfiguration._config.getConfig().dec_column_id;
    }

    function getFOVColumnID()
    {
      return _selfFormConfiguration._config.getConfig().fov_column_id;
    }

    function getDefaultSortColumnID()
    {
      return _selfFormConfiguration._config.getConfig().default_sort_column;
    }

    function getName()
    {
      return _selfFormConfiguration._config.getConfig().id;
    }

    $.extend(this,
             {
               "getDownloadAccessKey": getDownloadAccessKey,
               "getSelectListString": getSelectListString,
               "getColumnOptions": getColumnOptions,
               "getTableMetadata": getTableMetadata,
               "getResultsTableMetadata": getResultsTableMetadata,
               "getFootprintColumnID": getFootprintColumnID,
               "getRAColumnID": getRAColumnID,
               "getDecColumnID": getDecColumnID,
               "getFOVColumnID": getFOVColumnID,
               "getDefaultSortColumnID": getDefaultSortColumnID,
               "getDefaultColumnIDs": _selfFormConfiguration._config.getDefaultColumnIDs,
               "getDefaultUnitTypes": _selfFormConfiguration._config.getDefaultUnitTypes,
               "getAllColumnIDs": _selfFormConfiguration._config.getAllColumnIDs,
               "addField": addField,
               "getName": getName
             });
  }

  /**
   * CAOM-2 configuration.
   * @constructor
   */
  function CAOM2FormConfiguration()
  {
    var _self = this;
    this._config = ca.nrc.cadc.search.CAOM2.config;
    this.columnBundleManager = new ca.nrc.cadc.search.ColumnBundleManager();

    function getSelectedCollections()
    {
      return $("select[id='" + _self._config.collection_select_id + "']").val();
    }

    function getDefaultColumnIDs()
    {
      var selectedCollections = getSelectedCollections();
      return _self.columnBundleManager.getDefaultColumnIDs(selectedCollections);
    }

    /**
     * Obtain the full set of column IDs that will be in the select list, based
     * on some conditions at search time.
     *
     * @return {Array} Column IDs.
     */
    function getAllColumnIDs()
    {
      var selectedCollections = getSelectedCollections();
      return _self.columnBundleManager.getAllColumnIDs(selectedCollections);
    }

    /**
     * Obtain an object mapping of unit types.
     *
     * @return {Object}  Of Column ID to unit type mappings.
     */
    function getDefaultUnitTypes()
    {
      var selectedCollections = getSelectedCollections();
      return _self.columnBundleManager.getDefaultUnitTypes(selectedCollections);
    }

    function getConfig()
    {
      return _self._config;
    }

    $.extend(this,
             {
               "getDefaultColumnIDs": getDefaultColumnIDs,
               "getAllColumnIDs": getAllColumnIDs,
               "getDefaultUnitTypes": getDefaultUnitTypes,
               "getConfig": getConfig
             });
  }

  /**
   * ObsCore configuration.
   * @constructor
   */
  function ObsCoreFormConfiguration()
  {
    var _self = this;
    this._config = ca.nrc.cadc.search.ObsCore.config;
    this.columnBundleManager = new ca.nrc.cadc.search.ColumnBundleManager();

    function getDefaultColumnIDs()
    {
      return _self.columnBundleManager.getDefaultColumnIDs([_self._config.id]);
    }

    /**
     * Obtain the full set of column IDs that will be in the select list, based
     * on some conditions at search time.
     *
     * @return {Array} Column IDs.
     */
    function getAllColumnIDs()
    {
      return _self.columnBundleManager.getAllColumnIDs([_self._config.id]);
    }

    /**
     * Obtain an object mapping of unit types.
     *
     * @return {Object}  Of Column ID to unit type mappings.
     */
    function getDefaultUnitTypes()
    {
      return _self.columnBundleManager.getDefaultUnitTypes([_self._config.id]);
    }

    function getConfig()
    {
      return _self._config;
    }


    $.extend(this,
             {
               "getDefaultColumnIDs": getDefaultColumnIDs,
               "getAllColumnIDs": getAllColumnIDs,
               "getDefaultUnitTypes": getDefaultUnitTypes,
               "getConfig": getConfig
             });
  }

  /**
   * Should be an existing form in the document.
   *
   * @param _id               The unique identifier to find the Form Element.
   * @param _autoInitFlag     Whether to automatically initialize immediately
   *                          or not.
   * @param _configuration    Specific configuration for this form.
   * @constructor
   */
  function SearchForm(_id, _autoInitFlag, _configuration)
  {
    var _selfForm = this;
    this.id = _id;
    this.configuration = _configuration;
    this.$form = $("form#" + _id);
    this.currentRequest = null;
    this.currentTimeoutID = null;
    this.targetNameFieldID = null;
    this.dataTrain = new ca.nrc.cadc.search.datatrain.DataTrain(
      getConfiguration().getName().toLowerCase(), false);

    var VALIDATOR_ENDPOINT = "validate";
    var VALIDATOR_TIMER_DELAY = 500;

    var tooltipIconCSS = "advancedsearch-tooltip";
    var initialTooltipIconCSS = "wb-icon-question";
    var hoverTooltipIconCSS = "wb-icon-question-alt";

    this.validator = new ca.nrc.cadc.search.Validator(VALIDATOR_ENDPOINT,
      VALIDATOR_TIMER_DELAY);

    // Tooltip objects to keep track of.
    this.targetTooltipsters = [];

    /**
     * Initialize this form.
     */
    function init()
    {
      _selfForm.targetNameFieldID =
        getForm().find("input[name$='@Shape1.value']").prop("id");

      getForm().find(".search_criteria_input").on("change keyup",
                                                  function ()
                                                  {
                                                    searchCriteriaChanged($(this));
                                                  });

      $("input:file[id$='_targetList']").change(
        function ()
        {
          if ($(this).val() !== '')
          {
            $(".targetList_clear").show();
            toggleDisabled($("input[id='" + _selfForm.targetNameFieldID +
                             "']"), true);
          }
          else
          {
            toggleDisabled($("input[id='" + _selfForm.targetNameFieldID +
                             "']"), false);
          }
        }).change();

      // Those items with associated fields that will be disabled as an 'OR'
      // field.
      // jenkinsd 2015.01.05
      //
      $("*[data-assoc-field]").on("change keyup", function ()
      {
        var $thisElement = $(this);
        var thisValue = $thisElement.val();

        toggleDisabled($("[id='" + $thisElement.data("assoc-field") + "']"),
                       ((thisValue !== null) && ($.trim(thisValue) !== '')));
      }).change();

      $("input.ui-autocomplete-input").each(
        function ()
        {
          var id = $(this).prop("id");

          // Create arrays for response objects.
          var suggestionKeys = [];

          $(this).autocomplete(
            {
              // Define the minimum search string length
              // before the suggested values are shown.
              minLength: 2,

              // Define callback to format results
              source: function (req, callback)
              {
                // Reset each time as they type.
                suggestionKeys.length = 0;

                var field =
                  ca.nrc.cadc.search.AUTOCOMPLETE_TAP_REQUEST_DATA.fields[id];
                var defaultData =
                  ca.nrc.cadc.search.AUTOCOMPLETE_TAP_REQUEST_DATA.payload;
                var payload =
                  $.extend({}, defaultData,
                           {
                             "QUERY": new cadc.web.util.StringUtil(
                               defaultData.QUERY).format(field.tap_column,
                                                         req.term.toLowerCase())
                           });

                $.get(ca.nrc.cadc.search.AUTOCOMPLETE_TAP_REQUEST_DATA.endpoint,
                      payload).done(function (csvData)
                                    {
                                      var csvArray = csvData.split('\n');
                                      if (csvArray.length > 1)
                                      {
                                        suggestionKeys = csvArray.slice(1);
                                        callback(suggestionKeys);
                                      }
                                    });
              },
              select: function (event, ui)
              {
                var val = ui.item.value;
                var index = $.inArray(val, suggestionKeys);
                ui.item.value = suggestionKeys[index];
              }
            });
        });

      // Click on the tooltip example, and update the representative field.
      $(document).on("click", "a.advanced_search_tooltip_example", function ()
      {
        var $thisLink = $(this);
        var uTypeID = $thisLink.prop("name");

        // Set the <select> option
        if ($thisLink.data("select-id"))
        {
          setSelectValue(uTypeID, $thisLink.data("select-id"),
                         $thisLink.data("select-option"));
        }
        else
        {
          setInputValue(uTypeID, $thisLink.text());
        }

        return false;
      });

      // All of those checkboxes that will disable something when checked.
      getForm().find("[data-disable-to]").change(function ()
                                                 {
                                                   var $checkbox = $(this);
                                                   var dataItem =
                                                     $checkbox.data("disable-to");

                                                   getForm().find("[id='"
                                                                  + dataItem
                                                                  + "']").prop(
                                                                    "disabled",
                                                                    $checkbox.is(":checked"));
                                                 });

      $("select.resolver_select").change(function ()
                                         {
                                           var $resolverSelectName = $(this).prop("name");
                                           var $fieldID =
                                             $resolverSelectName.substring(0, $resolverSelectName.indexOf("@"));
                                           searchCriteriaChanged($("input[id='"
                                                                   + $fieldID
                                                                   + "']"));
                                         });

      getForm().find(".targetList_clear").click(function ()
                                                {
                                                  clearTargetList();
                                                });

      // Prevent closing details when a value is present.
      $("details[id$='_details'] summary").click(function (event)
                                                 {
                                                   var $detailsElement =
                                                     $(this).parent("details");
                                                   var $inputElements =
                                                     $detailsElement.find(
                                                       "input.search_criteria_input");
                                                   var isOpen =
                                                     $detailsElement.prop("open");

                                                   if (isOpen)
                                                   {
                                                     var canProceed = true;

                                                     $.each($inputElements,
                                                            function (inputElementKey, inputElement)
                                                            {
                                                              var $inputElement =
                                                                $(inputElement);

                                                              if ($inputElement
                                                                  &&
                                                                  $inputElement.val()
                                                                  &&
                                                                  ($inputElement.val() !=
                                                                   ""))
                                                              {
                                                                // Disallow
                                                                // closure when
                                                                // value
                                                                // present.
                                                                event.preventDefault();
                                                                canProceed =
                                                                  false;

                                                                // Break out of
                                                                // the loop.
                                                                return false;
                                                              }
                                                              else
                                                              {
                                                                // Keep going.
                                                                return true;
                                                              }
                                                            });

                                                     return canProceed;
                                                   }
                                                   else
                                                   {
                                                     return true;
                                                   }
                                                 });

      // Bind form input validation function.
      getForm().find('input.ui-form-input-validate').each(function ()
                                                          {
                                                            var $input = $(this);
                                                            var callbackFunction = function (jsonError)
                                                            {
                                                              decorate($input, jsonError);
                                                            };
                                                            $input.bind('keydown', function ()
                                                            {
                                                              getValidator().inputKeyPressed($input, callbackFunction);
                                                            });
                                                          });

      // Bind the form's submission.
      getForm().submit(formSubmit);

      getTargetNameResolutionStatusObject().tooltipster(
        {
          arrow: false,
          theme: "tooltipster-advanced-search-resolver",
          position: "left",
          maxWidth: 170,
          offsetX: 230,
          trigger: "click",
          interactive: true,
          repositionOnResize: false,
          repositionOnScroll: false,
          onlyOne: false
        });

      subscribe(ca.nrc.cadc.search.events.onTargetNameResolved,
                function (event, args)
                {
                  var $targetNameResolutionStatus =
                    getTargetNameResolutionStatusObject();
                  var data = args.data;
                  $targetNameResolutionStatus.addClass("target_ok");
                  var tooltipCreator =
                    new ca.nrc.cadc.search.TooltipCreator();
                  tooltipCreator.extractResolverValue(data.resolveValue);
                  var $resolverTooltip =
                    getForm().find(".resolver-result-tooltip");
                  var $tooltipContainer =
                    tooltipCreator.getContent($resolverTooltip.html(),
                                              "", // title blank
                                              "resolver-result-tooltip-text",
                                              $targetNameResolutionStatus);

                  $targetNameResolutionStatus.tooltipster("content",
                                                          $tooltipContainer);

                  $targetNameResolutionStatus.tooltipster("show");

                  // Make them draggable.
                  $(".tooltipster-advanced-search-resolver").draggable(
                    {
                      handle: ".tooltip_header",
                      snap: true,
                      revert: false
                    });
                });

      subscribe(ca.nrc.cadc.search.events.onTargetNameUnresolved,
                function (event, args)
                {
                  var $targetNameResolutionStatus =
                    getTargetNameResolutionStatusObject();
                  var data = args.data;

                  $targetNameResolutionStatus.addClass("target_not_found");
                  decorate($targetNameResolutionStatus,
                           $.parseJSON('{"status":"' + data.resolveStatus
                                       + '"}'));
                });

      getDataTrain().init();

      try
      {
        trigger(ca.nrc.cadc.search.events.onInit, {});
      }
      catch (err)
      {
        console.error("Error found.\n" + err);
      }
    }

    /**
     * Given the JSON data, load the tooltips for those fields.
     * @param jsonData
     */
    function loadTooltips(jsonData)
    {
      var tooltipCreator = new ca.nrc.cadc.search.TooltipCreator();
      getForm().find("ul.search-constraints li").each(function (liKey, liElement)
                                                      {
                                                        var $liItem = $(liElement);
                                                        var $tooltipHeader =
                                                          $liItem.find("summary.search_criteria_label_container");
                                                        var tooltipHeaderText = $tooltipHeader.text();
                                                        var $searchInputItem =
                                                          $liItem.find(".search_criteria_input:first");
                                                        var $inputID =
                                                          $searchInputItem.prop("id");
                                                        var tipJSON = jsonData[$inputID];

                                                        if (tipJSON &&
                                                            tipJSON.tipHTML)
                                                        {
                                                          var tipMarkup = tipJSON.tipHTML;
                                                          var tipsterPlacement;
                                                          var offsetX, offsetY;

                                                          if ($liItem.hasClass("label_tooltip_right"))
                                                          {
                                                            tipsterPlacement =
                                                              "right";
                                                            offsetX = -12;
                                                          }
                                                          else
                                                          {
                                                            tipsterPlacement =
                                                              "left";
                                                            offsetX = 240;
                                                          }

                                                          if (($inputID ===
                                                               "Observation.observationID")
                                                              || ($inputID ===
                                                                  "DataID.observationID"))
                                                          {
                                                            offsetY = -350;
                                                          }
                                                          else
                                                          {
                                                            offsetY = 0;
                                                          }

                                                          var $ttIconImg = $('<span class="' +
                                                                             tooltipIconCSS +
                                                                             ' ' +
                                                                             initialTooltipIconCSS +
                                                                             ' float-right" />');

                                                          $liItem.find(".search_criteria_label_contents").before($ttIconImg);

                                                          var $tooltipDiv = tooltipCreator.getContent(tipMarkup,
                                                                                                      tooltipHeaderText,
                                                                                                      null,
                                                                                                      $ttIconImg);

                                                          var tipster = $ttIconImg.tooltipster({
                                                                                                 interactive: true,
                                                                                                 animation: "fade",
                                                                                                 theme: "tooltipster-advanced-search",
                                                                                                 content: $tooltipDiv,
                                                                                                 maxWidth: 400,
                                                                                                 arrow: false,
                                                                                                 repositionOnResize: false,
                                                                                                 repositionOnScroll: false,
                                                                                                 position: tipsterPlacement,
                                                                                                 offsetX: offsetX,
                                                                                                 offsetY: offsetY,
                                                                                                 onlyOne: true,
                                                                                                 trigger: "custom"
                                                                                               });

                                                          if ($inputID ===
                                                              "Plane.position.bounds")
                                                          {
                                                            _selfForm.targetTooltipsters =
                                                              _selfForm.targetTooltipsters.concat(tipster);
                                                          }

                                                          $ttIconImg.hover(function (e)
                                                                           {
                                                                             var $thisSpan = $(this);

                                                                             $thisSpan.removeClass(initialTooltipIconCSS);
                                                                             $thisSpan.addClass(hoverTooltipIconCSS);

                                                                             return false;
                                                                           },
                                                                           function (e)
                                                                           {
                                                                             var $thisSpan = $(this);

                                                                             $thisSpan.removeClass(hoverTooltipIconCSS);
                                                                             $thisSpan.addClass(initialTooltipIconCSS);

                                                                             return false;
                                                                           });

                                                          $ttIconImg.on("click", function (e)
                                                          {
                                                            e.preventDefault();
                                                            $ttIconImg.tooltipster("show");

                                                            // Make them
                                                            // draggable.
                                                            $(".tooltipster-advanced-search").draggable(
                                                              {
                                                                handle: ".tooltip_header",
                                                                snap: true,
                                                                revert: false
                                                              });

                                                            return false;
                                                          });
                                                        }
                                                      });
    }

    /**
     * Action to perform when the given criteria (form element) has changed.
     * @param $node
     */
    function searchCriteriaChanged($node)
    {
      var id = $node.attr("id");
      var value = $node.val();
      var autocompleteURL = ca.nrc.cadc.search.AUTOCOMPLETE_ENDPOINT + id;
      var hasValue = ((value !== "") && (value !== null));

      if (id == _selfForm.targetNameFieldID)
      {
        // input text field disabled implies file has been chosen
        if (!$("input[id='" + id + "']").prop("disabled"))
        {
          indicateInputPresence(hasValue, id, value);
        }

        toggleDisabled($("input[id='" + id + "_targetList']"), hasValue);

        var resolver = getForm().find(
          "select.resolver_select option:selected").val();

        if (hasValue && (resolver !== "NONE"))
        {
          clearTimeout();

          // Give the user a little more time to type stuff in.
          _selfForm.currentTimeoutID = window.setTimeout(
            function ()
            {
              clearTargetNameResolutionStatusOnly();

              var $targetNameResolutionStatus =
                getTargetNameResolutionStatusObject();

              $targetNameResolutionStatus.addClass("busy");

              $.getJSON(autocompleteURL, {term: value, resolver: resolver},
                        function (data)
                        {
                          $targetNameResolutionStatus.removeClass("busy");
                          clearTargetNameResolutionTooltip();

                          // Was input text cleared before the event arrived?
                          if ($.trim($("input[id='" + id + "']").val()).length >
                              0)
                          {
                            var arg =
                            {
                              "data": data,
                              "id": id
                            };

                            // no, check resolve status
                            if (data.resolveStatus === "GOOD")
                            {
                              trigger(ca.nrc.cadc.search.events.onTargetNameResolved,
                                      arg);
                            }
                            else
                            {
                              trigger(ca.nrc.cadc.search.events.onTargetNameUnresolved,
                                      arg);
                            }
                          }
                        });
            }, 700);
        }
        else
        {
          clearTargetNameResolutionStatus();
        }
      }
      else if ($node.hasClass('ui_unitconversion_input'))
      {
        // Pass request to server
        $.getJSON(autocompleteURL, {term: value}, function (data)
        {
          var elementID;

          if (id.indexOf("_targetList") > 0)
          {
            elementID = _selfForm.targetNameFieldID;
          }
          else if (id.indexOf("_PRESET") > 0)
          {
            elementID = id.substr(0, id.indexOf("_PRESET"));
          }
          else
          {
            elementID = id;
          }

          var $label = getForm().find("label[for='" + elementID +
                                      "']").prev("summary").children("span.search_criteria_label_contents");
          if ($label)
          {
            $label.empty();
            var searchCriteriaLabel;

            if ((value !== '') && (JSON.stringify(data).indexOf("NaN") < 0))
            {
              searchCriteriaLabel = data;
            }
            else
            {
              searchCriteriaLabel = "";
            }

            $label.text(searchCriteriaLabel);
          }
          else
          {
            console.warn("Unable to reset text for " + elementID);
          }
        }).error(function (jqXHR, status, message)
                 {
                   console.log("Error: " + message);
                 });
      }
      else if (id.match('^Observation.'))
      {
        indicateInputPresence(hasValue, id, value);
      }
      else if (id.match('Plane.position.bounds_targetList'))
      {
        // On chrome, 'value' contains the full path, e.g. C:\fakepath\test.txt.
        // Just use the file name instead.
        var mPos = value.lastIndexOf('\\');

        if (mPos == -1)
        {
          mPos = value.lastIndexOf('/');
        }

        var mFilename = value.substring(mPos + 1, value.length);
        indicateInputPresence(hasValue, "Plane.position.bounds", mFilename);
      }

      trigger(ca.nrc.cadc.search.events.onSearchCriteriaChanged,
              {
                formItem: $node
              });
    }

    function indicateInputPresence(hasValue, elementID, elementValue)
    {
      var $label = getForm().find("label[for='" + elementID +
                                  "']").prev("summary").children("span.search_criteria_label_contents");
      if ($label)
      {
        $label.empty();

        if (hasValue)
        {
          var mText = elementValue;
          $label.text(function ()
                      {
                        var maxLength;

                        if ((elementID ===
                             ca.nrc.cadc.search.CAOM2_TARGET_NAME_FIELD_ID)
                            || (elementID ===
                                ca.nrc.cadc.search.OBSCORE_TARGET_NAME_FIELD_ID))
                        {
                          maxLength =
                            ca.nrc.cadc.search.TARGET_FORM_LABEL_INPUT_LENGTH;
                        }
                        else
                        {
                          maxLength =
                            ca.nrc.cadc.search.FORM_LABEL_INPUT_LENGTH;
                        }

                        if (elementValue.length > maxLength)
                        {
                          mText = elementValue.substring(0, maxLength) + "...";
                        }
                        return "(" + mText + ")";
                      });
        }
      }
    }

    function getForm()
    {
      return _selfForm.$form;
    }

    function getID()
    {
      return _selfForm.id;
    }

    function getDataTrain()
    {
      return _selfForm.dataTrain;
    }

    function getName()
    {
      return getConfiguration().getName();
    }

    function getDownloadAccessKey()
    {
      return getConfiguration().getDownloadAccessKey();
    }

    function getConfiguration()
    {
      return _selfForm.configuration;
    }

    /**
     * Obtain this form's form configuration metadata.
     *
     * @returns {cadc.vot.Metadata|*}
     */
    function getResultsTableMetadata()
    {
      return getConfiguration().getResultsTableMetadata();
    }

    function isActive(_formID)
    {
      return _formID == getID();
    }

    function getValidator()
    {
      return _selfForm.validator;
    }

    /**
     * Decorate the appropriate fields with error messages.
     *
     * @param $input      The jQuery input object.
     * @param jsonError   The JSON object of error messages.
     */
    function decorate($input, jsonError)
    {
      var $inputParent = $input.parent();

      if (!jsonError || $.isEmptyObject(jsonError))
      {
        $inputParent.removeClass("form-attention");
      }
      else
      {
        $inputParent.addClass("form-attention");
      }
    }

    /**
     * Clear any errors.
     */
    function clearErrors()
    {
      getForm().find(".form-attention").each(function ()
                                             {
                                               decorate($(this).find("input.search_criteria_input"), null);
                                             });
    }

    /**
     * Perform a basic validation of this form.
     *
     * @returns {boolean}   True if valid, False otherwise.
     */
    function validate()
    {
      var valid = false;
      var $thisForm = getForm();

      $thisForm.find("input:text").each(function ()
                                        {
                                          if ($(this).val() != '')
                                          {
                                            valid = true;
                                          }
                                        });

      if (!valid)
      {
        $thisForm.find("select.hierarchy_select :selected").each(function ()
                                                                 {
                                                                   if (!$(this).text().match(/^All/))
                                                                   {
                                                                     valid =
                                                                       true;
                                                                   }
                                                                 });
      }

      if (!valid)
      {
        $thisForm.find("input:hidden#target").each(function ()
                                                   {
                                                     if ($(this).val() != '')
                                                     {
                                                       valid = true;
                                                     }
                                                   });
      }

      if (!valid)
      {
        $thisForm.find("input:hidden#collection").each(function ()
                                                       {
                                                         if ($(this).val() !=
                                                             '')
                                                         {
                                                           valid = true;
                                                         }
                                                       });
      }

      if (!valid)
      {
        $thisForm.find("select.preset-date").each(function ()
                                                  {
                                                    if ($(this).val() != '')
                                                    {
                                                      valid = true;
                                                    }
                                                  })
      }

      $thisForm.find("input:file").each(function ()
                                        {
                                          if ($(this).val() != '')
                                          {
                                            valid = true;
                                          }
                                        });

      if (valid)
      {
        trigger(ca.nrc.cadc.search.events.onValid, {});
      }
      else
      {
        trigger(ca.nrc.cadc.search.events.onInvalid, {});
      }

      return valid;
    }

    /**
     * Toggle a field's disabled attribute.
     *
     * @param node      The node to set.
     * @param disable   The disabled flag to set.
     */
    function toggleDisabled(node, disable)
    {
      node.prop('disabled', disable);

      if (disable === false)
      {
        node.removeAttr('disabled');
      }
    }

    /**
     * Disable searches from this form.
     */
    function disable()
    {
      getForm().prop("disabled", true);
      getForm().find("input:submit").prop("disabled", true);
    }

    /**
     * Enable searches from this form.
     */
    function enable()
    {
      getForm().prop("disabled", false);
      getForm().find("input:submit").prop("disabled", false);
    }

    function clearTargetList()
    {
      var $targetList = getForm().find("input:file[id$='_targetList']");
      $targetList.val("");

      var targetListID = $targetList.attr("id");
      var utypeValue =
        targetListID.substring(0, targetListID.indexOf("_targetList"));

      toggleDisabled($("input[id='" + utypeValue + "']"), false);
      toggleDisabled($targetList, false);

      return $targetList;
    }

    function getTargetNameResolutionStatusObject()
    {
      return getForm().find("span.target_name_resolution_status");
    }

    /**
     * Return those checkboxes that disable other fields to unchecked.
     */
    function clearDisablingCheckboxes()
    {
      // Force issue a change().
      getForm().find("[data-disable-to]:checked").prop("checked",
                                                       false).change();
    }

    /**
     * Clear the target name resolution image.
     */
    function clearTargetNameResolutionStatusOnly()
    {
      var targetNameResolutionStatus = getTargetNameResolutionStatusObject();

      targetNameResolutionStatus.removeClass("busy");
      targetNameResolutionStatus.removeClass("target_ok");
      targetNameResolutionStatus.removeClass("target_not_found");

      // Clear errors
      clearErrors();
    }

    /**
     * Clear the target name resolution image.
     */
    function clearTargetNameResolutionStatus()
    {
      clearTargetNameResolutionStatusOnly();

      // Clear the resolution tooltip.
      clearTargetNameResolutionTooltip();
    }

    /**
     * Remove the data from the target name resolution tooltip.
     */
    function clearTargetNameResolutionTooltip()
    {
      getTargetNameResolutionStatusObject().tooltipster("content", "");
    }

    /**
     * Attempt at cross-browser HTTPRequest creation.
     * @returns {*} Request
     */
    function createRequest()
    {
      var _thisRequest;

      try
      {
        _thisRequest = new XMLHttpRequest();
      }
      catch (trymicrosoft)
      {
        try
        {
          _thisRequest = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (othermicrosoft)
        {
          try
          {
            _thisRequest = new ActiveXObject("Microsoft.XMLHTTP");
          }
          catch (failed)
          {
            _thisRequest = null;
          }
        }
      }

      _selfForm.currentRequest = _thisRequest;
      return _selfForm.currentRequest;
    }

    function submit()
    {
      getForm().submit();
    }

    /**
     * Cancel the current form submission.
     */
    function cancel()
    {
      getForm().stop(true, true);

      if (_selfForm.currentRequest)
      {
        _selfForm.currentRequest.abort();
      }

      trigger(ca.nrc.cadc.search.events.onCancel, {});
    }

    /**
     * Action to perform before form serialization begins.
     */
//    function beforeSerialize(arr, $form, options)
    function beforeSerialize()
    {
      $("#UPLOAD").remove();

      var inputFile = $("input:file[name='targetList']");
      if (inputFile && !inputFile.prop("disabled") && (inputFile.val() !== ""))
      {
        var upload = $("<input>");
        upload.prop("type", "hidden");
        upload.prop("name", "UPLOAD");
        upload.prop("id", "UPLOAD");
        upload.prop("value", "search_upload,param:targetList");

        getForm().append(upload);

        // Update the file input name with the value from the target list
        // select.
        var resolver =
          $("select[id='Plane.position.bounds@Shape1Resolver.value']").val();

        inputFile.prop('name', 'targetList.' + resolver);
      }

      // Save the form to sessionStorage.

      // Observation.observationID=val1&Plane.position.bounds=m101
      sessionStorage.setItem('form_data', getForm().serialize());
      sessionStorage.setItem('isReload', false);
    }

    $(window).ready(function ()
                    {
                      // if time between unload and ready is short (1 second or
                      // less), page is reloaded
                      if (($.now() - sessionStorage.getItem('unloadTime')) <
                          1000)
                      {
                        sessionStorage.setItem('isReload', true);
                      }
                    });

    $(window).unload(function ()
                     {
                       // when page is unloaded, save the selected tab
                       sessionStorage.setItem('unloadTime', $.now());
                       sessionStorage.setItem('isReload', false);
                     });

    /**
     * Hide all of the tooltips.  This is used when the form is submitted.
     */
    function closeAllTooltips()
    {
      var cssSelector = '.' + tooltipIconCSS;
      $(cssSelector).tooltipster('hide');
    }

    function formSubmit(event)
    {
      event.preventDefault();

      closeAllTooltips();

      // Clear old session storage form_data.
      sessionStorage.removeItem('form_data');

      if (validate())
      {
        subscribe(ca.nrc.cadc.search.events.onSubmitComplete,
                  function ()
                  {
                    if (_selfForm.targetTooltipsters.length === 2)
                    {
                      _selfForm.targetTooltipsters[1].disable();
                      _selfForm.targetTooltipsters[1].enable();
                    }
                  });

        var inputFile = $("input:file");
        var isUpload = (inputFile && (inputFile.val() !== ""));
        toggleDisabled($("input[name='targetList']"), false);

        var netStart = (new Date()).getTime();

        try
        {
          $("input." + getConfiguration().getName() + "_selectlist").val(
            getConfiguration().getSelectListString(false));
        }
        catch (e)
        {
          cancel();
          alert("Error: " + e.message);
        }

        getForm().ajaxSubmit(
          {
            url: "/AdvancedSearch/find",
            target: "#file_upload_response",
            dataType: "json",
            beforeSubmit: beforeSerialize,
            success: function (json, textStatus, request)
            {
              getForm().find("input:hidden#target").remove();
              getForm().find("input:hidden#collection").remove();

              _selfForm.currentRequest = null;

              var args =
              {
                "data": json,
                "success": true,
                "startDate": netStart,
                "cadcForm": _selfForm
              };

              trigger(ca.nrc.cadc.search.events.onSubmitComplete, args);
            },
            error: function (request, textStatus, data)
            {
              console.error("Error: " + request.responseText);

              trigger(ca.nrc.cadc.search.events.onSubmitComplete,
                      {
                        "error_url": request.responseText,
                        "success": false,
                        "startDate": netStart,
                        "cadcForm": _selfForm
                      });
            },
            complete: function (request, textStatus, data)
            {
              if (textStatus === 'timeout')
              {
                alert("The search took too long to return.\n" +
                      "Please refine your search or try again later.");

                trigger(ca.nrc.cadc.search.events.onSubmitComplete,
                        {
                          "success": false,
                          "startDate": netStart,
                          "cadcForm": _selfForm,
                          "error": {
                            status: request.status,
                            message: textStatus
                          }
                        });
              }
            },
            iframe: isUpload,
            xhr: createRequest
          });
      }
    }

    function resetFields()
    {
      // function that resets all fields to default values
      var $currentForm = getForm();
      $currentForm.find("input:text").val("");
      $("#UPLOAD").remove();

      $("#include_proprietary").removeAttr("checked");

      clearTargetList();

      getForm().find("select.search_criteria_input").each(
        function ()
        {
          var $selectCriteria = $(this);
          $selectCriteria.val("");
          toggleDisabled($selectCriteria, false);
        });

      getForm().find('input.search_criteria_input').each(
        function ()
        {
          var $formItem = $(this);
          $("label[for='" + $formItem.attr("id") +
            "']").prev("summary").children("span.search_criteria_label_contents").text("");
          toggleDisabled($formItem, false);
          closeDetailsItem($formItem.parents("details"));
        });

      clearTargetNameResolutionStatus();
      clearDisablingCheckboxes();

      $('.hierarchy select').each(function ()
                                  {
                                    $(this).val("");
                                  });

      var firstSelect = $('.hierarchy select:eq(0)');

      $("input[name$='.DOWNLOADCUTOUT']").prop("checked", false);

      // Convert to DOM Element object.
      var jsFirstSelect = document.getElementById(firstSelect.prop("id"));
      if (jsFirstSelect !== null)
      {
        getDataTrain().updateLists(jsFirstSelect, true);
      }

      clearErrors();
      closeAllTooltips();

      trigger(ca.nrc.cadc.search.events.onReset, {});
    }

    function setSelectValue(_uTypeID, _selectID, _optionValue)
    {
      var $detailsItem =
        getForm().find("details[id='" + _uTypeID + "_details']");

      // Only proceed if a valid input ID was passed in.
      if ($detailsItem)
      {
        var $select = $detailsItem.find("select[id='" + _selectID + "']");
        $select.val(_optionValue).change();

        if (_optionValue)
        {
          openDetailsItem($detailsItem);
        }
        else
        {
          closeDetailsItem($detailsItem);
        }
      }
    }

    /**
     * Populate a form <input> value that is encased in a <details> item.  If
     * the value is empty or null, then close the <details> item.
     *
     * @param _inputID      The ID of the <input> field.
     * @param _inputValue   The value to set.
     */
    function setInputValue(_inputID, _inputValue)
    {
      var $inputItem = getForm().find("input[id='" + _inputID + "']");

      // Only proceed if a valid input ID was passed in.
      if ($inputItem)
      {
        if ($inputItem.is(":checkbox"))
        {
          // Default value is checked.
          var checkedFlag = ((_inputValue === true)
                             ||
                             ca.nrc.cadc.search.CHECKBOX_CHECKED_REGEX.test($.trim(_inputValue))
                             || !_inputValue);
          $inputItem.prop("checked", checkedFlag);
        }
        else
        {
          $inputItem.val(_inputValue).change();
        }

        var $detailsItem =
          getForm().find("details[id='" + _inputID + "_details']");

        if (_inputValue)
        {
          openDetailsItem($detailsItem);
        }
        else
        {
          closeDetailsItem($detailsItem);
        }
      }
    }

    /*
     * Update the select element with the select value. Return true if the
     * select is updated, false otherwise.
     * 
     * @param {Object} _select        The select element to up updated.
     * @param {Object} _selectValues   The selected value array.
     * @returns {Boolean}
     */
    function setDatatrainValue(_$select, _selectValues)
    {
      var $options = _$select.find("option").filter(function ()
                                                    {
                                                      return _selectValues.indexOf($(this).val()) >=
                                                             0;
                                                    }).prop("selected", true);

      _$select.val(_selectValues);
      _$select.change();

      // WebRT #55941: adjust scrollbar to expose selected option
      var selected = _$select[0];
      var size = selected.size;
      var index = selected.selectedIndex + 1;
      var ratio = selected.scrollHeight / selected.length;
      var tail = selected.length - selected.selectedIndex;

      // scrollbar is needed?
      if (index > size)
      {
        // yes, selected option on last 'page'?
        if (tail > size)
        {
          // no, scroll to selected option
          // use (index - 1) so that it works on Firefox browsers as well
          // otherwise can just use (index)
          _$select.scrollTop(ratio * (index - 1));
        }
        else
        {
          // yes, scroll to option that displays the last 'page'
          _$select.scrollTop(ratio * (index - (size - tail)));
        }
      }

      return ($options && ($options.length > 0));
    }

    /**
     * Open a one of the hidden items on the form.  This is used by the tooltip
     * examples to set a value.
     *
     * @param $detailsItem    The <details> item to open.
     */
    function openDetailsItem($detailsItem)
    {
      $detailsItem.prop("open", true);
    }

    /**
     * Close a one of the hidden items on the form.  This is used by the tooltip
     * examples to set a value.
     *
     * @param $detailsItem    The <details> item to open.
     */
    function closeDetailsItem($detailsItem)
    {
      $detailsItem.prop("open", false);
    }

    function createField(_row)
    {
      return getConfiguration().createField(_row);
    }

    function clearTimeout()
    {
      if (_selfForm.currentTimeoutID)
      {
        window.clearTimeout(_selfForm.currentTimeoutID);
      }
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
      args.cadcForm = _selfForm;

      return $(_selfForm).trigger(_event, _args);
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param _event      Event object.
     * @param __handler   Handler function.
     */
    function subscribe(_event, __handler)
    {
      $(_selfForm).on(_event.type, __handler);
    }

    $.extend(this,
             {
               // Methods
               "getForm": getForm,
               "getID": getID,
               "getName": getName,
               "getDownloadAccessKey": getDownloadAccessKey,
               "getResultsTableMetadata": getResultsTableMetadata,
               "getConfiguration": getConfiguration,
               "isActive": isActive,
               "resetFields": resetFields,
               "toggleDisabled": toggleDisabled,
               "cancel": cancel,
               "submit": submit,
               "openDetailsItem": openDetailsItem,
               "setInputValue": setInputValue,
               "setSelectValue": setSelectValue,
               "setDatatrainValue": setDatatrainValue,
               "closeDetailsItem": closeDetailsItem,
               "getDataTrain": getDataTrain,
               "init": init,
               "disable": disable,
               "enable": enable,
               "createField": createField,
               "clearTimeout": clearTimeout,
               "loadTooltips": loadTooltips,

               // Event handling.
               "subscribe": subscribe
             });

    if (_autoInitFlag)
    {
      init();
    }
  }

})(jQuery);
