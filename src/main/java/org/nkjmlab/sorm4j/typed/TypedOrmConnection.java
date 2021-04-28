package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.BasicCommand;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.ResultSetMapMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.TransactionFunction;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Main API for typed object relation mapping. The api consists of {@link TypedOrmReader<T>},
 * {@link TypedOrmUpdater<T>}, {@link OrmMapReader}, {@link SqlExecutor}and
 * {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
@Experimental
public interface TypedOrmConnection<T> extends TypedOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader,
    SqlExecutor, TypedResultSetMapper<T>, ResultSetMapMapper, TransactionFunction, AutoCloseable {


  /**
   * Creates a {@link BasicCommand} from SQL string.
   *
   * @param sql
   * @return
   */
  @Experimental
  BasicCommand createCommand(String sql);


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
