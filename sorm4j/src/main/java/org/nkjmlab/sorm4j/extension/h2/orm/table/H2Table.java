package org.nkjmlab.sorm4j.extension.h2.orm.table;

import org.nkjmlab.sorm4j.table.Table;

public interface H2Table<T> extends Table<T>, H2TableOrm<T> {}
