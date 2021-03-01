package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.TypedOrmMapperImpl;

public interface TypedOrmMapper<T>
    extends TypeOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader, SqlExecutor {

  public static <T> TypedOrmMapper<T> of(Class<T> objectClass, Connection conn) {
    return new TypedOrmMapperImpl<>(objectClass, conn, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmMapper<T> of(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    return new TypedOrmMapperImpl<>(objectClass, connection, options);
  }

}
