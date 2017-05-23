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
  final String detailsID = utype + "_details";
%>

<%--<li class="label_tooltip_<%= tipSide %>">--%>
  <%--<details id="<%= detailsID %>">--%>
    <%--<summary class="search_criteria_label_container">--%>
      <%--<span class="search_criteria_label <%= detailsID %>"><fmt:message key="<%= labelKey %>" bundle="${langBundle}"/></span>--%>
      <%--<span class="search_criteria_label_contents color-accent"></span>--%>
    <%--</summary>--%>
<div data-toggle="tooltip" data-placement="${param.tipSide}" title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>">
    <div class="form-group">
        <label for="${param.utype}" class="control-label">
          <fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>
        </label>

        <div id="${param.utype}_input_decorate">
          <input id="${param.utype}"
                 name="<%= name %>" value="" size="20"
                 type="text"
                 class="form-control search_criteria_input width-100 ui-form-input-validate ui_unitconversion_input"/>
        </div>
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>"
               value="<%= formName %>"/>
    </div>
</div>
  <%--</details>--%>
<%--</li>--%>
