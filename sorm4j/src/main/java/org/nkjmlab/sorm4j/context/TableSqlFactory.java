package org.nkjmlab.sorm4j.context;

import org.nkjmlab.sorm4j.result.TableMetaData;

public interface TableSqlFactory {

  TableSql create(TableMetaData tableMetaData);

}
