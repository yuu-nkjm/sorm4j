package org.nkjmlab.sorm4j.util.table_schema;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;

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

  @Override
  default String getTableName() {
    return getTableSchema().getTableName();
  }

}
