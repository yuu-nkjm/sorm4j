package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

public interface TypedOrmConnection<T>
    extends TypedOrmMapper<T>, TransactionFunction, Closeable, AutoCloseable {

  void runTransaction(Consumer<TypedOrmConnection<T>> handler);

  <R> R executeTransaction(Function<TypedOrmConnection<T>, R> handler);

  OrmConfigStore getConfigStore();



}
