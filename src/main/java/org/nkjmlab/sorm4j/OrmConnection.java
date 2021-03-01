package org.nkjmlab.sorm4j;

import java.io.Closeable;
import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.OrmConnectionImpl;

public interface OrmConnection extends OrmMapper, TransactionFunction, Closeable, AutoCloseable {


  public static OrmConnection of(Connection conn) {
    return new OrmConnectionImpl(conn, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static OrmConnection of(Connection connection, OrmConfigStore options) {
    return new OrmConnectionImpl(connection, options);
  }

  <T> TypedOrmConnection<T> toTyped(Class<T> objectClass);

}
