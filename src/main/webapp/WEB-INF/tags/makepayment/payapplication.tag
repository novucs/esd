<%@taglib prefix="t" tagdir="/WEB-INF/tags/makepayment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="linkText" type="java.lang.String" %>
<%@ attribute name="linkUrl" type="java.lang.String" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="amountOwed" type="java.lang.String" %>


<div class="row">
    <div class="col s12 center-align">
        <div class="row">
            <c:choose>
                <c:when test="${amountOwed == 0}">
                    <div class="row">
                        <div class="col s12 center-align">
                            <div class="row">
                                <h6/>No payment required</h6>
                                <br/>
                                <div>Your account is in credit and your application is being processed.</div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <div class="col s12 center-align">
                            <div class="row">
                                <h6/>Make a payment for your new membership</h6>
                                <br/>
                                <div>
                                 Please enter your details below. You will be charged &pound;${amountOwed}
                                </div>
                                <br/>
                                <t:form />
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
            <div class="col s4 left"><a href="javascript:history.back()">Go Back</a></div>
        </div>
    </div>
</div>