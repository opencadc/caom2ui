;(function($, window) {
  'use strict'
  // register namespace
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            Preview: Preview
          }
        }
      }
    }
  })

  /**
   * @param {String} _collection      The Collection to use.
   * @param {String} _observationID   The CAOM-2 Observation ID.
   * @param {String} _productID       The Archive specific Product ID.
   * @param {Number}  _size            The size of the thumbnail, usually 256, or 1024 pixels.
   * @param {String} _runID           The UWS Run ID to append to the URL.
   * @param {String} [_endpoint]  The endpoint for previews service.  This is not configurable yet!
   * @constructor
   */
  function Preview(
    _collection,
    _observationID,
    _productID,
    _size,
    _runID,
    _endpoint
  ) {
    this.collection = _collection
    this.observationID = _observationID
    this.productID = _productID
    this.size = _size
    this.runID = _runID
    this.endpoint = _endpoint || ca.nrc.cadc.search.services.previewsEndpoint

    /**
     * Obtain the current archive.
     *
     * @return {string}
     * @private
     */
    this._getArchive = function() {
      return this.previewCollectionConfiguration.archive
    }

    /**
     * HST Preview converter.
     *
     * @return {String}       URL to obtain the preview.
     * @private
     */
    this._convertHST = function() {
      var fileID =
        this.observationID + (this.size <= 512 ? '_PREV_256.JPG' : '_PREV.JPG')
      var adURI = new cadc.web.util.URI(
        'ad:/' + this._getArchive() + '/' + fileID
      )

      return (
        adURI.getPath() +
        '?logkey=preview&logvalue=Observation&runid=' +
        this.runID
      )
    }

    /**
     * Preview converter by Observation ID and Product ID.  This is ONLY relevant in CAOM-2.
     *
     * This relies on the collectionName and extension to be set in the PREVIEW_COLLECTION_CONFIGURATIONS dictionary
     * above.
     *
     * @return {String}       Path suffix to obtain the preview.
     * @private
     */
    this._convertObservationIDProductID = function() {
      var entry = this.previewCollectionConfiguration

      var fileID =
        entry.collectionName +
        '_' +
        this.observationID +
        '_' +
        this.productID +
        '_preview_'

      if (this.size <= 128) {
        fileID = fileID + '64'
      } else if (this.size <= 512) {
        fileID = fileID + '256'
      } else {
        fileID = fileID + '1024'
      }

      fileID = fileID + entry.extension
      var adURI = new cadc.web.util.URI('ad:/' + entry.archive + '/' + fileID)

      return (
        adURI.getPath() +
        '?logkey=preview&logvalue=Observation&runid=' +
        this.runID
      )
    }

    /**
     * @return {function} converter function to call later on.
     */
    this.getConverter = function() {
      return this.previewCollectionConfiguration
        ? this.previewCollectionConfiguration.converter
        : null
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
    this.getPreview = function(_successCallback, _failCallback) {
      var converter = this.getConverter()
      var previewConverterFunction = converter ? converter.bind(this) : null

      if (previewConverterFunction) {
        var previewURL = this.endpoint + previewConverterFunction()

        $.ajax({
          method: 'HEAD',
          url: previewURL,
          xhrFields: {
            withCredentials: true
          }
        })
          .done(function() {
            _successCallback(previewURL)
          })
          .fail(function(jqXHR, textStatus, errorThrown) {
            if (_failCallback) {
              _failCallback(jqXHR.status, errorThrown)
            }
          })
      }
    }

    // The Hash of Collections.  Each one will know its Archive, and Preview URL
    // converter to use. 
    //
    // Narrowed down to support only JCMT and HST(CA).
    this.PREVIEW_COLLECTION_CONFIGURATIONS = {
      HST: { archive: 'HSTCA', converter: this._convertHST },
      JCMT: {
        archive: 'JCMT',
        converter: this._convertObservationIDProductID,
        collectionName: 'jcmt',
        extension: '.png'
      }
    }

    this.previewCollectionConfiguration =
      this.PREVIEW_COLLECTION_CONFIGURATIONS[this.collection] || {}
  }
})(jQuery, window)
