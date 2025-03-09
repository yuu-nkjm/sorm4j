package org.nkjmlab.sorm4j.internal.context;

import org.nkjmlab.sorm4j.internal.sql.metadata.DbMetaData;
import org.nkjmlab.sorm4j.internal.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.sql.TableSql;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
