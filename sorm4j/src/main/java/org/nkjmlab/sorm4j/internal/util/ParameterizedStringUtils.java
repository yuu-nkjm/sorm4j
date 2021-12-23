package org.nkjmlab.sorm4j.internal.util;

import java.util.Arrays;
import java.util.function.Function;

public final class ParameterizedStringUtils {

  private ParameterizedStringUtils() {}

  /**
   *
   * Creates a new parameterized message like <a href=
   * "https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/message/ParameterizedMessageFactory.html#newMessage-java.lang.String-java.lang.Object...-">ParameterizedMessageFactory
   * (Apache Log4j API 2.14.1 API)</a>
   *
   * <p>
   * This method simulate as follows:
   * <code>ParameterizedMessageFactory.INSTANCE.newMessage(msg, params).getFormattedMessage()</code>
   *
   * <p>
   * Examples
   *
   * <pre>
   * newString("My name is {}. My score is {}", "Alice",100) => "My name is Alice. My score is 100";
   *
   * newString("{}" , 1) => "1";
   *
   * newString("{}{}", 1) => "1{}";
   *
   * newString("{}", 1, 2) => "1";
   *
   * @param msg including place holders. placeholder is "{}"
   * @param params
   * @return
   */
  public static String newString(String msg, Object... params) {
    return newStringWithPlaceHolder(msg, "{}", params);
  }

  /**
   * @see #newString(String, Object...)
   */
  public static String newStringWithPlaceHolder(String msg, String placeholder, Object... params) {
    if (params == null || params.length == 0) {
      return msg;
    }
    return newString(msg, placeholder, params.length, index -> {
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



  public static String newString(String msg, String placeholder, int numOfParameter,
      Function<Integer, String> parameterReplacer) {
    final StringBuilder sbuf = new StringBuilder(msg.length() + 50);
    int i = 0;
    int j;
    final int placeholderStringLength = placeholder.length();
    for (int p = 0; p < numOfParameter; p++) {
      j = msg.indexOf(placeholder, i);
      if (j == -1) {
        break;
      }
      sbuf.append(msg, i, j);
      sbuf.append(parameterReplacer.apply(p));
      i = j + placeholderStringLength;
    }
    sbuf.append(msg, i, msg.length());
    return sbuf.toString();
  }

}
