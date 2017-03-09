<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="User storage system.">
  <meta name="author" content="Canadian Astronomy Data Centre">
  <meta name="keywords"
        content="VOSpace, IVOA, CADC, Canadian Astronomy Data Centre">
  <title>Sample CAOM-2 Search</title>

  <!-- Custom CSS -->

  <!-- End custom CSS -->

  <!-- Bootstrap core CSS -->
  <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
  <link href="css/bootstrap-theme.min.css" rel="stylesheet" media="screen">
</head>
<body>
<script type="application/javascript" src="js/bootstrap.min.js"></script>
</body>
</html>








<!DOCTYPE html>

<html lang="en">
<head>
  <link rel="icon" href="../../favicon.ico">

  <title>User Storage</title>

  <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
  <%--<link href="../../assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">--%>

  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Custom CSS -->
  <link rel="stylesheet" href="http://www.canfar.net/beta-canfar.github.io/css/canfar.css" media="screen"/>
  <link rel="stylesheet" href="http://www.canfar.net/beta-canfar.github.io/css/github-dark.css" media="screen"/>
  <link rel="stylesheet" href="http://www.canfar.net/beta-canfar.github.io/css/print.css" media="print"/>

  <!-- Bootstrap core CSS -->
  <link href="${contextPath}css/bootstrap.min.css" rel="stylesheet" media="screen">
  <link href="${contextPath}css/bootstrap-theme.min.css" rel="stylesheet" media="screen">

  <link rel="stylesheet" href="${contextPath}css/datatables.min.css" media="screen"/>
  <link rel="stylesheet" href="${contextPath}css/storage.css" media="screen"/>
</head>

<body>

<#assign startTime = .now?time>
  <#assign isRoot = folder.root>

    <#if username??>
      <#assign homeURL = '${contextPath}list/${username}'>
    </#if>


    <#include "defaults/_top_nav.ftl">

      <div class="container-fluid">

        <#include "_main.ftl">

      </div><!-- /.container -->


      <!-- Bootstrap core JavaScript
      ================================================== -->
      <!-- Placed at the end of the document so the pages load faster -->
      <script src="${contextPath}js/jquery.min.js"></script>
      <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
      <#--<script src="${contextPath}js/ie10-viewport-bug-workaround.js"></script>-->

                                                                                  <script type="text/javascript" src="${contextPath}js/org.opencadc.js"></script>
      <script type="text/javascript" src="${contextPath}js/datatables.min.js"></script>
      <script type="text/javascript" src="${contextPath}js/file-size.js"></script>
      <script type="text/javascript"
              src="${contextPath}js/jquery.csv-0.71.min.js"></script>
      <script type="text/javascript" src="${contextPath}js/jquery-browser.js"></script>
      <script type="text/javascript" src="${contextPath}js/jquery.form-3.24.js"></script>
      <script type="text/javascript"
              src="${contextPath}js/jquery.contextmenu/jquery.contextMenu-1.01.js"></script>
      <script type="text/javascript"
              src="${contextPath}js/jquery.tablesorter-2.7.2.min.js"></script>
      <script type="text/javascript"
              src="${contextPath}js/jquery-impromptu.min.js"></script>
      <script type="text/javascript" src="${contextPath}js/filemanager.js"></script>

      <!--
       AWAYS ensure the bootstrap.min.js comes last!
       The popover will not work otherwise.

       jenkinsd 2016.06.24
      -->
      <script src="${contextPath}js/bootstrap.min.js"></script>

      <script type="text/javascript">

        $(document).ready(function ()
                          {
                            <#-- Intercept the JavaScript here for the Folder Details button. -->
                                                                                      <#if !isRoot>
                                                                                           // Activate the Details button.
                                                                                           $('[data-toggle="popover"]').popover(
                                                                                             {
                                                                                               html: true,
                                                                                               title: "<strong>${folder.name}</strong>",
                                                                                               content: function ()
                                                                                               {
                                                                                                 return '<table class="table table-condensed table-bordered">'
                                                                                                        +
                                                                                                        '<tbody><tr><td>Owned by</td><td class="info"><strong>${folder.owner}</strong></td></tr>'
                                                                                                        +
                                                                                                        '<tr><td>Last used</td><td class="info">${folder.lastModifiedHumanReadable}</td></tr>'
                                                                                                        +
                                                                                                        '<tr><td colspan="2">Is <#if !folder.writable><span class="text-danger">not </span></#if>writable by you.</td></tr>'
                                                                                                        + '</tbody></table>';
                                                                                               }
                                                                                             });
                            </#if>

                            // Override CSS for search filter field.
                            $.fn.DataTable.ext.oStdClasses.sFilter =
                              "dataTables_filter";

                            // For quick pre-load.
                            var rows = [];
                            <#list initialRows as row>
                                   rows.push([${row}]);
                            </#list>

                               $.getJSON('${contextPath}scripts/languages/' +
                                         $("html").attr("lang") + '.json')
                                 .done(function (json)
                                       {
                                         // Row count is 100 by default to just show
                                         // a moving barber pole progress.
                                         fileManager(rows, $("#beacon"),
                                                     "<#if startURI??>${startURI}</#if>",
                                                     "${folder.path}", ${folder.writable?c},
                                                     100 , json, "${contextPath}");
                                       })
                                 .fail(function (request, textStatus, errorThrown)
                                       {
                                         console.log("Error (" + request.status + "): "
                                                     + textStatus + "\n" +
                                                     errorThrown);
                                       });

                            $(document).on("click", "a#logout", function ()
                            {
                              $.ajax({
                                       url: '${contextPath}ac/authenticate',
                                       method: 'DELETE'
                                     })
                                .done(function ()
                                      {
                                        window.location.reload(true);
                                      });
                            });
                          });
      </script>
</body>
</html>
