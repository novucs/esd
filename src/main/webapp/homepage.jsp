<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet"
          href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
    <link href="//fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>


<body>
<div>
    <nav class="navigation-bar">
        <div class="nav-wrapper">
            <ul id="nav-mobile" class="right hide-on-med-and-down">
                <li><a href="logout">Login</a></li>
                <li><a href="register">Register</a></li>
            </ul>
        </div>
    </nav>
    <div class="container align-center">
        <div class="card">
            <div class="card-content text-center">
                <div>
                    <h1 class="brand-logo">
                        <span>XYZ</span>
                        <span>Drivers Association</span>
                    </h1>
                </div>
                <div class="text-center">
                    <a href="logout" class="waves-effect waves-light btn-large ">Get Started</a>
                </div>
            </div>
        </div>
    </div>


    <footer class="page-footer">
        <div class="row">
            <div class="col s12 center-align">
                <span id="group-info">UFCF85-30-3 - Group 10 - Assignment</span>
            </div>
        </div>
    </footer>
</div>
</body>
</html>
