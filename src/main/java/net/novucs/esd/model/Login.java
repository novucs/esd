package net.novucs.esd.model;

import net.novucs.esd.enums.LoginStatus;
import net.novucs.esd.orm.*;
import org.apache.derby.client.am.SqlException;

import java.security.InvalidParameterException;
import java.sql.SQLException;

public class Login {

  private String username;

  private String password;

  public Login(String username, String password){

  }

  public LoginStatus go(){
    try{

      DaoManager manager = new DaoManager(new ConnectionSource(
          "localhost:1527", "user", "password"
      ));

      Dao<User> userDao = manager.get(User.class);
      User user = (userDao.select().where(new Where().eq("email", this.username))).one();

      if(user == null){
        return LoginStatus.IncorrectCredentials;
      }

      // Encrypt password here and check against database password
      if(password != user.getPassword()){
        return LoginStatus.IncorrectCredentials;
      }
      return LoginStatus.LoggedIn;
    } catch(SQLException e){
      //todo: log exception
      return LoginStatus.LoginFailed;
    }
  }
}
