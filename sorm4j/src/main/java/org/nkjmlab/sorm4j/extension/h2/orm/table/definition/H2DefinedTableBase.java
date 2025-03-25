package org.nkjmlab.sorm4j.extension.h2.orm.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.table.orm.DefinedTableBase;

/**
 * A base class for H2 database tables with predefined schema.
 *
 * <p>This class provides utility methods to create, drop, and manage tables in H2. It is designed
 * to be extended by other classes.
 *
 * @param <T> the entity type
 */
public abstract class H2DefinedTableBase<T> extends DefinedTableBase<T>
    implements H2DefinedTable<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param orm
   * @param valueType
   * @param tableDefinition
   */
  public H2DefinedTableBase(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param orm
   * @param valueType
   */
  public H2DefinedTableBase(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Override
  public H2DefinedTable<T> createTableIfNotExists(CsvRead csvRead) {
    getOrm()
        .execute(
            getTableDefinition().getCreateTableIfNotExistsStatement()
                + " as select * from "
                + csvRead);
    return this;
  }
}
