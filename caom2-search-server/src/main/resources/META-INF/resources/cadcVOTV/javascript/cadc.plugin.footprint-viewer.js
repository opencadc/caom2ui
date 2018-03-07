;(function($, A, window, undefined) {
  'use strict'

  if (typeof A === 'undefined') {
    // Require AladinLite.
    throw new Error(
      'AladinLite must be present.  (http://aladin.u-strasbg.fr/AladinLite/)'
    )
  }

  // register namespace
  $.extend(true, window, {
    cadc: {
      vot: {
        plugin: {
          footprint: AladinLiteFootprintViewer
        }
      }
    }
  })

  /**
   * AladinLite footprint viewer.  This is incorporated as a Plugin to allow
   *
   * @constructor
   */
  function AladinLiteFootprintViewer(_inputs) {
    var POLYGON = 'Polygon'
    var CIRCLE = 'Circle'
    // var POLYGON_SPLIT = POLYGON + ' ICRS'
    // var CIRCLE_SPLIT = CIRCLE + ' ICRS'
    var DEFAULT_FOV_DEGREES = 180
    var DEFAULT_FOV_BUFFER = 500 / 100

    var _self = this
    var _defaults = {
      targetSelector: '#aladin-lite',
      toggleSwitchSelector: null,
      hidden: false, // Always show by default.
      toggleClose: function($toggleSelector) {
        $toggleSelector.html($toggleSelector.data('open'))
      },
      toggleOpen: function($toggleSelector) {
        $toggleSelector.html($toggleSelector.data('close'))
      },
      aladin_options: {}, // Specific options for AladinLite.
      renderedRowsOnly: true,
      footprintFieldID: 'footprint',
      raFieldID: 'ra',
      decFieldID: 'dec',
      fovFieldID: 'fov',
      colour: 'orange',
      navigateToSelected: true,
      maxRowCount: false,
      highlightColour: 'yellow',
      /**
       * Perform further calculations on the FOV before setting it.  Useful
       * for further reducing it (e.g. from square degrees to degrees), or
       * buffering the field with some padding.
       *
       * @param {Number} fovValue
       * @return  {Number}
       */
      afterFOVCalculation: function(fovValue) {
        return fovValue * DEFAULT_FOV_BUFFER
      },
      resizeCalculation: function() {},
      onHover: true,
      onClick: false
    }

    // Start with opposite max values.
    this.fovBox = {
      raLeft: null,
      raRight: null,
      decTop: null,
      decBottom: null
    }

    this.grid = null

    // {cadc.vot.Viewer}
    this.viewer = null
    this.handler = new Slick.EventHandler()

    var inputs = $.extend(true, {}, _defaults, _inputs)

    this.footprintFieldID = inputs.footprintFieldID
    this.raFieldID = inputs.raFieldID
    this.decFieldID = inputs.decFieldID
    this.fovFieldID = inputs.fovFieldID
    this.$target = $(inputs.targetSelector)

    //
    // Declare AladinLite
    //
    this.aladin = null

    // footprint overlay, public data
    this.aladinOverlay = null
    //
    // End declaration of AladinLite
    //

    // currently 'active' (hover/click) row
    //
    this.currentFootprint = null

    // Start at this location.  Reset when re-rendering.
    this.defaultRA = null
    this.defaultDec = null

    this.fieldOfViewSetFlag = false

    /**
     * Initialize with the Slick Grid instance.
     * @param {cadc.vot.Viewer} _viewer      The CADC VOTable Viewer instance.
     */
    function init(_viewer) {
      destroy()

      if (inputs.hidden === true) {
        _self.$target.hide()
      }

      if (inputs.toggleSwitchSelector !== null) {
        var $toggleSwitchSelector = $(inputs.toggleSwitchSelector)

        if (
          inputs.hidden === true &&
          $toggleSwitchSelector.data('open') !== null
        ) {
          $toggleSwitchSelector.html($toggleSwitchSelector.data('open'))
        }

        $toggleSwitchSelector.on('click', function(e) {
          e.preventDefault()
          _self.$target.toggle()
          _toggleView()
          _toggleViewButton()

          return false
        })
      }

      /**
       * @property _self.viewer
       * @type {cadc.vot.Viewer}
       */
      _self.viewer = _viewer

      /**
       * @property _self.grid
       * @type {Slick.Grid}
       */
      _self.grid = _viewer.getGrid()

      _self.aladin = A.aladin(inputs.targetSelector, inputs.aladin_options)
      _self.aladinOverlay = A.graphicOverlay({
        color: inputs.colour,
        lineWidth: 3
      })
      _self.aladin.addOverlay(_self.aladinOverlay)
      _self.currentFootprint = A.graphicOverlay({
        name: 'current',
        color: inputs.highlightColour,
        lineWidth: 5
      })
      _self.aladin.addOverlay(_self.currentFootprint)
      _self.viewAladinButton = $('#slick-visualize')
      _self.viewAladinStatus = $('#slick-visualize-status')
      _self.rowCount = 0

      if (inputs.fov !== null) {
        _self.aladin.setFoV(inputs.fov)
      }

      if (_self.grid.getData().getLength) {
        if (inputs.renderedRowsOnly === true) {
          _self.handler.subscribe(
            _self.grid.onRenderComplete,
            handleRenderComplete
          )
        } else {
          _self.viewer.subscribe(cadc.vot.events.onRowAdded, function(e, args) {
            handleAddFootprint(e, args)

            if (inputs.maxRowCount) {
              if (_self.rowCount === 0) {
                _enableButton()
              }

              _self.rowCount++

              if (
                _self.rowCount > inputs.maxRowCount &&
                _self.viewAladinButton.hasClass('ui-disabled') === false
              ) {
                _disableButton()
              }
            }
          })

          _self.viewer.subscribe(cadc.vot.events.onDataLoaded, function() {
            _setFieldOfView()
          })

          _self.viewer.subscribe(cadc.vot.events.onFilterData, function(
            event,
            args
          ) {
            reset()

            var v = args.application
            // var data = v.getGrid().getData()
            var currentRows = v.getRows()
            var cdl = currentRows.length

            if (inputs.maxRowCount && cdl <= inputs.maxRowCount) {
              _enableButton()

              for (var cdi = 0; cdi < cdl; cdi++) {
                handleAddFootprint(event, {
                  rowData: currentRows[cdi]
                })
              }

              _setFieldOfView()
            } else {
              _disableButton()
            }
          })
        }
      }

      if (inputs.onHover === true) {
        _self.handler.subscribe(_self.grid.onMouseEnter, handleMouseEnter)
        _self.handler.subscribe(_self.grid.onMouseLeave, handleMouseLeave)
      }

      if (inputs.onClick === true) {
        _self.handler.subscribe(_self.grid.onClick, handleClick)
      }
    }

    function _resetCurrent() {
      if (_self.currentFootprint) {
        _self.currentFootprint.removeAll()
      }
    }

    function reset() {
      _self.aladinOverlay.removeAll()
      _self.currentFootprint.removeAll()

      _self.fieldOfViewSetFlag = false
      _self.defaultRA = null
      _self.defaultDec = null

      _resetCurrent()
    }

    function destroy() {
      _self.handler.unsubscribeAll()
      _self.aladin = null
      _self.aladinOverlay = null
      _self.currentFootprint = null
      _self.$target.empty()
      _self.defaultDec = null
      _self.defaultRA = null
      _self.DEC = null
      _self.RA0 = null
      _self.RA180 = null
      _self.fieldOfViewSetFlag = false
      _self.fovBox = {
        raLeft: null,
        raRight: null,
        decTop: null,
        decBottom: null
      }

      if (inputs.toggleSwitchSelector !== null) {
        $(inputs.toggleSwitchSelector).off('click')
      }

      if (_self.viewer !== null) {
        _self.viewer.unsubscribe(cadc.vot.events.onRowAdded)
        _self.viewer.unsubscribe(cadc.vot.events.onDataLoaded)
      }

      _toggleViewButton()
    }

    /**
     * Return the calculated maximums for a box.
     *
     * @param _footprint    Footprint string of coordinates.
     * @returns {{maxRA: *, minRA: *, maxDec: *, minDec: *}}
     * @private
     */
    function _calculateFootprintFOV(_footprint) {
      var raValues = []
      var decValues = []

      if (_footprint.region === CIRCLE) {
        var ra = _footprint.coords[0][0]
        var dec = _footprint.coords[0][1]
        var radius = _footprint.coords[1]

        raValues.push(ra + radius, ra - radius)
        decValues.push(dec + radius, dec - radius)
      }
      else if (_footprint.region === POLYGON) {
        for (var f = 0; f < _footprint.coords.length; f++) {
          // Even numbers are RA values.
          raValues.push(_footprint.coords[f][0])
          decValues.push(_footprint.coords[f][1])
        }
      }

      return {
        maxRA: Math.max.apply(null, raValues),
        minRA: Math.min.apply(null, raValues),
        maxDec: Math.max.apply(null, decValues),
        minDec: Math.min.apply(null, decValues)
      }
    }

    function _calculateFootprintsFOV(_footprints) {
      var maxRA = []
      var minRA = []
      var maxDec = []
      var minDec = []
      for (var i = 0; i < _footprints.length; i++) {
        var footprint = _footprints[i]
        var footprintFOV = _calculateFootprintFOV(footprint)
        maxRA.push(footprintFOV.maxRA)
        minRA.push(footprintFOV.minRA)
        maxDec.push(footprintFOV.maxDec)
        minDec.push(footprintFOV.minDec)
      }
      return {
        maxRA: Math.max.apply(null, maxRA),
        minRA: Math.min.apply(null, minRA),
        maxDec: Math.max.apply(null, maxDec),
        minDec: Math.min.apply(null, minDec)
      }
    }

    /**
     * Update the current FOV box.
     *
     * @param _footprint  The footprint string, with only coordinate points.
     * @private
     */
    function _updateFOV(_footprint) {
      var rowFOVBox = _calculateFootprintFOV(_footprint)

      var maxRA = rowFOVBox.maxRA
      var minRA = rowFOVBox.minRA
      var maxDec = rowFOVBox.maxDec
      var minDec = rowFOVBox.minDec

      if (_self.fovBox.raLeft === null || _self.fovBox.raLeft < maxRA) {
        _self.fovBox.raLeft = maxRA
      }

      if (_self.fovBox.raRight === null || _self.fovBox.raRight > minRA) {
        _self.fovBox.raRight = minRA
      }

      if (_self.fovBox.decTop === null || _self.fovBox.decTop < maxDec) {
        _self.fovBox.decTop = maxDec
      }

      if (_self.fovBox.decBottom === null || _self.fovBox.decBottom > minDec) {
        _self.fovBox.decBottom = minDec
      }
    }

    function _handleAction(_dataRow) {
      var raValue = _dataRow[_self.raFieldID]
      var decValue = _dataRow[_self.decFieldID]

      if (raValue !== null && $.trim(raValue) !== '' && decValue !== null && $.trim(decValue) !== '') {
        var selectedFootprints = _getFootprints(_dataRow[_self.footprintFieldID])

        for (var i = 0; i < selectedFootprints.length; i++) {
          var selectedFootprint = selectedFootprints[i]

          if (selectedFootprint.region === CIRCLE) {
            _self.currentFootprint.addFootprints(A.circle(selectedFootprint.coords[0], selectedFootprint.coords[1]))
          }
          else if (selectedFootprint.region === POLYGON) {
            _self.currentFootprint.addFootprints(A.polygon(selectedFootprint.coords))
          }
        }

        if (inputs.navigateToSelected === true) {
          _self.aladin.gotoRaDec(raValue, decValue)

          var selectedRowFOVBox = _calculateFootprintsFOV(selectedFootprints)
          var fieldOfView = Math.max(
            selectedRowFOVBox.maxRA - selectedRowFOVBox.minRA,
            selectedRowFOVBox.maxDec - selectedRowFOVBox.minDec
          )
          _self.aladin.setFoV(Math.min(DEFAULT_FOV_DEGREES, inputs.afterFOVCalculation(fieldOfView)))
        }
      }
      else {
        console.warn('Unable to add footprint for (' + raValue + ', ' + decValue + ')')
      }

      if (_self.aladin && _self.aladin.view) {
        _self.aladin.view.forceRedraw()
      }
    }

    function handleClick(e, args) {
      _resetCurrent()

      _handleAction(args.grid.getDataItem(args.row))

      // Do not allow row selection to happen.
      e.stopImmediatePropagation()
      return false
    }

    function handleMouseEnter(e, args) {
      e.stopImmediatePropagation()
      _handleAction(args.grid.getDataItem(args.cell.row))
    }

    function handleMouseLeave() {
      _resetCurrent()
    }

    function handleAddFootprint(e, args) {
      var _row = args.rowData
      var footprintValue = _row[_self.footprintFieldID]
      var raValue = $.trim(_row[_self.raFieldID])
      var decValue = $.trim(_row[_self.decFieldID])

      // Set the default location to the first item we see.
      if (_self.defaultRA === null && raValue !== null && raValue !== '') {
        _self.defaultRA = raValue
      }

      if (_self.defaultDec === null && decValue !== null && decValue !== '') {
        _self.defaultDec = decValue
      }

      // footprintValue = "Union ICRS (Polygon 22.870533 8.937613 22.869736 8.941107 22.867902 8.943593 22.865270 8.945237 22.862210 8.945809 22.859152 8.945228 22.856525 8.943575 22.854699 8.941084 22.853931 8.937072 22.854711 8.934094 22.856937 8.931267 22.859669 8.929790 22.862759 8.929409 22.865773 8.930179 22.868290 8.931991 22.869954 8.934591 Polygon 24.040157 9.410975 24.039578 9.413998 24.037911 9.416598 24.035392 9.418411 24.031850 9.419198 24.028306 9.418422 24.025780 9.416618 24.024105 9.414024 24.023532 9.410487 24.024302 9.407506 24.026131 9.405015 24.028761 9.403362 24.031823 9.402781 24.035367 9.403556 24.037893 9.405360 24.039568 9.407955 Circle 22.871000 8.935000 0.006)"
      // footprintValue = "Union ICRS (Polygon 22.870533 8.937613 22.869736 8.941107 22.867902 8.943593 22.865270 8.945237 22.862210 8.945809 22.859152 8.945228 22.856525 8.943575 22.854699 8.941084 22.853931 8.937072 22.854711 8.934094 22.856937 8.931267 22.859669 8.929790 22.862759 8.929409 22.865773 8.930179 22.868290 8.931991 22.869954 8.934591 Polygon 24.040157 9.410975 24.039578 9.413998 24.037911 9.416598 24.035392 9.418411 24.031850 9.419198 24.028306 9.418422 24.025780 9.416618 24.024105 9.414024 24.023532 9.410487 24.024302 9.407506 24.026131 9.405015 24.028761 9.403362 24.031823 9.402781 24.035367 9.403556 24.037893 9.405360 24.039568 9.407955)"
      // footprintValue = "Union ICRS (Circle 22.871000 8.935000 0.006)"
      var footprints = _getFootprints(footprintValue)
      for (var i = 0; i < footprints.length; i++) {
        var footprint = footprints[i]

        if (footprint.region === CIRCLE) {
          _self.aladinOverlay.add(A.circle(footprint.coords[0][0], footprint.coords[0][1], footprint.coords[1]))
        }
        else if (footprint.region === POLYGON) {
          _self.aladinOverlay.add(A.polygon(footprint.coords))
        }
        else {
          console.log("Unknown footprint " + footprint)
        }

        if (!inputs.fov || inputs.fov === null) {
          _updateFOV(footprint)
        }
      }
    }

    function _getFootprints(footprintString) {
      var footprints = []
      var region = null
      var coordinates = []

      if (footprintString) {
        var shapes = footprintString.split(/(Polygon|Circle)/)
        for (var i = 0; i < shapes.length; i++) {

          var shape = shapes[i].trim()
          if (shape.length === 0) {
            continue
          }
          if (shape === POLYGON || shape === CIRCLE) {
            region = shape
            continue
          }
          var coords = shape.split(/[\s()]+/)
          if (coords.length < 3) {
            continue
          }
          for (var j = 0; j < coords.length; j++) {
            var coord = coords[j]
            if (coord.length === 0) {
              continue
            }
            if (!isNaN(coord)) {
              coordinates.push(Number(coord))
            }
          }

          if (!region && coordinates.length > 0) {
            var isPolygon = coordinates.length % 2 === 0
            var isCircle = coordinates.length === 3

            if (isPolygon) {
              region = POLYGON;
            }
            else if (isCircle) {
              region = CIRCLE
            }
          }

          if (region && coordinates.length > 0) {
            // split polygon array into 2 segment chunks
            if (region === POLYGON) {
              var vertices = []
              while (coordinates.length) {
                vertices.push(coordinates.splice(0, 2))
              }
              coordinates = vertices
            }
            else if (region === CIRCLE) {
              coordinates = [[coordinates[0], coordinates[1]], coordinates[2]]
            }
            footprints.push({region: region, coords: coordinates})
            region = null
            coordinates = []
          }
        }
      }
      return footprints
    }

    /**
     * Set the Field of View.  This is used when the data is done loading completely, or the data has filtered down.
     *
     * This assumes the fovBox has been built up using the _updateFOV method.
     *
     * @private
     */
    function _setFieldOfView() {
      var fieldOfView = Math.max(
        _self.fovBox.raLeft - _self.fovBox.raRight,
        _self.fovBox.decTop - _self.fovBox.decBottom
      )
      _self.aladin.setFoV(
        Math.min(DEFAULT_FOV_DEGREES, inputs.afterFOVCalculation(fieldOfView))
      )
      _self.fieldOfViewSetFlag = true

      if (_self.defaultRA !== null && _self.defaultDec !== null) {
        _self.aladin.gotoRaDec(_self.defaultRA, _self.defaultDec)
      }
    }

    function handleRenderComplete(e, args) {
      if (inputs.renderedRowsOnly === true) {
        reset()

        var renderedRange = args.grid.getRenderedRange()

        for (
          var i = renderedRange.top, ii = renderedRange.bottom;
          i < ii;
          i++
        ) {
          handleAddFootprint(e, {
            rowData: args.grid.getDataItem(i)
          })
        }
      }

      if (_self.fieldOfViewSetFlag === false) {
        _setFieldOfView()
      }
    }

    function _enableButton() {
      _self.viewAladinButton.removeClass('ui-disabled')
      _self.viewAladinStatus.addClass('wb-invisible')
    }

    function _disableButton() {
      _self.viewAladinButton.addClass('ui-disabled')
      _self.viewAladinStatus.removeClass('wb-invisible')
      inputs.toggleClose($(inputs.toggleSwitchSelector))
      _self.$target.hide()
    }

    function _toggleViewButton() {
      if (inputs.toggleSwitchSelector !== null) {
        if (_self.$target.is(':visible')) {
          inputs.toggleOpen($(inputs.toggleSwitchSelector))
        } else {
          inputs.toggleClose($(inputs.toggleSwitchSelector))
        }
      }
    }

    function _toggleView() {
      if (_self.viewer && _self.grid && inputs.resizeCalculation) {
        inputs.resizeCalculation()
      }
    }

    $.extend(this, {
      init: init,
      destroy: destroy
    })
  }
})(jQuery, A, window)
