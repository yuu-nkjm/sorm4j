package org.nkjmlab.sorm4j.sql;

import java.sql.ResultSet;

@FunctionalInterface
public interface RowMapper<T> {
  T mapRow(ResultSet rs, int rowNum);
}
