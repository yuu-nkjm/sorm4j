package org.nkjmlab.sorm4j.extension;

import java.sql.Connection;
import org.nkjmlab.sorm4j.sql.TableMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, Class<?> objectClass, Connection connection);

}
