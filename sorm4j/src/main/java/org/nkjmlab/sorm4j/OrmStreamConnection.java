package org.nkjmlab.sorm4j;

import java.sql.PreparedStatement;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.mapping.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public interface OrmStreamConnection {

  /**
   * Returns {@link Stream} represents all rows from the table indicated by object class.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with try-with-resources block.
   *
   * @param <T>
   * @param type
   * @return
   */
  <T> Stream<T> openStreamAll(Class<T> type);

  /**
   * Returns an {@link Stream}. It is able to convert to Stream, List, and so on.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with try-with-resources block.
   *
   * @param <T>
   * @param type
   * @param sql
   * @return
   */
  <T> Stream<T> openStream(Class<T> type, ParameterizedSql sql);

  /**
   * Returns an {@link Stream}. It is able to convert to Stream, List, and so on.
   * <p>
   * Parameters will be set according with the correspondence defined in
   * {@link SqlParametersSetter#setParameters(PreparedStatement,Object[])}
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with try-with-resources block.
   *
   * @param <T>
   * @param type
   * @param sql
   * @param parameters
   * @return
   */
  <T> Stream<T> openStream(Class<T> type, String sql, Object... parameters);


}
