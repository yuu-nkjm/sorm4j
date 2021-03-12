package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.sqlstatement.NamedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.SelectQuery;

/**
 * Main API for typed object relation mapping. The api consists of {@link TypedOrmReader<T>},
 * {@link TypedOrmUpdater<T>}, {@link OrmMapReader}, {@link SqlExecutor}and
 * {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface TypedOrmConnection<T> extends TypedOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader,
    SqlExecutor, TransactionFunction, Closeable, AutoCloseable {

  /**
   * Create {@link NamedParameterQuery} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterQuery<T> createNamedParameterQuery(String sql);

  /**
   * Create {@link OrderedParameterQuery} from SQL string.
   *
   * @param sql
   * @return
   */
  OrderedParameterQuery<T> createOrderedParameterQuery(String sql);

  /**
   * Create {@link SelectQuery}.
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


}
