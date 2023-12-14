package org.nkjmlab.sorm4j.util.h2.internal;

public class LiteralUtils {
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

  /**
   * Wraps the given string in single quotes. If the argument is null, the method returns null.
   *
   * @param str The string to be wrapped in single quotes. If null, the return value will also be
   *     null.
   * @return The string wrapped in single quotes. Returns null if the input argument is null.
   */
  public static String wrapSingleQuote(Object str) {
    return str == null ? null : "'" + str + "'";
  }
}
