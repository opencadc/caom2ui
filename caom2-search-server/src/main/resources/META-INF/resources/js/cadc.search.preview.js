/**
 * User: jenkinsd
 * Date: 6/12/12
 * Time: 4:27 PM
 *
 * Story 962
 */

(function ($)
{
  // register namespace
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "PREVIEW_URL_PREFIX": "/AdvancedSearch/preview",
            "Preview": Preview
          }
        }
      }
    }
  });

  /**
   * @param _collection      The Collection to use.
   * @param _observationID   The CAOM-2 Observation ID.
   * @param _productID       The Archive specific Product ID.
   * @param _size            The size of the thumbnail, usually 64, 256, or 1024
   *                         pixels.
   * @param _runID           The UWS Run ID to append to the URL.
   * @constructor
   */
  function Preview(_collection, _observationID, _productID, _size, _runID)
  {
    var _myself = this;

    this.collection = _collection;
    this.observationID = _observationID;
    this.productID = _productID;
    this.size = _size;
    this.runID = _runID;

    // The Hash of Collections.  Each one will know its Archive, and Preview URL
    // converter to use.
    var PREVIEW_COLLECTION_CONFIGURATIONS =
    {
      BLAST: {archive: "BLAST", converter: convertCADC},
      IRIS: {archive: "IRIS", converter: convertCADC},
      CGPS: {archive: "CGPS", converter: convertCADC},
      VGPS: {archive: "VGPS", converter: convertCADC},
      CFHT: {archive: "CFHT", converter: convertCFHT},
      HST: {archive: "HSTCA", converter: convertHST},
      HSTHLA: {archive: "HLADR2", converter: convertHST},
      OMM: {archive: "OMM", converter: convertHST},
      DAO: {
        archive: "DAO", converter: convertObservationIDProductID,
        collectionName: "DAO", extension: ".png"
      },
      DAOPLATES: {
        archive: "DAO", converter: convertObservationIDProductID,
        collectionName: "DAOPLATES", extension: ".png"
      },
      JCMT: {
        archive: "JCMT", converter: convertObservationIDProductID,
        collectionName: "jcmt", extension: ".png"
      },
      UKIRT: {
        archive: "UKIRT", converter: convertObservationIDProductID,
        collectionName: "ukirt", extension: ".png"
      },
      MOST: {
        archive: "MOST", converter: convertObservationIDProductID,
        collectionName: "MOST", extension: ".jpg"
      },
      GEMINI: {archive: "GEMINI", converter: convertGEMINI}
    };

    this.previewCollectionConfiguration =
      PREVIEW_COLLECTION_CONFIGURATIONS[_collection] || {};

    /*
     Methods
     */
    function getCollection()
    {
      return _myself.collection;
    }

    function getObservationID()
    {
      return _myself.observationID;
    }

    function getSize()
    {
      return _myself.size;
    }

    function getRunID()
    {
      return _myself.runID;
    }

    function getProductID()
    {
      return _myself.productID;
    }

    function getPreviewCollectionConfiguration()
    {
      return _myself.previewCollectionConfiguration;
    }

    function getConverter()
    {
      return getPreviewCollectionConfiguration().converter;
    }

    function getArchive()
    {
      return getPreviewCollectionConfiguration().archive;
    }

    /**
     * HST Preview converter.
     *
     * @return {String}       URL to obtain the preview.
     */
    function convertHST()
    {
      var fileID;

      if (getSize() <= 512)
      {
        fileID = getObservationID() + "_PREV_256.JPG";
      }
      else
      {
        fileID = getObservationID() + "_PREV.JPG";
      }

      var adURI = new cadc.web.util.URI("ad:/" + getArchive() + "/" + fileID);

      return ca.nrc.cadc.search.PREVIEW_URL_PREFIX + adURI.getPath()
             + "?logkey=preview&logvalue=Observation&runid=" + getRunID();
    }

    /**
     * GEMINI Preview converter.
     *
     * @return {String}       URL to obtain the preview.
     */
    function convertGEMINI()
    {
      var fileID = getObservationID() + "_preview_";

      if (getSize() <= 512)
      {
        fileID = fileID + "256";
      }
      else
      {
        fileID = fileID + "1024";
      }

      fileID = fileID + ".jpg";
      var adURI = new cadc.web.util.URI("ad:/" + getArchive() + "/" + fileID);

      return ca.nrc.cadc.search.PREVIEW_URL_PREFIX + adURI.getPath()
             + "?logkey=preview&logvalue=Observation&runid=" + getRunID();
    }


    /**
     * CADC Preview converter.  This is the fallback converter for most
     * archives.
     *
     * @return {String}       URL to obtain the preview.
     */
    function convertCADC()
    {
      var fileID = getObservationID() + "_preview_";

      if (getSize() <= 128)
      {
        fileID = fileID + "64";
      }
      else if (getSize() <= 512)
      {
        fileID = fileID + "256";
      }
      else
      {
        fileID = fileID + "1024";
      }

      fileID = fileID + ".png";
      var adURI = new cadc.web.util.URI("ad:/" + getArchive() + "/" + fileID);

      return ca.nrc.cadc.search.PREVIEW_URL_PREFIX + adURI.getPath()
             + "?logkey=preview&logvalue=Observation&runid=" + getRunID();
    }

    /**
     * CFHT Preview converter.  Some logic is implemented pertaining to the size
     * requested.
     *
     * @return {String}       URL to obtain the preview.
     */
    function convertCFHT()
    {
      var previewURL;

      if (getSize() <= 512)
      {
        var path = getObservationID() + "_preview_256";
        var adURI = new cadc.web.util.URI("ad:/" + getArchive() + "/" + path);
        previewURL = ca.nrc.cadc.search.PREVIEW_URL_PREFIX + adURI.getPath()
                     + "?logkey=preview&logvalue=Observation&runid="
                     + getRunID();
      }
      else
      {
        previewURL = "/cadcbin/cfht/preview.html?collectionID="
                     + getObservationID();
      }

      return previewURL;
    }

    /**
     * Preview converter by Observation ID and Product ID.  This is ONLY
     * relevant in CAOM-2, and is used by JCMT and MOST.
     *
     * This relies on the collectionName and extension to be set in the
     * PREVIEW_COLLECTION_CONFIGURATIONS dictionary above.
     *
     * @return {String}       URL to obtain the preview.
     */
    function convertObservationIDProductID()
    {
      var entry = PREVIEW_COLLECTION_CONFIGURATIONS[getCollection()];

      var fileID = entry.collectionName
                   + "_" + getObservationID()
                   + "_" + getProductID() + "_preview_";

      if (getSize() <= 128)
      {
        fileID = fileID + "64";
      }
      else if (getSize() <= 512)
      {
        fileID = fileID + "256";
      }
      else
      {
        fileID = fileID + "1024";
      }

      fileID = fileID + entry.extension;
      var adURI = new cadc.web.util.URI("ad:/" + entry.archive + "/" + fileID);

      return ca.nrc.cadc.search.PREVIEW_URL_PREFIX + adURI.getPath()
             + "?logkey=preview&logvalue=Observation&runid=" + getRunID();
    }

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
    function getPreview(_successCallback, _failCallback)
    {
      var previewConverterFunction = getConverter();

      if (previewConverterFunction)
      {
        var previewURL = previewConverterFunction();

        $.ajax({
                 method: "HEAD",
                 url: previewURL
               })
          .done(function()
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
    }

    $.extend(this,
             {
               "getPreview": getPreview,
               "getConverter": getConverter
             });
  }
})(jQuery);
