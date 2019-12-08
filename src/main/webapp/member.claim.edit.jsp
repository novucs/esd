<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s8 push-s2 card">
            <div class="card-content">
                <div class="card-title">Edit your claim</div>
                <h6>
                    Enter the new value of your claim.
                </h6>
                <p>
                    The maximum amount for this claim is &pound;<fmt:formatNumber type="number" maxFractionDigits="2" value="${maxClaimValue}"/>.
                </p>
                <form method="post" action="editclaim">
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
                                                   value="${claimValue}"
                                                   type="number"
                                                   step=".01"
                                                   class=validate
                                                   message=""
                                                   onchange="maintainFormat()"
                                                   required
                                            />
                                            <input
                                                  type="hidden"
                                                  id="claimId"
                                                  name="claimId"
                                                  value="${claimId}"/>
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
                                            type="submit" name="editclaim">
                                        Submit
                                    </button>
                                </div>
                                <div class="col s2 right"></div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<div class="card-action">
  <a href="${pageContext.request.contextPath}/dashboard">Back to dashboard</a>
</div>