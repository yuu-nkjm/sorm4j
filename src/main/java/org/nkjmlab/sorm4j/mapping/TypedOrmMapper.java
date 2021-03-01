package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.TypeOrmReader;
import org.nkjmlab.sorm4j.TypedOrmUpdater;
import org.nkjmlab.sorm4j.config.OrmConfigStore;

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
