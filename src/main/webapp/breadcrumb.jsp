<%@ page import="net.novucs.esd.lifecycle.Session" %>
<% Boolean hasSession =
        ((Session) (request.getSession().getAttribute("session"))).getUser() != null; %>
<% if (hasSession) { %>
    <div class="row xyz-breadcrumb">
        <div class="col s12">
            <a href="${baseUrl}/" class="breadcrumb">XYZ Drivers Association</a>
            <% if (((String) request.getAttribute("page")).contains("admin")) { %>
                <a href="${baseUrl}/admin/dashboard" class="breadcrumb">Administration</a>
            <% } %>
            <a href="${requestScope['javax.servlet.forward.request_uri']}" class="breadcrumb">${title}</a>
        </div>
    </div>
<% } %>