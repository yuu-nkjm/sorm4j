package org.nkjmlab.sorm4j.internal.util;

import org.junit.jupiter.api.Test;

class LoggerFactoryTest {

  @Test
  void test() {
    LoggerFactory.error(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.warn(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.info(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.debug(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.trace(false, getClass(), "Name is {}", "Alice");
  }

}
