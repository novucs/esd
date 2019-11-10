<%@ page import="net.novucs.esd.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="css/login.css">
<div class="container">
    <div class="row">
        <div id="register-fail-component-container" class="col s6 push-s3 rounded-container">
            <div class="row">
                <div class="col s12 center-align">
                    <h4>Login Success</h4>
                </div>
            </div>
            <div class="row">
                <div class="col s12 center-align">
                    You are currently logged in as
                    <%= ((User) request.getAttribute("user")).getName() %>,
                    you may now logout by clicking <a href="logout">here</a>.
                </div>
            </div>
        </div>
    </div>
</div>