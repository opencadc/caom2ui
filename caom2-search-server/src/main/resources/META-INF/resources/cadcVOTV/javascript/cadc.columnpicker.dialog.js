;(function($, window, undefined) {
  $.extend(true, window, {
    cadc: {
      vot: {
        picker: {
          CLEAR_BLOCK: "<div class='clear'></div>",
          CHECKBOX_ID: '_checkbox_selector',
          TARGET_SELECTOR_OPTION_KEY: 'targetSelector',
          DIALOG_TRIGGER_ID_OPTION_KEY: 'dialogTriggerID',
          DIALOG_CLOSE_SELECTOR_KEY: 'closeDialogSelector',
          DialogColumnPicker: DialogColumnPicker,
          defaultOptions: {
            showAllButtonText: 'Show all columns',
            resetButtonText: 'Default columns',
            orderAlphaButtonText: 'Order alphabetically',
            dialogButtonID: 'slick-columnpicker-panel-change-column',

            // Options for the dialog
            modal: true,
            autoOpen: false,
            closeDialogSelector: '.dialog_close'
          },
          defaultSortableOptions: {
            // Options for the sortable menus.
            //helper: 'clone',
            opacity: 0.8,
            refreshPositions: true,
            cancel: '.ui-state-disabled'
          },
          events: {
            onColumnPickerInit: new Slick.Event(),
            onColumnAddOrRemove: new Slick.Event(),
            onSort: new Slick.Event(),
            onResetColumnOrder: new Slick.Event(),
            onShowAllColumns: new Slick.Event(),
            onSortAlphabetically: new Slick.Event()
          }
        }
      }
    }
  })

  /**
   * New Dialog column picker.  To reduce complexity, it's practice here to
   * ensure the menu lists are updated first, then the refresh process will
   * use that to then set the columns.
   *
   * @param _columns   The columns to put.
   * @param _grid      The underlying Grid.
   * @param _options   Optional items.
   * @constructor
   */
  function DialogColumnPicker(_columns, _grid, _options) {
    var LINE_BREAK_HTML = '<br />'
    var selfColumnPicker = this

    this.options = $.extend({}, cadc.vot.picker.defaultOptions, _options)

    // Cached value to reset to.
    this.checkboxColumn = _grid.getColumns()[0]
    this.$dialog = $('#column_manager_container')
    this.$selectedItems = $(
      "<ul class='slick-columnpicker slick-columnpicker-tooltip' />"
    ).attr('id', 'cadc_columnpicker_selected_items')
    this.$availableItems = $(
      "<ul class='slick-columnpicker slick-columnpicker-tooltip' />"
    ).attr('id', 'cadc_columnpicker_available_items')
    this.grid = _grid
    this.originalDisplayedColumns = _grid.getColumns()
    this.allColumns = _columns
    this.$target = $(getOption(cadc.vot.picker.TARGET_SELECTOR_OPTION_KEY))

    /**
     * Order alphabetically one menu.
     *
     * @param _$menu    The <UL> menu to order the items of.
     * @param _reverseFlag  Whether to sort in reverse.
     * @private
     */
    function _alphaOrder(_$menu, _reverseFlag) {
      var arrayUtil = new cadc.web.util.ArrayUtil()
      var $arr = _$menu.find('li')

      // Make a copy to use later to reset the data-column-id attribute.
      var $arrCopy = $arr.clone(true, true)

      $arr.sort(function(o1, o2) {
        if (_reverseFlag === true) {
          return arrayUtil.compare(
            $(o2)
              .find('div:first-child')
              .text(),
            $(o1)
              .find('div:first-child')
              .text()
          )
        } else {
          return arrayUtil.compare(
            $(o1)
              .find('div:first-child')
              .text(),
            $(o2)
              .find('div:first-child')
              .text()
          )
        }
      })

      _$menu.empty()

      // Re-instate the data-column-id attributes as they are lost during
      // array sorting! jenkinsd 2015.06.03
      for (var sai = 0, sal = $arr.length; sai < sal; sai++) {
        var $nextSorted = $($arr[sai])

        for (var ai = 0, al = $arrCopy.length; ai < al; ai++) {
          var $nextItemWithID = $($arrCopy[ai])
          if ($nextItemWithID.attr('id') === $nextSorted.attr('id')) {
            _$menu.append($nextItemWithID)
            break
          }
        }
      }
    }

    /**
     * Initialize this column picker.
     */
    function init() {
      // Start fresh each time.
      selfColumnPicker.$target.empty()

      cadc.vot.picker.events.onSortAlphabetically.subscribe(function(
        event,
        args
      ) {
        if (
          args.$menu.attr('id') === selfColumnPicker.$selectedItems.attr('id')
        ) {
          setColumns()
        }
      })

      cadc.vot.picker.events.onShowAllColumns.subscribe(function() {
        setColumns()
      })

      cadc.vot.picker.events.onResetColumnOrder.subscribe(function(event) {
        setColumns()

        event.stopImmediatePropagation()
        return false
      })

      var $buttonHolder = $(
        "<div class='slick-column-picker-tooltip-button-holder'></div>"
      ).appendTo(selfColumnPicker.$target)
      selfColumnPicker.$target
        .append(cadc.vot.picker.CLEAR_BLOCK)
        .append('<hr />')

      var $showAllSpan = $("<span class='slick-column-picker-button'></span>")
        .text(getOption('showAllButtonText'))
        .appendTo($buttonHolder)
      var $resetSpan = $("<span class='slick-column-picker-button'></span>")
        .text(getOption('resetButtonText'))
        .appendTo($buttonHolder)

      // Clear before the menus.
      selfColumnPicker.$target.append(cadc.vot.picker.CLEAR_BLOCK)

      var alphaButtonHTML =
        "<span class='slick-column-picker-button alpha-sort margin-left-none'>&darr;&nbsp;&uarr;</span>"

      var $selectedAlphaSortButton = $(alphaButtonHTML)
      $selectedAlphaSortButton.attr(
        'id',
        selfColumnPicker.$selectedItems.attr('id') + '_ALPHASORT'
      )
      $selectedAlphaSortButton.data('reverse-sort', false)

      var $availableAlphaSortButton = $(alphaButtonHTML)
      $availableAlphaSortButton.attr(
        'id',
        selfColumnPicker.$availableItems.attr('id') + '_ALPHASORT'
      )
      $availableAlphaSortButton.data('reverse-sort', false)

      var $mainContainer = $("<div class='equalize' />")
      var $selectedItemsContainer = $('<div>')
        .addClass('row-start')
        .addClass('span-2')
        .append($selectedAlphaSortButton)
        .append(LINE_BREAK_HTML)
        .append(selfColumnPicker.$selectedItems)
      var $availableItemsContainer = $('<div>')
        .addClass('row-end')
        .addClass('span-2')
        .addClass('float-right')
        .append($availableAlphaSortButton)
        .append(LINE_BREAK_HTML)
        .append(selfColumnPicker.$availableItems)

      /**
       * Post alpha sort.
       *
       * @param _$menu        The menu to pass to the event.
       * @param _$origin      The clicked element
       * @returns {boolean}   Return false to prevent default click behaviour.
       * @private
       */
      var _onAlphaSort = function(_$menu, _$origin) {
        var reverseSortFlag = _$origin.data('reverse-sort')

        _alphaOrder(_$menu, reverseSortFlag)

        // Switch the reverse sort for next time.
        _$origin.data('reverse-sort', !reverseSortFlag)

        trigger(
          cadc.vot.picker.events.onSortAlphabetically,
          {
            $menu: _$menu
          },
          null
        )

        return false
      }

      $selectedAlphaSortButton.click(function() {
        return _onAlphaSort(selfColumnPicker.$selectedItems, $(this))
      })

      $availableAlphaSortButton.click(function() {
        return _onAlphaSort(selfColumnPicker.$availableItems, $(this))
      })

      $mainContainer
        .append($selectedItemsContainer)
        .append($availableItemsContainer)
        .append(cadc.vot.picker.CLEAR_BLOCK)

      selfColumnPicker.$dialog
        .find('.column_manager_columns')
        .append($mainContainer)

      selfColumnPicker.$dialog.on('popupbeforeposition', function() {
        initMenus()
      })

      /**
       * Clear the list item of state CSS.
       *
       * @param _$li    The jQuery list item.
       * @private
       */
      var _clearListItem = function(_$li) {
        _$li.removeClass('add_it').removeClass('remove_it')
      }

      /**
       * Function issued when the jQuery UI's Sortable menu feature has
       * ended.
       *
       * @param event   The event object.
       * @param ui      The ui object.
       */
      var onDrop = function(event, ui) {
        var $liItem = $(ui.item[0])
        var itemChecked =
          ui.sender[0].id === selfColumnPicker.$availableItems.attr('id')
        $liItem
          .find("input[id='column-picker-" + $liItem.data('column-id') + "']")
          .prop('checked', itemChecked)
        _clearListItem($liItem)
        setColumns()
      }

      /**
       * On hover of a sortable list.
       *
       * @param event   The event object.
       * @param ui      The ui object.
       */
      var onHover = function(event, ui) {
        var $liItem = $(ui.item[0])

        // Looking for the opposite as this is on OVER.
        var itemChecked = !$liItem.find(':checkbox').prop('checked')

        // Only change the style if it's moving to a different menu.
        if (ui.sender) {
          // The same menu.
          if (ui.sender[0].id === $(this).attr('id')) {
            _clearListItem($liItem)
          } else {
            $liItem.addClass(itemChecked === true ? 'add_it' : 'remove_it')
          }
        } else {
          _clearListItem($liItem)
        }

        return true
      }

      /**
       * Stop sorting for the selected items only.
       *
       * @param event   The event object.
       * @param ui      The ui object.
       */
      var onStop = function(event, ui) {
        _clearListItem($(ui.item[0]))
        setColumns()
      }

      var selectedItemsOptions = $.extend(
        {},
        {
          connectWith: '#' + selfColumnPicker.$availableItems.attr('id'),
          receive: onDrop,
          stop: onStop,
          over: onHover,
          appendTo: selfColumnPicker.$dialog
        },
        cadc.vot.picker.defaultSortableOptions
      )
      var availableItemsOptions = $.extend(
        {},
        {
          connectWith: '#' + selfColumnPicker.$selectedItems.attr('id'),
          receive: onDrop,
          over: onHover,
          appendTo: selfColumnPicker.$dialog
        },
        cadc.vot.picker.defaultSortableOptions
      )

      selfColumnPicker.$selectedItems.sortable(selectedItemsOptions)
      selfColumnPicker.$availableItems.sortable(availableItemsOptions)

      selfColumnPicker.$dialog
        .find(getOption(cadc.vot.picker.DIALOG_CLOSE_SELECTOR_KEY))
        .click(function() {
          selfColumnPicker.$dialog.popup('close')
          return false
        })

      /*
       *************************************
       *
       * Top button handling.
       *
       *************************************
       */
      $resetSpan.click(function() {
        resetMenus()

        trigger(cadc.vot.picker.events.onResetColumnOrder, null, null)

        return false
      })

      $showAllSpan.click(function() {
        var $items = selfColumnPicker.$availableItems.find('li')

        selfColumnPicker.$selectedItems.append($items)
        $items.find(':checkbox').prop('checked', true)

        selfColumnPicker.$availableItems.empty()

        trigger(cadc.vot.picker.events.onShowAllColumns, null, null)

        return false
      })

      selfColumnPicker.$availableItems.disableSelection()
      selfColumnPicker.$selectedItems.disableSelection()
    }

    /**
     * Obtain an option value by its key.
     * @param _key      The name of the option.
     * @returns {*}     The value of the option, or null if not found.
     */
    function getOption(_key) {
      return selfColumnPicker.options.hasOwnProperty(_key)
        ? selfColumnPicker.options[_key]
        : null
    }

    /**
     * Obtain the column object for the given column ID.
     * @param _colID    The ID of the column to look for.
     *
     * @return {Object} Column object.
     */
    function getColumn(_colID) {
      for (
        var aci = 0, acl = selfColumnPicker.allColumns.length;
        aci < acl;
        aci++
      ) {
        var nextColumn = selfColumnPicker.allColumns[aci]
        if (nextColumn.id == _colID) {
          return nextColumn
        }
      }

      return null
    }

    /**
     * Build the columns menus.
     *
     **/
    function addMenuItems(_gridColumns) {
      // Displayed columns.
      //var gridColumns = selfColumnPicker.originalDisplayedColumns;
      //var gridColumns = _grid.getColumns();

      for (var gi = 0, gl = _gridColumns.length; gi < gl; gi++) {
        var nextSelectedColumn = _gridColumns[gi]

        if (nextSelectedColumn.id != cadc.vot.picker.CHECKBOX_ID) {
          selfColumnPicker.$selectedItems.append(
            createColumnDOM(nextSelectedColumn, true)
          )
        }
      }

      // Get the rest.
      var availableCols = new cadc.web.util.Array(
        selfColumnPicker.allColumns
      ).subtract(function(element /*, index, array*/) {
        for (var ii = 0, gcl = _gridColumns.length; ii < gcl; ii++) {
          if (_gridColumns[ii].id == element.id) {
            return false
          }
        }

        return true
      })

      for (var i = 0, l = availableCols.length; i < l; i++) {
        var nextAvailableColumn = availableCols[i]

        // Should never happen since the checkbox column is never not in the
        // 'selected' menu list, but here we are anyway.
        if (nextAvailableColumn.id != cadc.vot.picker.CHECKBOX_ID) {
          selfColumnPicker.$availableItems.append(
            createColumnDOM(nextAvailableColumn, false)
          )
        }
      }
    }

    /**
     * Append the given column to the appropriate DOM menu.
     * @param _col           The column object.
     * @param __isDisplayed  Whether the given column is displayed or not.
     */
    function createColumnDOM(_col, __isDisplayed) {
      var $li = $('<li class="ui-state-default"></li>')
      $li.prop('id', 'ITEM_' + _col.id)
      $li.data('column-id', _col.id)

      var $input = $(
        "<input type='checkbox' id='column-picker-" +
          _col.id +
          "' name='column-picker-" +
          _col.id +
          "' />"
      ).data('column-id', _col.id)

      $input.prop('checked', __isDisplayed)

      // Occurs after the actual checkbox is modified (changed).
      $input.change(function() {
        var $listItem = $(this)
          .parent()
          .parent()

        // Add the clone to its destination.
        $listItem.appendTo(
          this.checked
            ? selfColumnPicker.$selectedItems
            : selfColumnPicker.$availableItems
        )

        // Refresh the list.
        setColumns()
      })

      var $columnLabel = $(
        "<div class='slick-column-picker-label-text'></div>"
      ).text(_col.name)
      $columnLabel.prop('id', 'LABEL_' + _col.id)

      $columnLabel.prepend($input)
      $columnLabel.appendTo($li)

      return $li
    }

    /**
     * Construct the unordered list of items.
     */
    function initMenus() {
      selfColumnPicker.$selectedItems.empty()
      selfColumnPicker.$availableItems.empty()

      addMenuItems(_grid.getColumns())
    }

    /**
     * Construct the unordered list of items.
     */
    function resetMenus() {
      selfColumnPicker.$selectedItems.empty()
      selfColumnPicker.$availableItems.empty()

      addMenuItems(selfColumnPicker.originalDisplayedColumns)
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param evt     The Event to fire.
     * @param args    Arguments to the event.
     * @param e       Event data.
     * @returns {*}   The event notification result.
     */
    function trigger(evt, args, e) {
      e = e || new Slick.EventData()
      args = args || {}
      return evt.notify(args, e, selfColumnPicker)
    }

    function updateColumns() {
      trigger(
        cadc.vot.picker.events.onSort,
        {
          visibleColumns: selfColumnPicker.grid.getColumns()
        },
        null
      )

      trigger(
        cadc.vot.picker.events.onColumnAddOrRemove,
        {
          visibleColumns: selfColumnPicker.grid.getColumns()
        },
        null
      )
    }

    /**
     * Commit the selected columns to the grid.
     */
    function setColumns() {
      var $selectedLIs = selfColumnPicker.$selectedItems.find('li')
      var selectedCols = []

      // Prepend the checkbox.
      selectedCols.push(selfColumnPicker.checkboxColumn)

      for (var slii = 0, slil = $selectedLIs.length; slii < slil; slii++) {
        selectedCols.push(getColumn($($selectedLIs[slii]).data('column-id')))
      }

      selfColumnPicker.grid.setColumns(selectedCols)
    }

    $.extend(this, {
      /**
       * Used by the Viewer to set units and other meaningful data things on
       * a column.
       *
       * @param _colID        The ID of the column.
       * @param _dataKey      The key to set on the data.
       * @param _dataObject   The item to set for the value of the data.
       */
      updateColumnData: function(_colID, _dataKey, _dataObject) {
        $.each(selfColumnPicker.allColumns, function(cI, cO) {
          if (cO.id == _colID) {
            $(cO).data(_dataKey, _dataObject)
          }
        })

        selfColumnPicker.updateColumns()
      },
      updateColumns: updateColumns
    })

    init()
  }
})(jQuery, window)
