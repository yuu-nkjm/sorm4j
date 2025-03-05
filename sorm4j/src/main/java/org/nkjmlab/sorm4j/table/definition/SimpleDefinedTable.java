package org.nkjmlab.sorm4j.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Experimental;

@Experimental
public final class SimpleDefinedTable<T> extends DefinedTableBase<T> {

  public SimpleDefinedTable(Sorm orm, Class<T> valueType) {
    super(orm, valueType);
  }

  public SimpleDefinedTable(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition);
  }
}
