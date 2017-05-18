<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.Enumerated" %>
<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>
<%@ page import="ca.nrc.cadc.search.form.Hierarchy" %>

<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request"
             class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    final String maxRowLimit = request.getParameter("maxRowLimit");
    final String downloadLink = "/downloadManager/download";
%>

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
<div class="invisible">
<span id="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" class="invisible i18n"><fmt:message key="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" bundle="${langBundle}" /></span>
<span id="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" class="invisible i18n"><fmt:message key="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" bundle="${langBundle}" /></span>
<span id="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" class="invisible i18n"><fmt:message key="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" bundle="${langBundle}" /></span>
<div id="column_manager_container" data-role="popup" data-theme="b"
class="column_manager_container ui-content">
<span class="wb-icon-x-alt2 float-right dialog-close"></span>
<h3><fmt:message key="COLUMN_MANAGER_HEADING_TEXT" bundle="${langBundle}" /></h3>
<span class="tooltipColumnPickerHelpText">
<fmt:message key="COLUMN_MANAGER_HELP_TEXT" bundle="${langBundle}" /></span>
<div class="column_manager_columns"></div>
</div>
</div>

<%--&lt;%&ndash; Aladin Lite container. &ndash;%&gt;--%>
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
<span id="NO_OBSERVATIONS_SELECTED_MESSAGE" class="invisible">
<fmt:message key="NO_OBSERVATIONS_SELECTED_MESSAGE" bundle="${langBundle}"/>
</span>
<span class="grid-header-icon-span">
<img class="margin-bottom-none margin-left-none margin-right-none align-middle grid-header-icon" src="cadcVOTV/images/transparent-20.png"/>
</span>
<span>
<button type="submit" id="downloadFormSubmit" form="downloadForm" class="btn btn-primary">
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
role="button" 
class="btn slick-columnpicker-panel-change-column-label ui-link"><fmt:message key="COLUMN_MANAGER_BUTTON_LABEL" bundle="${langBundle}" /></a>
</span>

<!-- The Visualize button to enable AladinLite. -->
<span class='slick-visualize-holder'>
<a href="#" id="slick-visualize" name='slick-visualize' tole="button" 
class="btn slick-visualize-label ui-link"
data-open="<fmt:message key="RESULTS_VISUALIZE_BUTTON_LABEL" bundle="${langBundle}"/>"
data-close="<fmt:message key="CLOSE_BUTTON_LABEL" bundle="${langBundle}" />"><fmt:message key="RESULTS_VISUALIZE_BUTTON_LABEL" bundle="${langBundle}" /></a>
</span>
</form>
</div>
<div id="resultTable"></div>
<div id="results-grid-footer" class="grid-footer">
<span class="grid-footer-label"></span>
</div>
</div>
</div>
