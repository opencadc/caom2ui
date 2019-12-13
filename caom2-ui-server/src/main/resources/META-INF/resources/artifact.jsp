<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This JSP page renders a CAOM Artifact in an HTML table.

It should be included with 

request.setAttribute("artifact", a);
<c:import url="artifact.jsp">
--%>

<%@ page import="ca.nrc.cadc.caom2.Part" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>

<jsp:useBean id="artifact" scope="request" type="ca.nrc.cadc.caom2.Artifact"/>
<jsp:useBean id="indent" scope="request" type="java.lang.Integer"/>


<div class="artifact" style="margin-left: <%= indent.intValue() %>em">

  <h2>Artifact</h2>

  <table class="content">
    <tr>
      <td>artifactID</td>
      <td><%= artifact.getID() %> aka <%= artifact.getID()
          .getLeastSignificantBits() %>
      </td>
    </tr>
    <tr>
      <td>lastModified</td>
      <td><%= SS.toString(artifact.getLastModified()) %>
      </td>
    </tr>
    <tr>
      <td>uri</td>
      <td><%= artifact.getURI() %>
      </td>
    </tr>
    <tr>
        <td>productType</td><td><%= SS.toString(artifact.getProductType()) %></td>
    </tr>
    <tr>
        <td>releaseType</td><td><%= SS.toString(artifact.getReleaseType()) %></td>
    </tr>
    </tr>
    <tr>
      <td>contentType</td>
      <td><%= artifact.contentType %>
      </td>
    </tr>
    <tr>
      <td>contentLength</td>
      <td><%= artifact.contentLength %>
      </td>
    </tr>
    <tr>
      <td>contentChecksum</td>
      <td><%= artifact.contentChecksum.toString() %>
      </td>
    </tr>
<%--    TODO: caom24: add contentRelease and contentReadGroups --%>
  </table>

  <%
    for (final Part part : artifact.getParts())
    {
      request.setAttribute("part", part);
  %>
  <c:import url="part.jsp"/>
  <%
    }
  %>
</div>
