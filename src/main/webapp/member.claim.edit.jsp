<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container">
    <div class="nice-container col s8 push-s2 card row">
        <div class="col s2"></div>
        <div class="card-content col s8">
            <div class="card-title">
                Edit your claim
            </div>
            <h6>
                Update the value and/or rationale of your claim.
            </h6>
            <br/>
            <p>
                The maximum amount for this claim is &pound;<fmt:formatNumber type="number" maxFractionDigits="2" value="${maxClaimValue}"/>.
            </p>
            <br/>
            <form method="post" action="editclaim">
                <div class="row">
                    <div class="input-field col s4">
                        <i class="material-icons prefix">account_balance_wallet</i>
                        <input name="claim-value"
                               id="claim-value"
                               min="10"
                               max="${maxClaimValue}"
                               value="${claimValue}"
                               type="number"
                               step=".01"
                               class=validate
                               message=""
                               onchange="maintainFormat()"
                               required
                        />
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
                                required>${rationale}</textarea>
                        <label for="claim-rationale">Rationale</label>
                        <input
                              type="hidden"
                              id="claimId"
                              name="claimId"
                              value="${claimId}"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col s2 right">
                        <button id="submit-claim-button"
                                class="waves-effect btn secondary-content right-align orange"
                                type="submit" name="editclaim">
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
