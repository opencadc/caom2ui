<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>


<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request" class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div class="maintable hierarchy">
  <div class="hierarchy module background-light">
    <h2 class="background-accent"><fmt:message key="DATA_ACQUISITION_LABEL" bundle="${langBundle}"/></h2>

    <div id="${param.modelDataSource}@Hierarchy"
         class="width-100 align-center margin-bottom-none advanced_search_data_train modelDataSource_${param.modelDataSource}"
         data-utypes="${param.utype}">
      <span class="wb-invisible hierarchy_utype">${param.utype}</span>

      <div class="hidden" id="${param.utype}.building">
        <span class="wb-icon-busy"></span>
        <fmt:message key="LOADING_MESSAGE" bundle="${langBundle}"/>
      </div>
    </div>
    <div class="clear"></div>
  </div>
</div>

