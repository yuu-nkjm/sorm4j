package org.nkjmlab.sorm4j.extension.h2.orm.table.definition;

import org.nkjmlab.sorm4j.extension.h2.orm.table.H2Table;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;

public interface H2DefinedTable<T> extends DefinedTable<T>, H2Table<T> {}
