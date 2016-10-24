<%@ taglib uri="WEB-INF/c.tld" prefix="c" %>


<jsp:useBean id="obs" scope="request" type="ca.nrc.cadc.caom2.Observation"/>

<html>
  <head>
    <title>CAOM Observation</title>
    <link rel="stylesheet" href="/caom2ui/cadc.css">
  </head>

  <body>
    <div class="main">

      <h1>Common Archive Observation Model (CAOM2)</h1>

      <c:import url="observation.jsp"/>

    </div>

  </body>

</html>