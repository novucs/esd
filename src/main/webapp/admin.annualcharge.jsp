<div class="row top-margin-30">
    <div class="col s12 center-align">
        <h3>
            Calculate Annual Charge
        </h3>
    </div>
</div>
<hr/>
<div class="row top-margin-30">
    <div class="col s6 center-align">
        The total amount claimed in the past year is ${claimSum}
    </div>
    <div class="col s6 center-align">
        The total number of members is ${numberOfMembers}
    </div>
</div>
<form id="slider-form" class="row top-margin-30" data-max-charge="${maxCharge}">
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
            <input type="range" id="percentage-charge" min="0" max="100" value="100" step="1"/>
        </p>
    </div>
</form>
<script src="${pageContext.request.contextPath}/js/admin.annualcharge.js"></script>