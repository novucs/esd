<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="username" type="java.lang.String"%>
<%@attribute name="password" type="java.lang.String"%>

<div class="row">
  <div id="register-success-component-container" class="col s6 push-s3 rounded-container">
      <div class="row">
          <div class="col s12 center-align">
              <h4>
                  Registration Successful
              </h4>
          </div>
      </div>
      <div class="row">
          <div class="col s12">
              <div class="col s12 center-align">
                  Account created for
                  <strong>
                      ${username}
                  </strong>
              </div>
              <br/>
              <div class="col s12 center-align">
                  Your temporary password is:
                  <strong>
                      ${password}
                  </strong>
              </div>
              <br/>
              <div class="col s12 center-align">
                  <div class="red-text">
                      Please ensure you make a note of this password before proceeding to
                      <a href="login">login</a>
                  </div>
              </div>
          </div>
      </div>
  </div>
</div>
