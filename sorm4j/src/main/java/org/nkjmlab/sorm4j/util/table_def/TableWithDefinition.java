package org.nkjmlab.sorm4j.util.table_def;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.Table;

@Experimental
public interface TableWithDefinition<T> extends Table<T> {

  /**
   * Gets the table definition.
   *
   * @return
   */
  TableDefinition getTableDefinition();


  default TableWithDefinition<T> createTableIfNotExists() {
    getTableDefinition().createTableIfNotExists(getOrm());
    return this;
  }

  default TableWithDefinition<T> createIndexesIfNotExists() {
    getTableDefinition().createIndexesIfNotExists(getOrm());
    return this;
  }

  default TableWithDefinition<T> dropTableIfExists() {
    getTableDefinition().dropTableIfExists(getOrm());
    return this;
  }

  @Override
  default String getTableName() {
    return getTableDefinition().getTableName();
  }

}
