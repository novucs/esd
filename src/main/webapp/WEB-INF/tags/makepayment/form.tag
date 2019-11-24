<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="row">
    <div class="col s12 center">
        <div class="col s3 left"></div>
        <div class="col s6 center-align">
            <form action="makepayment" method="post" id="payment-form">
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
              <button class="waves-effect btn secondary-content left-align">Pay &pound;${amountOwed}</button>
            </form>
            <script type="text/javascript">
                var amountOwed = <%=request.getAttribute("amountOwed")%>;
                var reference = "<%=request.getAttribute("payContext")%>";
                initStripe(amountOwed, reference);
            </script>
        </div>
        <div class="col s3 left"></div>
    </div>
</div>