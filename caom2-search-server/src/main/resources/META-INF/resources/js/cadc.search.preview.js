/**
 * User: jenkinsd
 * Date: 6/12/12
 * Time: 4:27 PM
 *
 * Story 962
 */

(function ($, window)
{
  // register namespace
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "Preview": Preview
          }
        }
      }
    }
  });

  /**
   * @param {String} _collection      The Collection to use.
   * @param {String} _observationID   The CAOM-2 Observation ID.
   * @param {String} _productID       The Archive specific Product ID.
   * @param {Number}  _size            The size of the thumbnail, usually 64, 256, or 1024
   *                         pixels.
   * @param {String} _runID           The UWS Run ID to append to the URL.
   * @param {String} [_endpoint]  The endpoint for previews service.  This is not configurable yet!
   * @constructor
   */
  function Preview(_collection, _observationID, _productID, _size, _runID, _endpoint)
  {
    this.collection = _collection;
    this.observationID = _observationID;
    this.productID = _productID;
    this.size = _size;
    this.runID = _runID;
    this.endpoint = _endpoint || "/search/preview";


    /**
     * Obtain the current archive.
     *
     * @return {string}
     * @private
     */
    this._getArchive = function ()
    {
      return this.previewCollectionConfiguration.archive;
    };

    /**
     * HST Preview converter.
     *
     * @return {String}       URL to obtain the preview.
     * @private
     */
    this._convertHST = function ()
    {
      var fileID = this.observationID + ((this.size <= 512) ? "_PREV_256.JPG" : "_PREV.JPG");
      var adURI = new cadc.web.util.URI("ad:/" + this._getArchive() + "/" + fileID);

      return adURI.getPath() + "?logkey=preview&logvalue=Observation&runid=" + this.runID;
    };

    /**
     * GEMINI Preview converter.
     *
     * @return {String}       URL to obtain the preview.
     * @private
     */
    this._convertGEMINI = function ()
    {
      var fileID = this.observationID + "_preview_" + ((this.size <= 512) ? "256" : "1024") + ".jpg";
      var adURI = new cadc.web.util.URI("ad:/" + this._getArchive() + "/" + fileID);

      return adURI.getPath() + "?logkey=preview&logvalue=Observation&runid=" + this.runID;
    };

    /**
     * CADC Preview converter.  This is the fallback converter for most archives.
     *
     * @return {String}       URL to obtain the preview.
     * @private
     */
    this._convertCADC = function ()
    {
      var fileID = this.observationID + "_preview_";

      if (this.size <= 128)
      {
        fileID = fileID + "64";
      }
      else if (this.size <= 512)
      {
        fileID = fileID + "256";
      }
      else
      {
        fileID = fileID + "1024";
      }

      fileID = fileID + ".png";
      var adURI = new cadc.web.util.URI("ad:/" + this._getArchive() + "/" + fileID);

      return adURI.getPath() + "?logkey=preview&logvalue=Observation&runid=" + this.runID;
    };

    /**
     * CFHT Preview converter.  Some logic is implemented pertaining to the size
     * requested.
     *
     * @return {String}       URL to obtain the preview.
     */
    this._convertCFHT = function ()
    {
      var previewURL;

      if (this.size <= 512)
      {
        var path = this.observationID + "_preview_256";
        var adURI = new cadc.web.util.URI("ad:/" + this._getArchive() + "/" + path);
        previewURL = adURI.getPath() + "?logkey=preview&logvalue=Observation&runid=" + this.runID;
      }
      else
      {
        previewURL = "/cadcbin/cfht/preview.html?collectionID=" + this.observationID;
      }

      return previewURL;
    };

    /**
     * Preview converter by Observation ID and Product ID.  This is ONLY
     * relevant in CAOM-2, and is used by JCMT and MOST.
     *
     * This relies on the collectionName and extension to be set in the PREVIEW_COLLECTION_CONFIGURATIONS dictionary
     * above.
     *
     * @return {String}       Path suffix to obtain the preview.
     * @private
     */
    this._convertObservationIDProductID = function ()
    {
      var entry = this.previewCollectionConfiguration;

      var fileID = entry.collectionName + "_" + this.observationID + "_" + this.productID + "_preview_";

      if (this.size <= 128)
      {
        fileID = fileID + "64";
      }
      else if (this.size <= 512)
      {
        fileID = fileID + "256";
      }
      else
      {
        fileID = fileID + "1024";
      }

      fileID = fileID + entry.extension;
      var adURI = new cadc.web.util.URI("ad:/" + entry.archive + "/" + fileID);

      return adURI.getPath() + "?logkey=preview&logvalue=Observation&runid=" + this.runID;
    };

    /**
     * @return {function} converter function to call later on.
     */
    this.getConverter = function ()
    {
      return this.previewCollectionConfiguration.converter;
    };

    /**
     * Convert the given elements into a Preview URL, and make a HEAD request to the
     * Data Web Service to see if it exists.  If it does, then issue the provided
     * callback.
     *
     * The callback function will be called with JSON containing the URL for the
     * Preview, or an error object and the status of the request.
     *
     * @param _successCallback    Function for successful check.
     * @param _failCallback    Function for failed check.
     * @return  The preview url, or null if none exists.
     */
    this.getPreview = function (_successCallback, _failCallback)
    {
      var previewConverterFunction = this.getConverter().bind(this);

      if (previewConverterFunction)
      {
        var previewURL = this.endpoint + previewConverterFunction();

        $.ajax({
                 method: "HEAD",
                 url: previewURL
               })
            .done(function ()
                  {
                    _successCallback(previewURL);
                  })
            .fail(function (jqXHR, textStatus, errorThrown)
                  {
                    if (_failCallback)
                    {
                      _failCallback(jqXHR.status, errorThrown);
                    }
                  });
      }
    };

    // The Hash of Collections.  Each one will know its Archive, and Preview URL
    // converter to use.
    this.PREVIEW_COLLECTION_CONFIGURATIONS =
        {
          BLAST: {archive: "BLAST", converter: this._convertCADC},
          IRIS: {archive: "IRIS", converter: this._convertCADC},
          CGPS: {archive: "CGPS", converter: this._convertCADC},
          VGPS: {archive: "VGPS", converter: this._convertCADC},
          CFHT: {archive: "CFHT", converter: this._convertCFHT},
          HST: {archive: "HSTCA", converter: this._convertHST},
          HSTHLA: {archive: "HLADR2", converter: this._convertHST},
          OMM: {archive: "OMM", converter: this._convertHST},
          DAO: {
            archive: "DAO", converter: this._convertObservationIDProductID,
            collectionName: "DAO", extension: ".png"
          },
          DAOPLATES: {
            archive: "DAO", converter: this._convertObservationIDProductID,
            collectionName: "DAOPLATES", extension: ".png"
          },
          JCMT: {
            archive: "JCMT", converter: this._convertObservationIDProductID,
            collectionName: "jcmt", extension: ".png"
          },
          UKIRT: {
            archive: "UKIRT", converter: this._convertObservationIDProductID,
            collectionName: "ukirt", extension: ".png"
          },
          MOST: {
            archive: "MOST", converter: this._convertObservationIDProductID,
            collectionName: "MOST", extension: ".jpg"
          },
          GEMINI: {archive: "GEMINI", converter: this._convertGEMINI}
        };

    this.previewCollectionConfiguration = this.PREVIEW_COLLECTION_CONFIGURATIONS[this.collection] || {};
  }
})(jQuery, window);
