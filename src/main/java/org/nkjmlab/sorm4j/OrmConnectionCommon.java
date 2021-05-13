package org.nkjmlab.sorm4j;

import java.sql.Connection;

public interface OrmConnectionCommon
    extends OrmMapReader, SqlExecutor, TransactionFunction, CommandFunction, AutoCloseable {

  /**
   * Gets {@link Connection}.
   *
   * @return
   */
  Connection getJdbcConnection();


}
