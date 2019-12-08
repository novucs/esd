<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s12 card">
            <div class="card-content text-center">
                <div class="card-title">
                    <h4>Error processing your payment</h4>
                </div>
                <div class="row">
                    <div class="col s12 center-align">
                        <div class="row">
                            <h6 class="red-text">You have not been charged.</h6>
                            <br/>
                            <div>
                                <a href="${pageContext.request.contextPath}/makepayment">Click here</a> to try again.
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/user.payments.js"></script>
