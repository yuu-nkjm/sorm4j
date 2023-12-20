package org.nkjmlab.sorm4j.util.h2;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.util.table_def.SimpleTableWithDefinition;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;

@Experimental
public class H2BasicTable<T> extends SimpleTableWithDefinition<T> implements H2Table<T> {

  /**
   * This table instance is bind to the table name defined in the given {@link TableDefinition}.
   *
   * @param sorm
   * @param valueType
   * @param tableDefinition
   */
  public H2BasicTable(Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    super(orm, valueType, tableDefinition);
  }

  /**
   * This table instance is bind to the table name defined in the given class.
   *
   * @param sorm
   * @param valueType
   */
  public H2BasicTable(Sorm orm, Class<T> valueType) {
    this(orm, valueType, TableDefinition.builder(valueType).build());
  }

  @Experimental
  @Override
  public H2BasicTable<T> createTableIfNotExists() {
    super.createTableIfNotExists();
    return this;
  }

  public H2BasicTable<T> createTableIfNotExists(CsvRead csvRead) {
    getOrm()
        .execute(
            getTableDefinition().getCreateTableIfNotExistsStatement()
                + " as select * from "
                + csvRead);
    return this;
  }

  @Experimental
  @Override
  public H2BasicTable<T> createIndexesIfNotExists() {
    super.createIndexesIfNotExists();
    return this;
  }

  @Experimental
  @Override
  public H2BasicTable<T> dropTableIfExists() {
    super.dropTableIfExists();
    return this;
  }

  @Experimental
  @Override
  public H2BasicTable<T> dropTableIfExistsCascade() {
    super.dropTableIfExistsCascade();
    return this;
  }
}
