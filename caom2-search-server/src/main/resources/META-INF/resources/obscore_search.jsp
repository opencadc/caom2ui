<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
         pageEncoding="UTF-8" %>

<jsp:useBean id="job" scope="request" class="ca.nrc.cadc.uws.Job"/>
<jsp:useBean id="errors" scope="request"
             class="ca.nrc.cadc.search.form.FormErrors"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    final String maxRowLimit = request.getParameter("maxRowLimit");
%>

<div role="tabpanel" class="tab-pane active" id="obsCoreQueryFormTab">
    <h2>ObsCore</h2>
    <form id="obscoreQueryForm" name="obscoreQueryForm" class="queryForm"
    method="post" action="${pageContext.request.contextPath}/find"
    enctype="multipart/form-data">

        <!-- Used by VOView to sort the results. -->
        <input type="hidden" name="sort_column" value="t_min"/>
        <input type="hidden" name="sort_order" value="descending"/>
        <input type="hidden" name="formName" value="adsform"/>

        <!-- Used by AdvancedSearch to pass to TAP. -->
        <input type="hidden" name="SelectList" class="ObsCore_selectlist" />
        <input type="hidden" name="MaxRecords" value="<%= maxRowLimit %>"/>
        <input type="hidden" name="format" value="csv"/>

        <%--<div class="equalize margin-top-large margin-bottom-none">--%>
        <%--<div class="span-3 row-start form-inline">--%>
        <%--<input type="submit"--%>
        <%--value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"--%>
        <%--data-rel="popup" data-dismissible="false"--%>
        <%--data-position-to="window" data-inline="true"--%>
        <%--data-transition="pop" data-corners="true" data-shadow="true"--%>
        <%--data-iconshadow="true" data-wrapperels="span" data-theme="c"--%>
        <%--aria-haspopup="true" aria-owns="queryOverlay"--%>
        <%--class="submit-obscore-query button button-accent ui-btn"/>--%>
        <%--<input type="reset" data-role="none"--%>
        <%--value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"--%>
        <%--class="reset-obscore-query-form button ui-btn"/>--%>
        <%--</div>--%>
        <%--<div class="span-5 row-end"></div>--%>
        <%--<div class="clear"></div>--%>
        <%--</div>--%>

        <div class="col-sm-12 button-holder">
            <button type="submit" class="btn btn-primary" value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />" >
                <fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />
            </button>
            <button type="reset" class="btn btn-default" value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />" >
                <fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />
            </button>
        </div>

        <div class="row">
            <div class="col-md-3 search-category">
                <div class="panel panel-default">
                    <div class="panel-heading"><fmt:message
                            key="OBSERVATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
                    <div class="panel-body search-constraints">
                        <c:import
                        url="text_formgroup.jsp?utype=DataID.observationID&tipSide=right"/>
                        <c:import
                        url="text_formgroup.jsp?utype=Target.Name&tipSide=right"/>
                        <c:import
                        url="timestamp.jsp?utype=Curation.releaseDate&tipSide=right" />
                    </div>
                </div>
            </div>


            <div class="col-md-3 search-category">
                <div class="panel panel-default">
                    <div class="panel-heading"><fmt:message
                            key="SPATIAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
                    <div class="panel-body search-constraints">

                        <c:import
                        url="position.jsp?utype=Char.SpatialAxis.Coverage.Support.Area&tipSide=right"/>
                        <c:import
                        url="number.jsp?utype=Char.SpatialAxis.Coverage.Bounds.Extent.diameter&tipSide=right"/>
                        <c:import
                        url="number.jsp?utype=Char.SpatialAxis.Resolution.refval.value&tipSide=right"/>
                        <c:import
                        url="number.jsp?utype=Char.SpatialAxis.numBins1&tipSide=right"/>
                        <c:import
                        url="number.jsp?utype=Char.SpatialAxis.numBins2&tipSide=right"/>
                        <c:import
                        url="boolean.jsp?name=Char.SpatialAxis.DOWNLOADCUTOUT&tipSide=right"/>

                  </div>
                </div>
            </div>

            <div class="col-md-3 search-category">
                <div class="panel panel-default">
                    <div class="panel-heading"><fmt:message
                            key="TIME_POLARIZATION_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
                    <div class="panel-body search-constraints">

                        <c:import
                        url="date.jsp?utype=Char.TimeAxis.Coverage.Bounds.Limits&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.TimeAxis.Coverage.Support.Extent&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.TimeAxis.Resolution.refval.value&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.TimeAxis.numBins&tipSide=left"/>
                        <c:import
                        url="text_formgroup.jsp?utype=Char.PolarizationAxis.stateList&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.PolarizationAxis.numBins&tipSide=left"/>

                    </div>
                </div>
            </div>


            <div class="col-md-3 search-category">
                <div class="panel panel-default">
                    <div class="panel-heading"><fmt:message
                            key="SPECTRAL_CONSTRAINT_LABEL" bundle="${langBundle}"/></div>
                    <div class="panel-body search-constraints">

                        <c:import
                        url="energy.jsp?utype=Char.SpectralAxis.Coverage.Bounds.Limits&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.SpectralAxis.Resolution.ResolPower.refval&tipSide=left"/>
                        <c:import
                        url="number.jsp?utype=Char.SpectralAxis.numBins&tipSide=left"/>
                        <c:import
                        url="boolean.jsp?name=Char.SpectralAxis.DOWNLOADCUTOUT&tipSide=left"/>

                    </div>
                </div>
            </div>

        <div class="clear"></div>
        </div>

        <c:import
        url="hierarchy.jsp?utype=DataID.Collection/Provenance.ObsConfig.Facility.name/Provenance.ObsConfig.Instrument.name/ObsDataset.calibLevel/ObsDataset.dataProductType&modelDataSource=obscore"/>

        <%--<div class="equalize margin-top-large margin-bottom-none">--%>
        <%--<div class="span-3 row-start form-inline">--%>
        <%--<input type="submit"--%>
        <%--value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />"--%>
        <%--data-rel="popup" data-dismissible="false"--%>
        <%--data-position-to="window" data-inline="true"--%>
        <%--data-transition="pop" data-corners="true" data-shadow="true"--%>
        <%--data-iconshadow="true" data-wrapperels="span" data-theme="c"--%>
        <%--aria-haspopup="true" aria-owns="queryOverlay"--%>
        <%--class="submit-obscore-query button button-accent ui-btn"/>--%>
        <%--<input type="reset" data-role="none"--%>
        <%--value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />"--%>
        <%--class="reset-obscore-query-form button ui-btn"/>--%>
        <%--</div>--%>
        <%--<div class="span-5 row-end"></div>--%>
        <%--<div class="clear"></div>--%>
        <%--</div>--%>


        <div class="col-sm-12 button-holder">
            <button type="submit" class="btn btn-primary" value="<fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />" >
                <fmt:message key="SEARCH_BUTTON_LABEL" bundle="${langBundle}" />
            </button>
            <button type="reset" class="btn btn-default" value="<fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />" >
                <fmt:message key="RESET_BUTTON_LABEL" bundle="${langBundle}" />
            </button>
        </div>

    </form>
</div>