package org.nkjmlab.sorm4j.internal.sql;

import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.result.ResultSetStream;
import org.nkjmlab.sorm4j.result.Tuple2;
import org.nkjmlab.sorm4j.result.Tuple3;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.util.command.Command;

public abstract class AbstractCommand implements Command {

  protected final OrmConnection conn;

  public AbstractCommand(OrmConnection conn) {
    this.conn = conn;
  }

  protected abstract ParameterizedSql parse();

  @Override
  public <T> T executeQuery(ResultSetTraverser<T> resultSetTraverser) {
    return conn.executeQuery(parse(), resultSetTraverser);
  }

  @Override
  public <T> List<T> executeQuery(RowMapper<T> rowMapper) {
    return conn.executeQuery(parse(), rowMapper);
  }

  @Override
  public int executeUpdate() {
    return conn.executeUpdate(parse());
  }

  @Override
  public <T> T readOne(Class<T> objectClass) {
    return conn.readOne(objectClass, parse());
  }

  @Override
  public <T> T readFirst(Class<T> objectClass) {
    return conn.readFirst(objectClass, parse());
  }

  @Override
  public <T> ResultSetStream<T> readStream(Class<T> objectClass) {
    return conn.readStream(objectClass, parse());
  }

  @Override
  public <T> List<T> readList(Class<T> objectClass) {
    return conn.readList(objectClass, parse());
  }

  @Override
  public Map<String, Object> readMapOne() {
    return conn.readMapOne(parse());
  }

  @Override
  public List<Map<String, Object>> readMapList() {
    return conn.readMapList(parse());
  }

  @Override
  public ResultSetStream<Map<String, Object>> readMapStream() {
    return conn.readMapStream(parse());
  }

  @Override
  public Map<String, Object> readMapFirst() {
    return conn.readMapFirst(parse());
  }

  @Override
  public <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2) {
    return conn.readTupleList(t1, t2, parse());
  }

  @Override
  public <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2,
      Class<T3> t3) {
    return conn.readTupleList(t1, t2, t3, parse());
  }

}
