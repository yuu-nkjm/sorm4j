package org.nkjmlab.sorm4j.util.table;

import java.sql.PreparedStatement;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.OrmStreamGenerator;
import org.nkjmlab.sorm4j.context.SqlParametersSetter;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public final class TypedOrmStreamGenerator<T> {

  private final Class<T> type;
  private final OrmStreamGenerator generator;

  public TypedOrmStreamGenerator(Class<T> type, OrmStreamGenerator generator) {
    this.type = type;
    this.generator = generator;
  }

  /**
   * Returns {@link Stream} represents all rows from the table indicated by object class.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with
   * {@link Table#acceptHandler(org.nkjmlab.sorm4j.common.FunctionHandler, org.nkjmlab.sorm4j.common.ConsumerHandler)}
   * for closing the database resources.
   *
   * @return
   */

  public Stream<T> streamAll() {
    return generator.streamAll(type);
  }

  /**
   * Returns an {@link Stream}. It is able to convert to Stream, List, and so on.
   *
   * <strong>Note: </strong>
   *
   * This method keeps ResultSet and PreparedStatement to open. {@link Stream} implements
   * {@link AutoCloseable} and it is expected that it is used with
   * {@link Table#acceptHandler(org.nkjmlab.sorm4j.common.FunctionHandler, org.nkjmlab.sorm4j.common.ConsumerHandler)}
   * for closing the database resources.
   *
   * @param sql
   * @return
   */
  public Stream<T> stream(ParameterizedSql sql) {
    return stream(sql.getSql(), sql.getParameters());
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
   * {@link AutoCloseable} and it is expected that it is used with
   * {@link Table#acceptHandler(org.nkjmlab.sorm4j.common.FunctionHandler, org.nkjmlab.sorm4j.common.ConsumerHandler)}
   * for closing the database resources.
   *
   * @param sql
   * @param parameters
   * @return
   */
  public Stream<T> stream(String sql, Object... parameters) {
    return generator.stream(type, sql, parameters);
  }

}
