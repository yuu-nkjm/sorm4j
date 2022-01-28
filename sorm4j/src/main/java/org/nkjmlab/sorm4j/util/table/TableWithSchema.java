package org.nkjmlab.sorm4j.util.table;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface TableWithSchema<T> extends Table<T> {

  /**
   * Gets the table schema.
   *
   * @return
   */
  TableSchema getTableSchema();


  default TableWithSchema<T> createTableIfNotExists() {
    getTableSchema().createTableIfNotExists(getSorm());
    return this;
  }

  default TableWithSchema<T> createIndexesIfNotExists() {
    getTableSchema().createIndexesIfNotExists(getSorm());
    return this;
  }

  default TableWithSchema<T> dropTableIfExists() {
    getTableSchema().dropTableIfExists(getSorm());
    return this;
  }

  @Override
  default String getTableName() {
    return getTableSchema().getTableName();
  }

}
