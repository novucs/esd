<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<% Boolean hasSession =
        ((Session) (request.getSession().getAttribute("session"))).getUser() != null; %>

<!DOCTYPE html>
<html>
<head>
    <title><%= request.getAttribute("title")%>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet"
          href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link rel="stylesheet" href="css/site.css">
    <link href="//fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<header>
    <nav class="navigation-bar">
        <div class="nav-wrapper">
            <a href="." class="brand-logo">
                <span>XYZ</span>
                <span>Drivers Association</span>
            </a>
            <% if (hasSession) { %>
            <ul class="right hide-on-med-and-down">
                <li><a href="#">Member Dashboard</a></li>
                <li><a href="#">Admin Dashboard</a></li>
                <!-- Dropdown Trigger -->
                <li><a class="dropdown-trigger" href="" data-target="dropdown"><i
                        class="large material-icons">account_box</i></a></li>
            </ul>
            <% } %>
            <ul id="dropdown" class="dropdown-content">
                <li><a>User Settings</a></li>
                <li><a>Reset Password</a></li>
                <li><a href="logout">Logout</a></li>
            </ul>
        </div>
    </nav>
</header>
<main>
    <jsp:include page="<%= request.getAttribute("page").toString()%>"/>
</main>
<footer class="page-footer">
    <div class="row">
        <div class="col s12 center-align">
                    <span id="group-info">
                        UFCF85-30-3 - Group 10 - Assignment
                    </span>
        </div>
    </div>
</footer>

<!-- Scripts -->
<script src="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="js/site.js"></script>
<script src="js/error.js"></script>
<% List<String> errors = (List<String>) request.getAttribute("errors"); %>
<% if (errors != null && errors.size() > 0) { %>
<% for (String message : errors) { %>
<script type="application/javascript">
  errorModule.displayError('<%=message%>');
</script>
<% } %>
<% } %>
</body>
</html>

