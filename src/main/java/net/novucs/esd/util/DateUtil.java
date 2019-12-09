package net.novucs.esd.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The type Date util.
 */
public class DateUtil {

  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  /**
   * Gets date time now.
   *
   * @return the date time now
   */
  public String getDateTimeNow() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT, Locale.UK);
    return sdf.format(cal.getTime());
  }

  /**
   * Gets date from string.
   *
   * @param dateTimeStr the date time str
   * @return the date from string
   */
  public ZonedDateTime getDateFromString(String dateTimeStr) {
    DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
    Date parsedDate;
    try {
      parsedDate = format.parse(dateTimeStr);
    } catch (ParseException e) {
      return null;
    }
    return parsedDate.toInstant().atZone(ZoneId.systemDefault());
  }

  /**
   * Gets date from string.
   *
   * @param dateTime the date time to format
   * @return the date from string
   */
  public static String getFormattedDate(ZonedDateTime dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return formatter.format(dateTime);
  }

  /**
   * Gets date time from string.
   *
   * @param dateTimeStr the date time str
   * @return the date time from string
   */
  public ZonedDateTime getDateTimeFromString(String dateTimeStr) {
    DateFormat format = new SimpleDateFormat(DATETIME_FORMAT, Locale.UK);
    Date parsedDate;
    try {
      parsedDate = format.parse(dateTimeStr);
    } catch (ParseException e) {
      return null;
    }
    return parsedDate.toInstant().atZone(ZoneId.systemDefault());
  }
}
