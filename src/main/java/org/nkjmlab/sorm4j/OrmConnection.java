package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.OrderedParameterSql;
import org.nkjmlab.sorm4j.sql.SelectQuery;

/**
 * Main API for object relation mapping. The api consists of {@link OrmReader}, {@link OrmUpdater},
 * {@link OrmMapReader}, {@link SqlExecutor}and {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    ResultSetMapper, TransactionFunction, Closeable, AutoCloseable {


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
   * Creates a {@link NamedParameterSql} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterSql createNamedParameterSql(String sql);

  /**
   * Creates a {@link OrderedParameterQuery} from SQL string.
   *
   * @param <T>
   * @param objectClass
   * @param sql
   * @return
   */
  <T> OrderedParameterQuery<T> createOrderedParameterQuery(Class<T> objectClass, String sql);

  /**
   * Creates a {@link OrderedParameterSql} from SQL string.
   *
   * @param sql
   * @return
   */
  OrderedParameterSql createOrderedParameterSql(String sql);

  /**
   * Creates a {@link SelectQuery}.
   *
   * @return
   */
  <T> SelectQuery<T> createSelectQuery(Class<T> objectClass);


  /**
   * Gets table name corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  String getTableName(Class<?> objectClass);

  /**
   * Creates a {@link TypedOrmConnection}
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> TypedOrmConnection<T> type(Class<T> objectClass);

}
