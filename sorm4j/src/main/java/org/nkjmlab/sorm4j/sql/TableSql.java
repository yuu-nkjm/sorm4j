package org.nkjmlab.sorm4j.sql;

import org.nkjmlab.sorm4j.common.container.RowMap;

public interface TableSql {

  String getDeleteSql();

  String getInsertSql();

  String getMergeSql();

  String getMultirowInsertSql(int num);

  String getMultirowMergeSql(int num);

  String getSelectAllSql();

  String getSelectByPrimaryKeySql();

  String getUpdateSql();

  String getExistsSql();

  String getUpdateSql(RowMap object);
}