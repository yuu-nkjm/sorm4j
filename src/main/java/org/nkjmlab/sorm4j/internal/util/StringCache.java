package org.nkjmlab.sorm4j.internal.util;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class StringCache {

  private StringCache() {}

  private static final ConcurrentMap<String, String> upperCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> lowerCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> canonicalCaches = new ConcurrentHashMap<>();

  public static String toUpperCase(String str) {
    return upperCaseCaches.computeIfAbsent(str, key -> str.toUpperCase(Locale.ENGLISH));
  }

  public static String toLowerCase(String str) {
    return lowerCaseCaches.computeIfAbsent(str, key -> str.toLowerCase(Locale.ENGLISH));
  }

  public static String toCanonical(String str) {
    return canonicalCaches.computeIfAbsent(str, key -> _toCanonical(str));
  }

  private static String _toCanonical(String str) {
    return str.replaceAll("_", "").replaceAll("\\s", "").toUpperCase(Locale.ENGLISH);
  }

  /**
   * Given a field or class name in the form CompoundName (for classes) or compoundName (for fields)
   * will return a set of guessed names such as [COMPOUND_NAME].
   */
  public static String toUpperSnakeCase(final String compoundName) {
    String camelCase = compoundName.substring(0, 1).toLowerCase() + compoundName.substring(1);
    return toUpperCase(camelCase.replaceAll("([A-Z])", "_$1"));
  }

  public static final List<String> addPluralSuffix(List<String> names) {
    return names.stream()
        .flatMap(name -> List
            .of(name, name + (Character.isUpperCase(name.charAt(name.length() - 1)) ? "S" : "s"))
            .stream())
        .collect(Collectors.toList());
  }


  public static boolean containsAsCanonical(Collection<String> list, String str) {
    if (str == null || str.length() == 0) {
      return false;
    }
    for (String e : list) {
      if (toCanonical(e).equals(toCanonical(str))) {
        return true;
      }
    }
    return false;
  }

  public static boolean equalsAsCanonical(Collection<String> set1, Collection<String> set2) {
    return set1.stream().map(s -> toCanonical(s)).collect(Collectors.toSet())
        .equals(set2.stream().map(s -> toCanonical(s)).collect(Collectors.toSet()));

  }

  public static ConcurrentMap<String, String> getUppercasecaches() {
    return upperCaseCaches;
  }

  public static ConcurrentMap<String, String> getLowercasecaches() {
    return lowerCaseCaches;
  }

  public static ConcurrentMap<String, String> getCanonicalcaches() {
    return canonicalCaches;
  }
}
