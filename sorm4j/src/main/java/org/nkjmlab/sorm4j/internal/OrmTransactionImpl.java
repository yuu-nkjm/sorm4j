package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.extension.SormContext;

/**
 * An transaction with object relation mapping.
 *
 * @author nkjm
 *
 */
public final class OrmTransactionImpl extends OrmConnectionImpl implements OrmTransaction {

  public OrmTransactionImpl(Connection connection, SormContext context) {
    super(connection, context);
    begin(context.getTransactionIsolationLevel());
  }

  @Override
  public void close() {
    rollback();
    super.close();
  }

}