package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * ORM functions with an instant connection. When executing ORM function, this object gets a
 * connection and executes the function, after that closes the connection immediately.
 *
 * @author nkjm
 *
 */
@Experimental
public interface TypedOrm<T> extends TypedOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader,
    TypedMetaDataFunction<T>, SqlExecutor {

  /**
   * Creates {@link TypedOrm}
   *
   * @param <S>
   * @param objectClass
   * @return
   */
  <S> TypedOrm<S> type(Class<S> objectClass);

  /**
   * Creates an {@link Orm}
   *
   * @return
   */
  Orm untype();

}
