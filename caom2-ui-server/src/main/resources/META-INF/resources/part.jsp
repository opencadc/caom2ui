<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This JSP page renders a CAOM Part in an HTML table.

It should be included with 

request.setAttribute("part", p);
<c:import url="part.jsp">
--%>

<%@ page import="ca.nrc.cadc.caom2.Chunk" %>
<%@ page import="ca.nrc.cadc.caom2.ui.server.SS" %>

<jsp:useBean id="part" scope="request" type="ca.nrc.cadc.caom2.Part"/>
<jsp:useBean id="indent" scope="request" type="java.lang.Integer"/>

<div class="part" style="margin-left: <%= indent.intValue() %>em">

  <h2>Part</h2>

  <table class="content">
    <%--    Provide entity id first --%>
    <%= SS.getCaomEntityID(part)%>

    <tr>
      <td>name</td>
      <td><%= part.getName() %>
      </td>
    </tr>
    <tr>
      <td>productType</td>
      <td><%= SS.toString(part.productType) %>
      </td>
    </tr>

    <%--    Populate in the rest of the CaomEntity member values --%>
    <%= SS.getCaomEntityPortion(part)%>
  </table>
  <%
    for (final Chunk chunk : part.getChunks())
    {
      request.setAttribute("chunk", chunk);
  %>
  <c:import url="chunk.jsp"/>
  <%
    }
  %>
</div>
