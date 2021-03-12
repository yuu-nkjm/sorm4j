package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.mapping.ConfigStore;
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

  NamedParameterQuery<T> createNamedParametersQuery(String sql);

  OrderedParameterQuery<T> createOrderedParametersQuery(String sql);

  SelectQuery<T> createSelectQuery();

  ConfigStore getConfigStore();

  String getTableName();


}
