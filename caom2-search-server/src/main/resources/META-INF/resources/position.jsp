<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>
<%@ page import="ca.nrc.cadc.search.form.Shape1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<%
  final String utype = request.getParameter("utype");
  final String name = utype + Shape1.VALUE;
  final String formName = utype + Shape1.NAME;
  final String resolverName = utype + Shape1.RESOLVER_VALUE;
%>

<div id="${param.utype}_formgroup"class="form-group">
    <div data-toggle="popover"
         data-utype="${param.utype}"
         data-placement="${param.tipSide}"
         data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
         class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue popover-right">
    </div>

    <details id="${param.utype}_details">
        <summary class="search_criteria_label_container">
            <label for="${param.utype}"
                   class="control-label"><fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/><span
                        class="search_criteria_label_contents color-accent"></span></label>
        </summary>
        <div class="form-group">
            <div>
                <label for="<%= resolverName %>"
                       class="sub-label"><fmt:message key="RESOLVER_FORM_LABEL" bundle="${langBundle}" /><span
                            class="search_criteria_label_contents color-accent"></span>
                    <span class="italic margin-left-small"><fmt:message key="RESOLVER_FORM_LABEL_ADDENDUM" bundle="${langBundle}" /></span>
                </label>
            </div>

            <select size="1" name="<%= resolverName %>" title="<fmt:message key="RESOLVER_FORM_LABEL" bundle="${langBundle}" />"
                    id="<%= resolverName %>" class="resolver-select form-control">
              <option value="ALL">
                <fmt:message key="ANY_RESOLVER_FORM_LABEL" bundle="${langBundle}" />
              </option>
              <option value="SIMBAD">SIMBAD</option>
              <option value="NED">NED</option>
              <option value="VIZIER">VIZIER</option>
              <option value="NONE">
                  <fmt:message key="NO_RESOLVER_FORM_LABEL" bundle="${langBundle}" />
            </option>
          </select>
        </div>

        <div class="input-group">
          <input id="${param.utype}"
                 type="text"
                 class="form-control search_criteria_input"
                 size="28"
                 name="<%= name %>" />
          <span id="${param.utype}_target_name_resolution_status"
                class="input-group-addon target_name_resolution_status"></span>
        </div>

        <div class="text-center">
            <span><strong>- <fmt:message key="OR_LABEL" bundle="${langBundle}"/> -</strong></span>
        </div>

        <div class="form-group">
            <div id="${param.utype}_targetList_fileInputDiv" class="">
                <label for="${param.utype}_targetList"
                       class="disabled" disabled><fmt:message key="TARGET_LIST_FORM_LABEL" bundle="${langBundle}" /><span
                            class="search_criteria_label_contents color-accent"></span></label>
                <div>
                  <input type="file" class="form-control file-form-control target-list"
                         id="${param.utype}_targetList" name="targetList"
                         title="<fmt:message key="TARGET_LIST_TOOLTIP" bundle="${langBundle}" />" />
                </div>
            </div>
        </div>

          <%--Link to SSOIS search for Mobile Objects--%>
          <div class="text-center">
            <span><strong>- <fmt:message key="OR_LABEL" bundle="${langBundle}"/> -</strong></span>
          </div>

          <div class="form-group">
            <a id="ssois_link" href="http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/en/ssois" target="ssois_window">
              <fmt:message key="SSOIS_MOBILE_OBJECTS" bundle="${langBundle}"/></a>
          </div>

        <input type="hidden" name="Form.name"
               value="targetList.targetList" />
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>"
               value="<%= formName %>" />

    </details>
</div>
