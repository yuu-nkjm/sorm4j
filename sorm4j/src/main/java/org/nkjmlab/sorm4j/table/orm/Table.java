package org.nkjmlab.sorm4j.table.orm;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.handler.ConsumerHandler;
import org.nkjmlab.sorm4j.common.handler.FunctionHandler;
import org.nkjmlab.sorm4j.internal.table.orm.SimpleTable;
import org.nkjmlab.sorm4j.util.function.exception.Try;

public interface Table<T> extends TableOrm<T> {

  /**
   * Gets {@link Orm} object
   *
   * @return
   */
  @Override
  Sorm getOrm();

  public static <T> Table<T> of(Sorm orm, Class<T> valueType) {
    return new SimpleTable<>(orm, valueType);
  }

  public static <T> Table<T> of(Sorm orm, Class<T> valueType, String tableName) {
    return new SimpleTable<>(orm, valueType, tableName);
  }

  default TableConnection<T> toTableConnection(OrmConnection conn) {
    return TableConnection.of(conn, getValueType(), getTableName());
  }

  /**
   * Open {@link TableOrmConnection}. You should always use <code>try-with-resources</code> block to
   * ensure the database connection is released.
   *
   * @return
   */
  default TableConnection<T> open() {
    return TableConnection.of(getOrm().open(), getValueType(), getTableName());
  }

  /**
   * Accepts a {@link OrmConnection} handler for a task with object-relation mapping. The connection
   * will be closed after the process of handler.
   *
   * @param handler
   */
  default void acceptHandler(ConsumerHandler<TableConnection<T>> handler) {
    try (TableConnection<T> conn = open()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Applies a {@link OrmConnection} handler for a task with object-relation mapping and gets the
   * result. The connection will be closed after the process of handler.
   *
   * @param <R>
   * @param connectionHandler
   * @return
   */
  default <R> R applyHandler(FunctionHandler<TableConnection<T>, R> handler) {
    try (TableConnection<T> conn = open()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }
}
