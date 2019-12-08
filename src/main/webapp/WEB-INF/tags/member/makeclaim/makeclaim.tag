<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="claimStatus" type="java.lang.String" %>
<%@ attribute name="membershipStatus" type="java.lang.String" %>
<%@ attribute name="expiredDate" type="java.lang.String" %>
<%@ attribute name="claimFrom" type="java.lang.String" %>


<div>
<c:choose>
    <c:when test="${claimStatus == 'CREATE'}">
        <t:makeclaimcreate />
    </c:when>

    <c:when test="${claimStatus == 'SUCCESS'}">
        <t:makeclaimsuccess />
    </c:when>

    <c:otherwise>
        <t:makeclaimfail />
    </c:otherwise>
</c:choose>
</div>
