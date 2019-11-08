package net.novucs.esd.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

  private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  public String getDateTimeNow() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT, Locale.UK);
    return sdf.format(cal.getTime());
  }

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
