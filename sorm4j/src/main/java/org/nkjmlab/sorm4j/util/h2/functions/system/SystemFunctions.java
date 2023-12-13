package org.nkjmlab.sorm4j.util.h2.functions.system;

import static org.nkjmlab.sorm4j.util.sql.SelectSql.literal;

import java.io.File;
import java.nio.charset.Charset;

public class SystemFunctions {

  /**
   *
   * Example.
   *
   * <pre>
   * csvwrite('C:\User\nkjmlab\nkjmlab-utils-helper\foo.csv','select * from
   * test_table','charset=UTF-8 fieldSeparator=,')
   *
   * @see <a href= "https://www.h2database.com/html/functions.html#csvwrite">Functions -
   *      CSVWRITE</a>
   *
   * @param toFile
   * @param selectSql
   * @param charset
   * @param fieldSeparator
   * @return
   */
  public static String getCallCsvWriteSql(
      File toFile,
      String selectSql,
      Charset charset,
      char fieldSeparator,
      Character fieldDelimiter) {
    String _fieldSeparator = toStringChar(fieldSeparator);
    String csvOptions =
        literal("charset=" + charset.name())
            + createFiledDelimiter(fieldDelimiter)
            + createFieldSepartor(_fieldSeparator);
    String csvStmt =
        "call csvwrite("
            + literal(toFile.getAbsolutePath())
            + ","
            + literal(selectSql)
            + ","
            + csvOptions
            + ")";
    return csvStmt;
  }

  private static String toStringChar(char fieldSeparator) {
    return "char(" + ((int) fieldSeparator) + ")";
  }

  private static String createFieldSepartor(String _fieldSeparator) {
    return "||" + literal(" fieldSeparator=") + "||" + _fieldSeparator;
  }

  private static String createFiledDelimiter(Character fieldDelimiter) {
    return (fieldDelimiter != null
        ? ("||" + literal(" fieldDelimiter=") + "||" + toStringChar(fieldDelimiter))
        : "");
  }
}
