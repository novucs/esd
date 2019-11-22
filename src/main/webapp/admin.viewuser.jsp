<%@ page import="net.novucs.esd.constants.ClaimStatus" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="CS_APPROVED" value="<%=ClaimStatus.APPROVED%>"/>
<c:set var="CS_PENDING" value="<%=ClaimStatus.PENDING%>"/>
<c:set var="CS_REJECTED" value="<%=ClaimStatus.REJECTED%>"/>

<div>
    <form class="row" method="post" action="" role="form">
        <div class="col s12 rounded-container info-section">
            <div class="row">
                <div class="col s12">
                    <h4>User Info</h4>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <input name="name" id="name" type="text" class="validate"
                           value="${user.name}" required disabled>
                    <label for="name">Name</label>
                </div>
                <div class="input-field col s6">
                    <input name="date_of_birth" id="date_of_birth" type="date" class="validate"
                           value="${user.dateOfBirth.toLocalDate()}" required disabled>
                    <label class="active" for="date_of_birth">Date of Birth</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <input name="email" id="email" type="email" class="validate"
                           value="${user.email}" required disabled>
                    <label for="email">Email</label>
                </div>
                <div class="input-field col s6">
                    <label>
                        Roles
                    </label>
                    <input type="text" disabled value="${roleText}">
                </div>
            </div>
        </div>
        <hr/>
        <div class="col s12 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <ul class="tabs">
                        <li class="tab col s6"><a class="active" href="#memberships">Memberships</a>
                        </li>
                        <li class="tab col s6"><a href="#claims">Claims</a></li>
                    </ul>
                </div>
                <div id="memberships" class="col s12 center-align">
                    <c:choose>
                        <c:when test="${fn:length(memberships) > 0}">
                            <c:forEach var="membership" items="${memberships}">
                                <div class="row">
                                    <div class="col s3">
                                        <label>
                                            ID:&nbsp;
                                        </label>
                                            ${membership.id}
                                    </div>
                                    <div class="col s3">
                                        <label>
                                            Start Date:&nbsp;
                                        </label>
                                        <fmt:parseDate value="${membership.startDate.toLocalDate()}"
                                                       type="date" pattern="yyyy-MM-dd"
                                                       var="paresedStartDate"/>
                                        <fmt:formatDate value="${paresedStartDate}" type="date"
                                                        pattern="dd-MM-yyyy" var="formatStartDate"
                                        />
                                            ${formatStartDate}
                                    </div>
                                    <div class="col s3">
                                        <label>
                                            Can Make Claims From:&nbsp;
                                        </label>
                                        <fmt:parseDate
                                                value="${membership.claimFromDate.toLocalDate()}"
                                                type="date" pattern="yyyy-MM-dd"
                                                var="parsedClaimDate"/>
                                        <fmt:formatDate value="${parsedClaimDate}" type="date"
                                                        pattern="dd-MM-yyyy" var="formatClaimDate"
                                        />
                                            ${formatClaimDate}
                                    </div>
                                    <c:set var="statusColor" scope="session"
                                           value="${membership.status.equals('ACTIVE') ?
                                            'green-text' : 'red-text'}"/>
                                    <div class="col s3">
                                        <label>
                                            Status:&nbsp;
                                        </label>
                                        <span class="${statusColor}">
                                                ${membership.status}
                                        </span>
                                    </div>
                                    <br/>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="row">
                                <div class="col s12">
                                    <h6>
                                        User has no memberships.
                                    </h6>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="claims" class="col s12 center-align">
                    <c:choose>
                        <c:when test="${fn:length(claims) > 0}">
                            <c:forEach var="claim" items="${claims}">
                                <div class="row">
                                    <div class="col s3">
                                        <label>
                                            ID:&nbsp;
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
                        <c:otherwise>
                            <div class="row">
                                <div class="col s12">
                                    <h6>
                                        User has made no claims.
                                    </h6>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <hr/>
    </form>
</div>
<script src="${pageContext.request.contextPath}/js/admin.viewuser.js"></script>
