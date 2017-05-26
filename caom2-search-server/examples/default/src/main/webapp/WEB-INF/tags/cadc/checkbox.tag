<%@ tag body-content="empty" %>
<%@ attribute name="i18nKey" required="true" %>
<%@ attribute name="checkboxName" required="true" %>
<%@ attribute name="cssClasses" required="false" %>
<%@ attribute name="disableTo" required="false" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div class="form-group">
    <div class="checkbox">
        <label for="${checkboxName}"
               class="col-sm-1 search_criteria_label">
            <input type="checkbox"
                   class='<c:if test="${not empty fn:trim(cssClasses)}">${cssClasses}</c:if>'
                    <c:if test="${not empty fn:trim(disableTo)}"> data-disable-to="${disableTo}" </c:if>
                   id="${checkboxName}" name="${checkboxName}" />
            <fmt:message key="${i18nKey}" bundle="${langBundle}" />
        </label>
    </div>
</div>


