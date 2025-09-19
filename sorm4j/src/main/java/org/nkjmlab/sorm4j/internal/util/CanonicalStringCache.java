package org.nkjmlab.sorm4j.internal.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    return cache.computeIfAbsent(name, key -> CanonicalStringUtils.canonicalize(name));
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
