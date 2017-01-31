<%
  final String tooltipClass = request.getParameter("tooltipClass");
  final String forID = request.getParameter("forID");
  final String label = request.getParameter("label");
  final String cssClassName;

  if ((tooltipClass != null) && (!tooltipClass.trim().equals("")))
  {
    cssClassName = "search_criteria_label " + tooltipClass;
  }
  else
  {
    cssClassName = "search_criteria_label";
  }
%>

<label id="<%= forID %>_label" for="<%= forID %>" class="<%= cssClassName %>">
  <a class="openable" id="<%= forID %>_toggleanchor">
    <img src="/_search/images/arrow_right.png" id="search_criteria_closed" alt="arrow">
    <img src="/_search/images/arrow_down.png" id="search_criteria_open" style="display: none;" alt="arrow">
      &nbsp;<span class="label_text"><%= label %></span></a>
</label>