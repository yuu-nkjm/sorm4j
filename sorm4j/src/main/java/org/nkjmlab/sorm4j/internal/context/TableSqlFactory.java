package org.nkjmlab.sorm4j.internal.context;

import org.nkjmlab.sorm4j.container.sql.metadata.TableMetaData;
import org.nkjmlab.sorm4j.internal.container.DbMetaData;
import org.nkjmlab.sorm4j.internal.container.TableSql;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
