(function ($)
{
  // Register namespace
  $.extend(true, window, {
    "cadc": {
      "web": {
        "json": {
          "babelfish": {
            "VALUE_KEY": "$",
            "ATTRIBUTE_KEY_PREFIX": "@"
          }
        },
        "User": User,
        "UserManager": UserManager,
        "WHOAMI_ENDPOINT": "/ac/whoami",

        "USER_CREATE_ENDPOINT": "/ac/userRequests",

        "USER_PASSWORD_CHANGE_ENDPOINT": "/ac/modifyPassword",

        // Need to replace {0} with the User ID
        // (See cadc.web.util.StringUtil.format() in cadc.util.js)!
        "USER_ACCOUNT_ENDPOINT": "/ac/users/{0}?idType=HTTP",

        "events": {
          "onUserLoad": new jQuery.Event("onUserLoad"),
          "onUserUpdate": new jQuery.Event("onUserUpdate")
        }
      }
    }
  });


  /**
   * Class to manage a User via web service calls.
   * @constructor
   */
  function UserManager()
  {
    var _selfUserManager = this;
    _selfUserManager.user = null;

    /**
     * Load the currently authenticated user.
     */
    function loadCurrent()
    {
      var ajaxInput = {
        method: "GET",
        dataType: "json",
        // headers: "Accept: application/json",
        url: cadc.web.WHOAMI_ENDPOINT,
        data: {},
        beforeSend: function(xhr) {
          xhr.withCredentials = true;
          xhr.setRequestHeader("Accept", "application/json");
          return true;
        },
        jsonp: false,
        crossDomain: true
      };

      $.ajax(ajaxInput).done(
        function (data/*, textStatus, jqXHR*/)
        {
          loadUser(data);
          fire(cadc.web.events.onUserLoad, {"user": _selfUserManager.user});
        }).fail(function (jqXHR, textStatus, errorThrown)
                {
                  console.log("Error: " + errorThrown);

                  fire(cadc.web.events.onUserLoad,
                    {
                      "errorStatus": jqXHR.status,
                      "errorMessage": textStatus,
                      "error": errorThrown
                    });
                });
    }

    /**
     * Load the user into this manager from the given JSON.
     *
     * @param json    JSON Data.
     */
    function loadUser(json)
    {
      var userID;
      var identities = json.user.identities[cadc.web.json.babelfish.VALUE_KEY];
      for (var i = 0; i < identities.length; i++)
      {
        var identity = identities[i];
        if (identity[cadc.web.json.babelfish.ATTRIBUTE_KEY_PREFIX + "type"] === "HTTP")
        {
          userID = identity[cadc.web.json.babelfish.VALUE_KEY];
          break;
        }
      }

      var personalDetail = json.user.personalDetails;
      if (!personalDetail)
      {
        personalDetail = {};
      }

      setUser(new cadc.web.User(userID,
                                getObjectValue(personalDetail, "firstName"),
                                getObjectValue(personalDetail, "lastName"),
                                getObjectValue(personalDetail, "email"),
                                getObjectValue(personalDetail, "institute"),
                                getObjectValue(personalDetail, "address"),
                                getObjectValue(personalDetail, "city"),
                                getObjectValue(personalDetail, "country")));
    }

    function getObjectValue(_jsonObject, _jsonObjectKey)
    {
      var val;

      if (_jsonObject.hasOwnProperty(_jsonObjectKey))
      {
        val = _jsonObject[_jsonObjectKey][cadc.web.json.babelfish.VALUE_KEY];
      }
      else
      {
        val = null;
      }

      return val;
    }

    /**
     * Obtain the user object as JSON.
     *
     * @returns {String}    JSON string of form data.
     */
    function serializeFormDataAsJSON(_username, _$personalDetailItemsArray)
    {
      var identity = {
        "@type": "HTTP",
        "$": _username
      };

      var personalDetails =
        serializeUserDetailsAsJSON(_$personalDetailItemsArray);
      var formDataObject = {
        user: {
          identities: {
            "$": [identity]
          }
        }
      };

      formDataObject.user.personalDetails = personalDetails;

      return JSON.stringify(formDataObject);
    }

    function serializeUserDetailsAsJSON(_$personalDetailItemsArray)
    {
      var personalDetail = {};

      _$personalDetailItemsArray.each(function ()
                                      {
                                        var $nextFormItem = $(this);

                                        personalDetail[$nextFormItem.attr("name")] =
                                          {"$": ($nextFormItem.val() || '')};
                                      });

      return personalDetail;
    }

    function updateUser(_$personalDetailItemsArray)
    {
      var formAction = new cadc.web.util.StringUtil(
        cadc.web.USER_ACCOUNT_ENDPOINT).format(getUser().getUserID());
      var formDataJSON = serializeFormDataAsJSON(getUser().getUserID(),
                                                 _$personalDetailItemsArray);

      $.ajax(
        {
          url: formAction,
          method: "POST",
          contentType: "application/json",
          mimeType: "application/json",
          dataType: "text",
          headers: {Accept: "application/json"},
          data: formDataJSON
        }).done(function ()
                {
                  fire(cadc.web.events.onUserUpdate, {});
                }).fail(function (xhr)
                        {
                          fire(cadc.web.events.onUserUpdate,
                            {
                              "errorStatus": xhr.status,
                              "errorMessage": "Error (" + xhr.status + "): "
                                              + "unable to update your Profile."
                                              + " (" + xhr.responseText + ") "
                            });
                        });
    }

    function getUser()
    {
      return _selfUserManager.user;
    }

    function setUser(__user)
    {
      _selfUserManager.user = __user;
    }

    function subscribe(event, eHandler)
    {
      $(_selfUserManager).on(event.type, eHandler);
    }

    function fire(event, eventData)
    {
      var eData = eventData || {};
      eData.userManager = _selfUserManager;

      $(_selfUserManager).trigger(event, eData);
    }

    $.extend(this,
      {
        "loadCurrent": loadCurrent,
        "updateUser": updateUser,
        "getUser": getUser,
        "setUser": setUser,
        "serializeFormDataAsJSON": serializeFormDataAsJSON,

        // Event handling,
        "subscribe": subscribe,

        // Here for testing!
        "TEST_LOAD_USER": loadUser
      });
  }

  /**
   * Represents a user as per Form data.
   *
   * @param _userID       The CADC User ID.
   * @param _firstName    First name
   * @param _lastName     Last name
   * @param _email        Email
   * @param _institute    Institute
   * @param _address      Address
   * @param _city         City
   * @param _country      Country
   * @constructor
   */
  function User(_userID, _firstName, _lastName, _email, _institute, _address,
                _city, _country)
  {
    var _self = this;

    this.userID = _userID;
    this.firstName = _firstName;
    this.lastName = _lastName;
    this.email = _email;
    this.institute = _institute;
    this.address = _address;
    this.city = _city;
    this.country = _country;


    function getUserID()
    {
      return _self.userID;
    }

    function getFirstName()
    {
      return _self.firstName;
    }

    function getLastName()
    {
      return _self.lastName;
    }

    function getFullName()
    {
      return getFirstName() + " " + getLastName();
    }

    function getEmail()
    {
      return _self.email;
    }

    function getInstitute()
    {
      return _self.institute;
    }

    function getAddress()
    {
      return _self.address;
    }

    function getCity()
    {
      return _self.city;
    }

    function getCountry()
    {
      return _self.country;
    }


    $.extend(this,
      {
        "getUserID": getUserID,
        "getFirstName": getFirstName,
        "getLastName": getLastName,
        "getFullName": getFullName,
        "getEmail": getEmail,
        "getInstitute": getInstitute,
        "getAddress": getAddress,
        "getCity": getCity,
        "getCountry": getCountry
      });
  }

})(jQuery);