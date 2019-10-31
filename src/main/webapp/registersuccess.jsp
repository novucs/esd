<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<body>
<div class="container">
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
                      Check emails for <%= request.getParameter("username") %> to confirm your account and <a href="login">proceed to login</a>.
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>