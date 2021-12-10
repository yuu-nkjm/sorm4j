package org.nkjmlab.sorm4j.internal.util;

import java.util.Arrays;
import java.util.function.Function;

public final class MessageUtils {

  private MessageUtils() {};


  public static String newMessage(String msg, Object... params) {
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
