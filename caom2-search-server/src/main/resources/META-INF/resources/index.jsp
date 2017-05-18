<%@ page import="ca.nrc.cadc.config.ApplicationConfiguration" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final ApplicationConfiguration configuration = new ApplicationConfiguration();

  // Conservative default.
  final int defaultMaxRowLimit = 10000;
  final String contentLanguage = request.getHeader("Content-Language");
  final String requestHeaderLang = (contentLanguage == null)
                                   ? "en" : contentLanguage;

  final String downloadLink = "/downloadManager/download";

  final int maxRowLimit =
      configuration.lookupInt("org.opencadc.search.max-row-count",
                              defaultMaxRowLimit);
  final boolean showObsCoreTab =
      configuration.lookupBoolean("org.opencadc.search.obs-core", true);
  final String tapSyncEndpoint = configuration.lookup("org.opencadc.search.tap-service-endpoint", "/search/tap/sync");
%>

<%-- Request scope variables so they can be seen in the imported JSPs --%>
<fmt:setLocale value="<%= requestHeaderLang %>" scope="request"/>
<fmt:setBundle basename="AdvancedSearchBundle" var="langBundle" scope="request"/>

<c:import url="_page_header.html"/>

<body>

<div class="container-fluid">

<%--<div id="wb-skip">--%>
  <%--<ul id="wb-tphp">--%>
    <%--<li id="wb-skip1"><a href="#wb-cont">Skip to main content</a></li>--%>

    <%--<!-- The wb-nav element is in the page footer. -->--%>
    <%--<li id="wb-skip2"><a href="#wb-nav">Skip to secondary menu</a></li>--%>
  <%--</ul>--%>
<%--</div>--%>

<%--<div id="wb-core" class="base">--%>
<%--<div id="wb-core-in">--%>
  <%--&lt;%&ndash;<c:import url="<%= maintenanceWarningURL %>" />&ndash;%&gt;--%>
<%--<div id="wb-main" role="main">--%>
<%--<div id="wb-main-in">--%>
<%-- MainContentStart --%>

  <h1><fmt:message key="TITLE" bundle="${langBundle}"/></h1>

  <ul id="tabList" class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active">
      <a href="#queryFormTab"
         aria-controls="queryFormTab"
         role="tab"
         data-toggle="tab"><fmt:message key="CAOM_QUERY_TAB_TITLE"
                                           bundle="${langBundle}"/></a>
    </li>
    <% if (showObsCoreTab)
    { %>
    <li role="presentation">
      <a href="#obsCoreQueryFormTab"
         aria-controls="obsCoreQueryFormTab"
         role="tab"
         data-toggle="tab"><fmt:message key="OBSCORE_QUERY_TAB_TITLE"
                                                  bundle="${langBundle}"/></a>
    </li>
    <% } %>
    <li role="presentation">
      <a href="#resultTableTab" aria-controls="resultTableTab" role="tab"
         data-toggle="tab"><fmt:message key="RESULTS_TAB_TITLE"
                                             bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#errorTableTab" aria-controls="errorTableTab" role="tab"
         data-toggle="tab"><fmt:message key="ERROR_TAB_TITLE"
                                            bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#queryTab" aria-controls="queryTab" role="tab"
         data-toggle="tab"><fmt:message key="ADQL_QUERY_TAB_TITLE"
                                       bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#helpTab" aria-controls="helpTab" role="tab"
         data-toggle="tab"> <fmt:message key="HELP_TAB_TITLE"
                                       bundle="${langBundle}"/></a>
    </li>
  </ul>


    <%--todo: maxRowLimit needs to be fed in here properly--%>
  <div class="tab-content">
      <!-- CAOM2 Search Query Tab -->
      <c:import url="caom2_search.jsp?maxRowLimit=10000000"/>

      <!-- ObsCore Query Tab -->
      <c:import url="obscore_search.jsp?maxRowLimit=10000000"/>

      <%--<div role="tabpanel" class="tab-pane active" id="obsCoreQueryFormTab">--%>
          <%--<h2>ObsCore</h2>--%>
          <%--<form id="obscoreQueryForm" name="obscoreQueryForm" class="queryForm"--%>
                <%--method="post" action="${pageContext.request.contextPath}/find"--%>
                <%--enctype="multipart/form-data">--%>

              <%--<!-- Used by VOView to sort the results. -->--%>
              <%--<input type="hidden" name="sort_column" value="t_min"/>--%>
              <%--<input type="hidden" name="sort_order" value="descending"/>--%>
              <%--<input type="hidden" name="formName" value="adsform"/>--%>

              <%--<!-- Used by AdvancedSearch to pass to TAP. -->--%>
              <%--<input type="hidden" name="SelectList" class="ObsCore_selectlist" />--%>
              <%--<input type="hidden" name="MaxRecords" value="<%= maxRowLimit %>"/>--%>
              <%--<input type="hidden" name="format" value="csv"/>--%>

              <%--&lt;%&ndash;<div class="equalize margin-top-large margin-bottom-none">&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="span-3 row-start form-inline">&ndash;%&gt;--%>
              <%--&lt;%&ndash;<input type="submit"&ndash;%&gt;--%>
              <%--&lt;%&ndash;value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-rel="popup" data-dismissible="false"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-position-to="window" data-inline="true"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-transition="pop" data-corners="true" data-shadow="true"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-iconshadow="true" data-wrapperels="span" data-theme="c"&ndash;%&gt;--%>
              <%--&lt;%&ndash;aria-haspopup="true" aria-owns="queryOverlay"&ndash;%&gt;--%>
              <%--&lt;%&ndash;class="submit-obscore-query button button-accent ui-btn"/>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<input type="reset" data-role="none"&ndash;%&gt;--%>
              <%--&lt;%&ndash;value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"&ndash;%&gt;--%>
              <%--&lt;%&ndash;class="reset-obscore-query-form button ui-btn"/>&ndash;%&gt;--%>
              <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="span-5 row-end"></div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="clear"></div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;</div>&ndash;%&gt;--%>

              <%--<div class="col-sm-12 button-holder">--%>
                  <%--<button type="submit" class="btn btn-primary" value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />" >--%>
                      <%--<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />--%>
                  <%--</button>--%>
                  <%--<button type="reset" class="btn btn-default" value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />" >--%>
                      <%--<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />--%>
                  <%--</button>--%>
              <%--</div>--%>

              <%--<div class="row">--%>
                  <%--<div class="col-md-3 search-category">--%>
                      <%--<div class="panel panel-default">--%>
                          <%--<div class="panel-heading"><fmt:message--%>
                                  <%--key="OBSERVATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>--%>
                          <%--<div class="panel-body search-constraints">--%>
                              <%--<c:import--%>
                                      <%--url="text_formgroup.jsp?utype=DataID.observationID&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="text_formgroup.jsp?utype=Target.Name&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="timestamp.jsp?utype=Curation.releaseDate&tipSide=right" />--%>
                          <%--</div>--%>
                      <%--</div>--%>
                  <%--</div>--%>


                  <%--<div class="col-md-3 search-category">--%>
                      <%--<div class="panel panel-default">--%>
                          <%--<div class="panel-heading"><fmt:message--%>
                                  <%--key="SPATIAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>--%>
                          <%--<div class="panel-body search-constraints">--%>

                              <%--<c:import--%>
                                      <%--url="position.jsp?utype=Char.SpatialAxis.Coverage.Support.Area&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpatialAxis.Coverage.Bounds.Extent.diameter&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpatialAxis.Resolution.refval.value&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpatialAxis.numBins1&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpatialAxis.numBins2&tipSide=right"/>--%>
                              <%--<c:import--%>
                                      <%--url="boolean.jsp?name=Char.SpatialAxis.DOWNLOADCUTOUT&tipSide=right"/>--%>

                          <%--</div>--%>
                      <%--</div>--%>
                  <%--</div>--%>

                  <%--<div class="col-md-3 search-category">--%>
                      <%--<div class="panel panel-default">--%>
                          <%--<div class="panel-heading"><fmt:message--%>
                                  <%--key="TIME_POLARIZATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>--%>
                          <%--<div class="panel-body search-constraints">--%>

                              <%--<c:import--%>
                                      <%--url="date.jsp?utype=Char.TimeAxis.Coverage.Bounds.Limits&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.TimeAxis.Coverage.Support.Extent&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.TimeAxis.Resolution.refval.value&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.TimeAxis.numBins&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="text_formgroup.jsp?utype=Char.PolarizationAxis.stateList&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.PolarizationAxis.numBins&tipSide=left"/>--%>

                          <%--</div>--%>
                      <%--</div>--%>
                  <%--</div>--%>


                  <%--<div class="col-md-3 search-category">--%>
                      <%--<div class="panel panel-default">--%>
                          <%--<div class="panel-heading"><fmt:message--%>
                                  <%--key="SPECTRAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>--%>
                          <%--<div class="panel-body search-constraints">--%>

                              <%--<c:import--%>
                                      <%--url="energy.jsp?utype=Char.SpectralAxis.Coverage.Bounds.Limits&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpectralAxis.Resolution.ResolPower.refval&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="number.jsp?utype=Char.SpectralAxis.numBins&tipSide=left"/>--%>
                              <%--<c:import--%>
                                      <%--url="boolean.jsp?name=Char.SpectralAxis.DOWNLOADCUTOUT&tipSide=left"/>--%>

                          <%--</div>--%>
                      <%--</div>--%>
                  <%--</div>--%>

                  <%--<div class="clear"></div>--%>
              <%--</div>--%>

              <%--<c:import--%>
                      <%--url="hierarchy.jsp?utype=DataID.Collection/Provenance.ObsConfig.Facility.name/Provenance.ObsConfig.Instrument.name/ObsDataset.calibLevel/ObsDataset.dataProductType&modelDataSource=obscore"/>--%>

              <%--&lt;%&ndash;<div class="equalize margin-top-large margin-bottom-none">&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="span-3 row-start form-inline">&ndash;%&gt;--%>
              <%--&lt;%&ndash;<input type="submit"&ndash;%&gt;--%>
              <%--&lt;%&ndash;value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-rel="popup" data-dismissible="false"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-position-to="window" data-inline="true"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-transition="pop" data-corners="true" data-shadow="true"&ndash;%&gt;--%>
              <%--&lt;%&ndash;data-iconshadow="true" data-wrapperels="span" data-theme="c"&ndash;%&gt;--%>
              <%--&lt;%&ndash;aria-haspopup="true" aria-owns="queryOverlay"&ndash;%&gt;--%>
              <%--&lt;%&ndash;class="submit-obscore-query button button-accent ui-btn"/>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<input type="reset" data-role="none"&ndash;%&gt;--%>
              <%--&lt;%&ndash;value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"&ndash;%&gt;--%>
              <%--&lt;%&ndash;class="reset-obscore-query-form button ui-btn"/>&ndash;%&gt;--%>
              <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="span-5 row-end"></div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;<div class="clear"></div>&ndash;%&gt;--%>
              <%--&lt;%&ndash;</div>&ndash;%&gt;--%>


              <%--<div class="col-sm-12 button-holder">--%>
                  <%--<button type="submit" class="btn btn-primary" value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />" >--%>
                      <%--<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />--%>
                  <%--</button>--%>
                  <%--<button type="reset" class="btn btn-default" value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />" >--%>
                      <%--<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />--%>
                  <%--</button>--%>
              <%--</div>--%>

          <%--</form>--%>
      <%--</div>--%>

      <!-- Result Tab -->
      <c:import url="results.jsp?maxRowLimit=10000000"/>

  <%--<!-- Error Tab -->--%>
  <%--<div id="errorTableTab">--%>
    <%--<div class="grid-container">--%>
      <%--<div id="error-grid-header" class="grid-header">--%>
        <%--<span class="grid-header-label"></span>--%>
      <%--</div>--%>
      <%--<div id="errorTable"></div>--%>
    <%--</div>--%>
    <%--<div id="errorTooltipColumnPickerHolder">--%>
      <%--<div class="tooltip columnpicker">--%>
        <%--<h3>Add/remove displayed columns</h3>--%>
        <%--<span class="tooltipColumnPickerHelpText">--%>
          <%--Drag &amp; drop columns above or below the red bar, move the red bar--%>
          <%--itself or click on the checkboxes.--%>
        <%--</span>--%>
        <%--<br/>--%>
        <%--<br/>--%>

        <%--<h3>Reorder columns</h3>--%>
        <%--<span class="tooltipColumnPickerHelpText">--%>
          <%--Drag &amp; drop the columns or drag &amp; drop the column headers--%>
          <%--directly in the results table.--%>
        <%--</span>--%>

        <%--<div class="tooltip_content"></div>--%>
      <%--</div>--%>
    <%--</div>--%>
  <%--</div>--%>

  <%--<!-- Query Tab -->--%>
  <%--<div id="queryTab" class="wet-boew-prettify lang-sql">--%>
    <%--<div id="query_holder">--%>
      <%--<h3 class="wb-invisible">ADQL Query</h3>--%>
      <%--<pre class="prettyprint lang-sql"><code id="query" class="lang-sql"></code></pre>--%>
    <%--</div>--%>
  <%--</div>--%>

  <!-- Help Tab -->
  <%--<div id="helpTab" >--%>
    <%--<c:import url="_help.jsp"/>--%>
  <%--</div>--%>
  <%--</div>--%>
  <%--</div>--%>

  <%--<div class="wb-invisible">--%>
    <%--<div id="queryOverlay" data-role="popup">--%>
      <%--<img src="images/queryoverlay.gif" alt=""/>--%>
      <%--<br/>--%>
                <%--<span id="overlay_status">--%>
                  <%--<fmt:message key="EXECUTING_QUERY_LABEL"--%>
                               <%--bundle="${langBundle}"/></span>--%>
      <%--<br/>--%>
                <%--<span id="overlay_cancel">--%>
                  <%--<input id="cancel_search" type="button" value="Cancel"--%>
                         <%--class="button"/>--%>
                <%--</span>--%>
    <%--</div>--%>
  <%--</div>--%>
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

          <script type="text/javascript"
                  src="cadcJS/javascript/org.opencadc.js"></script>

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
            <script type="application/javascript" src="js/bootstrap.min.js"></script>

          <script type="text/javascript">
            $(document).ready(function ()
                              {
                                var searchApp =
                                    new ca.nrc.cadc.search.AdvancedSearchApp({
                                                                               "tapSyncEndpoint": "<%= tapSyncEndpoint %>",
                                                                               "pageLanguage": $("html").prop("lang"),
                                                                               "autoInitFlag": false
                                                                             });

                                searchApp.subscribe(ca.nrc.cadc.search.events.onAdvancedSearchInit,
                                                    function (event, args)
                                                    {
                                                      // TODO: Deal with situation where error is not null
                                                      args.application.start();
                                                    });

                                searchApp.init();
                              });
          </script>

  <!-- Close off the wb-body -->
</div>

<c:import url="_page_footer_js.html"/>

</body>
</html>
