package org.nkjmlab.sorm4j.internal.util;

import java.util.Locale;

import javax.lang.model.SourceVersion;

/**
 * A utility class for caching and converting strings to different case formats.
 *
 * <p>This class provides efficient caching mechanisms for frequently used string transformations,
 * such as converting strings to uppercase or canonical case. It ensures that repeated conversions
 * are optimized for performance by leveraging a cache.
 *
 * <p><b>Usage Example:</b>
 *
 * <pre>
 * String upper = StringCache.toUpperCase("hello"); // "HELLO"
 * String canonical = StringCache.toCanonicalName("studentId"); // "STUDENT_ID"
 * boolean exists = StringCache.containsAsCanonical(list, "studentId");
 * </pre>
 */
public final class CanonicalStringUtils {
  private CanonicalStringUtils() {}

  /**
   * Converts the given string to its canonical uppercase snake_case representation.
   *
   * <p>This method normalizes the input by removing spaces and slashes before converting it to an
   * uppercase snake_case format.
   *
   * <p><b>Example:</b>
   *
   * <pre>
   * "STUDENT_ID" -> "STUDENT_ID"
   * "studentId" -> "STUDENT_ID"
   * "student id" -> "STUDENT_ID"
   * "student-Id" -> "STUDENT_ID"
   * </pre>
   *
   * @param name the input string
   * @return the canonical uppercase snake_case representation of the input
   */
  public static String canonicalize(String name) {
    return toUpperSnakeCase(sanitizeIdentifier(name));
  }

  /**
   * Converts the given string to an uppercase snake_case format.
   *
   * <p>Given a field or class name in camelCase or PascalCase format, this method converts it to an
   * uppercase snake_case format. If the input string already follows snake_case, it remains
   * unchanged.
   *
   * <p><b>Example:</b>
   *
   * <pre>
   * "studentId" -> "STUDENT_ID"
   * "StudentId" -> "STUDENT_ID"
   * "SNAKE_CASE" -> "SNAKE_CASE"
   * </pre>
   *
   * @param name the input string
   * @return the converted uppercase snake_case string
   */
  public static String toUpperSnakeCase(final String s) {
    // Handle null/empty cases
    if (s == null || s.isEmpty()) {
      return s;
    }

    // Respect avoid breaking non-ASCII: just uppercase.
    if (containsNonAscii(s)) {
      return s.toUpperCase(Locale.ROOT);
    }

    StringBuilder out = new StringBuilder(s.length() + 8);
    char prev = 0;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      char next = (i + 1 < s.length()) ? s.charAt(i + 1) : 0;

      if (i > 0 && shouldInsertUnderscore(prev, c, next)) {
        out.append('_');
      }

      out.append(Character.toUpperCase(c));
      prev = c;
    }

    return out.toString();
  }

  private static boolean containsNonAscii(String text) {
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) > 0x7F) {
        return true;
      }
    }
    return false;
  }

  private static boolean shouldInsertUnderscore(char prev, char curr, char next) {
    boolean prevIsLower = (prev >= 'a' && prev <= 'z');
    boolean prevIsDigit = (prev >= '0' && prev <= '9');
    boolean prevIsUpper = (prev >= 'A' && prev <= 'Z');

    boolean currIsUpper = (curr >= 'A' && curr <= 'Z');
    boolean nextIsLower = (next >= 'a' && next <= 'z');

    // Camel case boundary: aB, 0A
    boolean camelBoundary = currIsUpper && (prevIsLower || prevIsDigit);

    // Acronym boundary: URLParser -> URL_Parser
    boolean acronymBoundary = currIsUpper && prevIsUpper && nextIsLower;

    // Letter-digit boundary: html5Parser -> HTML5_PARSER
    boolean letterDigitBoundary =
        (Character.isDigit(curr) && Character.isLetter(prev))
            || (Character.isLetter(curr) && Character.isDigit(prev));

    return camelBoundary || acronymBoundary || letterDigitBoundary;
  }

  public static String toLowerSnakeCase(final String s) {
    return toUpperSnakeCase(s).toLowerCase(Locale.ROOT);
  }

  /**
   * Converts an arbitrary string into a valid Java identifier.
   *
   * <p>Sanitization rules:
   *
   * <ul>
   *   <li>Invalid code points are replaced with a single underscore ({@code '_'}).
   *   <li>Consecutive underscores are collapsed into one.
   *   <li>A trailing underscore is removed if present.
   *   <li>If the first code point is not a valid {@linkplain Character#isJavaIdentifierStart(int)
   *       Java identifier start}, a single underscore is prefixed.
   *   <li>If the result is not a legal Java identifier according to {@link
   *       javax.lang.model.SourceVersion#isIdentifier(CharSequence)}, a single underscore is
   *       prefixed as a rescue (e.g., {@code "class"} → {@code "_class"}). If still illegal, an
   *       {@link IllegalArgumentException} is thrown. This includes the single underscore
   *       identifier ({@code "_"}).
   * </ul>
   *
   * <p>Examples:
   *
   * <pre>{@code
   * sanitizeIdentifier("abc");           // "abc"
   * sanitizeIdentifier("123abc");        // "_123abc"
   * sanitizeIdentifier("a・b・c");        // "a_b_c"
   * sanitizeIdentifier("Test__Value__"); // "Test_Value"
   * sanitizeIdentifier("_");             // throws IllegalArgumentException
   * sanitizeIdentifier("class");         // "_class"
   * }</pre>
   *
   * @param raw the input string to sanitize, must not be {@code null} or empty
   * @return a valid Java identifier derived from {@code raw}
   * @throws IllegalArgumentException if {@code raw} is {@code null}, empty, or cannot be sanitized
   *     into a legal Java identifier
   */
  public static String sanitizeIdentifier(String raw) {
    if (raw == null) {
      throw new IllegalArgumentException("name is null");
    }
    if (raw.isEmpty()) {
      throw new IllegalArgumentException("name is empty");
    }

    final StringBuilder sb = new StringBuilder(raw.length());
    boolean prevUnderscore = false;
    int index = 0;

    // 1) Single pass: replace invalid code points with '_' and avoid consecutive '_'.
    for (int offset = 0; offset < raw.length(); ) {
      final int cp = raw.codePointAt(offset);
      final boolean ok =
          (index == 0 && Character.isJavaIdentifierStart(cp))
              || (index > 0 && Character.isJavaIdentifierPart(cp));

      if (ok) {
        if (cp == '_') {
          // Avoid consecutive underscores from original input.
          if (!prevUnderscore) {
            sb.append('_');
            prevUnderscore = true;
          }
        } else {
          sb.appendCodePoint(cp);
          prevUnderscore = false;
        }
      } else {
        // Replace invalid with '_' (avoiding runs).
        if (!prevUnderscore) {
          sb.append('_');
          prevUnderscore = true;
        }
      }

      index++;
      offset += Character.charCount(cp);
    }

    // 2) Remove a trailing underscore without regex.
    int len = sb.length();
    if (len == 0) {
      throw new IllegalArgumentException("empty after sanitization");
    }
    if (sb.charAt(len - 1) == '_') {
      sb.setLength(len - 1);
      if (sb.length() == 0) {
        // Only underscores were present.
        throw new IllegalArgumentException("empty after trimming trailing underscore");
      }
    }

    // 3) Ensure the first code point is a valid IdentifierStart; if not, prefix a single '_'.
    if (!Character.isJavaIdentifierStart(sb.codePointAt(0))) {
      sb.insert(0, '_');
    }

    // 4) Final legality check; rescue once by prefixing '_' if needed (e.g., keywords).
    String candidate = sb.toString();
    if (!SourceVersion.isIdentifier(candidate)) {
      candidate = "_" + candidate;
      if (!SourceVersion.isIdentifier(candidate)) {
        throw new IllegalArgumentException("not a legal Java identifier: " + candidate);
      }
    }

    return candidate;
  }

  /** Converts SNAKE_CASE to PascalCase (UpperCamelCase). */
  public static String toPascalCase(String snake) {
    if (snake == null || snake.isEmpty()) {
      return snake;
    }
    String[] parts = snake.toLowerCase(Locale.ROOT).split("_");
    StringBuilder sb = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty()) {
        continue; // skip empty due to consecutive underscores
      }
      sb.append(Character.toUpperCase(part.charAt(0)));
      if (part.length() > 1) {
        sb.append(part.substring(1));
      }
    }
    return sb.toString();
  }

  /** Converts SNAKE_CASE to camelCase (lowerCamelCase). */
  public static String toCamelCase(String snake) {
    String pascal = toPascalCase(snake);
    if (snake == null || pascal.isEmpty()) {
      return pascal;
    }
    return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
  }
}
