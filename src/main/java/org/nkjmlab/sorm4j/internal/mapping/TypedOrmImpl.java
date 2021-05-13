package org.nkjmlab.sorm4j.internal.mapping;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.ConsumerHandler;
import org.nkjmlab.sorm4j.FunctionHandler;
import org.nkjmlab.sorm4j.Orm;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.sql.BasicCommand;
import org.nkjmlab.sorm4j.sql.Command;
import org.nkjmlab.sorm4j.sql.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.TableMetaData;
import org.nkjmlab.sorm4j.sql.result.InsertResult;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;
import org.nkjmlab.sorm4j.typed.TypedOrm;


public class TypedOrmImpl<T> implements TypedOrm<T> {

  private OrmImpl orm;
  private Class<T> objectClass;

  public TypedOrmImpl(Class<T> objectClass, OrmImpl orm) {
    this.orm = orm;
    this.objectClass = objectClass;
  }

  @Override
  public List<T> readAll() {
    return orm.readAll(objectClass);
  }

  @Override
  public LazyResultSet<T> readAllLazy() {
    return orm.readAllLazy(objectClass);
  }

  @Override
  public T readByPrimaryKey(Object... primaryKeyValues) {
    return orm.readByPrimaryKey(objectClass, primaryKeyValues);
  }

  @Override
  public T readFirst(ParameterizedSql sql) {
    return orm.readFirst(objectClass, sql);
  }

  @Override
  public T readFirst(String sql, Object... parameters) {
    return orm.readFirst(objectClass, sql, parameters);
  }

  @Override
  public LazyResultSet<T> readLazy(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LazyResultSet<T> readLazy(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> readList(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> readList(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T readOne(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T readOne(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RowMapper<T> getRowMapper() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultSetTraverser<List<T>> getResultSetTraverser() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean exists(T object) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int[] delete(List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int delete(T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] delete(T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] deleteOn(String tableName, List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int deleteOn(String tableName, T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] deleteOn(String tableName, T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int deleteAll() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int deleteAllOn(String tableName) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] insert(List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int insert(T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] insert(T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGet(List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGet(T object) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGet(T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T object) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InsertResult<T> insertAndGetOn(String tableName, T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] insertOn(String tableName, List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int insertOn(String tableName, T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] insertOn(String tableName, T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] merge(List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int merge(T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] merge(T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] mergeOn(String tableName, List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int mergeOn(String tableName, T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] mergeOn(String tableName, T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] update(List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int update(T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] update(T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int[] updateOn(String tableName, List<T> objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int updateOn(String tableName, T object) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int[] updateOn(String tableName, T... objects) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RowMapper<Map<String, Object>> getRowToMapMapper() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ResultSetTraverser<List<Map<String, Object>>> getResultSetToMapTraverser() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Object> readMapFirst(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Object> readMapFirst(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LazyResultSet<Map<String, Object>> readMapLazy(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Map<String, Object>> readMapList(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Map<String, Object>> readMapList(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Object> readMapOne(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Object> readMapOne(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Command createCommand(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BasicCommand createCommand(String sql) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OrderedParameterCommand createCommand(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NamedParameterCommand createCommand(String sql, Map<String, Object> parameters) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTableName(Class<?> objectClass) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TableMetaData getTableMetaData(Class<?> objectClass) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TableMetaData getTableMetaData(Class<?> objectClass, String tableName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void acceptPreparedStatementHandler(ParameterizedSql sql,
      ConsumerHandler<PreparedStatement> handler) {
    // TODO Auto-generated method stub

  }

  @Override
  public <T> T applyPreparedStatementHandler(ParameterizedSql sql,
      FunctionHandler<PreparedStatement, T> handler) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T executeQuery(ParameterizedSql sql, ResultSetTraverser<T> traverser) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> List<T> executeQuery(ParameterizedSql sql, RowMapper<T> mapper) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int executeUpdate(String sql, Object... parameters) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int executeUpdate(ParameterizedSql sql) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public <S> TypedOrm<S> type(Class<S> objectClass) {
    return new TypedOrmImpl<>(objectClass, orm);
  }

  @Override
  public Orm untype() {
    return orm;
  }

}
