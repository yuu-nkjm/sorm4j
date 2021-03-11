package org.nkjmlab.sorm4j.sqlstatement;

import java.util.Map;
import org.nkjmlab.sorm4j.TypedOrmReader;

/**
 * An implementation of {@link NamedParameterQuery}
 *
 * @author nkjm
 *
 * @param <T>
 */
public class NamedParameterQueryImpl<T> extends AbstQuery<T> implements NamedParameterQuery<T> {

  private final NamedParameterSql namedParameterSql;

  NamedParameterQueryImpl(TypedOrmReader<T> conn, String sql) {
    super(conn);
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


}
