<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="obs" scope="request" type="ca.nrc.cadc.caom2.Observation"/>

<html>
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>CAOM Observation</title>
    <style rel="stylesheet">
      body
      {
        /*
        color: white;
        background: black;
        */
        color: black;
        background: white;

        font-family: sans-serif;
        font-size: smaller; /* default looks very large in most browsers */

        margin: 0;
        padding: 0;
        height: 100%;
      }

      div.chunk
      {
        background: inherit;
        color: inherit;
        font-size: inherit;
        margin: 2em;
        border: 1px solid black;
        padding: 2px;
      }

      div.chunk td.img
      {
        text-align: center;
      }

      h1
      {
        font-size: 120%;
        font-weight: bold;
        color: inherit;
        background: inherit;
        padding-top: 1em;
        padding-left: 1em;
      }

      h2
      {
        font-size: 100%;
        color: inherit;
        background: inherit;
        padding-top: 1em;
        padding-left: 2em;
      }

      h3
      {
        font-size: 100%;
        color: inherit;
        background: inherit;
        padding-top: 1em;
        padding-left: 2em;
      }

      /* TABLES */
      table
      {
      }

      th
      {
        text-align: center;
      }

      td
      {
        text-align: left;
      }

      td.primary
      {
        font-weight: bold;
      }

      td.value
      {
      }

      table.form
      {
        margin-left: auto; /* center table in enclosing block */
        margin-right: auto; /* center table in enclosing block */
        border-spacing: 0;
        border: 0 none;
        padding-left: 0;
        padding-right: 0;
        padding-bottom: 0;
        empty-cells: show;
      }

      table.form td
      {
        /*width: 175px;*/
        text-align: center;
        border: 1em solid white;
        /*background: #ccc;*/
      }

      table.content
      {
        font-family: monospace;
        border-collapse: collapse;
        border-spacing: 0;
        border: 1px solid black;
        padding-left: 0;
        padding-right: 0;
        empty-cells: show;
      }

      table.content th
      {
        background: #bfccd9;
        color: black;
        text-align: center;
        border: 1px solid black;
        padding-left: 0.5em;
        padding-right: 0.5em;
      }

      table.content td
      {
        border: 0 solid;
        vertical-align: top;
        padding-left: 0.5em;
        padding-right: 0.5em;
      }

      tr.odd_b td
      {
        border: 1px solid;
        vertical-align: top;
        padding-left: 0.5em;
        padding-right: 0.5em;
      }

      tr.even_b td
      {
        border: 1px solid;
        vertical-align: top;
        padding-left: 0.5em;
        padding-right: 0.5em;
      }

      table.content tr:nth-child(even) {
        background: #ddd;
      }

      table.content tr.subhead
      {
        background: #bfccd9;
      }

      table.content tr.odd_b
      {
        background: white;
      }

      table.content tr.even_b
      {
        background: #ddd;
      }

      table.content tr.subhead
      {
        background: #bfccd9;
      }

      img
      {
        border: 0 none;
      }
    </style>
  </head>

  <body>
    <div class="main">

      <h1>Common Archive Observation Model (CAOM2)</h1>

      <c:import url="observation.jsp"/>

    </div>

  </body>

</html>