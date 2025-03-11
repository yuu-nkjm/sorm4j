package org.nkjmlab.sorm4j.internal.sql.parameterize;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nkjmlab.sorm4j.common.exception.SormException;
import org.nkjmlab.sorm4j.internal.util.ParameterizedStringFormatter;
import org.nkjmlab.sorm4j.sql.parameterize.ParameterizedSql;
import org.nkjmlab.sorm4j.util.sql.SqlStringUtils;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 */
public final class ParameterizedSqlImpl implements ParameterizedSql {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private ParameterizedSqlImpl(String sql, Object... parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  @Override
  public String toString() {
    return "sql=["
        + sql
        + "]"
        + ((parameters == null || parameters.length == 0)
            ? ""
            : ", parameters=" + Arrays.toString(parameters) + "");
  }

  @Override
  public final String getSql() {
    return sql;
  }

  @Override
  public final Object[] getParameters() {
    return parameters;
  }

  private static char LIST_PLACEHOLDER_LEFT = '<';
  private static char LIST_PLACEHOLDER_CENTER = '?';
  private static char LIST_PLACEHOLDER_RIGHT = '>';

  private static final String LIST_PLACEHOLDER =
      "" + LIST_PLACEHOLDER_LEFT + LIST_PLACEHOLDER_CENTER + LIST_PLACEHOLDER_RIGHT;

  public static ParameterizedSql of(String sql, Object... parameters) {
    return sql.contains(LIST_PLACEHOLDER)
        ? ListPlaceholdersHandler.createExpandedListPlaceholdersSql(sql, parameters)
        : new ParameterizedSqlImpl(sql, parameters);
  }

  @Override
  public String getExecutableSql() {
    String result = this.sql;
    String regex = "(?<!\\w)\\?(?!\\w)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(result);

    int paramIndex = 0;
    StringBuffer sb = new StringBuffer();

    while (matcher.find() && paramIndex < parameters.length) {
      matcher.appendReplacement(sb, SqlStringUtils.literal(parameters[paramIndex++]));
    }
    matcher.appendTail(sb);

    return sb.toString();
  }

  private static class ListPlaceholdersHandler {
    private static ParameterizedSql createExpandedListPlaceholdersSql(
        String sql, Object[] parameters) {
      final List<Integer> specialParameterIndexes =
          createSpecialParameterIndexes(
              sql, LIST_PLACEHOLDER_LEFT, LIST_PLACEHOLDER_CENTER, LIST_PLACEHOLDER_RIGHT);

      List<Object> flattenListParams = new ArrayList<>();
      for (int i = 0; i < parameters.length; i++) {
        if (specialParameterIndexes.contains(i)) {
          Object o = parameters[i];
          if (o instanceof Collection) {
            ((Collection<?>) o).forEach(p -> flattenListParams.add(p));
          } else if (o.getClass().isArray()) {
            int length = Array.getLength(o);
            for (int j = 0; j < length; j++) {
              Object elem = Array.get(o, j);
              flattenListParams.add(elem);
            }
          } else {
            throw new SormException(
                LIST_PLACEHOLDER + " parameter should be bind Collection or Array");
          }
        } else {
          flattenListParams.add(parameters[i]);
        }
      }
      String _sql =
          ParameterizedStringFormatter.newString(
              sql,
              LIST_PLACEHOLDER,
              specialParameterIndexes.size(),
              index -> {
                int parameterLength = getSize(parameters[specialParameterIndexes.get(index)]);
                return "?,".repeat(parameterLength).substring(0, 2 * parameterLength - 1);
              });
      return new ParameterizedSqlImpl(_sql, flattenListParams.toArray());
    }

    private static int getSize(Object object) {
      if (object instanceof Collection) {
        return ((Collection<?>) object).size();
      } else if (object.getClass().isArray()) {
        return Array.getLength(object);
      } else {
        throw new SormException(LIST_PLACEHOLDER + " parameter should be bind Collection or Array");
      }
    }

    private static List<Integer> createSpecialParameterIndexes(
        String str, char prefix, char normalPlaceholder, char suffix) {
      final char[] arry = str.toCharArray();

      final List<Integer> ret = new ArrayList<>();
      int parameterIndex = 0;

      for (int i = 0; i < arry.length; i++) {
        char c = arry[i];
        if (c == normalPlaceholder
            && i - 1 >= 0
            && arry[i - 1] == prefix
            && i + 1 < arry.length
            && arry[i + 1] == suffix) {
          ret.add(parameterIndex);
          parameterIndex++;
        } else if (c == normalPlaceholder) {
          parameterIndex++;
        }
      }
      return ret;
    }
  }
}
