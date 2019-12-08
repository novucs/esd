<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container">
    <div class="row">
        <div class="nice-container col s12 card">
            <div class="card-content text-center">
                <div class="card-title"><h4>Make a payment</h4></div>
                <br/>
                <div class="tab">
                    <button class="tablinks" onclick="choosePaymentMethod(event, 'card')">
                        Card Payment
                    </button>
                    <button class="tablinks" onclick="choosePaymentMethod(event, 'offline')">
                        Offline Payment
                    </button>
                </div>
                <br/>
                <div id="card" class="tabcontent">
                    <h6>Pay your membership fees</h6>
                    <div>You have an outstanding balance of &pound;${fee}</div>
                    <br/>
                    <div class="col s3 left"></div>
                    <div class="col s6 center-align">
                        <form action="makepayment" method="post" id="stripe-payment-form">
                            <div class="form-row">
                                <div class="card-input-field">
                                    <label for="card-number">Card number</label>
                                    <div id="card-number">
                                        <!-- A Stripe Element will be inserted here. -->
                                    </div>
                                </div>
                            </div>
                            <div id="card-info" class="form-row">
                                <div id="payment-expiry" class="card-input-field">
                                    <label for="card-expiry">Card expiry</label>
                                    <div id="card-expiry">
                                        <!-- A Stripe Element will be inserted here. -->
                                    </div>
                                </div>
                                <div id="payment-cvc" class="card-input-field">
                                    <label for="card-cvc">Card CVC</label>
                                    <div id="card-cvc">
                                        <!-- A Stripe Element will be inserted here. -->
                                    </div>
                                </div>
                            </div>
                            <div class="form-row">
                                <!-- Used to display Element errors. -->
                                <div id="card-errors" role="alert">
                                    <!-- A Stripe Element will be inserted here. -->
                                </div>
                            </div>
                            <br/>
                            <button class="waves-effect btn secondary-content left-align">
                                Pay &pound;${fee}
                            </button>
                        </form>
                    </div>
                </div>
                <div id="offline" class="tabcontent">
                    <h6>Make a payment by bank transfer</h6>
                    <p>Annual membership fee is &pound;${fee}</p>
                    <p>Please make a bank transfer and provide the reference.</p>

                    <div class="col s3 left"></div>
                    <div class="col s6 center-align">
                        <form action="makepayment" method="post" id="offline-payment-form">
                            <div class="form-row">
                                <div class="input-field">
                                    <input name="reference" id="reference"
                                           type="text" required/>
                                    <label for="reference">Reference</label>
                                </div>
                            </div>
                            <br/>
                            <button class="waves-effect btn secondary-content left-align">Submit
                            </button>
                        </form>
                    </div>
                </div>
            </div>
            <div class="card-action"><a href="javascript:history.back()">Go Back</a>
            </div>
        </div>
    </div>
</div>
<script src="https://js.stripe.com/v3/"></script>
<script src="${pageContext.request.contextPath}/js/user.payments.js"></script>
<script type="text/javascript">
  const fee = parseInt('<%=request.getAttribute("fee")%>');
  const reference = '<%=request.getAttribute("payContext")%>';
  initStripe(fee, reference);
</script>
