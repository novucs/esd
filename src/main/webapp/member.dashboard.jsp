<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
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
                        <h6>Welcome back, <strong><%=user.getName()%></strong></h6>
                    </div>
                    <div class="card-icon">
                        <i class="material-icons medium valign">face</i>
                    </div>
                </div>
            </div>
        </div>
        <% if (passwordChange) { %>
        <t:tile title="Change your password" label="Update your account password!" icon="vpn_key"
                colour="red" size="s4" labelLink="./profile"/>
        <% } %>
    </div>
    <div class="row">
        <t:tile title="${userTotalClaims}" label="Total Claims" icon="ballot"/>
        <t:tile title="${membershipValid}" label="Account Status" icon="verified_user"/>
        <t:tile title="${userTotalPayments}" label="Total Payments" icon="attach_money"/>
    </div>
</div>
