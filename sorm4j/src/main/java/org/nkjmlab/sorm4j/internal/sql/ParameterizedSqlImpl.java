package org.nkjmlab.sorm4j.internal.sql;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.internal.util.MessageUtils;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.SelectSql;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
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
    return "sql=[" + sql + "]" + ((parameters == null || parameters.length == 0) ? ""
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

  private static final String EMBEDDED_PLACEHOLDER = "{?}";
  private static final String LIST_PLACEHOLDER = "<?>";

  public static ParameterizedSql of(String sql, Object... parameters) {
    return new ParameterizedSqlImpl(sql, parameters);
  }

  public static ParameterizedSql parse(String sql, Object... parameters) {
    if (parameters.length == 0) {
      return new ParameterizedSqlImpl(sql, parameters);
    }

    ParameterizedSql embeddedSql =
        sql.contains(EMBEDDED_PLACEHOLDER) ? parseEmbeddedPlaceholder(sql, parameters)
            : new ParameterizedSqlImpl(sql, parameters);

    return sql.contains(LIST_PLACEHOLDER)
        ? parseListPlaceholder(embeddedSql.getSql(), embeddedSql.getParameters())
        : embeddedSql;
  }


  private static ParameterizedSql parseListPlaceholder(String sql, Object[] parameters) {
    final List<Integer> specialParameterIndexes = createSpecialParameterIndexes(sql, '<', '?', '>');

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
          throw new SormException("<?> parameter should be bind Collection or Array");
        }
      } else {
        flattenListParams.add(parameters[i]);
      }
    }
    String _sql = MessageUtils.replacePlaceholder(sql, LIST_PLACEHOLDER,
        specialParameterIndexes.size(), index -> {
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
      throw new SormException("<?> parameter should be bind Collection or Array");
    }
  }


  public static ParameterizedSql parseEmbeddedPlaceholder(String sql, Object... parameters) {

    final List<Integer> specialParameterIndexes = createSpecialParameterIndexes(sql, '{', '?', '}');

    List<Object> removedEmbeddedParams = new ArrayList<>();
    for (int i = 0; i < parameters.length; i++) {
      if (!specialParameterIndexes.contains(i)) {
        removedEmbeddedParams.add(parameters[i]);
      }
    }
    String _sql =
        MessageUtils.replacePlaceholder(sql, EMBEDDED_PLACEHOLDER, specialParameterIndexes.size(),
            index -> parameters[specialParameterIndexes.get(index)] == null ? "null"
                : parameters[specialParameterIndexes.get(index)].toString());

    return new ParameterizedSqlImpl(_sql, removedEmbeddedParams.toArray());
  }


  private static List<Integer> createSpecialParameterIndexes(String str, char prefix,
      char normalPlaceholder, char suffix) {
    final char[] arry = str.toCharArray();

    final List<Integer> ret = new ArrayList<>();
    int parameterIndex = 0;

    for (int i = 0; i < arry.length; i++) {
      char c = arry[i];
      if (c == normalPlaceholder && i - 1 >= 0 && arry[i - 1] == prefix && i + 1 < arry.length
          && arry[i + 1] == suffix) {
        ret.add(parameterIndex);
        parameterIndex++;
      } else if (c == normalPlaceholder) {
        parameterIndex++;
      }
    }
    return ret;
  }


  @Override
  public String getBindedSql() {
    String sql = this.sql;
    for (int i = 0; i < parameters.length; i++) {
      sql = sql.replaceFirst("\\?", SelectSql.literal(parameters[i]));
    }
    return sql;
  }

}
