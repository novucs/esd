<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
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
        <t:tile title="Change your password" label="Update your account password!" icon="vpn_key"
                colour="red" size="s4" labelLink="./settings"/>
        <% } %>
    </div>
    <div class="row">
        <t:tile title="0" label="Outstanding Claims" icon="ballot"/>
        <t:tile title="Active" label="Account Status" icon="verified_user"/>
        <t:tile title="0" label="Total Payments" icon="attach_money"/>
    </div>
</div>
