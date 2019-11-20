<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6 style="color: red;"/>There was an error processing your claim.</h6>
            <br/>
            <div>Please check your open claims before trying again.</div>
            <br/>
            <div><a href="${pageContext.request.contextPath}/claims">Click here</a> to view your claims.</div>
        </div>
    </div>
</div>