package org.nkjmlab.sorm4j.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.basic.ConsumerHandler;
import org.nkjmlab.sorm4j.basic.FunctionHandler;
import org.nkjmlab.sorm4j.basic.ResultSetTraverser;
import org.nkjmlab.sorm4j.basic.RowMapper;
import org.nkjmlab.sorm4j.command.BasicCommand;
import org.nkjmlab.sorm4j.command.Command;
import org.nkjmlab.sorm4j.command.NamedParameterCommand;
import org.nkjmlab.sorm4j.command.OrderedParameterCommand;
import org.nkjmlab.sorm4j.common.InsertResult;
import org.nkjmlab.sorm4j.common.TableMetaData;
import org.nkjmlab.sorm4j.common.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple3;
import org.nkjmlab.sorm4j.extension.SormContext;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

/**
 * An entry point of object-relation mapping.
 *
 * @author nkjm
 *
 */
public final class SormImpl implements Sorm {

  private final DataSource dataSource;
  private final SormContext sormContext;

  @Experimental
  public static Sorm create(DataSource dataSource, SormContext context) {
    return new SormImpl(dataSource, context);
  }

  public SormImpl(DataSource connectionSource, SormContext context) {
    this.sormContext = context;
    this.dataSource = connectionSource;
  }

  @Override
  public OrmTransaction openTransaction() {
    return new OrmTransactionImpl(getJdbcConnection(), sormContext);
  }


  @Override
  public <R> R apply(FunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = openConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public <R> R applyTransactionHandler(FunctionHandler<OrmTransaction, R> handler) {
    try (OrmTransaction transaction = openTransaction()) {
      R ret = handler.apply(transaction);
      transaction.commit();
      return ret;
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public <R> R applyJdbcConnectionHandler(FunctionHandler<Connection, R> handler) {
    try (Connection conn = getJdbcConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public String getContextString() {
    return sormContext.toString();
  }


  @Override
  public OrmConnection openConnection() {
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
  public void accept(ConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = openConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }



  @Override
  public void acceptTransactionHandler(ConsumerHandler<OrmTransaction> handler) {
    try (OrmTransaction transaction = openTransaction()) {
      handler.accept(transaction);
      transaction.commit();
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void acceptJdbcConnectionHandler(ConsumerHandler<Connection> handler) {
    try (Connection conn = getJdbcConnection()) {
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
    try (OrmConnection conn = openConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  private void acceptAndClose(ConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = openConnection()) {
      handler.accept(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }


  @Override
  public <T> List<T> readAll(Class<T> objectClass) {
    return applyAndClose(conn -> conn.readAll(objectClass));
  }


  @Override
  public <T> T readByPrimaryKey(Class<T> objectClass, Object... primaryKeyValues) {
    return applyAndClose(conn -> conn.readByPrimaryKey(objectClass, primaryKeyValues));
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
  public <T> int[] deleteOn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.deleteOn(tableName, objects));
  }

  @Override
  public <T> int deleteOn(String tableName, T object) {
    return applyAndClose(conn -> conn.deleteOn(tableName, object));
  }

  @Override
  public <T> int[] deleteOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.deleteOn(tableName, objects));
  }

  @Override
  public <T> int deleteAll(Class<T> objectClass) {
    return applyAndClose(conn -> conn.deleteAll(objectClass));
  }

  @Override
  public int deleteAllOn(String tableName) {
    return applyAndClose(conn -> conn.deleteAllOn(tableName));
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
  public int insertMapOn(String tableName, Map<String, Object> object) {
    return applyAndClose(conn -> conn.insertMapOn(tableName, object));
  }

  @Override
  public int[] insertMapOn(String tableName,
      @SuppressWarnings("unchecked") Map<String, Object>... objects) {
    return applyAndClose(conn -> conn.insertMapOn(tableName, objects));
  }

  @Override
  public int[] insertMapOn(String tableName, List<Map<String, Object>> objects) {
    return applyAndClose(conn -> conn.insertMapOn(tableName, objects));
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
  public <T> InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.insertAndGetOn(tableName, objects));
  }

  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName, T object) {
    return applyAndClose(conn -> conn.insertAndGetOn(tableName, object));
  }

  @Override
  public <T> InsertResult<T> insertAndGetOn(String tableName,
      @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insertAndGetOn(tableName, objects));
  }



  @Override
  public <T> int[] insertOn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.insertOn(tableName, objects));
  }

  @Override
  public <T> int insertOn(String tableName, T object) {
    return applyAndClose(conn -> conn.insertOn(tableName, object));
  }

  @Override
  public <T> int[] insertOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.insertOn(tableName, objects));
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
  public <T> int[] mergeOn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.mergeOn(tableName, objects));
  }

  @Override
  public <T> int mergeOn(String tableName, T object) {
    return applyAndClose(conn -> conn.mergeOn(tableName, object));
  }

  @Override
  public <T> int[] mergeOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.mergeOn(tableName, objects));
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
  public <T> int[] updateOn(String tableName, List<T> objects) {
    return applyAndClose(conn -> conn.updateOn(tableName, objects));
  }

  @Override
  public <T> int updateOn(String tableName, T object) {
    return applyAndClose(conn -> conn.updateOn(tableName, object));
  }

  @Override
  public <T> int[] updateOn(String tableName, @SuppressWarnings("unchecked") T... objects) {
    return applyAndClose(conn -> conn.updateOn(tableName, objects));
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
  public RowMapper<Map<String, Object>> getRowToMapMapper() {
    return applyAndClose(conn -> conn.getRowToMapMapper());
  }

  @Override
  public ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser() {
    return applyAndClose(conn -> conn.getResultSetToMapTraverser());
  }

  @Override
  public Map<String, Object> readMapFirst(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readMapFirst(sql));
  }

  @Override
  public Map<String, Object> readMapFirst(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readMapFirst(sql, parameters));
  }

  @Override
  public List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readMapList(sql));
  }

  @Override
  public List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readMapList(sql, parameters));
  }

  @Override
  public Map<String, Object> readMapOne(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readMapOne(sql));
  }

  @Override
  public Map<String, Object> readMapOne(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readMapOne(sql, parameters));
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
  public void acceptWithLogging(ConsumerHandler<OrmConnection> handler) {
    sormContext.getLoggerContext().forceLogging = true;
    accept(handler);
    sormContext.getLoggerContext().forceLogging = false;
  }

  @Override
  public <R> R applyWithLogging(FunctionHandler<OrmConnection, R> handler) {
    sormContext.getLoggerContext().forceLogging = true;
    R ret = apply(handler);
    sormContext.getLoggerContext().forceLogging = false;
    return ret;
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

}
