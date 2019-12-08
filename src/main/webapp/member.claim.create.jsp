<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>

<div class="container">
    <div class="nice-container col s8 push-s2 card row">
        <div class="col s2"></div>
        <div class="card-content col s8">
            <div class="card-title">Make a new claim</div>
            <c:choose>
                <c:when test="${remainingClaims > 0}">
                    <p>You can make ${remainingClaims} more claims this year.</p>
                </c:when>
                <c:otherwise>
                    <p class="red-text">You have made the maximum number of claims this year.</p>
                    <p class="red-text">
                        You are welcome to submit additional claims however likelihood of
                        approval is limited.
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
            <p>The maximum amount for this claim is &pound;${maxClaimValue}.</p>
            <form method="post" action="makeclaim">
                <div class="row">
                    <div class="input-field col s4">
                        <i class="material-icons prefix">account_balance_wallet</i>
                        <input id="claim-value"
                               name="claim-value"
                               min="10"
                               max="${maxClaimValue}"
                               type="number"
                               class="validate"
                               required>
                        <label for="claim-value">£ amount</label>
                    </div>
                    <div class="input-field col s8">
                        <i class="material-icons prefix">edit</i>
                        <textarea
                                id="claim-rationale"
                                name="claim-rationale"
                                class="materialize-textarea validate"
                                minlength="32"
                                maxlength="255"
                                required></textarea>
                        <label for="claim-rationale">Rationale</label>
                    </div>
                </div>
                <div class="row">
                    <div class="col s12 center">
                        <button id="submit-claim-button"
                                class="waves-effect btn secondary-content right-align orange"
                                type="submit" name="makeclaim">
                            Submit
                        </button>
                    </div>
                </div>
            </form>
            <div class="card-action">
                <a href="${pageContext.request.contextPath}/dashboard">Back to dashboard</a>
            </div>
        </div>
    </div>
</div>
