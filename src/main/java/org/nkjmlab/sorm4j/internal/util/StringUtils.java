package org.nkjmlab.sorm4j.internal.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

  private static final String PLACEHOLDER = "{}";
  private static final int LENGTH_OF_PLACEHOLDER = PLACEHOLDER.length();

  public static String format(String msg, Object... params) {
    if (params == null || params.length == 0) {
      return msg;
    }

    final StringBuilder sbuf = new StringBuilder(msg.length() + 50);

    int i = 0;
    int j;
    for (int indexOfParameter = 0; indexOfParameter < params.length; indexOfParameter++) {

      j = msg.indexOf(PLACEHOLDER, i);

      if (j == -1) {
        break;
      }

      sbuf.append(msg, i, j);
      Object o = params[indexOfParameter];
      appendObjectString(sbuf, o);
      i = j + LENGTH_OF_PLACEHOLDER;
    }
    sbuf.append(msg, i, msg.length());
    return sbuf.toString();
  }

  private static void appendObjectString(StringBuilder sbuf, Object o) {
    if (o == null) {
      sbuf.append("null");
    } else if (o.getClass().isArray()) {
      String s = Arrays.deepToString(new Object[] {o});
      sbuf.append(s.substring(1, s.length()));
    } else {
      sbuf.append(o);
    }
  }

}
