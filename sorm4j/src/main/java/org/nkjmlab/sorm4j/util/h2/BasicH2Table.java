package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.table_def.BasicTableWithDefinition;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class BasicH2Table<T> extends BasicTableWithDefinition<T> implements H2Table<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDifinition
   */
  public BasicH2Table(Sorm sorm, Class<T> valueType, TableDefinition tableDifinition) {
    super(sorm, valueType, tableDifinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public BasicH2Table(Sorm sorm, Class<T> valueType) {
    this(sorm, valueType, TableDefinition.builder(valueType).build());
  }

  @Experimental
  @Override
  public BasicH2Table<T> createTableIfNotExists() {
    H2Table.super.createTableIfNotExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> createIndexesIfNotExists() {
    H2Table.super.createIndexesIfNotExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> dropTableIfExists() {
    H2Table.super.dropTableIfExists();
    return this;
  }

  @Experimental
  @Override
  public BasicH2Table<T> dropTableIfExistsCascade() {
    H2Table.super.dropTableIfExistsCascade();
    return this;
  }
}
