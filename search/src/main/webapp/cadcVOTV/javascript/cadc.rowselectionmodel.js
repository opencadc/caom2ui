(function ($)
{
  // register namespace
  $.extend(true, window, {
    "CADC": {
      "RowSelectionModel": CADCRowSelectionModel
    }
  });

  function CADCRowSelectionModel(options)
  {
    var _grid;
    var _ranges = [];
    var _self = this;
    var _handler = new Slick.EventHandler();
    var _inHandler;
    var _options;
    var _defaults = {
      selectActiveRow: true,
      selectClickedRow: false,
      propagateEvents: false
    };

    function init(grid)
    {
      _options = $.extend(true, {}, _defaults, options);
      _grid = grid;
      _handler.subscribe(_grid.onActiveCellChanged,
                         wrapHandler(handleActiveCellChange));
      _handler.subscribe(_grid.onKeyDown,
                         wrapHandler(handleKeyDown));
      _handler.subscribe(_grid.onClick,
                         wrapHandler(handleClick));
    }

    function destroy()
    {
      _handler.unsubscribeAll();
    }

    function wrapHandler(handler)
    {
      return function ()
      {
        if (!_inHandler)
        {
          _inHandler = true;
          handler.apply(this, arguments);
          _inHandler = false;
        }
      };
    }

    function rangesToRows(ranges)
    {
      var rows = [];
      for (var i = 0; i < ranges.length; i++)
      {
        for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++)
        {
          rows.push(j);
        }
      }
      return rows;
    }

    function rowsToRanges(rows)
    {
      var ranges = [];
      var lastCell = _grid.getColumns().length - 1;
      for (var i = 0; i < rows.length; i++)
      {
        ranges.push(new Slick.Range(rows[i], 0, rows[i], lastCell));
      }
      return ranges;
    }

    function getRowsRange(from, to)
    {
      var i, rows = [];
      for (i = from; i <= to; i++)
      {
        rows.push(i);
      }
      for (i = to; i < from; i++)
      {
        rows.push(i);
      }
      return rows;
    }

    function getSelectedRows()
    {
      return rangesToRows(_ranges);
    }

    function setSelectedRows(rows)
    {
      setSelectedRanges(rowsToRanges(rows));
    }

    function setSelectedRanges(ranges)
    {
      _ranges = ranges;
      _self.onSelectedRangesChanged.notify(_ranges);
    }

    function getSelectedRanges()
    {
      return _ranges;
    }

    function refreshSelectedRanges()
    {
      var rowsFromRanges = getSelectedRows();
      setSelectedRows(rowsFromRanges);
    }
    
    function isCellSelectable(cell)
    {
      return _grid.getDataItem(cell.row)[cadc.vot.ROW_SELECT_DISABLED_KEY]
             !== true;
    }

    function handleActiveCellChange(e, data)
    {
      if (_options.selectActiveRow && data.row != null)
      {
        setSelectedRanges([new Slick.Range(data.row, 0, data.row,
                                           _grid.getColumns().length - 1)]);
      }
    }

    function handleKeyDown(e)
    {
      var activeRow = _grid.getActiveCell();
      if (activeRow && e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey
          && (e.which == 38 || e.which == 40))
      {
        var selectedRows = getSelectedRows();
        selectedRows.sort(function (x, y)
                          {
                            return x - y
                          });

        if (!selectedRows.length)
        {
          selectedRows = [activeRow.row];
        }

        var top = selectedRows[0];
        var bottom = selectedRows[selectedRows.length - 1];
        var active;

        if (e.which == 40)
        {
          active = activeRow.row < bottom || top == bottom ? ++bottom : ++top;
        }
        else
        {
          active = activeRow.row < bottom ? --bottom : --top;
        }

        if (active >= 0 && active < _grid.getDataLength())
        {
          _grid.scrollRowIntoView(active);
          _ranges = rowsToRanges(getRowsRange(top, bottom));
          setSelectedRanges(_ranges);
        }

        e.preventDefault();
        e.stopPropagation();
      }
    }

    function isTargetAllowRowSelection($target)
    {
      return !$target.hasClass("no-row-select");
    }

    function handleClick(e)
    {
      // Enable highlighting of the selected row.
      _grid.getOptions().enableCellNavigation = true;

      var continueOn;
      
      var cell = _grid.getCellFromEvent(e);
      var $target = $(e.target);

      if (cell && _grid.canCellBeActive(cell.row, cell.cell)
          && isCellSelectable(cell))
      {
        var selection = rangesToRows(_ranges);
        var idx = $.inArray(cell.row, selection);
        var activeCell = _grid.getActiveCell();

        if (_grid.getOptions().multiSelect)
        {
          if (idx === -1) // not selected
          {
            if ((activeCell) && (activeCell.row === cell.row) && (cell.cell !== 0))
            {
              _grid.resetActiveCell();
            }
            else
            {
              selection.push(cell.row);
              _grid.setActiveCell(cell.row, cell.cell);
            }
          }
          else if (idx !== -1) // un-check the checkbox
          {
            selection = $.grep(selection, function (o, i)
            {
              return (o !== cell.row);
            });
            _grid.setActiveCell(cell.row, cell.cell);
          }
          else if (selection.length)
          {
            var last = selection.pop();
            var from = Math.min(cell.row, last);
            var to = Math.max(cell.row, last);
            selection = [];
            for (var i = from; i <= to; i++)
            {
              if (i !== last)
              {
                selection.push(i);
              }
            }
            selection.push(last);
            _grid.setActiveCell(cell.row, cell.cell);
          }
        }

        var shouldNotPropagate = ((_options.propagateEvents === false)
                                  || $target.is(":checkbox")
                                  || $target.hasClass("no-propagate-event"));

        if ($target.is(":checkbox") || (_options.selectClickedRow
                                        && isTargetAllowRowSelection($target)))
        {
          _ranges = rowsToRanges(selection);
          setSelectedRanges(_ranges);
        }

        if (shouldNotPropagate)
        {
          e.stopImmediatePropagation();
        }

        continueOn = !shouldNotPropagate;
      }
      else
      {
        // Disable highlighting of the selected row.
        _grid.getOptions().enableCellNavigation = false;
        continueOn = false;
      }

      return continueOn;
    }

    $.extend(this, {
      "getSelectedRows": getSelectedRows,
      "setSelectedRows": setSelectedRows,

      "getSelectedRanges": getSelectedRanges,
      "setSelectedRanges": setSelectedRanges,

      "refreshSelectedRanges": refreshSelectedRanges,

      "init": init,
      "destroy": destroy,

      "onSelectedRangesChanged": new Slick.Event()
    });
  }
})(jQuery);