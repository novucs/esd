<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 30/10/2019
  Time: 10:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Homepage</title>
</head>
<header class="site-header" role="banner">
    <h1>
        <a class="site-header_logo-link" href=".">
            XYZ Drivers Association
        </a>
    </h1>
</header>
<ul>
    <li>
        <a class="site-nav" href="\app\members">Member's Dashboard</a>
    </li>
    <li>
        <a class="site-nav" href="\admin">Admin Dashboard</a>
    </li>
</ul>
</html>

<style scoped>
    .h1 a{
        color: inherit;
        text-decoration: none;
        font-weight: inherit;
        text-align: center;
        padding: 10px;
        align: "center";
    }
    a{
        color: black;
        cursor: pointer;
        text-decoration: none;
        font-weight: bold;
        text-transform: uppercase;
        display: block;
        font: 16px "lato";
    }
    li{
        display: inline-block;
        margin-bottom: 0;
        vertical-align: middle;
        padding: 40px;

    }
    .site-header_logo-link {
        display: inline-block;
        word-break: break-word;
        font: 60px "Source Code Pro";
    }
    .site-nav {
        padding: 3px 10px;
    }
</style>
