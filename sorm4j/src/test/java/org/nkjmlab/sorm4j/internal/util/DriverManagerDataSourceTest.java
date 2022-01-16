package org.nkjmlab.sorm4j.internal.util;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.test.common.SormTestUtils;

class DriverManagerDataSourceTest {

  private static DriverManagerDataSource ds = SormTestUtils.createDriverManagerDataSource();

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
      ds.getConnection("", "");
    } catch (SQLException e) {
    }
  }

  @Test
  void testGetLogWriter() {
    try {
      ds.getLogWriter();
    } catch (SQLException e) {
    }
  }

  @Test
  void testSetLogWriter() {
    try {
      ds.setLogWriter(null);
    } catch (SQLException e) {
    }
  }

  @Test
  void testSetLoginTimeout() {
    try {
      ds.setLoginTimeout(0);
    } catch (SQLException e) {
    }
  }

  @Test
  void testGetLoginTimeout() {
    try {
      ds.getLoginTimeout();
    } catch (SQLException e) {
    }
  }

}
