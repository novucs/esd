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
import net.novucs.esd.model.Application;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ManageApplicationResult;
import net.novucs.esd.util.PaginationUtil;

public class AdminManageApplicationsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE_SIZE_FILTER = "applicationPageSizeFilter";

  // TODO: update these statuses once complete
  private static final String PAID_STATUS = "OPEN";
  private static final String APPROVED_STATUS = "APPROVED";
  private static final String DENIED_STATUS = "DENIED";

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<User> userDao;

  private List<ManageApplicationResult> manageApplicationResults(int offset, int limit)
      throws SQLException {
    @SuppressWarnings("SqlResolve")
    PreparedStatement statement = userDao.getConnectionSource().getConnection().prepareStatement(
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
            + "FETCH NEXT ? ROWS ONLY ");
    statement.setString(1, PAID_STATUS);
    statement.setInt(2, offset);
    statement.setInt(3, limit);
    ResultSet resultSet = statement.executeQuery();

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
          updateAllStatuses(APPROVED_STATUS);
          break;
        case "approve-selection":
          updateStatusesById(APPROVED_STATUS, applicationIds);
          break;
        case "deny-selection":
          updateStatusesById(DENIED_STATUS, applicationIds);
          break;
        case "deny-all":
          updateAllStatuses(DENIED_STATUS);
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

  public void updateAllStatuses(String status) throws SQLException {
    List<Application> applications = applicationDao.select()
        .where(new Where().eq("status", PAID_STATUS)).all();
    for (Application application : applications) {
      application.setStatus(status);
      applicationDao.update(application);
    }
  }

  public void updateStatusesById(String status, List<Integer> ids) throws SQLException {
    for (Integer id : ids) {
      Application application = applicationDao.selectById(id);
      application.setStatus(status);
      applicationDao.update(application);
    }
  }

  @Override
  public String getServletInfo() {
    return "AdminManageApplicationsServlet";
  }
}
