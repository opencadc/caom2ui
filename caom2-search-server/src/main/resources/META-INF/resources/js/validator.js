/*
 *    ALMA - Atacama Large Millimeter Array
 *    (c) European Southern Observatory, 2002
 *    Copyright by ESO (in the framework of the ALMA collaboration),
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 *    MA 02111-1307  USA
 */

(function ($)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "Validator": Validator
          }
        }
      }
    }
  });

  /**
   * Create the Validator object.
   *
   * @param {String} _serviceEndpoint
   * @param {Number} _timerDelay
   * @constructor
   */
  function Validator(_serviceEndpoint, _timerDelay)
  {
    this.serviceEndpoint = _serviceEndpoint;

    /**
     * @type {number}
     */
    this.validatorTimer = null;

    this.timerDelay = _timerDelay;


    /**
     * Initiate the timeout of a key press.
     *
     * @param input                       The input element.
     * @param onValidateCompleteCallback  The callback upon validation
     *                                    completion. {Optional}
     */
    this.inputKeyPressed = function (input, onValidateCompleteCallback)
    {
      if (this.validatorTimer)
      {
        clearTimeout(this.validatorTimer);
      }

      this.validatorTimer = setTimeout(function ()
                                       {
                                         this.validate(input, onValidateCompleteCallback);
                                       }.bind(this), this.timerDelay);
    };

    /**
     * Validate the value of the given input.
     *
     * @param {jQuery}    $input                       The input element.
     * @param {function}  [onValidateCompleteCallback]  The callback upon validation completion.
     * @returns {boolean}
     */
    this.validate = function($input, onValidateCompleteCallback)
    {
      var validated;
      var value = $input.val() ? $.trim($input.val()) : "";

      // Nothing to validate.
      if (value === "")
      {
        this.clearError($input);
        validated = false;

        if (onValidateCompleteCallback)
        {
          onValidateCompleteCallback();
        }
      }
      else
      {
        var name = $input.attr("name");

        // Query parameters.
        var parameters = {};
        parameters["field"] = name;
        parameters[name] = value;

        // Do the query. If JSON is returned indicating a validation error
        // decorate the field, else clear the error decoration.
        $.getJSON(this.serviceEndpoint, parameters)
            .done(function (data)
                  {
                    try
                    {
                      if (data)
                      {
                        this.addError($input);
                      }
                      else
                      {
                        this.clearError($input);
                      }
                    }
                    catch (e)
                    {
                      console.error("An error occurred.\n\n" + e);
                    }

                    if (onValidateCompleteCallback)
                    {
                      onValidateCompleteCallback(data);
                    }
                  }.bind(this))
            .fail(function (jqXHR, textStatus, errorThrown)
                  {
                    console.error("An error occurred.\n\n" + errorThrown);
                  });

        validated = true;
      }

      return validated;
    };

    /**
     * Decorate the input to indicate a validation error.
     *
     * @param {jQuery} $input    jQuery input element.
     */
    this.addError = function($input)
    {
      $input.addClass("input_error");
    };

    /**
     * Clear the error decoration for this input.
     * @param {jQuery} $input
     */
    this.clearError = function($input)
    {
      $input.removeClass("input_error");
    };
  }
})(jQuery);
