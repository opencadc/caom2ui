<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This JSP page renders an HTML table with the Observation metadata. 
--%>

<%@ page import="ca.nrc.cadc.caom2.Plane" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>


<jsp:useBean id="obs" scope="request" type="ca.nrc.cadc.caom2.Observation"/>
<%
  Integer indent = 4;
  request.setAttribute("indent", indent);
%>

<div class="observation" style="margin-left: <%= indent.intValue() %>em">

  <h2><%= obs.getClass().getSimpleName() %>
  </h2>

  <table class="content">
    <tr class="even">
      <td>obsID</td>
      <td><%= obs.getID() %> aka <%= obs.getID().getLeastSignificantBits() %>
      </td>
    </tr>
    <tr>
      <td>lastModified</td>
      <td><%= SS.toString(obs.getLastModified()) %>
      </td>
    </tr>
    <tr class="even">
      <td>collection</td>
      <td><%= obs.getCollection() %>
      </td>
    </tr>
    <tr>
      <td>observationID</td>
      <td><%= obs.getObservationID() %>
      </td>
    </tr>
    <tr class="even">
      <td>metaRelease</td>
      <td><%= SS.toString(obs.metaRelease) %>
      </td>
    </tr>
    <tr>
      <td>sequenceNumber</td>
      <td><%= SS.toString(obs.sequenceNumber) %>
      </td>
    </tr>
    <tr class="even">
      <td>algorithm</td>
      <td><%= SS.toString(obs.getAlgorithm()) %>
      </td>
    </tr>
    <tr>
      <td>members</td>
      <td><%= SS.toMemberString(request.getContextPath(), obs, request.getParameter("ID")) %>
      </td>
    </tr>
    <tr class="even">
      <td>type</td>
      <td><%= SS.toString(obs.type) %>
      </td>
    </tr>
    <tr>
      <td>intent</td>
      <td><%= SS.toString(obs.intent) %>
      </td>
    </tr>
    <tr class="even">
      <td>proposal</td>
      <td><%= SS.toString(obs.proposal) %>
      </td>
    </tr>
    <tr>
      <td>requirements</td>
      <td><%= SS.toString(obs.requirements) %>
      </td>
    </tr>
    <tr class="even">
      <td>telescope</td>
      <td><%= SS.toString(obs.telescope) %>
      </td>
    </tr>
    <tr>
      <td>instrument</td>
      <td><%= SS.toString(obs.instrument) %>
      </td>
    </tr>
    <tr class="even">
      <td>target</td>
      <td><%= SS.toString(obs.target) %>
      </td>
    </tr>
    <tr class="even">
      <td>target position</td>
      <td><%= SS.toString(obs.targetPosition) %>
      </td>
    </tr>
    <tr>
      <td>environment</td>
      <td><%= SS.toString(obs.environment) %>
      </td>
    </tr>
  </table>

  <%
    for (final Plane plane : obs.getPlanes())
    {
      request.setAttribute("plane", plane);
  %>
  <c:import url="plane.jsp"/>
  <%
    }
  %>
</div>
