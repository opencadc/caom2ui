<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>


<c:set var="labelKey" value="${param.utype}_FORM_LABEL" />

<%--<li class="label_tooltip_${param.tipSide}">--%>
  <%--<details id="${param.utype}_details">--%>
    <%--<summary class="search_criteria_label_container">--%>
      <%--<span class="search_criteria_label ${param.utype}_details"><fmt:message key="${labelKey}" bundle="${langBundle}"/></span>--%>
      <%--<span class="search_criteria_label_contents color-accent"></span>--%>
    <%--</summary>--%>
    <label for="${param.utype}" class="">
      <fmt:message key="${labelKey}" bundle="${langBundle}"/></label>
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
  <%--</details>--%>
<%--</li>--%>
