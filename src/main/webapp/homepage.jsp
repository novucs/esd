<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link rel="stylesheet" href="css/homepage.css">
    <link href="//fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>


<body background="city%20and%20car.jpeg">
<div>
    <!-- Navbar goes here -->
    <nav class="navigation-bar">
        <div class="nav-wrapper">
            <a href="/app/homepage" class="brand-logo"></a>
            <ul id="nav-mobile" class="right hide-on-med-and-down">
                <li><a href="/app/logout">Login</a></li>
                <li><a href="/app/registeration">Register</a></li>
            </ul>
        </div>
    </nav>

    <div>
        <div class="rounded-container">
            <div>
                <h1 href="/homepage" class="brand-logo">
                    <span>Welcome to</span>
                    <span>XYZ</span>
                    <span>Drivers Association</span>
                </h1>
            </div>
            <div id="started">
                <a href="/app/logout" class="waves-effect waves-light btn-large started">Get
                    Started</a>
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
