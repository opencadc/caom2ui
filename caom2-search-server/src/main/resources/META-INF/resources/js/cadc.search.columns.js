(function ($)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "DETAILS_BASE_URL": "/caom2ui/view/",
            "CAOM2_RESOLVER_VALUE_KEY": "Plane.position.bounds@Shape1Resolver.value",
            "OBSCORE_RESOLVER_VALUE_KEY": "Char.SpatialAxis.Coverage.Support.Area@Shape1Resolver.value",
            "CAOM2_TARGET_NAME_VALUE_KEY": "Plane.position.bounds@Shape1.value",
            "DETAILS_CSS": "details_tooltip_link",
            "DATALINK_URL_PREFIX": " /caom2ops/datalink",
            "columns": {
              "OBSERVATION_URI_UTYPE": "caom2:Observation.uri",
              "OBSERVATION_ID_UTYPE": "caom2:Observation.observationID",
              "ColumnManager": ColumnManager
            },
            "events": {
              "onQuickSearchLinkClicked": new jQuery.Event("onQuickSearchLinkClicked")
            },
            "options": {
              "columnOptions": {
                "caom2:Observation.observationID": {
                  "label": "Obs. ID",
                  "fitMax": true,
                  "formatter": function (row, cell, value, columnDef,
                                         dataContext)
                  {
                    var obsURI = dataContext[
                      ca.nrc.cadc.search.columns.OBSERVATION_URI_UTYPE];

                    return formatDetailsCell(value, obsURI, columnDef, row);
                  }
                },
                "caom2:Observation.uri": {
                  "label": "Preview",
                  "select": "Observation.observationURI",
                  "width": 70,
                  "sortable": false,
                  "filterable": false,
                  "formatter": function ()
                  {
                    return "<span class='cellValue preview'></span>";
                  },
                  "asyncFormatter": function (cellNode, row, dataContext)
                  {
                    var $cell = $(cellNode);

                    var planeURIValue = dataContext["caom2:Plane.uri"];

                    function createLink($cell, thumbnailURL)
                    {
                      // Clickable link to the preview page
                      var $link = $("<a>Preview</a>");
                      $link.addClass("preview_tooltip_link");
                      $link.attr("id", observationID + "_preview");
                      $link.attr("href", "#");

                      var $cellSpan = $("<span></span>");
                      $cellSpan.addClass("cellValue");
                      $cellSpan.addClass("preview");
                      $cellSpan.append($link);

                      $cell.empty().append($cellSpan);

                      // Create the thumbnail tooltip
                      var $thunbnailImage = $("<img />");
                      $thunbnailImage.attr("id", observationID +
                                                 "_256_preview");
                      $thunbnailImage.attr("src", thumbnailURL);
                      $thunbnailImage.addClass("image-actual");

                      $link.tooltipster({
                                          interactive: true,
                                          arrow: false,
                                          content: $thunbnailImage,
                                          theme: "tooltipster-preview-thumbnail",
                                          position: "right",
                                          onlyOne: true
                                        });

                      return $link;
                    }

                    function insertPreviewLink(previewUrls, thumbnailUrls,
                                               collection, observationID,
                                               productID)
                    {
                      // Create the preview link
                      if ((thumbnailUrls.length > 0)
                          || (previewUrls.length > 0))
                      {
                        var $link = createLink($cell, thumbnailUrls[0]);

                        // Display the preview window
                        $link.click(function ()
                                    {
                                      // Preview window content
                                      var $col = $("<p style=\"text-align: center;\"></p>").text("collection: " +
                                                                                                 collection);
                                      var $obsId = $("<p style=\"text-align: center;\"></p>").text("ObservationID: " +
                                                                                                   observationID);
                                      var $pdctId = $("<p style=\"text-align: center;\"></p>").text("productID: " +
                                                                                                    productID);

                                      var $previews = $("<div></div>");

                                      for (var j = 0; j < previewUrls.length;
                                           j++)
                                      {
                                        var $preview = $("<img style=\"display: block; margin: auto;\"/>");
                                        $preview.prop("id", observationID +
                                                            "_preview_" + j);
                                        $preview.prop("src", previewUrls[j]);
                                        $preview.addClass("image-actual");
                                        $previews.append($preview, "<br>");
                                      }

                                      var $content = $("<div id=\"scoped-content\"></div>")
                                        .append($col, $obsId, $pdctId, $previews);

                                      var w = window.open("", "_PREVIEW");
                                      w.document.body.innerHTML = "";

                                      // Open and close are here to stop browsers
                                      // expecting more data.
                                      // jenkinsd 2017.04.12
                                      //
                                      w.document.open();
                                      w.document.write($content.html());
                                      w.document.close();

                                      w.document.title = collection + " - " + productID;
                                      w.focus();
                                    });
                      }
                      else
                      {
                        $cell.empty();
                      }
                    }

                    if (planeURIValue)
                    {
                      var previewURI = new cadc.web.util.URI(planeURIValue);
                      var pathItems = previewURI.getPathItems();
                      var collection, observationID, productID;

                      if (pathItems.length === 3)
                      {
                        collection = pathItems[0];
                        observationID = pathItems[1];
                        productID = pathItems[2];
                      }
                      else if (pathItems.length === 2)
                      {
                        collection = pathItems[0];
                        observationID = pathItems[1];
                        productID = "";
                      }
                      else if (pathItems.length === 1)
                      {
                        collection = pathItems[0];
                        observationID = "";
                        productID = "";
                      }
                      else
                      {
                        collection = "";
                        observationID = "";
                        productID = "";
                      }

                      var runID = $("#downloadForm").find("input[name='fragment']").val().substring(6);

                      // Get the thumbnails and previews from datalink
                      $.ajax({
                               url: ca.nrc.cadc.search.DATALINK_URL_PREFIX,
                               dataType: "xml",
                               data: {
                                 id: planeURIValue,
                                 request: "downloads-only",
                                 runid: runID
                               }
                             })
                        .done(function (data, textStatus, jqXHR)
                              {
                                if (jqXHR.status === 200)
                                {
                                  var evaluator = new cadc.vot.xml.VOTableXPathEvaluator(data, "votable");

                                  // Check query status
                                  var queryStatus = evaluator.evaluate("/VOTABLE/RESOURCE[@type='results']/INFO[@name='QUERY_STATUS' and @value='OK']");
                                  if (queryStatus.length !== 0)
                                  {
                                    // Determine field indexes
                                    var accessUrlIndex;
                                    var errorMessageIndex;
                                    var semanticsIndex;
                                    var readableIndex = -1;
                                    var fields = evaluator.evaluate("/VOTABLE/RESOURCE[@type='results']/TABLE/FIELD");
                                    for (var fieldIndex = 0;
                                         fieldIndex < fields.length;
                                         fieldIndex++)
                                    {
                                      var field = fields[fieldIndex];
                                      var name = field.getAttribute("name");
                                      switch (name)
                                      {
                                        case "access_url":
                                          accessUrlIndex = fieldIndex;
                                          break;
                                        case "error_message":
                                          errorMessageIndex = fieldIndex;
                                          break;
                                        case "semantics":
                                          semanticsIndex = fieldIndex;
                                          break;
                                        case "readable":
                                          readableIndex = fieldIndex;
                                          break;
                                      }
                                    }

                                    // Loop through the table rows
                                    var thumbnailUrls = [];
                                    var previewUrls = [];
                                    var tableRows = evaluator.evaluate("/VOTABLE/RESOURCE[@type='results']/TABLE/DATA/TABLEDATA/TR");
                                    for (var trIndex = 0;
                                         trIndex < tableRows.length; trIndex++)
                                    {
                                      var tableDatas = tableRows[trIndex].children;
                                      var errorMessage = tableDatas[errorMessageIndex].textContent;

                                      if (errorMessage.length > 0)
                                      {
                                        console.error("DataLink preview error: "
                                                      + errorMessage);
                                      }
                                      else
                                      {
                                        var readable =
                                          ((readableIndex >= 0)
                                           &&
                                           tableDatas[readableIndex].textContent
                                           === 'true');
                                        if (readable === true)
                                        {
                                          var semantics = tableDatas[semanticsIndex].textContent;
                                          if (semantics ===
                                              'http://www.openadc.org/caom2#thumbnail')
                                          {
                                            thumbnailUrls.push(tableDatas[accessUrlIndex].textContent);
                                          }
                                          else if (semantics === '#preview')
                                          {
                                            previewUrls.push(tableDatas[accessUrlIndex].textContent);
                                          }
                                        }
                                      }
                                    }

                                    // If datalink didn't provide thumbnail and
                                    // preview urls, create the urls and check
                                    // if they exist.
                                    if (thumbnailUrls.length === 0
                                        || previewUrls.length === 0)
                                    {
                                      var thumbnailPreview = new ca.nrc.cadc.search.Preview(collection, observationID,
                                        productID, 256, runID);
                                      thumbnailPreview.getPreview(function (thumbnailURL)
                                                         {
                                                           var preview =
                                                             new ca.nrc.cadc.search.Preview(collection,
                                                               observationID,
                                                               productID, 1024,
                                                               runID);

                                                           preview.getPreview(
                                                             function (previewURL)
                                                             {
                                                               if (previewURL)
                                                               {
                                                                 var $link = createLink($cell, thumbnailURL);
                                                                 $link.attr("href", previewURL);
                                                                 $link.attr("target", "_PREVIEW");

                                                                 var $previewImage = $("<img />");
                                                                 $previewImage.prop("id", observationID +
                                                                                          "_256_preview");
                                                                 $previewImage.prop("src", previewURL);
                                                                 $previewImage.addClass("image-actual");
                                                               }
                                                             });

                                                         });
                                    }
                                    else
                                    {
                                      insertPreviewLink(previewUrls,
                                                        thumbnailUrls, collection,
                                                        observationID, productID);
                                    }
                                  }
                                }
                              })
                        .fail(function (jqXHR, textStatus, errorThrown)
                              {
                                if (jqXHR.status !== 404)
                                {
                                  console.error("Error >> " + errorThrown +
                                                " (" + jqXHR.status + ")");
                                }
                              });
                    }
                    else
                    {
                      $cell.empty();
                    }
                  }
                },
                "caom2:Observation.collection": {
                  "label": "Collection",
                  "fitMax": true
                },
                "caom2:Observation.algorithm.name": {
                  "label": "Algorithm Name"
                },
                "caom2:Observation.type": {
                  "label": "Obs. Type",
                  "fitMax": true
                },
                "caom2:Observation.intent": {
                  "label": "Intent"
                },
                "caom2:Observation.sequenceNumber": {
                  "label": "Sequence Number",
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, null);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, null),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Observation.environment.tau": {
                  "label": "Tau",
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, 2);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, 2),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Observation.proposal.id": {
                  "label": "Proposal ID",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    var utype = columnDef.utype;
                    var uTypeName = utype.substr(utype.indexOf(":") + 1);
                    var valueObject = {};

                    if (value)
                    {
                      valueObject[uTypeName] = value;
                    }

                    return formatQuickSearchLink(value, valueObject, utype,
                                                 null);
                  }
                },
                "caom2:Observation.proposal.pi": {
                  "label": "P.I. Name",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    var utype = columnDef.utype;
                    var uTypeName = utype.substr(utype.indexOf(":") + 1);
                    var valueObject = {};

                    if (value)
                    {
                      valueObject[uTypeName] = value;
                    }

                    return formatQuickSearchLink(value, valueObject, utype,
                                                 null);
                  }
                },
                "caom2:Observation.proposal.title": {
                  "label": "Proposal Title"
                },
                // This is a typo in the database!  PLEASE make sure this is the
                // only reference to 'proprosal' instead of proposal.
                "caom2:Observation.proprosal.project": {
                  "label": "Proposal Project",
                  "select": "Observation.proposal_project",
                  "extended": true
                },
                "caom2:Observation.proposal.keywords": {
                  "label": "Proposal Keywords"
                },
                "caom2:Observation.target.name": {
                  "label": "Target Name",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    var utype = columnDef.utype;
                    var valueObject = {};

                    if (value)
                    {
                      valueObject["Plane.position.bounds"] = value;
                      valueObject[ca.nrc.cadc.search.CAOM2_RESOLVER_VALUE_KEY]
                        = "NONE";
                    }

                    return formatQuickSearchLink(value, valueObject, utype,
                                                 null);
                  }
                },
                "caom2:Observation.target.type": {
                  "label": "Target Type"
                },
                "caom2:Observation.target.moving": {
                  "label": "Moving Target",
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, 2);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, 2),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Observation.target.standard": {
                  "label": "Target Standard",
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, 2);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, 2),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Observation.instrument.name": {
                  "label": "Instrument"
                },
                "caom2:Observation.instrument.keywords": {
                  "label": "Instrument Keywords",
                  "fitMax": true
                },
                "caom2:Plane.id": {
                  "label": "planeID",
                  "select": "Plane.planeID",
                  "extended": true
                },
                "caom2:Plane.uri": {
                  "label": "CAOM Plane URI",
                  "select": "Plane.planeURI"
                },
                "caom2:Plane.uri.downloadable": {
                  "label": "DOWNLOADABLE",
                  "select": "isDownloadable(Plane.planeURI)"
                },
                "caom2:Plane.productID": {
                  "label": "Product ID",
                  "fitMax": true,
                  "formatter": function (row, cell, value, columnDef,
                                         dataContext)
                  {
                    var obsURI = dataContext[
                      ca.nrc.cadc.search.columns.OBSERVATION_URI_UTYPE];

                    return formatDetailsCell(value, obsURI, columnDef, row);
                  }
                },
                "caom2:Plane.metaRelease": {
                  "label": "Meta Release",
                  "extended": true
                },
                "caom2:Plane.dataRelease": {
                  "label": "Data Release"
                },
                "caom2:Plane.dataProductType": {
                  "label": "Data Type",
                  "fitMax": false,
                  "width": 65,
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatCalibrationName(value, columnDef.utype);
                  }
                },
                "caom2:Plane.calibrationLevel": {
                  "label": "Cal. Lev.",
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, null);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, null),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Plane.provenance.name": {
                  "label": "Provenance Name"
                },
                "caom2:Plane.provenance.version": {
                  "label": "Prov. Version"
                },
                "caom2:Plane.provenance.reference": {
                  "label": "Prov. Reference",
                  "extended": true
                },
                "caom2:Plane.provenance.producer": {
                  "label": "Prov. Producer",
                  "extended": true
                },
                "caom2:Plane.provenance.project": {
                  "label": "Prov. Project"
                },
                "caom2:Plane.provenance.runID": {
                  "label": "Prov. Run ID"
                },
                "caom2:Plane.provenance.lastExecuted": {
                  "label": "Prov. Last Executed"
                },
                "caom2:Plane.provenance.keywords": {
                  "label": "Provenance Keywords",
                  "extended": true
                },
                "caom2:Plane.provenance.inputs": {
                  "label": "Prov. Inputs",
                  "extended": true
                },
                "caom2:Plane.position.bounds": {
                  "label": "Polygon"
                },
                "caom2:Plane.energy.resolvingPower": {
                  "label": "Resolving Power",
                  "width": 117,
                  "valueFormatter": function (value)
                  {
                    return formatNumeric(value, 2);
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatNumeric(value, 2),
                                            columnDef.utype, value);
                  }
                },
                "caom2:Plane.position.bounds.cval1": {
                  "label": "RA (J2000.0)",
                  "select": "COORD1(CENTROID(Plane.position.bounds))",
                  // Fit max would be nice, but the original data is in a long
                  // decimal format, so set it manually.
                  "fitMax": false,
                  "width": 105,
                  "converter": "RAConverter",
                  "valueFormatter": function (value, column)
                  {
                    return formatUnit(value, column, "hms");
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatUnit(value, columnDef, "hms")
                                            || Number.NaN, columnDef.utype,
                                            value);
                  },
                  "header": {
                    "units": [
                      {
                        "label": "H:M:S",
                        "value": "hms",
                        "default": true
                      },
                      {
                        "label": "Degrees",
                        "value": "DEGREES"
                      }
                    ]
                  }
                },
                "caom2:Plane.position.bounds.cval2": {
                  "label": "Dec. (J2000.0)",
                  "select": "COORD2(CENTROID(Plane.position.bounds))",
                  // Fit max would be nice, but the original data is in a long
                  // decimal format, so set it manually.
                  "fitMax": false,
                  "width": 110,
                  "converter": "DECConverter",
                  "valueFormatter": function (value, column)
                  {
                    return formatUnit(value, column, "dms");
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatUnit(value, columnDef, "dms")
                                            || Number.NaN, columnDef.utype,
                                            value);
                  },
                  "header": {
                    "units": [
                      {
                        "label": "D:M:S",
                        "value": "dms",
                        "default": true
                      },
                      {
                        "label": "Degrees",
                        "value": "DEGREES"
                      }
                    ]
                  }
                },
                "caom2:Plane.position.bounds.area": {
                  "label": "Field of View",
                  "datatype": "double",
                  "unit": "deg**2",
                  "select": "AREA(Plane.position.bounds)",
                  // Fit max would be nice, but the original data is in a long
                  // decimal format, so set it manually.
                  "fitMax": false,
                  "width": 100,
                  "converter": "AreaConverter",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return format(value, columnDef.utype, value,
                                  $(columnDef).data("unitValue"));
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Sq. arcsec",
                        "value": "sec**2"
                      },
                      {
                        "label": "Sq. arcmin",
                        "value": "min**2"
                      },
                      {
                        "label": "Sq. deg",
                        "value": "deg**2",
                        "default": true
                      }
                    ]
                  }
                },
                "caom2:Plane.position.resolution": {
                  "label": "IQ",
                  "fitMax": true,
                  "width": 105,
                  "converter": "AngleConverter",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return format(value, columnDef.utype, value,
                                  $(columnDef).data("unitValue"));
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Milliarcseconds",
                        "value": "milliarcsec"
                      },
                      {
                        "label": "Arcseconds",
                        "value": "arcsec",
                        "default": true
                      },
                      {
                        "label": "Arcminutes",
                        "value": "arcmin"
                      }
                    ]
                  }
                },
                "caom2:Plane.position.sampleSize": {
                  "label": "Pixel Scale",
                  // Fit max would be nice, but the original data is in a long
                  // decimal format, so set it manually.
                  "fitMax": false,
                  "width": 110,
                  "converter": "AngleConverter",
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return format(value, columnDef.utype, value,
                                  $(columnDef).data("unitValue"));
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Milliarcseconds",
                        "value": "milliarcsec"
                      },
                      {
                        "label": "Arcseconds",
                        "value": "arcsec",
                        "default": true
                      },
                      {
                        "label": "Arcminutes",
                        "value": "arcmin"
                      },
                      {
                        "label": "Degrees",
                        "value": "degrees"
                      }
                    ]
                  }
                },
                "caom2:Observation.requirements.flag": {
                  "label": "Quality",
                  "fitMax": true
                },
                "caom2:Plane.energy.emBand": {
                  "label": "Band",
                  "fitMax": true
                },
                "caom2:Plane.energy.bounds.lower": {
                  "select": "Plane.energy_bounds_lower",

                  "label": "Min. Wavelength"
                },
                "caom2:Plane.energy.bounds.upper": {
                  "select": "Plane.energy_bounds_upper",
                  "label": "Max. Wavelength"
                },
                "caom2:Plane.energy.bandpassName": {
                  "label": "Filter",
                  "fitMax": true
                },
                "caom2:Plane.energy.transition.species": {
                  "label": "Molecule"
                },
                "caom2:Plane.energy.transition.transition": {
                  "label": "Transition"
                },
                "caom2:Plane.energy.restwav": {
                  "label": "Rest-frame Energy"
                },
                "caom2:Plane.time.bounds.lower": {
                  "select": "Plane.time_bounds_lower",
                  "label": "Start Date",
                  // Fit max would be nice, but the default values are in MJD,
                  // which is smaller than the Calendar dates, so set it up for
                  // Calendar instead.
                  "fitMax": false,
                  "width": 145,
                  "converter": "DateConverter",
                  "valueFormatter": function (value, column)
                  {
                    return formatUnit(value, column, "IVOA");
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    var searchValue = {};

                    if (value)
                    {
                      var intValue = parseInt(value);
                      searchValue["Plane.time.bounds"] =
                        (intValue + ".." + (intValue + 1));
                    }

                    return formatQuickSearchLink(value, searchValue,
                                                 columnDef.utype,
                                                 $(columnDef).data("unitValue"));
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Calendar",
                        "value": "IVOA",
                        "default": true
                      },
                      {
                        "label": "MJD",
                        "value": "MJD"
                      }
                    ]
                  }
                },
                "caom2:Plane.time.bounds.upper": {
                  "select": "Plane.time_bounds_upper",
                  "label": "End Date",
                  // Fit max would be nice, but the default values are in MJD,
                  // which is smaller than the Calendar dates, so set it up for
                  // Calendar instead.
                  "fitMax": false,
                  "width": 145,
                  "converter": "DateConverter",
                  "valueFormatter": function (value, column)
                  {
                    return formatUnit(value, column, "IVOA");
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatUnit(value, columnDef, "IVOA")
                                            || Number.NaN, columnDef.utype,
                                            value);
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Calendar",
                        "value": "IVOA",
                        "default": true
                      },
                      {
                        "label": "MJD",
                        "value": "MJD"
                      }
                    ]
                  }
                },
                "caom2:Plane.time.exposure": {
                  "label": "Int. Time",
                  // Fitmax would be nice, but the original value doesn't
                  // conform. Use a width instead.
                  "fitMax": false,
                  "width": 80,
                  "converter": "TimeConverter",
                  "valueFormatter": function (value, column)
                  {
                    return formatUnit(value, column, "SECONDS");
                  },
                  "formatter": function (row, cell, value, columnDef)
                  {
                    return formatOutputHTML(formatUnit(value, columnDef,
                                                       "SECONDS")
                                            || Number.NaN, columnDef.utype,
                                            value);
                  },
                  "header": {
                    "units": [
                      {
                        "label": "Seconds",
                        "value": "SECONDS",
                        "default": true
                      },
                      {
                        "label": "Minutes",
                        "value": "MINUTES"
                      },
                      {
                        "label": "Hours",
                        "value": "HOURS"
                      },
                      {
                        "label": "Days",
                        "value": "DAYS"
                      }
                    ]
                  }
                },
                "obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter": {
                  "fitMax": true,
                  "label": "s_fov",
                  "select": "s_fov"
                },
                "obscore:Char.SpatialAxis.Coverage.Support.Area": {
                  "fitMax": true,
                  "label": "s_region",
                  "select": "s_region"
                },
                "obscore:Char.SpatialAxis.Resolution.refval.value": {
                  "fitMax": true,
                  "label": "s_resolution",
                  "select": "s_resolution"
                },
                "obscore:Char.SpatialAxis.numBins1": {
                  "fitMax": true,
                  "label": "s_xel1",
                  "select": "s_xel1"
                },
                "obscore:Char.SpatialAxis.numBins2": {
                  "fitMax": true,
                  "label": "s_xel2",
                  "select": "s_xel2"
                },
                "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime": {
                  "fitMax": true,
                  "label": "t_min",
                  "select": "t_min"
                },
                "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StopTime": {
                  "fitMax": true,
                  "label": "t_max",
                  "select": "t_max"
                },
                "obscore:Curation.PublisherDID": {
                  "fitMax": true,
                  "label": "obs_publisher_did",
                  "select": "obs_publisher_did"
                },
                "obscore:DataID.Collection": {
                  "fitMax": true,
                  "label": "obs_collection",
                  "select": "obs_collection"
                },
                "obscore:Provenance.ObsConfig.Facility.name": {
                  "fitMax": true,
                  "label": "facility_name",
                  "select": "facility_name"
                },
                "obscore:Provenance.ObsConfig.Instrument.name": {
                  "fitMax": true,
                  "label": "instrument_name",
                  "select": "instrument_name"
                },
                "obscore:DataID.observationID": {
                  "fitMax": true,
                  "label": "obs_id",
                  "select": "obs_id"
                },
                "obscore:ObsDataset.dataProductType": {
                  "label": "dataproduct_type",
                  "select": "dataproduct_type"
                },
                "obscore:ObsDataset.calibLevel": {
                  "label": "calib_level",
                  "select": "calib_level"
                },
                "obscore:Curation.releaseDate": {
                  "label": "obs_release_date",
                  "select": "obs_release_date"
                },
                "obscore:Target.Name": {
                  "label": "target_name",
                  "select": "target_name"
                },
                "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1": {
                  "label": "s_ra",
                  "select": "s_ra"
                },
                "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2": {
                  "label": "s_dec",
                  "select": "s_dec"
                },
                "obscore:Char.TimeAxis.Coverage.Support.Extent": {
                  "label": "t_exptime",
                  "select": "t_exptime"
                },
                "obscore:Char.TimeAxis.Resolution.refval.value": {
                  "label": "t_resolution",
                  "select": "t_resolution"
                },
                "obscore:Char.TimeAxis.numBins": {
                  "label": "t_xel",
                  "select": "t_xel"
                },
                "obscore:Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit": {
                  "label": "em_min",
                  "select": "em_min"
                },
                "obscore:Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit": {
                  "label": "em_max",
                  "select": "em_max"
                },
                "obscore:Char.SpectralAxis.Resolution.ResolPower.refval": {
                  "label": "em_res_power",
                  "select": "em_res_power"
                },
                "obscore:Char.SpectralAxis.numBins": {
                  "label": "em_xel",
                  "select": "em_xel"
                },
                "obscore:Char.SpectralAxis.ucd": {
                  "label": "em_ucd",
                  "select": "em_ucd"
                },
                "obscore:Char.PolarizationAxis.stateList": {
                  "label": "pol_states",
                  "select": "pol_states"
                },
                "obscore:Char.PolarizationAxis.numBins": {
                  "label": "pol_xel",
                  "select": "pol_xel"
                },
                "obscore:Char.ObservableAxis.ucd": {
                  "label": "o_ucd",
                  "select": "o_ucd"
                },
                "obscore:Access.Reference": {
                  "label": "access_url",
                  "select": "access_url"
                },
                "obscore:Access.Format": {
                  "label": "access_format",
                  "select": "access_format"
                },
                "obscore:Access.Size": {
                  "label": "access_estsize",
                  "select": "access_estsize"
                },
                "obscore:Curation.PublisherDID.downloadable": {
                  "label": "downloadable",
                  "select": "isDownloadable(obs_publisher_did)"
                }
              }
            }
          }
        }
      }
    }
  });

  /**
   * Format the given value between IVOA and W3C date formats.
   * @param value       The IVOA String date.
   * @returns {String}  Formatted string value.
   */
  function formatIVOAToW3CDateValue(value)
  {
    var val;

    if (value)
    {
      var date =
        moment(value, ca.nrc.cadc.search.formats.date.ISO_MS).toDate();

      var dateFormat =
        new ca.nrc.cadc.search.DateFormat(date,
          ca.nrc.cadc.search.formats.date.W3C);
      val = dateFormat.format();
    }
    else
    {
      val = "";
    }

    return val;
  }

  var IVOAToW3CDateOptions = {
    // Fit max would be nice, but the default values are in IVOA, which is
    // smaller than the Calendar dates, so set it up for Calendar
    // instead.
    "fitMax": false,
    "valueFormatter": function (value)
    {
      return formatIVOAToW3CDateValue(value);
    },
    "formatter": function (row, cell, value, columnDef)
    {
      return formatOutputHTML(formatIVOAToW3CDateValue(value), columnDef.utype,
                              value);
    }
  };

  var spectralUnits = {
    "units": [
      {
        "label": "m",
        "value": "m",
        "default": true
      },
      {
        "label": "cm",
        "value": "cm"
      },
      {
        "label": "mm",
        "value": "mm"
      },
      {
        "label": "um",
        "value": "um"
      },
      {
        "label": "nm",
        "value": "nm"
      },
      {
        "label": "A",
        "value": "A"
      },
      {
        "label": "Hz",
        "value": "Hz"
      },
      {
        "label": "kHz",
        "value": "kHz"
      },
      {
        "label": "MHz",
        "value": "MHz"
      },
      {
        "label": "GHz",
        "value": "GHz"
      },
      {
        "label": "eV",
        "value": "eV"
      },
      {
        "label": "keV",
        "value": "keV"
      },
      {
        "label": "MeV",
        "value": "Mev"
      },
      {
        "label": "Gev",
        "value": "Gev"
      }
    ]
  };

  var spectralProperties = {
    "fitMax": true,
    "converter": "WavelengthConverter",
    "valueFormatter": function (value, column)
    {
      return formatUnit(value, column, "m");
    },
    "formatter": function (row, cell, value, columnDef)
    {
      return formatOutputHTML(formatUnit(value, columnDef, "m")
                              || Number.NaN, columnDef.utype,
                              value);
    },
    "header": spectralUnits
  };

  // deep copy so that the properties are not shared among the different columns
  var restFrameSpectralProperties = $.extend(true, {}, spectralProperties);
  var minSpectralProperties = $.extend(true, {}, spectralProperties);
  var maxSpectralProperties = $.extend(true, {}, spectralProperties);

  var IVOAToW3CDateOptionsNarrow = {};
  $.extend(true, IVOAToW3CDateOptionsNarrow, IVOAToW3CDateOptions,
           {width: 100});

  var IVOAToW3CDateOptionsWide = {};
  $.extend(true, IVOAToW3CDateOptionsWide, IVOAToW3CDateOptions,
           {width: 132});

  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.metaRelease"], IVOAToW3CDateOptionsNarrow);
  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.dataRelease"], IVOAToW3CDateOptionsNarrow);
  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.provenance.lastExecuted"], IVOAToW3CDateOptionsWide);
  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.energy.bounds.lower"], minSpectralProperties);
  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.energy.bounds.upper"], maxSpectralProperties);
  $.extend(ca.nrc.cadc.search.options.columnOptions["caom2:Plane.energy.restwav"], restFrameSpectralProperties);

  /**
   * Format the given value as a number and match the given significant digits.
   * @param _value        The value to format.
   * @param _sigDigits    The significant digits to reduce to.
   *
   * @return  {String}
   */
  function formatNumeric(_value, _sigDigits)
  {
    return isNaN(_value) ? ""
      : (new cadc.web.util.NumberFormat(_value, _sigDigits)).format();
  }

  /**
   * Format to the given unit.
   *
   * @param _value              The Value to convert.
   * @param _column {object}    The column object.
   * @param _defaultUnit        The default unit to use.
   * @returns {*}
   */
  function formatUnit(_value, _column, _defaultUnit)
  {
    var formattedValue;
    var toUnit = $(_column).data("unitValue");

    if (!toUnit)
    {
      toUnit = _defaultUnit;
    }

    if ((typeof(_value) === "number" && !isNaN(_value)) || (_value))
    {
      try
      {
        var uType = _column.utype;
        var columnManager = new ColumnManager();
        var converter = columnManager.getConverter(uType, _value, toUnit);
        var formatter =
          new ca.nrc.cadc.search.UnitConversionFormat(converter, _value, uType);

        formattedValue = formatter.formatValue();
      }
      catch (e)
      {
        console.error(e.toString());
      }
    }
    else
    {
      formattedValue = "";
    }

    return formattedValue;
  }

  /**
   * Format the details link for details about the current Observation.
   * @param value           The link text.
   * @param observationURI  The URI of the observation to build.
   * @param column          The column objects.
   * @param rowNum          The row number.
   * @returns {string}
   */
  function formatDetailsCell(value, observationURI, column, rowNum)
  {
    var $link = $("<a></a>");
    var obsURI = new cadc.web.util.URI(observationURI);
    var detailsURI = new cadc.web.util.URI(ca.nrc.cadc.search.DETAILS_BASE_URL
                                           + obsURI.getPath());

    $link.text(value);
    $link.prop("id", column.id + "_" + rowNum + "_observation_details");
    $link.addClass(ca.nrc.cadc.search.DETAILS_CSS);
    $link.prop("target", "_observation_details");
    $link.prop("href", detailsURI.toString());
    $link.data("observation-uri", observationURI);
    $link.attr("data-observation-uri", observationURI);

    var $cellSpan = $("<span />");
    $cellSpan.addClass("cellValue");
    $cellSpan.attr("title", "See details about '" + value + "'");
    $cellSpan.append($link);

    return $cellSpan.get(0).outerHTML;
  }

  function getCalibrationLevelName(numericKey)
  {
    if (numericKey && ($.trim(numericKey).length > 0))
    {
      return ca.nrc.cadc.search.datatrain.CALIBRATION_LEVEL_MAP[numericKey];
    }
    else
    {
      return null;
    }
  }

  function formatCalibrationName(value, utype)
  {
    var levelName = getCalibrationLevelName(value);
    var titleStringUtil =
      new cadc.web.util.StringUtil("(" + value + ") " + levelName);
    var title = levelName ? titleStringUtil.sanitize() : "";

    return formatOutputHTML(value, utype, title);
  }

  function formatOutputHTML(value, columnUType, title)
  {
    var htmlFormat = new ca.nrc.cadc.search.HTMLCellFormat(value, title, columnUType);

    return htmlFormat.format();
  }

  /**
   * Format the given utype value as a link for a quick search.
   *
   * @param value             The value of the cell.
   * @param _searchItems      An object containing search parameter(s) on click.
   * @param columnUType       The uType of the column.
   * @param toUnit            The unit to convert to.
   */
  function formatQuickSearchLink(value, _searchItems, columnUType, toUnit)
  {
    var $output = (toUnit ? $(format(value, columnUType, value, toUnit))
      : $(formatOutputHTML(value, columnUType, value)));

    if ($output.text())
    {
      var currentURI = cadc.web.util.currentURI();

      $.each(_searchItems, function (name, value)
      {
        currentURI.setQueryValue(name, encodeURIComponent(value));
      });

      // Then issue the href (target) of the link.
      var $link = $("<a></a>");

      // Force open in a new window.  This MUST be set to _blank.
      $link.attr("target", "_blank");
      $link.attr("href", currentURI.toString());
      $link.addClass("quicksearch_link");
      $link.text($output.text());

      var tipOutput;

      // Date columns only.
      if (columnUType.indexOf("Plane.time.bounds") >= 0)
      {
        var delimiter = (toUnit === "MJD") ? "." : " ";
        tipOutput = $link.text().split(delimiter)[0];
      }
      else
      {
        tipOutput = $link.text();
      }

      $output.empty();
      $output.attr("title", "Search on '" + tipOutput + "'");
      $output.append($link);
    }

    return $output.get(0).outerHTML;
  }

  /**
   * Default formatter for the column data.
   *
   * @param value         The value of the cell.
   * @param columnUType   The uType, used only for formatting output.
   * @param title         The title of the output.
   * @param toUnit        The unit to convert to, if any.
   * @returns {string}
   */
  function format(value, columnUType, title, toUnit)
  {
    var formattedValue;

    if ((typeof(value) === "number" && !isNaN(value)) || (value))
    {
      try
      {
        var columnManager = new ColumnManager();
        var converter = columnManager.getConverter(columnUType, value, toUnit);
        var formatter =
          new ca.nrc.cadc.search.UnitConversionFormat(converter, value,
            columnUType);

        formattedValue = formatter.format();
      }
      catch (e)
      {
        console.error(e.toString());
      }
    }
    else
    {
      formattedValue = formatOutputHTML("", columnUType, title);
    }

    return formattedValue;
  }

  function ColumnManager()
  {
    function getOptions()
    {
      return ca.nrc.cadc.search.options;
    }

    function getColumnOption(_id)
    {
      return getOptions().columnOptions[_id];
    }

    /**
     * Obtain the value converter for the column whose ID matches the given _id.
     * @param _id       The ID of the column to retrieve a converter for.
     * @param _value    The value to build into the converter.
     * @param _unit     The unit to build into the converter.
     *
     * @return  Converter object, or null if none found.
     */
    function getConverter(_id, _value, _unit)
    {
      var converterType = getColumnOption(_id).converter;

      return (converterType
        ? new ca.nrc.cadc.search.unitconversion[converterType](_value, _unit)
        : null);
    }

    /**
     * Obtain the column ID for the given column label.  Can be called with the
     * columnID, too, to provide more robustness for the caller.
     *
     * @param _label      The label to look up.
     * @returns {String}
     */
    function getIDFromLabel(_label)
    {
      var result = null;

      // If not label is provided, don't bother iterating.
      if (_label)
      {
        $.each(getOptions().columnOptions, function (columnID, object)
        {
          if ((columnID === _label) || (object.label === _label))
          {
            result = columnID;
            return false;
          }
        });
      }

      return result;
    }

    /**
     * Format
     * @param _column   The UType of the column to convert.
     * @param _value    The value to convert.
     */
    function format(_column, _value)
    {
      var columnOption = getColumnOption(_column.utype);
      var formattedValue;

      if (columnOption && columnOption.formatter)
      {
        // Should be the HTML <span> output.
        var htmlOutput = columnOption.formatter(null, null, _value, _column,
                                                null);

        if (htmlOutput)
        {
          formattedValue = $(htmlOutput).text();
        }
        else
        {
          formattedValue = _value;
        }
      }
      else
      {
        formattedValue = _value;
      }

      return formattedValue;
    }

    /**
     * Obtain an array of the filter pattern used in the given string.
     *
     * i.e. getFilterPattern("< 4") would yield ["<", "4"].
     *
     * This is used during unit conversion within ranges (WebRT 65953).
     *
     * @param _string   The string to get the pattern for.
     * @return {Array}  The pattern array.
     */
    function getFilterPattern(_string)
    {
      var breakdown = [];
      var match;
      var pattern;

      if (_string.indexOf("..") > 0)
      {
        pattern = /\s*(.*)\s*(\.\.)\s*(.*)\s*/g;
      }
      else
      {
        pattern = /(<=|>=|<|>|!)?\s*(.*)/g;
      }

      if ((match = pattern.exec(_string)))
      {
        for (var i = 1; i < match.length; i++)
        {
          var nextMatch = match[i];
          if (nextMatch)
          {
            breakdown.push(nextMatch);
          }
        }
      }

      return breakdown;
    }

    /**
     * Obtain whether the given string is filter syntax or not.
     * @param _string
     */
    function isFilterSyntax(_string)
    {
      return _string && /^(<|>|<=|>=|!|\.\.)$/.test($.trim(_string));
    }


    $.extend(this,
             {
               "getOptions": getOptions,
               "getConverter": getConverter,
               "getIDFromLabel": getIDFromLabel,
               "getFilterPattern": getFilterPattern,
               "isFilterSyntax": isFilterSyntax,
               "format": format,
               "formatDetailsCell": formatDetailsCell
             });
  }

})(jQuery);
