<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@attribute name="username" type="java.lang.String"%>

<div class="row">
    <div id="register-fail-component-container" class="col s6 push-s3 rounded-container">
        <div class="row">
            <div class="col s12 center-align">
                <h4>Registration Unsuccessful</h4>
            </div>
        </div>
        <div class="row">
            <div class="col s12 center-align">
                An account already exists for <strong>${username}</strong> try
                logging in with it <a href="login">here</a>.
            </div>
        </div>
    </div>
</div>