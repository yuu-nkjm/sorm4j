package org.nkjmlab.sorm4j.internal.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StringUtils {

  private StringUtils() {};


  private static final ConcurrentMap<String, String> upperCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> lowerCaseCaches = new ConcurrentHashMap<>();

  private static final ConcurrentMap<String, String> canonicalCaches = new ConcurrentHashMap<>();

  public static String toUpperCase(String str) {
    String upper = upperCaseCaches.computeIfAbsent(str, key -> key.toUpperCase(Locale.ENGLISH));
    return upper;
  }

  public static String toLowerCase(String str) {
    String upper = lowerCaseCaches.computeIfAbsent(str, key -> key.toLowerCase(Locale.ENGLISH));
    return upper;
  }

  public static String toCanonical(String str) {
    return canonicalCaches.computeIfAbsent(str,
        key -> key.replaceAll("_", "").replaceAll("\\s", "").toUpperCase(Locale.ENGLISH));
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


  public static String format(String msg, Object... params) {
    if (params == null || params.length == 0) {
      return msg;
    }
    return replacePlaceholder(msg, "{}", params.length, index -> {
      Object o = params[index];
      if (o == null) {
        return "null";
      } else if (o.getClass().isArray()) {
        String s = Arrays.deepToString(new Object[] {o});
        return s.substring(1, s.length());
      } else {
        return o.toString();
      }
    });
  }


  public static String replacePlaceholder(String messege, String placeholder, int numOfPlaceholder,
      Function<Integer, String> placeholderReplacer) {
    final int placeholderLength = placeholder.length();
    final StringBuilder sbuf = new StringBuilder(messege.length() + 50);
    int i = 0;
    int j;
    for (int p = 0; p < numOfPlaceholder; p++) {
      j = messege.indexOf(placeholder, i);
      sbuf.append(messege, i, j);
      sbuf.append(placeholderReplacer.apply(p));
      i = j + placeholderLength;
    }
    sbuf.append(messege, i, messege.length());
    return sbuf.toString();
  }

}
