<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>


<div class="col-sm-12 label_tooltip_${param.tipSide}">
    <div class="form-group">
        <cadc:checkbox checkboxName="${param.name}" i18nKey="${param.name}_FORM_LABEL" />
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>" >
    </div>
</div>
