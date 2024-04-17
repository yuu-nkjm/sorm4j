package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class H2BasicTableMappedOrm<T> implements H2TableMappedOrm<T> {

  private final Orm orm;
  private final Class<T> valueType;
  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public H2BasicTableMappedOrm(Orm orm, Class<T> valueType, TableDefinition tableDefinition) {
    this.orm = orm;
    this.valueType = valueType;
    this.tableDefinition = tableDefinition;
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public H2BasicTableMappedOrm(Orm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public Orm getOrm() {
    return orm;
  }

  @Override
  public String getTableName() {
    return tableDefinition.getTableName();
  }

  @Override
  public Class<T> getValueType() {
    return valueType;
  }

  @Override
  public TableDefinition getTableDefinition() {
    return tableDefinition;
  }
}
