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
    <tr>
      <td>chunkID</td>
      <td><%= chunk.getID() %> aka <%= chunk.getID()
          .getLeastSignificantBits() %>
      </td>
    </tr>
    <tr>
      <td>lastModified</td>
      <td><%= SS.toString(chunk.getLastModified()) %>
      </td>
    </tr>
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

    <tr class="even">
      <td>positionAxis1</td>
      <td><%= chunk.positionAxis1 %>
      </td>
    </tr>
    <tr class="even">
      <td>positionAxis2</td>
      <td><%= chunk.positionAxis2 %>
      </td>
    </tr>
    <tr>
      <td>position</td>
      <td><%= SS.toString(chunk.position) %>
      </td>
    </tr>

    <tr class="even">
      <td>energyAxis</td>
      <td><%= chunk.energyAxis %>
      </td>
    </tr>
    <tr>
      <td>energy</td>
      <td><%= SS.toString(chunk.energy) %>
      </td>
    </tr>

    <tr class="even">
      <td>timeAxis</td>
      <td><%= chunk.timeAxis %>
      </td>
    </tr>
    <tr>
      <td>time</td>
      <td><%= SS.toString(chunk.time) %>
      </td>
    </tr>

    <tr class="even">
      <td>polarizationAxis</td>
      <td><%= chunk.polarizationAxis %>
      </td>
    </tr>
    <tr>
      <td>polarization</td>
      <td><%= SS.toString(chunk.polarization) %>
      </td>
    </tr>

    <tr class="even">
      <td>observableAxis</td>
      <td><%= chunk.observableAxis %>
      </td>
    </tr>
    <tr>
      <td>observable</td>
      <td><%= SS.toString(chunk.observable) %>
      </td>
    </tr>
  </table>
</div>
