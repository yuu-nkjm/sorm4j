package org.nkjmlab.sorm4j.util.h2.table;

import org.nkjmlab.sorm4j.table.Table;

public interface H2Table<T> extends Table<T>, H2TableOrm<T> {}
