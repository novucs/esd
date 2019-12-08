package net.novucs.esd.util;

import java.sql.SQLException;
import java.util.List;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

/**
 * The Membership Util.
 */
public final class MembershipUtil {

  private MembershipUtil() {
    throw new IllegalStateException();
  }

  /**
   * Get a users application.
   *
   * @param user user
   * @param dao application dao
   * @return Application
   * @throws SQLException exception
   */
  public static Application getApplication(User user, Dao<Application> dao)
      throws SQLException {
    return dao.select().where(new Where().eq("user_id", user.getId())).first();
  }

  /**
   * Get users memberships.
   *
   * @param user user
   * @param dao membership dao
   * @return list of memberships
   * @throws SQLException exception
   */
  public static List<Membership> getMemberships(User user, Dao<Membership> dao)
      throws SQLException {
    return dao.select()
        .where(new Where().eq("user_id", user.getId()))
        .all();
  }

  /**
   * Check if a user has previous memberships.
   *
   * @param user user
   * @param dao membership dao
   * @return boolean
   * @throws SQLException exception
   */
  public static boolean hasPreviousMemberships(User user, Dao<Membership> dao)
      throws SQLException {
    return !MembershipUtil.getMemberships(user, dao).isEmpty();
  }

  /**
   * Get a users active membership.
   *
   * @param user user
   * @param dao membership dao
   * @return Membership
   * @throws SQLException exception
   */
  public static Membership getActiveMembership(User user, Dao<Membership> dao)
      throws SQLException {
    return MembershipUtil.getMemberships(user, dao)
        .stream()
        .filter(Membership::isActive)
        .findFirst()
        .orElse(null);
  }

  /**
   * Check if a user has an active membership.
   *
   * @param user user
   * @param dao membership dao
   * @return boolean
   * @throws SQLException exception
   */
  public static boolean hasActiveMembership(User user, Dao<Membership> dao)
      throws SQLException {
    return MembershipUtil.getActiveMembership(user, dao) != null;
  }

  /**
   * Verify a users roles.
   *
   * @param user user
   * @param userRoleDao user role dao
   * @param roleDao role dao
   * @throws SQLException exception
   */
  public static void removeMembershipRole(User user, Dao<UserRole> userRoleDao, Dao<Role> roleDao)
      throws SQLException {
    Role memberRole = roleDao.select()
        .where(new Where().eq("name", Role.MEMBER)).first();

    List<UserRole> userRoles = userRoleDao.select()
        .where(new Where()
            .eq("user_id", user.getId())
            .and()
            .eq("role_id", memberRole.getId()))
        .all();

    if (!userRoles.isEmpty()) {
      userRoleDao.delete(userRoles);
    }
  }
}
