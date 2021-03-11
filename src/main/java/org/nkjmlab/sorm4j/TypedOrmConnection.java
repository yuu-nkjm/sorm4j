package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.mapping.OrmConfigStore;
import org.nkjmlab.sorm4j.sqlstatement.SelectQuery;
import org.nkjmlab.sorm4j.sqlstatement.NamedParameterQuery;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterQuery;

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

  void runTransaction(Consumer<TypedOrmConnection<T>> handler);

  <R> R executeTransaction(Function<TypedOrmConnection<T>, R> handler);

  OrmConfigStore getConfigStore();

  String getTableName();

  SelectQuery<T> createSelectQuery();

  NamedParameterQuery<T> createNamedParametersQuery(String sql);

  OrderedParameterQuery<T> createOrderedParametersQuery(String sql);


}
