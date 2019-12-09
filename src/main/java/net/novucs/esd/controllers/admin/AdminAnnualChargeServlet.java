package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import net.novucs.esd.model.Action;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Notification;
import net.novucs.esd.model.NotificationType;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserAction;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.notifications.NotificationService;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ClaimUtil;

public class AdminAnnualChargeServlet extends BaseServlet {

  private static final long serialVersionUID = 1337133713371338L;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<Role> roleDao;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Action> actionDao;

  @Inject
  private Dao<UserAction> userActionDao;

  @Inject
  private NotificationService notificationService;

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    try{
      LocalDate from = LocalDate.now().minusYears(1);
      LocalDate to = LocalDate.now();

      int claimSum = ClaimUtil.sumAllClaims(claimDao, from, to);
      long numberOfMembers = getNumberOfMembers();

      long maxCharge = claimSum / numberOfMembers;
      if(claimSum == 0){
        maxCharge = 10;
      }

      request.setAttribute("claimSum", claimSum);
      request.setAttribute("numberOfMembers", numberOfMembers);
      request.setAttribute("maxCharge", maxCharge);

      super.forward(request, response, "Annual Sum", "admin.annualcharge");
    } catch(SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
    }
  }

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

    int rangeValue = Integer.parseInt(request.getParameter("range"));
    int maxValue = Integer.parseInt(request.getParameter("max-charge"));
    double charge =  maxValue * rangeValue / 100;
    int[] poundsAndPence = parsePoundsAndPence(charge);
    Integer pounds = poundsAndPence[0];
    Integer pence = poundsAndPence[1];
    ZonedDateTime completeBy = ZonedDateTime.now().plusMonths(1);
    ZonedDateTime created = ZonedDateTime.now();
    Action action = new Action(pounds, pence, completeBy, created);
    try {
      actionDao.insert(action);
      List<Integer> memberIds = getAllMemberIds();
      List<UserAction> userActions = new ArrayList<>();
      for (Integer id : memberIds){
        userActions.add(new UserAction(id, action.getId()));
      }

      userActionDao.insert(userActions);
      int notificationUserId = Session.fromRequest(request).getUser().getId();
      Notification notification = new Notification("Successfully charged members: "
          + String.valueOf(charge), notificationUserId, notificationUserId,
          NotificationType.SUCCESS);
      notificationService.sendNotification(notification);

      response.sendRedirect("annualcharge");

    } catch (SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
    }
  }

  private int[] parsePoundsAndPence(double value){
    String[] arr=String.valueOf(value).split("\\.");
    int[] intArr=new int[2];
    intArr[0]=Integer.parseInt(arr[0]); // 1
    intArr[1]=Integer.parseInt(arr[1]); // 9
    return intArr;
  }

  private List<Integer> getAllMemberIds() throws SQLException {
    Role memberRole = roleDao.select().where(new Where().eq("name", Role.MEMBER)).one();
    return userRoleDao.select()
        .where(new Where().eq("role_id", memberRole.getId()))
        .all()
        .stream()
        .map(UserRole::getUserId)
        .collect(Collectors.toList());
  }

  private long getNumberOfMembers() throws SQLException {
    Role memberRole = roleDao.select().where(new Where().eq("name", Role.MEMBER)).one();

    return userRoleDao.select()
        .where(new Where().eq("role_id", memberRole.getId()))
        .count("*");
  }
}
