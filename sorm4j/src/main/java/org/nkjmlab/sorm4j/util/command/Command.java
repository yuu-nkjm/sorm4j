package org.nkjmlab.sorm4j.util.command;

import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.common.Tuple.Tuple2;
import org.nkjmlab.sorm4j.common.Tuple.Tuple3;
import org.nkjmlab.sorm4j.mapping.ResultSetTraverser;
import org.nkjmlab.sorm4j.mapping.RowMapper;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;


/**
 * An executable request
 *
 * @author nkjm
 *
 */
@Experimental
public interface Command {



  /**
   * Creates a {@link Command} from SQL string.
   *
   * @param sql
   * @return
   */

  static Command create(OrmConnection conn, ParameterizedSql sql) {
    return BasicCommand.from(conn, sql.getSql()).addParameter(sql.getParameters());
  }


  /**
   * Creates a {@link BasicCommand} from SQL string.
   *
   * @param sql
   * @return
   */

  static BasicCommand create(OrmConnection conn, String sql) {
    return BasicCommand.from(conn, sql);
  }


  /**
   * Creates a {@link NamedParameterCommand} from SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static NamedParameterCommand create(OrmConnection conn, String sql,
      Map<String, Object> parameters) {
    return NamedParameterCommand.of(conn, sql).bindAll(parameters);
  }


  /**
   * Creates a {@link OrderedParameterCommand} from SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  static OrderedParameterCommand create(OrmConnection conn, String sql, Object... parameters) {
    return OrderedParameterCommand.of(conn, sql).addParameter(parameters);
  }

  /**
   * Executes a query and apply the given handler to the returned result set.
   *
   * @param <T>
   * @param resultSetTraverser
   * @return
   */
  <T> T executeQuery(ResultSetTraverser<T> resultSetTraverser);


  /**
   * Executes a query and apply the given mapper to the each row in returned result set.
   *
   * @param <T>
   * @param rowMapper
   * @return
   */
  <T> List<T> executeQuery(RowMapper<T> rowMapper);

  /**
   * Executes an update and returns the number of rows modified.
   *
   * @return
   */
  int executeUpdate();


  /**
   * Reads an object from the database.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> T readFirst(Class<T> objectClass);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> List<T> readList(Class<T> objectClass);

  /**
   * Reads a list of objects from the database by mapping the results of the parameterized SQL query
   * into instances of the given object class. Only the columns returned from the SQL query will be
   * set into the object instance.
   *
   * @param <T>
   * @param objectClass
   * @return
   */
  <T> T readOne(Class<T> objectClass);

  /**
   * Reads results as List of {@link Tuple2} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @return
   */
  @Experimental
  <T1, T2> List<Tuple2<T1, T2>> readTupleList(Class<T1> t1, Class<T2> t2);

  /**
   * Reads results as List of {@link Tuple3} for reading JOIN SQL results typically.
   *
   * @see {@link OrmColumnAliasPrefix} for use column alias prefix.
   *
   * @param <T1>
   * @param <T2>
   * @param t1
   * @param t2
   * @return
   */

  @Experimental
  <T1, T2, T3> List<Tuple3<T1, T2, T3>> readTupleList(Class<T1> t1, Class<T2> t2, Class<T3> t3);



}
