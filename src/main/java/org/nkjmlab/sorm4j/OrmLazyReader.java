package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import org.nkjmlab.sorm4j.extension.SormOptions;
import org.nkjmlab.sorm4j.extension.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;

public interface OrmLazyReader {

  /**
   * Returns {@link LazyResultSet} represents all rows from the table indicated by object class.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> LazyResultSet<T> readAllLazy(Class<T> objectClass);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(SormOptions,PreparedStatement, Object[])}
   *
   *
   */
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters);

  /**
   * Returns an {@link LazyResultSet}. It is able to convert to Stream, List, and so on.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> LazyResultSet<T> readLazy(Class<T> objectClass, ParameterizedSql sql);
}
