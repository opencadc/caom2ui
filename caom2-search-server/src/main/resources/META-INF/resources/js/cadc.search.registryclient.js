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
            registryclient: {
              BASEURL: '',
              TAP_MAQ_URI: 'ivo://cadc.nrc.ca/tap/maq',
              TAP_URI: 'ivo://cadc.nrc.ca/tap',
              TAP_SYNC_ENDPOINT : '/sync',
              RegistryClient: RegistryClient,
              events: {
                onRegistryClientOK: new jQuery.Event(
                    'SearchRegistryClient:onRegistryClientOK'
                ),
                onRegistryClientFail: new jQuery.Event(
                    'SearchRegistryClient:onRegistryClientFail'
                )
              }
            }
          }
        }
      }
    }
  })


  /**
   * @param {String} [_options.tapSyncEndpoint=/search/tap/sync]    TAP Endpoint.
   * @constructor
   */
  function RegistryClient(_options) {

    this.defaults = {
      baseURL: ca.nrc.cadc.search.BASEURL,
      activateMAQ: true
    }

    this.options = $.extend({}, true, this.defaults, _options)

    var _rc = this

    var _regClient = this.options.baseURL === '' ?
                        new Registry() :
                        new Registry({baseURL: this.options.baseURL})

    function prepareTAPCall(baseURI) {
      return _regClient.getServiceURL(
              baseURI,
              'ivo://ivoa.net/std/TAP',
              'vs:ParamHTTP',
              'cookie'
          )
    }

    /**
     * Make call to server to get TAP data
     * @private
     */
    function postTAPRequest(tapQuery, format, activateMAQ) {
      var baseURI = ''
      if (activateMAQ === true) {
        baseURI = ca.nrc.cadc.search.datatrain.TAP_MAQ_URI
      } else {
        baseURI = ca.nrc.cadc.search.datatrain.TAP_URI
      }

      Promise.resolve(this.prepareTAPCall(baseURI))
        .then( function (serviceURL) {
          serviceURL = serviceURL + ca.nrc.cadc.search.registryclient.TAP_SYNC_ENDPOINT

          $.post(
              serviceURL,
              {
                LANG: 'ADQL',
                FORMAT: format,
                USEMAQ: activateMAQ,
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
                    ca.nrc.cadc.search.registryclient.events.onRegistryClientOK,
                    {data: data}
                )
              }
          )
          .fail(
              function (jqXHR) {
                _rc.trigger(
                    ca.nrc.cadc.search.registryclient.events.onRegistryClientFail,
                    {responseText: jqXHR.responseText}
                )
              }
          )

        })
        .catch(function (err) {
          _rc.trigger(
            ca.nrc.cadc.search.registryclient.events.onRegistryClientFail,
            {responseText: err}
          )
        })

    }


    // ---------- Event Handling Functions ----------
    /**
     * Fire an event.  Taken from the slick.grid Object.
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

    // Set these functions as public
    $.extend(this, {
      postTAPRequest: postTAPRequest,
      prepareTAPCall: prepareTAPCall,
      subscribe: subscribe,
      trigger: trigger
    })

  }

})(jQuery, window)
