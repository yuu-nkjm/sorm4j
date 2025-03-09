package org.nkjmlab.sorm4j.internal.context;

import org.nkjmlab.sorm4j.container.sql.TableSql;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.DbMetaData;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.TableMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
