package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.util.h2.table.definition.H2DefinedTableBase;

/**
 * @deprecated Use {@link org.nkjmlab.sorm4j.util.h2.table.definition.H2DefinedTableBase} instead.
 *     <p>This class is deprecated and should no longer be used. Please use {@link
 *     H2DefinedTableBase} as the base class for H2-defined tables.
 * @see org.nkjmlab.sorm4j.util.h2.table.definition.H2DefinedTableBase
 */
@Experimental
@Deprecated
public class H2BasicTable<T> extends H2DefinedTableBase<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public H2BasicTable(Sorm sorm, Class<T> valueType, TableDefinition tableDefinition) {
    super(sorm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public H2BasicTable(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableDefinition.builder(valueType).build());
  }
}
