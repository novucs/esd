<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6/>Your claim has been submitted for processing.</h6>
            <br/>
            <div><a href="${pageContext.request.contextPath}/claims">Click here</a> to view the status of your claim.</div>
        </div>
    </div>
</div>