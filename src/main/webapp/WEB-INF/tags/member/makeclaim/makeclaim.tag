<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="claimStatus" type="java.lang.String" %>
<%@ attribute name="membershipStatus" type="java.lang.String" %>
<%@ attribute name="expiredDate" type="java.lang.String" %>
<%@ attribute name="claimFrom" type="java.lang.String" %>


<div>
<c:choose>
    <c:when test="${claimStatus == 'CREATE'}">
        <c:choose>
            <c:when test="${membershipStatus == 'NONE'}">
                <t:makeclaimineligible
                  messageText="In order to make a claim you need to purchase a membership"
                  linkText="purchase your membership"
                  linkUrl="/makepayment"
                  />
            </c:when>
            <c:when test="${membershipStatus == 'EXPIRED'}">
                <t:makeclaimineligible
                    messageText="Your membership expired on ${expiredDate}. To make a claim you must renew your membership"
                    linkText="renew your membership"
                    linkUrl="/makepayment"
                    />
            </c:when>
            <c:when test="${membershipStatus == 'SUSPENDED'}">
                <t:makeclaimineligible
                    messageText="Your account is currently suspended, contact the Administrator for more details"
                    linkText="contact the administrator"
                    linkUrl="mailto:admin@esd.net"
                    />
            </c:when>
            <c:when test="${membershipStatus == 'FULL_USED'}">
                <t:makeclaimineligible
                    messageText="You have used your claim quota for this membership period"
                    linkText="view your claims"
                    linkUrl="/claims"
                    />
            </c:when>
            <c:when test="${membershipStatus == 'FULL_WAIT'}">
                <t:makeclaimineligible
                    messageText="New members must wait 6 months before making a claim, you will be eligible to make a claim on ${claimFrom}"
                    linkText="return to your Dashboard"
                    linkUrl="/dashboard"
                    />
            </c:when>
            <c:otherwise>
                <t:makeclaimcreate />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${claimStatus == 'SUCCESS'}">
        <t:makeclaimsuccess />
    </c:when>
    <c:otherwise>
        <t:makeclaimfail />
    </c:otherwise>
</c:choose>
</div>
