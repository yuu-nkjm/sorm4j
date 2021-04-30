package org.nkjmlab.sorm4j.internal.util;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.DefaultResultSetConverter;

class LoggerFactoryTest {

  @Test
  void test() {
    LoggerFactory.error(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.warn(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.info(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.debug(false, getClass(), "Name is {}", "Alice");
    LoggerFactory.trace(false, getClass(), "Name is {}", "Alice");

    LoggerFactory.error(DefaultResultSetConverter.class, "{}", "hoge");
    LoggerFactory.error(DefaultResultSetConverter.class, "{}", "hoge");

  }

}
