(function ($)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
              "TooltipCreator": TooltipCreator
          }
        }
      }
    }
  });

  /**
   * Create Resolver results as a tooltip.
   */
  function TooltipCreator()
  {
    /**
     * Obtain the tooltip string, with markup, for the given markup text and the
     * given header.  This will return a jQuery object representing the content.
     *
     * @param tipHTML
     * @param tooltipHeaderText
     * @param tipClass optional tooltip Class
     * @param tipHeader
     * @returns {jQuery}
     */
    function getContent(tipHTML, tooltipHeaderText, tipClass, tipHeader)
    {
      var $divElement = $("<div class='module-tool module-simplify module-tool-tooltip'></div>");
      var $tooltipHeader = $("<div class='tooltip_header'></div>");
      $("<h6></h6>").appendTo($tooltipHeader);
      var $tooltipTextElement = $("<p>").appendTo("<div class='tooltip_text'></div>");

      if (tipClass)
      {
        $divElement.addClass(tipClass);
      }

      $tooltipTextElement.html(tipHTML);
      $divElement.append($tooltipTextElement.parent());

      return $divElement.clone();
    }


    /**
     * Obtain the tooltip header, with markup, for the given markup text and the
     * given header.  This will return a jQuery object representing the content.
     *
     * @param tooltipHeaderText
     * @returns {jQuery}
     */
    function getHeader(tooltipHeaderText, tooltipID)
    {
      var $divEl =
          $("<div class=''></div>");

      var $spanEl = $('<span class="text-info"></span>');
      $spanEl.text(tooltipHeaderText);

      var buttonID = tooltipID + "_close";
      var $buttonEl = $('<div id="' + buttonID
                        + '" class="glyphicon glyphicon-remove-circle popover-blue popover-right"></div>');

      $divEl.append($spanEl);
      $divEl.append($buttonEl);

      return $divEl.clone(true, true);
    }



    /**
     * Obtain the text with markup for the tooltip body, from the NameResolver
     * result.
     *
     * Allows the tooltip labels to be in more than one language.
     */
    function extractResolverValue(resolverResult)
    {
      // coordinates are 3 values to one field:
      //
      //        "RA": ".resolver-result-coordinates",
      //        "Dec": ".resolver-result-coordinates",
      //        "coordsys": ".resolver-result-coordinates",
      //
      var resolverMap = {"target": ".resolver-result-target",
        "service": ".resolver-result-service",
        "oname": ".resolver-result-name",
        "otype": ".resolver-result-type",
        "mtype": ".resolver-result-morphology",
        "time": ".resolver-result-time"};

      if (resolverResult)
      {
        // make a string into a set of key-value pairs
        var resolverObject = {};
        var x = resolverResult.split('\n');
        for (var f in x)
        {
          if (x.hasOwnProperty(f))
          {
            var xField = x[f];
            var a = xField.split(':')[0];
            var b = xField.split(':')[1];
            resolverObject[a] = b.trim();
          }
        }

        for (var field in resolverMap)
        {
          if (resolverObject.hasOwnProperty(field))
          {
            $(resolverMap[field]).text(resolverObject[field]);
          }
        }

        if (resolverObject["RA"] || resolverObject["Dec"] || resolverObject["coordsys"])
        {
          $(".resolver-result-coordinates").text(
            (resolverObject["RA"] ? resolverObject["RA"] : "") + " " +
              (resolverObject["Dec"] ? resolverObject["Dec"] : "") + " " +
              (resolverObject["coordsys"] ? resolverObject["coordsys"] : ""));
        }
      }
    }

    $.extend(this,
             {
               "extractResolverValue": extractResolverValue,
               "getContent": getContent,
               "getHeader": getHeader
             });

  }
})(jQuery);
