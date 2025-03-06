package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import java.sql.SQLException;

import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 */
public final class OrmTransactionImpl extends OrmConnectionImpl implements OrmTransaction {

  /**
   * Begins transaction with the given transaction isolation level.
   *
   * @param connection
   * @param context
   * @param isolationLevel {@link Connection#TRANSACTION_READ_COMMITTED}, {@link
   *     Connection#TRANSACTION_READ_UNCOMMITTED} and so on.
   */
  public OrmTransactionImpl(Connection connection, SormContextImpl context, int isolationLevel) {
    super(connection, context);
    setAutoCommit(false);
    try {
      getJdbcConnection().setTransactionIsolation(isolationLevel);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void close() {
    try {
      if (!getJdbcConnection().isClosed()) {
        rollback();
      }
    } catch (SQLException e) {
    } finally {
      super.close();
    }
  }
}
