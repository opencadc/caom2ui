<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
String target = (String) request.getAttribute("target");
if (target == null)
    target = "";

String error = (String) request.getAttribute("error");
if (error == null)
    error = "";
%>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="/cadc/resources/cadc.css">
        <link rel="stylesheet" type="text/css" href="/_search/css/search.css">
        <title>Search Error</title>
    </head>
    <body>
        <c:catch><c:import url="http://localhost/cadc/skin/quickHeader" /></c:catch>
        <div class="as_error">
            <p>
                An error occurred processing the search:&nbsp;&nbsp;<%= target %>
            </p>
            <p>
                <%= error %>
            </p>
        </div>
    </body>
</html>