package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.BasicCommand;
import org.nkjmlab.sorm4j.sql.Command;
import org.nkjmlab.sorm4j.sql.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;
import org.nkjmlab.sorm4j.sql.result.Tuple2;
import org.nkjmlab.sorm4j.sql.result.Tuple3;
import org.nkjmlab.sorm4j.typed.TypedOrm;

public class OrmImpl implements Orm {

  private final Sorm sorm;

  public OrmImpl(Sorm sorm) {
    this.sorm = sorm;
  }

  private <R> R applyAndClose(FunctionHandler<OrmConnection, R> handler) {
    try (OrmConnection conn = sorm.openConnection()) {
      return handler.apply(conn);
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  private void acceptAndClose(ConsumerHandler<OrmConnection> handler) {
    try (OrmConnection conn = sorm.openConnection()) {
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
  public <T> LazyResultSet<T> readAllLazy(Class<T> objectClass) {
    return applyAndClose(conn -> conn.readAllLazy(objectClass));
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
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readLazy(objectClass, sql));
  }

  @Override
  public <T> LazyResultSet<T> readLazy(Class<T> objectClass, String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readLazy(objectClass, sql, parameters));
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
  public <T> int[] delete(List<T> objects) {
    return applyAndClose(conn -> conn.delete(objects));
  }

  @Override
  public <T> int delete(T object) {
    return applyAndClose(conn -> conn.delete(object));
  }

  @Override
  public <T> int[] delete(T... objects) {
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
  public <T> int[] deleteOn(String tableName, T... objects) {
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
  public <T> int[] insert(T... objects) {
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
  public <T> InsertResult<T> insertAndGet(T... objects) {
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
  public <T> InsertResult<T> insertAndGetOn(String tableName, T... objects) {
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
  public <T> int[] insertOn(String tableName, T... objects) {
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
  public <T> int[] merge(T... objects) {
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
  public <T> int[] mergeOn(String tableName, T... objects) {
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
  public <T> int[] update(T... objects) {
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
  public <T> int[] updateOn(String tableName, T... objects) {
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
  public TableMetaData getTableMetaData(Class<?> objectClass, String tableName) {
    return applyAndClose(conn -> conn.getTableMetaData(objectClass, tableName));
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
  public LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql) {
    return applyAndClose(conn -> conn.readMapLazy(sql));
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    return applyAndClose(conn -> conn.readMapLazy(sql, parameters));
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
  public void acceptPreparedStatementHandler(ParameterizedSql sql,
      ConsumerHandler<PreparedStatement> handler) {
    acceptAndClose(conn -> conn.acceptPreparedStatementHandler(sql, handler));
  }

  @Override
  public <T> T applyPreparedStatementHandler(ParameterizedSql sql,
      FunctionHandler<PreparedStatement, T> handler) {
    return applyAndClose(conn -> conn.applyPreparedStatementHandler(sql, handler));
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
  public <S> TypedOrm<S> type(Class<S> objectClass) {
    return new TypedOrmImpl<>(objectClass, this);
  }


}
