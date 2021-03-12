package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.mapping.ConfigStore;

/**
 * Main API for object relation mapping. The api consists of {@link OrmReader}, {@link OrmUpdater},
 * {@link OrmMapReader}, {@link SqlExecutor}and {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    TransactionFunction, Closeable, AutoCloseable {

  ConfigStore getConfigStore();

}
