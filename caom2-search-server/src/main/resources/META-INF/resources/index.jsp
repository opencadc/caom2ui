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
