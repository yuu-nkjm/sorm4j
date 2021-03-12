package org.nkjmlab.sorm4j;

import java.io.Closeable;
import org.nkjmlab.sorm4j.sqlstatement.NamedParameterSql;
import org.nkjmlab.sorm4j.sqlstatement.OrderedParameterSql;
import org.nkjmlab.sorm4j.sqlstatement.SelectBuilder;

/**
 * Main API for object relation mapping. The api consists of {@link OrmReader}, {@link OrmUpdater},
 * {@link OrmMapReader}, {@link SqlExecutor}and {@link TransactionFunction}.
 *
 * @author nkjm
 *
 */
public interface OrmConnection extends OrmReader, OrmUpdater, OrmMapReader, SqlExecutor,
    TransactionFunction, Closeable, AutoCloseable {

  /**
   * Create {@link NamedParameterSql} from SQL string.
   *
   * @param sql
   * @return
   */
  NamedParameterSql createNamedParameterSql(String sql);

  /**
   * Create {@link OrderedParameterSql} from SQL string.
   *
   * @param sql
   * @return
   */
  OrderedParameterSql createOrderedParameterSql(String sql);

  /**
   * Create {@link SelectBuilder}.
   *
   * @return
   */
  SelectBuilder createSelectBuilder();


}
