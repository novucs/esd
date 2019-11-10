<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="net.novucs.esd.model.Role" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String userName = ((User) request.getAttribute("user")).getName(); %>
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
                    You are currently logged in as <%= userName %>,
                    you may now logout by clicking <a href="logout">here</a>.
                </div>
            </div>
            <div class="row">
                <div class="col s12 center-align">
                    <ul class="collection with-header">
                        <li class="collection-header"><h5><%= userName %>'s Roles</h5></li>
                        <% for (Role role : ((List<Role>) request.getAttribute("roles"))) { %>
                            <li class="collection-item"><%= role.getName() %></li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>