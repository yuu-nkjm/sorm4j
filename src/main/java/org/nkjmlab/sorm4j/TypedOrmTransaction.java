package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

public class TypedOrmTransaction<T> extends TypedOrmConnection<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  TypedOrmTransaction(Class<T> objectClass, Connection connection, OrmConfigStore options,
      int isolationLevel) {
    super(objectClass, connection, options);
    begin(isolationLevel);
  }


  public static <T> TypedOrmTransaction<T> of(Class<T> objectClass, Connection conn) {
    return of(objectClass, conn, DEFAULT_ISOLATION_LEVEL, DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmTransaction<T> of(Class<T> objectClass, Connection conn,
      int isolationLevel) {
    return of(objectClass, conn, isolationLevel, DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmTransaction<T> of(Class<T> objectClass, Connection connection,
      int isolationLevel, OrmConfigStore options) {
    return new TypedOrmTransaction<T>(objectClass, connection, options, isolationLevel);
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
