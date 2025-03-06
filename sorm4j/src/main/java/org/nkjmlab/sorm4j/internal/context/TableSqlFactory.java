package org.nkjmlab.sorm4j.internal.context;

import org.nkjmlab.sorm4j.container.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.internal.container.TableSql;
import org.nkjmlab.sorm4j.internal.container.sql.metadata.DbMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
