<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final String formName = request.getParameter("utype") + ca.nrc.cadc.search.form.Number.NAME;
%>

<div id="${param.utype}_formgroup" class="form-group">
  <div data-utype="${param.utype}"
       data-toggle="popover"
       data-placement="${param.tipSide}"
       data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
       style="float:right;"
       class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue">
  </div>

  <details id="${param.utype}_details">
    <summary class="search_criteria_label_container">
      <label for="${param.utype}"
             class="control-label"><fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/><span
              class="search_criteria_label_contents color-accent"></span>
      </label>

    </summary>

      <div id="${param.utype}_input_decorate">
        <input id="${param.utype}"
               name="${param.utype}" value="" size="15"
               type="text"
               class="form-control search_criteria_input width-100 ui-form-input-validate ui_unitconversion_input" />
      </div>
      <input type="hidden"
             name="<%= FormConstraint.FORM_NAME %>"
             value="<%= formName %>"/>

  </details>
</div>

