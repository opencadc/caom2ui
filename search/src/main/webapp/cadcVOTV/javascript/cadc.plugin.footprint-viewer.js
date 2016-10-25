(function ($)
{
  if (typeof A === "undefined")
  {
    // Require AladinLite.
    throw new Error("AladinLite must be present.  (http://aladin.u-strasbg.fr/AladinLite/)");
  }

  // register namespace
  $.extend(true, window, {
    "cadc": {
      "vot": {
        "plugin": {
          "footprint": AladinLiteFootprintViewer
        }
      }
    }
  });


  /**
   * AladinLite footprint viewer.  This is incorporated as a Plugin to allow
   *
   * @constructor
   */
  function AladinLiteFootprintViewer(_inputs)
  {
    var PI_OVER_180 = Math.PI / 180.0;
    var DEG_PER_ARC_SEC = 1.0 / 3600.0;
    var POLYGON_SPLIT = "Polygon ICRS";
    var DEFAULT_FOV_DEGREES = 180;

    var _self = this;
    var _defaults = {
      targetSelector: "#aladin-lite",
      toggleSwitchSelector: null,     // Always show by default.
      toggleClose: function ($toggleSelector)
      {
        $toggleSelector.data("close", $toggleSelector.html());
        $toggleSelector.html($toggleSelector.data("open"));
      },
      toggleOpen: function ($toggleSelector)
      {
        $toggleSelector.data("open", $toggleSelector.html());
        $toggleSelector.html($toggleSelector.data("close"));
      },
      aladin_options: {},  // Specific options for AladinLite.
      renderedRowsOnly: true,
      footprintFieldID: "footprint",
      raFieldID: "ra",
      decFieldID: "dec",
      fovFieldID: "fov",
      colour: "orange",
      navigateToSelected: true,
      highlightColour: "yellow",
      /**
       * Perform further calculations on the FOV before setting it.  Useful
       * for further reducing it (e.g. from square degrees to degrees).
       * @param {Number} fovValue
       * @return  {Number}
       */
      afterFOVCalculation: function (fovValue)
      {
        return fovValue;
      },
      onHover: true,
      onClick: false,
      coords: [1000, -1000, 0, 0]   // Remember to slice this!
    };

    this.grid = null;

    /**
     * {cadc.vot.Viewer}
     */
    this.viewer = null;
    this.handler = new Slick.EventHandler();

    var inputs = $.extend(true, {}, _defaults, _inputs);

    this.footprintFieldID = inputs.footprintFieldID;
    this.raFieldID = inputs.raFieldID;
    this.decFieldID = inputs.decFieldID;
    this.fovFieldID = inputs.fovFieldID;
    this.$target = $(inputs.targetSelector);

    //
    // Declare AladinLite
    //
    this.aladin = null;

    // footprint overlay, public data
    this.aladinOverlay = null;
    //
    // End declaration of AladinLite
    //

    // currently 'active' (hover/click) row
    //
    this.currentFootprint = null;

    // Start at this location.  Reset when re-rendering.
    this.defaultRA = null;
    this.defaultDec = null;

    // Field of View calculation
    // For FOV computation.
    this.DEC = null;
    this.RA0 = null;
    this.RA180 = null;
    this.fieldOfViewSetFlag = false;

    /**
     * Initialize with the Slick Grid instance.
     * @param _viewer{cadc.vot.Viewer}      The CADC VOTable Viewer instance.
     */
    function init(_viewer)
    {
      destroy();

      if (inputs.toggleSwitchSelector != null)
      {
        _self.$target.hide();
        var $toggleSwitchSelector = $(inputs.toggleSwitchSelector);

        if ($toggleSwitchSelector.data("open") != null)
        {
          $toggleSwitchSelector.html($toggleSwitchSelector.data("open"));
        }

        $toggleSwitchSelector.on("click", function (e)
        {
          e.preventDefault();

          _self.$target.toggle();

          if (_self.$target.is(":visible"))
          {
            inputs.toggleOpen($(this), _self.$target);
          }
          else
          {
            inputs.toggleClose($(this), _self.$target);
          }

          return false;
        });
      }

      _self.viewer = _viewer;
      _self.grid = _viewer.getGrid();
      _self.aladin = A.aladin(inputs.targetSelector, inputs.aladin_options);
      _self.aladinOverlay =
        A.graphicOverlay({color: inputs.colour, lineWidth: 3});
      _self.DEC = _defaults.coords.slice(0);
      _self.RA0 = _defaults.coords.slice(0);
      _self.RA180 = _defaults.coords.slice(0);
      _self.aladin.addOverlay(_self.aladinOverlay);
      _self.currentFootprint = A.graphicOverlay({
                                                  name: "current",
                                                  color: inputs.highlightColour,
                                                  lineWidth: 5
                                                });
      _self.aladin.addOverlay(_self.currentFootprint);

      if (inputs.fov != null)
      {
        _self.aladin.setFoV(inputs.fov);
      }

      if (_self.grid.getData().getLength)
      {
        if (inputs.renderedRowsOnly === true)
        {
          _self.handler.subscribe(_self.grid.onRenderComplete,
                                  handleRenderComplete);
        }
        else
        {
          _self.viewer.subscribe(cadc.vot.events.onRowAdded,
                                 handleAddFootprint);

          _self.viewer.subscribe(cadc.vot.events.onDataLoaded,
                                 function ()
                                 {
                                   _setFieldOfView();
                                 });

          _self.viewer.subscribe(cadc.vot.events.onFilterData,
                                 function (event, args)
                                 {
                                   reset();

                                   var v = args.application;
                                   var data = v.getGrid().getData();
                                   var currentRows = data.getRows();

                                   for (var cdi = 0, cdl = currentRows.length;
                                        cdi < cdl; cdi++)
                                   {
                                     handleAddFootprint(event, {
                                       rowData: data.getItemByIdx(cdi)
                                     });
                                   }

                                   _setFieldOfView();
                                 });
        }
      }

      if (inputs.onHover === true)
      {
        _self.handler.subscribe(_self.grid.onMouseEnter, handleMouseEnter);
        _self.handler.subscribe(_self.grid.onMouseLeave, handleMouseLeave);
      }

      if (inputs.onClick === true)
      {
        _self.handler.subscribe(_self.grid.onClick, handleClick);
      }
    }

    function _resetCurrent()
    {
      if (_self.currentFootprint)
      {
        _self.currentFootprint.removeAll();
      }
    }

    function reset()
    {
      _self.aladinOverlay.removeAll();
      _self.currentFootprint.removeAll();

      _self.fieldOfViewSetFlag = false;
      _self.defaultRA = null;
      _self.defaultDec = null;
      _self.DEC = _defaults.coords.slice(0);
      _self.RA0 = _defaults.coords.slice(0);
      _self.RA180 = _defaults.coords.slice(0);

      _resetCurrent();
    }

    function destroy()
    {
      _self.handler.unsubscribeAll();
      _self.aladin = null;
      _self.aladinOverlay = null;
      _self.currentFootprint = null;
      _self.$target.empty();
      _self.defaultDec = null;
      _self.defaultRA = null;
      _self.DEC = null;
      _self.RA0 = null;
      _self.RA180 = null;
      _self.fieldOfViewSetFlag = false;

      if (inputs.toggleSwitchSelector != null)
      {
        $(inputs.toggleSwitchSelector).off("click");
      }

      if (_self.viewer != null)
      {
        _self.viewer.unsubscribe(cadc.vot.events.onRowAdded);
        _self.viewer.unsubscribe(cadc.vot.events.onDataLoaded);
      }
    }

    function _calcFOV()
    {
      _self.RA0[2] = (0.5 * (_self.RA0[0] + _self.RA0[1] ));
      _self.RA0[3] = (_self.RA0[1] - _self.RA0[0]);

      _self.RA180[2] = (0.5 * (_self.RA180[0] + _self.RA180[1] ));
      _self.RA180[3] = (_self.RA180[1] - _self.RA180[0]);

      _self.DEC[2] = (0.5 * (_self.DEC[0] + _self.DEC[1] ));
      _self.DEC[3] = (_self.DEC[1] - _self.DEC[0]);

      var aRA = _self.RA0.slice(0);

      if (_self.RA0[3] > _self.RA180[3])
      {
        _self.RA180[0] = ((_self.RA180[0] + 180.0) % 360.0);
        _self.RA180[1] = ((_self.RA180[1] + 180.0) % 360.0);
        _self.RA180[2] = ((_self.RA180[2] + 180.0) % 360.0);

        aRA = _self.RA180.slice(0);
      }

      return aRA;
    }

    function _calcRowFOV(_decValue, _raValue, _halfFOV)
    {
      var mi = _decValue - _halfFOV;
      var ma = _decValue + _halfFOV;

      if (_self.DEC[0] > mi)
      {
        _self.DEC[0] = mi;
      }

      if (_self.DEC[1] < ma)
      {
        _self.DEC[1] = ma;
      }

      mi = (((_raValue - _halfFOV) + 360.0 ) % 360.0);
      ma = (((_raValue + _halfFOV) + 360.0 ) % 360.0);

      if (_self.RA0[0] > mi)
      {
        _self.RA0[0] = mi;
      }

      if (_self.RA0[1] < ma)
      {
        _self.RA0[1] = ma;
      }

      mi = (mi + 180.0) % 360.0;
      ma = (ma + 180.0) % 360.0;

      if (_self.RA180[0] > mi)
      {
        _self.RA180[0] = mi;
      }

      if (_self.RA180[1] < ma)
      {
        _self.RA180[1] = ma;
      }
    }

    function sanitizeFootprint(nextFootprint)
    {
      var sanitizedFootprint;

      if ((nextFootprint != null) && ($.trim(nextFootprint).length > 0))
      {
        var footprintElements = nextFootprint.split(/\s/);

        for (var fei = 0, fel = footprintElements.length; fei < fel;
             fei++)
        {
          var footprintElement = footprintElements[fei];

          if (isNaN(footprintElement))
          {
            delete footprintElements[fei];
          }
        }

        if (footprintElements.length > 0)
        {
          sanitizedFootprint = POLYGON_SPLIT + footprintElements.join(" ");
        }
        else
        {
          sanitizedFootprint = null;
        }
      }
      else
      {
        sanitizedFootprint = null;
      }

      return sanitizedFootprint;
    }

    function _handleAction(_dataRow)
    {
      var raValue = _dataRow[_self.raFieldID];
      var decValue = _dataRow[_self.decFieldID];

      if ((raValue != null) && ($.trim(raValue) != "") && (decValue != null)
          && ($.trim(decValue) != ""))
      {
        var selectedFootprint =
          sanitizeFootprint(_dataRow[_self.footprintFieldID]);

        if (selectedFootprint != null)
        {
          _self.currentFootprint.addFootprints(
            _self.aladin.createFootprintsFromSTCS(selectedFootprint));

          if (inputs.navigateToSelected === true)
          {
            _self.aladin.gotoRaDec(raValue, decValue);

            var fovValue = _dataRow[_self.fovFieldID];
            if (fovValue != null)
            {
              if (inputs.afterFOVCalculation != null)
              {
                fovValue = inputs.afterFOVCalculation(fovValue);
              }

              _self.aladin.setFoV(fovValue);
            }
          }
        }
        else
        {
          console.warn("Unable to add footprint for (" + raValue + ", "
                       + decValue + ")");
        }
      }
      else
      {
        console.warn("RA and Dec are invalid.");
      }

      if (_self.aladin && _self.aladin.view)
      {
        _self.aladin.view.forceRedraw();
      }
    }

    function handleClick(e, args)
    {
      _resetCurrent();
      _handleAction(args.grid.getDataItem(args.row));
    }

    function handleMouseEnter(e, args)
    {
      _handleAction(args.grid.getDataItem(args.cell.row));
    }

    function handleMouseLeave()
    {
      _resetCurrent();
    }

    function handleAddFootprint(e, args)
    {
      var _row = args.rowData;
      var polygonValue = _row[_self.footprintFieldID];
      var raValue = $.trim(_row[_self.raFieldID]);
      var decValue = $.trim(_row[_self.decFieldID]);

      // Set the default location to the first item we see.
      if ((_self.defaultRA == null) && (raValue != null) && (raValue != ""))
      {
        _self.defaultRA = raValue;
      }

      if ((_self.defaultDec == null) && (decValue != null) && (decValue != ""))
      {
        _self.defaultDec = decValue;
      }

      var halfFOV = 0.5 * DEG_PER_ARC_SEC * _row[_self.fovFieldID];

      if (polygonValue != null)
      {
        var footprintValues = polygonValue.split(POLYGON_SPLIT);
        var footprintValuesLength = footprintValues.length;

        for (var fpvi = 0; fpvi < footprintValuesLength; fpvi++)
        {
          var nextFootprint = sanitizeFootprint(footprintValues[fpvi]);

          if (nextFootprint != null)
          {
            _self.aladinOverlay.addFootprints(
              _self.aladin.createFootprintsFromSTCS(nextFootprint));

            if (inputs.fov == null)
            {
              _calcRowFOV(Number(decValue), Number(raValue), halfFOV);
            }
          }
        }
      }
    }

    function _setFieldOfView()
    {
      var fieldOfView = -1;

      if (inputs.fov == null)
      {
        var aRA = _calcFOV();

        // Add 20% to add some space around the footprints
        fieldOfView = Math.max(_self.DEC[3], (aRA[3] * Math.cos(_self.DEC[2]
                      * PI_OVER_180))) * 1.2;
      }
      else
      {
        fieldOfView = Number(inputs.fov);
      }

      if (fieldOfView < 0)
      {
        fieldOfView = DEFAULT_FOV_DEGREES;
      }

      if (inputs.afterFOVCalculation != null)
      {
        fieldOfView = inputs.afterFOVCalculation(fieldOfView);
      }

      _self.aladin.setFoV(Math.min(DEFAULT_FOV_DEGREES, fieldOfView));
      _self.fieldOfViewSetFlag = true;

      if ((_self.defaultRA != null) && (_self.defaultDec != null))
      {
        _self.aladin.gotoRaDec(_self.defaultRA, _self.defaultDec);
      }
    }

    function handleRenderComplete(e, args)
    {
      if (inputs.renderedRowsOnly === true)
      {
        reset();

        var renderedRange = args.grid.getRenderedRange();

        for (var i = renderedRange.top, ii = renderedRange.bottom; i < ii; i++)
        {
          handleAddFootprint(e, {rowData: args.grid.getDataItem(i)});
        }
      }

      if (_self.fieldOfViewSetFlag === false)
      {
        _setFieldOfView();
      }
    }

    $.extend(this, {
      "init": init,
      "destroy": destroy
    });
  }
})(jQuery);
