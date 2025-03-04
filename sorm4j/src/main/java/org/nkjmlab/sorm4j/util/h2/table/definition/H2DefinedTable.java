package org.nkjmlab.sorm4j.util.h2.table.definition;

import org.nkjmlab.sorm4j.table.definition.DefinedTable;
import org.nkjmlab.sorm4j.util.h2.table.H2Table;

public interface H2DefinedTable<T> extends DefinedTable<T>, H2Table<T> {}
