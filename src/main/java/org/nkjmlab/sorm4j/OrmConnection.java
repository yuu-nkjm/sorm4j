package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface OrmConnection extends OrmMapper, TransactionFunction, Closeable, AutoCloseable {

  <T> TypedOrmConnection<T> toTyped(Class<T> objectClass);

  void runTransaction(Consumer<OrmConnection> handler);

  <R> R executeTransaction(Function<OrmConnection, R> handler);


}
