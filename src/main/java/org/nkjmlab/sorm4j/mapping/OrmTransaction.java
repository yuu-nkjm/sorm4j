package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.util.Try;

public final class OrmTransaction extends OrmConnectionImpl {
  // private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  OrmTransaction(Connection connection, OrmConfigStore options, int isolationLevel) {
    super(connection, options);
    begin(isolationLevel);
  }


  public static OrmTransaction of(Connection conn) {
    return of(conn, OrmConfigStore.DEFAULT_ISOLATION_LEVEL, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static OrmTransaction of(Connection conn, int isolationLevel) {
    return of(conn, isolationLevel, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static OrmTransaction of(Connection connection, int isolationLevel,
      OrmConfigStore options) {
    return new OrmTransaction(connection, options, isolationLevel);
  }

  public void runTransaction(Consumer<OrmConnection> handler) {
    setAutoCommit(false);
    setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
    handler.accept(this);
    rollback();
  }

  public <R> R executeTransaction(Function<OrmConnection, R> handler) {
    setAutoCommit(false);
    setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
    R ret = handler.apply(this);
    rollback();
    return ret;
  }


  private void setTransactionIsolation(int isolationLevel) {
    Try.runOrThrow(() -> getJdbcConnection().setTransactionIsolation(isolationLevel),
        OrmException::new);
  }


  /**
   * ALWAYS rollback before closing the connection if there's any caught/uncaught exception, the
   * transaction will be rolled back if everything is successful / commit is successful, the
   * rollback will have no effect.
   */
  @Override
  public void close() {
    rollback();
    super.close();
  }


}
