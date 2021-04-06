package org.nkjmlab.sorm4j.internal.mapping;

import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.OrmReader;
import org.nkjmlab.sorm4j.OrmUpdater;
import org.nkjmlab.sorm4j.ResultSetMapMapper;
import org.nkjmlab.sorm4j.ResultSetMapper;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.TransactionFunction;

public interface OrmMapper extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    ResultSetMapper, ResultSetMapMapper, TransactionFunction, AutoCloseable {

  /**
   * Gets table name corresponding to the given object class.
   *
   * @param objectClass
   * @return
   */
  String getTableName(Class<?> objectClass);


}
