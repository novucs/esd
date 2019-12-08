package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.Notification;
import net.novucs.esd.model.NotificationType;
import net.novucs.esd.model.User;
import net.novucs.esd.notifications.NotificationService;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ManageApplicationResult;
import net.novucs.esd.util.PaginationUtil;

public class AdminManageApplicationsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE_SIZE_FILTER = "applicationPageSizeFilter";

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<User> userDao;

  @Inject
  private NotificationService notificationService;

  @SuppressWarnings("SqlResolve")
  private List<ManageApplicationResult> manageApplicationResults(int offset, int limit)
      throws SQLException {
    try (PreparedStatement statement = userDao.getConnectionSource()
        .getConnection().prepareStatement(
            "SELECT "
                + "    app.\"id\" AS \"app_id\", "
                + "    \"user\".\"id\", "
                + "    \"user\".\"name\", "
                + "    \"user\".\"username\", "
                + "    \"user\".\"email\", "
                + "    \"user\".\"address\", "
                + "    \"user\".\"date_of_birth\" "
                + "FROM \"user\" "
                + "LEFT JOIN \"application\" app on \"user\".\"id\" = app.\"user_id\" "
                + "WHERE app.\"status\" = ? "
                + "OFFSET ? ROWS "
                + "FETCH NEXT ? ROWS ONLY ")) {
      statement.setString(1, ApplicationStatus.PAID.name());
      statement.setInt(2, offset);
      statement.setInt(3, limit);

      try (ResultSet resultSet = statement.executeQuery()) {
        List<ManageApplicationResult> results = new ArrayList<>();

        while (resultSet.next()) {
          int applicationId = resultSet.getInt(1);
          int userId = resultSet.getInt(2);
          String name = resultSet.getString(3);
          String username = resultSet.getString(4);
          String email = resultSet.getString(5);
          String address = resultSet.getString(6);
          ZonedDateTime dateOfBirth = ZonedDateTime
              .ofInstant(resultSet.getTimestamp(7).toInstant(), ZoneOffset.UTC);

          results.add(new ManageApplicationResult(
              applicationId,
              userId,
              name,
              username,
              email,
              address,
              dateOfBirth
          ));
        }

        return results;
      }
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      int pageNumber = (int) PaginationUtil.getPageNumber(request);
      int offset = PaginationUtil.getOffset(pageSize, pageNumber);
      List<ManageApplicationResult> results = manageApplicationResults(offset, pageSize);

      int maxPages = PaginationUtil.getMaxPages(userDao, pageSize);
      PaginationUtil.setRequestAttributes(request, maxPages, pageNumber, pageSize);
      request.setAttribute("results", results);

      super.forward(request, response, "Manage Applications", "admin.manageapplications");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName())
          .log(Level.SEVERE, "Failed to load manage applications servlet", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String method = request.getParameter("method");
    List<Integer> applicationIds = Arrays
        .stream(request.getParameterMap().getOrDefault("application-id", new String[]{}))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    try {
      switch (method) {
        case "approve-all":
          updateAllStatuses(request, ApplicationStatus.APPROVED);
          break;
        case "approve-selection":
          updateStatusesById(request, ApplicationStatus.APPROVED, applicationIds);
          break;
        case "deny-selection":
          updateStatusesById(request, ApplicationStatus.DENIED, applicationIds);
          break;
        case "deny-all":
          updateAllStatuses(request, ApplicationStatus.DENIED);
          break;
        default:
          break;
      }
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName())
          .log(Level.SEVERE, "Failed to execute manage applications SQL update", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    response.sendRedirect("applications");
  }

  public void updateAllStatuses(HttpServletRequest request, ApplicationStatus status)
      throws SQLException {
    List<Application> applications = applicationDao.select()
        .where(new Where().eq("status", ApplicationStatus.PAID.name())).all();
    for (Application application : applications) {
      application.setStatus(status);
      applicationDao.update(application);
      addUpdateMessage(request, status, application);
    }
  }

  public void updateStatusesById(
      HttpServletRequest request, ApplicationStatus status, List<Integer> ids)
      throws SQLException {
    for (Integer id : ids) {
      Application application = applicationDao.selectById(id);
      application.setStatus(status);
      applicationDao.update(application);
      addUpdateMessage(request, status, application);
    }
  }

  public void addUpdateMessage(
      HttpServletRequest request, ApplicationStatus status, Application application)
      throws SQLException {
    User user = userDao.selectById(application.getUserId());

    int userId = Session.fromRequest(request).getUser().getId();
    notificationService.sendNotification(new Notification((status == ApplicationStatus.DENIED
        ? "Denied" : "Approved")  + " " + user.getName() + " - " + user.getEmail(),
        userId, userId, NotificationType.SUCCESS));

    notificationService.sendNotification(new Notification("Your application was approved.",
        userId, user.getId(), NotificationType.SUCCESS));
  }

  @Override
  public String getServletInfo() {
    return "AdminManageApplicationsServlet";
  }
}
