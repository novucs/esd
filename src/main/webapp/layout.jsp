<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<% Boolean hasSession =
        ((Session) (request.getSession().getAttribute("session"))).getUser() != null; %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>${title}</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- Compiled and minified CSS -->
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" />
        <link rel="stylesheet" href="${baseUrl}/css/site.css" />
        <link rel="stylesheet" href="${baseUrl}/css/${name}.css" />
    </head>
    <body>
        <ul id="dropdown" class="dropdown-content">
            <li><a>User</a></li>
            <li><a>Admin</a></li>
        </ul>
        <header>
            <nav class="navigation-bar">
                <div class="nav-wrapper">
                    <a href="${baseUrl}/homepage" class="brand-logo">
                        <span>XYZ</span>
                        <span>Drivers Association</span>
                    </a>
                    <% if (hasSession) { %>
                    <ul class="right hide-on-med-and-down">
                        <li><a href="${baseUrl}/dashboard">Member Dashboard</a></li>
                        <li><a href="${baseUrl}/admin/dashboard">Admin Dashboard</a></li>
                        <li>
                            <a class="dropdown-trigger" href="" data-target="dropdown">
                                Switch View
                                <i class="material-icons right"></i>
                            </a>
                        </li>
                    </ul>
                    <% } %>
                </div>
            </nav>
        </header>
        <main class="container">
            <jsp:include page="${page}" />
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
        <script src="${baseUrl}/js/error.js"></script>
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

