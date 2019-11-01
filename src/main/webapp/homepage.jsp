<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Homepage</title>
        <link rel="stylesheet" href="css/login.css">
    </head>
    <body>
        <!-- Dropdown Structure -->
        <ul id="dropdown" class="dropdown-content">
            <li><a href="#!">one</a></li>
            <li><a href="#!">two</a></li>
            <li class="divider"></li>
            <li><a href="#!">three</a></li>
        </ul>
        <nav class="navigation-bar">
            <div class="nav-wrapper">
                <a href="#!" class="brand-logo">XYZ Drivers Association</a>
                <ul class="right hide-on-med-and-down">
                    <li><a href="/members">Member Dashboard</a></li>
                    <li><a href="/admin">Admin Dashboard</a></li>
                    <!-- Dropdown Trigger -->
                    <li><a class="dropdown-trigger" href="" data-target="dropdown">Dropdown<i class="material-icons right"></i></a></li>
                </ul>
            </div>
        </nav>

    </body>
</html>



<script src="js/homepage.js"></script>
