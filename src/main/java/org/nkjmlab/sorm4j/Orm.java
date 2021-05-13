package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.typed.TypedOrm;

/**
 * ORM functions with an instant connection. When executing ORM function, this object gets a
 * connection and executes the function, after that closes the connection immediately.
 *
 * @author nkjm
 *
 */
@Experimental
public interface Orm extends OrmReader, OrmUpdater, OrmMapReader, CommandFunction,
    TableMetaDataFunction, SqlExecutor {
  /**
   * Creates {@link TypedOrm}
   *
   * @param <S>
   * @param objectClass
   * @return
   */
  <S> TypedOrm<S> type(Class<S> objectClass);

}
