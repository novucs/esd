<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s12 card">
            <div class="card-content text-center">
                <div class="card-title"><h4>Make a payment</h4></div>
                <div class="row">
                    <div class="col s12 center-align">
                        <div class="row">
                            <div class="row">
                                <div class="col s12 center-align">
                                    <div class="row">
                                        <h6>No payment required</h6>
                                        <br/>
                                        <div>Your account is in good shape!</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col s4 left"><a href="javascript:history.back()">Go Back</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/user.payments.js"></script>
