package org.nkjmlab.sorm4j.internal.util;

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class JdbcTypeUtils {
  private JdbcTypeUtils() {}

  public static List<JDBCType> convert(int... sqlTypes) {
    return Arrays.stream(sqlTypes).mapToObj(i -> convert(i)).collect(Collectors.toList());
  }

  public static JDBCType convert(int sqlTypes) {
    return JDBCType.valueOf(sqlTypes);
  }

}
