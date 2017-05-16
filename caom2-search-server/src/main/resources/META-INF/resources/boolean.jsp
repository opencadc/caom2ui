<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8" %>

<%@ page import="ca.nrc.cadc.search.form.FormConstraint" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/cadc" prefix="cadc" %>

<%
  final String name = request.getParameter("name");
  final String labelKey = name + "_FORM_LABEL";
  final String tipSide = request.getParameter("tipSide");
  final String formName = name + "@Boolean";
%>

<%--TODO: handle tooltips--%>
<div class="col-sm-12 label_tooltip_<%= tipSide %>">
  <%--<div id="<%= name %>_details" class="form-inline">--%>

    <div class="form-group">
      <%--<div class="col-sm-2"></div>--%>
      <%--<div class="col-sm-10 checkbox">--%>
        <cadc:checkbox checkboxName="<%= name %>" i18nKey="<%= labelKey %>" />
        <input type="hidden"
               name="<%= FormConstraint.FORM_NAME %>" >
      <%--</div>--%>
    </div>
</div>
