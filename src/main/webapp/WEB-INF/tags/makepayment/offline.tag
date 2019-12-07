<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="row">
    <div class="col s12 center">
        <div class="col s3 left"></div>
        <div class="col s6 center-align">
            <form action="makepayment" method="post" id="payment-form">
              <div class="form-row">
                  <div class="input-field">
                      <input name="reference" id="reference"
                             type="text" required/>
                      <label for="reference">Reference</label>
                  </div>
              </div>
              <br/>
              <button class="waves-effect btn secondary-content left-align">Submit</button>
            </form>
        </div>
        <div class="col s3 left"></div>
    </div>
</div>