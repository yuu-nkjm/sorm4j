package org.nkjmlab.sorm4j;

import java.sql.Connection;
import org.nkjmlab.sorm4j.config.OrmConfigStore;
import org.nkjmlab.sorm4j.mapping.OrmMapperImpl;

public interface OrmMapper extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor {

  public static OrmMapper of(Connection conn) {
    return of(conn, OrmConfigStore.DEFAULT_CONFIGURATIONS);
  }

  public static OrmMapper of(Connection connection, OrmConfigStore configStore) {
    return new OrmMapperImpl(connection, configStore);
  }


}
