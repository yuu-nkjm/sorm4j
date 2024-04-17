package org.nkjmlab.sorm4j.util.table_def;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.SimpleTable;

@Experimental
public class SimpleTableWithDefinition<T> extends SimpleTable<T> implements TableWithDefinition<T> {

  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public SimpleTableWithDefinition(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition.getTableName());
    this.tableDefinition = tableDefinition;
  }

  public SimpleTableWithDefinition(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public TableDefinition getTableDefinition() {
    return tableDefinition;
  }
}
