package org.nkjmlab.sorm4j.util.h2.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public class H2SimpleDefinedTable<T> extends H2DefinedTableBase<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public H2SimpleDefinedTable(Sorm sorm, Class<T> valueType, TableDefinition tableDefinition) {
    super(sorm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public H2SimpleDefinedTable(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableDefinition.builder(valueType).build());
  }
}
