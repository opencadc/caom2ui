/**
 * CADC VOTable viewer plugin to hook into the filter input boxes to suggest
 * data from the grid as the user types.
 *
 * @param _viewer     The cadc.vot.Viewer object containing data.
 *
 * jenkinsd 2014.12.01
 */
(function ($)
{
  // register namespace
  $.extend(true, $.fn, {
    "cadcVOTV_filter_suggest": cadcVOTV_filter_suggest
  });

  /**
   * Make use of autocomplete suggestions on filtering.
   *
   * @param _viewer           The VOTable viewer object.
   * @param _returnCount      The optional maximum number of items to return.
   * @constructor
   */
  function cadcVOTV_filter_suggest(_viewer, _returnCount)
  {
    var $inputField = $(this);
    var suggestionKeys = [];
    var columnID = $inputField.data("columnId");


    function filter(val, closeAutocompleteFlag)
    {
      if (closeAutocompleteFlag)
      {
        $inputField.autocomplete("close");
      }

      _viewer.doFilter(val, columnID);

      var grid = _viewer.getGrid();
      grid.invalidateAllRows();
      grid.resizeCanvas();
    }

    /**
     * Verify whether to match against all of the data, or only the current
     * subset (already filtered by another column).
     *
     * The logic is to check the current column filters in the Viewer, and if
     * there is just one (the driver filter), and it is this current column
     * filter, then match against the entire data set.
     *
     * @returns {boolean|*}
     */
    function matchAgainstFullData()
    {
      // Existing column filters.  We need this to
      // check if we should match against what is
      // in the Grid only, or to go back and match
      // against the full set of data.
      //
      // This is done by checking if there is a
      // single column filter in play, and if it
      // matches this one.
      //
      // jenkinsd 2014.12.15
      var existingColumnFilters =
          _viewer.getColumnFilters();

      var keyCount = 0;

      for (var k in existingColumnFilters)
      {
        if (existingColumnFilters.hasOwnProperty(k)
            && existingColumnFilters[k])
        {
          keyCount++;
        }
      }

      return ((keyCount === 0)
              || ((keyCount === 1) && existingColumnFilters[columnID]));
    }

    $inputField.on("change keyup", function (event)
    {
      var trimmedVal = $.trim($inputField.val());

      // Clear it if the input is cleared.
      if (!trimmedVal || (trimmedVal === ''))
      {
        _viewer.getColumnFilters()[columnID] = '';
        filter("", true);
      }
    });

    // Autocomplete the items from the Grid's data.
    $inputField.autocomplete({
                               // Define the minimum search string length
                               // before the suggested values are shown.
                               minLength: 1,

                               // Define callback to format results
                               source: function (req, callback)
                               {
                                 var enteredValue = req.term;

                                 // Reset each time as they type.
                                 suggestionKeys = [];

                                 // Conditional logic to not use autocomplete, such as range searches.
                                 var trimmedVal = $.trim(enteredValue);
                                 var space = " ";
                                 var numericRangeSearchRegex = /^(>|<|=)/i;
                                 var rangeSearchString = "..";
                                 var endsWithSpace =
                                     (enteredValue.indexOf(space,
                                                           (enteredValue.length - space.length)) !== -1);

                                 // Ends with space, so exact match.
                                 if (endsWithSpace
                                     || trimmedVal.match(numericRangeSearchRegex)
                                     || (trimmedVal.indexOf(rangeSearchString) !== -1))
                                 {
                                   // Exact match on space at end.
                                   filter(trimmedVal, true);
                                 }
                                 // Clear it if the input is cleared.
                                 else if (!trimmedVal || (trimmedVal === ''))
                                 {
                                   filter("", true);
                                 }
                                 else
                                 {
                                   var grid = _viewer.getGrid();
                                   var dataView = grid.getData();
                                   var uniqueItems = [];
                                   var columnFilterObject = {};
                                   var fullDataMatch =
                                       matchAgainstFullData();

                                   var l = fullDataMatch ?
                                           dataView.getItems().length :
                                           dataView.getLength();

                                   columnFilterObject[columnID] = enteredValue;

                                   for (var ii = 0; ((ii < l)
                                                 && (!_returnCount
                                                     || (suggestionKeys.length <= _returnCount))); ii++)
                                   {
                                     var item = fullDataMatch
                                         ? dataView.getItemByIdx(ii)
                                         : dataView.getItem(ii);
                                     var nextItem =
                                         _viewer.formatCellValue(item, grid,
                                                                 columnID);

                                     if (!uniqueItems[nextItem]
                                         && _viewer.searchFilter(
                                             item,
                                             {
                                               columnFilters: columnFilterObject,
                                               grid: grid,
                                               doFilter: _viewer.valueFilters,
                                               formatCellValue: _viewer.formatCellValue
                                             }))
                                     {
                                       uniqueItems[nextItem] = true;
                                       suggestionKeys.push(nextItem);
                                     }
                                   }
                                 }

                                 //var uniqueKeys = suggestionKeys.filter(onlyUnique);

                                 // For a single available value, pre select it.
                                 if (suggestionKeys.length == 1)
                                 {
                                   filter(suggestionKeys[0], false);
                                 }

                                 callback(suggestionKeys);
                               },
                               select: function (event, ui)
                               {
                                 filter(($.trim(ui.item.value) || ""), true);
                               }
                             }).blur(function(e)
                                         {
                                           var enteredValue =
                                               $.trim($inputField.val());

                                           if (enteredValue)
                                           {
                                             // Exact match on blur.
                                             filter(enteredValue, true);
                                           }
                                           else
                                           {
                                             filter("", true);
                                           }
                                         });

    return this;
  }
})(jQuery);

