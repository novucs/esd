<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                    Generate Report
                </button>
            </div>
        </div>
    </div>
</form>
<c:choose>
    <c:when test="${showReport != null}">
        <div class="row rounded-container">
            <div class="col s12 text-center">
                <div class="row">
                    <div class="col s12 center-align">
                        <h4>
                            Turnover Report
                        </h4>
                        <div class="row top-margin-30">
                            <div class="col s12 center-align">
                                Showing figures from
                                <strong>
                                        ${fromFormatted}
                                </strong>
                                to
                                <strong>
                                        ${toFormatted}
                                </strong>
                            </div>
                        </div>
                    </div>
                </div>
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
                <hr/>
                <div id="claims" class="row top-margin-30">
                    <div  class="col s12 center-align">
                    <c:choose>
                        <c:when test="${fn:length(claims) > 0}">
                            <c:forEach var="claim" items="${claims}">
                                <div class="row">
                                    <div class="col s3">
                                        <label>
                                            Id:&nbsp;
                                        </label>
                                        <span>
                                                ${claim.id}
                                        </span>
                                    </div>
                                    <div class="col s3">
                                        <label>
                                            Claim Date:&nbsp;
                                        </label>
                                        <fmt:parseDate value="${claim.claimDate.toLocalDate()}"
                                                       type="date" pattern="yyyy-MM-dd"
                                                       var="parsedClaimDate"/>
                                        <fmt:formatDate value="${parsedClaimDate}" type="date"
                                                        pattern="dd-MM-yyyy" var="formatClaimDate"
                                        />
                                            ${formatClaimDate}
                                    </div>
                                    <div class="col s3">
                                        <label>
                                            Claim Amount:&nbsp;
                                        </label>
                                        <span>
                                            &pound;${claim.pounds}.${claim.pence == 0 ? '00' : claim.pence}
                                        </span>
                                    </div>
                                    <c:set var="claimStatusColor" scope="session"
                                           value="${claim.status.equals(CS_APPROVED) ?
                                            'green-text' : (claim.status.equals(CS_PENDING) ?
                                            'orange-text' : 'red-text')}"/>
                                    <div class="col s3">
                                        <label>
                                            Status:&nbsp;
                                        </label>
                                        <span class="${claimStatusColor}">
                                                ${claim.status}
                                        </span>
                                    </div>
                                    <br/>
                                </div>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                </div>
                </div>
            </div>
        </div>
    </c:when>
</c:choose>
<script src="${pageContext.request.contextPath}/js/admin.reporting.js"></script>