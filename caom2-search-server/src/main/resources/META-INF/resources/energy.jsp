<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.Energy" %>
<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final String utype = request.getParameter("utype");
  final String name = utype + Energy.VALUE;
  final String formName = utype + Energy.NAME;

%>

<div class="form-group">
    <%--<label for="${param.utype}" class="control-label">--%>
      <%--<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>--%>
    <%--</label>--%>
    <div data-toggle="popover"
         data-utype="${param.utype}"
         data-placement="${param.tipSide}"
         data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
         class="glyphicon glyphicon-question-sign popover-blue popover-right">
    </div>

    <details id="${param.utype}_details">
        <summary class="search_criteria_label_container">
            <label for="${param.utype}" class="control-label">
                <fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>
            </label>
            <span class="search_criteria_label_contents color-accent"></span>
        </summary>
        <div id="${param.utype}_input_decorate">
          <input id="${param.utype}"
                 name="<%= name %>" value="" size="20"
                 type="text"
                 class="form-control search_criteria_input width-100 ui-form-input-validate ui_unitconversion_input"/>
        </div>
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>"
               value="<%= formName %>"/>
    </details>
</div>

