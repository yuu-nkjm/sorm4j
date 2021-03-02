package org.nkjmlab.sorm4j;

public interface TypedOrmMapper<T>
    extends TypeOrmReader<T>, TypedOrmUpdater<T>, OrmMapReader, SqlExecutor {

  String getTableName();

}
