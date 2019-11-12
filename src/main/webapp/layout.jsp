<%@ page import="java.util.List" %>
<%@ page import="net.novucs.esd.lifecycle.Session" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<% Boolean userHasSession =
        ((Session) (request.getSession().getAttribute("session"))).getUser() != null; %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>${title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- Compiled and minified CSS -->
  <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css" />
  <link rel="stylesheet" href="//fonts.googleapis.com/icon?family=Material+Icons">
  <link rel="stylesheet" href="${baseUrl}/css/site.css" />
  <link rel="stylesheet" href="${baseUrl}/css/${name}.css" />
  <link rel="stylesheet" href="${baseUrl}/css/overrides.css" />
</head>
<body>
  <t:navigation hasSession="<%=userHasSession%>"/>
    <% if (userHasSession) { %>
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

