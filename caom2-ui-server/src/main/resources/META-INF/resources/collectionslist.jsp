<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>

<%
    List<String> collections = (List<String>) request.getAttribute("collections");
%>

<html>
<head>
    <title>CAOM Observation</title>
    <link rel="stylesheet" type="text/css" href="css/cadc.css"/>
</head>

<body>
<div class="main">

<h1>Common Archive Observation Model (CAOM2)</h1>

<p>
Note: You must be logged in to view observation metadata through this interface.
</p>

<h2>Collections:</h2>

<%
    for (String collection : collections)
    {
%>
    <p>
        <b>list all observations:</b> <a href="list/<%= collection %>"><%= collection %></a>
    </p>
<%
    }
%>
</div>

</body>

</html>