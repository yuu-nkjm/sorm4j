package org.nkjmlab.sorm4j.table.orm;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.table.orm.SimpleDefinedTable;
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

  public static <T> DefinedTable<T> of(Sorm orm, Class<T> valueType) {
    return new SimpleDefinedTable<>(orm, valueType);
  }

  public static <T> DefinedTable<T> of(
      Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    return new SimpleDefinedTable<>(orm, valueType, tableDefinition);
  }
}
