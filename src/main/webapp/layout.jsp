<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title><%= request.getAttribute("title")%>
        </title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Compiled and minified CSS -->
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
        <link rel="stylesheet" href="css/site.css">
    </head>
    <body>
        <ul id="dropdown" class="dropdown-content">
            <li><a>User</a></li>
            <li><a>Admin</a></li>
        </ul>
        <header>
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
        </header>
        <main>
            <jsp:include page="<%= request.getAttribute("page").toString()%>"/>
        </main>
        <footer class="page-footer">
            <div class="container">
                <a class="grey-text text-lighten-4 right" href="/app/login">Login</a>
                Version 1.0.0
            </div>
        </footer>

        <!-- Scripts -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
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

