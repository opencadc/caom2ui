<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request" class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<div class="col-sm-12">
  <div class="panel panel-default">
    <div class="panel-heading">
      <fmt:message key="DATA_ACQUISITION_LABEL" bundle="${langBundle}"/></div>
        <div class="panel-body">
            <div id="${param.modelDataSource}@Hierarchy"
                            class="width-100 text-align advanced_search_data_train modelDataSource_${param.modelDataSource} ${param.colcount}"
                            data-utypes="${param.utype}">
                <div class="invisible hierarchy_utype">${param.utype}</div>

                <div class="hidden" id="${param.utype}.building">
                    <span class="wb-icon-busy"></span>
                    <fmt:message key="LOADING_MESSAGE" bundle="${langBundle}"/>
                </div>
            </div>
        </div>
    <div class="clear"></div>
  </div>
</div>

