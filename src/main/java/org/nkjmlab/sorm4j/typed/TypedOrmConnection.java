package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.ResultSetMapMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.TransactionFunction;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.sql.helper.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.helper.NamedParameterRequest;
import org.nkjmlab.sorm4j.sql.helper.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sql.helper.OrderedParameterRequest;
import org.nkjmlab.sorm4j.sql.helper.SelectQuery;

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
   * Creates a {@link NamedParameterQuery} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterQuery<T> createNamedParameterQuery(String sql);

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
   * @param sql
   * @return
   */
  OrderedParameterQuery<T> createOrderedParameterQuery(String sql);

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
