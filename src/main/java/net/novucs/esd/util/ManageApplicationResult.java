package net.novucs.esd.util;

import java.time.ZonedDateTime;

public class ManageApplicationResult {

  private final int applicationId;
  private final int userId;
  private final String name;
  private final String username;
  private final String email;
  private final String address;
  private final ZonedDateTime dateOfBirth;

  public ManageApplicationResult(int applicationId, int userId, String name,
      String username, String email, String address, ZonedDateTime dateOfBirth) {
    this.applicationId = applicationId;
    this.userId = userId;
    this.name = name;
    this.username = username;
    this.email = email;
    this.address = address;
    this.dateOfBirth = dateOfBirth;
  }

  public int getApplicationId() {
    return applicationId;
  }

  public int getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

  public ZonedDateTime getDateOfBirth() {
    return dateOfBirth;
  }
}
