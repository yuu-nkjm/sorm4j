package org.nkjmlab.sorm4j.util.table_def;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.table.BasicTable;

@Experimental
public class BasicTableWithDefinition<T> extends BasicTable<T> implements TableWithDefinition<T> {

  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public BasicTableWithDefinition(Sorm sorm, Class<T> valueType, TableDefinition tableDefinition) {
    super(sorm, valueType, tableDefinition.getTableName());
    this.tableDefinition = tableDefinition;
  }

  public BasicTableWithDefinition(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public TableDefinition getTableDefinition() {
    return tableDefinition;
  }
}
