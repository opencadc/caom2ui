<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>
<%@ page import="ca.nrc.cadc.search.form.Shape1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<%
  final String utype = request.getParameter("utype");
  final String labelKey = utype + "_FORM_LABEL";
  final String tipSide = request.getParameter("tipSide");
  final String name = utype + Shape1.VALUE;
  final String formName = utype + Shape1.NAME;
  final String resolverName = utype + Shape1.RESOLVER_VALUE;
  final String detailsID = utype + "_details";
%>

<li class="label_tooltip_<%= tipSide %>">
  <details id="<%= detailsID %>">
    <summary class="search_criteria_label_container">
      <span class="search_criteria_label <%= detailsID %>"><fmt:message key="<%= labelKey %>" bundle="${langBundle}"/></span>
      <span class="search_criteria_label_contents color-accent"></span>
    </summary>
    <label for="<%= utype %>" class="wb-invisible">
      <fmt:message key="<%= labelKey %>" bundle="${langBundle}" />
    </label>
    <div class="resolve_panel margin-top-medium">
      <div class="form-inline">
        <label for="<%= resolverName %>" class="form-label-inline">
          <fmt:message key="RESOLVER_FORM_LABEL" bundle="${langBundle}" /><span class="italic margin-left-small"><fmt:message key="RESOLVER_FORM_LABEL_ADDENDUM" bundle="${langBundle}" /></span></label>
        <select size="1" name="<%= resolverName %>" title="<fmt:message key="RESOLVER_FORM_LABEL" bundle="${langBundle}" />"
                id="<%= resolverName %>" class="resolver_select">
          <option value="ALL">
            <fmt:message key="ANY_RESOLVER_FORM_LABEL" bundle="${langBundle}" /></option>
          <option value="SIMBAD">SIMBAD</option>
          <option value="NED">NED</option>
          <option value="VIZIER">VIZIER</option>
          <option value="NONE"><fmt:message key="NO_RESOLVER_FORM_LABEL" bundle="${langBundle}" /></option>
        </select>
      </div>
    </div>
    <div class="form-input-append">
      <input type="text" class="search_criteria_input"
             size="28" id="<%= utype %>"
             name="<%= name %>" />
      <span id="<%= utype %>_target_name_resolution_status"
            class="form-addon target_name_resolution_status"></span>
    </div>
    <div class="align-center margin-top-none margin-bottom-medium">
      <span class="font-large">- <fmt:message key="OR_LABEL" bundle="${langBundle}"/> -</span>
    </div>
    <div id="<%= utype %>_targetList_fileInputDiv">
      <label for="<%= utype %>_targetList" class="wb-invisible">
        <fmt:message key="TARGET_LIST_FORM_LABEL" bundle="${langBundle}" /></label>
      <input type="file" class="search_criteria_input"
             id="<%= utype %>_targetList" name="targetList"
             title="<fmt:message key="TARGET_LIST_TOOLTIP" bundle="${langBundle}" />" />
      <span id="<%= utype %>_targetList_clear" class="wb-icon-eraser targetList_clear"></span>
    </div>

    <%--Link to SSOIS search for Mobile Objects--%>
    <div class="align-center margin-top-none margin-bottom-medium">
      <span class="font-large">- <fmt:message key="OR_LABEL" bundle="${langBundle}"/> -</span>
    </div>
    <div class="align-left margin-top-none margin-bottom-medium">
      <a href="http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/en/ssois" target="_blank" class=""><fmt:message key="SSOIS_MOBILE_OBJECTS" bundle="${langBundle}"/></a>
    </div>

    <input type="hidden" name="Form.name"
           value="targetList.targetList" />
    <input type="hidden"
           name="<%= FormConstraint.FORM_NAME %>"
           value="<%= formName %>" />
  </details>
</li>
