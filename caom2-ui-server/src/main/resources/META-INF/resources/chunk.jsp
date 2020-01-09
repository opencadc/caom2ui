<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This JSP page renders a CAOM Artifact in an HTML table.

It should be included with 

request.setAttribute("chunk", c);
<c:import url="chunk.jsp">
--%>

<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>

<jsp:useBean id="chunk" scope="request" type="ca.nrc.cadc.caom2.Chunk"/>
<jsp:useBean id="indent" scope="request" type="java.lang.Integer"/>

<div class="chunk" style="margin-left: <%= indent.intValue() %>em">

  <h3>Chunk</h3>

  <table class="content">
    <%--    Provide entity id first --%>
    <%= SS.getCaomEntityID(chunk)%>

    <tr>
      <td>productType</td>
      <td><%= SS.toString(chunk.productType) %>
      </td>
    </tr>
    <tr>
      <td>naxis</td>
      <td><%= chunk.naxis %>
      </td>
    </tr>

    <tr>
      <td>positionAxis1</td>
      <td><%= chunk.positionAxis1 %>
      </td>
    </tr>
    <tr>
      <td>positionAxis2</td>
      <td><%= chunk.positionAxis2 %>
      </td>
    </tr>
    <tr>
      <td>position</td>
      <td><%= SS.toString(chunk.position) %>
      </td>
    </tr>

    <tr>
      <td>energyAxis</td>
      <td><%= chunk.energyAxis %>
      </td>
    </tr>
    <tr>
      <td>energy</td>
      <td><%= SS.toString(chunk.energy) %>
      </td>
    </tr>

    <tr>
      <td>timeAxis</td>
      <td><%= chunk.timeAxis %>
      </td>
    </tr>
    <tr>
      <td>time</td>
      <td><%= SS.toString(chunk.time) %>
      </td>
    </tr>

    <tr>
      <td>polarizationAxis</td>
      <td><%= chunk.polarizationAxis %>
      </td>
    </tr>
    <tr>
      <td>polarization</td>
      <td><%= SS.toString(chunk.polarization) %>
      </td>
    </tr>

    <tr>
      <td>customAxis</td>
      <td><%= chunk.customAxis %>
      </td>
    </tr>
    <tr>
      <td>custom</td>
      <td><%= SS.toString(chunk.custom) %>
      </td>
    </tr>

    <tr>
      <td>observableAxis</td>
      <td><%= chunk.observableAxis %>
      </td>
    </tr>
    <tr>
      <td>observable</td>
      <td><%= SS.toString(chunk.observable) %>
      </td>
    </tr>

    <%--    Populate in the rest of the CaomEntity member values --%>
    <%= SS.getCaomEntityPortion(chunk)%>
  </table>
</div>
