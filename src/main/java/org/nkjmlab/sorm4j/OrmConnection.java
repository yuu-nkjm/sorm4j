package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

public interface OrmConnection extends OrmMapper, TransactionFunction, Closeable, AutoCloseable {

  void runTransaction(Consumer<OrmConnection> handler);

  <R> R executeTransaction(Function<OrmConnection, R> handler);

  OrmConfigStore getConfigStore();


}
