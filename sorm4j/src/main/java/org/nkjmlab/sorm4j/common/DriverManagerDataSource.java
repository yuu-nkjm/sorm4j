package org.nkjmlab.sorm4j.common;

import javax.sql.DataSource;

import org.nkjmlab.sorm4j.internal.common.DriverManagerDataSourceImpl;

public interface DriverManagerDataSource extends DataSource {

  public static DriverManagerDataSource create(String jdbcUrl) {
    return create(jdbcUrl, null, null);
  }

  public static DriverManagerDataSource create(String jdbcUrl, String username, String password) {
    return DriverManagerDataSourceImpl.create(jdbcUrl, username, password);
  }
}
