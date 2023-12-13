package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.internal.H2OrmConnectionImpl;

@Experimental
public interface H2OrmConnection extends OrmConnection, H2Connection {

  static H2OrmConnection of(OrmConnection ormConnection) {
    return new H2OrmConnectionImpl(ormConnection);
  }
}
