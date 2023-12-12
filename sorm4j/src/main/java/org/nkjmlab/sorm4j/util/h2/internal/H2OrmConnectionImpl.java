package org.nkjmlab.sorm4j.util.h2.internal;

import java.sql.Connection;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.SormContextImpl;
import org.nkjmlab.sorm4j.util.h2.H2OrmConnection;

public class H2OrmConnectionImpl extends OrmConnectionImpl implements H2OrmConnection {

  public H2OrmConnectionImpl(Connection connection, SormContextImpl sormContext) {
    super(connection, sormContext);
  }

  public H2OrmConnectionImpl(OrmConnection ormConnection) {
    this(ormConnection.getJdbcConnection(), (SormContextImpl) ormConnection.getContext());
  }

  @Override
  public OrmConnection getOrm() {
    return this;
  }
}
