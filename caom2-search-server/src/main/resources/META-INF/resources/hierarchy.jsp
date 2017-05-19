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
  final String uType = request.getParameter("utype");
  final Hierarchy hierarchy = new Hierarchy(job, uType);

  for (final Enumerated enumerated : hierarchy.getEnumerated())
  {
%>
<input type="hidden"
       name="<%= FormConstraint.FORM_NAME %>"
       value="<%= enumerated.getUType() + Enumerated.NAME %>"/>
<input type="hidden"
       id="<%= enumerated.getUType() + ".json" %>"
       value="<c:out value="<%= enumerated.toJSONString() %>" />"/>
<%
  }
%>

<div class="col-sm-12 maintable hierarchy">
  <div class="panel panel-default">
    <div class="panel-heading">
      <fmt:message key="DATA_ACQUISITION_LABEL" bundle="${langBundle}"/></div>
        <div class="panel-body">
            <div id="${param.modelDataSource}<%= Hierarchy.NAME %>"
                            class="width-100 align-center margin-bottom-none advanced_search_data_train modelDataSource_${param.modelDataSource}"
                            data-utypes="<%= uType %>">
                <span class="invisible hierarchy_utype"><%= uType %></span>

                <div class="hidden" id="<%= uType + ".building" %>">
                    <span class="wb-icon-busy"></span>
                    <fmt:message key="LOADING_MESSAGE" bundle="${langBundle}"/>
                </div>
            </div>
        </div>
    <div class="clear"></div>
  </div>
</div>

