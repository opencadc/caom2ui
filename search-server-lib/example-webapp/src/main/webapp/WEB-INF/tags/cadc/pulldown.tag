<%@ tag body-content="empty" %>
<%@ attribute name="utype" required="true" %>
<%@ attribute name="cssClasses" required="false" %>
<%@ attribute name="defaultOptionLabel" required="false" %>
<%@ attribute name="options" required="true"
              type="java.util.Map" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<select id="${utype}" name="${utype}" class="${cssClasses}">
  <c:forEach items="${options}" var="a">
    <option value="${a.value}" <c:if test="${not empty defaultOptionLabel and defaultOptionLabel eq a.key}">selected="selected"</c:if>>${a.key}</option>
  </c:forEach>
</select>
