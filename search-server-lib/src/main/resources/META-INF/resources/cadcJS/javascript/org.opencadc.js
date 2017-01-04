/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
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
    "org": {
      "opencadc": {
        "StringUtil": StringUtil
      }
    }
  });


  /**
   * Basic String utility class.
   *
   * @constructor
   */
  function StringUtil()
  {
    /**
     * Obtain whether the given string has any length (i.e. > 0).
     * @param _str          The string to check.
     * @returns {boolean}
     */
    function hasLength(_str)
    {
      return ((_str != null) && (_str.length > 0));
    }

    /**
     * Obtain whether the given string has any text (i.e. !== '').
     * @param _str          The string to check.
     * @returns {boolean}
     */
    function hasText(_str)
    {
      var wrapper = String(_str);
      return hasLength(wrapper) && ($.trim(wrapper) !== "");
    }

    /**
     * Format the given string.
     *
     * Given the string:
     *
     * {code}
     * var str = 'My name is {1} and I am {2} years old';
     * new org.opencadc.StringUtil().format(str, 'George', 39);
     * {code}
     *
     * would return:
     *
     * My name is George and I am 39 years old
     *
     * Indexes begin at 1, NOT 0.
     *
     * @param _str                The string to check.
     * @param _values {Array}     The values to replace.
     * @returns {string}
     */
    function format(_str, _values)
    {
      // Create new string to not modify the original.
      return _str.replace(/{(\d+)}/g, function (match, number)
      {
        var index = (number - 1);
        return _values[index] ? _values[index] : match;
      });
    }

    /**
     * Sanitize the given string for HTML.
     *
     * @param _str        String to sanitize.
     * @returns {string}
     */
    function markupForHTML(_str)
    {
      return _str ? _str.toString().replace(/&/g, "&amp;")
        .replace(/</g, "&lt;").replace(/>/g, "&gt;") : "";
    }

    /**
     * Obtain whether the given regex matches the given string.
     *
     * @param _regex        The regex to execute.
     * @param _str          The string to execute against.
     * @returns {boolean}
     */
    function matches(_regex, _str)
    {
      return new RegExp(_regex).test(_str);
    }

    /**
     * Obtain whether the _string contains the given _str.
     *
     * @param _string         The String to check.
     * @param _match          The string to see if is contained.
     * @param _matchCase      Optionally match case.
     * @returns {boolean}
     */
    function contains(_string, _match, _matchCase)
    {
      var expression = ".*" + _match + ".*";
      var regExp = (_matchCase === true) ? new RegExp(expression)
        : new RegExp(expression, "gi");

      return regExp.test(_string);
    }

    $.extend(this,
        {
          "sanitize": markupForHTML,
          "hasLength": hasLength,
          "hasText": hasText,
          "format": format,
          "matches": matches,
          "contains": contains
        });
  }

})(jQuery);
