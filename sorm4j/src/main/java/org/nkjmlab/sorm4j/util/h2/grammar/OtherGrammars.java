package org.nkjmlab.sorm4j.util.h2.grammar;

import static org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils.wrapSingleQuote;

import org.nkjmlab.sorm4j.annotation.Experimental;

/** <a href="https://www.h2database.com/html/grammar.html">SQL Grammar</a> */
@Experimental
public class OtherGrammars {


  public static String scriptCompressionEncryption(String password) {
    return "COMPRESSION DEFLATE CIPHER AES password " + wrapSingleQuote(password);
  }

  public static String drop(boolean includeDrop) {
    return includeDrop ? "drop" : "";
  }
}
