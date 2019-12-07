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
                                <br/>
                                <div class="tab">
                                  <button class="tablinks" onclick="choosePaymentMethod(event, 'card')">Card Payment</button>
                                  <button class="tablinks" onclick="choosePaymentMethod(event, 'offline')">Offline Payment</button>
                                </div>
                                <br/>
                                <div id="card" class="tabcontent">
                                    <h6/>${paymentTitle}</h6>
                                    <div>
                                        ${paymentMessage} &pound;${amountOwed}
                                    </div>
                                    <br/>
                                    <t:form />
                                </div>
                                <div id="offline" class="tabcontent">
                                    <h6/>Make a payment by bank transfer</h6>
                                    <p>
                                        Annual membership fee is &pound;${amountOwed}
                                    </p>
                                    <p>
                                        Please make a bank transfer and provide the reference.
                                    </p>
                                    <t:offline />
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
            <div class="col s4 left"><a href="javascript:history.back()">Go Back</a></div>
        </div>
    </div>
</div>