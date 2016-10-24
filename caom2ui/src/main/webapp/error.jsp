<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>

<%--
This JSP page renders a full HTML document with the complete CAOM Observation. 
--%>

<%
  String errorMsg = (String) request.getAttribute("errorMsg");
  RuntimeException rex =
      (RuntimeException) request.getAttribute("runtimeException");
%>

<html>
  <head>
    <title>CAOM Observation</title>
    <link rel="stylesheet" href="/caom2ui/cadc.css">
  </head>

  <body>
    <div class="main">

      <h1>Common Archive Observation Model (CAOM)</h1>

      <p>
        <span style="font-weight: bold;">Message:&nbsp;</span><%= errorMsg.replaceAll("\\|", "<br /><br />") %>
      </p>


      <%
        if (rex != null)
        {
      %>
      <h2>Unexpected failure / Erreur:</h2>
<pre>
<%
  java.io.StringWriter sw = new java.io.StringWriter();
  java.io.PrintWriter pw = new java.io.PrintWriter(sw);
  rex.printStackTrace(pw);
  pw.close();
  pageContext.getOut().write(sw.toString());
%>
</pre>
      <%
        }
      %>

    </div>

  </body>

</html>