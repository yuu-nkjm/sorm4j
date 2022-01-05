package org.nkjmlab.sorm4j.extension;

import java.sql.ResultSet;

public interface ColumnValueConverter {
  boolean isApplicable(SormOptions options, ResultSet resultSet, int column, int columnType,
      Class<?> toType);

  <T> T convertTo(SormOptions options, ResultSet resultSet, int column, int columnType,
      Class<T> toType);
}
