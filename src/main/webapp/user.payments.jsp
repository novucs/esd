<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/makepayment" %>
<script src="${pageContext.request.contextPath}/js/user.payments.js"></script>
<script src="https://js.stripe.com/v3/"></script>

<div class="container">
    <div class="row">
        <div class="nice-container col s12 card">
            <div class="card-content text-center">
                <div class="card-title">
                    <c:choose>
                        <c:when test="${payContext eq 'PAY_SUCCESS'}">
                            <h4>Payment received</h4>
                        </c:when>
                        <c:when test="${payContext eq 'PAY_FAIL'}">
                            <h4>Error processing your payment</h4>
                        </c:when>
                        <c:otherwise>
                            <h4>Make a payment</h4>
                        </c:otherwise>
                    </c:choose>
                </div>
                ${empty PAY_CONTEXT ? "" : "<div>" + PAY_CONTEXT + "</div>"}
                <c:choose>
                    <c:when test="${payContext eq 'PAY_SUCCESS'}">
                        <t:success/>
                    </c:when>
                    <c:when test="${payContext eq 'PAY_FAIL'}">
                        <t:fail/>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${payContext eq 'PAY_APPLICATION'}">
                                <t:makepayment
                                        noPaymentMessage="Your account is in credit and your application is being processed."
                                        paymentTitle="Make a payment for your new membership"
                                        paymentMessage="Please enter your details below. You will be charged"
                                />
                            </c:when>
                            <c:when test="${payContext eq 'PAY_MEMBERSHIP'}">
                                <t:makepayment
                                        noPaymentMessage="Your account is in good shape!"
                                        paymentTitle="Pay your membership fees"
                                        paymentMessage="You have an outstanding balance of"
                                />
                            </c:when>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
