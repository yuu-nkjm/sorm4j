package org.nkjmlab.sorm4j.util.h2.internal;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class H2Keyword {

  private static String wrapSingleQuote(Object str) {
    return str == null ? null : "'" + str + "'";
  }

  public static String scriptCompressionEncryption(String password) {
    return "COMPRESSION DEFLATE CIPHER AES password " + wrapSingleQuote(password);
  }

  public static String drop(boolean includeDrop) {
    return includeDrop ? "drop" : "";
  }
}
