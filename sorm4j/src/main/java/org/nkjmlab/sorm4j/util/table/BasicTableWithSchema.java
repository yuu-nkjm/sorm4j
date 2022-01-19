package org.nkjmlab.sorm4j.util.table;

import org.nkjmlab.sorm4j.Sorm;

public class BasicTableWithSchema<T> extends BasicTable<T> implements TableWithSchema<T> {

  private final TableSchema tableSchema;

  /**
   * This table instance is bind to the table name defined in the given {@link TableSchema}.
   *
   * @param sorm
   * @param valueType
   * @param tableSchema
   */
  public BasicTableWithSchema(Sorm sorm, Class<T> valueType, TableSchema tableSchema) {
    super(sorm, valueType, tableSchema.getTableName());
    this.tableSchema = tableSchema;
  }

  @Override
  public TableSchema getTableSchema() {
    return tableSchema;
  }

}
