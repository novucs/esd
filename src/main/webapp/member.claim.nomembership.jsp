<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s8 push-s2 card">
            <div class="card-content">
                <div class="card-title">Make a new claim</div>
                <h6 class="red-text">Account ineligible to make a new claim.</h6>
                <br/>
                <div>In order to make a claim you need to purchase a membership.</div>
                <br/>
                <div><a href="${pageContext.request.contextPath}/makepayment">Click here</a> to purchase your membership.</div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/makeclaim.js"></script>
