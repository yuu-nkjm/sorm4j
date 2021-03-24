package org.nkjmlab.sorm4j.internal.sql;

import java.util.Map;
import org.nkjmlab.sorm4j.sql.NamedParameterQuery;
import org.nkjmlab.sorm4j.sql.NamedParameterSql;
import org.nkjmlab.sorm4j.sql.SqlStatement;

/**
 * An implementation of {@link NamedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class NamedParameterQueryImpl<T> extends AbstractQuery<T>
    implements NamedParameterQuery<T> {

  private final NamedParameterSql namedParameterSql;

  NamedParameterQueryImpl(QueryExecutor<T> executor, String sql) {
    super(executor);
    this.namedParameterSql = NamedParameterSql.from(sql);
  }


  @Override
  public NamedParameterQuery<T> bindAll(Map<String, Object> namedParams) {
    namedParameterSql.bindAll(namedParams);
    return this;
  }

  @Override
  public NamedParameterQuery<T> bind(String key, Object value) {
    namedParameterSql.bind(key, value);
    return this;
  }

  @Override
  public SqlStatement toSqlStatement() {
    return namedParameterSql.toSqlStatement();
  }


  public static <T> NamedParameterQuery<T> createFrom(QueryExecutor<T> executor, String sql) {
    return new NamedParameterQueryImpl<>(executor, sql);
  }


}
