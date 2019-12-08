<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/member/makeclaim" %>
<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>

<div class="container">
    <div class="row">
        <div class="nice-container col s8 push-s2 card">
            <div class="card-content">
                <div class="card-title">
                    Make a new claim
                </div>
                <p class="text-center"><t:makeclaim /></p>
            </div>
        </div>
    </div>
</div>
