(function ($)
{
  // register namespace
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "TAP_SYNC": "/search/tap/sync",
            "i18n": {
              "en": {
                "ONE_CLICK_DOWNLOAD_TIP": "Single file or .tar if multiple files",
                "ROW_COUNT_MESSAGE": "Showing {0} rows ({1} before filtering).",
                "CROSS_DOMAIN_ERROR": "Server error retrieving data"
              },
              "fr": {
                "ONE_CLICK_DOWNLOAD_TIP": "Seul fichier ou .tar si plusieurs",
                "ROW_COUNT_MESSAGE": "Affichage de {0} résultats ({1} avant l\"application du filtre).",
                "CROSS_DOMAIN_ERROR": "French version of Server error retrieving data"
              }
            },
            "PACKAGE_SERVICE_ENDPOINT": "/search/package",
            "UNIT_CONVERSION_ENDPOINT": "/search/unitconversion/",
            "QUICKSEARCH_SELECTOR": ".quicksearch_link",
            "GRID_SELECTOR": "#resultTable",
            "RESULTS_PAGE_SIZE": 500,
            "AdvancedSearchApp": AdvancedSearchApp,
            "events": {
              "onAdvancedSearchInit": new jQuery.Event("onAdvancedSearchInit")
            },
            "downloadTypes": ["votable", "csv", "tsv"]
          }
        }
      }
    }
  });

  /**
   * The main AdvancedSearch application.
   *
   * @param _pageLanguage   The language from the page.
   * @param _autoInitFlag   Whether to auto-initialize this application.
   * @constructor
   */
  function AdvancedSearchApp(_pageLanguage, _autoInitFlag)
  {
    var _self = this;

    // Stat fields to show on result table.
    var netEnd, loadStart, loadEnd;

    this.pageLanguage = _pageLanguage || "en";

    /**
     * @property
     * @type {StringUtil}
     */
    var stringUtil = new org.opencadc.StringUtil();

    var downloadFormSubmit = $('#downloadFormSubmit');
    var downloadForm = $('#downloadForm');
    var queryOverlay = $('#queryOverlay');
    var queryTab = $('#queryTab');
    var $tabContainer = $('#tabContainer');

    // Text area containing the ADQL query.
    var $queryCode = $('#query');
    var columnManager = new ca.nrc.cadc.search.columns.ColumnManager();
    var resultsVOTV;

    this.caomSearchForm = null;
    this.obsCoreSearchForm = null;

    // The active Form's ID being used to submit the last query.
    var activeFormID;

    function getPageLanguage()
    {
      return _self.pageLanguage;
    }

    function getCAOMSearchForm()
    {
      return _self.caomSearchForm;
    }

    function getObsCoreSearchForm()
    {
      return _self.obsCoreSearchForm;
    }

    function getDownloadForm()
    {
      return _self.downloadForm;
    }

    function setCAOMSearchForm(form)
    {
      _self.caomSearchForm = form;
    }

    function setObsCoreSearchForm(form)
    {
      _self.obsCoreSearchForm = form;
    }

    /**
     * Obtain the currently active form object.
     */
    function getActiveForm()
    {
      if (!activeFormID)
      {
        activeFormID = (getActiveTabID().toLowerCase().indexOf('obscore') > 0)
            ? getObsCoreSearchForm().getID() : getCAOMSearchForm().getID();
      }

      return (!activeFormID || getCAOMSearchForm().isActive(activeFormID))
          ? getCAOMSearchForm() : getObsCoreSearchForm();
    }

    /**
     * Obtain the currently set maximum record return count.
     * @returns {Number}
     */
    function getMaxRecordCount()
    {
      return getActiveForm().getForm().find('input[name="MaxRecords"]').val();
    }

    /**
     * Pretty print the ADQL in the text area.
     *
     * @param adqlText
     * @returns {XML|string}
     */
    function adqlPrint(adqlText)
    {
      return adqlText.replace(/(FROM|WHERE|AND)/g,
                              function (match)
                              {
                                return '\n' + match;
                              }).replace(/JOIN/g,
                                         function (match)
                                         {
                                           return '\n\t' + match;
                                         }).replace(/,/g,
                                                    function (match)
                                                    {
                                                      return match + '\n\t';
                                                    });
    }

    /**
     * Post-query load of the ADQL results.
     */
    function loadUWSJob(jobURL, successCallback, failCallback)
    {
      if (jobURL)
      {
        var jobLoader = new ca.nrc.cadc.search.uws.UWSJobLoader(jobURL);

        jobLoader.subscribe(ca.nrc.cadc.search.uws.events.onJobLoaded, successCallback);
        jobLoader.subscribe(ca.nrc.cadc.search.uws.events.onJobLoadFailed, failCallback);

        jobLoader.load();
      }
      else
      {
        console.error('Unable to obtain Job ADQL.');
      }
    }

    /**
     * Set the ADQL data from the current job, if any.
     *
     * @param _includeExtendedColumns   Whether to include the 'invisible' set
     *                                  of columns in the SELECT clause.
     * @returns {String} text of ADQL.
     */
    function getADQL(_includeExtendedColumns)
    {
      var jobString = sessionStorage.getItem('uws_job');
      var adqlText;

      if (jobString)
      {
        var jobJSON = JSON.parse(jobString);
        var uwsJobParser = new ca.nrc.cadc.search.uws.json.UWSJobParser(jobJSON);
        adqlText = uwsJobParser.getJob().getParameterValue('QUERY');

        var selectListString = getActiveForm().getConfiguration().getSelectListString(_includeExtendedColumns);

        adqlText = 'SELECT ' + selectListString + ' ' + adqlText.substring(adqlText.indexOf('FROM'));
      }
      else
      {
        adqlText = ''
      }

      return adqlText;
    }

    /**
     * Get the metadata for all columns
     *
     * @param callback    Function to indicate completion.
     */
    function initConfigurations(callback)
    {
      var tapQuery = 'select * from TAP_SCHEMA.columns where '
                     + '((table_name=\'caom2.Observation\' or '
                     + 'table_name=\'caom2.Plane\') and utype like \'caom2:%\') or '
                     + '(table_name=\'ivoa.ObsCore\' and utype like \'obscore:%\')';

      var caomFormConfig = new ca.nrc.cadc.search.FormConfiguration(new ca.nrc.cadc.search.CAOM2.FormConfiguration());
      var obsCoreFormConfig = new ca.nrc.cadc.search.FormConfiguration(
          new ca.nrc.cadc.search.ObsCore.FormConfiguration());

      $.get(ca.nrc.cadc.search.TAP_SYNC,
            {
              REQUEST: 'doQuery',
              LANG: 'ADQL',
              QUERY: tapQuery,
              FORMAT: 'votable'
            },
            function (data)
            {
              new cadc.vot.Builder(
                  1000,
                  {
                    xmlDOM: data
                  },
                  function (voTableBuilder)
                  {
                    voTableBuilder.build(voTableBuilder.buildRowData);

                    var voTable = voTableBuilder.getVOTable();
                    var resources = voTable.getResources();
                    var tables = resources[0].getTables();
                    var tableData = tables[0].getTableData();
                    var rows = tableData.getRows();

                    for (var ri = 0, rl = rows.length; ri < rl; ri++)
                    {
                      var nextRow = rows[ri];
                      var cells = nextRow.getCells();

                      var tableName;

                      for (var ci = 0, cl = cells.length; ci < cl; ci++)
                      {
                        var nextCell = cells[ci];
                        var nextFieldName = nextCell.getField().getName();

                        if (nextFieldName === 'table_name')
                        {
                          tableName = nextCell.getValue();
                          break;
                        }
                      }

                      if (tableName.indexOf("caom2") >= 0)
                      {
                        caomFormConfig.addField(nextRow);
                      }
                      else if (tableName.indexOf("ivoa") >= 0)
                      {
                        obsCoreFormConfig.addField(nextRow);
                      }
                    }

                    callback(null, caomFormConfig, obsCoreFormConfig);
                  });
            }, "xml").fail(function ($xhr, textStatus)
                           {
                             callback("ERROR: TAP query failed: "
                                      + ($xhr.responseXML ? $xhr.responseXML : $xhr.responseText)
                                      + "( " + textStatus + " )", null, null);
                           });
    }

    /**
     * Initialize all things pertinent to the application.
     */
    this.init = function ()
    {
      // Internet Explorer compatibility.
      //
      // WebRT 48318
      // jenkinsd 2014.02.13
      //
      wgxpath.install();

      this.subscribe(ca.nrc.cadc.search.events.onAdvancedSearchInit, function ()
      {
        /*
         * Story 1644
         * The start method should set this, then tabs after that should set the
         * hash as they go.
         *
         * This should be automatic from the easy tabs library, but I have a
         * feeling the WET 3.1 library is getting in the way.
         *
         * TODO - Re-evaluate when WET 4.0 is implemented!
         *
         * jenkinsd 11.10.2014
         */
        $.address.change(function (event)
                         {
                           var slashIndex = event.value.indexOf("/");
                           var eventHash = ((slashIndex >= 0)
                               ? event.value.substring(slashIndex + 1)
                               : event.value);

                           var tabID = eventHash || getActiveTabID().split("#")[1];

                           if (!eventHash)
                           {
                             window.location.hash = "#" + tabID;
                           }
                           else
                           {
                             selectTab(tabID);
                           }
                         });
      });

      initConfigurations(function (error, caomConfiguration, obsCoreConfiguration)
                         {
                           if (error)
                           {
                             var errorMessage = "Metadata field failed to initialize: " +
                                                error;
                             console.error(errorMessage);
                             this._trigger(ca.nrc.cadc.search.events.onAdvancedSearchInit,
                                           {
                                             error: errorMessage
                                           });
                           }
                           else
                           {
                             cleanMetadata(caomConfiguration);
                             cleanMetadata(obsCoreConfiguration);

                             initForms(caomConfiguration, obsCoreConfiguration);
                             this._trigger(ca.nrc.cadc.search.events.onAdvancedSearchInit, {});
                           }
                         }.bind(this));

      /*
       * Story 1644
       * On tab click, update the window hash.  When the window hash is updated,
       * the $.address.change() will be activated.
       *
       * This should be automatic from the easy tabs library, but I have a
       * feeling the WET 3.1 library is getting in the way.
       *
       * TODO - Re-evaluate when WET 4.0 is implemented!
       *
       * jenkinsd 11.10.2014
       */
      $('li.tab').click(function ()
                        {
                          window.location.hash = $(this).find("a").first().attr("href");
                        });
    };

    /**
     * Remove empty or non-existent fields from the metadata.
     * @param _formConfig   The form configuration to modify.
     */
    function cleanMetadata(_formConfig)
    {
      var metadataFields = _formConfig.getTableMetadata().getFields();
      var cleanFields = [];

      for (var i = 0; i < metadataFields.length; i++)
      {
        var f = metadataFields[i];
        if (f)
        {
          cleanFields.push(f);
        }
      }

      _formConfig.getTableMetadata().setFields(cleanFields);
    }

    /**
     * Deserialize the form data in the search results.
     */
    function deserializeFormData(formData)
    {
      // RegEx for '+' character.
      var plus = /\+/g;
      var map = {};

      if ((formData) && (formData.length > 0))
      {
        var kvPairs = formData.split("&");
        for (var i = 0; i < kvPairs.length; i++)
        {
          var kvPair = kvPairs[i].split("=");
          var key = decodeURIComponent(kvPair[0]).replace(plus, " ");
          var value = decodeURIComponent(kvPair[1]).replace(plus, " ");

          // if an entry already exists for the key, add value to existing
          // value(s)
          if ((map[key]) && (map[key].length > 0))
          {
            // already has a value, add new value 
            map[key].push(value);
          }
          else
          {
            var arr = [];
            arr[0] = value;
            map[key] = arr;
          }
        }
      }

      return map;
    }

    /**
     * Repopulates the form using the form data passed in.
     */
    function repopulateForm(formDataMap)
    {
      var mCollections = $('#Observation\\.collection').val();
      for (var k in formDataMap)
      {
        if (formDataMap.hasOwnProperty(k))
        {
          var values = formDataMap[k];

          if (values)
          {
            // get current element
            var currentEl = $('[id="' + k + '"]');

            if ((currentEl) && (currentEl.length > 0))
            {
              // we have the specified element
              if (currentEl.prop("tagName").toLowerCase() === "input")
              {
                if (currentEl.prop("type").toLowerCase() === "text")
                {
                  if (values[0].length > 0)
                  {
                    // repopulate text input
                    getActiveForm().setInputValue(currentEl.prop("id"), decodeURIComponent(values[0]));
                  }
                }
                else if (currentEl.prop("type").toLowerCase() === "checkbox")
                {
                  // repopulate a checkbox
                  currentEl.prop("checked", (values[0] === "on"));
                }
              }
              else if (currentEl.prop("tagName").toLowerCase() === "select")
              {
                // De-select all of the options, first.
                currentEl.find("option").prop("selected", false);
                var sourceValues = values;

                // repopulate either a dropdown list or a hierarchy select
                if ((k === "Observation.collection") && (mCollections !== null) && (mCollections.length > 0))
                {
                  sourceValues = mCollections;
                }

                for (var i = 0; i < sourceValues.length; i++)
                {
                  currentEl.find("option[value='" + sourceValues[i] + "']").prop("selected", true);
                }
              }
            }
          }
        }
      }
    }

    /**
     * If the table viewer isn't displayed, check for cached form data,
     * and re-post the query displaying the search results.
     */
    function updateResults()
    {
      if (!$('div.slick-viewport').length)
      {
        var formData = sessionStorage.getItem("form_data");
        if (formData)
        {
          // Deserialize and repopulate form.
          var formDataMap = deserializeFormData(formData);
          repopulateForm(formDataMap);
          getActiveForm().submit();
        }
      }
    }

    /**
     * Update the existing tab content.
     * @param tabID   The ID of the current tab.
     */
    function updateCurrentTabContent(tabID)
    {
      if (tabID === 'resultTableTab')
      {
        updateResults();
      }
      else if (tabID === 'queryTab')
      {
        $queryCode.text(adqlPrint(getADQL(false)));
      }
    }

    /**
     * On address change, select that tab.
     * @param tabID   The TabID from the hash.
     */
    function selectTab(tabID)
    {
      // If it's been initialized.
      if ($tabContainer.easytabs)
      {
        $tabContainer.easytabs("select", "#" + tabID);
      }

      // Scroll to top.
      window.scrollTo(0, 0);

      updateCurrentTabContent(tabID);

      // Update the language selector link.
      var $languageLink = $('a.lang-link');

      if ($languageLink.length > 0)
      {
        var currLink = $languageLink.attr("href");

        if (currLink)
        {
          if (currLink.indexOf("#") >= 0)
          {
            $languageLink.attr("href", currLink.slice(0, currLink.indexOf("#")) + "#" + tabID);
          }
          else
          {
            $languageLink.attr("href", currLink + "#" + tabID);
          }
        }
      }
    }

    function initForms(caomConfiguration, obsCoreConfiguration)
    {
      setCAOMSearchForm(new ca.nrc.cadc.search.SearchForm("queryForm", false, caomConfiguration));
      setObsCoreSearchForm(new ca.nrc.cadc.search.SearchForm("obscoreQueryForm", false, obsCoreConfiguration));

      jQuery.fn.exists = function ()
      {
        return this.length > 0;
      };

      // Used to send arrays of values as a parameter to a GET request.
      jQuery.ajaxSettings.traditional = true;

      // Disable the forms to begin with.
      getCAOMSearchForm().disable();
      getObsCoreSearchForm().disable();

      var tooltipURL = "json/tooltips_" + getPageLanguage() + ".json";

      $.getJSON(tooltipURL, function (jsonData)
      {
        getCAOMSearchForm().loadTooltips(jsonData);
        getObsCoreSearchForm().loadTooltips(jsonData);
      });

      // Trap the backspace key to prevent it going 'Back' when not using it to
      // delete characters.                                      tabContainer
      // Story 959 - Task 2920.
      // jenkinsd 2012.05.24
      //
      $('html').keydown(function (event)
                        {
                          if (event.keyCode === 8)
                          {
                            var currentFocus = $('*:focus');

                            if (!currentFocus.is("input") && !currentFocus.is("textarea"))
                            {
                              event.preventDefault();
                            }
                          }
                        });

      /**
       *
       * @returns {string}
       */
      var getFormQueryString = function ()
      {
        var parameters = [];
        var $activeFormObject = getActiveForm().getForm();
        var fields = $activeFormObject.serializeArray();
        $.each(fields, function (index, field)
        {
          if (field.value &&
              field.name !== "sort_column" &&
              field.name !== "sort_order" &&
              field.name !== "formName" &&
              field.name !== "SelectList" &&
              field.name !== "MaxRecords" &&
              field.name !== "format" &&
              field.name !== "Form.name")
          {
            parameters.push($activeFormObject.find("[name='" + field.name + "']").attr("id") + "="
                            + encodeURIComponent(field.value.replace(/\%/g, '*')));
          }
        });

        return (parameters.length > 0) ? ("?" + parameters.join("&")) : "";
      };

      // Form setup and binding of events.
      var caomform = getCAOMSearchForm();
      var obscoreform = getObsCoreSearchForm();

      var onFormCancel = function ()
      {
        console.warn("Cancelling search.");
        queryOverlay.popup("close");
      };

      caomform.subscribe(ca.nrc.cadc.search.events.onCancel, onFormCancel);
      obscoreform.subscribe(ca.nrc.cadc.search.events.onCancel, onFormCancel);

      var onFormSubmitComplete = function (eventData, args)
      {
        if (args.success)
        {
          processResults(args.data, args.startDate, function ()
          {
            // Perform a results tab link click here to simulate moving to the
            // results tab.
            $('#resultTableTab-link').click();
          });
        }
        else
        {
          processErrorResults(args.error_url, args.startDate,
                              args.cadcForm.getForm());
        }
      };

      caomform.subscribe(ca.nrc.cadc.search.events.onSubmitComplete,
                         onFormSubmitComplete);
      obscoreform.subscribe(ca.nrc.cadc.search.events.onSubmitComplete,
                            onFormSubmitComplete);

      var onFormValid = function (eventData, args)
      {
        if (resultsVOTV)
        {
          resultsVOTV.destroy();
        }

        var cadcForm = args.cadcForm;

        // Searching on different data.  Switch the columns.
        if (!activeFormID || !cadcForm.isActive(activeFormID))
        {
          // This is now the active form.
          activeFormID = cadcForm.getID();
        }

        var formatCheckbox = function ($rowItem)
        {
          if (!stringUtil.hasText($rowItem[getActiveForm().getDownloadAccessKey()]))
          {
            var $checkboxSelect = $("input:checkbox._select_" + $rowItem.id);
            var $parentContainer = $checkboxSelect.parent("div");

            $parentContainer.empty();
            $("<span class=\"_select_" + $rowItem.id + "\">N/A</span>").appendTo($parentContainer);
          }
        };

        // To be used when the grid.onRenderedRows event is
        // fired.
        var onRowRendered = function ($rowItem, rowIndex)
        {
          if ($rowItem)
          {
            formatCheckbox($rowItem, rowIndex);
          }
        };

        var isRowDisabled = function (row)
        {
          var downloadableColumnName = getActiveForm().getDownloadAccessKey();
          var downloadableColumnValue =
              row.getCellValue(downloadableColumnName);

          return (downloadableColumnValue === null);
        };

        var rowCountMessage = function (totalRows, rowCount)
        {
          return stringUtil.format(ca.nrc.cadc.search.i18n[getPageLanguage()]["ROW_COUNT_MESSAGE"],
                                   [totalRows, rowCount]);
        };

        var oneClickDownloadTitle = function ()
        {
          return ca.nrc.cadc.search.i18n[getPageLanguage()]["ONE_CLICK_DOWNLOAD_TIP"];
        };

        // Options for the CADC VOTV instance
        var cadcVOTVOptions =
            {
              editable: false,
              enableAddRow: false,
              showHeaderRow: true,
              showTopPanel: false,
              enableCellNavigation: true,
              asyncEditorLoading: true,
              defaultColumnWidth: 100,
              explicitInitialization: false,
              enableAsyncPostRender: true,
              fullWidthRows: false,
              pager: false,
              headerRowHeight: 50,
              multiSelect: true,
              propagateEvents: true,
              leaveSpaceForNewRows: false,
              // ID of the sort column (Start Date).
              sortColumn: getActiveForm().getConfiguration().getDefaultSortColumnID(),
              sortDir: "desc",
              topPanelHeight: 5,
              enableTextSelectionOnCells: true,
              gridResizable: false,
              rerenderOnResize: false,
              emptyResultsMessageSelector: "#cadcvotv-empty-results-message",
              frozenColumn: 0,
              frozenBottom: false,
              enableSelection: true,
              suggest_maxRowCount: 7,
              targetNodeSelector: "#resultTable",    // Shouldn't really be an
                                                     // option as it's mandatory!
              columnFilterPluginName: "suggest",
              enableOneClickDownload: true,
              oneClickDownloadTitle: oneClickDownloadTitle(),
              oneClickDownloadURL: ca.nrc.cadc.search.PACKAGE_SERVICE_ENDPOINT,
              oneClickDownloadURLColumnID: getActiveForm().getConfiguration().getDownloadAccessKey(),
              headerCheckboxLabel: "Mark",
              rowManager: {
                onRowRendered: onRowRendered,
                isRowDisabled: isRowDisabled
              },
              columnManager: {
                filterable: true,
                forceFitColumns: false,
                resizable: true,

                // Story 1647
                // Generic formatter.  Needs to have a format(column, value)
                // method.
                formatter: columnManager,
                picker: {
                  style: "dialog",
                  options: {
                    showAllButtonText: $('#COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT').text(),
                    resetButtonText: $('#COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT').text(),
                    orderAlphaButtonText: $('#COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT').text(),
                    dialogTriggerID: "slick-columnpicker-panel-change-column",
                    targetSelector: $('#column_manager_container').find('.column_manager_columns').first(),
                    position: {my: "right", at: "right bottom"},
                    closeDialogSelector: ".dialog-close",
                    refreshPositions: true
                  }
                }
              },
              maxRowLimit: getMaxRecordCount(),
              maxRowLimitWarning: $('#max_row_limit_warning').val(),
              rowCountMessage: rowCountMessage,
              plugins: {
                footprint: {
                  hidden: true,
                  enabled: true,
                  onHover: false,
                  onClick: true,
                  maxRowCount: 10000,
                  renderedRowsOnly: false,
                  toggleSwitchSelector: "#slick-visualize",
                  footprintFieldID: getActiveForm().getConfiguration().getFootprintColumnID(),
                  fovFieldID: getActiveForm().getConfiguration().getFOVColumnID(),
                  raFieldID: getActiveForm().getConfiguration().getRAColumnID(),
                  decFieldID: getActiveForm().getConfiguration().getDecColumnID()
                }
              }
            };

        var options = columnManager.getOptions();
        var opts = $.extend(true, {}, cadcVOTVOptions, options);

        resultsVOTV = new cadc.vot.Viewer(ca.nrc.cadc.search.GRID_SELECTOR,
                                          opts);

        // Unfortunately this has to be selected at the Document level since
        // the
        // items in question (located by
        // ca.nrc.cadc.search.QUICKSEARCH_SELECTOR) aren't actually created
        // yet.  jenkinsd 2015.05.08
        $(document).on("click", ca.nrc.cadc.search.QUICKSEARCH_SELECTOR,
                       function (event)
                       {
                         var hrefURI = new cadc.web.util.URI(event.target.href);
                         var href = hrefURI.toString();

                         // Strip off the fragment part of the
                         // href url if necessary.
                         var index = href.indexOf("#");
                         if (index !== -1)
                         {
                           href = href.substring(0, index);
                         }

                         var serializer = new cadc.vot.ResultStateSerializer(
                             href,
                             resultsVOTV.sortcol,
                             resultsVOTV.sortDir ? "asc" : "dsc",
                             resultsVOTV.getDisplayedColumns(),
                             resultsVOTV.getResizedColumns(),
                             resultsVOTV.getColumnFilters(),
                             resultsVOTV.getUpdatedColumnSelects());

                         var windowName = "_" + $(event.target).text();

                         window.open(serializer.getResultStateUrl(), windowName,
                                     '');

                         return false;
                       });

        resultsVOTV.subscribe(cadc.vot.events.onUnitChanged,
                              function (event, args)
                              {
                                var columnID = args.column.id;
                                var filterValue =
                                    resultsVOTV.getColumnFilters()[columnID];

                                processFilterValue(filterValue, args,
                                                   function (breakdownPureFilterValue,
                                                             breakdownDisplayFilterValue)
                                                   {
                                                     $(args.column).data("pureFilterValue",
                                                                         breakdownPureFilterValue);

                                                     resultsVOTV.setColumnFilter(columnID,
                                                                                 breakdownDisplayFilterValue);
                                                     resultsVOTV.getColumnFilters()[columnID] =
                                                         breakdownDisplayFilterValue;
                                                   });
                              });

        downloadFormSubmit.click(function (event)
                                 {
                                   event.preventDefault();

                                   downloadForm.find("input[name='uri']").remove();

                                   if (resultsVOTV.getSelectedRows().length <=
                                       0)
                                   {
                                     alert(downloadForm.find("span#NO_OBSERVATIONS_SELECTED_MESSAGE").text());
                                   }
                                   else
                                   {
                                     $.each(resultsVOTV.getSelectedRows(), function (arrayIndex, selectedRowIndex)
                                     {
                                       var $nextRow = resultsVOTV.getRow(selectedRowIndex);
                                       var $nextPlaneURI = $nextRow["caom2:Plane.uri.downloadable"];

                                       var $input = $('<input>');
                                       $input.prop("type", "hidden");
                                       $input.prop("name", "uri");
                                       $input.prop("id", $nextPlaneURI);
                                       $input.val($nextPlaneURI);

                                       downloadForm.append($input);
                                     });

                                     // Story 1566, when all 'Product Types'
                                     // checkboxes are checked, do not send any
                                     var allChecked = downloadForm.find("input.product_type_option_flag").not(":checked").length ===
                                                      0;
                                     if (allChecked)
                                     {
                                       // disable all 'Product Types' checkboxes
                                       $.each(downloadForm.find("input.product_type_option_flag:checked"), function ()
                                       {
                                         $(this).prop('disabled', true);
                                       });
                                     }

                                     window.open('', 'DOWNLOAD', '');
                                     downloadForm.submit();

                                     // Story 1566, re-enable all product types
                                     // checkbox
                                     if (allChecked)
                                     {
                                       // re-enable all 'Product Types'
                                       // checkboxes
                                       $.each(downloadForm.find("input.product_type_option_flag:checked"), function ()
                                       {
                                         $(this).prop('disabled', false);
                                       });
                                     }
                                   }
                                 });

        $('#results_bookmark').click(function (event)
                                     {
                                       event.preventDefault();
                                       var hrefURI = new cadc.web.util.URI(this.href);
                                       hrefURI.clearQuery();
                                       var href = hrefURI.toString();

                                       // Strip off the fragment part of the
                                       // href url if necessary.
                                       var index = href.indexOf("#");
                                       if (index !== -1)
                                       {
                                         href = href.substring(0, index);
                                       }

                                       var serializer = new cadc.vot.ResultStateSerializer(
                                           href + getFormQueryString(),
                                           resultsVOTV.sortcol,
                                           resultsVOTV.sortDir ? "asc" : "dsc",
                                           resultsVOTV.getDisplayedColumns(),
                                           resultsVOTV.getResizedColumns(),
                                           resultsVOTV.getColumnFilters(),
                                           resultsVOTV.getUpdatedColumnSelects());
                                       alert(serializer.getResultStateUrl());
                                     });

        resultsVOTV.setDisplayColumns([]);

        // Set the default columns.
        setDefaultColumns(resultsVOTV);
        setDefaultUnitTypes(resultsVOTV);

        queryOverlay.find("#overlay_cancel").show();
        queryOverlay.popup("open");
      };

      caomform.subscribe(ca.nrc.cadc.search.events.onValid, onFormValid);
      obscoreform.subscribe(ca.nrc.cadc.search.events.onValid, onFormValid);

      var onFormInvalid = function ()
      {
        alert("Please enter at least one value to search on.");
      };

      caomform.subscribe(ca.nrc.cadc.search.events.onInvalid, onFormInvalid);
      obscoreform.subscribe(ca.nrc.cadc.search.events.onInvalid, onFormInvalid);

      $(':reset').click(function ()
                        {
                          getActiveForm().resetFields();
                        });

      $('#cancel_search').click(function ()
                                {
                                  getActiveForm().cancel();
                                });

      // End form setup.
    }

    // End initForms function.

    /**
     * Obtain the currently active tab's ID.
     *
     * Check the sessionStorage for the activePanel component, then the
     * currently listed active tab (i.e. with class 'active').
     */
    function getActiveTabID()
    {
      var activeTab = $('ul#tabList li.active');
      var defaultTab = $('ul#tabList li.default');
      var langURLPath = $("span[lang='" + getPageLanguage() + "'].lang-link-target").text();
      var cachedTabID =
          sessionStorage.getItem("activePanel-" + langURLPath + "0");
      var targetTabID;

      if (cachedTabID)
      {
        targetTabID = cachedTabID;
      }
      else if (activeTab && activeTab.find("a:first").length)
      {
        targetTabID = activeTab.find("a:first").attr("href");
      }
      else
      {
        targetTabID = defaultTab.find("a:first").attr("href");
      }

      return targetTabID;
    }

    /**
     * Start this application.  This will check for a quick submission.
     */
    function start()
    {
      // After the series of columns (Data Train) has loaded, then proceed.
      var postDataTrainLoad = function (_continue)
      {
        if (_continue)
        {
          var queryObject = currentURI().getQuery();

          // Work directly with the form object.
          var $submitForm = getActiveForm().getForm();
          var doSubmit;

          if (JSON.stringify(queryObject) !== JSON.stringify({}))
          {
            // Update text fields.
            $.each(queryObject, function (qKey, qValue)
            {
              if (qValue && (qValue.length > 0))
              {
                if ((qKey === ca.nrc.cadc.search.CAOM2_RESOLVER_VALUE_KEY)
                    || (qKey === ca.nrc.cadc.search.OBSCORE_RESOLVER_VALUE_KEY))
                {
                  getActiveForm().clearTimeout();
                  getActiveForm().setSelectValue(ca.nrc.cadc.search.CAOM2_TARGET_NAME_FIELD_ID, qKey,
                                                 decodeURIComponent(qValue.join()));
                }
                else
                {
                  getActiveForm().setInputValue(qKey, decodeURIComponent(qValue.join()));
                }

                doSubmit = true;
              }
            });

            getActiveForm().getForm().find("input").change();

            // Update datatrain
            var dtUtype = $submitForm.find(".hierarchy_utype").text();
            var dtUtypes = dtUtype.split("/");
            for (var i = 0; i < dtUtypes.length; i++)
            {
              var dtSelectUtype = dtUtypes[i];
              var dtSelectUtypeValues = [];

              // Array of values.
              var dtSelectValues = currentURI.getQueryValues(dtSelectUtype);

              if (dtSelectValues && (dtSelectValues.length > 0))
              {
                dtSelectUtypeValues =
                    dtSelectUtypeValues.concat(dtSelectValues);
              }

              // The "collection" keyword is grandfathered in, but actually
              // maps to Observation.collection, so check for "collection" while
              // we're checking for Observation.collection.
              //
              // jenkinsd 2014.02.25
              if (dtSelectUtype === 'Observation.collection')
              {
                var grandfatheredCollectionValues =
                    currentURI.getQueryValues("collection");

                if (grandfatheredCollectionValues
                    && (grandfatheredCollectionValues.length > 0))
                {
                  dtSelectUtypeValues =
                      dtSelectUtypeValues.concat(grandfatheredCollectionValues);
                }
              }

              if (dtSelectUtypeValues && (dtSelectUtypeValues.length > 0))
              {
                var dtSelect = $submitForm.find('select[id="' + dtSelectUtype
                                                + '"]');
                if (dtSelect
                    && getActiveForm().setDatatrainValue($(dtSelect[0]),
                                                         dtSelectUtypeValues))
                {
                  doSubmit = true;
                }
                else
                {
                  alert('Incompatible query parameter: '
                        + dtSelectUtype + " > " + dtSelectUtypeValues);
                  getActiveForm().cancel();
                  doSubmit = false;
                  break;
                }
              }
            }
          }

          if (doSubmit && (!stringUtil.hasText(currentURI.getQueryValue("noexec"))
                           || (currentURI.getQueryValue("noexec") === "false")))
          {
            // Initialize popup.
            $('#queryOverlay').popup();

            // Execute the form submission.
            getActiveForm().submit();
          }
          else
          {
            // If the current tab is the results tab, display the search
            // results if necessary.

            var activeTabID = window.location.hash || getActiveTabID();
            var isNoExecFlag = ((currentURI.getQueryValue('noexec') !== null)
                                && (currentURI.getQueryValue('noexec') === "true"));
            var destinationTabID;

            if (((activeTabID !== "#queryFormTab") && (sessionStorage.getItem("isReload") === false)) || isNoExecFlag
                || !activeTabID)
            {
              // go to query tab
              destinationTabID = "queryFormTab";
            }
            else
            {
              destinationTabID = activeTabID.substring(1);
            }

            //window.location.hash = "#" + destinationTabID;
            selectTab(destinationTabID);
          }
        }
      };

      // Default form.
      getCAOMSearchForm().subscribe(ca.nrc.cadc.search.events.onInit,
                                    function (event, args)
                                    {
                                      if (args && args.error)
                                      {
                                        console.error(
                                            'Error reading TAP schema >> '
                                            + args.error);
                                      }
                                      else
                                      {
                                        getCAOMSearchForm().enable();
                                        getCAOMSearchForm().resetFields();
                                      }
                                    });

      getCAOMSearchForm().getDataTrain().subscribe(ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
                                                   function ()
                                                   {
                                                     postDataTrainLoad(true);
                                                   });

      getCAOMSearchForm().getDataTrain().subscribe(ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
                                                   function ()
                                                   {
                                                     postDataTrainLoad(false);
                                                   });

      getObsCoreSearchForm().subscribe(ca.nrc.cadc.search.events.onInit,
                                       function ()
                                       {
                                         getObsCoreSearchForm().enable();
                                         getObsCoreSearchForm().resetFields();
                                       });

      getCAOMSearchForm().init();
      getObsCoreSearchForm().init();
    }

    // End start method.

    /**
     * Default columns for a new search.  If previously saved columns exist,
     * then use those.
     *
     * @param _viewer  {cadc.vot.Viewer}    The VOTV viewer instance.
     */
    function setDefaultColumns(_viewer)
    {
      // Check if defaultColumnIDs has already been set in the
      // viewer options (i.e. from a bookmark url) and if so use them.

      // Clear the existing ones first.
      _viewer.getOptions().defaultColumnIDs = [];

      var deserializer = new cadc.vot.ResultStateDeserializer(window.location.href);
      var viewerOptions = deserializer.getViewerOptions();

      if (!$.isEmptyObject(viewerOptions))
      {
        $.extend(true, _viewer.getOptions(), sanitizeColumnOptions(viewerOptions));
      }

      if (!_viewer.getOptions().defaultColumnIDs || _viewer.getOptions().defaultColumnIDs.length === 0)
      {
        var $activeFormConfiguration = getActiveForm().getConfiguration();
        _viewer.getOptions().defaultColumnIDs = $activeFormConfiguration.getDefaultColumnIDs();
      }
    }

    function sanitizeColumnOptions(_columnOptions)
    {
      var sanitizedObject = {};

      sanitizedObject.sortColumn = columnManager.getIDFromLabel(_columnOptions.sortColumn);
      sanitizedObject.sortDir = _columnOptions.sortDir;

      if (_columnOptions.columnOptions)
      {
        sanitizedObject.columnOptions = {};
        $.each(_columnOptions.columnOptions, function (key, obj)
        {
          sanitizedObject.columnOptions[columnManager.getIDFromLabel(key)] = obj;
        });
      }

      if (_columnOptions.columnFilters)
      {
        sanitizedObject.columnFilters = {};
        $.each(_columnOptions.columnFilters, function (key, obj)
        {
          sanitizedObject.columnFilters[columnManager.getIDFromLabel(key)] =
              obj;
        });
      }

      if (_columnOptions.defaultColumnIDs)
      {
        sanitizedObject.defaultColumnIDs = [];

        for (var i = 0; i < _columnOptions.defaultColumnIDs.length; i++)
        {
          sanitizedObject.defaultColumnIDs.push(
              columnManager.getIDFromLabel(_columnOptions.defaultColumnIDs[i]));
        }
      }

      return sanitizedObject;
    }

    /**
     * Default unit type specfic to a collection.
     *
     * @param _viewer {cadc.vot.Viewer}     The VOTV viewer instance.
     */
    function setDefaultUnitTypes(_viewer)
    {
      var unitTypes = getActiveForm().getConfiguration().getDefaultUnitTypes();

      for (var columnName in unitTypes)
      {
        var newDefaultAdded = false;
        var oldDefaultRemoved = false;

        if (unitTypes.hasOwnProperty(columnName))
        {
          var defaultUnitType = unitTypes[columnName];
          var columnOptions = _viewer.getOptionsForColumn(columnName);
          var units = columnOptions['header']['units'];
          for (var i = 0; i < units.length; i++)
          {
            var unit = units[i];
            if (defaultUnitType === unit['value'])
            {
              if (unit['default'])
              {
                // no need to remove default unit type
                oldDefaultRemoved = true;
              }

              unit['default'] = true;
              newDefaultAdded = true;
            }
            else
            {
              // look for default being set in other unit types
              if (unit['default'])
              {
                // remove it
                delete unit['default'];
                oldDefaultRemoved = true;
              }
            }

            if (newDefaultAdded && oldDefaultRemoved)
            {
              break;
            }
          }

          _viewer.setOptionsForColumn(columnName, columnOptions);
        }
      }
    }

    // Called when the results are in and the UWS Job is complete.
    function setJobParameters(jobParams, callback)
    {
      var queryParam = "QUERY=" + encodeURIComponent(getADQL(true));
      var votableURL = ca.nrc.cadc.search.TAP_SYNC + "?LANG=ADQL&REQUEST=doQuery&" + queryParam;

      if (jobParams.upload_url && (jobParams.upload_url !== null))
      {
        votableURL += "&UPLOAD=" + encodeURIComponent(jobParams.upload_url);
      }

      for (var dti = 0, dtl = ca.nrc.cadc.search.downloadTypes.length; dti < dtl; dti++)
      {
        var nextDownloadType = ca.nrc.cadc.search.downloadTypes[dti];

        var nextVOTableURI = new cadc.web.util.URI(votableURL + "&FORMAT=" + nextDownloadType);
        $("a.votable_link_" + nextDownloadType).prop("href", nextVOTableURI.getRelativeURI());
      }

      if (callback)
      {
        callback();
      }
    }

    function postQuerySubmission(jobParams)
    {
      queryOverlay.popup("close");

      var selectAllCheckbox = $("input[name='selectAllCheckbox']");
      selectAllCheckbox.prop("title", "Mark/Unmark all");

      $("select#download_option_product_type").focus();

      $(".cellValue.preview").tooltip({
                                        position: "bottom right",
                                        offset: [-10, -10],
                                        effect: "toggle",
                                        delay: 0,
                                        relative: true,
                                        events: {
                                          def: "mouseover,mouseout",
                                          input: "focus,blur",
                                          widget: "focus mouseenter,blur mouseleave",
                                          tooltip: "mouseover,mouseout"
                                        }
                                      });
      setJobParameters(jobParams);
    }

    function processErrorResults(error_url)
    {
      var $errorTooltipColumnPickerHolder = $('#errorTooltipColumnPickerHolder');

      // Options for the Error CADC VOTV instance
      var errorVOTVOptions =
          {
            editable: false,
            enableAddRow: false,
            showHeaderRow: true,
            showTopPanel: true,
            enableCellNavigation: false,
            asyncEditorLoading: true,
            defaultColumnWidth: 100,
            explicitInitialization: false,
            enableAsyncPostRender: true,
            fullWidthRows: true,
            pager: false,
            headerRowHeight: 50,
            multiSelect: false,
            leaveSpaceForNewRows: false,
            sortColumn: 'LineNumber',  // ID of the sort column.
            sortDir: 'asc',
            topPanelHeight: 5,
            enableTextSelectionOnCells: true,
            gridResizable: true,
            rerenderOnResize: false,
            enableSelection: false,
            targetNodeSelector: '#errorTable',    // Shouldn't really be an option
                                                  // as it's mandatory!
            columnManager: {
              filterable: true,
              forceFitColumns: false,
              //          forceFitColumnMode: 'max',
              resizable: true,
              picker: {
                style: 'tooltip',
                panel: $('div#error-grid-header'),
                options: {
                  buttonText: ((getPageLanguage() === 'fr') ?
                               'Gérer l\'affichage des colonnes' :
                               'Change Columns')
                },
                tooltipOptions: {
                  targetSelector: $errorTooltipColumnPickerHolder.find('.tooltip_content').first(),
                  appendTooltipContent: true,
                  tooltipContent: $errorTooltipColumnPickerHolder.find('.tooltip').first(),
                  position: 'center right',
                  // The horizontal spacing is 0 so that when hovering from the
                  // input field to the tooltip, the parent div is not left (and
                  // the tooltip stays open
                  offset: [150, 0],
                  relative: true,
                  delay: 50,
                  effect: 'toggle',
                  events: {
                    def: ',',
                    widget: 'click,mouseleave'
                  }
                }
              }
            },
            columnOptions: {
              'TargetError': {
                width: 400
              },
              'LineNumber': {
                width: 100
              },
              'Target': {
                width: 100
              },
              'RA': {
                width: 100
              },
              'DEC': {
                width: 100
              },
              'radius': {
                width: 80
              }
            }  // Done by column ID.
          };

      var errorVOTV = new cadc.vot.Viewer('#errorTable', errorVOTVOptions);

      try
      {
        errorVOTV.build({
                          url: error_url
                        },
                        function ()
                        {
                          errorVOTV.render();

                          $('#errorTable').find('.grid-header-label')
                              .text(getPageLanguage() === 'fr' ? 'Erreur.' : 'Error');

                          // Necessary at the end!
                          errorVOTV.refreshGrid();

                          queryOverlay.popup('close');
                        },
                        function (jqXHR, status, message)
                        {
                          console.error('Error status: ' + status);
                          console.error('Error message: ' + message);
                          console.error('Error from response: ' + jqXHR.responseText);
                        });

        $('#errorTableTab-link').click();
      }
      catch (e)
      {
        console.error('Found error! > ' + e);
        queryOverlay.popup('close');
      }
    }

    function processFilterValue(filterValue, args, callback)
    {
      var columnID = args.column.id;
      var unit = args.unitValue;
      var $col = $(args.column);
      var converter = columnManager.getConverter(columnID, filterValue, unit);
      var previousUnit = $col.data('previousUnitValue');
      var pureFilterValue = $col.data('pureFilterValue') || filterValue;
      var breakdownPureFilterValue = '';
      var breakdownDisplayFilterValue = '';

      if (pureFilterValue && converter && converter.convertValue
          && converter.rebase)
      {
        var filterBreakdown = columnManager.getFilterPattern(pureFilterValue);

        for (var i = 0; i < filterBreakdown.length; i++)
        {
          var next = filterBreakdown[i];

          // Assume non-numbers are syntax.
          if (columnManager.isFilterSyntax(next))
          {
            breakdownPureFilterValue += next;
            breakdownDisplayFilterValue += next;
          }
          // Convert the numbers
          else
          {
            var rebaseConverter = columnManager.getConverter(columnID, next,
                                                             unit);
            var rebasedVal = rebaseConverter.rebase(previousUnit);
            converter = columnManager.getConverter(columnID, rebasedVal, unit);
            breakdownPureFilterValue += converter.convertValue();
            breakdownDisplayFilterValue += converter.convert();
          }
        }
      }

      callback(breakdownPureFilterValue, breakdownDisplayFilterValue);
    }

    function processResults(json, startDate, searchCompleteCallback)
    {
      netEnd = (new Date()).getTime();

      // Next story should handle this better.
      if (json.errorMessage)
      {
        searchError(json.errorMessage);
      }
      else
      {
        var jobHost = (new cadc.web.util.URI(json.job_url)).getHost();
        var localHost = (new cadc.web.util.URI(window.location.href)).getHost();
        if (jobHost !== localHost)
        {
          console.error("cross domain error - local host: " + localHost + ", requested job host: " + jobHost);
          searchError(ca.nrc.cadc.search.i18n[getPageLanguage()]["CROSS_DOMAIN_ERROR"]);
        }
        else
        {
          var runID = json.run_id;

          var buildPanelMessage = function (queryTimeStart, queryTimeEnd, loadTimeStart, loadTimeEnd)
          {
            var isFR = getPageLanguage() === "fr";
            var totalQueryTime = ((queryTimeEnd - queryTimeStart) / 1000.0);
            var totalLoadTime = ((loadTimeEnd - loadTimeStart) / 1000.0);
            var secondsString = isFR ? " secondes" : " seconds";
            return (isFR ? "Recherche et transfert: "
                    : "Query and transfer: ") + totalQueryTime
                   + secondsString + " - " + (isFR ? "Lecture et affichage: " : "Load and render: ")
                   + totalLoadTime + secondsString;
          };

          loadUWSJob(json.job_url, function (event, args)
          {
            sessionStorage.setItem("uws_job", JSON.stringify(args.job));

            loadStart = (new Date()).getTime();

          }, function (event, args)
                     {
                       console.error("Status error when loading job: "
                                     + args.errorStatusCode);
                     });

          if (json.display_units)
          {
            $(document).data("displayUnits", json.display_units);
          }

          downloadForm.find("input[name='fragment']").val("RUNID=" + runID);

          // Clean and prepare the download form.
          downloadForm.find("input[name='uri']").remove();
          downloadForm.find("input[name='cutout']").remove();

          if (json.cutout)
          {
            var input = $('<input>');

            input.prop("type", "hidden");
            input.prop("name", "cutout");
            input.val(json.cutout);

            downloadForm.append(input);
          }

          resultsVOTV.clearColumnFilters();

          resultsVOTV.build({
                              url: json.results_url,
                              // useRelativeURL: true,
                              type: $("input[name='format']").val(),
                              tableMetadata: getActiveForm().getResultsTableMetadata(),
                              pageSize: ca.nrc.cadc.search.RESULTS_PAGE_SIZE
                            },
                            function ()
                            {
                              if (searchCompleteCallback)
                              {
                                searchCompleteCallback();
                              }

                              loadEnd = (new Date()).getTime();

                              resultsVOTV.render();

                              postQuerySubmission({upload_url: json.upload_url});

                              var message = buildPanelMessage(startDate, netEnd,
                                                              loadStart, loadEnd);

                              $("#results-grid-footer").find(".grid-footer-label").text(message);

                              // Necessary at the end!
                              resultsVOTV.refreshGrid();
                            },
                            function (jqXHR, status, message)
                            {
                              console.error("Error status: " + status);
                              console.error("Error message: " + message);
                              console.error("Error from response: " + jqXHR.responseText);
                              getActiveForm().cancel();
                              alert(message);
                            });
        }
      }
    }

    // End processResults()

    function searchError(message)
    {
      getActiveForm().cancel();
      alert(message);
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param _event       The Event to fire.
     * @param _args        Arguments to the event.
     * @returns {*}       The event notification result.
     */
    this._trigger = function (_event, _args)
    {
      var args = _args || {};
      args.application = this;

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

    if (_autoInitFlag)
    {
      init();
    }

    $.extend(this,
             {
               'start': start,
               'getADQL': getADQL,
               'getActiveForm': getActiveForm,
               'getDownloadForm': getDownloadForm,
               'deserializeFormData': deserializeFormData,
               'selectTab': selectTab,
               'getActiveTabID': getActiveTabID,

               // Exposed for testing
               'processFilterValue': processFilterValue,
               'sanitizeColumnOptions': sanitizeColumnOptions
             });
  }
})(jQuery);
