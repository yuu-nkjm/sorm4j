package org.nkjmlab.sorm4j.util.h2.sql;

import static org.nkjmlab.sorm4j.util.sql.SelectSql.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class H2CsvFunctions {
  private H2CsvFunctions() {}

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
  public static String getCallCsvWriteSql(File toFile, String selectSql, Charset charset,
      char fieldSeparator) {
    String _fieldSeparator = toStringChar(fieldSeparator);
    String csvOptions =
        literal("charset=" + charset.name() + " fieldSeparator=") + "||" + _fieldSeparator;
    String csvStmt = "call csvwrite(" + literal(toFile.getAbsolutePath()) + "," + literal(selectSql)
        + "," + csvOptions + ")";
    return csvStmt;
  }


  /**
   * <pre>
   * getCsvReadSql(new File("foo.csv"), StandardCharsets.UTF_8, ",", null)
   *
   * returns
   *
   * csvread('C:\Users\bar\foo.csv',null,'charset=UTF-8 fieldSeparator='||char(44))
   *
   * @see <a href= "https://www.h2database.com/html/functions.html#csvread">Functions - CSVREAD</a>
   * @param csvFile
   * @param csvColumns columns in CSV file. null or empty means the all columns.
   * @param charset
   * @param fieldSeparator
   * @return
   */
  public static String getCsvReadSql(File csvFile, List<String> csvColumns, Charset charset,
      char fieldSeparator) {
    String _fieldSeparator = toStringChar(fieldSeparator);
    String csvOptions =
        literal("charset=" + charset.name() + " fieldSeparator=") + "||" + _fieldSeparator;
    String colSql = (csvColumns == null || csvColumns.size() == 0 ? "null"
        : String.join("||" + _fieldSeparator + "||", csvColumns.stream()
            .map(col -> literal(col.toUpperCase().replace("`", ""))).toArray(String[]::new)));

    String readCsvSql =
        "csvread(" + literal(csvFile.getAbsolutePath()) + "," + colSql + "," + csvOptions + ")";
    return readCsvSql;
  }


  private static String toStringChar(char fieldSeparator) {
    return "char(" + ((int) fieldSeparator) + ")";
  }

  /**
   *
   * @param selectedColumns columns in select clause. null or empty means the all columns.
   * @param csvFile
   * @param csvColumns
   * @param charset
   * @param fieldSeparator
   * @return
   */
  public static String getCsvReadAndSelectSql(List<String> selectedColumns, File csvFile,
      List<String> csvColumns, Charset charset, char fieldSeparator) {
    return "select "
        + (selectedColumns == null || selectedColumns.size() == 0 ? "*"
            : String.join(",", selectedColumns))
        + " from " + getCsvReadSql(csvFile, csvColumns, charset, fieldSeparator);
  }


}
