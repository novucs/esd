<%@tag description="Generic Header Contents" pageEncoding="UTF-8"%>
<%@attribute name="hasSession" required="true" type="java.lang.Boolean"%>
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