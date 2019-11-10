<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="container">
    <div class="row">
        <div id="register-fail-component-container" class="col s6 push-s3 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <h4>Registration Unsuccessful</h4>
                </div>
            </div>
            <div class="row">
                <div class="col s12 center-align">
                    An account already exists for <%= request.getParameter("username") %> try
                    logging in with it <a href="login">here</a>.
                </div>
            </div>
        </div>
    </div>
</div>