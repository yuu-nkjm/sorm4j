package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.mapping.OrmConfigStore;

/**
 * Main API for object relation mapping. The api consists of {@link OrmReader}, {@link OrmUpdater},
 * {@link OrmMapReader}, {@link SqlExecutor}and {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    TransactionFunction, Closeable, AutoCloseable {

  void runTransaction(Consumer<OrmConnection> handler);

  <R> R executeTransaction(Function<OrmConnection, R> handler);

  OrmConfigStore getConfigStore();


}
