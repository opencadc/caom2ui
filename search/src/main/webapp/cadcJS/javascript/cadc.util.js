/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2009.                            (c) 2009.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *  $Revision: 4 $
 *
 ************************************************************************
 */
(function ($)
{
  // register namespace
  $.extend(true, window, {
    "cadc": {
      "web": {
        "util": {
          "StringUtil": StringUtil,
          "NumberFormat": NumberFormat,
          "Array": Array,
          "ArrayUtil": ArrayUtil,
          "GUID": GUID
        }
      }
    }
  });


  /**
   * Extended Array object to perform operations on arrays.
   *
   * @param _arr    The base array.
   * @constructor
   */
  function Array(_arr)
  {
    var arrayUtil = new ArrayUtil();

    if (arrayUtil.isUninitialized(_arr))
    {
      throw new Error("Base array is required.");
    }

    var self = this;

    this.baseArray = _arr;


    /**
     * Subtract the contents of _array from ths array.  This is not a diff,
     * just an overlap find and remove operation.
     *
     * @param arguments {Array | function}
     *  The Array to remove OR
     *  The function to filter out items.  This is useful for arrays of objects
     *  whose equality is no concise.  (function (element, index, array) {})
     */
    function subtract()
    {
      if ((arguments.length !== 1) || !arguments[0])
      {
        throw new Error("Subtract requires an array or a filter function.");
      }
      else
      {
        if (typeof arguments[0] === "function")
        {
          return subtractFilterHandler(arguments[0]);
        }
        else
        {
          return subtractArray(arguments[0])
        }
      }
    }

    function subtractFilterHandler(_filterHandler)
    {
      if (!_filterHandler)
      {
        throw new Error("Filter handler is required.");
      }
      else
      {
        return self.baseArray.filter(_filterHandler);
      }
    }

    function subtractArray(_array)
    {
      if (arrayUtil.isUninitialized(_array))
      {
        throw new Error("Array being subtracted is required.");
      }
      else
      {
        return subtractFilterHandler(function (item)
                                     {
                                       return (_array.indexOf(item) < 0);
                                     });
      }
    }

    /**
     * Sort this Array in _ascendingFlag ? order.  This will clone the base
     * array and return it sorted.  The base array remains unaffected.
     *
     * @param {*} _propertyName  The name of the property to search on, if this
     *                       is an array of objects.  It is null otherwise.
     * @returns {Blob|ArrayBuffer|Array|string|*}
     */
    function sort(_propertyName)
    {
      var cloneArray = self.baseArray.slice(0);

      cloneArray.sort(function (o1, o2)
                      {
                        var score;

                        if (_propertyName)
                        {
                          if (o1.hasOwnProperty(_propertyName)
                              && o2.hasOwnProperty(_propertyName))
                          {
                            score = arrayUtil.compare(o1[_propertyName],
                                                      o2[_propertyName]);
                          }
                          else
                          {
                            throw new Error("Property '" + _propertyName
                                            + "' does not exist in the objects "
                                            + "being compared.")
                          }
                        }
                        else
                        {
                          score = arrayUtil.compare(o1, o2);
                        }

                        return score;
                      });

      return cloneArray;
    }

    $.extend(this,
        {
          "subtract": subtract,
          "sort": sort
        });
  }

  function ArrayUtil()
  {
    function isUninitialized(_arr)
    {
      return ((_arr === undefined) || (_arr === null));
    }

    /**
     * Inner sort method.  This will determine data types and do appropriate
     * comparisons.
     *
     * @param _left {*}     Anything under the sun.
     * @param _right {*}    Anything under the other sun.
     * @returns {number}    The Score of the sort comparison.
     */
    function compare(_left, _right)
    {
      var leftCompare, rightCompare;

      if ((typeof _left === 'string') && ((typeof _right === 'string')))
      {
        leftCompare = _left.toLowerCase();
        rightCompare = _right.toLowerCase();
      }
      else if (((typeof _left === 'object') && ((typeof _right === 'object')))
               || ((typeof _left === 'function')
                   && ((typeof _right === 'function'))))
      {
        leftCompare = _left.toString();
        rightCompare = _right.toString();
      }
      else
      {
        leftCompare = _left;
        rightCompare = _right;
      }

      return (leftCompare > rightCompare)
          ? 1 : (leftCompare < rightCompare) ? -1 : 0;
    }

    $.extend(this,
        {
          "isUninitialized": isUninitialized,
          "compare": compare
        });
  }

  /**
   * GUID generator.
   *
   * @constructor
   */
  function GUID()
  {
    function s4()
    {
      return Math.floor((1 + Math.random()) * 0x10000).
          toString(16).substring(1);
    }

    function generate()
    {
      return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
             s4() + '-' + s4() + s4() + s4();
    }

    $.extend(this,
        {
          "generate": generate
        });
  }

  /**
   * Basic String utility class.
   *
   * @param _string
   * @constructor
   */
  function StringUtil(_string)
  {
    var _selfStringUtil = this;

    this.string = function ()
    {
      return (_string === Number(0)) ? "0"
          : (_string ? _string.toString() : "");
    }();


    function getString()
    {
      return _selfStringUtil.string;
    }

    function hasLength()
    {
      var s = getString();
      return ((s != null) && (s.length > 0));
    }

    function hasText()
    {
      return hasLength() && ($.trim(getString()) != "");
    }

    function format()
    {
      // Create new string.
      var s = "" + getString();
      var args = arguments;

      return s.replace(/{(\d+)}/g, function (match, number)
      {
        return args[number] ? args[number] : match;
      });
    }

    function sanitize()
    {
      var returnValue;

      if (!getString())
      {
        returnValue = "";
      }
      else
      {
        returnValue = getString().toString().replace(/&/g, "&amp;").
            replace(/</g, "&lt;").replace(/>/g, "&gt;");
      }

      return returnValue;
    }

    function matches(_regex)
    {
      return new RegExp(_regex).test(getString());
    }

    /**
     * Obtain whether the String in this StringUtil contains the given _str.
     *
     * @param _str            The string to see if is contained.
     * @param _matchCase      Optionally match case.
     * @returns {boolean}
     */
    function contains(_str, _matchCase)
    {
      var expression = ".*" + _str + ".*";
      var regExp = (_matchCase === true) ? new RegExp(expression)
        : new RegExp(expression, "gi");

      return regExp.test(getString());
    }

    $.extend(this,
        {
          "sanitize": sanitize,
          "hasLength": hasLength,
          "hasText": hasText,
          "format": format,
          "matches": matches,
          "contains": contains
        });
  }

  /**
   * Format the given number for output.
   *
   * @param _val          The value to format.
   * @param _sigDigits    The value to use for fixed or precision (Optional).
   * @constructor
   */
  function NumberFormat(_val, _sigDigits)
  {
    var _selfNumberFormat = this;

    this.numberVal = Number(_val);
    this.significantDigits = _sigDigits || 0;

    function getNumberValue()
    {
      return _selfNumberFormat.numberVal;
    }

    function getSignificantDigits()
    {
      return _selfNumberFormat.significantDigits;
    }

    /**
     * Format to fixation, meaning the number of integers after the decimal
     * place.
     * @returns {string}
     */
    function formatFixation()
    {
      return getNumberValue().toFixed(getSignificantDigits());
    }

    /**
     * Format to precision, meaning the number of characters all together.
     * @returns {string}
     */
    function formatPrecision()
    {
      return getNumberValue().toPrecision(getSignificantDigits());
    }

    /**
     * An attempt to reproduce the printf(%.g) format.
     *
     * From the sprintf man page:
     *
     * 'Style e is used if the exponent from its conversion is less than -4 or
     *  greater than or equal to the precision.  Trailing zeros are removed from
     *  the fractional part of the result; a decimal point appears only if it
     *  is followed by at least one digit.'
     *
     * jenkinsd 2013.12.20
     *
     * @returns {string}
     */
    function formatExponentOrFloat()
    {
      var output;
      var num = getNumberValue();
      var exponentialVal = num.toExponential(getSignificantDigits());

      var exponent = num.toExponential().split('+')[1];

      if ((exponent < -4) || (exponent >= getSignificantDigits()))
      {
        output = exponentialVal;
      }
      else
      {
        output = formatFixation();
      }

      return output;
    }

    /**
     * Default format function.
     *
     * @return {string}
     */
    function format()
    {
      var formattedValue;

      if (getSignificantDigits())
      {
        formattedValue = formatFixation();
      }
      else
      {
        formattedValue = getNumberValue().toString();
      }

      return formattedValue;
    }


    $.extend(this,
        {
          "format": format,
          "formatFixation": formatFixation,
          "formatPrecision": formatPrecision,
          "formatExponentOrFloat": formatExponentOrFloat
        });
  }
})(jQuery);
