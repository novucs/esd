package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.PaginationUtil;

public class MemberManageClaimsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE_SIZE_FILTER = "userPageSizeFilter";

  private static final String CLAIM_SEARCH_QUERY = "claimSearchQuery";

  @Inject
  private Dao<Claim> claimDao;
  @Inject
  private Dao<Membership> membershipDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      Session session = Session.fromRequest(request);
      User user = session.getUser();
      String searchQuery = (String) session.getFilter(CLAIM_SEARCH_QUERY);
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      double pageNumber = PaginationUtil.getPageNumber(request);

      Membership membership = membershipDao.select()
          .where(new Where().eq("user_id", user.getId()))
          .all()
          .stream()
          .filter(Membership::isAbleToClaim)
          .findFirst()
          .orElse(null);

      List<Claim> claims = new ArrayList<>();
      if (searchQuery == null && membership != null) {
        claims = PaginationUtil
            .paginate(claimDao, pageSize, pageNumber,
                new Where().eq("membership_id", membership.getId()));
      } else if (membership != null) {
        String[] columns = {"rationale", "status"};
        claims = PaginationUtil.paginateWithSearch(claimDao, pageSize, pageNumber,
            new Where().eq("membership_id", membership.getId()),
            searchQuery, columns);
      }

      int max = PaginationUtil.getMaxPages(claimDao, pageSize);
      PaginationUtil.setRequestAttributes(request, max, pageNumber, pageSize);
      claims.sort(Comparator.comparing(Claim::getId).reversed());
      request.setAttribute("claims", claims);
      session.removeFilter(CLAIM_SEARCH_QUERY);

      super.forward(request, response, "Manage Claims", "member.claims.manage");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Here we will set the filters in the users session.

    String searchQuery = request.getParameter("search-claims-query");
    PaginationUtil.postPaginationWithSearch(request, PAGE_SIZE_FILTER, CLAIM_SEARCH_QUERY,
        searchQuery);

    response.sendRedirect("claims");
  }

  @Override
  public String getServletInfo() {
    return "MemberManageClaimsServlet";
  }
}
