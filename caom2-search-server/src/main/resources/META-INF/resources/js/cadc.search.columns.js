;
(function ($, window) {
  'use strict'
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            URI_MATCH_REGEX: /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/,
            DETAILS_BASE_URL: '/caom2ui/view',
            CAOM2_RESOLVER_VALUE_KEY: 'Plane.position.bounds@Shape1Resolver.value',
            OBSCORE_RESOLVER_VALUE_KEY: 'Char.SpatialAxis.Coverage.Support.Area@Shape1Resolver.value',
            CAOM2_TARGET_NAME_VALUE_KEY: 'Plane.position.bounds@Shape1.value',
            COLLECTION_VALUE_KEY: 'Observation.collection',
            INSTRUMENT_NAME_KEY: 'Observation.instrument.name',
            DETAILS_CSS: 'details_tooltip_link',
            DATALINK_URL_SUFFIX: '/datalink',
            columns: {
              PUBLISHER_ID_UTYPE: 'caom2:Plane.publisherID',
              OBSERVATION_URI_UTYPE: 'caom2:Observation.uri',
              OBSERVATION_ID_UTYPE: 'caom2:Observation.observationID',
              COLLECTION_UTYPE: 'caom2:Observation.collection',
              INSTRUMENT_NAME_UTYPE: 'caom2:Observation.instrument.name',
              ColumnManager: ColumnManager
            },
            datalink: {
              thumbnail_uri: '#thumbnail',
              pkg_uri: '#package',
              preview_uri: '#preview'
            },
            preview: {
              unauthorized_message: {
                en: 'You do not have permission to view this Preview image.',
                fr: "Vous n'avez pas la permission de visualiser cette image"
              }
            },
            columnOptions: {
              'caom2:Upload.target': {
                tap_column_name: 'Upload.target',
                label: 'Upload Target',
                fitMax: false,
                width: 210,
                formField: 'targetList'
              },
              'caom2:Upload.ra': {
                tap_column_name: 'Upload.ra',
                datatype: 'double',
                label: 'Upload Target RA',
                formField: 'targetList',
                fitMax: false,
                width: 130,
                converter: 'RAConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'hms')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'hms') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                    label: 'H:M:S',
                    value: 'hms',
                    default: true
                  },
                    {
                      label: 'Degrees',
                      value: 'DEGREES'
                    }
                  ]
                }
              },
              'caom2:Upload.dec': {
                tap_column_name: 'Upload.dec',
                dataypte: "double",
                label: 'Upload Target Dec',
                fitMax: false,
                formField: 'targetList',
                width: 140,
                converter: 'DECConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'dms')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'dms') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                    label: 'D:M:S',
                    value: 'dms',
                    default: true
                  },
                    {
                      label: 'Degrees',
                      value: 'DEGREES'
                    }
                  ]
                }
              },
              'caom2:Upload.radius': {
                tap_column_name: 'Upload.radius',
                datatype: 'double',
                label: 'Cutout Radius',
                formField: 'targetList',
                fitMax: false,
                visible: false
              },
              'caom2:Observation.observationID': {
                label: 'Obs. ID',
                fitMax: true,
                formatter: function (row, cell, value, columnDef, dataContext) {
                  var publisherID =
                    dataContext[ca.nrc.cadc.search.columns.PUBLISHER_ID_UTYPE]
                  return formatDetailsCell(
                    value,
                    publisherID,
                    columnDef,
                    row
                  )
                }
              },
              'caom2:Observation.uri': {
                label: 'Preview',
                tap_column_name: 'Observation.observationURI',
                width: 70,
                sortable: false,
                filterable: false,
                formatter: function () {
                  return '<span class="cellValue preview"></span>'
                },
                asyncFormatter: function (cellNode, row, dataContext) {
                  var $cell = $(cellNode)
                  var planePublisherIdValue =
                    dataContext[ca.nrc.cadc.search.columns.PUBLISHER_ID_UTYPE]

                  /**
                   * Create a link for a preview.
                   *
                   * @param {jQuery} $cell         The jQuery DOM element to add to.
                   * @param {string|null} thumbnailURL  The URL for the thumbnail.  [Optional]
                   * @return {*|HTMLElement}
                   */
                  function createLink($cell, thumbnailURL) {
                    // Clickable link to the preview page
                    var $link = $('<a>Preview</a>')
                    $link.addClass('preview_tooltip_link')
                    $link.attr('id', observationID + '_preview')
                    $link.attr('href', '#')

                    var $cellSpan = $('<span></span>')
                    $cellSpan.addClass('cellValue')
                    $cellSpan.addClass('preview')
                    $cellSpan.append($link)

                    $cell.empty().append($cellSpan)

                    if (thumbnailURL && thumbnailURL !== null) {
                      // Create the thumbnail tooltip
                      var $thunbnailImage = $('<img />')
                      $thunbnailImage.attr('id', observationID + '_256_preview')
                      $thunbnailImage.attr('src', thumbnailURL)
                      $thunbnailImage.addClass('image-actual')

                      $link.tooltipster({
                        interactive: true,
                        arrow: false,
                        content: $thunbnailImage,
                        theme: 'tooltipster-preview-thumbnail',
                        position: 'right',
                        onlyOne: true
                      })
                    }

                    return $link
                  }

                  /**
                   * Create a link object and append it to the row.
                   *
                   * jenkinsd 2018.02.26
                   */
                  function insertPreviewLink(
                    previewUrls,
                    thumbnailUrls,
                    publisherId
                  ) {
                    // Create the preview link
                    var $link = createLink(
                      $cell,
                      thumbnailUrls !== null && thumbnailUrls.length > 0 ?
                      thumbnailUrls[0] :
                      null
                    )

                    if (previewUrls.length > 0) {
                      var pubIDQuery = new cadc.web.util.URI(publisherId)
                        .getQueryString()
                        .split('/')
                      var observationID = pubIDQuery[0]
                      var productID = pubIDQuery[1]

                      // Display the preview window
                      $link.click(function () {
                        var $content = $('<div id="scoped-content"></div>')

                        // Preview window content
                        var $obsId = $(
                          '<p style="text-align: center;"></p>'
                        ).text('observationID: ' + observationID)
                        var $pdctId = $(
                          '<p style="text-align: center;"></p>'
                        ).text('productID: ' + productID)
                        var $pubId = $(
                          '<p style="text-align: center;"></p>'
                        ).text('publisherID: ' + publisherId)
                        var $previews = $('<div></div>')

                        for (var j = 0; j < previewUrls.length; j++) {
                          var $preview = $(
                            '<img style="display: block; margin: auto;"/>'
                          )
                          $preview.prop('id', observationID + '_preview_' + j)
                          $preview.prop('src', previewUrls[j])
                          $preview.addClass('image-actual')
                          $previews.append($preview, '<br>')
                        }

                        $content.append($obsId, $pdctId, $pubId, $previews)

                        // name parameter (second one) passed in to open()
                        // leads to Safari not being able to focus on the document
                        // (w.focus() returns undefined, implying the system can't do it)
                        // Calling with no parameters works for Chrome, Safari & Firefox
                        // as of Dec 2017 - s2224
                        var w = window.open()
                        w.document.body.innerHTML = ''

                        // Open and close are here to stop browsers expecting more data.
                        // jenkinsd 2017.04.12
                        //
                        w.document.open()
                        w.document.write($content.html())
                        w.document.close()

                        w.document.title = collection + ' - ' + productID
                        w.focus()
                        return false
                      })
                    } else {
                      $link.click(function () {
                        return false
                      })
                    }
                  } // End of insertPreviewLink

                  if (planePublisherIdValue) {
                    var previewURI = new cadc.web.util.URI(
                      planePublisherIdValue
                    )

                    var pathItems = previewURI.getPathItems()
                    var pl = pathItems.length
                    var queryItems = previewURI.getQueryString().split('/')

                    // Last path item.
                    var collection = pl && pathItems[pl - 1]
                    var observationID = queryItems[0]
                    var productID = queryItems[1]

                    var runID = $('#downloadForm').find("input[name='runid']").val()

                    // Get the thumbnails and previews from datalink
                    $.ajax({
                      url: ' ' +
                        ca.nrc.cadc.search.services.applicationEndpoint +
                        ca.nrc.cadc.search.DATALINK_URL_SUFFIX,
                      dataType: 'xml',
                      data: {
                        id: planePublisherIdValue,
                        request: 'downloads-only',
                        runid: runID
                      },
                      xhrFields: {
                        withCredentials: true
                      },
                      statusCode: {
                        200: function (data) {
                          var evaluator = new cadc.vot.xml.VOTableXPathEvaluator(
                            data,
                            'votable'
                          )

                          // Determine field indexes
                          var accessUrlIndex
                          var errorMessageIndex
                          var semanticsIndex
                          var readableIndex = -1
                          var contentTypeIndex
                          var fields = evaluator.evaluate(
                            "/VOTABLE/RESOURCE[@type='results']/TABLE/FIELD"
                          )
                          for (
                            var fieldIndex = 0, fl = fields.length; fieldIndex < fl; fieldIndex++
                          ) {
                            var field = fields[fieldIndex]
                            var name = field.getAttribute('name')
                            switch (name) {
                              case 'access_url':
                                {
                                  accessUrlIndex = fieldIndex
                                  break
                                }

                              case 'error_message':
                                {
                                  errorMessageIndex = fieldIndex
                                  break
                                }

                              case 'semantics':
                                {
                                  semanticsIndex = fieldIndex
                                  break
                                }

                              case 'link_authorized':
                                {
                                  readableIndex = fieldIndex
                                  break
                                }

                              case 'content_type':
                                {
                                  contentTypeIndex = fieldIndex
                                  break
                                }
                            }
                          }

                          // Loop through the table rows
                          var rowID = dataContext['id']
                          var thumbnailURLs = []
                          var previewURLs = []
                          var packageURLs = []
                          var otherURLs = []
                          var tableDataRows = evaluator.evaluate(
                            '/VOTABLE/RESOURCE[@type="results"]/TABLE/DATA/TABLEDATA/TR'
                          )

                          for (
                            var trIndex = 0, trl = tableDataRows.length; trIndex < trl; trIndex++
                          ) {
                            var tableDataCells =
                              tableDataRows[trIndex].children
                            var errorMessage =
                              tableDataCells[errorMessageIndex].textContent
                            var readable =
                              readableIndex >= 0 &&
                              tableDataCells[readableIndex].textContent ===
                              'true'

                            if (errorMessage.length > 0) {
                              console.error(
                                'DataLink preview error: ' + errorMessage
                              )
                            } else if (readable === true) {
                              var contentType =
                                tableDataCells[contentTypeIndex].textContent
                              var semantics =
                                tableDataCells[semanticsIndex].textContent
                              var accessURL =
                                tableDataCells.length >= accessUrlIndex ?
                                tableDataCells[accessUrlIndex].textContent :
                                ''
                              if (accessURL) {
                                if (
                                  semantics ===
                                  ca.nrc.cadc.search.datalink.thumbnail_uri
                                ) {
                                  thumbnailURLs.push(accessURL)
                                } else if (
                                  semantics ===
                                  ca.nrc.cadc.search.datalink.preview_uri &&
                                  contentType.indexOf('image') >= 0
                                ) {
                                  previewURLs.push(accessURL)
                                } else if (
                                  semantics ===
                                  ca.nrc.cadc.search.datalink.pkg_uri
                                ) {
                                  packageURLs.push(accessURL)
                                } else {
                                  otherURLs.push(accessURL)
                                }
                              }
                            }
                          }

                          // CADC Story 2288 - One Click Downloads
                          // For those DataLink semantics that are not Previews or Thumbnails, keep track of
                          // them and apply them as:
                          // 1. If there is a single semantic left, then use it.
                          // 2. If there are multiple semantics left with no #pkg semantic, disable one-click.
                          // 3. Use the #pkg semantic URL.
                          var $oneClickLink = $('a#_one-click_' + rowID)
                          if (otherURLs.length === 1) {
                            $oneClickLink.attr('href', otherURLs[0]).show()
                          } else if (packageURLs.length >= 1) {
                            $oneClickLink.attr('href', packageURLs[0]).show()
                          }

                          // If datalink didn't provide thumbnail and preview URLs, create the urls and
                          // check if they exist.
                          if (
                            thumbnailURLs.length === 0 &&
                            previewURLs.length === 0
                          ) {
                            var thumbnailPreview = new ca.nrc.cadc.search.Preview(
                              collection,
                              observationID,
                              productID,
                              256,
                              runID,
                              undefined
                            )

                            var addMainPreview = function (thumbnailURL) {
                              var preview = new ca.nrc.cadc.search.Preview(
                                collection,
                                observationID,
                                productID,
                                1024,
                                runID,
                                undefined
                              )

                              preview.getPreview(function (previewURL) {
                                if (previewURL) {
                                  var $link = createLink($cell, thumbnailURL)
                                  $link.attr('href', previewURL)
                                  $link.attr('target', '_PREVIEW')

                                  var $previewImage = $('<img />')
                                  $previewImage.prop(
                                    'id',
                                    observationID + '_256_preview'
                                  )
                                  $previewImage.prop('src', previewURL)
                                  $previewImage.addClass('image-actual')
                                }
                              })
                            }

                            thumbnailPreview.getPreview(
                              addMainPreview,
                              function (status) {
                                if (status === 404) {
                                  addMainPreview(null)
                                }
                              }
                            )
                          } else {
                            insertPreviewLink(
                              previewURLs,
                              thumbnailURLs,
                              planePublisherIdValue
                            )
                          }
                        }
                      }
                    }).fail(function (jqXHR, textStatus, errorThrown) {
                      if (jqXHR.status !== 404) {
                        console.error(
                          'Error >> ' + errorThrown + ' (' + jqXHR.status + ')'
                        )
                      }
                    })
                  } else {
                    $cell.empty()
                  }
                }
              },
              'caom2:Observation.collection': {
                label: 'Collection',
                fitMax: true
              },
              'caom2:Observation.algorithm.name': {
                label: 'Algorithm Name'
              },
              'caom2:Observation.type': {
                label: 'Obs. Type',
                fitMax: true
              },
              'caom2:Observation.intent': {
                label: 'Intent'
              },
              'caom2:Observation.sequenceNumber': {
                label: 'Sequence Number',
                valueFormatter: function (value) {
                  return formatNumeric(value, null)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, null),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Observation.environment.tau': {
                label: 'Tau',
                valueFormatter: function (value) {
                  return formatNumeric(value, 2)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, 2),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Observation.proposal.id': {
                label: 'Proposal ID',
                formatter: function (row, cell, value, columnDef, dataContext) {
                  var utype = columnDef.utype
                  var uTypeName = utype.substr(utype.indexOf(':') + 1)
                  var valueObject = {}

                  if (value) {
                    valueObject[uTypeName] = value
                  }

                  var collection = dataContext[ca.nrc.cadc.search.columns.COLLECTION_UTYPE]

                  if (collection) {
                    valueObject[ca.nrc.cadc.search.COLLECTION_VALUE_KEY] = collection
                  }

                  return formatQuickSearchLink(value, valueObject, utype, null)
                }
              },
              'caom2:Observation.proposal.pi': {
                label: 'P.I. Name',
                formatter: function (row, cell, value, columnDef) {
                  var utype = columnDef.utype
                  var uTypeName = utype.substr(utype.indexOf(':') + 1)
                  var valueObject = {}

                  if (value) {
                    valueObject[uTypeName] = value
                  }

                  return formatQuickSearchLink(value, valueObject, utype, null)
                }
              },
              'caom2:Observation.proposal.title': {
                label: 'Proposal Title'
              },
              // This is a typo in the database!  PLEASE make sure this is the
              // only reference to 'proprosal' instead of proposal.
              'caom2:Observation.proposal.project': {
                label: 'Proposal Project',
                tap_column_name: 'Observation.proposal_project'
              },
              'caom2:Observation.proposal.keywords': {
                label: 'Proposal Keywords'
              },
              'caom2:Observation.target.name': {
                label: 'Target Name',
                formatter: function (row, cell, value, columnDef) {
                  var utype = columnDef.utype
                  var valueObject = {}

                  if (value) {
                    valueObject['Plane.position.bounds'] = value
                    valueObject[ca.nrc.cadc.search.CAOM2_RESOLVER_VALUE_KEY] =
                      'NONE'
                  }

                  return formatQuickSearchLink(value, valueObject, utype, null)
                }
              },
              'caom2:Observation.target.type': {
                label: 'Target Type'
              },
              'caom2:Observation.target.moving': {
                label: 'Moving Target',
                valueFormatter: function (value) {
                  return formatNumeric(value, 2)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, 2),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Observation.target.standard': {
                label: 'Target Standard',
                valueFormatter: function (value) {
                  return formatNumeric(value, 2)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, 2),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Observation.target.keywords': {
                label: 'Target Keywords',
                fitMax: true
              },
              'caom2:Observation.instrument.name': {
                label: 'Instrument'
              },
              'caom2:Observation.instrument.keywords': {
                label: 'Instrument Keywords',
                fitMax: true
              },
              'caom2:Plane.id': {
                label: 'planeID',
                tap_column_name: 'Plane.planeID',
                extended: true
              },
              'caom2:Plane.publisherID.downloadable': {
                label: 'DOWNLOADABLE',
                tap_column_name: 'isDownloadable(Plane.publisherID)'
              },
              'caom2:Plane.productID': {
                label: 'Product ID',
                fitMax: true,
                formatter: function (row, cell, value, columnDef, dataContext) {
                  var publisherID =
                    dataContext[ca.nrc.cadc.search.columns.PUBLISHER_ID_UTYPE]
                  return formatDetailsCell(
                    value,
                    publisherID,
                    columnDef,
                    row
                  )
                }
              },
              'caom2:Plane.metaRelease': {
                label: 'Meta Release',
                extended: true
              },
              'caom2:Plane.dataRelease': {
                label: 'Data Release'
              },
              'caom2:Plane.dataProductType': {
                label: 'Data Type',
                fitMax: false,
                width: 65,
                formatter: function (row, cell, value, columnDef) {
                  return formatDataType(value, columnDef.utype)
                }
              },
              'caom2:Plane.calibrationLevel': {
                label: 'Cal. Lev.',
                valueFormatter: function (value) {
                  return formatNumeric(value, null)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, null),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Plane.provenance.name': {
                label: 'Provenance Name'
              },
              'caom2:Plane.provenance.version': {
                label: 'Prov. Version'
              },
              'caom2:Plane.provenance.reference': {
                label: 'Prov. Reference',
                extended: true
              },
              'caom2:Plane.provenance.producer': {
                label: 'Prov. Producer',
                extended: true
              },
              'caom2:Plane.provenance.project': {
                label: 'Prov. Project'
              },
              'caom2:Plane.provenance.runID': {
                label: 'Prov. Run ID'
              },
              'caom2:Plane.provenance.lastExecuted': {
                label: 'Prov. Last Executed'
              },
              'caom2:Plane.provenance.keywords': {
                label: 'Provenance Keywords',
                extended: true
              },
              'caom2:Plane.provenance.inputs': {
                label: 'Prov. Inputs',
                extended: true
              },
              'caom2:Plane.position.bounds': {
                label: 'Shape'
              },
              'caom2:Plane.energy.resolvingPower': {
                label: 'Resolving Power',
                width: 117,
                valueFormatter: function (value) {
                  return formatNumeric(value, 2)
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatNumeric(value, 2),
                    columnDef.utype,
                    value
                  )
                }
              },
              'caom2:Plane.position.bounds.cval1': {
                label: 'RA (J2000.0)',
                datatype: 'double',
                tap_column_name: 'COORD1(CENTROID(Plane.position.bounds))',
                // Fit max would be nice, but the original data is in a long
                // decimal format, so set it manually.
                fitMax: false,
                width: 116,
                converter: 'RAConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'hms')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'hms') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                      label: 'H:M:S',
                      value: 'hms',
                      default: true
                    },
                    {
                      label: 'Degrees',
                      value: 'DEGREES'
                    }
                  ]
                }
              },
              'caom2:Plane.position.bounds.cval2': {
                label: 'Dec. (J2000.0)',
                datatype: 'double',
                tap_column_name: 'COORD2(CENTROID(Plane.position.bounds))',
                // Fit max would be nice, but the original data is in a long
                // decimal format, so set it manually.
                fitMax: false,
                width: 125,
                converter: 'DECConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'dms')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'dms') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                      label: 'D:M:S',
                      value: 'dms',
                      default: true
                    },
                    {
                      label: 'Degrees',
                      value: 'DEGREES'
                    }
                  ]
                }
              },
              'caom2:Plane.position.bounds.area': {
                label: 'Field of View',
                datatype: 'double',
                unit: 'deg**2',
                tap_column_name: 'AREA(Plane.position.bounds)',
                // Fit max would be nice, but the original data is in a long
                // decimal format, so set it manually.
                fitMax: false,
                width: 100,
                converter: 'AreaConverter',
                formatter: function (row, cell, value, columnDef) {
                  return format(
                    value,
                    columnDef.utype,
                    value,
                    $(columnDef).data('unitValue')
                  )
                },
                header: {
                  units: [{
                      label: 'Sq. arcsec',
                      value: 'sec**2'
                    },
                    {
                      label: 'Sq. arcmin',
                      value: 'min**2'
                    },
                    {
                      label: 'Sq. deg',
                      value: 'deg**2',
                      default: true
                    }
                  ]
                }
              },
              'caom2:Plane.position.resolution': {
                label: 'IQ',
                fitMax: true,
                width: 105,
                converter: 'AngleConverter',
                formatter: function (row, cell, value, columnDef) {
                  return format(
                    value,
                    columnDef.utype,
                    value,
                    $(columnDef).data('unitValue')
                  )
                },
                header: {
                  units: [{
                      label: 'Milliarcseconds',
                      value: 'milliarcsec'
                    },
                    {
                      label: 'Arcseconds',
                      value: 'arcsec',
                      default: true
                    },
                    {
                      label: 'Arcminutes',
                      value: 'arcmin'
                    }
                  ]
                }
              },
              'caom2:Plane.position.sampleSize': {
                label: 'Pixel Scale',
                // Fit max would be nice, but the original data is in a long
                // decimal format, so set it manually.
                fitMax: false,
                width: 110,
                converter: 'AngleConverter',
                formatter: function (row, cell, value, columnDef) {
                  return format(
                    value,
                    columnDef.utype,
                    value,
                    $(columnDef).data('unitValue')
                  )
                },
                header: {
                  units: [{
                      label: 'Milliarcseconds',
                      value: 'milliarcsec'
                    },
                    {
                      label: 'Arcseconds',
                      value: 'arcsec',
                      default: true
                    },
                    {
                      label: 'Arcminutes',
                      value: 'arcmin'
                    },
                    {
                      label: 'Degrees',
                      value: 'degrees'
                    }
                  ]
                }
              },
              'caom2:Observation.requirements.flag': {
                label: 'Quality',
                fitMax: true
              },
              'caom2:Plane.energy.emBand': {
                label: 'Band',
                fitMax: true
              },
              'caom2:Plane.energy.bounds.lower': {
                tap_column_name: 'Plane.energy_bounds_lower',

                label: 'Min. Wavelength'
              },
              'caom2:Plane.energy.bounds.upper': {
                tap_column_name: 'Plane.energy_bounds_upper',
                label: 'Max. Wavelength'
              },
              'caom2:Plane.energy.bandpassName': {
                label: 'Filter',
                fitMax: true
              },
              'caom2:Plane.energy.transition.species': {
                label: 'Molecule'
              },
              'caom2:Plane.energy.transition.transition': {
                label: 'Transition'
              },
              'caom2:Plane.energy.restwav': {
                label: 'Rest-frame Energy'
              },
              'caom2:Plane.time.bounds.lower': {
                tap_column_name: 'Plane.time_bounds_lower',
                label: 'Start Date',
                // Fit max would be nice, but the default values are in MJD,
                // which is smaller than the Calendar dates, so set it up for
                // Calendar instead.
                fitMax: false,
                width: 145,
                converter: 'DateConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'IVOA')
                },
                formatter: function (row, cell, value, columnDef, dataContext) {
                  var searchValue = {}
                  //caom2:Observation.collection
                  //caom2:Observation.instrument.name
                  var instrument =
                        dataContext[ca.nrc.cadc.search.columns.INSTRUMENT_NAME_UTYPE]
                  if (instrument) {
                    searchValue[ca.nrc.cadc.search.INSTRUMENT_NAME_KEY] = instrument
                  }

                  var collection =
                        dataContext[ca.nrc.cadc.search.columns.COLLECTION_UTYPE]

                  if (collection) {
                    searchValue[ca.nrc.cadc.search.COLLECTION_VALUE_KEY] = collection
                  }

                  if (value) {
                    var intValue = parseInt(value)
                    searchValue['Plane.time.bounds.samples'] =
                      intValue + '..' + (intValue + 1)
                  }

                  return formatQuickSearchLink(
                    value,
                    searchValue,
                    columnDef.utype,
                    $(columnDef).data('unitValue')
                  )
                },
                header: {
                  units: [{
                      label: 'Calendar',
                      value: 'IVOA',
                      default: true
                    },
                    {
                      label: 'MJD',
                      value: 'MJD'
                    }
                  ]
                }
              },
              'caom2:Plane.time.bounds.upper': {
                tap_column_name: 'Plane.time_bounds_upper',
                label: 'End Date',
                // Fit max would be nice, but the default values are in MJD,
                // which is smaller than the Calendar dates, so set it up for
                // Calendar instead.
                fitMax: false,
                width: 145,
                converter: 'DateConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'IVOA')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'IVOA') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                      label: 'Calendar',
                      value: 'IVOA',
                      default: true
                    },
                    {
                      label: 'MJD',
                      value: 'MJD'
                    }
                  ]
                }
              },
              'caom2:Plane.time.exposure': {
                label: 'Int. Time',
                // Fitmax would be nice, but the original value doesn't
                // conform. Use a width instead.
                fitMax: false,
                width: 95,
                converter: 'TimeConverter',
                valueFormatter: function (value, column) {
                  return formatUnit(value, column, 'SECONDS')
                },
                formatter: function (row, cell, value, columnDef) {
                  return formatOutputHTML(
                    formatUnit(value, columnDef, 'SECONDS') || Number.NaN,
                    columnDef.utype,
                    value
                  )
                },
                header: {
                  units: [{
                      label: 'Seconds',
                      value: 'SECONDS',
                      default: true
                    },
                    {
                      label: 'Minutes',
                      value: 'MINUTES'
                    },
                    {
                      label: 'Hours',
                      value: 'HOURS'
                    },
                    {
                      label: 'Days',
                      value: 'DAYS'
                    }
                  ]
                }
              },
              'caom2:Plane.publisherID': {
                fitMax: true,
                label: 'Publisher ID',
                tap_column_name: 'Plane.publisherID'
              },
              'obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter': {
                fitMax: true,
                label: 's_fov',
                tap_column_name: 's_fov'
              },
              'obscore:Char.SpatialAxis.Coverage.Support.Area': {
                fitMax: true,
                label: 's_region',
                tap_column_name: 's_region'
              },
              'obscore:Char.SpatialAxis.Resolution.refval.value': {
                fitMax: true,
                label: 's_resolution',
                tap_column_name: 's_resolution'
              },
              'obscore:Char.SpatialAxis.numBins1': {
                fitMax: true,
                label: 's_xel1',
                tap_column_name: 's_xel1'
              },
              'obscore:Char.SpatialAxis.numBins2': {
                fitMax: true,
                label: 's_xel2',
                tap_column_name: 's_xel2'
              },
              'obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime': {
                fitMax: true,
                label: 't_min',
                tap_column_name: 't_min'
              },
              'obscore:Char.TimeAxis.Coverage.Bounds.Limits.StopTime': {
                fitMax: true,
                label: 't_max',
                tap_column_name: 't_max'
              },
              'obscore:Curation.PublisherDID': {
                fitMax: true,
                label: 'obs_publisher_did',
                tap_column_name: 'obs_publisher_did'
              },
              'obscore:DataID.Collection': {
                fitMax: true,
                label: 'obs_collection',
                tap_column_name: 'obs_collection'
              },
              'obscore:Provenance.ObsConfig.Facility.name': {
                fitMax: true,
                label: 'facility_name',
                tap_column_name: 'facility_name'
              },
              'obscore:Provenance.ObsConfig.Instrument.name': {
                fitMax: true,
                label: 'instrument_name',
                tap_column_name: 'instrument_name'
              },
              'obscore:DataID.observationID': {
                fitMax: true,
                label: 'obs_id',
                tap_column_name: 'obs_id'
              },
              'obscore:ObsDataset.dataProductType': {
                label: 'dataproduct_type',
                tap_column_name: 'dataproduct_type'
              },
              'obscore:ObsDataset.calibLevel': {
                label: 'calib_level',
                tap_column_name: 'calib_level'
              },
              'obscore:Curation.releaseDate': {
                label: 'obs_release_date',
                tap_column_name: 'obs_release_date'
              },
              'obscore:Target.Name': {
                label: 'target_name',
                tap_column_name: 'target_name'
              },
              'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1': {
                label: 's_ra',
                tap_column_name: 's_ra'
              },
              'obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2': {
                label: 's_dec',
                tap_column_name: 's_dec'
              },
              'obscore:Char.TimeAxis.Coverage.Support.Extent': {
                label: 't_exptime',
                tap_column_name: 't_exptime'
              },
              'obscore:Char.TimeAxis.Resolution.refval.value': {
                label: 't_resolution',
                tap_column_name: 't_resolution'
              },
              'obscore:Char.TimeAxis.numBins': {
                label: 't_xel',
                tap_column_name: 't_xel'
              },
              'obscore:Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit': {
                label: 'em_min',
                tap_column_name: 'em_min'
              },
              'obscore:Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit': {
                label: 'em_max',
                tap_column_name: 'em_max'
              },
              'obscore:Char.SpectralAxis.Resolution.ResolPower.refval': {
                label: 'em_res_power',
                tap_column_name: 'em_res_power'
              },
              'obscore:Char.SpectralAxis.numBins': {
                label: 'em_xel',
                tap_column_name: 'em_xel'
              },
              'obscore:Char.SpectralAxis.ucd': {
                label: 'em_ucd',
                tap_column_name: 'em_ucd'
              },
              'obscore:Char.PolarizationAxis.stateList': {
                label: 'pol_states',
                tap_column_name: 'pol_states'
              },
              'obscore:Char.PolarizationAxis.numBins': {
                label: 'pol_xel',
                tap_column_name: 'pol_xel'
              },
              'obscore:Char.ObservableAxis.ucd': {
                label: 'o_ucd',
                tap_column_name: 'o_ucd'
              },
              'obscore:Access.Reference': {
                label: 'access_url',
                tap_column_name: 'access_url'
              },
              'obscore:Access.Format': {
                label: 'access_format',
                tap_column_name: 'access_format'
              },
              'obscore:Access.Size': {
                label: 'access_estsize',
                tap_column_name: 'access_estsize'
              },
              'obscore:Curation.PublisherDID.downloadable': {
                label: 'downloadable',
                tap_column_name: 'isDownloadable(obs_publisher_did)'
              }
            }
          }
        }
      }
    }
  })

  /**
   * Format the given value between IVOA and W3C date formats.
   * @param value       The IVOA String date.
   * @returns {String}  Formatted string value.
   */
  function formatIVOAToW3CDateValue(value) {
    var val

    if (value) {
      var date = moment(value, ca.nrc.cadc.search.formats.date.ISO_MS).toDate()
      var dateFormat = new ca.nrc.cadc.search.DateFormat(
        date,
        ca.nrc.cadc.search.formats.date.W3C
      )
      val = dateFormat.format()
    } else {
      val = ''
    }

    return val
  }

  var IVOAToW3CDateOptions = {
    // Fit max would be nice, but the default values are in IVOA, which is
    // smaller than the Calendar dates, so set it up for Calendar
    // instead.
    fitMax: false,
    valueFormatter: function (value) {
      return formatIVOAToW3CDateValue(value)
    },
    formatter: function (row, cell, value, columnDef) {
      return formatOutputHTML(
        formatIVOAToW3CDateValue(value),
        columnDef.utype,
        value
      )
    }
  }

  var spectralUnits = {
    units: [{
        label: 'm',
        value: 'm',
        default: true
      },
      {
        label: 'cm',
        value: 'cm'
      },
      {
        label: 'mm',
        value: 'mm'
      },
      {
        label: 'um',
        value: 'um'
      },
      {
        label: 'nm',
        value: 'nm'
      },
      {
        label: 'A',
        value: 'A'
      },
      {
        label: 'Hz',
        value: 'Hz'
      },
      {
        label: 'kHz',
        value: 'kHz'
      },
      {
        label: 'MHz',
        value: 'MHz'
      },
      {
        label: 'GHz',
        value: 'GHz'
      },
      {
        label: 'eV',
        value: 'eV'
      },
      {
        label: 'keV',
        value: 'keV'
      },
      {
        label: 'MeV',
        value: 'Mev'
      },
      {
        label: 'Gev',
        value: 'Gev'
      }
    ]
  }

  var spectralProperties = {
    fitMax: true,
    converter: 'WavelengthConverter',
    valueFormatter: function (value, column) {
      return formatUnit(value, column, 'm')
    },
    formatter: function (row, cell, value, columnDef) {
      return formatOutputHTML(
        formatUnit(value, columnDef, 'm') || Number.NaN,
        columnDef.utype,
        value
      )
    },
    header: spectralUnits
  }

  // deep copy so that the properties are not shared among the different columns
  var restFrameSpectralProperties = $.extend(true, {}, spectralProperties)
  var minSpectralProperties = $.extend(true, {}, spectralProperties)
  var maxSpectralProperties = $.extend(true, {}, spectralProperties)

  var IVOAToW3CDateOptionsNarrow = {}
  $.extend(true, IVOAToW3CDateOptionsNarrow, IVOAToW3CDateOptions, {
    width: 100
  })

  var IVOAToW3CDateOptionsWide = {}
  $.extend(true, IVOAToW3CDateOptionsWide, IVOAToW3CDateOptions, {
    width: 132
  })

  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.metaRelease'],
    IVOAToW3CDateOptionsNarrow
  )
  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.dataRelease'],
    IVOAToW3CDateOptionsNarrow
  )
  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.provenance.lastExecuted'],
    IVOAToW3CDateOptionsWide
  )
  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.energy.bounds.lower'],
    minSpectralProperties
  )
  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.energy.bounds.upper'],
    maxSpectralProperties
  )
  $.extend(
    ca.nrc.cadc.search.columnOptions['caom2:Plane.energy.restwav'],
    restFrameSpectralProperties
  )

  /**
   * Format the given value as a number and match the given significant digits.
   * @param _value        The value to format.
   * @param _sigDigits    The significant digits to reduce to.
   *
   * @return  {String}
   */
  function formatNumeric(_value, _sigDigits) {
    return isNaN(_value) ?
      '' :
      new cadc.web.util.NumberFormat(_value, _sigDigits).format()
  }

  /**
   * Format to the given unit.
   *
   * @param _value              The Value to convert.
   * @param _column {object}    The column object.
   * @param _defaultUnit        The default unit to use.
   * @returns {*}
   */
  function formatUnit(_value, _column, _defaultUnit) {
    var formattedValue
    var toUnit = $(_column).data('unitValue')

    if (!toUnit) {
      toUnit = _defaultUnit
    }

    if ((typeof _value === 'number' && !isNaN(_value)) || _value) {
      try {
        var uType = _column.utype
        var columnManager = new ColumnManager()
        var converter = columnManager.getConverter(uType, _value, toUnit)
        var formatter = new ca.nrc.cadc.search.UnitConversionFormat(
          converter,
          _value,
          uType
        )

        formattedValue = formatter.formatValue()
      } catch (e) {
        console.error(e.toString())
      }
    } else {
      formattedValue = ''
    }

    return formattedValue
  }

  /**
   * Format the details link for details about the current Observation.
   * @param {String} value           The link text.
   * @param {String} publisherID  The URI of the publisher ID to build.
   * @param {{}} column          The column object.
   * @param {Number} rowNum          The row number.
   * @returns {string}
   */
  function formatDetailsCell(
    value,
    publisherID,
    column,
    rowNum
  ) {
    var $link = $('<a></a>')
    var publisherIDURI = new cadc.web.util.URI(publisherID)
    var detailsURI = new cadc.web.util.URI(
      ca.nrc.cadc.search.DETAILS_BASE_URL +
      '?ID=' +
      encodeURIComponent(publisherID.substring(0, publisherID.lastIndexOf('/')))
    )

    $link.text(value)
    $link.prop('id', column.id + '_' + rowNum + '_observation_details')
    $link
      .addClass(ca.nrc.cadc.search.DETAILS_CSS)
      .addClass('no-propagate-event')
    $link.prop('target', '_observation_details')
    $link.prop('href', detailsURI.toString())
    $link.data('publisher-id-uri', publisherIDURI)
    $link.attr('data-publisher-id-uri', publisherIDURI)

    var $cellSpan = $('<span />')
    $cellSpan.addClass('cellValue')
    $cellSpan.attr('title', "See details about '" + value + "'")
    $cellSpan.append($link)

    return $cellSpan.get(0).outerHTML
  }

  function formatDataType(value, utype) {
    var outputVal = ca.nrc.cadc.search.URI_MATCH_REGEX.test(value) ?
      new cadc.web.util.URI(value).getHash() :
      value
    return formatOutputHTML(outputVal, utype, value)
  }

  function formatOutputHTML(value, columnUType, title) {
    var htmlFormat = new ca.nrc.cadc.search.HTMLCellFormat(
      value,
      title,
      columnUType
    )

    return htmlFormat.format()
  }

  /**
   * Format the given utype value as a link for a quick search.
   *
   * @param {String} value             The value of the cell.
   * @param {{}} _searchItems      An object containing search parameter(s) on click.
   * @param {String} columnUType       The uType of the column.
   * @param {String} toUnit            The unit to convert to.
   */
  function formatQuickSearchLink(value, _searchItems, columnUType, toUnit) {
    var $output = toUnit ?
      $(format(value, columnUType, value, toUnit)) :
      $(formatOutputHTML(value, columnUType, value))

    if ($output.text()) {
      var currentURIStr = cadc.web.util.currentURI().toString()
      // Trim off existing query and tab reference
      var baseURI = new cadc.web.util.URI(
        currentURIStr.substr(0, currentURIStr.indexOf('?'))
      )

      $.each(_searchItems, function (name, value) {
        baseURI.setQueryValue(name, encodeURIComponent(value))
      })

      // Then issue the href (target) of the link.
      var $link = $('<a></a>')

      // Force open in a new window.  This MUST be set to _blank.
      $link.attr('target', '_blank')
      $link.attr('href', baseURI.toString())
      $link.addClass('quicksearch_link').addClass('no-propagate-event')
      $link.text($output.text())

      var tipOutput

      // Date columns only.
      if (columnUType.indexOf('Plane.time.bounds.samples') >= 0) {
        var delimiter = toUnit === 'MJD' ? '.' : ' '
        tipOutput = $link.text().split(delimiter)[0]
      } else {
        tipOutput = $link.text()
      }

      $output.empty()
      $output.attr('title', "Search on '" + tipOutput + "'")
      $output.append($link)
    }

    return $output.get(0).outerHTML
  }

  /**
   * Default formatter for the column data.
   *
   * @param {String|Number|Boolean} value         The value of the cell.
   * @param {String} columnUType   The uType, used only for formatting output.
   * @param {String} title         The title of the output.
   * @param {String} toUnit        The unit to convert to, if any.
   * @returns {string}
   */
  function format(value, columnUType, title, toUnit) {
    var formattedValue

    if ((typeof value === 'number' && !isNaN(value)) || value) {
      try {
        var columnManager = new ColumnManager()
        var converter = columnManager.getConverter(columnUType, value, toUnit)
        var formatter = new ca.nrc.cadc.search.UnitConversionFormat(
          converter,
          value,
          columnUType
        )

        formattedValue = formatter.format()
      } catch (e) {
        console.error(e.toString())
      }
    } else {
      formattedValue = formatOutputHTML('', columnUType, title)
    }

    return formattedValue
  }

  /**
   * @constructor
   */
  function ColumnManager() {
    /**
     * @return {{}}   Options object.
     */
    this.getColumnOptions = function () {
      return ca.nrc.cadc.search.columnOptions
    }

    /**
     * Get the options object for the given column.
     *
     * @param {String} _id     The column ID to look up.
     * @return {{}}
     * @private
     */
    this._getColumnOption = function (_id) {
      return this.getColumnOptions()[_id]
    }

    /**
     * Obtain the value converter for the column whose ID matches the given _id.
     * @param {String} _id       The ID of the column to retrieve a converter for.
     * @param {String} _value    The value to build into the converter.
     * @param {String} _unit     The unit to build into the converter.
     *
     * @return  Converter object, or null if none found.
     */
    this.getConverter = function (_id, _value, _unit) {
      var converterType = this._getColumnOption(_id).converter
      return converterType ?
        new ca.nrc.cadc.search.unitconversion[converterType](_value, _unit) :
        null
    }

    /**
     * Obtain the column ID for the given column label.  Can be called with the
     * columnID, too, to provide more robustness for the caller.
     *
     * @param {String} _label      The label to look up.
     * @returns {String}    ID value.
     */
    this.getIDFromLabel = function (_label) {
      var result = null

      // If not label is provided, don't bother iterating.
      if (_label) {
        $.each(this.getColumnOptions(), function (columnID, object) {
          if (columnID === _label || object.label === _label) {
            result = columnID
            return false
          }
        })
      }

      return result
    }

    /**
     * Format the value.
     *
     * @param {{}} _column   The column object.
     * @param {String} _value    The value to convert.
     */
    this.format = function (_column, _value) {
      var columnOption = this._getColumnOption(_column.utype)
      var formattedValue

      if (columnOption && columnOption.formatter) {
        // Should be the HTML <span> output.
        var htmlOutput = columnOption.formatter(
          null,
          null,
          _value,
          _column,
          null
        )
        formattedValue = htmlOutput ? $(htmlOutput).text() : _value
      } else {
        formattedValue = _value
      }

      return formattedValue
    }

    /**
     * Obtain an array of the filter pattern used in the given string.
     *
     * i.e. getFilterPattern("< 4") would yield ["<", "4"].
     *
     * This is used during unit conversion within ranges (WebRT 65953).
     *
     * @param {String} _string   The string to get the pattern for.
     * @return {[]}  The pattern array.
     */
    this.getFilterPattern = function (_string) {
      var breakdown = []
      var match
      var pattern =
        _string.indexOf('..') > 0 ?
        /\s*(.*)\s*(\.\.)\s*(.*)\s*/g :
        /(<=|>=|<|>|!)?\s*(.*)/g

      if ((match = pattern.exec(_string))) {
        var ml = match.length
        for (var i = 1; i < ml; i++) {
          var nextMatch = match[i]
          if (nextMatch) {
            breakdown.push(nextMatch)
          }
        }
      }

      return breakdown
    }

    /**
     * Obtain whether the given string is filter syntax or not.
     * @param {String} _string    String to check.
     * @return {boolean}
     */
    this.isFilterSyntax = function (_string) {
      return _string && /^(<|>|<=|>=|!|\.\.)$/.test($.trim(_string))
    }
  }
})(jQuery, window)
