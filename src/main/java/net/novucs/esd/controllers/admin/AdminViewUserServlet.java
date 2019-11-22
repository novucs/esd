package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Select;
import net.novucs.esd.orm.Where;

public class AdminViewUserServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String userId  = request.getParameter("userId");

    try{

      User user = userDao.selectById(Integer.parseInt(userId));
      request.setAttribute("user", user);

      List<Membership> userMemberships = membershipDao.select()
          .where(new Where().eq("user_id", user.getId())).all();
      List<Claim> claims = new ArrayList<>();

      if(!userMemberships.isEmpty()){
        Where query = new Where().eq("membership_id", userMemberships.get(0).getId());
        int max = userMemberships.size() - 1;
        for(int i = 1; i <= max; i++){
          query.or().eq("membership_id", userMemberships.get(i).getId());
        }
         claims = claimDao.select().where(query).all();
      }


      request.setAttribute("claims", claims);
      request.setAttribute("memberships", userMemberships);

      super.forward(request, response, "View User", "admin.viewuser");
    } catch (SQLException e){
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch(Exception e){
      e.printStackTrace();
      Logger.getLogger(AdminViewUserServlet.class.getName()).log(Level.SEVERE, e.getMessage());
    }
  }

  @Override
  public String getServletInfo() {
    return "AdminViewUserServlet";
  }
}
