<%@ page import="ca.nrc.cadc.web.Configuration" %>
<%@ page import="ca.nrc.cadc.config.ApplicationConfiguration" %>
<%@ page import="ca.nrc.cadc.util.StringUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final ApplicationConfiguration configuration = new ApplicationConfiguration(Configuration.DEFAULT_CONFIG_FILE_PATH);

  // Conservative default.
  final int defaultMaxRowLimit = 10000;
  final String contentLanguage = request.getHeader("Content-Language");
  final String requestHeaderLang = (contentLanguage == null) ? "en" : contentLanguage;

  final int maxRowLimit = configuration.lookupInt("org.opencadc.search.max-row-count", defaultMaxRowLimit);
  final boolean showObsCoreTab = configuration.lookupBoolean("org.opencadc.search.obs-core", true);
  final String applicationEndpoint = configuration.lookup("org.opencadc.search.app-service-endpoint", "/search");
  final String tapServiceId = configuration.lookup("org.opencadc.search.tap-service-id");
%>

<%-- Request scope variables so they can be seen in the imported JSPs --%>
<fmt:setLocale value="<%= requestHeaderLang %>" scope="request"/>
<fmt:setBundle basename="Caom2SearchBundle" var="langBundle" scope="request"/>

<c:url value="_page_header.jsp" var="pageHeaderURL">
  <c:param name="lang" value="${requestHeaderLang}" />
</c:url>

<c:import url='${pageHeaderURL' />


<body>
<div class="container-fluid">

  <%-- MainContentStart --%>
  <ul id="tabList" class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active">
      <a href="#queryFormTab"
         id="queryFormTabLink"
         aria-controls="queryFormTab"
         role="tab"
         data-toggle="tab"><fmt:message key="CAOM_QUERY_TAB_TITLE"
                                           bundle="${langBundle}"/></a>
    </li>
    <% if (showObsCoreTab) { %>
    <li role="presentation">
      <a href="#obsCoreQueryFormTab"
         id="obsCoreQueryFormTabLink"
         aria-controls="obsCoreQueryFormTab"
         role="tab"
         data-toggle="tab"><fmt:message key="OBSCORE_QUERY_TAB_TITLE"
                                                  bundle="${langBundle}"/></a>
    </li>
    <% } %>
    <li role="presentation">
      <a href="#resultTableTab"
         id="resultTableTabLink"
         aria-controls="resultTableTab"
         role="tab"
         data-toggle="tab"><fmt:message key="RESULTS_TAB_TITLE"
                                             bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#errorTableTab"
         id="errorTableTabLink"
         aria-controls="errorTableTab"
         role="tab"
         data-toggle="tab"><fmt:message key="ERROR_TAB_TITLE"
                                            bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#queryTab"
         id="queryTabLink"
         aria-controls="queryTab"
         role="tab"
         data-toggle="tab"><fmt:message key="ADQL_QUERY_TAB_TITLE"
                                       bundle="${langBundle}"/></a>
    </li>
    <li role="presentation">
      <a href="#helpTab"
         id="helpTabLink"
         aria-controls="helpTab"
         role="tab"
         data-toggle="tab"> <fmt:message key="HELP_TAB_TITLE"
                                       bundle="${langBundle}"/></a>
    </li>
  </ul>


    <%--todo: maxRowLimit needs to be fed in here properly--%>

  <div class="tab-content">
    <!-- CAOM2 Search Query Tab -->
    <c:import url='<%= "caom2_search.jsp?maxRowLimit=" + maxRowLimit %>' />

    <!-- ObsCore Query Tab -->
    <c:import url='<%= "obscore_search.jsp?maxRowLimit=" + maxRowLimit %>' />

    <!-- Result Tab -->
    <c:import url='<%= "results.jsp?maxRowLimit=" + maxRowLimit %>' />

    <!-- Error Tab -->
    <div role="tabpanel" class="tab-pane" id="errorTableTab">
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
    <div id="queryTab" class="lang-sql tab-pane" role="tabpanel">
      <div id="query_holder">
        <h3 class="wb-invisible">ADQL Query</h3>
        <pre class="prettyprint lang-sql"><code id="query" class="lang-sql"></code></pre>
      </div>
    </div>

    <!-- Help Tab -->
    <div role="tabpanel" class="tab-pane" id="helpTab" >
      <c:import url="_help.jsp"/>
    </div>
  </div>
  <%--</div>--%>


  <div class="modal fade" id="queryOverlay" role="dialog">
      <div class="modal-dialog modal-sm">
          <div class="modal-content">
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal">&times;</button>
                  <span id="overlay_status">
                        <h4><fmt:message key="EXECUTING_QUERY_LABEL" bundle="${langBundle}"/></h4>
                  </span>
              </div>
              <div class="modal-body">
                  <img src="images/queryoverlay.gif" class="query-overlay-loading" alt=""/>
              </div>
              <div class="modal-footer">
                  <button id="cancel_search" type="button" class="btn btn-default btn-sm" data-dismiss="modal"><fmt:message key="CANCEL_BUTTON_LABEL"
                                                                                                                            bundle="${langBundle}"/></button>
              </div>
          </div>
      </div>
  </div>

  <div class="hidden" id="preloadthumbnails"></div>

  <%-- To find pixel lengths of strings. --%>
  <%-- To find pixel lengths of strings. --%>
  <div id="lengthFinder"></div>
</div>

<script type="text/javascript">
  $(function ()
    {
      // Load in the CADC VOTV related CSS
      $("head")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/aladin.min.css\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/jquery-ui-1.11.4.min.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/tooltipster.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/caom2_search.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick.grid.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick.pager.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/cadc.columnpicker.dialog.css?version=@version@\"/>")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/cadc.votv.css?version=@version@\" />")
          .append("<link rel=\"stylesheet\" type=\"text/css\" href=\"cadcVOTV/css/slick-default-theme.css?version=@version@\" />");
    });
</script>
<script type="application/javascript" src="js/bootstrap.min.js"></script>
<script type="application/javascript" src="js/bootstrap-toggle.min.js"></script>
<script type="text/javascript"
        src="cadcVOTV/javascript/jquery.event.drag-2.2.min.js?version=@version@"></script>
<script type="text/javascript"
        src="js/jquery.form.js?version=@version@"></script>
<script type="text/javascript" charset="utf-8"
        src="cadcVOTV/javascript/aladin.js?version=@version@"></script>
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
        src="cadcVOTV/javascript/slick.grid.js?version=@version@"></script>
<script type="text/javascript"
        src="cadcVOTV/javascript/slick.dataview.js?version=@version@"></script>
<script type="text/javascript"
        src="cadcVOTV/javascript/slick.pager.js?version=@version@"></script>
<script type="text/javascript"
        src="cadcVOTV/javascript/cadc.columnpicker.modal.js?version=@version@"></script>
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
        src="js/cadc.search.tapclient.js?version=@version@"></script>
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
<script type="text/javascript"
        src="cadcJS/javascript/registry-client.js"></script>


<script type="text/javascript">
  $(document).ready(function ()
    {
      var searchApp = new ca.nrc.cadc.search.AdvancedSearchApp({
        autocompleteEndpoint: '<%= applicationEndpoint %>/unitconversion/',
        targetResolverEndpoint: '<%= applicationEndpoint %>/unitconversion/',
        tapServiceId: '<%= tapServiceId %>',
        packageEndpoint: '<%= applicationEndpoint %>/package',
        validatorEndpoint: '<%= applicationEndpoint %>/validate',
        previewsEndpoint: '<%= applicationEndpoint %>/preview',
        searchEndpoint: '<%= applicationEndpoint %>/find',
        applicationEndpoint: '<%= applicationEndpoint %>',
        pageLanguage: $('html').prop('lang'),
        autoInitFlag: false,
        showObscoreTab: <%= showObsCoreTab %>
      })

      searchApp.subscribe(ca.nrc.cadc.search.events.onAdvancedSearchInit,
        function (event, args) {
          args.application.start();
        })

      searchApp.subscribe(ca.nrc.cadc.search.events.onAdvancedSearchInitFail,
        function(event, args) {
          // quote usage inverted here because french version of string has apostrophe.
          alert("<fmt:message key='ERROR_SEARCH_NOT_AVAILABLE' bundle='${langBundle}'/>")
        })

      searchApp.init();

    });

</script>

<!-- Close off the wb-body -->
</body>
</html>
