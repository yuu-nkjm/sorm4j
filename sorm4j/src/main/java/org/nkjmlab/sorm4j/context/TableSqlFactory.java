package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.common.JdbcTableMetaData;
import org.nkjmlab.sorm4j.result.JdbcDatabaseMetaData;

public interface TableSqlFactory {

  TableSql create(JdbcTableMetaData tableMetaData, JdbcDatabaseMetaData databaseMetaData);
}
