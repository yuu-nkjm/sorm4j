package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.SelectQuery;
import org.nkjmlab.sorm4j.typed.TypedOrmConnection;

/**
 * Main API for object relation mapping. The api consists of {@link OrmReader}, {@link OrmUpdater},
 * {@link OrmMapReader}, {@link SqlExecutor}and {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    ResultSetMapper, ResultSetMapMapper, TransactionFunction, AutoCloseable {


  /**
   * Creates a {@link NamedParameterQuery} from SQL string.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> NamedParameterQuery<T> createNamedParameterQuery(Class<T> objectClass, String sql);

  /**
   * Creates a {@link NamedParameterRequest} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterRequest createNamedParameterRequest(String sql);

  /**
   * Creates a {@link OrderedParameterQuery} from SQL string.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   */
  <T> OrderedParameterQuery<T> createOrderedParameterQuery(Class<T> objectClass, String sql);

  /**
   * Creates a {@link OrderedParameterRequest} from SQL string.
   *
   * @param sql
   * @return
   */
  OrderedParameterRequest createOrderedParameterRequest(String sql);


  /**
   * Creates a {@link SelectQuery}.
   *
   * @return
   */
  <T> SelectQuery<T> createSelectQuery(Class<T> objectClass);


  /**
   * Creates a {@link TypedOrmConnection}
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  @Experimental
  <T> TypedOrmConnection<T> type(Class<T> objectClass);

  /**
   * Gets table name corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  String getTableName(Class<?> objectClass);

}
