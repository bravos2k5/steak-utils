package com.bravos.steak.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * A utility class for converting between different date-time representations and epoch milliseconds.
 * Default time zone can be set by using -Duser.timezone=Your/Timezone when starting the JVM. Example: -Duser.timezone=America/New_York
 */
public class DateTimeHelper {

  private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
  private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

  /**
   * Converts epoch milliseconds to an Instant.
   * @param epochMillis The epoch milliseconds to convert.
   * @return An Instant representing the given epoch milliseconds.
   */
  public static Instant toInstant(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis);
  }

  /**
   * Converts epoch milliseconds to a LocalDateTime in the system's default time zone.
   * @param epochMillis The epoch milliseconds to convert.
   * @return A LocalDateTime representing the given epoch milliseconds in the system's default time zone.
   */
  public static LocalDateTime toLocalDateTime(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis).atZone(DEFAULT_ZONE).toLocalDateTime();
  }

  /**
   * Converts epoch milliseconds to an OffsetDateTime in UTC. Offset date-time is a date-time with an offset from UTC/Greenwich.
   * @param epochMillis The epoch milliseconds to convert.
   * @return An OffsetDateTime representing the given epoch milliseconds in UTC.
   */
  public static OffsetDateTime toOffsetDateTime(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC);
  }

  /**
   * Converts epoch milliseconds to a ZonedDateTime in the system's default time zone.
   * @param epochMillis The epoch milliseconds to convert.
   * @return A ZonedDateTime representing the given epoch milliseconds in the system's default time zone.
   */
  public static ZonedDateTime toZonedDateTime(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis).atZone(DEFAULT_ZONE);
  }

  /**
   * Converts epoch milliseconds to a Date.
   * @param epochMillis The epoch milliseconds to convert.
   * @return A Date representing the given epoch milliseconds.
   */
  public static Date toDate(long epochMillis) {
    return new Date(epochMillis);
  }

  /**
   * Converts epoch milliseconds to an ISO 8601 formatted string in UTC.
   * @param epochMillis The epoch milliseconds to convert.
   * @return A string representing the given epoch milliseconds in ISO 8601 format in UTC.
   */
  public static String toISOString(long epochMillis) {
    return ISO_FORMATTER.format(toInstant(epochMillis).atZone(ZoneOffset.UTC));
  }

  /**
   * Converts an Instant to epoch milliseconds.
   * @param instant The Instant to convert.
   * @return The epoch milliseconds representing the given Instant.
   */
  public static long from(Instant instant) {
    return instant.toEpochMilli();
  }

  /**
   * Converts a LocalDateTime in the system's default time zone to epoch milliseconds.
   * @param localDateTime The LocalDateTime to convert.
   * @return The epoch milliseconds representing the given LocalDateTime.
   */
  public static long from(LocalDateTime localDateTime) {
    return localDateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
  }

  /**
   * Converts a ZonedDateTime to epoch milliseconds.
   * @param zonedDateTime The ZonedDateTime to convert.
   * @return The epoch milliseconds representing the given ZonedDateTime.
   */
  public static long from(ZonedDateTime zonedDateTime) {
    return zonedDateTime.toInstant().toEpochMilli();
  }

  /**
   * Converts an OffsetDateTime to epoch milliseconds.
   * @param offsetDateTime The OffsetDateTime to convert.
   * @return The epoch milliseconds representing the given OffsetDateTime.
   */
  public static long from(OffsetDateTime offsetDateTime) {
    return offsetDateTime.toInstant().toEpochMilli();
  }

  /**
   * Converts a Date to epoch milliseconds.
   * @param date The Date to convert.
   * @return The epoch milliseconds representing the given Date.
   */
  public static long from(Date date) {
    return date.getTime();
  }

  /**
   * Gets the current time in epoch milliseconds.
   * @return The current time in epoch milliseconds.
   */
  public static long currentTimeMillis() {
    return Instant.now().atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
  }

  /**
   * Converts an ISO 8601 formatted string to epoch milliseconds.
   * @param isoString The ISO 8601 formatted string to convert.
   * @return The epoch milliseconds representing the given ISO 8601 formatted string.
   */
  public static long fromISOString(String isoString) {
    return ZonedDateTime.parse(isoString).toInstant().toEpochMilli();
  }

  /**
   * Gets the current LocalDateTime in the system's default time zone.
   * @return The current LocalDateTime.
   */
  public static LocalDateTime now() {
    return LocalDateTime.now(DEFAULT_ZONE);
  }

  /**
   * Gets the system's default time zone.
   * @return The default ZoneId.
   */
  public static ZoneId getDefaultZone() {
    return DEFAULT_ZONE;
  }

}
