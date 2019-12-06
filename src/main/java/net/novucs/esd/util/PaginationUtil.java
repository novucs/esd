package net.novucs.esd.util;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public final class PaginationUtil {

  public static String[] pageSizes = {"1", "2", "3"};

  private PaginationUtil() {

  }

  public static void postPagination(HttpServletRequest request, String pageSizeFilterName) {
    String ps = request.getParameter("page-size");
    Session session = Session.fromRequest(request);

    if (ps != null) {
      session.setFilter(pageSizeFilterName, Integer.parseInt(ps));
    }
  }

  public static void postPaginationWithSearch(HttpServletRequest request,
      String pageSizeFilterName, String searchFilterName, String searchQuery) {
    postPagination(request, pageSizeFilterName);

    if (searchQuery != null) {
      Session.fromRequest(request).setFilter(searchFilterName, searchQuery);
    }
  }

  public static int getPageSize(HttpServletRequest request, String pageSizeFilterName) {
    Session session = Session.fromRequest(request);
    Integer pageSize = (Integer) session.getFilter(pageSizeFilterName);
    return pageSize == null ? Integer.parseInt(pageSizes[0]) : pageSize;
  }

  public static double getPageNumber(HttpServletRequest request) {
    String pageNumberParameter = request.getParameter("pn");
    return pageNumberParameter == null ? 1 : Double.parseDouble(pageNumberParameter);
  }

  public static <M> List<M> paginate(Dao<M> dao, int pageSize, double pageNumber)
      throws SQLException {
    return dao.select().offset(
        (int) (pageSize * (pageNumber - 1))).limit(pageSize).all();
  }

  public static <M> List<M> paginateWithSearch(Dao<M> dao, int pageSize, double pageNumber,
      String searchQuery, String... columnNames) throws SQLException {
    return dao.select().where(new Where().search(searchQuery, columnNames)).offset(
        (int) (pageSize * (pageNumber - 1))).limit(pageSize).all();
  }

  public static <M> int getMaxPages(Dao<M> dao, double pageSize) throws SQLException {
    return (int) Math.ceil(dao.select().all().size() / pageSize);
  }

  public static void setRequestAttributes(HttpServletRequest request, int maxPages,
      double pageNumber, int pageSize) {
    request.setAttribute("maxPages", maxPages);
    request.setAttribute("pn", pageNumber);
    request.setAttribute("ps", pageSize);
    request.setAttribute("pageSizes", PaginationUtil.pageSizes);
  }
}
