package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.table_def.BasicTable;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class BasicH2Table<T> extends BasicTable<T> implements H2Table<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public BasicH2Table(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public BasicH2Table(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Experimental
  @Override
  public BasicH2Table<T> createTableIfNotExists() {
    super.createTableIfNotExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> createIndexesIfNotExists() {
    super.createIndexesIfNotExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> dropTableIfExists() {
    super.dropTableIfExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> dropTableIfExistsCascade() {
    super.dropTableIfExistsCascade();
    return this;
  }
}
