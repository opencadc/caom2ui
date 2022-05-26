;
(function ($, window) {
  // register namespace
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            defaults: {
              pageLanguage: 'en',
              applicationEndpoint: '/search',
              autoInitFlag: true,
              enableOneClickDownload: true,
              targetResolverEndpoint: '/search/unitconversion',
              packageEndpoint: '/search/package',
              autocompleteEndpoint: '/search/unitconversion',
              validatorEndpoint: '/search/validate',
              previewsEndpoint: '/search/preview',
              searchEndpoint: '/search/find',
              showObscoreTab: true
            },
            services: {},
            field_ignore: [
              'sort_column',
              'sort_order',
              'formName',
              'SelectList',
              'MaxRecords',
              'format',
              'Form.name'
            ],
            i18n: {
              en: {
                ONE_CLICK_DOWNLOAD_TIP: 'Single file or .tar if multiple files',
                ROW_COUNT_MESSAGE: 'Showing {1} rows ({2} before filtering).',
                CROSS_DOMAIN_ERROR: 'Server error retrieving data'
              },
              fr: {
                ONE_CLICK_DOWNLOAD_TIP: 'Seul fichier ou .tar si plusieurs',
                ROW_COUNT_MESSAGE: "Affichage de {1} résultats ({2} avant l'application du filtre).",
                CROSS_DOMAIN_ERROR: 'French version of Server error retrieving data'
              }
            },
            QUICKSEARCH_SELECTOR: '.quicksearch_link',
            GRID_SELECTOR: '#resultTable',
            RESULTS_PAGE_SIZE: 500,
            AdvancedSearchApp: AdvancedSearchApp,
            events: {
              onAdvancedSearchInit: new jQuery.Event('onAdvancedSearchInit'),
              onAdvancedSearchInitFail: new jQuery.Event('onAdvancedSearchInitFail'),
              onSetBookmarkUrl: new jQuery.Event('onSetBookmarkUrl')
            },
            downloadTypes: ['votable', 'csv', 'tsv']
          }
        }
      }
    }
  })

  /**
   * The main AdvancedSearch application.
   *
   * @param {Object} _options   Options to this Application.
   * @param {String} [_options.pageLanguage="en"]   The language from the page.
   * @param {Boolean} [_options.autoInitFlag=true]   Whether to auto-initialize this application.
   * @param {String} [_options.packageEndpoint="/search/package"]   Relative URI endpoint to the CAOM2 package service.
   * @param {String} [_options.previewsEndpoint="/search/preview"]   Relative URI endpoint to the Preview service.
   * @param {String} [_options.validatorEndpoint="/search/validate"]   Relative URI endpoint to the Validator service.
   * @param {String} [_options.autocompleteEndpoint="/search/unitconversion"]   Relative URI endpoint to the unit
   * @param {String} [_options.targetResolverEndpoint="/search/unitconversion"]   Target resolver endpoint service
   *     conversion service.
   * @param {String} [_options.applicationEndpoint="/search"]   Endpoint to this application
   * @constructor
   */
  function AdvancedSearchApp(_options) {
    // Stat fields to show on result table.
    var netEnd, loadStart, loadEnd
    var _searchApp = this

    /**
     * @property
     * @type {StringUtil}
     */
    var stringUtil = new org.opencadc.StringUtil()
    var downloadFormSubmit = $('#downloadFormSubmit')
    var downloadForm = $('#downloadForm')
    var queryOverlay = $('#queryOverlay')
    var queryTab = $('#queryTab')
    var $tabContainer = $('#tabContainer')
    var isFirstLoad = true

    // Text area containing the ADQL query.
    var $queryCode = $('#query')
    var columnManager = new ca.nrc.cadc.search.columns.ColumnManager()
    var resultsVOTV
    var previousCollections = []
    var tooltipJsonData = {}

    var services = {
      autocompleteEndpoint: _options.autocompleteEndpoint,
      targetResolverEndpoint: _options.targetResolverEndpoint,
      packageEndpoint: _options.packageEndpoint,
      validatorEndpoint: _options.validatorEndpoint,
      previewsEndpoint: _options.previewsEndpoint,
      searchEndpoint: _options.searchEndpoint,
      applicationEndpoint: _options.applicationEndpoint,
    }

    $.extend(true, ca.nrc.cadc.search.services, services)

    this.options = $.extend({}, ca.nrc.cadc.search.defaults, _options)

    /**
     * @type {ca.nrc.cadc.search.SearchForm|SearchForm}
     */
    this.caomSearchForm = null

    /**
     * @type {ca.nrc.cadc.search.SearchForm|SearchForm}
     */
    this.obsCoreSearchForm = null

    // The active Form's ID being used to submit the last query.
    this.activeFormID = 'queryForm'

    // ------ start SearchTapClient setup & related functions ------

    var _searchTapClient = new ca.nrc.cadc.search.tapclient.SearchTapClient(_options)
    // Set _searchTapClient as part of the option set that
    this.options.tapClient = _searchTapClient

    this._detachTapClientListeners = function() {
      _searchTapClient.unsubscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientOK, this.loadVOTable)
      _searchTapClient.unsubscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientFail, this.reportError)
    }

    this._attachTapClientListeners = function() {
      _searchTapClient.subscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientOK, this.loadVOTable)
      _searchTapClient.subscribe(ca.nrc.cadc.search.tapclient.events.onTAPClientFail, this.reportError)
    }

    // ------ end SearchTapClient setup & related functions ------

    /**
     * @return {String}
     */
    this.getPageLanguage = function () {
      return this.options.pageLanguage
    }

    /**
     *
     * @return {ca.nrc.cadc.search.SearchForm|SearchForm}
     */
    this.getCAOMSearchForm = function () {
      return this.caomSearchForm
    }

    /**
     *
     * @return {ca.nrc.cadc.search.SearchForm|SearchForm}
     */
    this.getObsCoreSearchForm = function () {
      return this.obsCoreSearchForm
    }

    /**
     * Set the new form for CAOM-2.
     *
     * @param {ca.nrc.cadc.search.SearchForm|SearchForm} form  New CAOM2 form instance.
     * @private
     */
    this._setCAOMSearchForm = function (form) {
      this.caomSearchForm = form
    }

    /**
     * Set the new form for ObsCore.
     *
     * @param {ca.nrc.cadc.search.SearchForm|SearchForm} form  New ObsCore form instance.
     * @private
     */
    this._setObsCoreSearchForm = function (form) {
      this.obsCoreSearchForm = form
    }

    /**
     * Obtain the currently active tab's ID.
     *
     * Check the sessionStorage for the activePanel component, then the
     * currently listed active tab (i.e. with class 'active').
     *
     * @return  {String}    The ID of the active tab.
     * @private
     */
    this._getActiveTabID = function () {
      var $tabList = $('ul#tabList')
      var activeTab = $tabList.find('li.active')
      var defaultTab = $tabList.find('li.default')
      var langURLPath = $(
        "span[lang='" + this.getPageLanguage() + "'].lang-link-target"
      ).text()
      var cachedTabID = sessionStorage.getItem(
        'activePanel-' + langURLPath + '0'
      )
      var targetTabID

      if (cachedTabID) {
        targetTabID = cachedTabID
      } else if (activeTab && activeTab.find('a:first').length) {
        targetTabID = activeTab.find('a:first').attr('href')
      } else {
        targetTabID = defaultTab.find('a:first').attr('href')
      }

      return targetTabID
    }

    /**
     * Obtain the currently active form object.
     *
     * return {ca.nrc.cadc.search.SearchForm|SearchForm}    Form instance.
     * @private
     */
    this._getActiveForm = function () {
      return this.getCAOMSearchForm().isActive(this.activeFormID) ?
        this.getCAOMSearchForm() :
        this.getObsCoreSearchForm()
    }

    /**
     * Obtain the currently set maximum record return count.
     * @returns {Number}
     */
    this.getMaxRecordCount = function () {
      return this._getActiveForm()
        .getForm()
        .find('input[name="MaxRecords"]')
        .val()
    }

    /**
     * Pretty print the ADQL in the text area.
     *
     * @param {String}  adqlText    The ADQL to set.
     * @returns {String}
     * @private
     */
    this._adqlPrint = function (adqlText) {
      return adqlText
        .replace(/(FROM|WHERE|AND)/g, function (match) {
          return '\n' + match
        })
        .replace(/JOIN/g, function (match) {
          return '\n\t' + match
        })
        .replace(/,/g, function (match) {
          return match + '\n\t'
        })
    }

    /**
     * Post-query load of the ADQL results.
     *
     * @param {String} jobURL   The Job URL to load from.  Warning: Could be CORS.
     * @param {function} successCallback   Call back to call on successful job load.
     * @param {function} failCallback      Call back to call on unsuccessful job load.
     * @private
     */
    this._loadUWSJob = function (jobURL, successCallback, failCallback) {
      if (jobURL) {
        var jobLoader = new ca.nrc.cadc.search.uws.UWSJobLoader(jobURL)

        jobLoader.subscribe(
          ca.nrc.cadc.search.uws.events.onJobLoaded,
          successCallback
        )
        jobLoader.subscribe(
          ca.nrc.cadc.search.uws.events.onJobLoadFailed,
          failCallback
        )

        jobLoader.load()
      } else {
        console.error('Unable to obtain Job ADQL.')
      }
    }

    /**
     * Set the ADQL data from the current job, if any.
     *
     * @param {boolean} _includeExtendedColumns   Whether to include the 'invisible' set of columns in the SELECT
     *     clause.
     * @returns {String} text of ADQL.
     * @private
     */
    this._getADQL = function (_includeExtendedColumns) {
      var jobString = sessionStorage.getItem('uws_job')
      var adqlText

      if (jobString) {
        var jobJSON = JSON.parse(jobString)
        var uwsJobParser = new ca.nrc.cadc.search.uws.json.UWSJobParser(jobJSON)
        adqlText = uwsJobParser.getJob().getParameterValue('QUERY')
        var selectListString = this._getActiveForm().getSelectListString(_includeExtendedColumns)

        adqlText =
          'SELECT ' +
          selectListString +
          ' ' +
          adqlText.substring(adqlText.indexOf('FROM'))
      } else {
        adqlText = ''
      }

      return adqlText
    }

    this._getTargetUpload = function () {
      var jobString = sessionStorage.getItem('uws_job')
      var upload
      if (jobString) {
        var jobJSON = JSON.parse(jobString)
        var uwsJobParser = new ca.nrc.cadc.search.uws.json.UWSJobParser(jobJSON)
        upload = uwsJobParser.getJob().getParameterValue('UPLOAD')
      }
      return upload
    }

    this._displayError = function (error) {
      var errorMessage = 'Metadata field failed to initialize: ' + error
      console.error(errorMessage)
      _searchApp._trigger(ca.nrc.cadc.search.events.onAdvancedSearchInitFail, {
        error: errorMessage
      })
    }

    this.reportError = function (event, args) {
      _searchApp._detachTapClientListeners()
      _searchApp._displayError(args.responseText)
    }

    /**
     * Perform CAOM-2 and Obscore form configuration and initialization.
     * Called after initial TAP schema call succeeds. Second step in making search
     * forms available for use.
     *
     * @param event
     * @param args
     */
    this.loadVOTable = function (event, args) {
      var data = args.data
      _searchApp._detachTapClientListeners()

      var caomFormConfig = new ca.nrc.cadc.search.FormConfiguration(
          new ca.nrc.cadc.search.CAOM2.FormConfiguration(),
          _searchApp.options
      )
      var obsCoreFormConfig = new ca.nrc.cadc.search.FormConfiguration(
          new ca.nrc.cadc.search.ObsCore.FormConfiguration(),
          _searchApp.options
      )

      new cadc.vot.Builder(
          1000, {
            xmlDOM: data
          },
          function (voTableBuilder) {
            try {
              voTableBuilder.build(voTableBuilder.buildRowData)

              var voTable = voTableBuilder.getVOTable()
              var resources = voTable.getResources()
              var tables = resources[0].getTables()
              var tableData = tables[0].getTableData()
              var rows = tableData.getRows()

              for (var ri = 0, rl = rows.length; ri < rl; ri++) {
                var nextRow = rows[ri]
                var cells = nextRow.getCells()

                var tableName

                for (var ci = 0, cl = cells.length; ci < cl; ci++) {
                  var nextCell = cells[ci]
                  var nextFieldName = nextCell.getField().getName()

                  if (nextFieldName === 'table_name') {
                    tableName = nextCell.getValue()
                    break
                  }
                }

                if (tableName.indexOf('caom2') >= 0) {
                  caomFormConfig.addField(nextRow)
                } else if (tableName.indexOf('ivoa') >= 0) {
                  obsCoreFormConfig.addField(nextRow)
                }
              }

              // Add utypes for extra form fields (not included in
              // standard list returned from initial TAP schema call)
              // Note: can be generalized to support obsCoreFormConfig in future.
              caomFormConfig.addExtraUtypeFields()
              _searchApp._cleanMetadata(caomFormConfig)
              _searchApp._cleanMetadata(obsCoreFormConfig)

              _searchApp._initializeForms(caomFormConfig, obsCoreFormConfig)
              _searchApp._trigger(ca.nrc.cadc.search.events.onAdvancedSearchInit, {})

            } catch (e) {
              this._displayError(e)
            }
          }
        )

    }

    /**
     * Gather search form configuration information.
     * First step in making search form available for use.
     *
     * @private
     */
    this._initFormConfigurations = function () {
      var tapQuery =
        'select * from TAP_SCHEMA.columns where ' +
        "((table_name='caom2.Observation' or " +
        "table_name='caom2.Plane') and utype like 'caom2:%') or " +
        "(table_name='ivoa.ObsCore' and utype like 'obscore:%')"

      // Start by gathering the TAP schema information
      // using SearchTapClient instance. OK and Fail events issued by the class
      // are responded to in functions named in _attachTapClientListeners().
      // Listeners are removed after inital page load so response to these events
      // only occurs at startup.
      _searchApp._attachTapClientListeners()
      _searchTapClient.postTAPRequest(tapQuery, 'votable')
    }

    /**
     * Initialize all things pertinent to the application.
     */
    this.init = function () {
      // Internet Explorer compatibility.
      //
      // WebRT 48318
      // jenkinsd 2014.02.13
      //
      wgxpath.install()
      this._initFormConfigurations()

      /*
       * Story 1644
       * On tab click, update the window hash.  When the window hash is updated,
       * the $.address.change() will be activated.
       *
       * This should be automatic from the easy tabs library, but I have a
       * feeling the WET 3.1 library is getting in the way.
       *
       * TODO - Re-evaluate when Bootstrap is implemented!
       *
       * jenkinsd 11.10.2014
       *
       * TODO: Amended to expect Bootstrap.
       * jenkinsd 05.03.2017
       *
       */
      $('#tabList > li').click(
        function (e) {
          var tabID = e.target.hash
          window.location.hash = tabID

          if (tabID.toLowerCase().indexOf('obscore') > 0) {
            this.activeFormID = this.getObsCoreSearchForm().getID()
          } else if (tabID.toLowerCase().indexOf('queryform') > 0) {
            this.activeFormID = this.getCAOMSearchForm().getID()
          }
        }.bind(this)
      )

      this._initBackButtonHandling()
    }

    /**
     * Ensure back button navigates through tabs user (or app) has clicked through.
     * @private
     */

    this._initBackButtonHandling = function () {
      // Add a hash of previous to the URL when a new tab is shown
      $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        // Avoid duplication of history elements
        $currentTarget = $(e.target)
        $relatedTarget = $(e.relatedTarget)

        if (
          $currentTarget.attr('href') !== window.location.hash &&
          $currentTarget.attr('href') !== $relatedTarget.attr('href')
        ) {
          history.pushState(null, null, $currentTarget.attr('href'))
        }
      })

      //// Navigate to a tab when the history changes (back button is pressed)
      // TODO: leaving this in because the back button doesn't work to navigate through the tabs
      // without something like this, but when 'Preview' is clicked, something about loading the
      // new tab clears the location.hash.length, so coming into this piece of code means the tab
      // switches to the first tab. :(
      //var thisWindow = window;
      //thisWindow.addEventListener("popstate", function(e)
      //                                    {
      //                                      if (location.hash.length != 0)
      //                                      {
      //                                        $('[href="' + location.hash + '"]').tab("show");
      //                                      }
      //                                      else
      //                                      {
      //                                        $(".nav-tabs a:first").tab("show");
      //
      //                                      }
      //                                    }
      //);
    }

    /**
     * Remove empty or non-existent fields from the metadata.
     *
     * @param {ca.nrc.cadc.search.FormConfiguration|FormConfiguration} _formConfig   The form configuration to modify.
     *
     * @private
     */
    this._cleanMetadata = function (_formConfig) {
      var tableMetaData = _formConfig.getTableMetadata()
      var metadataFields = tableMetaData.getFields()
      var cleanFields = []

      for (var i = 0, mfl = metadataFields.length; i < mfl; i++) {
        var f = metadataFields[i]
        if (f) {
          cleanFields.push(f)
        }
      }

      tableMetaData.setFields(cleanFields)
    }

    /**
     * Deserialize the form data in the search results.
     *
     * This is only made public to allow testing.
     *
     * @param {String}  formData    The String serialized form data.
     * @returns {{}}
     */
    this.deserializeFormData = function (formData) {
      // RegEx for '+' character.
      var plus = /\+/g
      var map = {}

      if (formData && formData.length > 0) {
        var kvPairs = formData.split('&')
        for (var i = 0; i < kvPairs.length; i++) {
          var kvPair = kvPairs[i].split('=')
          var key = decodeURIComponent(kvPair[0]).replace(plus, ' ')
          var value = decodeURIComponent(kvPair[1]).replace(plus, ' ')

          // if an entry already exists for the key, add value to existing
          // value(s)
          if (map[key] && map[key].length > 0) {
            // already has a value, add new value
            map[key].push(value)
          } else {
            var arr = []
            arr[0] = value
            map[key] = arr
          }
        }
      }

      return map
    }

    /**
     * Repopulates the form using the form data passed in.
     *
     * @param {{}}  formDataMap   The map (hash) object containing form information.
     * @private
     */
    this._repopulateForm = function (formDataMap) {
      var mCollections = $('#Observation\\.collection').val()
      for (var k in formDataMap) {
        if (formDataMap.hasOwnProperty(k)) {
          var values = formDataMap[k]

          if (values) {
            // get current element
            var currentEl = $('[id="' + k + '"]')

            if (currentEl && currentEl.length > 0) {
              // we have the specified element
              if (currentEl.prop('tagName').toLowerCase() === 'input') {
                if (currentEl.prop('type').toLowerCase() === 'text') {
                  if (values[0].length > 0) {
                    // repopulate text input
                    this._getActiveForm().setInputValue(
                      currentEl.prop('id'),
                      decodeURIComponent(values[0])
                    )
                  }
                } else if (
                  currentEl.prop('type').toLowerCase() === 'checkbox'
                ) {
                  // repopulate a checkbox
                  currentEl.prop('checked', values[0] === 'on')
                }
              } else if (currentEl.prop('tagName').toLowerCase() === 'select') {
                // De-select all of the options, first.
                currentEl.find('option').prop('selected', false)
                var sourceValues = values

                // repopulate either a dropdown list or a hierarchy select
                if (
                  k === 'Observation.collection' &&
                  mCollections !== null &&
                  mCollections.length > 0
                ) {
                  sourceValues = mCollections
                }

                for (var i = 0; i < sourceValues.length; i++) {
                  currentEl
                    .find("option[value='" + sourceValues[i] + "']")
                    .prop('selected', true)
                }
              }
            }
          }
        }
      }
    }

    /**
     * If the table viewer isn't displayed, check for cached form data, and re-post the query displaying the search
     * results.
     *
     * @private
     */
    this._updateResults = function () {
      if (!$('div.slick-viewport').length) {
        var formData = sessionStorage.getItem('form_data')
        if (formData) {
          // Deserialize and repopulate form.
          var formDataMap = this.deserializeFormData(formData)
          this._repopulateForm(formDataMap)
          this._getActiveForm().submit()
        }
      }
    }

    /**
     * Update the existing tab content.
     *
     * @param {String} tabID   The ID of the current tab.
     *
     * @private
     */
    this._updateCurrentTabContent = function (tabID) {
      if (tabID === 'resultTableTab') {
        this._updateResults()
      } else if (tabID === 'queryTab') {
        $queryCode.text(this._adqlPrint(this._getADQL(false)))
      }
    }

    /**
     * On address change, select that tab.
     *
     * @param {String} tabID   The TabID from the hash to select (move to).
     * @private
     */
    this._selectTab = function (tabID) {
      // If it's been initialized.
      if ($tabContainer.easytabs) {
        $tabContainer.easytabs('select', '#' + tabID)
      }

      // Scroll to top.
      window.scrollTo(0, 0)

      this._updateCurrentTabContent(tabID)

      // Update the language selector link.
      var $languageLink = $('a.lang-link')

      if ($languageLink.length > 0) {
        var currLink = $languageLink.attr('href')

        if (currLink) {
          if (currLink.indexOf('#') >= 0) {
            $languageLink.attr(
              'href',
              currLink.slice(0, currLink.indexOf('#')) + '#' + tabID
            )
          } else {
            $languageLink.attr('href', currLink + '#' + tabID)
          }
        }
      }
    }

    /**
     *
     * @returns {string}
     */
    this._getFormQueryString = function () {
      var parameters = []
      var $activeFormObject = this._getActiveForm().getForm()
      var fields = $activeFormObject.serializeArray()
      $.each(fields, function (index, field) {
        if (
          field.value &&
          ca.nrc.cadc.search.field_ignore.indexOf(field.name) < 0
        ) {
          parameters.push(
            $activeFormObject.find("[name='" + field.name + "']").attr('id') +
            '=' +
            encodeURIComponent(field.value.replace(/\%/g, '*'))
          )
        }
      })
      return parameters.length > 0 ? '?' + parameters.join('&') : ''
    }

    /**
     * Initialize the form instances.
     * Third step in making search forms available for use.
     *
     * @param {ca.nrc.cadc.search.CAOM2.FormConfiguration}  caomConfiguration CAOM2 form configuration.
     * @param {ca.nrc.cadc.search.ObsCore.FormConfiguration}  obsCoreConfiguration  ObsCore form configuration.
     * @private
     */
    this._initializeForms = function (caomConfiguration, obsCoreConfiguration) {
      // Start setting up caomSearchForm
      // obsCoreSearchForm is set up nearer the end of this function

      try {
        // Grab current endpoint from registry client so configuration of votable
        // can be completed
        caomConfiguration.options.tapSyncEndpoint = _searchTapClient.getLastEndpoint()
        var caomSearchForm = new ca.nrc.cadc.search.SearchForm(
            'queryForm',
            false,
            caomConfiguration
        )

        // Disable the forms to begin with.
        caomSearchForm.disable()

        this._setCAOMSearchForm(caomSearchForm)

        jQuery.fn.exists = function () {
          return this.length > 0
        }

        // Used to send arrays of values as a parameter to a GET request.
        jQuery.ajaxSettings.traditional = true

        // Trap the backspace key to prevent it going 'Back' when not using it to
        // delete characters.                                      tabContainer
        // Story 959 - Task 2920.
        // jenkinsd 2012.05.24
        //
        $('html').keydown(function (event) {
          if (event.keyCode === 8) {
            var currentFocus = $('*:focus')

            if (!currentFocus.is('input') && !currentFocus.is('textarea')) {
              event.preventDefault()
            }
          }
        })

        var onFormCancel = function () {
          console.warn('Cancelling search.')
          queryOverlay.modal('hide')
        }

        caomSearchForm.subscribe(ca.nrc.cadc.search.events.onCancel, onFormCancel)

        var onFormSubmitComplete = function (eventData, args) {
          if (args.success) {
            this._processResults(args.data, args.startDate, function () {
              queryOverlay.modal('hide')
              $('#resultTableTabLink').tab('show')
            })
            this._setBookmarkURL(new cadc.web.util.currentURI())
          } else {
            this._processErrorResults(args.error_url)
          }
        }.bind(this)

        caomSearchForm.subscribe(
            ca.nrc.cadc.search.events.onSubmitComplete,
            onFormSubmitComplete
        )

        /**
         * Form validation succeeded.
         */
        var onFormValid = function (eventData, args) {
          var preserveColumnState = false
          var prevColumns = []
          var prevDisplayedColumns = []
          var prevColumnSelects = {}
          var prevSortOptions = {}
          var selectedCollections = this._getActiveForm().getCollectionSelectID()
          var currentCollections = $(
              '#' + selectedCollections.replace('.', '\\.')).val()

          var cadcForm = args.cadcForm

          if (resultsVOTV) {
            // the results column set is retained if the collection used
            // in the search is the same AND the form itself says it
            // should be prserved
            if ( (currentCollections.sort().join('') ===
                previousCollections.sort().join('') )
              && (cadcForm.preserveColumnSet() === true) ) {

                // Save viewer state from previous search
                preserveColumnState = true
                prevColumns = resultsVOTV.getColumns()

                prevDisplayedColumns = resultsVOTV.getDisplayedColumns()
                prevColumnSelects = resultsVOTV.getUpdatedColumnSelects()
                prevSortOptions['sortcol'] = resultsVOTV.sortcol
                prevSortOptions['sortAsc'] = resultsVOTV.sortAsc

            }

            resultsVOTV.destroy()
          }
          previousCollections = currentCollections

          // Searching on different data.  Switch the columns.
          if (!this.activeFormID || !cadcForm.isActive(this.activeFormID)) {
            // This is now the active form.
            this.activeFormID = cadcForm.getID()
          }

          var formatCheckbox = function ($rowItem) {
            if (
                !stringUtil.hasText(
                    $rowItem[this._getActiveForm().getDownloadAccessKey()]
                )
            ) {
              var $checkboxSelect = $('input:checkbox._select_' + $rowItem.id)
              var $parentContainer = $checkboxSelect.parent('div')

              $parentContainer.empty()
              $('<span class="_select_' + $rowItem.id + '">N/A</span>').appendTo(
                  $parentContainer
              )
            }
          }.bind(this)

          // To be used when the grid.onRenderedRows event is
          // fired.
          var onRowRendered = function ($rowItem, rowIndex) {
            if ($rowItem) {
              formatCheckbox($rowItem, rowIndex)
            }
          }

          var isRowDisabled = function (row) {
            var downloadableColumnName = this._getActiveForm().getDownloadAccessKey()
            var downloadableColumnValue = row.getCellValue(downloadableColumnName)

            return downloadableColumnValue === null
          }.bind(this)

          var rowCountMessage = function (totalRows, rowCount) {
            return stringUtil.format(
                ca.nrc.cadc.search.i18n[this.getPageLanguage()][
                    'ROW_COUNT_MESSAGE'
                    ],
                [totalRows, rowCount]
            )
          }.bind(this)

          var oneClickDownloadTitle = function () {
            return ca.nrc.cadc.search.i18n[this.getPageLanguage()][
                'ONE_CLICK_DOWNLOAD_TIP'
                ]
          }.bind(this)

          var activeForm = this._getActiveForm()
          var heightOffset = $('header').height() + $('#tabList').height() + 110

          // Options for the CADC VOTV instance
          var cadcVOTVOptions = {
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
            headerRowHeight: 52,
            variableViewportHeight: true,
            heightOffset: heightOffset,
            multiSelect: true,
            propagateEvents: true,
            leaveSpaceForNewRows: false,
            // ID of the sort column (Start Date).
            sortColumn: activeForm.getConfiguration().getDefaultSortColumnID(),
            sortDir: 'desc',
            topPanelHeight: 25,
            enableTextSelectionOnCells: true,
            gridResizable: false,
            rerenderOnResize: false,
            emptyResultsMessageSelector: '#cadcvotv-empty-results-message',
            frozenColumn: 0,
            frozenBottom: false,
            enableSelection: true,
            suggest_maxRowCount: 7,
            targetNodeSelector: '#resultTable', // Shouldn't really be an
            // option as it's mandatory!
            columnFilterPluginName: 'suggest',
            enableOneClickDownload: this.options.enableOneClickDownload,
            oneClickDownloadTitle: oneClickDownloadTitle(),
            oneClickDownloadURL: this.options.packageEndpoint,
            oneClickDownloadURLColumnID: activeForm
                .getConfiguration()
                .getDownloadAccessKey(),
            oneClickInvisibleDefault: true,
            headerCheckboxLabel: 'Mark',
            headerCheckboxWidth: 70,
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
                style: 'dialog',
                options: {
                  showAllButtonText: $(
                      '#COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT'
                  ).text(),
                  resetButtonText: $(
                      '#COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT'
                  ).text(),
                  orderAlphaButtonText: $(
                      '#COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT'
                  ).text(),
                  dialogTriggerID: 'change_column_button',
                  targetSelector: $('#column_manager_container')
                      .find('.column_manager_columns')
                      .first(),
                  position: {
                    my: 'right',
                    at: 'right bottom'
                  },
                  closeDialogSelector: '.dialog-close',
                  refreshPositions: true
                }
              }
            },
            maxRowLimit: this.getMaxRecordCount(),
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
                toggleSwitchSelector: '#slick-visualize',
                footprintFieldID: activeForm
                    .getConfiguration()
                    .getFootprintColumnID(),
                fovFieldID: activeForm.getConfiguration().getFOVColumnID(),
                raFieldID: activeForm.getConfiguration().getRAColumnID(),
                decFieldID: activeForm.getConfiguration().getDecColumnID()
              }
            },
            columnOptions: columnManager.getColumnOptions()
          }

          resultsVOTV = new cadc.vot.Viewer(
              ca.nrc.cadc.search.GRID_SELECTOR,
              cadcVOTVOptions
          )

          // Unfortunately this has to be selected at the Document level since the items in question (located by
          // ca.nrc.cadc.search.QUICKSEARCH_SELECTOR) aren't actually created yet.
          // jenkinsd 2015.05.08
          //
          $(document).on(
              'click',
              ca.nrc.cadc.search.QUICKSEARCH_SELECTOR,
              function (event) {
                var hrefURI = new cadc.web.util.URI(event.target.href)
                var href = hrefURI.toString()

                // Strip off the fragment part of the
                // href url if necessary.
                var index = href.indexOf('#')
                if (index !== -1) {
                  href = href.substring(0, index)
                }

                var serializer = new cadc.vot.ResultStateSerializer(
                    href,
                    resultsVOTV.sortcol,
                    resultsVOTV.sortDir ? 'asc' : 'dsc',
                    resultsVOTV.getDisplayedColumns(),
                    resultsVOTV.getResizedColumns(),
                    resultsVOTV.getColumnFilters(),
                    resultsVOTV.getUpdatedColumnSelects()
                )

                var windowName = '_' + $(event.target).text()

                window.open(serializer.getResultStateUrl(), windowName, '')

                return false
              }
          )

          resultsVOTV.subscribe(
              cadc.vot.events.onUnitChanged,
              function (event, args) {
                var viewer = args.application
                var columnID = args.column.id
                var filterValue = viewer.getColumnFilters()[columnID]

                this.processFilterValue(filterValue, args, function (breakdownPureFilterValue,
                                                                     breakdownDisplayFilterValue) {
                  $(args.column).data('pureFilterValue', breakdownPureFilterValue)

                  viewer.setColumnFilter(columnID, breakdownDisplayFilterValue)
                  viewer.getColumnFilters()[columnID] = breakdownDisplayFilterValue
                })
              }.bind(this)
          )

          downloadFormSubmit.off().click(function (event) {
            event.preventDefault()

            var fromInputFile = this._getActiveForm().hasInputFile()
            var doSpatialCutout = this._getActiveForm().doSpatialCutout()
            var doSpectralCutout = this._getActiveForm().doSpectralCutout()
            var downloadTuples = []

            // clear hidden URI inputs from any prior searches first
            downloadForm.find("input[name='uri']").remove()

            // Collect & prepare data to be submitted
            if (resultsVOTV.getSelectedRows().length <= 0) {
              translated_message = downloadForm.find('span#NO_OBSERVATIONS_SELECTED_MESSAGE').text()
              alert(translated_message)
            } else {
              var selectedRows = resultsVOTV.getSelectedRows()
              for (
                var arrIndex = 0, srl = selectedRows.length; arrIndex < srl; arrIndex++
              ) {
                var $nextRow = resultsVOTV.getRow(selectedRows[arrIndex])

                // uri is used for download request
                var $nextPlaneURI = $nextRow['caom2:Plane.publisherID.downloadable']

                // check for request combination that will lead to tuple generation
                if ((fromInputFile === true) && (doSpatialCutout === true)) {

                  // set up tuples which will be sent to downloadManager
                  // downloadManager request will have multipart data, using
                  // a JSON blob to transmit tuples built below

                  // Need to build a spatial cutout DALI string for each selected row.
                  var $nextPlaneCutout = 'CIRCLE ' + $nextRow['caom2:Upload.ra'] + " " + $nextRow['caom2:Upload.dec']
                    + ' ' + $nextRow['caom2:Upload.radius']

                  // grab target name for label
                  var $nextPlaneTargetName = $nextRow['caom2:Upload.target']

                  // translate the special characters in this string to what will
                  // be used in the SODA calls in downloadManager. This is being done
                  // here because lines such as 'M101 30"' will either have to be
                  // escaped here and decoded in the server (which seems like extra unnecessary
                  // work,) - or the translation function can be done here.
                  // Rules from user story CADC-1245, subtask CADC 8244
                  // '/' added because SODA service (caom2ops) does not accept it.
                  // 1) ' -> arcmin
                  // 2) " -> arcsec
                  // 3) '+' -> 'p'
                  // 4) ':' and '/' -> '_'
                  // 5) all whitespaces replaced by underscores.

                  $nextPlaneTargetName = $nextPlaneTargetName.replace('\'', 'arcmin')
                  $nextPlaneTargetName = $nextPlaneTargetName.replace('"', 'arcsec')
                  $nextPlaneTargetName = $nextPlaneTargetName.replace('\\+', 'p')
                  $nextPlaneTargetName = $nextPlaneTargetName.replace('\\:|\\/', '_')
                  $nextPlaneTargetName = $nextPlaneTargetName.replace(' ' , '_')

                  var tuple = {
                    "tupleID" : $nextPlaneURI,
                    "shape" : $nextPlaneCutout,
                    "label" : $nextPlaneTargetName
                  }

                  downloadTuples.push(tuple)

                } else {
                  // hidden input used with resolver form
                  // downloadManager request will have URI=#&URI=# format
                  var $input = $('<input>')
                  $input.prop('type', 'hidden')
                  $input.prop('name', 'uri')
                  $input.prop('id', $nextPlaneURI)
                  $input.val($nextPlaneURI)

                  downloadForm.append($input)
                }
              }

              // Story 1566, when all 'Product Types'
              // checkboxes are checked, do not send any
              // both (2739) - although I wonder if the 'all checked' check
              //being done twice is necessary (after the submit, for example.)
              var allChecked =
                    downloadForm
                      .find('input.product_type_option_flag')
                      .not(':checked').length === 0
              if (allChecked) {
                // disable all 'Product Types' checkboxes
                $.each(
                  downloadForm.find('input.product_type_option_flag:checked'),
                  function () {
                    $(this).prop('disabled', true)
                  }
                )
              }

              // Now get down to submitting the data
              if ((fromInputFile === true) && (doSpatialCutout === true)) {
                // iterate through downloadTuples and make the badgerfish json
                var badgerfishTuples = new Array()
                for (i=0; i<downloadTuples.length; i++) {
                  var tupleJSON = {"tuple":
                      {
                        "tupleID":{"$": downloadTuples[i].tupleID },
                        "shape":{"$": downloadTuples[i].shape },
                        "label":{"$": downloadTuples[i].label }
                      }
                  }
                  badgerfishTuples.push(tupleJSON)
                }

                // create payload item
                var jsonTuples = {"tupleList": {"$": badgerfishTuples }}

                var multiPartData = new FormData()

                // Add runid if defined
                var runID = downloadForm.find("input[name='runid']").val()
                if (runID !=  null) {
                  multiPartData.append('runid', runID)
                }

                // Add spectral cutout if defined
                // Spatial cutouts are included in JSON tuple data
                if (doSpectralCutout === true) {
                  var specCutout = downloadForm.find("input[name='band']")
                  multiPartData.append('band', specCutout.val())
                }

                // 'Blob' type is requred to have the 'filename="blob" parameter added
                // to the multipart section, and have the Content-type header added
                // so that the web service can correctly parse JSON payload sent
                multiPartData.append('blob', new Blob([JSON.stringify(jsonTuples)], {
                  type: 'application/json; charset=utf-8'
                }))

                // content-type: false below is important to retain
                // so the web service will correctly parse the data as an
                // upload file. if it's set to 'multipart/form-data' (as one would expect,)
                // downloadManager's web service can't parse it and it generates a blank page.
                // CADC-1245: cutout story for target upload file
                $.ajax({
                  url: '/downloadManager/download',
                  type: 'POST',
                  processData: false,
                  enctype: 'multipart/form-data',
                  contentType: false,
                  data: multiPartData
                })
                .done(function (data) {
                  var win = window.open()
                  // Q: does this work for all browsers??
                  win.document.write(data)
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                  alert(errorThrown)
                })

              } else {
                // do original call
                window.open('', 'DOWNLOAD', '')
                downloadForm.submit()
              }

              // Story 1566, re-enable all product types
              // checkbox
              if (allChecked) {
                // re-enable all 'Product Types'
                // checkboxes
                $.each(
                  downloadForm.find('input.product_type_option_flag:checked'),
                  function () {
                    $(this).prop('disabled', false)
                  }
                )
              }
            }
          }.bind(this))

          $('#results_bookmark').click(
              function (event) {
                event.preventDefault()
                this._setBookmarkURL(new cadc.web.util.URI(event.target.href))
                $('#bookmark_link').modal('show')
              }.bind(this)
          )

          if (preserveColumnState) {
            resultsVOTV.setColumns(prevColumns)
            resultsVOTV.setDisplayColumns(prevDisplayedColumns)
            resultsVOTV.setUpdatedColumnSelects(prevColumnSelects)

            // Set default sort column and direction.
            if (prevSortOptions.hasOwnProperty('sortcol')) {
              resultsVOTV['sortcol'] = prevSortOptions['sortcol']
            }
            if (prevSortOptions.hasOwnProperty('sortAsc')) {
              resultsVOTV['sortAsc'] = prevSortOptions['sortAsc']
            }
          } else {
            resultsVOTV.setDisplayColumns([])
          }

          // Set the default columns and units.
          this._setDefaultColumns(resultsVOTV)
          this._setDefaultUnitTypes(resultsVOTV)

          queryOverlay.modal('show')
        }.bind(this)

        caomSearchForm.subscribe(ca.nrc.cadc.search.events.onValid, onFormValid)


        var onFormInvalid = function (event, args) {
          alert(
              'Please enter at least one value to search on. (' +
              args.cadcForm.getName() +
              ')'
          )
        }

        caomSearchForm.subscribe(
            ca.nrc.cadc.search.events.onInvalid,
            onFormInvalid
        )

        $(':reset').click(
            function () {
              this._getActiveForm().resetFields()
            }.bind(this)
        )

        $('#cancel_search').click(
            function () {
              this._getActiveForm().cancel()
            }.bind(this)
        )

        // End caom2 search form and results tab setup.

        // Start obscore search form setup.
        // If the tab is not going to be shown, there's no need to set up the form.
        var obsCoreSearchForm = null


        if (this.options.showObscoreTab === true) {
          // Grab current endpoint from registry client so configuration of votable
          // can be completed
          obsCoreConfiguration.options.tapSyncEndpoint = _searchTapClient.getLastEndpoint()
          obsCoreSearchForm = new ca.nrc.cadc.search.SearchForm(
              'obscoreQueryForm',
              false,
              obsCoreConfiguration
          )

          // Disable the forms to begin with.
          obsCoreSearchForm.disable()
          obsCoreSearchForm.subscribe(
              ca.nrc.cadc.search.events.onCancel,
              onFormCancel
          )

          obsCoreSearchForm.subscribe(
              ca.nrc.cadc.search.events.onSubmitComplete,
              onFormSubmitComplete
          )

          obsCoreSearchForm.subscribe(
              ca.nrc.cadc.search.events.onValid,
              onFormValid
          )

          obsCoreSearchForm.subscribe(
              ca.nrc.cadc.search.events.onInvalid,
              onFormInvalid
          )
        }
        this._setObsCoreSearchForm(obsCoreSearchForm)
      } catch (errorMessage) {
        console.error('Error initializing search forms: ' + errorMessage)
        _searchApp._trigger(ca.nrc.cadc.search.events.onAdvancedSearchInitFail, {
          error: errorMessage
        })
      }

      // End obscore search form setup.

      // Enable submit buttons for both forms, now they are ready to be used
      $('.submit-query').removeAttr('disabled')

    }

    // End initForms function.

    this._setBookmarkURL = function (hrefURI) {
      hrefURI.clearQuery()
      var href = hrefURI.toString()

      // Strip off the fragment part of the
      // href url if necessary.
      var index = href.indexOf('#')
      if (index !== -1) {
        href = href.substring(0, index)
      }

      var serializer = new cadc.vot.ResultStateSerializer(
        href + this._getFormQueryString(),
        resultsVOTV.sortcol,
        resultsVOTV.sortDir ? 'asc' : 'dsc',
        resultsVOTV.getDisplayedColumns(),
        resultsVOTV.getResizedColumns(),
        resultsVOTV.getColumnFilters(),
        resultsVOTV.getUpdatedColumnSelects()
      )

      var serializedUrl = serializer.getResultStateUrl()

      // Build an href and place it in the bookmark div.
      var bookmarkLink = '<a href="' + serializedUrl + '" target="_blank">' + serializedUrl + '</a>'

      $('#bookmark_link')
        .find('#bookmark_url_display')
        .html(bookmarkLink)

      this._trigger(ca.nrc.cadc.search.events.onSetBookmarkUrl, {
        url: serializedUrl
      })
    }

    this.getQueryFromURI = function () {
      var currentURI = new cadc.web.util.currentURI()
      return currentURI.getQuery()
    }

    /**
     * Start this application.  This will check for a quick submission.
     */
    this.start = function () {
      // After the series of columns (Data Train) has loaded, then proceed.
      var postDataTrainLoad = function (_continue) {
        var activeSearchForm = this._getActiveForm()

        // set tooltips url
        var tooltipURL = 'json/tooltips_' + this.getPageLanguage() + '.json'

        if (_continue && isFirstLoad) {

          // set main search form tooltips
          $.getJSON(tooltipURL, function (jsonData) {
            tooltipJsonData = jsonData
            caomSearchForm.loadTooltips(jsonData, 'popover')
            caomSearchForm.loadTooltips(jsonData, 'dt-popover')

            if (obsCoreSearchForm !== null) {
              obsCoreSearchForm.loadTooltips(jsonData, 'popover')
              obsCoreSearchForm.loadTooltips(jsonData, 'dt-popover')
            }

          })

          // Don't process the queryfrom the URL if this is not the first page load.
          isFirstLoad = false

          var currentURI = new cadc.web.util.currentURI()
          var queryObject = currentURI.getQuery()

          //// Work directly with the form object.
          var $submitForm = activeSearchForm.getForm()
          var doSubmit

          if (JSON.stringify(queryObject) !== JSON.stringify({})) {
            // Update text fields.
            $.each(queryObject, function (qKey, qValue) {
              if (qValue && qValue.length > 0) {
                if (
                  qKey === ca.nrc.cadc.search.CAOM2_RESOLVER_VALUE_KEY ||
                  qKey === ca.nrc.cadc.search.OBSCORE_RESOLVER_VALUE_KEY
                ) {
                  activeSearchForm.clearTimeout()
                  activeSearchForm.setSelectValue(
                    ca.nrc.cadc.search.CAOM2_TARGET_NAME_FIELD_ID,
                    qKey,
                    decodeURIComponent(qValue.join())
                  )
                } else if (qKey === 'Plane.time.bounds.samples_PRESET') {
                  activeSearchForm.setSelectValue(
                    ca.nrc.cadc.search.TIME_BOUNDS_FIELD_ID,
                    qKey,
                    decodeURIComponent(qValue.join())
                  )
                } else if (qKey === ca.nrc.cadc.search.OBSERVATION_INTENT_FIELD_ID) {
                  activeSearchForm.setSelectValue(
                    ca.nrc.cadc.search.OBSERVATION_INTENT_FIELD_ID, 
                    qKey, 
                    decodeURIComponent(qValue.join()))
                } else {
                  activeSearchForm.setInputValue(qKey, decodeURIComponent(qValue.join()))
                }

                doSubmit = true
              }
            })

            // Update DataTrain
            var dtUType = $submitForm.find('.hierarchy_utype').text()
            var dtUTypes = dtUType.split('/')

            for (var i = 0; i < dtUTypes.length; i++) {
              var dtSelectUtype = dtUTypes[i]
              var dtSelectUtypeValues = []

              // Array of values.
              var dtSelectValues = currentURI.getQueryValues(dtSelectUtype)

              if (dtSelectValues && dtSelectValues.length > 0) {
                dtSelectUtypeValues = dtSelectUtypeValues.concat(dtSelectValues)
              }

              // The "collection" keyword is grandfathered in, but actually
              // maps to Observation.collection, so check for "collection" while
              // we're checking for Observation.collection.
              //
              // jenkinsd 2014.02.25
              if (dtSelectUtype === 'Observation.collection') {
                var grandfatheredCollectionValues = currentURI.getQueryValues(
                  'collection'
                )

                if (
                  grandfatheredCollectionValues &&
                  grandfatheredCollectionValues.length > 0
                ) {
                  dtSelectUtypeValues = dtSelectUtypeValues.concat(
                    grandfatheredCollectionValues
                  )
                }
              }

              if (dtSelectUtypeValues && dtSelectUtypeValues.length > 0) {
                dtSelectUtypeValues = dtSelectUtypeValues.map(function (val) {
                  return decodeURIComponent(val)
                })

                var dtSelect = $submitForm.find(
                  "select[id='" + dtSelectUtype + "']"
                )
                // This can't happen until the data train is loaded.
                if (
                  dtSelect &&
                  activeSearchForm.setDataTrainValue(
                    $(dtSelect[0]),
                    dtSelectUtypeValues
                  )
                ) {
                  doSubmit = true
                } else {
                  alert(
                    'Incompatible query parameter: ' +
                    dtSelectUtype +
                    ' > ' +
                    dtSelectUtypeValues
                  )
                  activeSearchForm.cancel()
                  doSubmit = false
                  break
                }
              }
            }
          }

          if (
            doSubmit &&
            (!stringUtil.hasText(currentURI.getQueryValue('noexec')) ||
              currentURI.getQueryValue('noexec') === 'false')
          ) {
            // Execute the form submission.
            activeSearchForm.submit()
          } else {
            // If the current tab is the results tab, display the search results if necessary.

            var activeTabID = window.location.hash || this._getActiveTabID()
            var isNoExecFlag =
              currentURI.getQueryValue('noexec') !== null &&
              currentURI.getQueryValue('noexec') === 'true'
            var destinationTabID

            if (
              (activeTabID !== '#queryFormTab' &&
                sessionStorage.getItem('isReload') === false) ||
              isNoExecFlag ||
              !activeTabID
            ) {
              // go to query tab
              destinationTabID = 'queryFormTab'
            } else {
              destinationTabID = activeTabID.substring(1)
            }

            this._selectTab(destinationTabID)
          }
        }
        else {
          // Initialize the data train tooltips
          caomSearchForm.loadTooltips(tooltipJsonData, 'dt-popover')

          if (this.options.showObscoreTab === true) {
            obsCoreSearchForm.loadTooltips(tooltipJsonData, 'dt-popover')
          }

        }
      }.bind(this)

      var caomSearchForm = this.getCAOMSearchForm()
      var obsCoreSearchForm = this.getObsCoreSearchForm()

      // Default form.
      caomSearchForm.subscribe(ca.nrc.cadc.search.events.onInit, function (
        event,
        args
      ) {
        if (args && args.error) {
          console.error('Error reading TAP schema >> ' + args.error)
        } else {
          caomSearchForm.enable()
          caomSearchForm.resetFields()
        }
      })

      caomSearchForm.getDataTrain().subscribe(
        ca.nrc.cadc.search.datatrain.events.onDataTrainLoaded,
        function () {
          postDataTrainLoad(true)

          this.subscribe(
            ca.nrc.cadc.search.events.onAdvancedSearchInit,
            function () {
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
              $.address.change(
                function (event) {
                  var slashIndex = event.value.indexOf('/')
                  var eventHash =
                    slashIndex >= 0 ?
                    event.value.substring(slashIndex + 1) :
                    event.value
                  var tabID = eventHash || this._getActiveTabID().split('#')[1]

                  if (eventHash) {
                    this._selectTab(tabID)
                  } else {
                    window.location.hash = '#' + tabID
                  }
                }.bind(this)
              )
            }
          )
        }.bind(this)
      )

      caomSearchForm
        .getDataTrain()
        .subscribe(
          ca.nrc.cadc.search.datatrain.events.onDataTrainLoadFail,
          function () {
            postDataTrainLoad(false)
          }
        )

      // if parameter function returns false the toggle is off
      caomSearchForm.init()

      if (this.options.showObscoreTab === true) {
        obsCoreSearchForm.subscribe(ca.nrc.cadc.search.events.onInit, function () {
          obsCoreSearchForm.enable()
          obsCoreSearchForm.resetFields()
        })
        obsCoreSearchForm.init()
      }
    }

    // End start method.

    /**
     *  Sanitize the values for the column options.
     *
     * @param {{}}  _columnOptions    Column options object.
     * @return {{}} Sanitized object.
     * @private
     */
    this._sanitizeColumnOptions = function (_columnOptions) {
      var sanitizedObject = {}

      sanitizedObject.sortColumn = columnManager.getIDFromLabel(
        _columnOptions.sortColumn
      )
      sanitizedObject.sortDir = _columnOptions.sortDir

      if (_columnOptions.columnOptions) {
        sanitizedObject.columnOptions = {}
        $.each(_columnOptions.columnOptions, function (key, obj) {
          sanitizedObject.columnOptions[columnManager.getIDFromLabel(key)] = obj
        })
      }

      if (_columnOptions.columnFilters) {
        sanitizedObject.columnFilters = {}
        $.each(_columnOptions.columnFilters, function (key, obj) {
          sanitizedObject.columnFilters[columnManager.getIDFromLabel(key)] = obj
        })
      }

      if (_columnOptions.defaultColumnIDs) {
        sanitizedObject.defaultColumnIDs = []

        for (var i = 0; i < _columnOptions.defaultColumnIDs.length; i++) {
          sanitizedObject.defaultColumnIDs.push(
            columnManager.getIDFromLabel(_columnOptions.defaultColumnIDs[i])
          )
        }
      }

      return sanitizedObject
    }

    /**
     * Default columns for a new search.  If previously saved columns exist,
     * then use those.
     *
     * @param {cadc.vot.Viewer|Viewer} _viewer    The VOTV viewer instance.
     * @private
     */
    this._setDefaultColumns = function (_viewer) {
      // Check if defaultColumnIDs has already been set in the
      // viewer options (i.e. from a bookmark url) and if so use them.

      // Clear the existing ones first.
      _viewer.getOptions().defaultColumnIDs = []

      var deserializer = new cadc.vot.ResultStateDeserializer(
        window.location.href
      )
      var viewerOptions = deserializer.getViewerOptions()

      if (!$.isEmptyObject(viewerOptions)) {
        $.extend(
          true,
          _viewer.getOptions(),
          this._sanitizeColumnOptions(viewerOptions)
        )
      }

      if (
        !_viewer.getOptions().defaultColumnIDs ||
        _viewer.getOptions().defaultColumnIDs.length === 0
      ) {
        var $activeForm = this._getActiveForm()

        // getDefaultColumnIDs() will add forom field columns if required, based on
        // how $activeForm is currently filled out
        var columnIDs = $activeForm.getDefaultColumnIDs()
        _viewer.getOptions().defaultColumnIDs = columnIDs
      }
    }

    /**
     * Default unit type specfic to a collection.
     *
     * @param {cadc.vot.Viewer|Viewer} _viewer     The VOTV viewer instance.
     * @private
     */
    this._setDefaultUnitTypes = function (_viewer) {
      // Get all default units for columns with a select
      var allColumnOptions = this._getActiveForm().getConfiguration().getColumnOptions()
      var allUnitTypes = {}
      for (var columnOption in allColumnOptions) {
        var column = allColumnOptions[columnOption]
        if (column.header) {
          for (var j = 0; j < column.header.units.length; j++) {
            var headerUnit = column.header.units[j]
            if (headerUnit['default']) {
              allUnitTypes[columnOption] = headerUnit.value
              break
            }
          }
        }
      }

      // Default unit types for the current form.
      var unitTypes = this._getActiveForm().getConfiguration().getDefaultUnitTypes()

      // Update allUnitTypes with default units from unitTypes.
      Object.assign( allUnitTypes, unitTypes )

      var updatedColumnSelects = _viewer.getUpdatedColumnSelects()

      for (var columnName in allUnitTypes) {
        var newDefaultAdded = false
        var oldDefaultRemoved = false

        if (allUnitTypes.hasOwnProperty(columnName)) {
          var defaultUnitType = allUnitTypes[columnName]
          var columnOptions = _viewer.getOptionsForColumn(columnName)
          var selectedUnit = updatedColumnSelects[columnName]
          var units = columnOptions['header']['units']
          for (var i = 0; i < units.length; i++) {
            var unit = units[i]
            if (selectedUnit) {
              if (selectedUnit === unit['value']) {
                if (unit['default']) {
                  oldDefaultRemoved = true
                }
                unit['default'] = true
                newDefaultAdded = true
              } else {
                if (unit['default']) {
                  unit['default'] = false
                  oldDefaultRemoved = true
                }
              }
            } else {
              if (defaultUnitType === unit['value']) {
                if (unit['default']) {
                  // no need to remove default unit type
                  oldDefaultRemoved = true
                }

                unit['default'] = true
                newDefaultAdded = true
              } else {
                // look for default being set in other unit types
                if (unit['default']) {
                  // remove it
                  delete unit['default']
                  oldDefaultRemoved = true
                }
              }
            }
            if (newDefaultAdded && oldDefaultRemoved) {
              break
            }
          }

          _viewer.setOptionsForColumn(columnName, columnOptions)
        }
      }
    }

    /**
     * Called when the results are in and the UWS Job is complete.
     *
     * @param {{}}  jobParams   JSON containing post job creation Upload URL information.
     * @param {String} jobParams.upload_url   URL for upload information to be passed to TAP.
     * @param {function}  [callback]    Optional on completion function.
     * @private
     */
    this._setJobParameters = function (jobParams, callback) {
      // Exclamation points are not encoded by default mechanisms, so explicitly replace them here.
      // WebRT 75687
      // jenkinsd 2019.02.22
      var queryParam = 'QUERY=' + encodeURIComponent(this._getADQL(true)).replace('!', '%21')

      var votableURL =
            _searchTapClient.getLastURL().href +
            '?LANG=ADQL&REQUEST=doQuery&' +
            queryParam

      var upload = this._getTargetUpload()
      if (upload) {
        votableURL += '&UPLOAD=' + upload
      }

      for (var dti = 0, dtl = ca.nrc.cadc.search.downloadTypes.length; dti < dtl; dti++) {
        var nextDownloadType = ca.nrc.cadc.search.downloadTypes[dti]

        var nextVOTableURI = new cadc.web.util.URI(votableURL + '&FORMAT=' + nextDownloadType)
        $('a.votable_link_' + nextDownloadType).prop(
          'href',
          nextVOTableURI.getURI()
        )
      }

      if (callback) {
        callback()
      }
    }

    /**
     * Called after the query form has been submitted.
     *
     * @param {{}}  jobParams   JSON containing post job creation Upload URL information.
     * @private
     */
    this._postQuerySubmission = function (jobParams) {
      queryOverlay.modal('hide')

      var selectAllCheckbox = $("input[name='selectAllCheckbox']")
      selectAllCheckbox.prop('title', 'Mark/Unmark all')

      $('select#download_option_product_type').focus()

      $('.cellValue.preview').tooltip({
        position: 'bottom right',
        offset: [-10, -10],
        effect: 'toggle',
        delay: 0,
        relative: true,
        events: {
          def: 'mouseover,mouseout',
          input: 'focus,blur',
          widget: 'focus mouseenter,blur mouseleave',
          tooltip: 'mouseover,mouseout'
        }
      })
      this._setJobParameters(jobParams)
    }

    /**
     * Display the error VOTable Grid.
     * @param {String}  error_url   UWS Job error URL.
     * @private
     */
    this._processErrorResults = function (error_url) {
      var $errorTooltipColumnPickerHolder = $('#errorTooltipColumnPickerHolder')
      var pageLanguage = this.getPageLanguage()

      // Options for the Error CADC VOTV instance
      var errorVOTVOptions = {
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
        sortColumn: 'LineNumber', // ID of the sort column.
        sortDir: 'asc',
        topPanelHeight: 5,
        enableTextSelectionOnCells: true,
        gridResizable: true,
        rerenderOnResize: false,
        enableSelection: false,
        targetNodeSelector: '#errorTable', // Shouldn't really be an option as it's mandatory!
        columnManager: {
          filterable: true,
          forceFitColumns: false,
          resizable: true,
          picker: {
            style: 'tooltip',
            panel: $('div#error-grid-header'),
            options: {
              buttonText: pageLanguage === 'fr' ?
                "Gérer l'affichage des colonnes" : 'Change Columns'
            },
            tooltipOptions: {
              targetSelector: $errorTooltipColumnPickerHolder
                .find('.tooltip_content')
                .first(),
              appendTooltipContent: true,
              tooltipContent: $errorTooltipColumnPickerHolder
                .find('.tooltip')
                .first(),
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
          TargetError: {
            width: 400
          },
          LineNumber: {
            width: 100
          },
          Target: {
            width: 100
          },
          RA: {
            width: 100
          },
          DEC: {
            width: 100
          },
          radius: {
            width: 80
          }
        } // Done by column ID.
      }

      var errorVOTV = new cadc.vot.Viewer('#errorTable', errorVOTVOptions)

      try {
        errorVOTV.build({
            url: error_url
          },
          function () {
            errorVOTV.render()

            $('#errorTable')
              .find('.grid-header-label')
              .text(pageLanguage === 'fr' ? 'Erreur.' : 'Error')

            // Necessary at the end!
            errorVOTV.refreshGrid()

            queryOverlay.modal('hide')
          },
          function (jqXHR, status, message) {
            console.error('Error status: ' + status)
            console.error('Error message: ' + message)
            console.error('Error from response: ' + jqXHR.responseText)
          }
        )

        $('#errorTableTab-link').click()
      } catch (e) {
        console.error('Found error! > ' + e)
        queryOverlay.modal('hide')
      }
    }

    /**
     * Called when a unit selection has changed and the current filter value needs to be taken into account.
     *
     * Made public only to support tests.
     *
     * @param filterValue
     * @param args
     * @param callback
     * @private
     */
    this.processFilterValue = function (filterValue, args, callback) {
      var columnID = args.column.id
      var unit = args.unitValue
      var $col = $(args.column)
      var converter = columnManager.getConverter(columnID, filterValue, unit)
      var previousUnit = $col.data('previousUnitValue')
      var pureFilterValue = $col.data('pureFilterValue') || filterValue
      var breakdownPureFilterValue = ''
      var breakdownDisplayFilterValue = ''

      if (
        pureFilterValue &&
        converter &&
        converter.convertValue &&
        converter.rebase
      ) {
        var filterBreakdown = columnManager.getFilterPattern(pureFilterValue)

        for (var i = 0; i < filterBreakdown.length; i++) {
          var next = filterBreakdown[i]

          // Assume non-numbers are syntax.
          if (columnManager.isFilterSyntax(next)) {
            breakdownPureFilterValue += next
            breakdownDisplayFilterValue += next
          } else {
            // Convert the numbers
            var rebaseConverter = columnManager.getConverter(
              columnID,
              next,
              unit
            )
            var rebasedVal = rebaseConverter.rebase(previousUnit)
            converter = columnManager.getConverter(columnID, rebasedVal, unit)
            breakdownPureFilterValue += converter.convertValue()
            breakdownDisplayFilterValue += converter.convert()
          }
        }
      }

      callback(breakdownPureFilterValue, breakdownDisplayFilterValue)
    }

    /**
     * Process results from the TAP search.  This function populates the ADQL tab with the query that was generated,
     * creates the VOTV grid, and finally builds it.
     *
     * @param {{}} json                       The json object with the URL to obtain the TAP query results.
     * @param {String}  [json.errorMessage]   Any errors with searching.
     * @param {String}  json.job_url          URL for the search UWS job.
     * @param {String}  json.results_url      URL to obtain search results.
     * @param {String}  json.run_id           The Job ID of the archive search job that spawned the TAP job.
     * @param {{}}      [json.display_units]   Hash map containing uType -> display unit.
     * @param {String}  [json.pos]            Requested spatial cutout value as a DALI string.
     * @param {String}  [json.band]           Requested spectral cutout value as a DALI string
     * @param {String}  [json.upload_url]     URL for upload information to be passed to TAP.
     * @param {Number} startDate              The start time in milliseconds to use as a start time.
     * @param {Function} searchCompleteCallback    Callback on completion.
     * @private
     */
    this._processResults = function (json, startDate, searchCompleteCallback) {
      netEnd = new Date().getTime()

      // Next story should handle this better.
      if (json.errorMessage) {
        this._searchError(json.errorMessage)
      } else {
        var pageLanguage = this.getPageLanguage()
        var runID = json.run_id

        /**
         * Construct a message for the bottom of the panel with interesting time information.
         *
         * @param {Number} queryTimeStart     Start of query in milliseconds.
         * @param {Number} queryTimeEnd       End of TAP query in milliseconds.
         * @param {Number} loadTimeStart      Start of load into grid in milliseconds.
         * @param {Number} loadTimeEnd        End of load into grid in milliseconds.
         * @return {string}
         */
        var buildPanelMessage = function (
          queryTimeStart,
          queryTimeEnd,
          loadTimeStart,
          loadTimeEnd
        ) {
          var isFR = pageLanguage === 'fr'
          var totalQueryTime = (queryTimeEnd - queryTimeStart) / 1000.0
          var totalLoadTime = (loadTimeEnd - loadTimeStart) / 1000.0
          var secondsString = isFR ? ' secondes' : ' seconds'
          return (
            (isFR ? 'Recherche et transfert: ' : 'Query and transfer: ') +
            totalQueryTime +
            secondsString +
            ' - ' +
            (isFR ? 'Lecture et affichage: ' : 'Load and render: ') +
            totalLoadTime +
            secondsString
          )
        }

        this._loadUWSJob(
          json.job_url,
          function (event, args) {
            sessionStorage.setItem('uws_job', JSON.stringify(args.job))

            loadStart = new Date().getTime()

            $queryCode.text(this._adqlPrint(this._getADQL(false)))
          }.bind(this),
          function (event, args) {
            console.error(
              'Status error when loading job: ' + args.errorStatusCode
            )
          }
        )

        if (json.display_units) {
          $(document).data('displayUnits', json.display_units)
        }

        // Set the runid for this download
        // value is used every time, unlike uri and pos which may
        // not be used at all in the case of a target upload file search
        // This is why it's not removed from downloadForm
        downloadForm.find("input[name='runid']").val(runID)

        // Clean and prepare the download form.
        downloadForm.find("input[name='uri']").remove()
        downloadForm.find("input[name='pos']").remove()
        downloadForm.find("input[name='band']").remove()

        if (json.pos) {
          // Spatial cutout requested. Add element to download form as POS.
          var input = $('<input>')
          input.prop('type', 'hidden')
          input.prop('name', 'pos')
          input.val(json.pos)

          downloadForm.append(input)
        }

        if (json.band) {
          // Spectral cutout requested. Add element to download form as BAND.
          var input = $('<input>')
          input.prop('type', 'hidden')
          input.prop('name', 'band')
          input.val(json.band)

          downloadForm.append(input)
        }

        var activeForm = this._getActiveForm()
        var url = json.results_url
        var buildInput = {
          url: url,
          type: $("input[name='format']").val(),
          tableMetadata: activeForm.getResultsTableMetadata(),
          pageSize: ca.nrc.cadc.search.RESULTS_PAGE_SIZE
        }

        resultsVOTV.clearColumnFilters()

        resultsVOTV.build(
          buildInput,
          function () {
            if (searchCompleteCallback) {
              searchCompleteCallback()
            }

            loadEnd = new Date().getTime()

            resultsVOTV.render()

            this._postQuerySubmission({
              upload_url: json.upload_url
            })

            // Necessary at the end!
            resultsVOTV.refreshGrid()
          }.bind(this),
          function (jqXHR, status, message) {
            console.error('Error status: ' + status)
            console.error('Error message: ' + message)
            console.error('Error from response: ' + jqXHR.responseText)
            activeForm.cancel()
            alert(message)
          }
        )
      }
    }

    // End processResults()

    /**
     * Handle an error with the search.
     * @param {String} message   Message to display.
     * @private
     */
    this._searchError = function (message) {
      this._getActiveForm().cancel()
      alert(message)
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param {jQuery.Event} _event       The Event to fire.
     * @param {{}}  _args        Arguments to the event.
     * @returns {*}       The event notification result.
     * @private
     */
    this._trigger = function (_event, _args) {
      var args = _args || {}
      args.application = this

      return $(this).trigger(_event, _args)
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param {jQuery.Event} _event       The Event to fire.
     * @param {function}  __handler   Handler function.
     */
    this.subscribe = function (_event, __handler) {
      $(this).on(_event.type, __handler)
    }

    if (this.options.autoInitFlag === true) {
      this.init()
    }
  }
})(jQuery, window)
