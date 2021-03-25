package org.nkjmlab.sorm4j.internal.mapping;

import org.nkjmlab.sorm4j.OrmMapReader;
import org.nkjmlab.sorm4j.SqlExecutor;
import org.nkjmlab.sorm4j.TypedOrmReader;
import org.nkjmlab.sorm4j.TypedOrmUpdater;

interface TypedOrmMapper<T>
    extends TypedOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader, SqlExecutor {

}
