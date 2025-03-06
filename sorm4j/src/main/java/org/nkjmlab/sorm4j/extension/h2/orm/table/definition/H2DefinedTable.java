package org.nkjmlab.sorm4j.extension.h2.orm.table.definition;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.H2Table;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTableBase.H2SimpleDefinedTable;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;

public interface H2DefinedTable<T> extends DefinedTable<T>, H2Table<T> {

  H2DefinedTable<T> createTableIfNotExists(CsvRead csvRead);

  public static <T> H2DefinedTable<T> of(Sorm orm, Class<T> valueType) {
    return new H2SimpleDefinedTable<>(orm, valueType);
  }

  public static <T> H2DefinedTable<T> of(
      Sorm orm, Class<T> valueType, TableDefinition tableDefinition) {
    return new H2SimpleDefinedTable<>(orm, valueType, tableDefinition);
  }
}
