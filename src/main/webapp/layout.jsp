<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>
            <%= request.getAttribute("title")%>
        </title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Compiled and minified CSS -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
        <link rel="stylesheet" href="css/site.css">
    </head>
    <body>
        <!-- INSERT NAVIGATION BAR -->
        <jsp:include page="<%= request.getAttribute("page").toString()%>" />
    </body>
    <footer>
        <!-- INSERT FOOTER HERE -->
    </footer>
</html>

