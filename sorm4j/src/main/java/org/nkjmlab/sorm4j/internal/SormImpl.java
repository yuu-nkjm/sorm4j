package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmStreamConnection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.InsertResult;
import org.nkjmlab.sorm4j.result.JdbcDatabaseMetaData;
import org.nkjmlab.sorm4j.result.TableMetaData;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.result.Tuple3;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.BasicCommand;
import org.nkjmlab.sorm4j.util.command.Command;
import org.nkjmlab.sorm4j.util.command.NamedParameterCommand;
import org.nkjmlab.sorm4j.util.command.OrderedParameterCommand;
import org.nkjmlab.sorm4j.util.table.BasicTable;
import org.nkjmlab.sorm4j.util.table.Table;

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

  public static Sorm create(DataSource dataSource, SormContext context) {
    return new SormImpl(dataSource, (SormContextImpl) context);
  }

  public SormImpl(DataSource connectionSource, SormContextImpl context) {
    this.sormContext = context;
    this.dataSource = connectionSource;
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
  public OrmConnection open() {
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
    return "Sorm [dataSource=" + dataSource + ", sormConfig=" + sormContext + "]";
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
    return applyAndClose(conn -> conn.readTupleList(t1, t2, t3, sql));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readTupleList(t1, t2, t3, sql, parameters));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2,
      ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readTupleList(t1, t2, sql));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2, String sql,
      Object... parameters) {
    return applyAndClose(conn -> conn.readTupleList(t1, t2, sql, parameters));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> join(Class<T1> t1, Class<T2> t2, String onCondition) {
    return applyAndClose(conn -> conn.join(t1, t2, onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> join(Class<T1> t1, Class<T2> t2, Class<T3> t3,
      String t1t2OnCondition, String t2t3OnCondition) {
    return applyAndClose(conn -> conn.join(t1, t2, t3, t1t2OnCondition, t2t3OnCondition));
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> leftJoin(Class<T1> t1, Class<T2> t2, String onCondition) {
    return applyAndClose(conn -> conn.join(t1, t2, onCondition));
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> leftJoin(Class<T1> t1, Class<T2> t2,
      String t1t2OnCondition, Class<T3> t3, String t2t3OnCondition) {
    return applyAndClose(conn -> conn.leftJoin(t1, t2, t1t2OnCondition, t3, t2t3OnCondition));
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
  public <T> int[] insert(List<T> objects) {
    return applyAndClose(conn -> conn.insert(objects));
  }

  @Override
  public <T> int insert(T object) {
    return applyAndClose(conn -> conn.insert(object));
  }

  @Override
  public int insertMapIn(String tableName, Map<String, Object> object) {
    return applyAndClose(conn -> conn.insertMapIn(tableName, object));
  }

  @Override
  public int[] insertMapIn(String tableName,
      @SuppressWarnings("unchecked") Map<String, Object>... objects) {
    return applyAndClose(conn -> conn.insertMapIn(tableName, objects));
  }

  @Override
  public int[] insertMapIn(String tableName, List<Map<String, Object>> objects) {
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
  public TableMetaData getTableMetaData(String tableName) {
    return applyAndClose(conn -> conn.getTableMetaData(tableName));
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
  public Command createCommand(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.createCommand(sql));
  }

  @Override
  public BasicCommand createCommand(String sql) {
    return applyAndClose(conn -> conn.createCommand(sql));
  }

  @Override
  public OrderedParameterCommand createCommand(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.createCommand(sql, parameters));
  }

  @Override
  public NamedParameterCommand createCommand(String sql, Map<String, Object> parameters) {
    return applyAndClose(conn -> conn.createCommand(sql, parameters));
  }

  @Override
  public JdbcDatabaseMetaData getJdbcDatabaseMetaData() {
    return applyAndClose(conn -> conn.getJdbcDatabaseMetaData());
  }

  @Override
  public <T> Table<T> getTable(Class<T> objectClass) {
    return new BasicTable<>(this, objectClass);
  }

  @Override
  public <T> void acceptHandler(FunctionHandler<OrmStreamConnection, Stream<T>> streamGenerator,
      ConsumerHandler<Stream<T>> streamHandler) {
    try (OrmConnection conn = open(); Stream<T> stream = streamGenerator.apply(conn)) {
      streamHandler.accept(stream);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <T, R> R applyHandler(FunctionHandler<OrmStreamConnection, Stream<T>> streamGenerator,
      FunctionHandler<Stream<T>, R> streamHandler) {
    try (OrmConnection conn = open(); Stream<T> stream = streamGenerator.apply(conn)) {
      return streamHandler.apply(stream);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

}
