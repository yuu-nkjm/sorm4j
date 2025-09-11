package org.nkjmlab.sorm4j.internal.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public final class CanonicalStringCache {

  /** A cache for storing canonical case string conversions. */
  private final Map<String, String> cache;

  private final String tableAndColumnSeparator;

  public static final CanonicalStringCache DEFAULT =
      new CanonicalStringCache(new ConcurrentHashMap<>(), "_DOT_");

  public static CanonicalStringCache getDefault() {
    return DEFAULT;
  }

  public CanonicalStringCache(Map<String, String> map, String tableAndColumnSeparator) {
    this.cache = map;
    this.tableAndColumnSeparator = tableAndColumnSeparator;
  }

  /**
   * Converts the given string to its canonical uppercase snake_case representation using a cache.
   *
   * <p>If the value has been previously computed, it is retrieved from the cache for efficiency.
   *
   * @param name the input string
   * @return the cached or newly computed canonical uppercase snake_case representation
   */
  public String toCanonicalName(String name) {
    return cache.computeIfAbsent(name, key -> canonicalize(name));
  }

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
    return toUpperSnakeCase(toSafeIdentifier(name));
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
  private static String toUpperSnakeCase(final String name) {
    if (name.indexOf('_') >= 0 || containsJapanese(name)) {
      return name.toUpperCase(Locale.ENGLISH);
    }
    return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2")
        .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")
        .toUpperCase(Locale.ENGLISH);
  }

  private static boolean containsJapanese(String text) {
    for (int i = 0; i < text.length(); i++) {
      if (Character.UnicodeBlock.of(text.charAt(i))
          == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
        return true;
      }
    }
    return false;
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
   * sanitizeToIdentifier("abc");           // "abc"
   * sanitizeToIdentifier("123abc");        // "_123abc"
   * sanitizeToIdentifier("a・b・c");        // "a_b_c"
   * sanitizeToIdentifier("Test__Value__"); // "Test_Value"
   * sanitizeToIdentifier("_");             // throws IllegalArgumentException
   * sanitizeToIdentifier("class");         // "_class"
   * }</pre>
   *
   * @param raw the input string to sanitize, must not be {@code null} or empty
   * @return a valid Java identifier derived from {@code raw}
   * @throws IllegalArgumentException if {@code raw} is {@code null}, empty, or cannot be sanitized
   *     into a legal Java identifier
   */
  static String toSafeIdentifier(String raw) {
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

  /**
   * Converts two given strings to their canonical uppercase snake_case representations and joins
   * them with separator.
   *
   * <p>This method is useful for generating namespaced or prefixed identifiers.
   *
   * <p><b>Example:</b>
   *
   * <pre>
   * toCanonicalNameWithTableName("users", "id") -> "USERS_DOT_ID"
   * </pre>
   *
   * @param tableName the prefix string
   * @param columnName the main string
   * @return a combined canonical representation of the prefix and name
   */
  public String toCanonicalNameWithTableName(String tableName, String columnName) {
    return toCanonicalName(tableName) + tableAndColumnSeparator + toCanonicalName(columnName);
  }

  /**
   * Checks whether the given collection contains the specified string after converting all elements
   * and the input string to their canonical names.
   *
   * <p>This method ensures case-insensitive and format-independent comparison by normalizing all
   * values to the canonical uppercase snake_case format.
   *
   * <p><b>Example:</b>
   *
   * <pre>
   * List.of("STUDENT_ID", "USER_NAME") contains "studentId" -> true
   * List.of("SNAKE_CASE", "CAMEL_CASE") contains "CamelCase" -> true
   * </pre>
   *
   * @param collection the collection of strings to search in
   * @param name the string to check for existence in the collection
   * @return {@code true} if the collection contains the given string in canonical form, {@code
   *     false} otherwise
   */
  public boolean containsCanonicalName(Collection<String> collection, String name) {
    if (name == null || name.isEmpty()) {
      return false;
    }
    return collection.stream().anyMatch(e -> equalsCanonicalName(e, name));
  }

  /**
   * Compares two strings after converting them to their canonical uppercase snake_case
   * representation.
   *
   * <p>This method ensures that two strings are considered equal if their canonical forms match,
   * regardless of differences in case, spaces, or special characters.
   *
   * <p><b>Example:</b>
   *
   * <pre>
   * comparingAsCanonical("studentId", "STUDENT_ID") -> true
   * comparingAsCanonical("user-name", "USER_NAME") -> true
   * comparingAsCanonical("orderId", "order_id") -> true
   * comparingAsCanonical("customerName", "customer_id") -> false
   * </pre>
   *
   * @param e the first string to compare
   * @param name the second string to compare
   * @return {@code true} if both strings are equal in their canonical form, {@code false} otherwise
   */
  public boolean equalsCanonicalName(String name, String other) {
    return toCanonicalName(name).equals(toCanonicalName(other));
  }

  @Override
  public String toString() {
    return cache.toString();
  }

  public Map<String, String> getCache() {
    return cache;
  }
}
