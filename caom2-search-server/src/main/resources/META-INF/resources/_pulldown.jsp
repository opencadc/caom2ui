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



<div id="${param.utype}_details" class="form-group" style="margin-bottom:0px; margin-top:10px;">
  <label for="${param.utype}" id="${param.utype}_LABEL"
         class="hidden search_criteria_label">
    <fmt:message key="${param.utype}_FORM_LABEL" bundle="${langBundle}" />
  </label>
  <cadc:pulldown utype="${param.utype}"
                 options="${observationIntentOptions}"
                 defaultOptionLabel="Science and Calibration data"
                 cssClasses="form-control search_criteria_input width-100" />
  <input type="hidden"
         name="<%= FormConstraint.FORM_NAME %>"
         value="${param.utype}@Text" />
</div>

