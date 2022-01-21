package org.nkjmlab.sorm4j.internal.util.command;

import java.util.List;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
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
  public <T> List<T> readList(Class<T> objectClass) {
    return conn.readList(objectClass, parse());
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
