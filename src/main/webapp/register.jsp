<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<div class="container">
    <div class="row">
        <c:choose>
            <c:when test="${registerStatus == 'success'}">
                <t:registersuccess />
            </c:when>
            <c:when test="${registerStatus == 'fail'}">
                <t:registerfail />
            </c:when>
            <c:otherwise>
                <t:registerform />
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script type="text/javascript" src="js/register.js"></script>