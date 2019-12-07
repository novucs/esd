<%@taglib prefix="t" tagdir="/WEB-INF/tags/makepayment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@attribute name="amountOwed" type="java.lang.String"%>
<%@ attribute name="noPaymentMessage"%>
<%@ attribute name="paymentTitle" required="true" %>
<%@ attribute name="paymentMessage" required="true" %>


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
                                <div>${noPaymentMessage}</div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <div class="col s12 center-align">
                            <div class="row">
                                <h6/>${paymentTitle}</h6>
                                <br/>
                                <div>
                                 ${paymentMessage} &pound;${amountOwed}
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