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

    this.addHooks = function() {
      // t75446 - Add VLASS to AladinLite Image laters
      var hipsDir = 'http://archive-new.nrao.edu/vlass/HiPS/VLASS1.1/Quicklook/'
      var label = 'VLASS1.1-QL-20180625'
      var currA = _self.aladin
      var currBaseImage = currA.getBaseImageLayer()

      // Set the default image survey so that the new HiPS layer is initialized.
      currA.setImageSurvey(
        currA.createImageSurvey(label, label, hipsDir, 'equatorial', 9, {
          imgFormat: 'png'
        })
      )

      // Configure the new HiPS layer.
      currA.getBaseImageLayer().getColorMap().update('rainbow')

      // Set the default back to the previous one.
      currA.setImageSurvey(currBaseImage)

      currA.gotoRaDec(90.0, 40.0)
      currA.setFoV(180.0)
    }

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
            var filteredRows = v.getFilteredRows()
            var cdl = filteredRows.length

            if (inputs.maxRowCount && cdl <= inputs.maxRowCount && cdl > 0) {
              _enableButton()

              for (var cdi = 0; cdi < cdl; cdi++) {
                handleAddFootprint(event, {
                  rowData: filteredRows[cdi],
                  forceUpdateFOV: true
                })
              }

              _handleAction(filteredRows[0])

              // _setFieldOfView()
            } else {
              _disableButton()
            }
          })
        }
      }

      // Run hooks (i.e. add image survey layers, etc.)
      _self.addHooks()

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
        var ra = _footprint.coords[0]
        var dec = _footprint.coords[1]
        var radius = _footprint.coords[2]

        raValues.push(ra + radius, ra - radius)
        decValues.push(dec + radius, dec - radius)
      } else if (_footprint.region === POLYGON) {
        for (var i = 0, len = _footprint.coords.length; i < len; i++) {
          // Even numbers are RA values.
          raValues.push(_footprint.coords[i][0])
          decValues.push(_footprint.coords[i][1])
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
      for (var i = 0, len = _footprints.length; i < len; i++) {
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

      if (
        raValue !== null &&
        $.trim(raValue) !== '' &&
        decValue !== null &&
        $.trim(decValue) !== ''
      ) {
        var selectedFootprints = _getFootprints(
          _dataRow[_self.footprintFieldID]
        )

        for (var i = 0, len = selectedFootprints.length; i < len; i++) {
          var selectedFootprint = selectedFootprints[i]

          if (selectedFootprint.region === CIRCLE) {
            _self.currentFootprint.add(
              A.circle(
                selectedFootprint.coords[0],
                selectedFootprint.coords[1],
                selectedFootprint.coords[2]
              )
            )
          } else if (selectedFootprint.region === POLYGON) {
            _self.currentFootprint.addFootprints([
              A.polygon(selectedFootprint.coords)
            ])
          }
        }

        if (inputs.navigateToSelected === true) {
          _self.aladin.gotoRaDec(raValue, decValue)

          var selectedRowFOVBox = _calculateFootprintsFOV(selectedFootprints)
          var fieldOfView = Math.max(
            selectedRowFOVBox.maxRA - selectedRowFOVBox.minRA,
            selectedRowFOVBox.maxDec - selectedRowFOVBox.minDec
          )
          _self.aladin.setFoV(
            Math.min(
              DEFAULT_FOV_DEGREES,
              inputs.afterFOVCalculation(fieldOfView)
            )
          )
        }
      } else {
        console.warn(
          'Unable to add footprint for (' + raValue + ', ' + decValue + ')'
        )
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
      var cell = args.grid.getCellFromEvent(e)
      if (cell) {
        _handleAction(args.grid.getDataItem(cell.row))
      }
    }

    function handleMouseLeave() {
      _resetCurrent()
      if (_self.aladin && _self.aladin.view) {
        _self.aladin.view.forceRedraw()
      }
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

      var footprints = _getFootprints(footprintValue)
      for (var i = 0, len = footprints.length; i < len; i++) {
        var footprint = footprints[i]

        if (footprint.region === CIRCLE) {
          _self.aladinOverlay.add(
            A.circle(
              footprint.coords[0],
              footprint.coords[1],
              footprint.coords[2]
            )
          )
        } else if (footprint.region === POLYGON) {
          _self.aladinOverlay.addFootprints([A.polygon(footprint.coords)])
        } else {
          console.log('Unknown footprint ' + footprint)
        }

        if (
          args.forceUpdateFOV === true ||
          (!inputs.fov || inputs.fov === null)
        ) {
          _updateFOV(footprint)
        }
      }
    }

    function _getFootprints(footprintString) {
      var footprints = []

      if (footprintString) {
        var region = null
        var coordinates = []
        var shapes = footprintString.split(/(Polygon|Circle)/)
        for (var i = 0, len = shapes.length; i < len; i++) {
          var shape = shapes[i].trim()
          if (shape.length > 0) {
            if (shape === POLYGON || shape === CIRCLE) {
              region = shape
            } else {
              var coords = shape.split(/[\s()]+/)
              var lenj = coords.length

              if (lenj >= 3) {
                for (var j = 0; j < lenj; j++) {
                  var coord = coords[j]
                  if (coord.length > 0 && !isNaN(coord)) {
                    coordinates.push(Number(coord))
                  }
                }

                if (!region && coordinates.length > 0) {
                  var isPolygon = coordinates.length % 2 === 0
                  var isCircle = coordinates.length === 3

                  if (isPolygon) {
                    region = POLYGON
                  } else if (isCircle) {
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
                  footprints.push({ region: region, coords: coordinates })
                  region = null
                  coordinates = []
                }
              }
            }
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
