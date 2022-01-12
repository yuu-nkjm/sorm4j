package org.nkjmlab.sorm4j.mapping;

import java.sql.Connection;
import org.nkjmlab.sorm4j.result.TableMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, Class<?> objectClass, Connection connection);

}
