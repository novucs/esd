<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Homepage</title>

        <style type="text/css">
            .h1 a {
                color: inherit;
                text-decoration: none;
                font-weight: inherit;
                text-align: center;
                padding: 10px;
            }

            a {
                color: black;
                cursor: pointer;
                text-decoration: none;
                font-weight: bold;
                text-transform: uppercase;
                display: block;
                font: 16px "lato";
            }

            li {
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
    </head>
    <body>
        <header class="site-header" role="banner">
            <h1>
                <a class="site-header_logo-link" href=".">
                    XYZ Drivers Association
                </a>
            </h1>
        </header>
        <ul>
            <li>
                <a class="site-nav" href="/app/members">Member's Dashboard</a>
            </li>
            <li>
                <a class="site-nav" href="/admin">Admin Dashboard</a>
            </li>
        </ul>
    </body>
</html>
