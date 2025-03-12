package org.nkjmlab.sorm4j.internal.extension.h2.orm.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTableBase;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;

public final class H2SimpleDefinedTable<T> extends H2DefinedTableBase<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public H2SimpleDefinedTable(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param orm
   * @param valueType
   */
  public H2SimpleDefinedTable(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }
}
