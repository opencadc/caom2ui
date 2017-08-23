<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>
<%@ page import="ca.nrc.cadc.search.form.Text" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final String utype = request.getParameter("utype");
  final String enableAutocomplete = request.getParameter("enableAutocomplete");
  final String formName = utype + Text.NAME;
  String classes = "form-control search_criteria_input";

  if ((enableAutocomplete != null) && Boolean.parseBoolean(enableAutocomplete))
  {
    classes += " ui-autocomplete-input";
  }
%>

<div id="${param.utype}_formgroup" class="form-group">
  <div data-toggle="popover"
       data-utype="${param.utype}"
       data-placement="${param.tipSide}"
       data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
       class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue popover-right">
  </div>
  <details id="${param.utype}_details">
    <summary class="search_criteria_label_container">
      <label for="${param.utype}"
             class="control-label search_criteria_label"><fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/><span
              class="search_criteria_label_contents color-accent"></span></label>
    </summary>

    <div id="${param.utype}_input_decorate">
      <input type="text" class="<%= classes %>" id="${param.utype}" name="${param.utype}">
    </div>

    <input type="hidden" name="<%= FormConstraint.FORM_NAME %>" value="<%= formName %>"/>
  </details>
</div>
