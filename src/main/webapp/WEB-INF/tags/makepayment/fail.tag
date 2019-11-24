<%@taglib prefix="t" tagdir="/WEB-INF/tags/makepayment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6 style="color: red;"/>You have not been charged.</h6>
            <br/>
            <div><a href="${pageContext.request.contextPath}/makepayment">Click here</a> to try again.</div>
        </div>
    </div>
</div>