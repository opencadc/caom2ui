<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="helpContent" class="margin-top-medium">
  <h2 class="background-accent">
    <fmt:message key="SEARCH_HELP_TITLE" bundle="${langBundle}" /></h2>

  <fmt:message key="SEARCH_HELP_HTML" bundle="${langBundle}" />

  <h2 class="background-accent">
    <fmt:message key="RESULTS_HELP_TITLE" bundle="${langBundle}" /></h2>

  <fmt:message key="RESULTS_HELP_HTML" bundle="${langBundle}" />
</div>
