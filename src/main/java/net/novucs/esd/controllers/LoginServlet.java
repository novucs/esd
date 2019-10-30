package net.novucs.esd.controllers;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {

    @Resource(lookup = "java:app/AppName")
    private transient String appName;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("title", String.format("%s - Login", appName));
        request.setAttribute("page", "/homepage.jsp");
        request.getRequestDispatcher("/layout.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("title", String.format("%s - Login", appName));
        request.setAttribute("page", "/login.jsp");
        request.getRequestDispatcher("/layout.jsp").forward(request, response);
    }
}
