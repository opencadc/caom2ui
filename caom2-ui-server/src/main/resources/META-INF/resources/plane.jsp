<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This JSP page renders a CAOM Plane in an HTML table.

It should be included with 

request.setAttribute("plane", p);
<c:import url="plane.jsp">

--%>
<%@ page import="ca.nrc.cadc.caom2.Artifact" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>

<jsp:useBean id="plane" scope="request" type="ca.nrc.cadc.caom2.Plane"/>
<jsp:useBean id="indent" scope="request" type="java.lang.Integer"/>

<div class="plane" style="margin-left: <%= indent.intValue() %>em">

  <h2>Plane</h2>

  <table class="content">
    <%--    Provide entity id first --%>
    <%= SS.getCaomEntityID(plane)%>

    <tr class="even">
      <td>productID</td>
      <td><%= plane.getProductID() %>
      </td>
    </tr>
    <tr>
      <td>creatorID</td>
      <td><%= plane.creatorID %>
      </td>
    </tr>
    <tr>
      <td>metaRelease</td>
      <td><%= SS.toString(plane.metaRelease) %>
      </td>
    </tr>
    <tr class="even">
      <td>metaReadGroups</td>
      <td><%= SS.serializeURISet(plane.getMetaReadGroups()) %>
      </td>
    </tr>
    <tr>
      <td>dataRelease</td>
      <td><%= SS.toString(plane.dataRelease) %>
      </td>
    </tr>
    <tr class="even">
      <td>dataReadGroups</td>
      <td><%= SS.serializeURISet(plane.getDataReadGroups()) %>
      </td>
    </tr>
    <tr>
      <td>dataProductType</td>
      <td><%= SS.toString(plane.dataProductType) %>
      </td>
    </tr>
    <tr class="even">
      <td>calibrationLevel</td>
      <td><%= SS.toString(plane.calibrationLevel) %>
      </td>
    </tr>

    <tr>
      <td>observable</td>
      <td><%= SS.toString(plane.observable) %>
      </td>
    </tr>
    <tr>
      <td>quality</td>
      <td><%= SS.toString(plane.quality) %>
      </td>
    </tr>
    <tr class="even">
      <td>metrics</td>
      <td><%= SS.toString(plane.metrics) %>
      </td>
    </tr>
    <tr class="even">
      <td>position</td>
      <td><%= SS.getPlanePosition(plane) %>
      </td>
    </tr>
    <tr>
      <td>energy</td>
      <td><%= SS.getPlaneEnergy(plane) %>
      </td>
    </tr>
    <tr class="even">
      <td>time</td>
      <td><%= SS.getPlaneTime(plane) %>
      </td>
    </tr>
    <tr>
      <td>polarization</td>
      <td><%= SS.getPlanePolarization(plane) %>
      </td>
    </tr>
    <tr>
      <td>custom</td>
      <td><%= SS.getPlaneCustom(plane) %>
      </td>
    </tr>
    <tr class="provenance">
      <td>provenance</td>
      <td><%= SS.toString(plane.provenance) %>
      </td>
    </tr>
    <%--    Populate in the rest of the CaomEntity member values --%>
    <%= SS.getCaomEntityPortion(plane)%>

  </table>

  <%
    for (final Artifact artifact : plane.getArtifacts())
    {
      request.setAttribute("artifact", artifact);
  %>
  <c:import url="artifact.jsp"/>
  <%
    }
  %>
</div>

