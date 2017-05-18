<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final String utype = request.getParameter("utype");
  final String labelKey = utype + "_FORM_LABEL";
  final String tipSide = request.getParameter("tipSide");
  final String formName = utype + ca.nrc.cadc.search.form.Number.NAME;
  final String detailsID = utype + "_details";
%>

<div data-toggle="tooltip" data-placement="<%= tipSide %>" title="<%= utype %>">
  <div class="form-group">
    <%--<details id="<%= detailsID %>">--%>
      <%--<summary class="search_criteria_label_container">--%>
        <%--<span class="search_criteria_label <%= detailsID %>"><fmt:message key="<%= labelKey %>" bundle="${langBundle}"/></span>--%>
        <%--<span class="search_criteria_label_contents color-accent"></span>--%>
      <%--</summary>--%>
      <label for="<%= utype %>" class="control-label">
        <fmt:message key="<%= labelKey %>" bundle="${langBundle}"/>
      </label>
      <div class="">
        <div id="<%= utype %>_input_decorate">
          <input id="<%= utype %>"
                 name="<%= utype %>" value="" size="15"
                 type="text"
                 class="form-control search_criteria_input width-100 ui-form-input-validate ui_unitconversion_input" />
        </div>
      </div>
      <input type="hidden"
             name="<%= FormConstraint.FORM_NAME %>"
             value="<%= formName %>"/>
    <%--</details>--%>
  </div>
</div>
