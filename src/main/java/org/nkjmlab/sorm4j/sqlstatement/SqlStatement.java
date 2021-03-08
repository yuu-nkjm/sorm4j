package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */
public final class SqlStatement {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private SqlStatement(String sql, Object... parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  public static SqlStatement of(String sql, Object... parameters) {
    return new SqlStatement(sql, parameters);
  }

  @Override
  public String toString() {
    return "sql=[" + sql + "]" + ((parameters == null || parameters.length == 0) ? ""
        : ", parameters=" + Arrays.toString(parameters) + "");
  }

  public final String getSql() {
    return sql;
  }

  public final Object[] getParameters() {
    return parameters;
  }

  public static String literal(Object element) {
    if (element == null) {
      return "null";
    } else if (element instanceof Number || element instanceof Boolean) {
      return element.toString();
    } else if (element instanceof List) {
      return joinCommaAndSpace(
          ((List<?>) element).stream().map(e -> literal(e)).collect(Collectors.toList()));
    }
    String str = element.toString();
    switch (str) {
      case "?":
        return str;
      default:
        return escapeAndWrapSingleQuote(str);
    }
  }

  private static String joinCommaAndSpace(List<String> elements) {
    return String.join(", ", elements);
  }


  private static String escapeAndWrapSingleQuote(String str) {
    return wrapSingleQuote(str.contains("'") ? str.replaceAll("'", "''") : str);
  }

  private static String wrapSingleQuote(String str) {
    return "'" + str + "'";
  }


}
