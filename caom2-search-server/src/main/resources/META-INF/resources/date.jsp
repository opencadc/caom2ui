<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.Date" %>
<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>
<%@ page import="ca.nrc.cadc.search.form.DatePreset" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
  final String utype = request.getParameter("utype");
  final String name = utype + Date.VALUE;
  final String presetName = utype + "_PRESET" + Date.VALUE;
  final String formName = utype + Date.NAME;

  // For the select list.  These MUST be put into variables as they cannot be
  // built in the fmt:message because who the heck knows.
  // jenkinsd 2015.01.05
  final String presetLabelkey = utype + "_PRESET_FORM_LABEL";
  final String presetPast24HoursLabelKey =
      utype + "_PRESET_PAST_24_HOURS_FORM_LABEL";
  final String presetPastWeekLabelKey =
      utype + "_PRESET_PAST_WEEK_FORM_LABEL";
  final String presetPastMonthLabelKey =
      utype + "_PRESET_PAST_MONTH_FORM_LABEL";
%>

<div id="${param.utype}_formgroup" class="form-group">
  <div id="${param.utype}_tooltip"
       data-toggle="popover"
       data-placement="${param.tipSide}"
       data-utype="${param.utype}"
       data-title="<fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}"/>"
       class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue popover-right">
  </div>

  <details id="${param.utype}_details">
    <summary class="search_criteria_label_container">
      <label for="${param.utype}" class="control-label"><fmt:message key="${param.utype}_FORM_LABEL"
                     bundle="${langBundle}"/><span
              class="search_criteria_label_contents color-accent"></span></label>
    </summary>

    <div id="${param.utype}_input_decorate">
      <input id="${param.utype}"
             name="<%= name %>" value="" size="20"
             type="text" data-assoc-field="${param.utype}_PRESET"
             class="form-control search_criteria_input width-100
                    ui-form-input-validate
                    ui_unitconversion_input"/>
    </div>

    <div class="text-center">
      <span><strong>- <fmt:message key="OR_LABEL" bundle="${langBundle}"/> -</strong></span>
    </div>

    <div class="form-group">
      <label class="sub-label"
             for="${param.utype}_PRESET"><fmt:message key="<%= presetLabelkey %>" bundle="${langBundle}"/><span
              class="search_criteria_label_contents color-accent"></span>
      </label>
      <select size="1" id="${param.utype}_PRESET"
              data-assoc-field="${param.utype}" name="<%= presetName %>"
              class="form-control search_criteria_input ui_unitconversion_input preset-date width-100">
        <option value=""></option>
        <option value="<%= DatePreset.PAST_24_HOURS.name() %>"><fmt:message key="<%= presetPast24HoursLabelKey %>" bundle="${langBundle}"/></option>
        <option value="<%= DatePreset.PAST_WEEK.name() %>"><fmt:message key="<%= presetPastWeekLabelKey %>" bundle="${langBundle}"/></option>
        <option value="<%= DatePreset.PAST_MONTH.name() %>"><fmt:message key="<%= presetPastMonthLabelKey %>" bundle="${langBundle}"/></option>
      </select>
    </div>
    <input type="hidden"
           name="<%= FormConstraint.FORM_NAME %>"
           value="<%= formName %>"/>

  </details>
</div>