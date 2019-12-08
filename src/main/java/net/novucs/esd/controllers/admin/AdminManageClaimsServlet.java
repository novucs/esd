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
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Notification;
import net.novucs.esd.model.NotificationType;
import net.novucs.esd.model.User;
import net.novucs.esd.notifications.NotificationService;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ManageClaimsResult;
import net.novucs.esd.util.PaginationUtil;

public class AdminManageClaimsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final String PAGE_SIZE_FILTER = "claimPageSizeFilter";
  private static final Where WHERE_CLAIM_IS_PENDING = new Where()
      .eq("status", ClaimStatus.PENDING.name());

  @Inject
  private NotificationService notificationService;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      int pageNumber = (int) PaginationUtil.getPageNumber(request);
      int offset = PaginationUtil.getOffset(pageSize, pageNumber);
      long count = claimDao.select().where(WHERE_CLAIM_IS_PENDING).count("*");
      int maxPages = (int) Math.max(1, Math.ceil(count / (double) pageSize));

      List<Claim> claims = claimDao.select().where(WHERE_CLAIM_IS_PENDING)
          .offset(offset).limit(pageSize).all();
      List<Membership> memberships = membershipDao.join(claims, Claim::getMembershipId);
      List<User> users = userDao.join(memberships, Membership::getUserId);

      List<ManageClaimsResult> results = claims.stream().flatMap(claim -> memberships.stream()
          .filter(membership -> claim.getMembershipId().equals(membership.getId()))
          .flatMap(membership -> users.stream()
              .filter(user -> membership.getUserId().equals(user.getId()))
              .map(user -> new ManageClaimsResult(claim, membership, user))))
          .collect(Collectors.toList());

      PaginationUtil.setRequestAttributes(request, maxPages, pageNumber, pageSize);
      request.setAttribute("results", results);
      super.forward(request, response, "Manage Claims", "admin.manageclaims");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PaginationUtil.postPagination(request, PAGE_SIZE_FILTER);
    String method = request.getParameter("method");
    if (method == null || method.isEmpty()) {
      response.sendRedirect("claims");
      return;
    }

    List<Integer> claimIds = Arrays
        .stream(request.getParameterMap().getOrDefault("claim-id", new String[]{}))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    try {
      switch (method) {
        case "approve-all":
          updateAllStatuses(request, ClaimStatus.APPROVED);
          break;
        case "approve-selection":
          updateStatusesById(request, ClaimStatus.APPROVED, claimIds);
          break;
        case "deny-selection":
          updateStatusesById(request, ClaimStatus.REJECTED, claimIds);
          break;
        case "deny-all":
          updateAllStatuses(request, ClaimStatus.REJECTED);
          break;
        default:
          break;
      }
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName())
          .log(Level.SEVERE, "Failed to execute manage claims SQL update", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    response.sendRedirect("claims");
  }

  public void updateAllStatuses(HttpServletRequest request, ClaimStatus status)
      throws SQLException {
    List<Claim> claims = claimDao.select().where(WHERE_CLAIM_IS_PENDING).all();
    for (Claim claim : claims) {
      claim.setStatus(status);
      claimDao.update(claim);
      addUpdateMessage(request, status, claim);
    }
  }

  public void updateStatusesById(HttpServletRequest request, ClaimStatus status, List<Integer> ids)
      throws SQLException {
    for (Integer id : ids) {
      Claim claim = claimDao.selectById(id);
      claim.setStatus(status);
      claimDao.update(claim);
      addUpdateMessage(request, status, claim);
    }
  }

  public void addUpdateMessage(HttpServletRequest request, ClaimStatus status, Claim claim)
      throws SQLException {
    User user = userDao.selectById(
        membershipDao.selectById(claim.getMembershipId()).getUserId());

    int userId = Session.fromRequest(request).getUser().getId();
    String message = status == ClaimStatus.REJECTED ? "Denied" : "Approved";

    notificationService.sendNotification(
        new Notification(message + " " + user.getName() + " - " + user.getEmail(),
            userId, userId, NotificationType.SUCCESS));

    notificationService.sendNotification(new Notification("Your claim was " + message,
        userId, user.getId(), NotificationType.SUCCESS));
  }

  @Override
  public String getServletInfo() {
    return "AdminManageClaimsServlet";
  }
}
