package org.nkjmlab.sorm4j.util.table_def;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.BasicTable;
import org.nkjmlab.sorm4j.table.Table;

@Experimental
public class BasicTableWithDefinition<T> extends BasicTable<T>
    implements WithTableDefinition, Table<T> {

  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public BasicTableWithDefinition(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition.getTableName());
    this.tableDefinition = tableDefinition;
  }

  public BasicTableWithDefinition(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public TableDefinition getTableDefinition() {
    return tableDefinition;
  }
}
