<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@ page import="net.novucs.esd.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<% Session userSession = ((Session) request.getSession().getAttribute("session")); %>
<% Boolean userHasSession = userSession.getUser() != null; %>
<% Boolean userIsMember = userSession.hasRole("Member"); %>
<% Boolean userIsAdmin = userSession.hasRole("Administrator"); %>
<c:set var="user" value="<%=userSession.getUser()%>"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/gif" href="${baseUrl}/assets/favicon.png" />
    <!-- Compiled and minified CSS -->
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" />
    <link rel="stylesheet" href="//fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="${baseUrl}/css/site.css" />
    <link rel="stylesheet" href="${baseUrl}/css/${name}.css" />
    <link rel="stylesheet" href="${baseUrl}/css/overrides.css" />
  </head>
  <body>
    <t:navigation hasSession="<%=userHasSession%>" isMember="<%=userIsMember%>" isAdmin="<%=userIsAdmin%>"/>
    <% if (userHasSession && !request.getAttribute("name").equals("homepage")) { %>
      <script type="text/javascript">
        const userId = "${user.id}";
      </script>
    <main class="container">
        <jsp:include page="breadcrumb.jsp" />
        <jsp:include page="${page}" />
      </main>
    <% } else { %>
      <main>
        <jsp:include page="${page}" />
      </main>
    <% } %>
    <t:footer />
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

