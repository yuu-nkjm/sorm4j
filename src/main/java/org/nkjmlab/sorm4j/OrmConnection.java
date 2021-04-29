package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.BasicCommand;
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
   * Creates a {@link BasicCommand} from SQL string.
   *
   * @param sql
   * @return
   */
  BasicCommand createCommand(String sql);


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
