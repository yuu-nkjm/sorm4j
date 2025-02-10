package org.nkjmlab.sorm4j.internal.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public final class StringCache {

  private static final Map<String, String> canonicalCaseCaches = new ConcurrentCache<>(1024);

  private static final Map<String, String> upperCaseCaches = new ConcurrentCache<>(256);

  private static final Map<String, String> lowerCaseCaches = new ConcurrentCache<>(256);

  public static String toUpperCase(String str) {
    return upperCaseCaches.computeIfAbsent(str, key -> str.toUpperCase(Locale.ENGLISH));
  }

  public static String toLowerCase(String str) {
    return lowerCaseCaches.computeIfAbsent(str, key -> str.toLowerCase(Locale.ENGLISH));
  }

  /**
   * Given a field or class name in the form CompoundName (for classes) or compoundName (for fields)
   * will return a set of guessed names such as [COMPOUND_NAME]. snake_case will return as
   * [SNAKE_CASE].
   */
  public static String toUpperSnakeCase(final String name) {
    String ret =
        StringCache.toUpperCase(
            name.contains("_")
                ? name
                : name.replaceAll("([0-9]+)([A-Z])", "$1_$2")
                    .replaceAll("([a-z])([A-Z])", "$1_$2")
                    .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2"));
    return ret;
  }

  /**
   * Converts the given string to string in canonical case.
   *
   * <p><b>Example</b>
   *
   * <pre>
   * STUDENT_ID = &gt; STUDENT_ID
   * studentId = &gt; STUDENT_ID
   * </pre>
   *
   * @param str
   * @return
   */
  public static String toCanonicalName(String str) {
    return canonicalCaseCaches.computeIfAbsent(
        str, key -> toUpperSnakeCase(str.replace("\s", "").replace("/", "")));
  }

  public static String toCanonicalNameWithPrefix(String columnAliasPrefix, String col) {
    return toCanonicalName(columnAliasPrefix) + "__" + toCanonicalName(col);
  }

  /**
   * Returns contains or not: the given collection contains the given string before comparing the
   * elements in the list and the string are converted to canonical case.
   *
   * @param collection
   * @param str
   * @return
   */
  public static boolean containsAsCanonical(Collection<String> collection, String str) {
    if (str == null || str.length() == 0) {
      return false;
    }
    for (String e : collection) {
      if (toCanonicalName(e).equals(toCanonicalName(str))) {
        return true;
      }
    }
    return false;
  }

  private StringCache() {}
}
