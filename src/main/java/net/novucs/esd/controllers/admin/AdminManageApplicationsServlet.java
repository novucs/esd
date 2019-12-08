package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
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
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ManageApplicationResult;
import net.novucs.esd.util.PaginationUtil;

public class AdminManageApplicationsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final Where WHERE_APPLICATION_IS_PAID = new Where()
      .eq("status", ApplicationStatus.PAID.name());
  private static final String PAGE_SIZE_FILTER = "applicationPageSizeFilter";

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      int pageNumber = (int) PaginationUtil.getPageNumber(request);
      int offset = PaginationUtil.getOffset(pageSize, pageNumber);

      List<Application> applications = applicationDao.select().where(WHERE_APPLICATION_IS_PAID)
          .offset(offset).limit(pageSize).all();
      List<User> users = userDao.join(applications, Application::getUserId);

      List<ManageApplicationResult> results = applications.stream()
          .flatMap(application -> users.stream()
              .filter(user -> application.getUserId().equals(user.getId()))
              .map(user -> new ManageApplicationResult(application, user)))
          .collect(Collectors.toList());

      int maxPages = PaginationUtil.getMaxPages(userDao, pageSize);
      PaginationUtil.setRequestAttributes(request, maxPages, pageNumber, pageSize);
      request.setAttribute("results", results);
      request.setAttribute("toasts", getSession(request).getToasts());

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
    List<Application> applications = applicationDao.select().where(WHERE_APPLICATION_IS_PAID).all();
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
    getSession(request).pushToast((status == ApplicationStatus.DENIED ? "Denied" : "Approved")
        + " " + user.getName() + " - " + user.getEmail());
  }

  @Override
  public String getServletInfo() {
    return "AdminManageApplicationsServlet";
  }
}
