package org.nkjmlab.sorm4j.util.table;

import java.sql.PreparedStatement;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.OrmStreamConnection;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public final class TypedOrmStreamConnection<T> {

  private final Class<T> type;
  private final OrmStreamConnection conn;

  public TypedOrmStreamConnection(Class<T> type, OrmStreamConnection conn) {
    this.type = type;
    this.conn = conn;
  }

  /**
   * Returns {@link Stream} represents all rows from the table indicated by object class.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with try-with-resources block.
   *
   * @return
   */

  public Stream<T> openStreamAll() {
    return conn.openStreamAll(type);
  }

  /**
   * Returns an {@link Stream}. It is able to convert to Stream, List, and so on.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with try-with-resources block.
   *
   * @param sql
   * @return
   */
  public Stream<T> openStream(ParameterizedSql sql) {
    return conn.openStream(type, sql);
  }

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
   * @param sql
   * @param parameters
   * @return
   */
  public Stream<T> openStream(String sql, Object... parameters) {
    return conn.openStream(type, sql, parameters);
  }

}
