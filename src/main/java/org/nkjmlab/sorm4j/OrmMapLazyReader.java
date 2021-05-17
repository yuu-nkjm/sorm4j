package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.extension.ResultSetConverter;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;

public interface OrmMapLazyReader {

  /**
   * See {@link #readMapLazy(String, Object...)}
   *
   * @param sql
   * @return
   */
  LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql);

  /**
   * Returns an {@link LazyResultSet} instance containing data from the execution of the provided
   * parametrized SQL and convert it to Stream, List, and so on.
   * <p>
   * Types returned from the database will be converted to Java types in the map according with the
   * correspondence defined in
   * {@link ResultSetConverter#toSingleMap(SormOptions, ResultSet, List, List)}.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions, PreparedStatement, Object... )}
   *
   * @param sql with ordered parameter. The other type parameters (e.g. named parameter, list
   *        parameter) could not be used.
   * @param parameters are ordered parameter.
   */
  LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters);

}
