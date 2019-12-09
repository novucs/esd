<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test='${disable == "Disabled"}'>
        <div class="row top-margin-30">
            <div class="col s12 center-align">
                <h3>
                    You cannot charge users until ${earliestDate}
                </h3>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row top-margin-30">
            <div class="col s12 center-align">
                <h3>
                    Calculate Annual Charge
                </h3>
            </div>
        </div>
        <div class="row top-margin-30">
            <hr/>
            <div class="col s6 center-align">
                The total amount claimed in the past year is ${claimSum}
            </div>
            <div class="col s6 center-align">
                The total number of members is ${numberOfMembers}
            </div>
        </div>
    </c:otherwise>
</c:choose>
<form method="post" id="slider-form" class="row top-margin-30">
    <div class="col s12">
        <div class="row">
            <div class="col s6 push-s3 center-align">
                <div class="row">
                    <h5>
                        Premium to charge members: &pound;
                        <span id="amount-to-charge">
                            ${maxCharge}
                        </span>
                    </h5>
                </div>
                <p class="range-field">
                    <input hidden name="max-charge" id="max-charge" value="${maxCharge}" />
                    <input ${disable} name="range" type="range" id="range" min="0" max="100" value="100" step="1"/>
                </p>
            </div>
        </div>
        <div class="row">
            <div class="col s6 push-s3 input-field center-align">
                <button ${disable} type="submit" class="waves-effect btn waves-light xyz-button">
                    Charge Members
                </button>
            </div>
        </div>
    </div>
</form>
<script src="${pageContext.request.contextPath}/js/admin.annualcharge.js"></script>