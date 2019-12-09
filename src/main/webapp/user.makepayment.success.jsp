<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s12 card">
            <div class="card-content text-center">
                <div class="card-title">
                    <h4>Payment received</h4>
                </div>
                <div class="row">
                    <div class="col s12 center-align">
                        <div class="row">
                            <h6>
                                Your payment has processed successfully.
                                You have been charged &pound;${charge}.
                            </h6>
                            <br/>
                            <div><a href="${pageContext.request.contextPath}/payments">Click here</a> to view your payments.</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/user.payments.js"></script>
<script src="https://js.stripe.com/v3/"></script>
