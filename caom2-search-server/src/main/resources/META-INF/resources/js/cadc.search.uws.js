;(function($) {
  // register namespace
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            uws: {
              xml: {
                UWSJobParser: UWSXMLJobParser
              },
              json: {
                UWSJobParser: UWSJSONJobParser
              },
              UWSJobLoader: UWSJobLoader,
              UWSJob: UWSJob,
              events: {
                onJobLoaded: new jQuery.Event('onJobLoaded'),
                onJobLoadFailed: new jQuery.Event('onJobLoadFailed')
              }
            }
          }
        }
      }
    }
  })

  /**
   * A UWS Job object.
   *
   * @constructor
   */
  function UWSJob(_id, _runID, _ownerID, _phase, __paramObject) {
    var _selfJob = this

    this.id = _id
    this.runID = _runID
    this.ownerID = _ownerID
    this.phase = _phase
    this.parameters = __paramObject

    function getID() {
      return _selfJob.id
    }

    function getRunID() {
      return _selfJob.runID
    }

    function getPhase() {
      return _selfJob.phase
    }

    function getParameters() {
      return _selfJob.parameters
    }

    function getParameterValue(paramKey) {
      return getParameters()[paramKey]
    }

    $.extend(this, {
      getID: getID,
      getRunID: getRunID,
      getPhase: getPhase,
      getParameters: getParameters,
      getParameterValue: getParameterValue
    })
  }

  /**
   * Job loader for UWS jobs.
   *
   * @param _jobURL     The Job URL.  Will be sanitized to use a relative URI.
   * @constructor
   */
  function UWSJobLoader(_jobURL) {
    var _selfJobLoader = this

    this.jobURL = _jobURL
    this.requestLoader = jQuery

    function getJobURL() {
      return _selfJobLoader.jobURL
    }

    function handleOutput($xmlData) {
      var jobParser = new ca.nrc.cadc.search.uws.xml.UWSJobParser($xmlData)
      trigger(ca.nrc.cadc.search.uws.events.onJobLoaded, {
        job: jobParser.getJob()
      })
    }

    function load() {
      var relJobURL = new cadc.web.util.URI(getJobURL())

      getRequestLoader()
        .get({
          url: relJobURL,
          xhrFields: {
            withCredentials: true
          }
        })
        .done(function(xmlData) {
          handleOutput($(xmlData))
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
          trigger(ca.nrc.cadc.search.uws.events.onJobLoadFailed, {
            errorTextStatus: textStatus,
            error: errorThrown,
            errorStatusCode: jqXHR.status
          })
        })
    }

    function setRequestLoader(_requestLoader) {
      _selfJobLoader.requestLoader = _requestLoader
    }

    function getRequestLoader() {
      return _selfJobLoader.requestLoader
    }

    /**
     * Fire an event.  Taken from the slick.grid Object.
     *
     * @param _event       The Event to fire.
     * @param _args        Arguments to the event.
     * @returns {*}       The event notification result.
     */
    function trigger(_event, _args) {
      var args = _args || {}

      return $(_selfJobLoader).trigger(_event, args)
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param _event      Event object.
     * @param __handler   Handler function.
     */
    function subscribe(_event, __handler) {
      $(_selfJobLoader).on(_event.type, __handler)
    }

    $.extend(this, {
      getJobURL: getJobURL,
      load: load,
      setRequestLoader: setRequestLoader,

      // Events
      subscribe: subscribe
    })
  }

  /**
   * A class to parse UWS Jobs from the given XML.
   *
   * @param json   The JSON object.
   * @constructor
   */
  function UWSJSONJobParser(json) {
    var _selfParser = this

    this.job = null

    function init() {
      _selfParser.job = new ca.nrc.cadc.search.uws.UWSJob(
        json.id,
        json.runID,
        json.ownerID,
        json.phase,
        json.parameters
      )
    }

    function getJob() {
      return _selfParser.job
    }

    init()

    $.extend(this, {
      getJob: getJob
    })
  }

  /**
   * A class to parse UWS Jobs from the given XML.
   *
   * @param $uwsXML   The XML jQuery object.
   * @constructor
   */
  function UWSXMLJobParser($uwsXML) {
    var _selfParser = this

    this.job = null

    function init() {
      var parameters = {}
      $uwsXML.find('uws\\:parameter, parameter').each(function() {
        var $nextParam = $(this)
        parameters[$nextParam.attr('id')] = $nextParam.text()
      })

      _selfParser.job = new ca.nrc.cadc.search.uws.UWSJob(
        $uwsXML.find('uws\\:jobId, jobId').text(),
        $uwsXML.find('uws\\:runId, runId').text(),
        $uwsXML.find('uws\\:ownerId, ownerId').text(),
        $uwsXML.find('uws\\:phase, phase').text(),
        parameters
      )
    }

    function getJob() {
      return _selfParser.job
    }

    init()

    $.extend(this, {
      getJob: getJob
    })
  }
})(jQuery)
