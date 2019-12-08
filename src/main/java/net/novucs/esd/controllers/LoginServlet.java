package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.MembershipUtil;

/**
 * The type Login servlet.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends BaseServlet {

  private static final long serialVersionUID = 1826081247044519303L;

  private static final String LOGIN_PATH = "/login";
  private static final String LOGIN_TITLE = "Login";

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Role> roleDao;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String username = request.getParameter("username");
    if (username == null) {
      Session.fromRequest(request).pushError("Username not provided");
      return;
    }

    User user;
    try {
      user = userDao.select().where(
          new Where()
            .eq("email", username)
            .or()
            .eq("username", username))
          .first();
    } catch (SQLException e) {
      throw new IOException("Failed to communicate with database", e);
    }

    String password = request.getParameter("password");
    Session session = Session.fromRequest(request);

    if (user != null && user.getPassword().authenticate(password)) {
      session.setUser(user);
      loginSuccess(response, session);
    } else {
      session.pushError("Incorrect username or password");
      super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Session session = Session.fromRequest(request);

    // Check if user is already logged in
    if (session.getUser() != null) {
      loginSuccess(response, session);
      return;
    }

    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
  }

  private void loginSuccess(HttpServletResponse response,
      Session session)
      throws IOException {
    try {
      User user = session.getUser();
      List<UserRole> userRoles = userRoleDao.select()
          .where(new Where().eq("user_id", user.getId()))
          .all();
      List<Role> roles = new ArrayList<>();

      for (UserRole userRole : userRoles) {
        Role role = roleDao.select()
            .where(new Where().eq("id", userRole.getRoleId()))
            .first();
        roles.add(role);
      }
      session.setRoles(roles);

      if (!MembershipUtil.hasActiveMembership(user, membershipDao)) {
        MembershipUtil.removeMembershipRole(user, userRoleDao, roleDao);
      }

      if (roles.stream().anyMatch(r -> r.getName()
          .toLowerCase(Locale.getDefault()).equals("administrator"))) {
        response.sendRedirect("admin/dashboard");
        return;
      }

      response.sendRedirect("dashboard");
    } catch (SQLException ex) {
      Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public String getServletInfo() {
    return "LoginServlet";
  }
}
