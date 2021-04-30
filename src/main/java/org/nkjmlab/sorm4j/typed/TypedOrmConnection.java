package org.nkjmlab.sorm4j.typed;

import org.nkjmlab.sorm4j.OrmCommonFunction;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * Main API for typed object relation mapping.
 *
 * @author nkjm
 *
 */
@Experimental
public interface TypedOrmConnection<T>
    extends TypedOrmReader<T>, TypedOrmUpdater<T>, TypedMetaDataHandler<T>, OrmCommonFunction {

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
