package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.SelectQuery;

/**
 * Main API for typed object relation mapping. The api consists of {@link TypedOrmReader<T>},
 * {@link TypedOrmUpdater<T>}, {@link OrmMapReader}, {@link SqlExecutor}and
 * {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface TypedOrmConnection<T> extends TypedOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader,
    SqlExecutor, ResultSetMapper, TransactionFunction, Closeable, AutoCloseable {

  /**
   * Creates a {@link NamedParameterQuery} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterQuery<T> createNamedParameterQuery(String sql);

  /**
   * Creates a {@link OrderedParameterQuery} from SQL string.
   *
   * @param sql
   * @return
   */
  OrderedParameterQuery<T> createOrderedParameterQuery(String sql);

  /**
   * Creates a {@link SelectQuery}.
   *
   * @return
   */
  SelectQuery<T> createSelectQuery();

  /**
   * Gets the table name mapping to this objects.
   *
   * @return
   */
  String getTableName();


  /**
   * Creates {@link TypedOrmConnection}
   *
   * @param <S>
   * @param objectClass
   * @return
   */
  <S> TypedOrmConnection<S> type(Class<S> objectClass);

  /**
   * Creates an {@link OrmConnection}
   *
   * @return
   */
  OrmConnection untype();



}
