<%@ tag import="net.novucs.esd.lifecycle.Session" %>
<%@tag description="Generic Header Contents" pageEncoding="UTF-8" %>
<%@attribute name="hasSession" required="true" type="java.lang.Boolean" %>
<%@attribute name="isMember" required="true" type="java.lang.Boolean" %>
<%@attribute name="isAdmin" required="true" type="java.lang.Boolean" %>
<% Session userSession = ((Session) request.getAttribute("session")); %>
<% Boolean userNoSession = userSession.getUser() == null; %>
<% String baseUrl = request.getContextPath(); %>
<header>
    <nav class="navigation-bar">
        <div class="nav-wrapper ${hasSession ? 'container' : ''}">
            <a href="." class="brand-logo">
                <span>XYZ</span>
                <span>Drivers Association</span>
            </a>
            <% if (userNoSession) { %>
            <nav class="navigation-bar">
                <div class="nav-wrapper">
                    <ul id="nav-mobile" class="right hide-on-med-and-down">
                        <li><a href="login">Login</a></li>
                        <li><a href="register">Register</a></li>
                    </ul>
                </div>
            </nav>
            <% } %>
            <% if (hasSession) { %>
            <ul class="right hide-on-med-and-down">
                <li>
                    <a href="${baseUrl}/dashboard" class="dropdown-trigger" data-target="memberDropdown">
                        Member
                    </a>
                </li>
                <ul id="memberDropdown" class="dropdown-content">
                    <li><a href="${baseUrl}/dashboard">Dashboard</a></li>
                    <% if (isMember) { %>
                    <li class="divider" tabindex="-1"></li>
                    <li><a href="${baseUrl}/profile">My Profile</a></li>
                    <li><a href="${baseUrl}/claims">My Claims</a></li>
                    <li><a href="${baseUrl}/payments">My Payments</a></li>
                    <li class="divider" tabindex="-1"></li>
                    <li><a href="${baseUrl}/makeclaim">Make a Claim</a></li>
                    <% } %>
                    <li><a href="${baseUrl}/makepayment">Pay for Membership</a></li>
                </ul>
                <% if (isAdmin) { %>
                <li>
                    <a href="${baseUrl}/admin/dashboard" class="dropdown-trigger"
                       data-target="adminDropdown">
                        Administration
                    </a>
                </li>
                <ul id="adminDropdown" class="dropdown-content">
                    <li><a href="${baseUrl}/admin/dashboard">Dashboard</a></li>
                    <li class="divider" tabindex="-1"></li>
                    <li><a href="${baseUrl}/admin/users">Manage Users</a></li>
                    <li><a href="${baseUrl}/admin/applications">Manage Applications</a></li>
                    <li><a href="${baseUrl}/admin/claims">Manage Claims</a></li>
                    <li><a href="${baseUrl}/admin/payments">Manage Payments</a></li>
                </ul>
                <% } %>
                <!-- Dropdown Trigger -->
                <li><a class="dropdown-trigger" href="#" data-target="userDropdown"><i
                        class="large material-icons">account_box</i></a></li>
            </ul>
            <% } %>
            <ul id="userDropdown" class="dropdown-content">
                <li><a href="${baseUrl}/settings">User Settings</a></li>
                <li><a href="${baseUrl}/logout">Logout</a></li>
            </ul>
        </div>
    </nav>
</header>