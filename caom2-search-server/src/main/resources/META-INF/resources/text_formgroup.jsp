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
    String classes = "search_criteria_input width-100";
//  if (utype.startsWith("Observation.proposal"))
    if ((enableAutocomplete != null) && Boolean.parseBoolean(enableAutocomplete))
    {
        classes += " ui-autocomplete-input";
    }
%>

<div class="form-group">
    <label for="${param.utype}" class="control-label">
        <fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>
    </label>
    <div data-toggle="popover"
         data-placement="${param.tipSide}"
         data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
         class="glyphicon glyphicon-question-sign popover_blue">
    </div>
    <div>
        <input type="text"
               class="form-control search_criteria_input"
               id="${param.utype}"
               name="${param.utype}">
    </div>
    <input type="hidden"
           name="<%= FormConstraint.FORM_NAME %>"
           value="<%= formName %>"/>
</div>
