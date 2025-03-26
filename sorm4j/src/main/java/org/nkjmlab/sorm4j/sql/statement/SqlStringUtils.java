package org.nkjmlab.sorm4j.sql.statement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlStringUtils {
  private SqlStringUtils() {}

  /**
   * Converts the given object into an SQL literal representation.
   *
   * <p>This method supports various types:
   *
   * <ul>
   *   <li>{@code null} is converted to the string {@code "null"}.
   *   <li>Arrays are converted into a string representation prefixed with {@code "array []"}.
   *   <li>{@link List} instances are converted into a comma-separated string.
   *   <li>{@link Number} and {@link Boolean} values are returned as-is.
   *   <li>The special case {@code "?"} is returned as-is.
   *   <li>All other values are quoted using the {@link #quote(String)} method.
   * </ul>
   *
   * @param element the object to be converted into an SQL literal
   * @return the SQL literal representation of the given object
   */
  public static String literal(Object element) {
    if (element == null) {
      return "null";
    } else if (element.getClass().isArray()) {
      final int length = Array.getLength(element);
      List<String> ret = new ArrayList<>(length);
      for (int i = 0; i < length; i++) {
        ret.add(literal(Array.get(element, i)));
      }
      return "array [" + String.join(", ", ret) + "]";
    } else if (element instanceof List) {
      return String.join(
          ", ", ((List<?>) element).stream().map(e -> literal(e)).toArray(String[]::new));
    }

    final String str = element.toString();
    if (element instanceof Number || element instanceof Boolean) {
      return str;
    }
    switch (str) {
      case "?":
        return str;
      default:
        return quote(str);
    }
  }

  /**
   * Returns a single-quoted string representation of the given input.
   *
   * <p>If the input contains single quotes, they are escaped by doubling them. If the input is
   * {@code null}, this method returns {@code null}.
   *
   * @param str the string to be quoted
   * @return the single-quoted string, or {@code null} if the input is {@code null}
   */
  public static String quote(String str) {
    return str == null ? null : "'" + str.replaceAll("'", "''") + "'";
  }

  /**
   * Joins the given elements into a single {@code String} with the specified delimiter.
   *
   * <p>Each element is converted to a {@code String} using {@link Objects#toString(Object,
   * String)}, where {@code null} values are represented as the string {@code "null"}.
   *
   * @param delimiter the delimiter to be used between elements
   * @param elements the elements to be joined
   * @return a single {@code String} consisting of the elements separated by the delimiter
   */
  public static String join(String delimiter, Object... elements) {
    return Arrays.stream(elements)
        .map(o -> Objects.toString(o, "null"))
        .collect(Collectors.joining(delimiter));
  }

  /**
   * @see #join(String, Object...)
   */
  public static String join(String delimiter, String[] elements) {
    return join(delimiter, (Object[]) elements);
  }

  /**
   * Escapes the characters in a String using Java String rules.
   *
   * @param str String to escape values in, may be null
   * @return String with escaped values, null if null string input
   */
  public static String escapeJavaString(String str) {
    return str == null
        ? null
        : str.replace("\\", "\\\\")
            .replace("\b", "\\b")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\f", "\\f")
            .replace("\'", "\\'")
            .replace("\"", "\\\"");
  }

  public static String chars(int num) {
    return wrapSpace("char(" + num + ")");
  }

  public static String decimal(int precision) {
    return wrapSpace("decimal(" + precision + ")");
  }

  public static String decimal(int precision, int scale) {
    return wrapSpace("decimal(" + precision + "," + scale + ")");
  }

  private static String wrapSpace(String str) {
    return str == null ? null : " " + str + " ";
  }
}
