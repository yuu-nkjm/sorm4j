package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmTransaction;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public final class OrmTransactionImpl extends OrmConnectionImpl implements OrmTransaction {

  public OrmTransactionImpl(Connection connection, SormContextImpl context, int isolationLevel) {
    super(connection, context);
    begin(isolationLevel);
  }

  @Override
  public void close() {
    rollback();
    super.close();
  }

}
