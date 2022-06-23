package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.result.JdbcDatabaseMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, JdbcDatabaseMetaData databaseMetaData);

}
