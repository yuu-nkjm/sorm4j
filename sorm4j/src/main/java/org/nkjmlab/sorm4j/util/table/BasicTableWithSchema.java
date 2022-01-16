package org.nkjmlab.sorm4j.util.table;

import java.sql.Connection;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;

public class BasicTableWithSchema<T> extends BasicTable<T> implements TableWithSchema<T> {

  private final TableSchema tableSchema;

  /**
   * This table instance is bind to the table name defined in the given {@link TableSchema}.
   *
   * @param orm
   * @param valueType
   * @param tableSchema
   */
  public BasicTableWithSchema(Orm orm, Class<T> valueType, TableSchema tableSchema) {
    super(orm, valueType, tableSchema.getTableName());
    this.tableSchema = tableSchema;
  }


  /**
   * This table instance is bind to the table name defined in the given {@link TableSchema}.
   *
   * @param dataSouce
   * @param valueType
   * @param tableSchema
   */
  public BasicTableWithSchema(DataSource dataSouce, Class<T> valueType, TableSchema tableSchema) {
    this(Sorm.create(dataSouce), valueType, tableSchema);
  }

  /**
   * This table instance is bind to the table name defined in the given {@link TableSchema}.
   *
   * @param connection
   * @param valueType
   * @param tableSchema
   */
  public BasicTableWithSchema(Connection connection, Class<T> valueType, TableSchema tableSchema) {
    this(OrmConnection.of(connection), valueType, tableSchema);
  }

  @Override
  public TableSchema getTableSchema() {
    return tableSchema;
  }

}
