package org.nkjmlab.sorm4j.internal.util;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class StringCache {

  private StringCache() {}

  private static final ConcurrentMap<String, String> upperCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> lowerCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> canonicalCaseCaches =
      new ConcurrentHashMap<>();

  public static String toUpperCase(String str) {
    return upperCaseCaches.computeIfAbsent(str, key -> str.toUpperCase(Locale.ENGLISH));
  }

  public static String toLowerCase(String str) {
    return lowerCaseCaches.computeIfAbsent(str, key -> str.toLowerCase(Locale.ENGLISH));
  }

  public static String toCanonicalCase(String str) {
    return canonicalCaseCaches.computeIfAbsent(str,
        key -> str.replaceAll("_", "").replaceAll("\\s", "").toUpperCase(Locale.ENGLISH));
  }

  public static boolean containsAsCanonical(Collection<String> list, String str) {
    if (str == null || str.length() == 0) {
      return false;
    }
    for (String e : list) {
      if (toCanonicalCase(e).equals(toCanonicalCase(str))) {
        return true;
      }
    }
    return false;
  }

  public static boolean equalsAsCanonical(Collection<String> set1, Collection<String> set2) {
    return set1.stream().map(s -> toCanonicalCase(s)).collect(Collectors.toSet())
        .equals(set2.stream().map(s -> toCanonicalCase(s)).collect(Collectors.toSet()));

  }

  public static ConcurrentMap<String, String> getUpperCaseCaches() {
    return upperCaseCaches;
  }

  public static ConcurrentMap<String, String> getLowerCaseCaches() {
    return lowerCaseCaches;
  }

  public static ConcurrentMap<String, String> getCanonicalCaseCaches() {
    return canonicalCaseCaches;
  }
}
