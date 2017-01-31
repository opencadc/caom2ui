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
   * @param _serviceEndpoint
   * @param _timerDelay
   * @constructor
   */
  function Validator(_serviceEndpoint, _timerDelay)
  {
    var myself = this;
    this.serviceEndpoint = _serviceEndpoint;
    this.validatorTimer = null;
    this.timerDelay = _timerDelay;


    function getServiceEndpoint()
    {
      return myself.serviceEndpoint;
    }

    function getValidatorTimer()
    {
      return myself.validatorTimer;
    }

    function getTimerDelay()
    {
      return myself.timerDelay;
    }

    /**
     * Initiate the timeout of a key press.
     *
     * @param input                       The input element.
     * @param onValidateCompleteCallback  The callback upon validation
     *                                    completion. {Optional}
     */
    function inputKeyPressed(input, onValidateCompleteCallback)
    {
      if (getValidatorTimer())
      {
        clearTimeout(getValidatorTimer());
      }

      myself.validatorTimer = setTimeout(function ()
                                  {
                                    myself.validate(input,
                                                    onValidateCompleteCallback);
                                  }, getTimerDelay());
    }

    /**
     * Validate the value of the given input.
     *
     * @param input                       The input element.
     * @param onValidateCompleteCallback  The callback upon validation
     *                                    completion. {Optional}
     * @returns {*}
     */
    function validate(input, onValidateCompleteCallback)
    {
      var validated;
      var value = input.val() ? $.trim(input.val()) : "";

      // Nothing to validate.
      if (value == "")
      {
        myself.clearError(input);
        validated = false;

        if (onValidateCompleteCallback)
        {
          onValidateCompleteCallback();
        }
      }
      else
      {
        var name = input.attr('name');

        // Query parameters.
        var parameters = {};
        parameters['field'] = name;
        parameters[name] = value;

        // Do the query. If JSON is returned indicating a validation error
        // decorate the field, else clear the error decoration.
        $.getJSON(getServiceEndpoint(), parameters)
            .done(function (data)
                  {
                    try
                    {
                      if (data)
                      {
                        myself.addError(input);
                      }
                      else
                      {
                        myself.clearError(input);
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
                  })
            .fail(function (jqXHR, textStatus, errorThrown)
                  {
                    console.error("An error occurred.\n\n" + errorThrown);
                  });

        validated = true;
      }

      return validated;
    }

    // Decorate the input to indicate a validation error.
    function addError(input)
    {
      input.addClass("input_error");
    }

    // Clear the error decoration for this input.
    function clearError(input)
    {
      input.removeClass("input_error");
    }

    $.extend(this,
             {
               "inputKeyPressed": inputKeyPressed,
               "validate": validate,
               "addError": addError,
               "clearError": clearError
             });

  }
})(jQuery);
