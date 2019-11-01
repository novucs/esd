<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Homepage</title>
    <link rel="stylesheet" href="css/homepage.css">
</head>

<body>
<main>
    <ul id="dropdown" class="dropdown-content">
        <li><a>User</a></li>
        <li><a>Admin</a></li>
    </ul>
    <nav class="navigation-bar">
        <div class="nav-wrapper">
            <a href="#!" class="brand-logo">XYZ Drivers Association</a>
            <ul class="right hide-on-med-and-down">
                <li><a href="/members">Member Dashboard</a></li>
                <li><a href="/admin">Admin Dashboard</a></li>
                <!-- Dropdown Trigger -->
                <li><a class="dropdown-trigger" href="" data-target="dropdown">Switch View<i
                        class="material-icons right"></i></a></li>
            </ul>
        </div>
    </nav>
</main>
<script src="js/homepage.js"></script>
<footer class="page-footer">
    <div class="container">
        <a class="grey-text text-lighten-4 right" href="/app/login">Login</a>
        Version 1.0.0
    </div>
</footer>
</body>
</html>



