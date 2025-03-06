package org.nkjmlab.sorm4j.table.orm;

import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public interface DefinedTable<T> extends Table<T> {

  /**
   * Gets the table definition.
   *
   * @return
   */
  TableDefinition getTableDefinition();

  default DefinedTable<T> createTableIfNotExists() {
    getTableDefinition().createTableIfNotExists(getOrm());
    return this;
  }

  default DefinedTable<T> createIndexesIfNotExists() {
    getTableDefinition().createIndexesIfNotExists(getOrm());
    return this;
  }

  default DefinedTable<T> dropTableIfExists() {
    getTableDefinition().dropTableIfExists(getOrm());
    return this;
  }

  default DefinedTable<T> dropTableIfExistsCascade() {
    getTableDefinition().dropTableIfExistsCascade(getOrm());
    return this;
  }
}
