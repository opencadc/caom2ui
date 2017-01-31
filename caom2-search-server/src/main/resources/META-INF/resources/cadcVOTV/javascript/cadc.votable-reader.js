(function ($)
{
  // register namespace
  $.extend(true, window, {
    "cadc": {
      "vot": {
        "Builder": Builder,
        "RowBuilder": RowBuilder,
        "VOTableXMLBuilder": VOTableXMLBuilder,
        "JSONBuilder": JSONBuilder,
        "CSVBuilder": CSVBuilder,
        "StreamBuilder": StreamBuilder,

        "xml": {
          "VOTableXPathEvaluator": VOTableXPathEvaluator
          // "votable": "http://www.ivoa.net/xml/VOTable/v1.3"
        },

        // Events
        "onRowAdd": new jQuery.Event("cadcVOTV:onRowAdd"),

        // For batch row adding.
        "onPageAddStart": new jQuery.Event("cadcVOTV:onPageAddStart"),
        "onPageAddEnd": new jQuery.Event("cadcVOTV:onPageAddEnd"),

        "onDataLoadComplete": new jQuery.Event("cadcVOTV:onDataLoadComplete")
      }
    }
  });
  
  function RowBuilder()
  {
    function setLongest(longestValues, cellID, newValue)
    {
      var stringLength = (newValue && newValue.length) ? newValue.length : -1;

      if (longestValues[cellID] === undefined)
      {
        longestValues[cellID] = -1;
      }
      if (stringLength > longestValues[cellID])
      {
        longestValues[cellID] = stringLength;
      }
    }  	
  	
	 function buildRowData(tableFields, rowID, rowData, longestValues, extract)
    {
      var rowCells = [];
      for (var cellIndex = 0, rowDataLength = rowData.length,
             tableFieldLength = tableFields.length;
           (cellIndex < rowDataLength) && (cellIndex < tableFieldLength);
           cellIndex++)
      {
        var cellField = tableFields[cellIndex];
        var cellDatatype = cellField.getDatatype();
        var stringValue = extract(rowData, cellIndex);

        setLongest(longestValues, cellField.getID(), stringValue);

        var cellValue;

        // Handle the possible array of values. (CADC Story 1750)
        // This is data type agnostic for now.
        if (cellField.containsInterval())
        {
          cellValue = stringValue;
        }
        else if (cellDatatype.isNumeric())
        {
          var num;

          if (!stringValue || ($.trim(stringValue) == ""))
          {
            num = Number.NaN;
          }
          else if (cellDatatype.isFloatingPointNumeric())
          {
            num = parseFloat(stringValue);
            num.toFixed(2);
          }
          else
          {
            num = parseInt(stringValue);
          }
          cellValue = num;
        }
        else if (cellDatatype.isBoolean())
        {
          cellValue = (stringValue === "true");
        }
        else
        {
          // Assume char or char-like value.
          cellValue = stringValue;
        }

        rowCells.push(new cadc.vot.Cell(cellValue, cellField));
      }

      return new cadc.vot.Row(rowID, rowCells);
    }  	
    
    $.extend(this,
             {
             	"buildRowData": buildRowData
             })
  }

  /**
   * Main builder class.  Uses an implementation of a certain kind of builder
   * internally.
   *
   * @param maxRowLimit
   * @param input
   * @param readyCallback
   * @param errorCallback
   * @constructor
   */
  function Builder(maxRowLimit, input, readyCallback, errorCallback)
  {
    var _selfBuilder = this;
    this.voTable = null;
    this._builder = null;

    function init()
    {
      if (input.xmlDOM && (input.xmlDOM.documentElement != null))
      {
        _selfBuilder._builder = new cadc.vot.VOTableXMLBuilder(input.xmlDOM);

        if (readyCallback)
        {
          readyCallback(_selfBuilder);
        }
      }
      else if (input.json)
      {
        _selfBuilder._builder = new cadc.vot.JSONBuilder(input.json);

        if (readyCallback)
        {
          readyCallback(_selfBuilder);
        }
      }
      else if (input.csv)
      {
        _selfBuilder._builder = new cadc.vot.CSVBuilder(maxRowLimit, input, buildRowData);

        if (readyCallback)
        {
          readyCallback(_selfBuilder);
        }
      }
      else if (input.url)
      {
        try
        {
          var streamBuilder = new cadc.vot.StreamBuilder(maxRowLimit, input, readyCallback,
                                                         errorCallback,
                                                         _selfBuilder);

          streamBuilder.start();
        }
        catch (e)
        {
          if (errorCallback)
          {
            errorCallback(null, null, e);
          }
        }
      }
      else
      {
        console.log("cadcVOTV: Input object is not set or not recognizable");
        throw new Error("cadcVOTV: Input object is not set or not recognizable. \n\n" + input);
      }
    }

    function getVOTable()
    {
      return _selfBuilder.voTable;
    }

    /**
     * For those builders that support streaming.
     * @param _responseData   More data.
     */
    function appendToBuilder(_responseData)
    {
      getInternalBuilder().append(_responseData)
    }

    function getInternalBuilder()
    {
      return _selfBuilder._builder;
    }

    function setInternalBuilder(_internalBuilder)
    {
      _selfBuilder._builder = _internalBuilder;
    }

    function getData()
    {
      if (getInternalBuilder())
      {
        return getInternalBuilder().getData();
      }
      else
      {
        return null;
      }
    }

    function build(buildRowData)
    {
      if (getInternalBuilder() && getInternalBuilder().build)
      {
        getInternalBuilder().build(buildRowData);

        if (getInternalBuilder().getVOTable)
        {
          _selfBuilder.voTable = getInternalBuilder().getVOTable();
        }
      }
    }

    function subscribe(builderEvent, handler)
    {
      if (getInternalBuilder().subscribe)
      {
        getInternalBuilder().subscribe(builderEvent, handler);
      }
    }

    function buildRowData(tableFields, rowID, rowData, longestValues, extract)
    {
      return new cadc.vot.RowBuilder().buildRowData(tableFields, rowID, 
                                                    rowData, longestValues, 
                                                    extract);
    }


    $.extend(this,
             {
               "build": build,
               "buildRowData": buildRowData,
               "getVOTable": getVOTable,
               "getData": getData,
               "setInternalBuilder": setInternalBuilder,
               "getInternalBuilder": getInternalBuilder,
               "appendToBuilder": appendToBuilder,

               // Event management
               "subscribe": subscribe
             });

    init();
  }

  /**
   * Evaluate XPath cross-browser.
   *
   * @param _xmlDOM                   The document to traverse.
   * @param _defaultNamespacePrefix   The prefix for default namespaces.
   * @constructor
   */
  function VOTableXPathEvaluator(_xmlDOM, _defaultNamespacePrefix)
  {
    var _selfXPathEvaluator = this;

    this.xmlDOM = _xmlDOM;
    this.defaultNamespacePrefix = _defaultNamespacePrefix;


    function getData()
    {
      return _selfXPathEvaluator.xmlDOM;
    }

    function getDefaultNamespacePrefix()
    {
      return _selfXPathEvaluator.defaultNamespacePrefix;
    }

    /**
     * Prepare the given expression to be processed.  This method will simply
     * prepend the default namespace where needed.
     *
     * @param _expression   The expression XPath to prepare.
     * @return {String}     The prepared path.
     */
    function preparePath(_expression)
    {
      var pathItems = _expression ? _expression.split("/") : [];
      var path = "";

      for (var piIndex = 0; piIndex < pathItems.length; piIndex++)
      {
        var nextItem = pathItems[piIndex];

        if (nextItem)
        {
          path += "/" + getDefaultNamespacePrefix() + ":" + nextItem;
        }
      }

      return path;
    }

    /**
     * Evaluate an XPath expression this reader's DOM, returning the results as
     * an array thanks wanderingstan at morethanwarm dot mail dot com for the
     * initial work.
     *
     * @param _expression   The expression XPath to look for from the root.
     * @return {Array}
     */
    function evaluate(_expression)
    {
      var expressionPath = preparePath(_expression);
      var documentNode = getData();
      var xpe = documentNode.ownerDocument || documentNode;

      var localNSResolver = function (prefix)
      {
        // var localName = cadc.vot.xml[prefix];
        var localName = xpe.documentElement.namespaceURI;
        var resolvedName;

        if (localName)
        {
          resolvedName = localName;
        }
        else
        {
          resolvedName = xpe.createNSResolver
                       ? xpe.createNSResolver(xpe.documentElement)(prefix)
                       : null;
        }

        return resolvedName;
      };

      if (!xpe.evaluate)
      {
        xpe.evaluate = document.evaluate;
      }

      var result = xpe.evaluate(expressionPath, documentNode, localNSResolver,
                                XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
      var found = [];
      var res;

      while (res = result.iterateNext())
      {
        found.push(res);
      }

      return found;
    }

    $.extend(this,
             {
               "evaluate": evaluate
             });
  }

  /**
   * The XML plugin reader.
   *
   * @param _xmlDOM    The XML DOM to use.
   * @constructor
   */
  function VOTableXMLBuilder(_xmlDOM)
  {
    var _selfXMLBuilder = this;

    this.voTable = null;
    this.xmlDOM = _xmlDOM;

    function init()
    {
      if (!document.evaluate)
      {
        if (wgxpath)
        {
          // Internet Explorer compatibility.
          //
          // WebRT 48318
          // jenkinsd 2014.02.13
          //
          wgxpath.install();
        }
        else
        {
          throw new Error("cadcVOTV: Internet Explorer poly fill not present.");
        }
      }
      else if (getData().documentElement.nodeName == 'parsererror')
      {
        throw new Error("cadcVOTV: XML input is invalid.\n\n");
      }
    }

    function getVOTable()
    {
      return _selfXMLBuilder.voTable;
    }

    /**
     * Given traverse the DOM for this document to the given expression.
     *
     * @param xPathExpression   Expression to traverse to.
     * @return {Array}          Array of found items.
     */
    function getElements(xPathExpression)
    {
      var evaluator = new VOTableXPathEvaluator(getData(), "votable");

      return evaluator.evaluate(xPathExpression);
    }

    function getData()
    {
      return _selfXMLBuilder.xmlDOM;
    }

    function build(buildRowData)
    {
      // Work around the default namespace.
      var xmlVOTableResourceDOMs = getElements("/VOTABLE/RESOURCE");

      var voTableParameters = [];
      var voTableResources = [];
      var voTableInfos = [];
      var resourceTables = [];
      var resourceInfos = [];
      var voTableFields;

      // Iterate over resources.
      for (var resourceIndex = 0; resourceIndex < xmlVOTableResourceDOMs.length;
           resourceIndex++)
      {
        var nextResourcePath = "/VOTABLE/RESOURCE[" + (resourceIndex + 1) + "]";
        var nextResourceDOM = xmlVOTableResourceDOMs[resourceIndex];
        var resourceInfoDOMs = getElements(nextResourcePath + "/INFO");

        // Iterate Resource INFOs
        for (var infoIndex = 0; infoIndex < resourceInfoDOMs.length;
             infoIndex++)
        {
          var nextInfo = resourceInfoDOMs[infoIndex];
          resourceInfos.push(new cadc.vot.Info(nextInfo.getAttribute("name"),
                                               nextInfo.getAttribute("value")));
        }

        var resourceDescriptionDOMs = getElements(nextResourcePath
                                                  + "/DESCRIPTION");

        var resourceDescription = resourceDescriptionDOMs.length > 0
                                  ? resourceDescriptionDOMs[0].value : null;

        var resourceMetadata = new cadc.vot.Metadata(null, resourceInfos,
                                                     resourceDescription, null,
                                                     null, null);

        var resourceTableDOMs = getElements(nextResourcePath + "/TABLE");

        // Iterate over tables.
        for (var tableIndex = 0; tableIndex < resourceTableDOMs.length;
             tableIndex++)
        {
          var nextTablePath = nextResourcePath + "/TABLE[" + (tableIndex + 1)
                              + "]";

          var tableFields = [];
          var resourceTableDescriptionDOM = getElements(nextTablePath
                                                        + "/DESCRIPTION");
          var resourceTableDescription =
              resourceTableDescriptionDOM.length > 0
              ? resourceTableDescriptionDOM[0].value : null;

          var resourceTableFieldDOM = getElements(nextTablePath + "/FIELD");

          // To record the longest value for each field (Column).  Will be
          // stored in the TableData instance.
          //
          // It contains a key of the field ID, and the value is the integer
          // length.
          //
          // Born from User Story 1103.
          var longestValues = {};

          /**
           * Method to construct a row.  This is called for each row read.
           *
           * @param rowData       The row data for a single row.
           * @param index         The row index.
           * @returns {string|*}
           */
          var getCellData = function (rowData, index)
          {
            var cellDataDOM = rowData[index];
            return (cellDataDOM.childNodes && cellDataDOM.childNodes[0]) ?
                   cellDataDOM.childNodes[0].nodeValue : "";
          };

          for (var fieldIndex = 0; fieldIndex < resourceTableFieldDOM.length;
               fieldIndex++)
          {
            var nextFieldPath = nextTablePath + "/FIELD[" + (fieldIndex + 1)
                                + "]";
            var fieldDOM = resourceTableFieldDOM[fieldIndex];
            var fieldID;
            var xmlFieldID = fieldDOM.getAttribute("id");
            var xmlFieldUType = fieldDOM.getAttribute("utype");
            var xmlFieldName = fieldDOM.getAttribute("name");

            if (xmlFieldID && (xmlFieldID != ""))
            {
              fieldID = xmlFieldID;
            }
            else
            {
              fieldID = xmlFieldName;
            }

            longestValues[fieldID] = -1;

            var fieldDescriptionDOM = getElements(nextFieldPath
                                                  + "/DESCRIPTION");

            var fieldDescription = ((fieldDescriptionDOM.length > 0)
                                    && fieldDescriptionDOM[0].childNodes
                                    && fieldDescriptionDOM[0].childNodes[0])
                ? fieldDescriptionDOM[0].childNodes[0].nodeValue
                : "";

            var field = new cadc.vot.Field(
                xmlFieldName,
                fieldID,
                fieldDOM.getAttribute("ucd"),
                xmlFieldUType,
                fieldDOM.getAttribute("unit"),
                fieldDOM.getAttribute("xtype"),
                new cadc.vot.Datatype(fieldDOM.getAttribute("datatype")),
                fieldDOM.getAttribute("arraysize"),
                fieldDescription,
                fieldDOM.getAttribute("name"));

            tableFields.push(field);
          }

          var tableMetadata = new cadc.vot.Metadata(null, null,
                                                    resourceTableDescription,
                                                    null, tableFields, null);

          voTableFields = (voTableFields === undefined) ? tableFields : voTableFields;

          var tableDataRows = [];
          var rowDataDOMs = getElements(nextTablePath + "/DATA/TABLEDATA/TR");
          var tableFieldsMetadata = tableMetadata.getFields();

          for (var rowIndex = 0, rowDataDOMLength = rowDataDOMs.length;
               rowIndex < rowDataDOMLength; rowIndex++)
          {
            var nextRowPath = nextTablePath + "/DATA/TABLEDATA/TR["
                              + (rowIndex + 1) + "]";
            var rowDataDOM = rowDataDOMs[rowIndex];
            var rowCellsDOM = getElements(nextRowPath + "/TD");
            var rowID = rowDataDOM.getAttribute("id");

            if (!rowID)
            {
              rowID = "vov_" + rowIndex;
            }

            var rowData = buildRowData(tableFieldsMetadata, rowID, rowCellsDOM,
                                       longestValues, getCellData);

            tableDataRows.push(rowData);
          }

          var tableData = new cadc.vot.TableData(tableDataRows, longestValues);
          resourceTables.push(new cadc.vot.Table(tableMetadata, tableData));
        }

        voTableResources.push(
            new cadc.vot.Resource(nextResourceDOM.getAttribute("id"),
                                  nextResourceDOM.getAttribute("name"),
                                  nextResourceDOM.getAttribute("type") == "meta",
                                  resourceMetadata, resourceTables));
      }

      var xmlVOTableDescription = getElements("/VOTABLE/DESCRIPTION");
      var voTableDescription = xmlVOTableDescription.length > 0
                               ? xmlVOTableDescription[0].value : null;
      var voTableMetadata = new cadc.vot.Metadata(voTableParameters,
                                                  voTableInfos,
                                                  voTableDescription, null,
                                                  voTableFields, null);

      _selfXMLBuilder.voTable = new cadc.vot.VOTable(voTableMetadata,
                                                     voTableResources);

      fireEvent(cadc.vot.onDataLoadComplete, null);
    }

    function fireEvent(event, eventData)
    {
      eventData = eventData || {};
      eventData.builder = _selfXMLBuilder;

      $(_selfXMLBuilder).trigger(event, eventData);
    }

    function subscribe(event, eHandler)
    {
      $(_selfXMLBuilder).on(event.type, eHandler);
    }

    $.extend(this,
             {
               "build": build,
               "evaluateXPath": getElements,
               "getData": getData,
               "getVOTable": getVOTable,

               "subscribe": subscribe
             });

    init();
  }

  // End XML.

  /**
   * The JSON plugin reader.
   *
   * @param jsonData    The JSON VOTable.
   * @constructor
   */
  function JSONBuilder(jsonData)
  {
    var _selfJSONBuilder = this;

    this.voTable = null;
    this.jsonData = jsonData;

    function getVOTable()
    {
      return _selfJSONBuilder.voTable;
    }

    function getData()
    {
      return _selfJSONBuilder.jsonData;
    }

    function build()
    {
      // Does nothing yet.
    }
  }

  /**
   * The CSV plugin reader.
   *
   * @param maxRowLimit   Limit for the maximum number of rows.
   * @param input         The CSV type and table metadata.
   * @param buildRowData  The function to make something vo-consistent from the row data.
   * @constructor
   */
  function CSVBuilder(maxRowLimit, input, buildRowData)
  {
    var _selfCSVBuilder = this;
    var longestValues = {};
    var chunk = {lastMatch: 0, rowCount: 0};
    var pageSize = input.pageSize || null;


    function init()
    {
	    if (pageSize)
	    {
	      // Also issue a page end on load complete.
	      subscribe(cadc.vot.onDataLoadComplete, function(e)
	      {
	        fireEvent(cadc.vot.onPageAddEnd);
	      });
	    }
    }

    /**
     * For non-streaming items.
     */
    function build()
    {
      if (input.csv)
      {
        append(input.csv);
        loadEnd();
      }
    }

    function append(asChunk)
    {
      var found = findRowEnd(asChunk, chunk.lastMatch);

      // skip the first row - it contains facsimiles of column names
      if ((chunk.rowCount === 0) && (found > 0))
      {
        found = advanceToNextRow(asChunk, found);
      }

      while ((found > 0) && (found !== chunk.lastMatch))
      {
        nextRow(asChunk.slice(chunk.lastMatch, found));
        found = advanceToNextRow(asChunk, found);
      }
    }

    function getCurrent()
    {
      // this is for testing support only
      return chunk;
    }

    function subscribe(event, eHandler)
    {
      $(_selfCSVBuilder).on(event.type, eHandler);
    }

    function fireEvent(event, eventData)
    {
      $(_selfCSVBuilder).trigger(event, eventData);
    }

    function advanceToNextRow(asChunk, lastFound)
    {
      chunk.rowCount++;
      chunk.lastMatch = lastFound;
      return findRowEnd(asChunk, chunk.lastMatch);
    }

    function findRowEnd(inChunk, lastFound)
    {
      return inChunk.indexOf("\n", lastFound + 1);
    }

    function nextRow(entry)
    {
      var entryAsArray = $.csv.toArray(entry);
      var tableFields = input.tableMetadata.getFields();

      var rowData = buildRowData(tableFields, "vov_" + chunk.rowCount,
                                 entryAsArray,
                                 longestValues,
                                 function (rowData, index)
                                 {
                                   return rowData[index].trim();
                                 });

      if (pageSize)
      {
        // Used to calculate the page start and end based on the current row 
        // count.
        var moduloPage = (chunk.rowCount % pageSize);

        if (moduloPage === 1)
        {
          fireEvent(cadc.vot.onPageAddStart);
        }
        else if (moduloPage === 0)
        {
          fireEvent(cadc.vot.onPageAddEnd);
        }
      }

      fireEvent(cadc.vot.onRowAdd, rowData);
    }

    function loadEnd()
    {
      fireEvent(cadc.vot.onDataLoadComplete, {"longestValues": longestValues});
    }

    $.extend(this,
             {
               "append": append,
               "build": build,
               "getCurrent": getCurrent,
               "subscribe": subscribe,
               "loadEnd": loadEnd
             });
             
    init();
  }

  /**
   * Stream builder for URLs being input.  Relies on the cadc.uri.js to be
   * imported.
   *
   * @param maxRowLimit     Limit for the maximum number of rows.
   * @param input           The input options.
   * @param readyCallback   Callback for ready.
   * @param errorCallback   Callback for errors.
   * @param __MAIN_BUILDER  The internal data-savvy builder.
   * @constructor
   */
  function StreamBuilder(maxRowLimit, input, readyCallback, errorCallback, __MAIN_BUILDER)
  {
    if (!(cadc.web))
    {
      throw "URL results rely on the CADC uri js (cadc.uri.js) in the cadcJS "
            + "module.";
    }

    var _selfStreamBuilder = this;

    this.errorCallbackFunction = null;
    this.url = new cadc.web.util.URI(input.url);

    function init()
    {
      if (errorCallback)
      {
        _selfStreamBuilder.errorCallbackFunction = errorCallback;
      }
      else
      {
        _selfStreamBuilder.errorCallbackFunction =
        function (jqXHR, status, message)
        {
          var outputMessage =
              "cadcVOTV: Unable to read from URL (" + input.url + ").";

          if (message && ($.trim(message) != ""))
          {
            outputMessage += "\n\nMessage from server: " + message;
          }

          throw new Error(outputMessage);
        };
      }
    }

    function getErrorCallbackFunction()
    {
      return _selfStreamBuilder.errorCallbackFunction;
    }

    function getURL()
    {
      return _selfStreamBuilder.url;
    }

    function getURLString()
    {
      var thisURL = getURL();
      var urlToUse;

      if (input.useRelativeURL)
      {
        urlToUse = thisURL.getRelativeURI();
      }
      else
      {
        urlToUse = thisURL.getURI();
      }

      return urlToUse;
    }

    function start()
    {
      $.ajax({
               url: getURLString(),
               type: "GET",
               xhr: createRequest
             }).fail(getErrorCallbackFunction());
    }

    function handleInputError()
    {
      var message =
          "cadcVOTV: Unable to obtain XML, JSON, or CSV VOTable from URL (" + input.url + ").";
      console.log(message);

      throw new Error(message);
    }

    /**
     * Create the internal builder once the request has been established.
     */
    function initializeInternalBuilder(event, target)
    {
      var req = event.target;

      if (req.readyState == req.HEADERS_RECEIVED)
      {
        var contentType = req.getResponseHeader("Content-Type");

        // Only CSV supports streaming!
        if (contentType && ((contentType.indexOf("csv") >= 0)
                            || (contentType.indexOf("text/plain") >= 0)))
        {
          __MAIN_BUILDER.setInternalBuilder(
                new cadc.vot.CSVBuilder(maxRowLimit, input,
                                        __MAIN_BUILDER.buildRowData));

          if (readyCallback)
          {
            readyCallback(__MAIN_BUILDER);
          }
        }
        else
        {
          handleInputError();
        }
      }
    }

    function loadEnd()
    {
      __MAIN_BUILDER.getInternalBuilder().loadEnd();
    }

    function handleProgress(event, target)
    {
      __MAIN_BUILDER.appendToBuilder(event.target.responseText);
    }

    function createRequest()
    {
      var request;

      try
      {
        // This won't work in versions 5 & 6 of Internet Explorer.
        request = new XMLHttpRequest();
      }
      catch (trymicrosoft)
      {
        console.log("Trying Msxml2 request.");
        try
        {
          request = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (othermicrosoft)
        {
          try
          {
            console.log("Trying Microsoft request.");
            request = new ActiveXObject("Microsoft.XMLHTTP");
          }
          catch (failed)
          {
            throw new Error("Unable to create an HTTP request.  Aborting!");
          }
        }
      }

      var readyStateChangeHandler;

      // Internet Explorer will need to be handled via the old state change
      // behaviour.
      if (window.ActiveXObject)
      {
        readyStateChangeHandler = function(_event, _target)
        {
          try
          {
            initializeInternalBuilder(_event, _target);

            // Complete
            if (this.readyState === this.DONE)
            {
              handleProgress(_event, _target);
              loadEnd();
            }
          }
          catch (e)
          {
            console.log(e);
            handleInputError();
          }
        };
      }
      else
      {
        request.addEventListener("progress", handleProgress, false);
        request.addEventListener("load", loadEnd, false);
        request.addEventListener("abort", loadEnd, false);
        readyStateChangeHandler = initializeInternalBuilder;
      }

      request.addEventListener("error", loadEnd, false);
      request.addEventListener("readystatechange", readyStateChangeHandler,
                               false);

      // Load end was not supported by Safari, so use the individual events that
      // it represents instead.
      //
      // jenkinsd 2014.01.21
      //
//      request.addEventListener("loadend", loadEnd, false);

      return request;
    }

    $.extend(this,
             {
               "start": start,
               "getURLString": getURLString
             });

    init();
  }

})(jQuery);
