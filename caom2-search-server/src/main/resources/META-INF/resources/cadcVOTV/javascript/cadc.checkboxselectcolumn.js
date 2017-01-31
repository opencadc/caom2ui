(function ($)
{
  // register namespace
  $.extend(true, window, {
    "CADC": {
      "CheckboxSelectColumn": CheckboxSelectColumn
    }
  });


  function CheckboxSelectColumn(options)
  {
    var _grid;
    var _self = this;
    var _handler = new Slick.EventHandler();
    var _selectedRowsLookup = {};
    var _defaults = {
      columnId: "_checkbox_selector",
      cssClass: null,
      toolTip: "Select/Deselect All",
      enableOneClickDownload: false,
      oneClickDownloadTitle: null,
      oneClickDownloadURL: null,
      width: 30
    };

    this.options = $.extend(true, {}, _defaults, options);

    function init(grid)
    {
      _grid = grid;
      _handler
          .subscribe(_grid.onSelectedRowsChanged, handleSelectedRowsChanged)
          .subscribe(_grid.onHeaderClick, handleHeaderClick)
          .subscribe(_grid.onKeyDown, handleKeyDown);
    }

    function destroy()
    {
      _handler.unsubscribeAll();
    }

    function handleSelectedRowsChanged(e, args)
    {
      var selectedRows = _grid.getSelectedRows();

      var lookup = {};
      for (var r = 0, ri = selectedRows.length; r < ri; r++)
      {
        var row = selectedRows[r];
        lookup[row] = true;
        if (lookup[row] !== _selectedRowsLookup[row])
        {
          _grid.invalidateRow(row);
          delete _selectedRowsLookup[row];
        }
      }

      for (var rowKey in _selectedRowsLookup)
      {
        _grid.invalidateRow(rowKey);
      }

      _selectedRowsLookup = lookup;

      _grid.render();

      var disabledRowCount=0;
      for (var i = 0; i < _grid.getDataLength(); i++)
      {
        _grid.getDataItem(i)[cadc.vot.ROW_SELECT_DISABLED_KEY]
          ? disabledRowCount++ : null;
      }

      var rowChecked = selectedRows.length
                       && (selectedRows.length == (_grid.getDataLength()
                                                   - disabledRowCount));

      _grid.updateColumnHeader(_self.options.columnId,
                               getHeaderCheckboxLabel() +
                               "<input type='checkbox' class='slick-header-column-checkboxsel'"
                               + (rowChecked ? " checked='checked'" : "") + " >",
                               _self.options.toolTip);
    }

    function getCheckboxLabel()
    {
      return _self.options.checkboxLabel ? _self.options.checkboxLabel : "";
    }

    function getHeaderCheckboxLabel()
    {
      return _self.options.headerCheckboxLabel ? _self.options.headerCheckboxLabel : "";
    }

    function handleKeyDown(e, args)
    {
      if (e.which == 32)
      {
        if (_grid.getColumns()[args.cell].id === _self.options.columnId)
        {
          // if editing, try to commit
          if (!_grid.getEditorLock().isActive()
              || _grid.getEditorLock().commitCurrentEdit())
          {
            toggleRowSelection(args.row);
          }
          e.preventDefault();
          e.stopImmediatePropagation();
        }
      }
    }

    function toggleRowSelection(row)
    {
      if (_selectedRowsLookup[row])
      {
        deselect(row);
      }
      else
      {
        select(row);
      }
    }

    function deselect(row)
    {
      _grid.setSelectedRows($.grep(_grid.getSelectedRows(), function (n)
      {
        return n != row;
      }));
    }

    function isOneClickDownloadEnabled()
    {
      return _self.options.enableOneClickDownload === true;
    }

    function select(row)
    {
      _grid.setSelectedRows(_grid.getSelectedRows().concat(row));
    }

    function handleHeaderClick(e, args)
    {
      var $eventTarget = $(e.target);

      if ((args.column.id === _self.options.columnId)
          && $eventTarget.is(":checkbox"))
      {
        // if editing, try to commit
        if (_grid.getEditorLock().isActive()
            && !_grid.getEditorLock().commitCurrentEdit())
        {
          e.preventDefault();
        }
        else
        {
          if ($eventTarget.is(":checked"))
          {
            var rows = [];
            for (var i = 0; i < _grid.getDataLength(); i++)
            {
              var disabled =
                  _grid.getDataItem(i)[cadc.vot.ROW_SELECT_DISABLED_KEY];

              if (!disabled)
              {
                rows.push(i);
              }
            }

            _grid.setSelectedRows(rows);
          }
          else
          {
            _grid.setSelectedRows([]);
          }
          e.stopPropagation();
        }

        e.stopImmediatePropagation();
      }
    }

    function getColumnDefinition()
    {
      return {
        id: _self.options.columnId,
        name: getHeaderCheckboxLabel() + "<input type='checkbox' />",
        toolTip: _self.options.toolTip,
        field: "sel",
        width: _self.options.width,
        resizable: false,
        sortable: false,
        headerCssClass: _self.options.headerCssClass,
        cssClass: _self.options.cssClass,
        formatter: checkboxSelectionFormatter
      };
    }

    function checkboxSelectionFormatter(row, cell, value, columnDef,
                                        dataContext)
    {
      if (dataContext)
      {
        var thisID = dataContext["id"];
        var cellOutput =
          "<input class='_select_" + thisID + "' type='checkbox' "
          + (_selectedRowsLookup[row] ? "checked='checked' " : "") + "/>";

        if (isOneClickDownloadEnabled())
        {
          var linkURL = $.trim(_self.options.oneClickDownloadURL);

          linkURL += "?ID=" + encodeURIComponent(
              dataContext[_self.options.oneClickDownloadURLColumnID]);

          cellOutput +=
            "<a id='_one-click_" + thisID + "' href='" + linkURL + "'"
            + (_self.options.oneClickDownloadTitle
              ? " title='" + _self.options.oneClickDownloadTitle + "'": "")
            + " class='no-propagate-event'><span class='wb-icon-drive-download margin-left-small no-propagate-event'></span></a>";
        }

        return cellOutput;
      }
      else
      {
        return null;
      }
    }

    $.extend(this, {
      "init": init,
      "destroy": destroy,
      "getColumnDefinition": getColumnDefinition
    });
  }
})(jQuery);
