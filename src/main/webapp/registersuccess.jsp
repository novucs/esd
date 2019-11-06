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
                      Account created for <strong><%= request.getParameter("username") %></strong>
                    </div>
                    <br></br>
                    <div class="col s12 center-align">
                      Your temporary password is: <strong><%= request.getAttribute("password") %></strong>
                    </div>
                    <br></br>
                    <div class="col s12 center-align">
                        <div style="color: red;">
                          Please ensure you make a note of this password before proceeding to <a href="login">login</a>
                        <div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>