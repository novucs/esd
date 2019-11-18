package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public class UserSettingsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private Dao<User> userDao;
  private Dao<UserRole> userRoleDao;
  private Dao<Role> roleDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
//      Session session = Session.fromRequest(request);

    // Check if user is already logged in

    super.forward(request, response, "Account Settings", "user.settings");
  }

  @Override
  public String getServletInfo() {
    return "UserSettingsServlet";
  }

  private void userExists(HttpServletResponse response, Session session)
      throws IOException, SQLException {

  }
}
