package org.nkjmlab.sorm4j;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.typed.TypedOrmConnection;

/**
 * Main API for object relation mapping.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, TableMetaDataFunction, OrmCommonFunction {

  /**
   * Creates a {@link TypedOrmConnection}
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  @Experimental
  <T> TypedOrmConnection<T> type(Class<T> objectClass);



}
