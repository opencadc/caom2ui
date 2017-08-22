<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="java.util.List" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.caom2repo.ObsLink" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>

<%
    String skin = "http://localhost/en/skin/";
    String htmlHead = skin + "htmlHead";
    String bodyHeader = skin + "quickHeader";
    String bodyFooter = skin + "bodyFooter";

    List<ObsLink> uris = (List<ObsLink>) request.getAttribute("uris");
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
    <b>found: </b><%= uris.size() %> (max: 100) most recent changes
</p>

<div class="table">
<table>
<tr>
    <th>num</th>
    <th>collection</th>
    <th>observationID</th>
    <th>lastModified (UTC)</th>
</tr>

<%
int i=1;
for (ObsLink u : uris)
{
%>
    <tr>
        <td><%= i++ %></td>
        <td><%= u.uri.getCollection() %></td>
        <td>
            <a href="<%= u.uri.getCollection() %>/<%= u.uri.getObservationID() %>">
            <%= u.uri.getObservationID() %>
            </a>
        </td>
        <td><%= SS.toString(u.lastModified) %></td>
    </tr>
<%
}
%>

</table>
</div>

</div>

</body>

</html>