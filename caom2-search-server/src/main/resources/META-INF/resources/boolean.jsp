<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>


<div class="label_tooltip_${param.tipSide}">

    <div data-toggle="popover"
         data-utype="${param.name}"
         data-placement="${param.tipSide}"
         data-title="<fmt:message key="${param.name}_FORM_LABEL" bundle="${langBundle}"/>"
         class="advancedsearch-tooltip glyphicon glyphicon-question-sign popover-blue popover-right">
    </div>

    <div class="form-group">
        <cadc:checkbox checkboxName="${param.name}" i18nKey="${param.name}_FORM_LABEL" />
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>" disabled="disabled"/>
    </div>
</div>
