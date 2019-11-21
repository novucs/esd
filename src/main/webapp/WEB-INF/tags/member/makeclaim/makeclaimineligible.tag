<%@taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="messageText" type="java.lang.String" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>

<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <h6 style="color: red;"/>Account ineligible to make a new claim.</h6>
            <br/>
            <div>${messageText}.</div>
            <br/>
            <div><a href="${linkUrl}">Click here</a> to ${linkText}.</div>
        </div>
    </div>
</div>