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
    getTableSchema().createTableIfNotExists(getOrm());
    return this;
  }

  default TableWithSchema<T> createIndexesIfNotExists() {
    getTableSchema().createIndexesIfNotExists(getOrm());
    return this;
  }

  default TableWithSchema<T> dropTableIfExists() {
    getTableSchema().dropTableIfExists(getOrm());
    return this;
  }

  default String getTableName() {
    return getTableSchema().getTableName();
  }

}
