<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>

<%
    final String utype = request.getParameter("utype");
    final String labelKey = utype + "_FORM_LABEL";
    final String tipSide = request.getParameter("tipSide");
%>

<c:set var="labelKey" value="${param.utype}_FORM_LABEL" />

<div data-toggle="tooltip" data-placement="<%= tipSide %>"
     title="<fmt:message key="<%= labelKey %>" bundle="${langBundle}"/>">
<div class="form-group">
    <label for="${param.utype}" class="control-label">
      <fmt:message key="${labelKey}" bundle="${langBundle}"/></label>
    <div id="${param.utype}_tooltip"
         data-toggle="popover"
         data-placement="${param.tipSide}"
         data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
         class="glyphicon glyphicon-question-sign popover-blue popover-right">
    </div>
    <div class="data_release_date_panel">
      <cadc:checkbox checkboxName="${param.utype}@PublicTimestampFormConstraint.value"
                     i18nKey="PUBLIC_DATA_FLAG_FORM_LABEL"
                     disableTo="${param.utype}" />

      <div id="${param.utype}_input_decorate">
        <input id="${param.utype}" name="${param.utype}" value="" size="20"
               type="text"
               class="form-control search_criteria_input width-100 ui-form-input-validate ui_unitconversion_input" />
      </div>
    </div>

    <cadc:formName formName="${param.utype}@TimestampFormConstraint" />
    <cadc:formName formName="${param.utype}@PublicTimestampFormConstraint" />
</div>
</div>
