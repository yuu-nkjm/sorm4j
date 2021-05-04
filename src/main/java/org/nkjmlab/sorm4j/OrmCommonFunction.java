package org.nkjmlab.sorm4j;

import java.util.Map;
import org.nkjmlab.sorm4j.sql.BasicCommand;
import org.nkjmlab.sorm4j.sql.Command;
import org.nkjmlab.sorm4j.sql.NamedParameterCommand;
import org.nkjmlab.sorm4j.sql.OrderedParameterCommand;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public interface OrmCommonFunction
    extends OrmMapReader, SqlExecutor, TransactionFunction, AutoCloseable {


  /**
   * Creates a {@link Command} from SQL string.
   *
   * @param sql
   * @return
   */
  Command createCommand(ParameterizedSql sql);


  /**
   * Creates a {@link BasicCommand} from SQL string.
   *
   * @param sql
   * @return
   */
  BasicCommand createCommand(String sql);


  /**
   * Creates a {@link OrderedParameterCommand} from SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  OrderedParameterCommand createCommand(String sql, Object... parameters);

  /**
   * Creates a {@link NamedParameterCommand} from SQL string.
   *
   * @param sql
   * @param parameters
   * @return
   */
  NamedParameterCommand createCommand(String sql, Map<String, Object> parameters);

}
