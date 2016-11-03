<%@ page import="ca.nrc.cadc.ApplicationConfiguration" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final ApplicationConfiguration configuration = new ApplicationConfiguration();
  final String defaultTheme = "gcwu-fegc";

  // Conservative default.
  final int defaultMaxRowLimit = 10000;
  final String contentLanguage = request.getHeader("Content-Language");
  final String requestHeaderLang = (contentLanguage == null)
                                   ? "en" : contentLanguage;

  final String staticWebHost =
      configuration.getString("org.opencadc.search.static-web-host",
                              "beta.cadc-ccda.hia-iha.nrc-cnrc.gc.ca");
  final String maintenanceWarningURL = requestHeaderLang
                                       + "/future_maintenance.html";
  String headerURL = requestHeaderLang
                     + "/_page_header.html?LAST_MOD=$LastChangedDate$";
  String footerURL = requestHeaderLang + "/_page_footer.html";
  final String bannerURL = requestHeaderLang
                           + "/_" + (requestHeaderLang.equals("fr") ? "ccda"
                                                                    : "cadc")
                           + "_banner.html";
  final String siteMenuURL = requestHeaderLang + "/_"
                             + (requestHeaderLang.equals("fr") ? "ccda"
                                                               : "cadc")
                             + "_site_menu.html";
  final String downloadLink = requestHeaderLang + "/"
                              + (requestHeaderLang.equals("fr")
                                 ? "telecharger" : "download");

  final int maxRowLimit =
      configuration.getInt("org.opencadc.search.max-row-count",
                           defaultMaxRowLimit);
  final boolean showObsCoreTab =
      configuration.getBoolean("org.opencadc.search.obs-core", false);
  final String theme = configuration.getString("org.opencadc.search.theme", null);

  if (theme != null)
  {
    headerURL += "&theme=" + theme;
  }

  final boolean isDefaultTheme = ((theme == null)
                                  || (theme.equals(defaultTheme)));
  footerURL += "?showfooter=" + isDefaultTheme;
  final String themeCSSPrefix = isDefaultTheme ? "gcwu" : "base";
%>

<%-- Request scope variables so they can be seen in the imported JSPs --%>
<fmt:setLocale value="<%= requestHeaderLang %>" scope="request"/>
<fmt:setBundle basename="AdvancedSearchBundle"
               var="langBundle" scope="request"/>

<c:import url="<%= headerURL %>"/>

<body>

<!-- Always include wb-body at the top, even though it's not ended until the footer. -->
<div id="wb-body">

<div id="wb-skip">
  <ul id="wb-tphp">
    <li id="wb-skip1"><a href="#wb-cont">Skip to main content</a></li>

    <!-- The wb-nav element is in the page footer. -->
    <li id="wb-skip2"><a href="#wb-nav">Skip to secondary menu</a></li>
  </ul>
</div>

<!--
 ####
 ####
 COPY THIS wb-head, and set the breadcrumbs appropriately.
 ####
 ####
-->
    <% if (isDefaultTheme) { %>
<div id="wb-head">
  <div id="wb-head-in">
    <header>
      <!-- HeaderStart -->
      <c:import url="<%= bannerURL %>"/>

      <nav role="navigation">
        <span lang="fr" class="lang-link-target">/fr/recherche/</span>
        <span lang="en" class="lang-link-target">/en/search/</span>

        <c:import url="<%= siteMenuURL %>"/>

        <div id="gcwu-bc">
          <h2><fmt:message key="BC_TITLE" bundle="${langBundle}"/></h2>

          <div id="gcwu-bc-in">
            <ol>
              <li><a href='<fmt:message key="BC_HOME_URI" bundle="${langBundle}"/>'><fmt:message key="BC_HOME_LABEL" bundle="${langBundle}"/></a></li>
              <li><fmt:message key="TITLE" bundle="${langBundle}"/></li>
            </ol>
          </div>
        </div>
      </nav>
      <!-- HeaderEnd -->
    </header>
  </div>
</div>
    <% } %>
<!--
 ####
 ####
 END COPY
 ####
 ####
-->

<div id="wb-core" class="<%= themeCSSPrefix %>">
<div id="wb-core-in">
  <%--<c:import url="<%= maintenanceWarningURL %>" />--%>
<div id="wb-main" role="main">
<div id="wb-main-in">
<%-- MainContentStart --%>

<h1 id="wb-cont"><fmt:message key="TITLE" bundle="${langBundle}"/></h1>

<div id="tabContainer"
     class="wet-boew-tabbedinterface auto-height-none">
<ul id="tabList" class="tabs">
  <li class="default tab">
    <a href="#queryFormTab"><fmt:message key="CAOM_QUERY_TAB_TITLE"
                                         bundle="${langBundle}"/></a>
  </li>
  <% if (showObsCoreTab)
  { %>
  <li class="tab">
    <a href="#obsCoreQueryFormTab"><fmt:message key="OBSCORE_QUERY_TAB_TITLE"
                                                bundle="${langBundle}"/></a>
  </li>
  <% } %>
  <li class="tab">
    <a href="#resultTableTab"><fmt:message key="RESULTS_TAB_TITLE"
                                           bundle="${langBundle}"/></a>
  </li>
  <li class="tab">
    <a href="#errorTableTab"><fmt:message key="ERROR_TAB_TITLE"
                                          bundle="${langBundle}"/></a>
  </li>
  <li class="tab">
    <a href="#queryTab"><fmt:message key="ADQL_QUERY_TAB_TITLE"
                                     bundle="${langBundle}"/></a>
  </li>
  <li class="tab">
    <a href="#helpTab"> <fmt:message key="HELP_TAB_TITLE"
                                     bundle="${langBundle}"/></a>
  </li>
</ul>
<div class="tabs-panel">
<!-- CAOM2 Query Tab -->
<div id="queryFormTab">
  <form id="queryForm" name="queryForm" class="queryForm advanced_search_form"
        method="post" action="/search/find"
        enctype="multipart/form-data">

    <!-- Used by VOView to sort the results. -->
    <input type="hidden" name="sort_column" value="Start Date" />
    <input type="hidden" name="sort_order" value="descending" />

    <!-- Used by AdvancedSearch to pass to TAP. -->
    <input type="hidden" name="formName" value="adsform"/>
    <input type="hidden" name="SelectList" class="CAOM2_selectlist" />
    <input type="hidden" name="MaxRecords" value="<%= maxRowLimit %>"/>
    <input type="hidden" name="format" value="csv"/>

    <!-- Used by AdvancedSearch to pass to VOTV. -->
    <input type="hidden" id="max_row_limit_warning"
           value="<fmt:message key="MAX_ROW_LIMIT_WARNING" bundle="${langBundle}"/>"/>

    <div class="equalize margin-top-large margin-bottom-none">
      <div class="span-3 row-start form-inline">
        <input type="submit"
               value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"
               data-rel="popup" data-dismissible="false"
               data-position-to="window" data-inline="true"
               data-transition="pop" data-corners="true" data-shadow="true"
               data-iconshadow="true" data-wrapperels="span" data-theme="c"
               aria-haspopup="true" aria-owns="queryOverlay"
               class="submit-query button button-accent ui-btn"/>
        <input type="reset" data-role="none"
               value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"
               class="reset-query-form button ui-btn"/>
      </div>
      <div class="span-5 row-end"></div>
      <div class="clear"></div>
    </div>

    <div class="span-8 margin-top-none margin-bottom-none clarify-message">
      <fmt:message key="TOOLTIP_CLARIFICATION_MESSAGE_PREFIX" bundle="${langBundle}" />&nbsp;<span class="wb-icon-question"></span><fmt:message key="TOOLTIP_CLARIFICATION_MESSAGE_SUFFIX" bundle="${langBundle}" />
    </div>

    <div class="equalize">
      <div class="span-2 row-start">
        <div class="module">
          <h2 class="background-accent"><fmt:message
              key="OBSERVATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="text.jsp?utype=Observation.observationID&tipSide=right"/>
            <c:import
                url="text.jsp?utype=Observation.proposal.pi&tipSide=right&enableAutocomplete=true"/>
            <c:import
                url="text.jsp?utype=Observation.proposal.id&tipSide=right&enableAutocomplete=true"/>
            <c:import
                url="text.jsp?utype=Observation.proposal.title&tipSide=right&enableAutocomplete=true"/>
            <c:import
                url="text.jsp?utype=Observation.proposal.keywords&tipSide=right"/>
            <c:import
                url="timestamp.jsp?utype=Plane.dataRelease&tipSide=right" />
            <c:import
                url="_pulldown.jsp?utype=Observation.intent&tipSide=right"/>

          </ul>
        </div>
      </div>
      <div class="span-2">
        <div class="module">
          <h2 class="background-accent"><fmt:message
              key="SPATIAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="position.jsp?utype=Plane.position.bounds&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Plane.position.sampleSize&tipSide=right"/>
            <c:import
                url="boolean.jsp?name=Plane.position.DOWNLOADCUTOUT&tipSide=right"/>

          </ul>
        </div>
      </div>
      <div class="span-2">
        <div class="module">
          <h2 class="background-accent"><fmt:message key="TIME_CONSTRAINT_LABEL"
                                                     bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="date.jsp?utype=Plane.time.bounds&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.time.exposure&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.time.bounds.width&tipSide=left"/>

          </ul>
        </div>
      </div>
      <div class="span-2 row-end">
        <div class="module">
          <h2 class="background-accent"><fmt:message
              key="SPECTRAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="energy.jsp?utype=Plane.energy.bounds&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.sampleSize&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.resolvingPower&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.bounds.width&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.restwav&tipSide=left"/>
            <c:import
                url="boolean.jsp?name=Plane.energy.DOWNLOADCUTOUT&tipSide=left"/>

          </ul>
        </div>
      </div>
      <div class="clear"></div>
    </div>

    <div class="resolver-result-tooltip">
      <strong><fmt:message key="RES_TARGET" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-target"></p>
      <strong><fmt:message key="RES_SERVICE" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-service"></p>
      <strong><fmt:message key="RES_COORDINATES" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-coordinates"></p>
      <strong><fmt:message key="RES_TYPE" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-type"></p>
      <strong><fmt:message key="RES_MORPHOLOGY" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-morphology"></p>
      <strong><fmt:message key="RES_TIME" bundle="${langBundle}"/></strong><br>
      <p class="resolver-result-time"></p>
    </div>

    <c:import
        url="hierarchy.jsp?utype=Plane.energy.emBand/Observation.collection/Observation.instrument.name/Plane.energy.bandpassName/Plane.calibrationLevel/Plane.dataProductType/Observation.type&modelDataSource=caom2"/>

    <div class="equalize margin-top-large margin-bottom-none">
      <div class="span-3 row-start form-inline">
        <input type="submit"
               value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"
               data-rel="popup" data-dismissible="false"
               data-position-to="window" data-inline="true"
               data-transition="pop" data-corners="true" data-shadow="true"
               data-iconshadow="true" data-wrapperels="span" data-theme="c"
               aria-haspopup="true" aria-owns="queryOverlay"
               class="submit-query button button-accent ui-btn"/>
        <input type="reset" data-role="none"
               value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"
               class="reset-query-form button ui-btn"/>
      </div>
      <div class="span-5 row-end"></div>
      <div class="clear"></div>
    </div>

  </form>
</div>

<!-- ObsCore Query Tab -->
<div id="obsCoreQueryFormTab">
  <form id="obscoreQueryForm" name="obscoreQueryForm" class="queryForm"
        method="post" action="/search/find"
        enctype="multipart/form-data">

    <!-- Used by VOView to sort the results. -->
    <input type="hidden" name="sort_column" value="t_min"/>
    <input type="hidden" name="sort_order" value="descending"/>
    <input type="hidden" name="formName" value="adsform"/>

    <!-- Used by AdvancedSearch to pass to TAP. -->
    <input type="hidden" name="SelectList" class="ObsCore_selectlist" />
    <input type="hidden" name="MaxRecords" value="<%= maxRowLimit %>"/>
    <input type="hidden" name="format" value="csv"/>

    <div class="equalize margin-top-large margin-bottom-none">
      <div class="span-3 row-start form-inline">
        <input type="submit"
               value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"
               data-rel="popup" data-dismissible="false"
               data-position-to="window" data-inline="true"
               data-transition="pop" data-corners="true" data-shadow="true"
               data-iconshadow="true" data-wrapperels="span" data-theme="c"
               aria-haspopup="true" aria-owns="queryOverlay"
               class="submit-obscore-query button button-accent ui-btn"/>
        <input type="reset" data-role="none"
               value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"
               class="reset-obscore-query-form button ui-btn"/>
      </div>
      <div class="span-5 row-end"></div>
      <div class="clear"></div>
    </div>

    <div class="equalize">
      <div class="span-2 row-start">
        <div class="module">
          <h2 class="background-accent"><fmt:message
              key="OBSERVATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="text.jsp?utype=DataID.observationID&tipSide=right"/>
            <c:import
                url="text.jsp?utype=Target.Name&tipSide=right"/>
            <c:import
                url="timestamp.jsp?utype=Curation.releaseDate&tipSide=right" />

          </ul>
        </div>
      </div>

      <div class="span-2">
        <div class="module">
          <h2 class="background-accent"><fmt:message
              key="SPATIAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></h2>

          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="position.jsp?utype=Char.SpatialAxis.Coverage.Support.Area&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Char.SpatialAxis.Coverage.Bounds.Extent.diameter&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Char.SpatialAxis.Resolution.refval.value&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Char.SpatialAxis.numBins1&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Char.SpatialAxis.numBins2&tipSide=right"/>
            <c:import
                url="boolean.jsp?name=Char.SpatialAxis.DOWNLOADCUTOUT&tipSide=right"/>

          </ul>
        </div>
      </div>

      <div class="span-2">
        <div class="module">
          <h2 class="background-accent">
            <fmt:message key="TIME_POLARIZATION_CONSTRAINT_LABEL"
                         bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="date.jsp?utype=Char.TimeAxis.Coverage.Bounds.Limits&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.TimeAxis.Coverage.Support.Extent&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.TimeAxis.Resolution.refval.value&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.TimeAxis.numBins&tipSide=left"/>
            <c:import
                url="text.jsp?utype=Char.PolarizationAxis.stateList&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.PolarizationAxis.numBins&tipSide=left"/>

          </ul>
        </div>
      </div>

      <div class="span-2 row-end">
        <div class="module">
          <h2 class="background-accent">
            <fmt:message key="SPECTRAL_CONSTRAINT_LABEL"
                         bundle="${langBundle}"/></h2>
          <ul class="list-bullet-none indent-small search-constraints">

            <c:import
                url="energy.jsp?utype=Char.SpectralAxis.Coverage.Bounds.Limits&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.SpectralAxis.Resolution.ResolPower.refval&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Char.SpectralAxis.numBins&tipSide=left"/>
            <c:import
                url="boolean.jsp?name=Char.SpectralAxis.DOWNLOADCUTOUT&tipSide=left"/>

          </ul>
        </div>
      </div>
      <div class="clear"></div>
    </div>

    <c:import
        url="hierarchy.jsp?utype=DataID.Collection/Provenance.ObsConfig.Facility.name/Provenance.ObsConfig.Instrument.name/ObsDataset.calibLevel/ObsDataset.dataProductType&modelDataSource=obscore"/>

    <div class="equalize margin-top-large margin-bottom-none">
      <div class="span-3 row-start form-inline">
        <input type="submit"
               value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"
               data-rel="popup" data-dismissible="false"
               data-position-to="window" data-inline="true"
               data-transition="pop" data-corners="true" data-shadow="true"
               data-iconshadow="true" data-wrapperels="span" data-theme="c"
               aria-haspopup="true" aria-owns="queryOverlay"
               class="submit-obscore-query button button-accent ui-btn"/>
        <input type="reset" data-role="none"
               value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"
               class="reset-obscore-query-form button ui-btn"/>
      </div>
      <div class="span-5 row-end"></div>
      <div class="clear"></div>
    </div>

  </form>
</div>

<!-- Result Tab -->
<div id="resultTableTab">
  <span class="votable_link_label">
    <fmt:message key="FULL_VOTABLE_LINK_LABEL" bundle="${langBundle}"/></span>
  <a href="#" class="votable_link_votable link_idle">VOTable</a>
  <a href="#" class="votable_link_csv link_idle">CSV</a>
  <a href="#" class="votable_link_tsv link_idle">TSV</a>
  
  <span class="result-state">
    <a href="#" id="results_bookmark" class="result-state-link link_idle">
        <fmt:message key="RESULT_STATE_LINK_LABEL" bundle="${langBundle}"/>
    </a>
  </span>

  <!-- Dialog to contain the column manager. -->
  <div class="wb-invisible">
    <span id="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" class="wb-invisible i18n"><fmt:message key="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" bundle="${langBundle}" /></span>
    <span id="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" class="wb-invisible i18n"><fmt:message key="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" bundle="${langBundle}" /></span>
    <span id="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" class="wb-invisible i18n"><fmt:message key="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" bundle="${langBundle}" /></span>
    <div id="column_manager_container" data-role="popup" data-theme="b"
         class="column_manager_container ui-content">
      <span class="wb-icon-x-alt2 float-right dialog-close"></span>
      <h3><fmt:message key="COLUMN_MANAGER_HEADING_TEXT" bundle="${langBundle}" /></h3>
      <span class="tooltipColumnPickerHelpText">
        <fmt:message key="COLUMN_MANAGER_HELP_TEXT" bundle="${langBundle}" /></span>
      <div class="column_manager_columns"></div>
    </div>
  </div>

  <%-- Aladin Lite container. --%>
  <div id="aladin-lite" style="height: 250px;width: 1180px;"></div>

  <div class="grid-container">
    <div id="cadcvotv-empty-results-message"
         class="cadcvotv-empty-results-message">
      <strong><fmt:message key="NO_RESULTS_RETURNED" bundle="${langBundle}" /></strong>
    </div>
    <div id="results-grid-header" class="grid-header">
      <form id="downloadForm" name="downloadForm" class="form-horizontal"
            action="<%= downloadLink %>" method="POST" target="DOWNLOAD">
        <input type="hidden" name="fragment" id="runId" value=""/>
        <span id="NO_OBSERVATIONS_SELECTED_MESSAGE" class="wb-invisible">
            <fmt:message key="NO_OBSERVATIONS_SELECTED_MESSAGE" bundle="${langBundle}"/>
        </span>
        <span class="grid-header-icon-span">
          <img class="margin-bottom-none margin-left-none margin-right-none align-middle grid-header-icon" src="cadcVOTV/images/transparent-20.png"/>
        </span>
        <span>
          <button type="submit" id="downloadFormSubmit" form="downloadForm" class="button button-accent">
            <fmt:message key="DOWNLOAD_BUTTON_LABEL" bundle="${langBundle}" />
          </button>
        </span>
        <span class="grid-header-label"></span>

        <!-- Here to prepopulate the change column button -->
        <span class='slick-columnpicker-panel-change-column-holder'>
          <a href="#column_manager_container"
             id="slick-columnpicker-panel-change-column"
             name='slick-columnpicker-panel-change-column'
             data-rel="popup" data-position-to="window"
             data-inline="true" data-dismissible="false"
             class="button slick-columnpicker-panel-change-column-label ui-link button-add"><fmt:message key="COLUMN_MANAGER_BUTTON_LABEL" bundle="${langBundle}" /></a>
        </span>

        <!-- The Visualize button to enable AladinLite. -->
        <span class='slick-visualize-holder'>
          <a href="#" id="slick-visualize" name='slick-visualize'
             class="button slick-visualize-label ui-link button-add" data-close="<fmt:message key="CLOSE_BUTTON_LABEL" bundle="${langBundle}" />"><fmt:message key="RESULTS_VISUALIZE_BUTTON_LABEL" bundle="${langBundle}" /></a>
        </span>
      </form>
    </div>
    <div id="resultTable"></div>
    <div id="results-grid-footer" class="grid-footer">
      <span class="grid-footer-label"></span>
    </div>
  </div>
</div>

<!-- Error Tab -->
<div id="errorTableTab">
  <div class="grid-container">
    <div id="error-grid-header" class="grid-header">
      <span class="grid-header-label"></span>
    </div>
    <div id="errorTable"></div>
  </div>
  <div id="errorTooltipColumnPickerHolder">
    <div class="tooltip columnpicker">
      <h3>Add/remove displayed columns</h3>
      <span class="tooltipColumnPickerHelpText">
        Drag &amp; drop columns above or below the red bar, move the red bar
        itself or click on the checkboxes.
      </span>
      <br/>
      <br/>

      <h3>Reorder columns</h3>
      <span class="tooltipColumnPickerHelpText">
        Drag &amp; drop the columns or drag &amp; drop the column headers
        directly in the results table.
      </span>

      <div class="tooltip_content"></div>
    </div>
  </div>
</div>

<!-- Query Tab -->
<div id="queryTab" class="wet-boew-prettify lang-sql">
  <div id="query_holder">
    <h3 class="wb-invisible">ADQL Query</h3>
    <pre class="prettyprint lang-sql"><code id="query" class="lang-sql"></code></pre>
  </div>
</div>

<!-- Help Tab -->
<div id="helpTab" >
  <c:import url="_help.jsp"/>
</div>
</div>
</div>

<div class="wb-invisible">
  <div id="queryOverlay" data-role="popup">
    <img src="images/queryoverlay.gif" alt=""/>
    <br/>
              <span id="overlay_status">
                <fmt:message key="EXECUTING_QUERY_LABEL"
                             bundle="${langBundle}"/></span>
    <br/>
              <span id="overlay_cancel">
                <input id="cancel_search" type="button" value="Cancel"
                       class="button"/>
              </span>
  </div>
</div>

<div class="hidden" id="preloadthumbnails"></div>

<%-- To find pixel lengths of strings. --%>
<div id="lengthFinder"></div>

  <script type="text/javascript">
    $(function ()
      {
        // Load in the CADC VOTV related CSS
        $("head")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/aladin.min.css\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/jquery-ui-1.11.4.min.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/tooltipster.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/advanced_search.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick.grid-frozen.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick.pager.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/cadc.columnpicker.dialog.css?version=@version@\"/>")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/cadc.votv.css?version=@version@\" />")
            .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick-default-theme.css?version=@version@\" />");
      });
  </script>

  <script type="text/javascript"
          src="cadcVOTV/javascript/jquery.event.drag-2.2.min.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/jquery.form.js?version=@version@"></script>
  <script type="text/javascript" charset="utf-8"
          src="cadcVOTV/javascript/aladin.js?version=@version@"></script>
  <script type="text/javascript"
          src="wet/javascript/polyfills/detailssummary-min.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/jquery-ui-1.11.4.min.js?version=@version@"></script>

  <!-- Moment for date parsing and formatting -->
  <script type="text/javascript"
          src="js/moment.min.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/json.human.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/wgxpath.install.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/jquery.tooltipster.custom.min.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/jquery.csv-0.71.min.js"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/slick.core.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.rowselectionmodel.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.checkboxselectcolumn.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/slick.grid-frozen.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/slick.dataview.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/slick.pager.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.columnpicker.dialog.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.plugin.unitselection.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.plugin.filter_suggest.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.votable.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.votable-reader.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.votv.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.votv.comparer.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcJS/javascript/cadc.uri.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.resultstate.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcVOTV/javascript/cadc.plugin.footprint-viewer.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/validator.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.uws.js?version=@version@"></script>
  <script type="text/javascript"
          src="cadcJS/javascript/cadc.util.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.format.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.unitconversion.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.columnbundles.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.columns.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.core.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.form.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.preview.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.app.js?version=@version@"></script>
  <script type="text/javascript"
          src="js/cadc.search.tooltipcreator.js?version=@version@"></script>

  <script type="text/javascript"
          src="js/hierarchy.js?version=@version@"></script>

<script type="text/javascript">
  $(document).ready(function ()
                    {
                      var searchApp =
                          new ca.nrc.cadc.search.AdvancedSearchApp($("html").prop("lang"), false);

                      searchApp.subscribe(ca.nrc.cadc.search.events.onAdvancedSearchInit,
                                          function ()
                                          {
                                            // TODO: Deal with situation where error is not null
                                            searchApp.start();
                                          });

                      searchApp.init();
                    });
</script>

  <% if (isDefaultTheme) { %>
<dl id="<%= themeCSSPrefix %>-date-mod" role="contentinfo">
  <dt><fmt:message key="DATE_MODIFIED_LABEL" bundle="${langBundle}"/>:</dt>
  <dd>
              <span>
                <time>$LastChangedDate$</time>
              </span>
  </dd>
</dl>
  <%  } %>

</div>
</div>

<!-- noindex -->

</div>
</div>
  <!-- Close off the wb-body -->
</div>

<c:import url="_page_footer_js.html" />

</body>
</html>
