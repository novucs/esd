<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.model.Role" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<% Session userSession = ((Session) request.getAttribute("session")); %>
<% String userName = userSession.getUser().getName(); %>
<div>
    Member Dashboard!

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
                <% for (Role role : userSession.getRoles()) { %>
                    <li class="collection-item"><%= role.getName() %></li>
                <% } %>
            </ul>
        </div>
    </div>
</div>
