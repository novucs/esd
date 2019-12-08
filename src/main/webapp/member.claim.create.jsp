<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>

<div class="container">
    <div class="row">
        <div class="nice-container col s8 push-s2 card">
            <div class="card-content">
                <div class="card-title">Make a new claim</div>
                <c:choose>
                    <c:when test="${remainingClaims > 0}">
                        <h6>
                            You can make ${remainingClaims} more claims this year.
                        </h6>
                    </c:when>
                    <c:otherwise>
                        <h6 class="red-text">
                            You have made the maximum number of claims this year.
                        </h6>
                        <p class="red-text">
                            You are welcome to submit additional claims however likelihood of
                            approval
                            is limited.
                        </p>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${remainingClaims < 2}">
                        <p>
                            The total value of claims submitted this year is
                            &pound;${membershipClaimValueToDate}.
                        </p>
                    </c:when>
                </c:choose>
                <p>
                    The maximum amount for this claim is &pound;${maxClaimValue}.
                </p>
                <form method="post" action="makeclaim">
                    <div class="row">
                        <div class="row">
                            <div class="col s12 center-align">
                                <div class="col s5 left"></div>
                                <div class="col s2 center">
                                    <div class="row" style="display: flex;">
                                        <div class="input-field" style="display: flex;">
                                            <p style="font-size: 14px;">&pound;&nbsp;</p>
                                            <input name="claim-value"
                                                   id="claim-value"
                                                   min="10"
                                                   max="${maxClaimValue}"
                                                   value=10.00
                                                   type="number"
                                                   step=".01"
                                                   class=validate
                                                   message=""
                                                   onchange="maintainFormat()"
                                                   required
                                            />
                                        </div>
                                    </div>
                                    <div class="col s5 right"></div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col s12 center-align">
                                    <div class="col s6 left"></div>
                                    <div class="col s4 center">
                                        <button id="submit-claim-button"
                                                class="waves-effect btn secondary-content right-align"
                                                type="submit" name="makeclaim">
                                            Submit
                                        </button>
                                    </div>
                                    <div class="col s2 right"></div>
                                </div>
                            </div>
                            <div class="col s8 left">Go back to your
                                <a href="${pageContext.request.contextPath}/dashboard">
                                    dashboard.</a>
                            </div>
                        </div>
                </form>
            </div>
        </div>
    </div>
</div>
