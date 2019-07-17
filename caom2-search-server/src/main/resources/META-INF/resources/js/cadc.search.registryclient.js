/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2008.                            (c) 2008.
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
              TAP_PLUS_ENDPOINT: '/cadc-plus-external/sync',
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

  //https://jeevesh.cadc.dao.nrc.ca/tap/cadc-plus-external/sync?LANG=ADQL&FORMAT=CSV&USEMAQ=true&QUERY=SELECT+obs_collection%2Cfacility_name%2Cinstrument_name%2Ccalib_level%2Cdataproduct_type%2C+CASE+WHEN+max_t_min+%3E%3D+56842.621145960875+THEN+1+ELSE+0+END+FROM+caom2.obscoreenumfield

  /**
   * @param {String} [_options.tapSyncEndpoint=/search/tap/sync]    TAP Endpoint.
   * @constructor
   */
  function RegistryClient(_options) {

    this.defaults = {
      baseURL: ca.nrc.cadc.search.BASEURL,
      activateMAQ: true
    }

    var options = $.extend({}, true, this.defaults, _options)
    var _rc = this

    var _regClient = new Registry({
      baseURL: options.baseURL
    })

    function _getBaseUrl() {
      if (typeof this.options.baseURL == 'undefined') {
        return ca.nrc.cadc.search.datatrain.BASEURL
      } else {
        return this.options.baseURL
      }
    }

    function prepareTAPCall(baseURI) {
      return _regClient.getServiceURL(
              baseURI,
              'ivo://ivoa.net/std/TAP',
              'vs:ParamHTTP',
              'cookie'
          )
    }

    /**
     * Make call to server to get TAP data to load into DataTrain
     * @private
     */
    function postTapRequest(tapQuery, format) {
      var baseURI = ''
      if (this.activateMAQ === true) {
        baseURI = ca.nrc.cadc.search.datatrain.TAP_MAQ_URI
      } else {
        baseURI = ca.nrc.cadc.search.datatrain.TAP_URI
      }

      Promise.resolve(this.prepareTAPCall(baseURI))
        .then( function (serviceURL) {
          // make the call here. ajax or otherwise...

          if (options.activateMAQ === true) {
            serviceURL = serviceURL + ca.nrc.cadc.search.registryclient.TAP_PLUS_ENDPOINT
          } else {
            serviceURL = serviceURL + ca.nrc.cadc.search.registryclient.TAP_SYNC_ENDPOINT
          }

          $.post(
              serviceURL,
              {
                LANG: 'ADQL',
                FORMAT: format,
                USEMAQ: true, // todo: get correct value in here
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
      args.cadcForm = this

      return $(this).trigger(_event, _args)
    }

    /**
     * Subscribe to one of this form's events.
     *
     * @param {jQuery.Event}  _event      Event object.
     * @param {function}  __handler   Handler function.
     */
    function subscribe(_event, __handler) {
      $(this).on(_event.type, __handler)
    }


    // Set these functions as public
    $.extend(this, {
      postTapRequest: postTapRequest,
      prepareTAPCall: prepareTAPCall,
      subscribe: subscribe,
      trigger: trigger
    })

  }


})(jQuery, window)
