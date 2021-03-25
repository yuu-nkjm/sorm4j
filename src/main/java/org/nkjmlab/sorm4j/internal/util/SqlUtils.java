package org.nkjmlab.sorm4j.internal.util;

import java.util.List;
import java.util.stream.Collectors;

public final class SqlUtils {

  private SqlUtils() {}

  /**
   * Returns single quoted expression. If it includes single quotations, they will be escaped.
   *
   * @param expr
   * @return
   */
  public static String quote(String str) {
    return wrapSingleQuote(str.contains("'") ? str.replaceAll("'", "''") : str);
  }

  private static String wrapSingleQuote(String str) {
    return "'" + str + "'";
  }

  /**
   * Convert the given arguments to SQL literal.
   *
   * @param element
   * @return
   */
  public static String literal(Object element) {
    if (element == null) {
      return "null";
    } else if (element instanceof Number || element instanceof Boolean) {
      return element.toString();
    } else if (element instanceof List) {
      return String.join(", ",
          ((List<?>) element).stream().map(e -> literal(e)).collect(Collectors.toList()));
    }
    String str = element.toString();
    switch (str) {
      case "?":
        return str;
      default:
        return quote(str);
    }
  }

}
