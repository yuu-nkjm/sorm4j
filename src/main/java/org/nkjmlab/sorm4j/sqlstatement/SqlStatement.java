package org.nkjmlab.sorm4j.sqlstatement;

import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.core.sqlstatement.SqlStatementImpl;


/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */

public interface SqlStatement {

  /**
   * Gets this SQL statement.
   *
   * @return
   */
  String getSql();

  /**
   * Gets this ordered parameters.
   *
   * @return
   */
  Object[] getParameters();


  /**
   * Create SqlStatement objects from the given arguments.
   *
   * @param sql
   * @param parameters
   * @return
   */
  public static SqlStatement of(String sql, Object... parameters) {
    return new SqlStatementImpl(sql, parameters);
  }

  /**
   * Convert the given arguments to SQL literal.
   *
   * @param element
   * @return
   */
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
