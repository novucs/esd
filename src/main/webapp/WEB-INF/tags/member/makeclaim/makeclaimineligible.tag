<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="remainingClaims" type="java.lang.String" %>
<%@ attribute name="maxClaimValue" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6/>
            You can make ${remainingClaims} more claim${remainingClaims == 1 ? "'s" : ''} this year.
            </h6>
            <br/>
            <div>

            </div>
            <br/>
            <div><a href="${linkUrl}">Click here</a> to ${linkText}.</div>
        </div>
    </div>
</div>