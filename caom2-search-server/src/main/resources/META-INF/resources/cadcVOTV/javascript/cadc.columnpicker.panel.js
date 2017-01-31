(function ($)
{
  /**
   * New Panel column picker.
   *
   * @param columns   The columns to put.
   * @param grid      The underlying Grid.
   * @param panel     The panel to put the link in.
   * @param tooltipOptions    The options to be passed to the tooltip.
   * @param options   Optional items.
   * @constructor
   */
  function PanelTooltipColumnPicker(columns, grid, panel, tooltipOptions,
                                    options)
  {
    // Cached value to reset to.
    var originalColumns;

    var $changeColumns;
    var $menu;
    var thresholdListItemSelector =
        "li.slick-column-picker-tooltip-threshold";
    var self = this;

    if (!jQuery.fn.tooltip && !!jQuery.fn.powerTip)
    {
      throw "CADC Panel Tooltip Column Picker requires a tooltip library "
          + "(jQuery.tools or jQuery.ui), or the powerTip library to be loaded.";
    }

    var defaults =
    {
      fadeSpeed: 250,
      linkText: "More columns..."
    };

    var defaultTooltipOptions =
    {
      appendTooltipContent: false,
      targetSelector: ".tooltip_content"
    };

    function s4()
    {
      return Math.floor((1 + Math.random()) * 0x10000).
          toString(16).substring(1);
    }

    function guid()
    {
      return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
             s4() + '-' + s4() + s4() + s4();
    }

    function init()
    {
      options = $.extend({}, defaults, options);
      tooltipOptions = $.extend({}, defaultTooltipOptions, tooltipOptions);
      originalColumns = [];
      var gridColumns = grid.getColumns().slice(0);

      $.each(gridColumns, function(gridColumnIndex, gridColumn)
      {
        originalColumns.push(gridColumn);
      });

      if (options.buttonText)
      {
        // Clean up first.
        panel.find("button.slick-columnpicker-panel-change-column-label").remove();
        var $changeColumnHolder = $("<span class='slick-columnpicker-panel-change-column-holder'></span>").appendTo(panel.children());
        $changeColumns = $("<button type='button' name='slick-columnpicker-panel-change-column' class='slick-columnpicker-panel-change-column-label button'></button>").appendTo($changeColumnHolder);
        $changeColumns.text(options.buttonText);
        $changeColumns.click(function (e)
        {
          buildTooltipPicker(e);
          var $thisButton = $(this);
          var tip = $thisButton.data("tooltip");
          if (tip.isShown(true))
          {
            tip.hide();
          }
          else
          {
            tip.show();
          }
          e.stopImmediatePropagation();
          return false;
        }).on("mouseleave", function(e) {
          return e.stopImmediatePropagation();
        });
      }
      else
      {
        // Clean up first.
        panel.find("a.slick-columnpicker-panel-change-column-label").remove();
        var $changeColumnHolder = $("<span class='slick-columnpicker-panel-change-column-holder'></span>").appendTo(panel.children());
        $changeColumns = $("<a name='slick-columnpicker-panel-change-column' class='slick-columnpicker-panel-change-column-label'></a>").appendTo($changeColumnHolder);
        $changeColumns.text(options.linkText);
        $changeColumns.mouseover(function (e)
        {
          buildTooltipPicker(e);
        });
      }

      // Used to support the outdated jQuery.tools tooltip.
      if (tooltipOptions.appendTooltipContent)
      {
        $(tooltipOptions.tooltipContent).remove();
        $changeColumns.after(tooltipOptions.tooltipContent);
      }

      // Assume default tooltip attachment.
      if (!tooltipOptions.tooltipInit)
      {
        $changeColumns.tooltip(tooltipOptions);
      }
      else
      {
        tooltipOptions.tooltipInit($changeColumns, tooltipOptions);
      }

      if (tooltipOptions.tooltipContent)
      {
        $(tooltipOptions.tooltipContent).find(".tooltip-close").click(
            function(e)
            {
              $changeColumns.data("tooltip").hide();
            });
      }

      // Clean up existing button holder.
      $(tooltipOptions.targetSelector).empty();

      var $buttonHolder = $("<div class='slick-column-picker-tooltip-button-holder'></div>").appendTo(tooltipOptions.targetSelector);

      var $showAllSpan = $("<span class='slick-column-picker-button'>Show all columns</span>").appendTo($buttonHolder);
      var $resetSpan = $("<span class='slick-column-picker-button'>Reset column order</span>").appendTo($buttonHolder);
      var $orderAlphaSpan = $("<span class='slick-column-picker-button'>Order alphabetically</span>").appendTo($buttonHolder);

      $resetSpan.click(function(e)
                       {
                         grid.setColumns(originalColumns.slice(0));
                         grid.invalidate();
                         grid.resizeCanvas();
                         buildTooltipPicker(e);
                         $menu.sortable("refresh");

                         trigger(self.onResetColumnOrder, null, null);
                       });

      $showAllSpan.click(function(e)
                         {
                           var colIDs = [];
                           var gridCols = grid.getColumns().slice(0);
                           var allCols = [];

                           $.each(gridCols, function(gcKey, gColDef)
                           {
                             colIDs.push(gColDef.id);
                             if(isVisible(gColDef.id))
                             {
                               allCols.push(gColDef);
                             }
                           });

                           $.each(columns, function(colKey, colDef)
                           {
                             var colID = colDef.id;
                             var isInGrid = false;

                             $.each(colIDs, function(ccKey, cColID)
                             {
                               if (cColID == colID)
                               {
                                 isInGrid = true;
                               }
                             });

                             if ((!isInGrid) && (isVisible(colID)))
                             {
                               allCols.push(colDef);
                             }
                           });

                           grid.setColumns(allCols);
                           grid.invalidate();
                           grid.resizeCanvas();
                           buildTooltipPicker(e);
                           $menu.sortable("refresh");

                           trigger(self.onShowAllColumns, null, null);
                         });

      $orderAlphaSpan.click(function(e)
                            {
                              var arr = grid.getColumns().slice(0);
                              arr.sort(function(o1, o2)
                                       {
                                         var lowerO1Name =
                                             o1.name.toLowerCase();
                                         var lowerO2Name =
                                             o2.name.toLowerCase();
                                         return lowerO1Name > lowerO2Name
                                                ? 1 : lowerO1Name < lowerO2Name
                                                      ? -1 : 0;
                                       });

                              grid.setColumns(arr);
                              grid.invalidate();
                              grid.resizeCanvas();
                              buildTooltipPicker(e);
                              $menu.sortable("refresh");

                              trigger(self.onSortAlphabetically, null, null);
                            });

      $menu = $("<ul class='slick-columnpicker slick-columnpicker-tooltip' />").
          appendTo(tooltipOptions.targetSelector);

      $menu.prop("id", guid());

      $menu.sortable({
                       opacity: 0.8,
                       containment: "parent",
                       tolerance: "pointer",
                       axis: "y",
                       helper: "original",
                       stop: function (e, ui)
                       {
                         var $checkbox = ui.item.find(":checkbox");
                         var $liItems = $menu.find("li");

                         var thisItemIndex = $liItems.index(ui.item);
                         var thresholdIndex = $liItems.index($(thresholdListItemSelector));

                         if (thisItemIndex < thresholdIndex)
                         {
                           // This item is above the threshold line.
                           $checkbox.prop("checked", true);
                         }
                         else if (thisItemIndex != thresholdIndex)
                         {
                           // This item is below the threshold line.
                           $checkbox.prop("checked", false);
                         }

                         updateColumns();
                         e.stopPropagation();
                       }
                     });


      $menu.bind("sortstart", function (event, ui)
      {
        ui.helper.css('margin-top', $(window).scrollTop());
      });

      $menu.bind("sortbeforestop", function (event, ui)
      {
        ui.helper.css('margin-top', 0);
      });

      $menu.disableSelection();
    }

    function getColumnOptions(_id)
    {
      return grid.getOptions().columnOptions[_id];
    }

    function isVisible(_id)
    {
      var colOpts = getColumnOptions(_id);
      return colOpts ?
             ((colOpts.visible !== undefined) ? colOpts.visible : true) :
             true;
    }

    function addColumns(cols)
    {
      $.each(cols, function(cindex, nextCol)
      {
        var $li = $("<li class=\"ui-state-default\"></li>").appendTo($menu);
        $li.prop("id", "ITEM_" + nextCol.id);
        $li.data("column-id", nextCol.id);

        // Omit the checkbox column and invisible fields
        if ((nextCol.id == "_checkbox_selector") || (!isVisible(nextCol.id)))
        {
          $li.hide();
        }

        var $input = $("<input type='checkbox' name='column-picker-"
                           + nextCol.id + "' />").data("column-id", nextCol.id);

        // Occurrs after the actual checkbox is checked.
        $input.change(function (e)
                      {
                        var $checkbox = $(this);
                        var $listItem = $checkbox.parent().parent();

                        if (!$checkbox.is(":checked"))
                        {
                          $menu.find(thresholdListItemSelector).after($listItem);
                          $listItem.find(":checkbox").prop("checked", false);
                        }
                        else
                        {
                          $menu.find(thresholdListItemSelector).before($listItem);
                          $listItem.find(":checkbox").prop("checked", true);
                        }

                        // Refresh the list.
                        $menu.sortable("refresh");

                        updateColumns();
                      });

        if (grid.getColumnIndex(nextCol.id))
        {
          $input.prop("checked", true);
        }

        var $columnLabel =
            $("<div class='slick-column-picker-label-text'></div>").text(
                nextCol.name);
        $columnLabel.prop("id", "LABEL_" + nextCol.id);

        var $columnUnit =
            $("<div class='slick-column-picker-unit-label-text'></div>").text(
                nextCol.unit || "");
        $columnUnit.prop("id", "_UNIT_" + nextCol.id);

        var $columnDescription =
            $("<div class='slick-column-picker-description-label-text'></div>").text(
                nextCol.description);
        $columnDescription.prop("id", "_DESC_" + nextCol.id);

        $columnLabel.prepend($input);

        $columnLabel.appendTo($li);
        $columnUnit.appendTo($li);
        $columnDescription.appendTo($li);
      });
    }

    function buildTooltipPicker(e)
    {
      $menu.empty();

      var displayedColumns = grid.getColumns();

      addColumns(displayedColumns);

      $("<li class='slick-column-picker-tooltip-threshold'><hr class='slick-column-picker-tooltip-threshold-line' /></li>").appendTo($menu);

      // What's left after the displayed columns.
      var remainingCols = [];

      $.each(columns, function(index, col)
      {
        var isDisplayed = false;

        $.each(displayedColumns, function(dIndex, dCol)
        {
          if (dCol.id == col.id)
          {
            isDisplayed = true;
          }
        });

        if (isDisplayed === false)
        {
          remainingCols.push(col);
        }
      });

      addColumns(remainingCols);

      $menu.parent().css("top", e.pageY).css("left", e.pageX);
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param evt     The Event to fire.
     * @param args    Arguments to the event.
     * @param e       Event data.
     * @returns {*}   The event notification result.
     */
    function trigger(evt, args, e)
    {
      e = e || new Slick.EventData();
      args = args || {};
      args.grid = grid;
      return evt.notify(args, e, self);
    }

    function updateColumns()
    {
      var previousItems = $menu.find(thresholdListItemSelector).prevAll("li");
      var previousItemCount = previousItems.length;
      var visibleColumns = [];

      $.each(previousItems, function (i, li)
      {
        var $listItem = $(li);
        var $listItemCheckbox = $listItem.find(":checkbox");

        $listItemCheckbox.prop("checked", true);

        var listItemColumnID = $listItem.data("column-id");

        $.each(columns, function(cI, cO)
        {
          if (cO.id == listItemColumnID)
          {
            visibleColumns[(previousItemCount - 1) - i] = cO;
          }
        });
      });

      if (visibleColumns.length)
      {
        grid.setColumns(visibleColumns);
        grid.invalidate();
      }

      var nextItems = $(thresholdListItemSelector).nextAll();
      $.each(nextItems, function (n, nli)
      {
        var $listItem = $(nli);
        $listItem.find(":checkbox").prop("checked", false);
      });

      trigger(self.onSort,
              {
                "visibleColumns": visibleColumns
              }, null);

      trigger(self.onColumnAddOrRemove,
              {
                "visibleColumns": visibleColumns
              }, null);
    }

    $.extend(this,
             {
               "updateColumnData": function(_colID, _dataKey, _dataObject)
               {
                 $.each(columns, function(cI, cO)
                 {
                   if (cO.id == _colID)
                   {
                     $(cO).data(_dataKey, _dataObject);
                   }
                 });

                 self.updateColumns();
               },
               "updateColumns": updateColumns,
               "onColumnAddOrRemove": new Slick.Event(),
               "onSort": new Slick.Event(),
               "onResetColumnOrder": new Slick.Event(),
               "onShowAllColumns": new Slick.Event(),
               "onSortAlphabetically": new Slick.Event()
             });

    init();
  }

  // Slick.Controls.PanelTooltipColumnPicker
  $.extend(true, window,
           {
             Slick: {
               Controls: {
                 PanelTooltipColumnPicker: PanelTooltipColumnPicker
               }
             }
           });
})(jQuery);
