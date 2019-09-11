<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request" class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div class="col-sm-12">
  <div class="panel panel-default">
    <div class="panel-heading">
      <fmt:message key="DATA_ACQUISITION_LABEL" bundle="${langBundle}"/></div>
    <div class="panel-body">
      <div id="${param.modelDataSource}_data_train"
           class="width-100 text-align advanced_search_data_train modelDataSource_${param.modelDataSource} ${param.colcount}"
           data-utypes="${param.utyifpe}">
        <div class="hidden hierarchy_utype">${param.utype}</div>
        <div id="${param.utype}.building" class="loading hidden">
            <span class="spinner-span glyphicon glyphicon-refresh fast-right-spinner"></span>
            <span class="info-span"><fmt:message key="LOADING_MESSAGE" bundle="${langBundle}"/></span>
        </div>
        <div class="reloadHierarchy hidden">
          <div class="dt-unavailable">
            <fmt:message key="DATATRAIN_UNAVAILABLE" bundle="${langBundle}"/>
            <fmt:message key="USE_UPPER_SEARCH_FORM" bundle="${langBundle}"/>
            <button type="button" class="btn btn-default btn-sm dt-unavailable reloadHierarchySubmit"><fmt:message key="RELOAD_CONSTRAINTS" bundle="${langBundle}"/></button>
          </div>
        </div>
        <div class="${param.modelDataSource}_dtTableDiv"></div>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</div>


