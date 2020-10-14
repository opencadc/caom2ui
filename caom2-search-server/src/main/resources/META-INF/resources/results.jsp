<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>


<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request"
             class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
//    final String maxRowLimit = request.getParameter("maxRowLimit");
    final String downloadLink = "/downloadManager/download";
%>

<div role="tabpanel" class="tab-pane" id="resultTableTab">

    <span class="votable_link_label">
        <fmt:message key="FULL_VOTABLE_LINK_LABEL" bundle="${langBundle}"/>
    </span>
    <a href="#" class="votable_link_votable link_idle">VOTable</a>
    <a href="#" class="votable_link_csv link_idle">CSV</a>
    <a href="#" class="votable_link_tsv link_idle">TSV</a>

    <span class="result-state">
        <a href="#" id="results_bookmark" class="result-state-link link_idle">
            <fmt:message key="RESULT_STATE_LINK_LABEL" bundle="${langBundle}"/>
        </a>
    </span>

    <!--  Modal to contain the bookmark link. -->
    <div class="modal fade" id="bookmark_link" role="dialog">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <span id="bookmark_overlay_status">
                      <h4><fmt:message key="RESULT_STATE_LINK_LABEL" bundle="${langBundle}"/></h4>
                    </span>
                </div>
                <div class="modal-body">
                    <div id="bookmark_url_display" class="results-url-modal"></div>
                </div>
            </div>
        </div>
    </div>

    <!--  Modal to contain the column manager. -->
    <div class="modal fade" id="column_manager" role="dialog">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" id="column_manager_close">&times;</button>
                    <span id="overlay_status">
                        <h4><fmt:message key="COLUMN_MANAGER_HEADING_TEXT" bundle="${langBundle}"/></h4>
                    </span>
                </div>
                <div class="modal-body">
                    <span>
                        <fmt:message key="COLUMN_MANAGER_HELP_TEXT" bundle="${langBundle}" />
                    </span>

                    <div id="column_manager_container">
                        <div class="column_manager_columns"></div>

                        <!-- Text used to populate buttons -->
                        <span id="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" class="hidden i18n">
                            <fmt:message key="COLUMN_MANAGER_SHOW_ALL_BUTTON_TEXT" bundle="${langBundle}" />
                        </span>
                        <span id="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" class="hidden i18n">
                            <fmt:message key="COLUMN_MANAGER_DEFAULT_COLUMNS_BUTTON_TEXT" bundle="${langBundle}" />
                        </span>
                        <span id="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" class="hidden i18n">
                            <fmt:message key="COLUMN_MANAGER_ORDER_ALPHABETICALLY_BUTTON_TEXT" bundle="${langBundle}" />
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <%--&lt;%&ndash; Aladin Lite container. &ndash;%&gt;--%>
    <div id="aladin-lite" style="height: 250px;width: 1180px;"></div>

    <div class="grid-container">
        <div id="cadcvotv-empty-results-message" class="cadcvotv-empty-results-message">
            <strong><fmt:message key="NO_RESULTS_RETURNED" bundle="${langBundle}" /></strong>
        </div>
        <div id="results-grid-header" class="grid-header">
            <form id="downloadForm" name="downloadForm" class="form-horizontal"
                action="<%= downloadLink %>" method="POST" target="DOWNLOAD">
                <input type="hidden" name="runid" id="runid" value=""/>
                <span id="NO_OBSERVATIONS_SELECTED_MESSAGE" class="hidden"><fmt:message key="NO_OBSERVATIONS_SELECTED_MESSAGE" bundle="${langBundle}"/></span>
                <span class="grid-header-icon-span">
                    <img class="margin-bottom-none margin-left-none margin-right-none align-middle grid-header-icon" src="cadcVOTV/images/transparent-20.png"/>
                </span>
                <span>
                    <button type="submit"
                            id="downloadFormSubmit"
                            form="downloadForm"
                            class="btn btn-sm btn-primary">
                        <fmt:message key="DOWNLOAD_BUTTON_LABEL" bundle="${langBundle}" />
                    </button>
                </span>
                <span class="grid-header-label"></span>

                <button id="change_column_button"
                        type="button"
                        class="btn btn-sm btn-default"
                data-toggle="modal"
                data-target="#column_manager">
                            <fmt:message key="COLUMN_MANAGER_BUTTON_LABEL" bundle="${langBundle}" />
                </button>

                <!-- The Visualize button to enable AladinLite. -->
                <button id="slick-visualize"
                        class="btn btn-sm btn-default"
                        type="button"
                        data-open="<fmt:message key="RESULTS_VISUALIZE_BUTTON_LABEL" bundle="${langBundle}"/>"
                        data-close="<fmt:message key="CLOSE_BUTTON_LABEL" bundle="${langBundle}" />">
                    <fmt:message key="RESULTS_VISUALIZE_BUTTON_LABEL" bundle="${langBundle}" />
                </button>
            </form>


        </div>
        <div id="resultTable"></div>
    </div>
</div>
