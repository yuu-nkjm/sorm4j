package org.nkjmlab.sorm4j.mapping;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

public class TypedOrmTransaction<T> extends TypedOrmConnectionImpl<T> {
  private static final org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();


  public TypedOrmTransaction(Class<T> objectClass, Connection connection, OrmConfigStore options,
      int isolationLevel) {
    super(objectClass, connection, options);
    begin(isolationLevel);
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
