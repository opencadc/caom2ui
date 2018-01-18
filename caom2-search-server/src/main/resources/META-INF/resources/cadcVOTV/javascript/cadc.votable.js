;(function($, window, undefined) {
  'use strict'

  // register namespace
  $.extend(true, window, {
    cadc: {
      vot: {
        VOTable: VOTable,
        Metadata: Metadata,
        Datatype: Datatype,
        Field: Field,
        Parameter: Parameter,
        Resource: Resource,
        Info: Info,
        Table: Table,
        Row: Row,
        Cell: Cell,
        TableData: TableData
      }
    }
  })

  /**
   *
   * Sample VOTable XML Document - Version 1.2 .
   *
   * <VOTABLE version="1.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   * xmlns="http://www.ivoa.net/xml/VOTable/v1.2"
   * xmlns:stc="http://www.ivoa.net/xml/STC/v1.30" >
   <RESOURCE name="myFavouriteGalaxies">
   <TABLE name="results">
   <DESCRIPTION>Velocities and Distance estimations</DESCRIPTION>
   <GROUP ID="J2000" utype="stc:AstroCoords">
   <PARAM datatype="char" arraysize="*" ucd="pos.frame" name="cooframe"
   utype="stc:AstroCoords.coord_system_id" value="UTC-ICRS-TOPO" />
   <FIELDref ref="col1"/>
   <FIELDref ref="col2"/>
   </GROUP>
   <PARAM name="Telescope" datatype="float" ucd="phys.size;instr.tel"
   unit="m" value="3.6"/>
   <FIELD name="RA"   ID="col1" ucd="pos.eq.ra;meta.main" ref="J2000"
   utype="stc:AstroCoords.Position2D.Value2.C1"
   datatype="float" width="6" precision="2" unit="deg"/>
   <FIELD name="Dec"  ID="col2" ucd="pos.eq.dec;meta.main" ref="J2000"
   utype="stc:AstroCoords.Position2D.Value2.C2"
   datatype="float" width="6" precision="2" unit="deg"/>
   <FIELD name="Name" ID="col3" ucd="meta.id;meta.main"
   datatype="char" arraysize="8*"/>
   <FIELD name="RVel" ID="col4" ucd="spect.dopplerVeloc" datatype="int"
   width="5" unit="km/s"/>
   <FIELD name="e_RVel" ID="col5" ucd="stat.error;spect.dopplerVeloc"
   datatype="int" width="3" unit="km/s"/>
   <FIELD name="R" ID="col6" ucd="pos.distance;pos.heliocentric"
   datatype="float" width="4" precision="1" unit="Mpc">
   <DESCRIPTION>Distance of Galaxy, assuming H=75km/s/Mpc</DESCRIPTION>
   </FIELD>
   <DATA>
   <TABLEDATA>
   <TR>
   <TD>010.68</TD><TD>+41.27</TD><TD>N  224</TD><TD>-297</TD><TD>5</TD><TD>0.7</TD>
   </TR>
   <TR>
   <TD>287.43</TD><TD>-63.85</TD><TD>N 6744</TD><TD>839</TD><TD>6</TD><TD>10.4</TD>
   </TR>
   <TR>
   <TD>023.48</TD><TD>+30.66</TD><TD>N  598</TD><TD>-182</TD><TD>3</TD><TD>0.7</TD>
   </TR>
   </TABLEDATA>
   </DATA>
   </TABLE>
   </RESOURCE>
   </VOTABLE>

   The Data Model can be expressed as:
   VOTable 	= 	hierarchy of Metadata + associated TableData, arranged as a set of Tables
   Metadata 	= 	Parameters + Infos + Descriptions + Links + Fields + Groups
   Table 	= 	list of Fields + TableData
   TableData 	= 	stream of Rows
   Row 	= 	list of Cells
   Cell 	=
   Primitive
   or variable-length list of Primitives
   or multidimensional array of Primitives
   Primitive 	= 	integer, character, float, floatComplex, etc (see table of primitives below).
   *  
   * The VOTable object.
   *
   * @param __metadata    The metadata from the source.
   * @param __resources   The resources from the source.
   * @constructor
   */
  function VOTable(__metadata, __resources) {
    var _self = this

    _self.resources = __resources
    _self.metadata = __metadata

    function getResources() {
      return _self.resources
    }

    function getMetadata() {
      return _self.metadata
    }

    $.extend(this, {
      getResources: getResources,
      getMetadata: getMetadata
    })
  }

  /**
   * VOTable Metadata class.
   *
   * @param __parameters
   * @param __infos
   * @param _description
   * @param __links
   * @param __fields
   * @param __groups
   * @constructor
   */
  function Metadata(
    __parameters,
    __infos,
    _description,
    __links,
    __fields,
    __groups
  ) {
    var _selfMetadata = this

    _selfMetadata.parameters = __parameters || []
    _selfMetadata.infos = __infos || []
    _selfMetadata.description = _description
    _selfMetadata.links = __links || []
    _selfMetadata.fields = __fields || []
    _selfMetadata.groups = __groups || []

    function getInfos() {
      return _selfMetadata.infos
    }

    function getDescription() {
      return _selfMetadata.description
    }

    function getParameters() {
      return _selfMetadata.parameters
    }

    function getFields() {
      return _selfMetadata.fields
    }

    /**
     * Set this metadata's fields.
     *
     * @param _fields  {Array} of values.
     */
    function setFields(_fields) {
      _selfMetadata.fields = _fields
    }

    function getLinks() {
      return _selfMetadata.links
    }

    function getGroups() {
      return _selfMetadata.groups
    }

    function addField(_field) {
      getFields().push(_field)
    }

    function insertField(_fieldIndex, _field) {
      getFields()[_fieldIndex] = _field
    }

    function hasFieldWithID(_fieldID) {
      return getField(_fieldID) != null
    }

    /**
     * Obtain a Field by its ID.
     * @param _fieldID    The field ID to obtain a field for.
     *
     * @return {cadc.vot.Field}   Field instance, or null if not found.
     */
    function getField(_fieldID) {
      if (_fieldID) {
        var currFields = getFields()

        for (var f = 0; f < currFields.length; f++) {
          var nextField = currFields[f]

          if (nextField && currFields[f].getID() == _fieldID) {
            return nextField
          }
        }
      }

      return null
    }

    $.extend(this, {
      getInfos: getInfos,
      getDescription: getDescription,
      getParameters: getParameters,
      getFields: getFields,
      getField: getField,
      setFields: setFields,
      getLinks: getLinks,
      getGroups: getGroups,
      addField: addField,
      insertField: insertField,
      hasFieldWithID: hasFieldWithID
    })
  }

  function Datatype(_datatypeValue) {
    var _selfDatatype = this

    _selfDatatype.datatypeValue = _datatypeValue || ''

    var stringTypes = [
      'varchar',
      'char',
      'adql:VARCHAR',
      'adql:CLOB',
      'adql:REGION',
      'polygon',
      'point',
      'circle',
      'interval',
      'uri'
    ]
    var integerTypes = ['int', 'long', 'short']
    var floatingPointTypes = ['float', 'double', 'adql:DOUBLE', 'adql:FLOAT']
    var timestampTypes = ['timestamp', 'adql:TIMESTAMP']
    var stringUtil = new org.opencadc.StringUtil()

    function getDatatypeValue() {
      return _selfDatatype.datatypeValue
    }

    function isNumeric() {
      // will accept float, double, long, int, short, real, adql:DOUBLE,
      // adql:INTEGER, adql:POINT, adql:REAL
      //
      return !isCharDatatype() && !isTimestamp() && !isBoolean()
    }

    /**
     * Return whether this datatype is a Timestamp.
     * @returns {boolean}   True if timestamp, False otherwise.
     */
    function isTimestamp() {
      return datatypeMatches(timestampTypes)
    }

    function isBoolean() {
      return getDatatypeValue() === 'boolean'
    }

    function isFloatingPointNumeric() {
      return datatypeMatches(floatingPointTypes)
    }

    function isIntegerNumeric() {
      return datatypeMatches(integerTypes)
    }

    function isCharDatatype() {
      return datatypeMatches(stringTypes)
    }

    function datatypeMatches(_datatypes) {
      var dataTypeValue = getDatatypeValue()
      for (var stIndex = 0; stIndex < _datatypes.length; stIndex++) {
        if (stringUtil.contains(dataTypeValue, _datatypes[stIndex])) {
          return true
        }
      }
      return false
    }

    $.extend(this, {
      getDatatypeValue: getDatatypeValue,
      isNumeric: isNumeric,
      isTimestamp: isTimestamp,
      isIntegerNumeric: isIntegerNumeric,
      isFloatingPointNumeric: isFloatingPointNumeric,
      isBoolean: isBoolean
    })
  }

  /**
   *
   * @param _name
   * @param _id
   * @param _ucd
   * @param _utype
   * @param _unit
   * @param _xtype
   * @param __datatype    Datatype object.
   * @param _arraysize
   * @param _description
   * @param label
   * @constructor
   */
  function Field(
    _name,
    _id,
    _ucd,
    _utype,
    _unit,
    _xtype,
    __datatype,
    _arraysize,
    _description,
    label
  ) {
    var _selfField = this
    var INTERVAL_XTYPE_KEYWORD = 'INTERVAL'
    var types = set(__datatype, _xtype)

    _selfField.name = _name
    _selfField.id = _id
    _selfField.ucd = _ucd
    _selfField.utype = _utype
    _selfField.unit = _unit
    _selfField.xtype = types._xt
    _selfField.datatype = types._dt
    _selfField.arraysize = _arraysize
    _selfField.description = _description
    _selfField.label = label

    function getName() {
      return _selfField.name
    }

    function getID() {
      return _selfField.id
    }

    function getLabel() {
      return _selfField.label
    }

    function getUType() {
      return _selfField.utype
    }

    function getUCD() {
      return _selfField.ucd
    }

    function getUnit() {
      return _selfField.unit
    }

    function getXType() {
      return _selfField.xtype
    }

    function containsInterval() {
      var stringUtil = new org.opencadc.StringUtil()
      return (
        stringUtil.contains(getXType(), INTERVAL_XTYPE_KEYWORD, false) ||
        stringUtil.contains(
          getDatatype().getDatatypeValue(),
          INTERVAL_XTYPE_KEYWORD,
          false
        )
      )
    }

    function getDatatype() {
      return _selfField.datatype
    }

    function getDescription() {
      return _selfField.description
    }

    function getArraySize() {
      return _selfField.arraysize
    }

    function set(_dtype, _xtype) {
      var dt, xt
      if (_xtype) {
        // xtype = 'polygon' | 'circle' | 'point' | 'interval' | 'uri'
        // over-rides the datatype of 'double'
        dt = new Datatype(_xtype)
      } else if (_dtype) {
        if (typeof _dtype === 'object') {
          dt = _dtype
        } else {
          var stringUtil = new org.opencadc.StringUtil()
          if (stringUtil.contains(_dtype, INTERVAL_XTYPE_KEYWORD)) {
            xt = INTERVAL_XTYPE_KEYWORD
          }
          dt = new Datatype(_dtype)
        }
      } else {
        dt = new Datatype('varchar')
      }
      return { _dt: dt, _xt: xt }
    }

    $.extend(this, {
      getDatatype: getDatatype,
      getID: getID,
      getName: getName,
      getUnit: getUnit,
      getUType: getUType,
      getXType: getXType,
      containsInterval: containsInterval,
      getDescription: getDescription,
      getUCD: getUCD,
      getArraySize: getArraySize
    })
  }

  /**
   *
   * @param _name
   * @param _id
   * @param _ucd
   * @param _utype
   * @param _unit
   * @param _xtype
   * @param __datatype
   * @param _arraysize
   * @param _description
   * @param _value
   * @constructor
   */
  function Parameter(
    _name,
    _id,
    _ucd,
    _utype,
    _unit,
    _xtype,
    __datatype,
    _arraysize,
    _description,
    _value
  ) {
    var _selfParameter = this

    _selfParameter.name = _name
    _selfParameter.id = _id
    _selfParameter.ucd = _ucd
    _selfParameter.utype = _utype
    _selfParameter.unit = _unit
    _selfParameter.xtype = _xtype
    _selfParameter.datatype = __datatype || {}
    _selfParameter.arraysize = _arraysize
    _selfParameter.description = _description
    _selfParameter.value = _value

    function getName() {
      return _selfParameter.name
    }

    function getValue() {
      return _selfParameter.value
    }

    function getUType() {
      return _selfParameter.utype
    }

    function getID() {
      return _selfParameter.id
    }

    function getUCD() {
      return _selfParameter.ucd
    }

    function getDescription() {
      return _selfParameter.description
    }

    $.extend(this, {
      getName: getName,
      getValue: getValue,
      getUType: getUType,
      getID: getID,
      getUCD: getUCD,
      getDescription: getDescription
    })
  }

  function Info(_name, _value) {
    var _selfInfo = this

    _selfInfo.name = _name
    _selfInfo.value = _value

    function getName() {
      return _selfInfo.name
    }

    function getValue() {
      return _selfInfo.value
    }

    function isError() {
      return getName() == 'ERROR'
    }

    $.extend(this, {
      getName: getName,
      getValue: getValue,
      isError: isError
    })
  }

  function Resource(_ID, _name, _metaFlag, __metadata, __tables) {
    var _selfResource = this

    _selfResource.ID = _ID
    _selfResource.name = _name
    _selfResource.metaFlag = _metaFlag
    _selfResource.metadata = __metadata
    _selfResource.tables = __tables

    function getTables() {
      return _selfResource.tables
    }

    function getID() {
      return _selfResource.ID
    }

    function isMeta() {
      return _selfResource.metaFlag
    }

    function getName() {
      return _selfResource.name
    }

    function getMetadata() {
      return _selfResource.metadata
    }

    function getDescription() {
      return getMetadata().getDescription()
    }

    function getInfos() {
      return getMetadata().getInfos()
    }

    $.extend(this, {
      getTables: getTables,
      getMetadata: getMetadata,
      getID: getID,
      getName: getName,
      isMeta: isMeta,
      getInfos: getInfos,
      getDescription: getDescription
    })
  }

  /**
   *
   * @param __metadata
   * @param __tabledata
   * @constructor
   */
  function Table(__metadata, __tabledata) {
    var _selfTable = this

    _selfTable.metadata = __metadata
    _selfTable.tabledata = __tabledata

    function getTableData() {
      return _selfTable.tabledata
    }

    function getMetadata() {
      return _selfTable.metadata
    }

    function getFields() {
      return getMetadata().getFields()
    }

    $.extend(this, {
      getTableData: getTableData,
      getFields: getFields,
      getMetadata: getMetadata
    })
  }

  /**
   *
   * @param _id
   * @param __cells
   * @constructor
   */
  function Row(_id, __cells) {
    var _selfRow = this

    _selfRow.id = _id
    _selfRow.cells = __cells || []

    function getID() {
      return _selfRow.id
    }

    function getCells() {
      return _selfRow.cells
    }

    function getSize() {
      return getCells().length
    }

    /**
     * Obtain the value of a cell in this row.
     *
     * @param _fieldID    The ID of the cell's field.
     * @returns {*}     Value of the cell.
     */
    function getCellValue(_fieldID) {
      var value = null

      $.each(getCells(), function(cellIndex, cell) {
        var cellFieldID = cell.getField().getID()

        if (cellFieldID === _fieldID) {
          value = cell.getValue()
        }
      })

      return value
    }

    $.extend(this, {
      getID: getID,
      getCells: getCells,
      getSize: getSize,
      getCellValue: getCellValue
    })
  }

  /**
   * Cell object within a row.
   *
   * @param _value
   * @param __field
   * @constructor
   */
  function Cell(_value, __field) {
    var _selfCell = this

    _selfCell.value = _value
    _selfCell.field = __field

    function getValue() {
      return _selfCell.value
    }

    function getField() {
      return _selfCell.field
    }

    $.extend(this, {
      getValue: getValue,
      getField: getField
    })
  }

  /**
   *
   * @param __rows
   * @param _longestValues
   * @constructor
   */
  function TableData(__rows, _longestValues) {
    var _selfTableData = this

    _selfTableData.rows = __rows
    _selfTableData.longestValues = _longestValues || {}

    function getRows() {
      return _selfTableData.rows
    }

    function getLongestValues() {
      return _selfTableData.longestValues
    }

    $.extend(this, {
      getRows: getRows,
      getLongestValues: getLongestValues
    })
  }
})(jQuery, window)
