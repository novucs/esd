<%@taglib prefix="t" tagdir="/WEB-INF/tags/makepayment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6/>Your payment has processed successfully. You have been charged &pound;${charge}.</h6>
            <br/>
            <div><a href="${pageContext.request.contextPath}/payments">Click here</a> to view your payments.</div>
        </div>
    </div>
</div>