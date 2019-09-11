<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request"
             class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<div role="tabpanel" class="tab-pane active" id="queryFormTab">

  <form id="queryForm" name="queryForm" class="queryForm"
        method="post" action="${pageContext.request.contextPath}/find" enctype="multipart/form-data">

    <!-- Used by VOView to sort the results. -->
    <input type="hidden" name="sort_column" value="Start Date"/>
    <input type="hidden" name="sort_order" value="descending"/>

    <!-- Used by AdvancedSearch to pass to TAP. -->
    <input type="hidden" name="formName" value="adsform"/>
    <input type="hidden" name="SelectList" class="CAOM2_selectlist"/>
    <input type="hidden" name="MaxRecords" value="${param.maxRowLimit}"/>
    <input type="hidden" name="format" value="csv"/>

    <!-- Used by AdvancedSearch to pass to VOTV. -->
    <input type="hidden" id="max_row_limit_warning"
           value="<fmt:message key="MAX_ROW_LIMIT_WARNING" bundle="${langBundle}"/>"/>

    <div class="col-sm-12 button-holder">
      <button disabled
              type="submit"
              class="btn btn-primary submit-query"
              value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />">
        <fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}"/>
      </button>
      <button type="reset"
              class="btn btn-default reset-query-form"
              value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />">
        <fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}"/>
      </button>
    </div>

    <div class="col-sm-12">
      <fmt:message key="TOOLTIP_CLARIFICATION_MESSAGE_PREFIX" bundle="${langBundle}"/>&nbsp;<span
        class="glyphicon glyphicon-question-sign popover-info"></span>&nbsp;<fmt:message
        key="TOOLTIP_CLARIFICATION_MESSAGE_SUFFIX" bundle="${langBundle}"/>
    </div>

    <div class="row">
      <div class="col-md-3 search-category">
        <div class="panel panel-default">
          <div class="panel-heading"><fmt:message
              key="OBSERVATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
          <div class="panel-body search-constraints">
            <c:import url="text_formgroup.jsp?utype=Observation.observationID&tipSide=right"/>
            <c:import url="text_formgroup.jsp?utype=Observation.proposal.pi&tipSide=right&enableAutocomplete=true"/>
            <c:import url="text_formgroup.jsp?utype=Observation.proposal.id&tipSide=right&enableAutocomplete=true"/>
            <c:import url="text_formgroup.jsp?utype=Observation.proposal.title&tipSide=right&enableAutocomplete=true"/>
            <c:import url="text_formgroup.jsp?utype=Observation.proposal.keywords&tipSide=right"/>
            <c:import url="timestamp.jsp?utype=Plane.dataRelease&tipSide=right"/>
            <c:import url="_pulldown.jsp?utype=Observation.intent&tipSide=right"/>
          </div>
        </div>
      </div>


      <div class="col-md-3 search-category">
        <div class="panel panel-default">
          <div class="panel-heading"><fmt:message
              key="SPATIAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
          <div class="panel-body search-constraints">
            <c:import
                url="position.jsp?utype=Plane.position.bounds&tipSide=right"/>
            <c:import
                url="number.jsp?utype=Plane.position.sampleSize&tipSide=right"/>
            <c:import
                url="boolean.jsp?name=Plane.position.DOWNLOADCUTOUT&tipSide=right"/>

          </div>
        </div>
      </div>

      <div class="col-md-3 search-category">
        <div class="panel panel-default">
          <div class="panel-heading"><fmt:message
              key="TIME_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
          <div class="panel-body search-constraints">
            <c:import
                url="date.jsp?utype=Plane.time.bounds.samples&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.time.exposure&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.time.bounds.width&tipSide=left"/>
          </div>
        </div>
      </div>

      <div class="col-md-3 search-category">
        <div class="panel panel-default">
          <div class="panel-heading"><fmt:message
              key="SPECTRAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
          <div class="panel-body search-constraints">
            <c:import
                url="energy.jsp?utype=Plane.energy.bounds.samples&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.sampleSize&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.resolvingPower&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.bounds.width&tipSide=left"/>
            <c:import
                url="number.jsp?utype=Plane.energy.restwav&tipSide=left"/>
            <c:import
                url="boolean.jsp?name=Plane.energy.DOWNLOADCUTOUT&tipSide=left"/>
          </div>
        </div>
      </div>

      <div class="col-sm-12 resolver-result-tooltip hidden">
        <strong><fmt:message key="RES_TARGET" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-target"></p>
        <strong><fmt:message key="RES_SERVICE" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-service"></p>
        <strong><fmt:message key="RES_COORDINATES" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-coordinates"></p>
        <strong><fmt:message key="RES_TYPE" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-type"></p>
        <strong><fmt:message key="RES_MORPHOLOGY" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-morphology"></p>
        <strong><fmt:message key="RES_TIME" bundle="${langBundle}"/></strong><br>
        <p class="resolver-result-time"></p>
      </div>

      <c:import
          url="hierarchy.jsp?colcount=seven-col&utype=Plane.energy.emBand/Observation.collection/Observation.instrument.name/Plane.energy.bandpassName/Plane.calibrationLevel/Plane.dataProductType/Observation.type&modelDataSource=caom2" />
    </div>

    <div class="col-sm-12 button-holder">
      <button disabled
              type="submit"
              class="btn btn-primary submit-query"
              value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />">
        <fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}"/>
      </button>
      <button type="reset"
              class="btn btn-default reset-query-form"
              value="reset">
        <fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}"/>
      </button>
    </div>

  </form>
</div>
