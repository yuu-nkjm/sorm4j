package org.nkjmlab.sorm4j;

import static org.nkjmlab.sorm4j.config.OrmConfigStore.*;
import java.io.Closeable;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.TypedOrmConnectionImpl;
import org.nkjmlab.sorm4j.mapping.TypedOrmMapper;

public interface TypedOrmConnection<T>
    extends TypedOrmMapper<T>, TransactionFunction, Closeable, AutoCloseable {

  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection conn) {
    return new TypedOrmConnectionImpl<T>(objectClass, conn, DEFAULT_CONFIGURATIONS);
  }

  public static <T> TypedOrmConnection<T> of(Class<T> objectClass, Connection connection,
      OrmConfigStore options) {
    return new TypedOrmConnectionImpl<T>(objectClass, connection, options);
  }

  OrmConnection toUntyped();

  void runTransaction(Consumer<TypedOrmConnection<T>> handler);

  <R> R executeTransaction(Function<TypedOrmConnection<T>, R> handler);



}
