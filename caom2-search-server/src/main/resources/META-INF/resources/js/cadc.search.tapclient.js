/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2019.                            (c) 2019.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

;(function($, window) {
  $.extend(true, window, {
    ca: {
      nrc: {
        cadc: {
          search: {
            tapclient: {
              TAP_SERVICE_STANDARD_ID : 'ivo://ivoa.net/std/TAP',
              TAP_SYNC_ENDPOINT : '/sync',
              SearchTapClient: SearchTapClient,
              events: {
                onTAPClientOK: new jQuery.Event(
                  'SearchTapClient:onTAPClientOK'
                ),
                onTAPClientFail: new jQuery.Event(
                  'SearchTapClient:onTAPClientFail'
                ),
                onTAPClientReady: new jQuery.Event(
                  'SearchTapClient:onTAPClientReady'
                ),
              }
            }
          }
        }
      }
    }
  })

  /**
   * @param {String} [_options.baseURL]    URL of host system.
   * @param {String} [_options.tapServiceId]    Service of TAP - from org.opencadc.search.properties
   * @constructor
   */
  function SearchTapClient(_options) {
    this.options = _options
    this._lastURLUsed

    this._TAPServiceURL = {}

    var _rc = this
    var _serviceLocatorEndpoint = this.options.serviceLocatorEndpoint === '' ?
                                    'service-locator' :
                                    this.options.serviceLocatorEndpoint

    function setTAPServiceURL(url, path) {
        _rc._TAPServiceURL = {
          'href' : url + path,
          'pathname' : path,
        }
    }

    function postRequest(serviceURL, format, tapQuery, callerId) {
      _rc._lastURLUsed = serviceURL

        $.post(
          serviceURL.href,
          {
            LANG: 'ADQL',
            FORMAT: format,
            QUERY: tapQuery
          },
          {
            xhrFields: {
              withCredentials: true
            },
            jsonp: false
          }
        )
          .done(
            function (data) {
              _rc.trigger(
                ca.nrc.cadc.search.tapclient.events.onTAPClientOK,
                {
                  data: data,
                  callerId: callerId
                }
              )
            }
          )
          .fail(
            function (jqXHR) {
              _rc.trigger(
                ca.nrc.cadc.search.tapclient.events.onTAPClientFail,
                {
                  responseText: jqXHR.responseText,
                  callerId: callerId
                }
              )
            }
          )
    }

    /**
     * Make call to server to get TAP data
     * @private
     */
    function postTAPRequest(tapQuery, format, callerId) {
      // callerId is so the listeners can determine if an event coming
      // from an instance of this object belongs to them or not.
      var baseURI = _rc.options.tapServiceId

      if (typeof _rc._TAPServiceURL.href === 'undefined') {
          $.get(
            _serviceLocatorEndpoint,
            {
              serviceID: baseURI,
              standardID: ca.nrc.cadc.search.tapclient.TAP_SERVICE_STANDARD_ID
            }
          )
          .done(function (serviceURLJSON) {
            _rc.setTAPServiceURL(serviceURLJSON.serviceURL, ca.nrc.cadc.search.tapclient.TAP_SYNC_ENDPOINT)
            postRequest(_rc._TAPServiceURL, format, tapQuery, callerId)
          })
          .fail(function (err) {
            _rc.trigger(
              ca.nrc.cadc.search.tapclient.events.onTAPClientFail,
              {responseText: err}
            )
          })
      } else {
        postRequest(_rc._TAPServiceURL, format, tapQuery, callerId)
      }

    }

    function getLastURL() {
      return this._lastURLUsed
    }

    function getLastEndpoint() {
      return this._lastURLUsed.pathname
    }

    // ---------- Event Handling Functions ----------
    /**
     * Fire an event.
     *
     * @param {jQuery.Event}  _event       The Event to fire.
     * @param {{}}  _args        Arguments to the event.
     * @returns {*}       The event notification result.
     * @private
     */
    function trigger(_event, _args) {
      var args = _args || {}
      return $(this).trigger(_event, _args)
    }

    /**
     * Subscribe to one of this object's events.
     *
     * @param {jQuery.Event}  _event      Event object.
     * @param {function}  __handler   Handler function.
     */
    function subscribe(_event, __handler) {
      $(this).on(_event.type, __handler)
    }

    /**
     * Unsubscribe to one of this object's events.
     *
     * @param {jQuery.Event}  _event      Event object.
     * @param {function}  __handler   Handler function.
     */
    function unsubscribe(_event, __handler) {
      $(this).off(_event.type, __handler)
    }

    // Set these functions as public
    $.extend(this, {
      getLastEndpoint: getLastEndpoint,
      getLastURL: getLastURL,
      postTAPRequest: postTAPRequest,
      setTAPServiceURL: setTAPServiceURL,
      subscribe: subscribe,
      trigger: trigger,
      unsubscribe: unsubscribe
    })

  }

})(jQuery, window)
