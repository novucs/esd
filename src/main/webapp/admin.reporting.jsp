<%@ page import="net.novucs.esd.model.ReportType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="CLAIMS" value="<%=ReportType.Claims.name()%>"/>
<c:set var="MEMBERSHIP" value="<%=ReportType.Membership.name()%>"/>
<c:set var="ALL" value="<%=ReportType.AllRevenue.name()%>"/>

<form class="row" method="post" name="reporting-form">
    <div class="col s12 ">
        <div class="row">
            <div class="col s3">
                <h6>
                    Select Dates
                </h6>
            </div>
        </div>
        <div class="row">
            <div class="col s4 input-field">
                <label for="date-from">From: </label>
                <input id="date-from" name="from" type="text" class="datepicker"
                       placeholder="Start date" value="${from}"/>
            </div>
            <div class="col s4 input-field">
                <label for="date-to">To: </label>
                <input id="date-to" type="text" name="to" class="datepicker"
                       placeholder="End date" value="${to}"/>
            </div>
            <div class="col s4 input-field">
                <button type="submit" class="waves-effect btn waves-light xyz-button">
                    Submit
                </button>
            </div>
        </div>
    </div>
</form>

<script type="text/javascript">
  const showDates = "${showReport == null}";
</script>

<c:choose>
    <c:when test="${showReport != null}">
        <div class="row rounded-container">
            <div class="col s12 text-center">
                <div class="row">
                    <div class="col s12 center-align">
                        <h4>
                            Turnover Report
                        </h4>
                    </div>
                </div>
                <hr/>
                <div class="row top-margin-30">
                    <div class="col s4 center-align">
                        Recieved from memberships: &pound;${membershipSum}
                    </div>
                    <div class="col s4 center-align">
                        Paid out to claims: &pound;${claimSum}
                    </div>
                    <div class="col s4 center-align ${turnover < 0 ? 'red-text' : 'green-text'}">
                        Turnover: &pound;${turnover}
                    </div>
                </div>
                <div class="row top-margin-30">
                    <div class="col s12 center-align">
                        Report shows figures from
                        <strong>
                                ${fromFormatted}
                        </strong>
                        to
                        <strong>
                                ${toFormatted}
                        <strong/>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
</c:choose>
<script src="${pageContext.request.contextPath}/js/admin.reporting.js"></script>