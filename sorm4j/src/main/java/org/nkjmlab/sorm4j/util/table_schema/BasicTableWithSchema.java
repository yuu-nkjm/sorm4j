package org.nkjmlab.sorm4j.util.table_schema;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.BasicTable;

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

  public BasicTableWithSchema(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableSchema.builder(valueType).build());
  }

  @Override
  public TableSchema getTableSchema() {
    return tableSchema;
  }

}
