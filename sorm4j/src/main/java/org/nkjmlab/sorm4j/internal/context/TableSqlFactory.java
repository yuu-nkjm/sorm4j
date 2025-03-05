package org.nkjmlab.sorm4j.internal.context;

import org.nkjmlab.sorm4j.context.common.TableMetaData;
import org.nkjmlab.sorm4j.context.common.TableSql;
import org.nkjmlab.sorm4j.internal.common.DbMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData, DbMetaData databaseMetaData);
}
