package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmConnectionCommon;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Main API for typed object relation mapping.
 *
 * @author nkjm
 *
 */
@Experimental
public interface TypedOrmConnection<T> extends TypedOrmReader<T>, TypedOrmLazyReader<T>,
    TypedOrmUpdater<T>, TypedMetaDataFunction<T>, OrmConnectionCommon {

  /**
   * Creates {@link TypedOrmConnection}
   *
   * @param <S>
   * @param objectClass
   * @return
   */
  <S> TypedOrmConnection<S> type(Class<S> objectClass);

  /**
   * Creates an {@link OrmConnection}
   *
   * @return
   */
  OrmConnection untype();


}
