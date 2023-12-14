package org.nkjmlab.sorm4j.util.h2;

import static org.nkjmlab.sorm4j.util.h2.internal.LiteralUtils.wrapSingleQuote;

import java.io.File;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.grammar.OtherGrammars;

@Experimental
public interface H2Orm {

  /**
   * Gets {@link OrmConnection} object
   *
   * @return
   */
  Orm getOrm();

  default void runscript(File srcFile) {
    getOrm()
        .execute(String.join(" ", "runscript", "from", wrapSingleQuote(srcFile.getAbsolutePath())));
  }

  default void runscript(File srcFile, String password) {
    getOrm()
        .execute(
            String.join(
                " ",
                "runscript",
                "from",
                wrapSingleQuote(srcFile.getAbsolutePath()),
                OtherGrammars.scriptCompressionEncryption(password)));
  }

  default void scriptTo(File destFile, boolean includeDrop) {
    getOrm()
        .execute(
            String.join(
                " ",
                "script",
                OtherGrammars.drop(includeDrop),
                "to",
                wrapSingleQuote(destFile.getAbsolutePath())));
  }

  default void scriptTo(File destFile, boolean includeDrop, String password) {
    getOrm()
        .execute(
            String.join(
                " ",
                "script",
                OtherGrammars.drop(includeDrop),
                "to",
                wrapSingleQuote(destFile.getAbsolutePath()),
                OtherGrammars.scriptCompressionEncryption(password)));
  }

}
