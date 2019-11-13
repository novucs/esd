<%@ page import="net.novucs.esd.model.Role" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page import="net.novucs.esd.model.User" %>
<% Session userSession = ((Session) request.getAttribute("session")); %>
<% User user = userSession.getUser(); %>
<% Boolean passwordChange = user.getNeedsPasswordChange() == 1; %>
<div>
    <div class="row no-bottom-margin">
        <div class="col <%=passwordChange ? "s8" : "s12"%>">
            <div class="card black white-text">
                <div class="card-content valign-wrapper">
                    <div class="card-text">
                        <h6>Welcome back, <strong><%=user.getName()%>
                        </strong></h6>
                        <p><em>Some extra details here about something...</em></p>
                    </div>
                    <div class="card-icon">
                        <i class="material-icons medium valign">face</i>
                    </div>
                </div>
            </div>
        </div>
        <% if (passwordChange) { %>
            <div class="col s4">
                <div class="card red white-text">
                    <div class="card-content valign-wrapper">
                        <div class="card-text">
                            <h6>Change your password</h6>
                            <p>
                                <a href="${pageContext.request.contextPath}/settings">
                                    Update your account password!
                                </a>
                            </p>
                        </div>
                        <div class="card-icon">
                            <i class="material-icons medium valign">vpn_key</i>
                        </div>
                    </div>
                </div>
            </div>
        <% } %>
    </div>
    <div class="row">
        <div class="col s12 m4">
            <div class="card black white-text">
                <div class="card-content valign-wrapper">
                    <div class="card-text">
                        <h6>0</h6>
                        <p>Outstanding Claims</p>
                    </div>
                    <div class="card-icon">
                        <i class="material-icons medium valign">ballot</i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col s12 m4">
            <div class="card black white-text">
                <div class="card-content valign-wrapper">
                    <div class="card-text">
                        <h6>Active</h6>
                        <p>Account Status</p>
                    </div>
                    <div class="card-icon">
                        <i class="material-icons medium valign">verified_user</i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col s12 m4">
            <div class="card black white-text">
                <div class="card-content valign-wrapper">
                    <div class="card-text">
                        <h6>0</h6>
                        <p>Total Payments</p>
                    </div>
                    <div class="card-icon">
                        <i class="material-icons medium valign">attach_money</i>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col s12 center-align">
            <ul class="collection with-header">
                <li class="collection-header"><h5><%=user.getName()%>'s Roles</h5></li>
                <% for (Role role : userSession.getRoles()) { %>
                    <li class="collection-item"><%= role.getName() %></li>
                <% } %>
            </ul>
        </div>
    </div>
</div>
