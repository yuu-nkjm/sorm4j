package org.nkjmlab.sorm4j.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.TableBase;

public class DefinedTableBase<T> extends TableBase<T> implements DefinedTable<T> {
  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public DefinedTableBase(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition.getTableName());
    this.tableDefinition = tableDefinition;
  }

  public DefinedTableBase(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public TableDefinition getTableDefinition() {
    return tableDefinition;
  }
}
