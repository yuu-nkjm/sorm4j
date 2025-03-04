package org.nkjmlab.sorm4j.util.h2.table;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public class H2SimpleTable<T> implements H2Table<T> {

  private final Sorm orm;
  private final Class<T> valueType;
  private final TableDefinition tableDefinition;

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public H2SimpleTable(Sorm sorm, Class<T> valueType, TableDefinition tableDefinition) {
    this.orm = sorm;
    this.valueType = valueType;
    this.tableDefinition = tableDefinition;
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public H2SimpleTable(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public Sorm getOrm() {
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
}
