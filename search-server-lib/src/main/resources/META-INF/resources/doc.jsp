<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Advanced Search Documentation</title>
    <link rel="stylesheet" href="/cadc/resources/cadc.css">
    <script type="text/javascript" src="/AdvancedSearch/resources/js/http.js"></script>
    <c:import url="http://localhost/cadc/skin/htmlHead" />
  </head>
  <body>
    <c:import url="http://localhost/cadc/skin/bodyHeader" />
    <div class="main">
      <h1>Advanced Search Documentation</h1>
      <p>
        AdvancedSearch is a web application that processes customizable query forms,
        generates and executes queries against the CVO Observation database, and displays
        the results (a list of observations) to the user in one or more formats. 
        Query forms are defined using the normal <code>FORM</code> element in an HTML page by including one
        or more configurable components. There are one or more components for each data type in
        use in the target database. For each data type, there may be multiple components that
        provide different levels of detail in specifying the query conditions.
      </p>

      <h2>Components</h2>
      <p>The currently available components are:</p>
        <ul>
          <li><a href="#shape1">shape1</a> - simple spatial query as used in QuickSearch</li>
          <li><a href="#shape2">shape2</a> - structured spatial query with built-in name resolver</li>
          <li><a href="#number">number</a> - number falls with a range, possibly open-ended</li>
          <li><a href="#text">text</a> - text search</li>
          <li><a href="#interval1">interval1</a> - interval includes specified value</li>
          <li><a href="#interval2">interval2</a> - interval overlaps another interval</li>
          <li><a href="#interval3">interval3</a> - interval pattern matching </li>
          <li><a href="#hierarchy">hierarchy</a> - one or more connected pick-lists</li>
        </ul>

      <p>
        For each component, the caller must specify which attribute in the data model (CAOM) is 
        to be used (see individual component documentation below for the list). In a HTML page, 
        a component can be included in the form using the Apache Server Side Include 
        (SSI) directive. For example, the QuickSearch component (shape1.jsp) would be included with:
      </p>

      <pre>
    &lt;!--#include virtual="/AdvancedSearch/shape1?attribute=plane.position.bounds"--&gt;
      </pre>

      <p>
        The <code>attribute</code> parameter is required. In some components, the semantics of the attribute within
        the model are used to configure other parts of the components, such as unit pick-lists and format parsers. 
        For example, if one uses the <code>number.jsp</code> component with the exposure time (plane.time.exposure),
        a unit pick-list will be included that lets users pick from a suitable list of time units. If that same
        component is used with the spatial pixel scale (plane.position.sampleSize), the unit pick-list will automatically 
        have suitable length units. 
      </p>
      <p>
        Each component also supports an optional <code>label</code> parameter to change the default label (some
        arbitrary text that appears in the component and normally determined from the attribute) to a specific 
        string. Use of this feature in archive-specific pages will perpetuate the current situation where every
        archive has it's own terminology for the same concepts; however, it does enable an archive-specific page
        to use labels that are familiar to existing (knowledgeable) users. <em>It would be preferable if the
        default labels were perfect and users understood them!</em> Use with care.
      </p>

      <h2>Creating a Custom Form</h2>
      <p>
        The components must be dynamically included in an HTML FORM with the attributes:
      </p>

      <pre>
    &lt;FORM name="adsform" method="post" action="/AdvancedSearch/find"&gt;
        ...
	&lt;!--#include virtual="/AdvancedSearch/submit"--&gt;
    &lt;/FORM&gt;
      </pre>

      <p>
        The form should include one or more search components and must also include 
	the <code>/AdvancedSearch/submit</code> resource. This resource provides
	the submit button and will be extended to include options (alternate views of results).
      </p>

      <h2>Now What?</h2>
      <p>
        When the form is submitted, the AdvancedSearch application will process the parameters, generate and execute the query,
        and write a result page. The result page includes the form (at the top) with the currently specified conditions and 
        the result table (displayed as an HTML table) at the bottom. Everything inside the FORM tag is reproduced, including 
        explanatory text, images, links, etc. (Note: AdvancedSearch retrieves the original form from the HTML page by getting 
        and parsing the referred page, so the referrer page must itself be served by a web server -- even during development.)
      </p>
      <p>
        The AdvancedASearch result page is skinnable (TODO: link to skinning documentation). It will be decorated by the 
	default AdvancedSearch skin (CVO skin) 
        unless the caller requests something different via the <code>skin</code> parameter. If the specified skin does not
        exist, the result page will have no decorations. Thus, if you want to include the result page inside another page, 
        simply specify a nonexistent skin in the form's action attribute:
      </p>
      <pre>
    &lt;FORM name="adsform" method="post" action="/AdvancedSearch/find"&gt;
    	&lt;input type="hidden" name="skin" value="ThisDoesNotExist"&gt;
        ...
	&lt;!--#include virtual="/AdvancedSearch/submit"--&gt;
    &lt;/FORM&gt;
      </pre>

      <h1>Component Documentation</h1>

      <!-- SHAPE1 -->
      <div>
        <h2><a name="shape1">Simple Positional Search: shape1.jsp</a></h2>
        <form name="adsform1" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>shape1.jsp</code> component provides a single text box for spatial query
            conditions. Users can enter coordinates in degrees (ICRS only) or sexigessimal or 
            an object name (which will be reolved to coordinates), plus an optional search radius 
            (degrees by default, optional ' or " for arcmin or arcsec). Only the coordinates and 
            radius are used in the query, so this component specifies a circle or point to be 
            compared to a 2D shape in the database.  The condition is true for every shape that 
            includes the specified point (r=0)
            or intersects the specified circle.
          </p>
          <p>  
            This is the same component used for QuickSearch on the CADC home page.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
              <c:import url="/shape1?attribute=plane.position.bounds" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component in an HTML page served directly by Apache:
        </p>
        <pre>&lt;!--#include virtual="/AdvancedSearch/shape1?attribute=plane.position.bounds"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
            <li>plane.position.bounds</li>          
        </ul>
      </div>

      <br>

      <!-- SHAPE2 -->
      <div>
        <h2><a name="shape2">Structured Positional Search: shape2.jsp</a></h2>
        <form name="adsform2" method="post" action="/AdvancedSearch/find">
          <p>
             The <code>shape2.jsp</code> component provides a structured spatial query condition. It includes
             a name resolver (text field and button) that uses JavaScript to query the server and populate the 
             coordinate boxes without reloading the page. Only the coordinates and radius (optional) are used in the
             query, so like <code>shape1.jsp</code> this component specifies a circle or point to be compared to a 
             2D shape in the database. The condition is true for every shape that includes the specified point (r=0)
             or intersects the specified circle.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/shape2?attribute=plane.position.bounds" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>
        <pre>&lt;!--#include virtual="/AdvancedSearch/shape2?attribute=plane.position.bounds"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
          <li>plane.position.bounds</li>
        </ul>          
        <p>
          Future supported CAOM attributes:
        </p>
        <ul>
          <li>TODO</li>
        </ul>   
      </div>

      <br>

      <!-- INTERVAL1 -->
      <div>
        <h2><a name="interval1">Simplest Interval Search: interval1.jsp</a></h2>
        <form name="adsform3" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>interval1.jsp</code> component provides a simple query condition on intervals (ranges), 
            such as the energy or time coverage of an observation. The user can provide a single value (with
            units from a suitable pick-list of units) and the condition is true for every interval that contains
            the specified value.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/interval1?attribute=plane.energy.bounds" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>
        <pre>&lt;!--#include virtual="/AdvancedSearch/interval1?attribute=plane.energy.bounds"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
          <li>plane.energy.bounds</li>         
          <li>plane.time.bounds</li>
        </ul>
      </div>

      <br>

      <!-- INTERVAL2 -->
      <div>
        <h2><a name="interval2">Broad Interval Search: interval2.jsp</a></h2>
        <form name="adsform4" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>interval2.jsp</code> component provides a broad query condition on intervals (ranges), 
            such as the energy or time coverage of an observation. The user can provide an interval value (with
            units from a suitable pick-list of units) and the condition is true for every interval that intersects
            (overlaps) the specified interval.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/interval2?attribute=plane.energy.bounds" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>  
        <pre>&lt;!--#include virtual="/AdvancedSearch/interval2?attribute=plane.energy.bounds"--&gt;</pre>
        <p>
          Currently supported attributes parameters:
        </p>
        <ul>
          <li>plane.energy.bounds</li>         
          <li>plane.time.bounds</li>
        </ul>
      </div>

      <br>

      <!-- INTERVAL3 -->
      <div>
        <h2><a name="interval3">Interval Pattern Matching: interval3.jsp</a></h2>
        <form name="adsform5" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>interval3.jsp</code> component provides a pattern matching query condition on intervals (ranges), 
            such as the energy or time coverage of an observation. The user can provide an interval value (with
            units from a suitable pick-list of units) and a matching tolerance; the condition is true for every 
            interval where both endpoints are within the specified tolerance of the specified interval endpoints.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/interval3?attribute=plane.energy.bounds" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>  
        <pre>&lt;!--#include virtual="/AdvancedSearch/interval3?attribute=plane.energy.bounds"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
          <li>plane.energy.bounds</li>         
          <li>plane.time.bounds</li>
        </ul>
      </div>

      <br>

      <!-- NUMBER -->
      <div>
        <h2><a name="number">Simple Numeric Search: number.jsp</a></h2>
        <form name="adsform6" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>number.jsp</code> component provides a way to specify a query condition on a simple
            numeric value. The user can provide a possibly open-ended range and the condition is true for 
            any numeric values that fall within this range. The component also includes unit pick-lists with 
            units that are suitable for the attribute.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/number?attribute=plane.time.exposure" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>
        <pre>&lt;!--#include virtual="/AdvancedSearch/number?attribute=plane.time.exposure"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
          <li>plane.energy.bounds.cval1</li>         
          <li>plane.energy.bounds.cval2</li>
          <li>plane.time.bounds.cval1</li>         
          <li>plane.time.bounds.cval2</li>
          <li>plane.time.exposure</li>
        </ul>
        <p>
          Future supported CAOM attributes:
        </p>
        <ul>
          <li>TODO</li>
        </ul>   
      </div>

      <br>

      <!-- TEXT -->
      <div>
        <h2><a name="text">Free Text Search: text.jsp</a></h2>
        <form name="adsform7" method="post" action="/AdvancedSearch/find">
          <p>
            The <code>text.jsp</code> component provides a free text search condition. (Note: this currently
            only supports strict equality comparison; this limitation will be removed ASAP, but for now it really
            is not a free text search at all... sorry.)
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/text?attribute=SimpleObservation.collectionID" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>  
        <pre>&lt;!--#include virtual="/AdvancedSearch/text?attribute=SimpleObservation.collectionID"--&gt;</pre>
        <p>
          Currently supported attribute values:
        </p>
        <ul>
          <li>SimpleObservation.collection</li>         
          <li>SimpleObservation.collectionID</li> 
          <li>SimpleObservation.project</li>
          <li>SimpleObservation.instrument.name</li>
          <li>Plane.project</li>
          <li>Plane.energy.bandpassName</li>          
        </ul>
        <p>
            Future supported CAOM attributes:
        </p>
        <ul>
          <li>TODO</li>
        </ul>   
      </div>

      <br>

      <!-- HIERARCHY -->        
      <div>
        <h2><a name="hierarchy">Pick-lists and Multi-Pick-lists: hierarchy.jsp</a></h2>
          <p>
            The <code>hierarchy.jsp</code> component provides one or more pick-lists with values from a
            hierarchy (tree structure). The concept and operation are identical to that found in music 
            collection/playing software like Rhythmbox or iTunes (genre, artist, album, title). In this case, 
            the <code>attribute</code> is an ordered list of CAOM attributes that defines the hierarchy.
            Items in  the list are separated by the / character, so it looks like a path. A pick-list
            is generated for each item in the attribute list that has multiple values to chose from.
          </p>

        <form name="adsform8" method="post" action="/AdvancedSearch/find">        
          <p>
            If the attribute list contains a single CAOM attribute, this creates a single pick-list with 
            the possible values of that attribute. The value list is dynamically generated from the 
            database.
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/hierarchy?attribute=SimpleObservation.collection" />   
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>

        <p>
          To include this component on a page:
        </p>  
        <pre>&lt;!--#include virtual="/AdvancedSearch/hierarchy?attribute=SimpleObservation.collection"--&gt;</pre>
        <p>
          A specific attribute in the list (generally the leading one) can have it's value set by appending a 
          colon followed by the value to the CAOM attribute. This will generate a pick-list for a 
          subset of other values. For example, here we generate a pick-list of filter names for the CFHT collection.
        </p>

        <form name="adsform9" method="post" action="/AdvancedSearch/find">
          <p>
            If the attribute contains two CAOM attributes and the leading one has a set value, this creates an 
            embedded condition on the first attribute and a single pick-list for the second attribute. The pick-list contains
            only those values that are found when the embedded condition is true. For example, one can declare a hierarchy
            of collection and filter and restrict the collection value to get a single pick-list (filters in the specified
            collection) as follows:
          </p>
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/hierarchy?attribute=SimpleObservation.collection:CFHT/Plane.energy.bandpassName" />
         </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>   
        </form>
        <p>
          To include this component on a page:
        </p>  
        <pre>&lt;!--#include virtual="/AdvancedSearch/hierarchy?attribute=SimpleObservation.collection:CFHT/Plane.energy.bandpassName"--&gt;</pre>
        <p>
          Multiple connected pick-lists are generated when there are multiple items in the attribute list. The content of
          the lists (the hierarchy) is dynamically generated from the database. This component also includes client-side 
          JavaScript that knows the hierarchy (the relationships between the pick-list values) and selecting value(s) on one 
          pick-list will restrict the available choices in subsequent pick-lists (the ones to the right in the attribute list and the page).
          (NOTE: This JavaScript feature is not working yet so it has been disabled for now)
        </p>

        <form name="adsform10" method="post" action="/AdvancedSearch/find">
          <br>
          <div style="margin-left: 2em; margin-right: 2em; background-color: #D3D3D3;">
            <c:import url="/hierarchy?attribute=SimpleObservation.collection/SimpleObservation.instrument.name/Plane.energy.bandpassName" />
          </div>
          <br>
          <div style="margin-left: 2em;">
            <c:import url="/submit?label= Try it " />
          </div>  
	</form>
          <p>
            To include this component on a page:
          </p>  
          <pre>&lt;!--#include virtual="/AdvancedSearch/hierarchy?attribute=SimpleObservation.collection/SimpleObservation.instrument.name/Plane.energy.bandpassName"--&gt;</pre>
          <p>
            Since this component generates pick-lists, it is useful with any attribute where there are a small 
            number of possible values (up to 10 or so). Thus, page/form developers must understand the content in
            order to define useful hierarchies or attributes. Currently supported attribute values:
          </p>
          <ul>
            <li>collection</li>         
            <li>project</li>
            <li>instrument.name</li>         
            <li>plane.energy.bandpassName</li>        
          </ul>
          <p>
            Future supported CAOM attributes:
          </p>
          <ul>
            <li>TODO</li>
          </ul>   
      </div>
    </div>
    <br>
    <c:import url="http://localhost/cadc/skin/bodyFooter" />
  </body>
</html>
