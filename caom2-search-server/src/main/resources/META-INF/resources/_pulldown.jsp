<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>

<jsp:useBean id="observationIntentOptions" class="java.util.TreeMap" />
<c:set target="${observationIntentOptions}" property="Science and Calibration data" value="" />
<c:set target="${observationIntentOptions}" property="Science data only"
       value="science" />
<c:set target="${observationIntentOptions}" property="Calibration data only"
       value="calibration" />

<%
  final String name = request.getParameter("utype");
  final String labelKey = name + "_FORM_LABEL";
  final String tipSide = request.getParameter("tipSide");
  final String formName = name + "@Text";
%>

<%--<li class="label_tooltip_<%= tipSide %> margin-top-medium">--%>
  <div id="<%= name %>_details" class="form-inline">
    <label for="<%= name %>" id="<%= name %>_LABEL"
           class="wb-invisible search_criteria_label">
      <fmt:message key="<%= labelKey %>" bundle="${langBundle}" /></label>
    <cadc:pulldown utype="${param.utype}"
                   options="${observationIntentOptions}"
                   defaultOptionLabel="Science and Calibration data"
                   cssClasses="search_criteria_input width-100" />
    <input type="hidden"
           name="<%= FormConstraint.FORM_NAME %>"
           value="<%= formName %>" />
  </div>
<%--</li>--%>
