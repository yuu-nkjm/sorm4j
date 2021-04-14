package org.nkjmlab.sorm4j.internal.sql;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.nkjmlab.sorm4j.SormException;
import org.nkjmlab.sorm4j.internal.util.SqlUtils;
import org.nkjmlab.sorm4j.internal.util.StringUtils;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * This class represents a sql statement with ordered parameters.
 *
 * @author nkjm
 *
 */
public final class SqlStatementImpl implements SqlStatement {

  // with ? placeholder
  private final String sql;
  // ordered parameters
  private final Object[] parameters;

  private SqlStatementImpl(String sql, Object... parameters) {
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

  private static final String LIST_PLACEHOLDER = "<?>";
  private static final String EMBEDDED_PLACEHOLDER = "{?}";

  public static SqlStatement parse(String sql, Object... parameters) {
    SqlStatement st = new SqlStatementImpl(sql, parameters);
    if (parameters.length == 0) {
      return st;
    }
    st = sql.contains(LIST_PLACEHOLDER) ? procListPlaceholder(sql, parameters) : st;
    st = sql.contains(EMBEDDED_PLACEHOLDER)
        ? procEmbeddedPlaceholder(st.getSql(), st.getParameters())
        : st;
    return st;
  }


  private static SqlStatement procListPlaceholder(String sql, Object[] parameters) {
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
    String _sql = StringUtils.replacePlaceholder(sql, LIST_PLACEHOLDER,
        specialParameterIndexes.size(), index -> {
          int parameterLength = getSize(parameters[specialParameterIndexes.get(index)]);
          return "?,".repeat(parameterLength).substring(0, 2 * parameterLength - 1);
        });
    return new SqlStatementImpl(_sql, flattenListParams.toArray());

  }


  private static int getSize(Object object) {
    if (object instanceof Collection) {
      return ((Collection) object).size();
    } else if (object.getClass().isArray()) {
      return Array.getLength(object);
    } else {
      throw new SormException("<?> parameter should be bind Collection or Array");
    }
  }


  private static SqlStatement procEmbeddedPlaceholder(String sql, Object[] parameters) {

    final List<Integer> specialParameterIndexes = createSpecialParameterIndexes(sql, '{', '?', '}');

    List<Object> removedEmbeddedParams = new ArrayList<>();
    for (int i = 0; i < parameters.length; i++) {
      if (!specialParameterIndexes.contains(i)) {
        removedEmbeddedParams.add(parameters[i]);
      }
    }
    String _sql =
        StringUtils.replacePlaceholder(sql, EMBEDDED_PLACEHOLDER, specialParameterIndexes.size(),
            index -> SqlUtils.literal(parameters[specialParameterIndexes.get(index)]));

    return new SqlStatementImpl(_sql, removedEmbeddedParams.toArray());
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


}
