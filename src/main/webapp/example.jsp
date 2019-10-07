<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>Example</h1>
<p>
<table border="1" style="text-align: center;">
    <thead>
    <tr>
        <th width="150">Key</th>
        <th width="500">Value</th>
    </tr>
    </thead>
    <tbody>
    <% for (Map.Entry<String, String> entry : ((Map<String, String>) request.getAttribute("anExampleMap")).entrySet()) { %>
    <tr>
        <td><%= entry.getKey() %>
        </td>
        <td><%= entry.getValue() %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>
</p>
</body>
</html>
