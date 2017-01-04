<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<jsp:useBean id="errors" scope="request"
             class="ca.nrc.cadc.search.form.FormErrors" />

<div style="color: red; text-align: center;">
        <%= errors.getFormError("hierarchy") %> for utype ${param.utype}
        <%--Utype ${param.utype}--%>
</div>