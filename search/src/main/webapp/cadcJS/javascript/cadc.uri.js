(function ($) {
  // register namespace
  $.extend(true, window, {
    "cadc": {
      "web": {
        "util": {
          "URI": URI,
          "currentURI": currentURI
        }
      }
    }
  });

  /**
   * Obtain the current URI object of the location in context.
   *
   * @return {URI}
   */
  function currentURI()
  {
    return new URI(window.location.href);
  }


  /**
   * URI object.
   *
   * @param uri     The uri string.
   * @constructor
   */
  function URI(uri)
  {
    var _self = this;
    this.uri = uri;
    this.uriComponents = {};
    this.query = {};

    function getURI()
    {
      return _self.uri;
    }

    // This function creates a new anchor element and uses location
    // properties (inherent) to get the desired URL data. Some String
    // operations are used (to normalize results across browsers).
    function init()
    {
      reparse(_self.uri);
    }

    /**
     * Parse the given URI into this object.  This method preserves the uri
     * property in this object as the 'original' uri.
     *
     * @param _uri    The new URI.
     */
    function reparse(_uri)
    {
      var parser = /^(?:([^:\/?\#]+):)?(?:\/\/([^\/?\#]*))?([^?\#]*)(?:\?([^\#]*))?(?:\#(.*))?/;
      var parsedURI = _uri.match(parser);
      var components = {};

      // Reset the objects.
      _self.uriComponents = {};
      _self.query = {};

      components.scheme = parsedURI[1] || "";
      components.host = parsedURI[2] || "";
      components.path = parsedURI[3] || "";
      components.query = parsedURI[4] || "";
      components.hash = parsedURI[5] || "";
      components.file = ((components.path
                          && components.path.match(/\/([^\/?#]+)$/i)) || [,''])[1];

      $.extend(_self.uriComponents, components);
      $.extend(_self.query, parseQuery());
    }

    /**
     * Obtain the relative URI for this URI.  Meaning, obtain the host-less
     * version of this URI, to avoid cross-domain constraint issues.
     *
     * @return  Relative URI, or empty string if none available.
     */
    function getRelativeURI()
    {
      var relativeURI = getPath();
      var queryString = getQueryString();
      var hashString = getHash();

      if (queryString)
      {
        relativeURI += "?" + queryString;
      }

      if (hashString)
      {
        relativeURI += "#" + hashString;
      }

      return relativeURI;
    }

    /**
     * Encode the relative URI.  This is useful when this URI will be passed
     * as a parameter.
     *
     * Also, since the hash needs to be encoded separately, the logic is well
     * re-used here.
     *
     * @return  {string} Encoded Relative URI.
     */
    function encodeRelativeURI()
    {
      var encodedRelativeURI;
      var relativeURI = getPath();
      var queryString = getQueryString();
      var hashString = getHash();

      if (queryString)
      {
        relativeURI += "?" + queryString;
      }

      encodedRelativeURI = encodeURI(relativeURI);

      // Treat the has separately.
      if (hashString)
      {
        encodedRelativeURI += encodeURIComponent("#" + hashString);
      }

      return encodedRelativeURI;
    }

    function getURIComponents()
    {
      return _self.uriComponents;
    }

    /**
     * Create an Object from the query string.
     *
     * @returns {{Object}}
     */
    function parseQuery()
    {
      var nvpair = {};
      var qs = getURIComponents().query;
      if ($.trim(qs))
      {
        var pairs = (qs != "") ? qs.split("&") : [];
        $.each(pairs, function(i, v)
        {
          var pair = v.split('=');
          var queryKey = pair[0];
          var keyValues = nvpair[queryKey] || [];

          // TODO - Is it a good idea to always decode this?  Should it be?
          keyValues.push(pair[1]);

          nvpair[queryKey] = keyValues;
        });
      }
      return nvpair;
    }

    function getQueryString()
    {
      return getURIComponents().query;
    }

    function getHash()
    {
      return getURIComponents().hash;
    }

    function getPath()
    {
      return getURIComponents().path;
    }

    function getPathItems()
    {
      var splitItems = getPath().split("/");

      if ((splitItems.length > 0) && (splitItems[0] == ""))
      {
        // If the path starts with a '/', then the first item will be an empty
        // string, so get rid of it.
        splitItems.splice(0, 1);
        return splitItems;
      }
      else
      {
        return splitItems;
      }
    }

    function getFile()
    {
      return getURIComponents().file;
    }

    function getHost()
    {
      return getURIComponents().host;
    }
    
    function getScheme()
    {
        return getURIComponents().scheme;
    }

    /**
     * Key -> value representation of the query string.  Assumes one value per
     * key.
     *
     * @returns {{Object}}
     * @deprecated  Use getQuery() object instead.
     */
    function getQueryStringObject()
    {
      var nvpair = {};
      var qs = getURIComponents().query.replace('?', '');
      var pairs = qs.split('&');

      $.each(pairs, function(i, v)
      {
        var pair = v.split('=');

        nvpair[pair[0]] = pair[1];
      });

      return nvpair;
    }

    /**
     * Return a key => array values pair.
     *
     * @returns {{String}}  String key with array of values.
     */
    function getQuery()
    {
      return _self.query;
    }

    /**
     * Return a single value for a key.
     *
     * @returns {{Object}}  value or null.
     */
    function getQueryValue(_key)
    {
      var queryItemArray = getQueryValues(_key);
      var val;

      if (queryItemArray.length > 0)
      {
        val = queryItemArray[0];
      }
      else
      {
        val = null;
      }

      return val;
    }

    function setQueryValue(_key, _val)
    {
      var existingValues = getQueryValues(_key);

      if (existingValues.length > 1)
      {
        throw new Error("There are multiple parameters with the name '" + _key
                        + "'.");
      }
      else
      {
        getQuery()[_key] = [_val];
      }

      //reparse(toString());
    }

    /**
     * Return an array of values for the given key.
     *
     * @returns {{Object}}  Array of items, or empty array.
     */
    function getQueryValues(_key)
    {
      var queryItemArray = getQuery()[_key];
      var val;

      if (queryItemArray && (queryItemArray.length > 0))
      {
        val = queryItemArray;
      }
      else
      {
        val = [];
      }

      return val;
    }

    /**
     * Remove ALL of the query parameters for the given key.
     * @param _key    The query parameter name.
     */
    function removeQueryValues(_key)
    {
      delete getQuery()[_key];
      reparse(toString());
    }

    /**
     * Build the string
     *
     */
    function toString()
    {
      var hashString;

      if (getHash() != '')
      {
        hashString = "#" + getHash();
      }
      else
      {
        hashString = "";
      }

      var scheme = getScheme();

      return (($.trim(scheme) == '') ? "" : (scheme + "://"))
             + getHost() + getPath() + buildQueryString(false)
          + hashString;
    }

    /**
     * Build the string value, and encode the query parameters.
     */
    function toEncodedString()
    {
      var hashString;

      if (getHash() != '')
      {
        hashString = "#" + getHash();
      }
      else
      {
        hashString = "";
      }

      var scheme = getScheme();

      return (($.trim(scheme) == '') ? "" : (scheme + "://"))
             + getHost() + getPath() + buildQueryString(true)
             + hashString;
    }

    function buildQueryString(_encodeValuesFlag)
    {
      var queryString = $.isEmptyObject(getQuery()) ? "" : "?";

      $.each(getQuery(), function(param, values)
      {
        for (var valIndex = 0; valIndex < values.length; valIndex++)
        {
          queryString += param + "=" + ((_encodeValuesFlag === true)
              ? encodeURIComponent(values[valIndex]) : values[valIndex]) + "&";
        }
      });

      if (queryString.charAt(queryString.length - 1) === ("&"))
      {
        queryString = queryString.substr(0, (queryString.length - 1));
      }

      return queryString;
    }

    /**
     * Specific function to obtain the currently requested target of a
     * login/logout within the CADC.  Nobody else will probably use this, but
     * it's here for convenience.
     *
     * @returns {string}
     */
    function getTargetURL()
    {
      // Get information about the current page
      var requestURL = new currentURI();
      var queryStringObject =
        requestURL.getQueryStringObject();
      var refererURL;

      if (queryStringObject.referer)
      {
        refererURL = new URI(queryStringObject.referer);
      }
      else
      {
        refererURL = new URI(document.referrer);
      }

      var targetURL =
        window.location.protocol + "//" + window.location.hostname
        +
        (window.location.port ? ":" + window.location.port : "");

      // Some sanitizing.
      if (refererURL.getPath().indexOf("/vosui") >= 0)
      {
        targetURL += "/vosui/";
      }
      else if (refererURL.getPath().indexOf("/canfar")
               >= 0)
      {
        targetURL += "/canfar/";
      }
      else if (refererURL.getPath().indexOf("/en/login")
               >= 0)
      {
        targetURL += "/en/";
      }
      else if (refererURL.getPath().indexOf("/fr/connexion")
               >= 0)
      {
        targetURL += "/fr/";
      }
      else
      {
        targetURL += refererURL.getPath();
      }

      if (requestURL.getHash()
          && (requestURL.getHash() != null)
          && (requestURL.getHash() != ""))
      {
        targetURL += "#" + requestURL.getHash();
      }

      var explicitTarget = requestURL.getQueryValue("target");

      if (explicitTarget)
      {
        targetURL = explicitTarget;
      }

      return targetURL;
    }

    /**
     * Specific function to obtain the currently requested target of a
     * login/logout within the CADC.  Nobody else will probably use this, but
     * it's here for convenience.
     *
     * @returns {string}
     */
    function getTargetURL()
    {
      // Get information about the current page
      var requestURL = new currentURI();
      var queryStringObject =
        requestURL.getQueryStringObject();
      var refererURL;

      if (queryStringObject.referer)
      {
        refererURL = new URI(queryStringObject.referer);
      }
      else
      {
        refererURL = new URI(document.referrer);
      }

      var targetURL =
        window.location.protocol + "//" + window.location.hostname
        +
        (window.location.port ? ":" + window.location.port : "");

      // Some sanitizing.
      if (refererURL.getPath().indexOf("/vosui") >= 0)
      {
        targetURL += "/vosui/";
      }
      else if (refererURL.getPath().indexOf("/canfar")
               >= 0)
      {
        targetURL += "/canfar/";
      }
      else if (refererURL.getPath().indexOf("/en/login")
               >= 0)
      {
        targetURL += "/en/";
      }
      else if (refererURL.getPath().indexOf("/fr/connexion")
               >= 0)
      {
        targetURL += "/fr/";
      }
      else
      {
        targetURL += refererURL.getPath();
      }

      if (requestURL.getHash()
          && (requestURL.getHash() != null)
          && (requestURL.getHash() != ""))
      {
        targetURL += "#" + requestURL.getHash();
      }

      var explicitTarget = requestURL.getQueryValue("target");

      if (explicitTarget)
      {
        targetURL = explicitTarget;
      }

      return targetURL;
    }

    function clearQuery()
    {
      $.each(getQuery(), function(param, values)
      {
        delete getQuery()[param];
      });
      reparse(toString());
    }

    init();

    $.extend(this,
             {
               "clearQuery": clearQuery,
               "getQuery": getQuery,
               "getQueryString": getQueryString,
               "getQueryStringObject": getQueryStringObject,
               "getQueryValue": getQueryValue,
               "setQueryValue": setQueryValue,
               "getQueryValues": getQueryValues,
               "removeQueryValues": removeQueryValues,
               "getPath": getPath,
               "getHost": getHost,
               "getPathItems": getPathItems,
               "getFile": getFile,
               "getURI": getURI,
               "getTargetURL": getTargetURL,
               "getRelativeURI": getRelativeURI,
               "encodeRelativeURI": encodeRelativeURI,
               "getHash": getHash,
               "getScheme": getScheme,
               "toString": toString,
               "toEncodedString": toEncodedString
             });
  }
})(jQuery);