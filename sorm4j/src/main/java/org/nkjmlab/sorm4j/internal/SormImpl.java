package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.common.JdbcTableMetaData;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.TableSql;
import org.nkjmlab.sorm4j.internal.result.ResultSetStreamSorm;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.JdbcDatabaseMetaData;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.result.RowMap;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.table.BasicTable;
import org.nkjmlab.sorm4j.table.Table;

/**
 * An entry point of object-relation mapping.
 *
 * @author nkjm
 *
 */
public final class SormImpl implements Sorm {

  public static final SormContextImpl DEFAULT_CONTEXT =
      SormContextImpl.class.cast(SormContext.builder().build());

  private final DataSource dataSource;
  private final SormContextImpl sormContext;
  private final ConcurrentMap<String, Table<?>> tables;


  public static Sorm create(DataSource dataSource, SormContext context) {
    return new SormImpl(dataSource, (SormContextImpl) context);
  }

  public SormImpl(DataSource connectionSource, SormContextImpl context) {
    this.sormContext = context;
    this.dataSource = connectionSource;
    this.tables = new ConcurrentHashMap<>();
  }

  @Override
  public OrmTransaction open(int isolationLevel) {
    return new OrmTransactionImpl(getJdbcConnection(), sormContext, isolationLevel);
  }

  @Override
  public <R> R applyHandler(FunctionHandler<OrmConnection, R> connectionHandler) {
    try (OrmConnection conn = open()) {
      return connectionHandler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyHandler(int isolationLevel,
      FunctionHandler<OrmTransaction, R> transactionHandler) {
    try (OrmTransaction transaction = open(isolationLevel)) {
      R ret = transactionHandler.apply(transaction);
      transaction.rollback();
      return ret;
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public void acceptHandler(int isolationLevel,
      ConsumerHandler<OrmTransaction> transactionHandler) {
    try (OrmTransaction transaction = open(isolationLevel)) {
      transactionHandler.accept(transaction);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public SormContext getContext() {
    return sormContext;
  }

  @Override
  public OrmConnectionImpl open() {
    return new OrmConnectionImpl(getJdbcConnection(), sormContext);
  }

  @Override
  public DataSource getDataSource() {
    return this.dataSource;
  }

  @Override
  public Connection getJdbcConnection() {
    try {
      return dataSource.getConnection();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public void acceptHandler(ConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = open()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public String toString() {
    return "Sorm [dataSource=" + dataSource + ", sormContext=" + sormContext + "]";
  }


  private <R> R applyAndClose(FunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = open()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T> List<T> selectAll(Class<T> objectClass) {
    return applyAndClose(conn -> conn.selectAll(objectClass));
  }


  @Override
  public <T> T selectByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.selectByPrimaryKey(objectClass, primaryKeyValues));
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readFirst(objectClass, sql));
  }

  @Override
  public <T> T readFirst(Class<T> objectClass, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readFirst(objectClass, sql, parameters));
  }


  @Override
  public <T> List<T> readList(Class<T> objectClass, ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readList(objectClass, sql));
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readList(objectClass, sql, parameters));
  }

  @Override
  public <T> T readOne(Class<T> objectClass, ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readOne(objectClass, sql));
  }

  @Override
  public <T> T readOne(Class<T> objectClass, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readOne(objectClass, sql, parameters));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, ParameterizedSql sql) {
    return readTupleList(t1, t2, t3, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readTupleList(t1, t2, t3, sql, parameters));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2,
      ParameterizedSql sql) {
    return readTupleList(t1, t2, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    return applyAndClose(conn -> conn.readTupleList(t1, t2, sql, parameters));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> joinOn(Class<T1> t1, Class<T2> t2, String onCondition) {
    return applyAndClose(conn -> conn.joinOn(t1, t2, onCondition));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> joinUsing(Class<T1> t1, Class<T2> t2, String... columns) {
    return applyAndClose(conn -> conn.joinUsing(t1, t2, columns));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> joinOn(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String t1t2OnCondition, String t2t3OnCondition) {
    return applyAndClose(conn -> conn.joinOn(t1, t2, t3, t1t2OnCondition, t2t3OnCondition));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> leftJoinOn(Class<T1> t1, Class<T2> t2, String onCondition) {
    return applyAndClose(conn -> conn.leftJoinOn(t1, t2, onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoinOn(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String t1t2OnCondition, String t2t3OnCondition) {
    return applyAndClose(conn -> conn.leftJoinOn(t1, t2, t3, t1t2OnCondition, t2t3OnCondition));
  }


  @Override
  public <T> RowMapper<T> getRowMapper(Class<T> objectClass) {
    return applyAndClose(conn -> conn.getRowMapper(objectClass));
  }

  @Override
  public <T> ResultSetTraverser<List<T>> getResultSetTraverser(Class<T> objectClass) {
    return applyAndClose(conn -> conn.getResultSetTraverser(objectClass));
  }

  @Override
  public <T> boolean exists(T object) {
    return applyAndClose(conn -> conn.exists(object));
  }

  @Override
  public <T> boolean exists(String tableName, T object) {
    return applyAndClose(conn -> conn.exists(tableName, object));
  }

  @Override
  public <T> boolean exists(String tableName, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.exists(tableName, primaryKeyValues));
  }

  @Override
  public <T> boolean exists(Class<T> type, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.exists(type, primaryKeyValues));
  }

  @Override
  public <T> int[] delete(List<T> objects) {
    return applyAndClose(conn -> conn.delete(objects));
  }

  @Override
  public <T> int delete(T object) {
    return applyAndClose(conn -> conn.delete(object));
  }

  @Override
  public <T> int[] delete(@SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.delete(objects));
  }


  @Override
  public <T> int[] deleteIn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.deleteIn(tableName, objects));
  }

  @Override
  public <T> int deleteIn(String tableName, T object) {
    return applyAndClose(conn -> conn.deleteIn(tableName, object));
  }

  @Override
  public <T> int[] deleteIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.deleteIn(tableName, objects));
  }

  @Override
  public <T> int deleteAll(Class<T> objectClass) {
    return applyAndClose(conn -> conn.deleteAll(objectClass));
  }

  @Override
  public int deleteAllIn(String tableName) {
    return applyAndClose(conn -> conn.deleteAllIn(tableName));
  }

  @Override
  public <T> int deleteByPrimaryKey(Class<T> type, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.deleteByPrimaryKey(type, primaryKeyValues));
  }

  @Override
  public <T> int deleteByPrimaryKeyIn(String tableName, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.deleteByPrimaryKeyIn(tableName, primaryKeyValues));
  }

  @Override
  public <T> int[] insert(List<T> objects) {
    return applyAndClose(conn -> conn.insert(objects));
  }

  @Override
  public <T> int insert(T object) {
    return applyAndClose(conn -> conn.insert(object));
  }

  @Override
  public int insertMapIn(String tableName, RowMap object) {
    return applyAndClose(conn -> conn.insertMapIn(tableName, object));
  }

  @Override
  public int[] insertMapIn(String tableName, RowMap... objects) {
    return applyAndClose(conn -> conn.insertMapIn(tableName, objects));
  }

  @Override
  public int[] insertMapIn(String tableName, List<RowMap> objects) {
    return applyAndClose(conn -> conn.insertMapIn(tableName, objects));
  }



  @Override
  public <T> int[] insert(@SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insert(objects));
  }


  @Override
  public <T> InsertResult<T> insertAndGet(List<T> objects) {
    return applyAndClose(conn -> conn.insertAndGet(objects));
  }

  @Override
  public <T> InsertResult<T> insertAndGet(T object) {
    return applyAndClose(conn -> conn.insertAndGet(object));
  }

  @Override
  public <T> InsertResult<T> insertAndGet(@SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insertAndGet(objects));
  }



  @Override
  public <T> InsertResult<T> insertAndGetIn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.insertAndGetIn(tableName, objects));
  }

  @Override
  public <T> InsertResult<T> insertAndGetIn(String tableName, T object) {
    return applyAndClose(conn -> conn.insertAndGetIn(tableName, object));
  }

  @Override
  public <T> InsertResult<T> insertAndGetIn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insertAndGetIn(tableName, objects));
  }

  @Override
  public <T> int[] insertIn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.insertIn(tableName, objects));
  }

  @Override
  public <T> int insertIn(String tableName, T object) {
    return applyAndClose(conn -> conn.insertIn(tableName, object));
  }

  @Override
  public <T> int[] insertIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insertIn(tableName, objects));
  }

  @Override
  public <T> int[] merge(List<T> objects) {
    return applyAndClose(conn -> conn.merge(objects));
  }

  @Override
  public <T> int merge(T object) {
    return applyAndClose(conn -> conn.merge(object));
  }

  @Override
  public <T> int[] merge(@SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.merge(objects));
  }


  @Override
  public <T> int[] mergeIn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.mergeIn(tableName, objects));
  }

  @Override
  public <T> int mergeIn(String tableName, T object) {
    return applyAndClose(conn -> conn.mergeIn(tableName, object));
  }

  @Override
  public <T> int[] mergeIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.mergeIn(tableName, objects));
  }

  @Override
  public <T> int[] update(List<T> objects) {
    return applyAndClose(conn -> conn.update(objects));
  }

  @Override
  public <T> int update(T object) {
    return applyAndClose(conn -> conn.update(object));
  }

  @Override
  public <T> int[] update(@SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.update(objects));
  }

  @Override
  public <T> int updateByPrimaryKey(Class<T> clazz, RowMap object, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.updateByPrimaryKey(clazz, object, primaryKeyValues));
  }

  @Override
  public int updateByPrimaryKeyIn(String tableName, RowMap object, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.updateByPrimaryKeyIn(tableName, object, primaryKeyValues));
  }

  @Override
  public <T> int[] updateIn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.updateIn(tableName, objects));
  }

  @Override
  public <T> int updateIn(String tableName, T object) {
    return applyAndClose(conn -> conn.updateIn(tableName, object));
  }

  @Override
  public <T> int[] updateIn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.updateIn(tableName, objects));
  }


  @Override
  public String getTableName(Class<?> objectClass) {
    return applyAndClose(conn -> conn.getTableName(objectClass));
  }

  @Override
  public TableMetaData getTableMetaData(Class<?> objectClass) {
    return applyAndClose(conn -> conn.getTableMetaData(objectClass));
  }


  @Override
  public JdbcTableMetaData getJdbcTableMetaData(String tableName) {
    return applyAndClose(conn -> conn.getJdbcTableMetaData(tableName));
  }

  @Override
  public TableSql getTableSql(Class<?> objectClass) {
    return applyAndClose(conn -> conn.getTableSql(objectClass));
  }

  @Override
  public TableSql getTableSql(String tableName) {
    return applyAndClose(conn -> conn.getTableSql(tableName));
  }

  @Override
  public <T> T executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      ResultSetTraverser<T> traverser) {
    return applyAndClose(conn -> conn.executeQuery(statementSupplier, traverser));
  }

  @Override
  public <T> List<T> executeQuery(FunctionHandler<Connection, PreparedStatement> statementSupplier,
      RowMapper<T> rowMapper) {
    return applyAndClose(conn -> conn.executeQuery(statementSupplier, rowMapper));
  }

  @Override
  public <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> traverser) {
    return applyAndClose(conn -> conn.executeQuery(sql, traverser));
  }

  @Override
  public <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> mapper) {
    return applyAndClose(conn -> conn.executeQuery(sql, mapper));
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.executeUpdate(sql, parameters));
  }

  @Override
  public int executeUpdate(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.executeUpdate(sql));
  }


  @Override
  public JdbcDatabaseMetaData getJdbcDatabaseMetaData() {
    return applyAndClose(conn -> conn.getJdbcDatabaseMetaData());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Table<T> getTable(Class<T> type) {
    return (Table<T>) tables.computeIfAbsent(type.getName(), key -> new BasicTable<>(this, type));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Table<T> getTable(Class<T> type, String tableName) {
    return (Table<T>) tables.computeIfAbsent(type.getName() + "-" + tableName,
        key -> new BasicTable<>(this, type, tableName));
  }

  @Override
  public <T> ResultSetStream<T> streamAll(Class<T> type) {
    return stream(type, getTableSql(type).getSelectAllSql());
  }

  @Override
  public <T> ResultSetStream<T> stream(Class<T> type, ParameterizedSql sql) {
    return stream(type, sql.getSql(), sql.getParameters());
  }

  @Override
  public <T> ResultSetStream<T> stream(Class<T> type, String sql, Object... parameters) {
    return new ResultSetStreamSorm<T>(this, type, sql, parameters);
  }

}
