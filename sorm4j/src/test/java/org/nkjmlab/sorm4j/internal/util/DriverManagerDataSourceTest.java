package org.nkjmlab.sorm4j.internal.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DriverManagerDataSourceTest {

  private static DataSource ds = SormTestUtils.createNewDatabaseDataSource();

  @Test
  void testGetParentLogger() {
    assertThrows(UnsupportedOperationException.class, () -> ds.getParentLogger());
  }

  @Test
  void testUnwrap() {
    assertThrows(UnsupportedOperationException.class, () -> ds.unwrap(getClass()));
  }

  @Test
  void testIsWrapperFor() {
    assertThrows(UnsupportedOperationException.class, () -> ds.isWrapperFor(getClass()));
  }

  @Test
  void testGetConnectionStringString() {
    try {
      ds.getConnection("sa", "password");
    } catch (SQLException e) {
      fail();
    }
  }

  @Test
  void testGetLogWriter() {
    try {
      ds.getLogWriter();
    } catch (SQLException e) {
      fail();
    }
  }

  @Test
  void testSetLogWriter() {
    try {
      ds.setLogWriter(null);
    } catch (SQLException e) {
      fail();
    }
  }

  @Test
  void testSetLoginTimeout() {
    try {
      ds.setLoginTimeout(0);
    } catch (SQLException e) {
      fail();
    }
  }

  @Test
  void testGetLoginTimeout() {
    try {
      ds.getLoginTimeout();
    } catch (SQLException e) {
      fail();
    }
  }
}
