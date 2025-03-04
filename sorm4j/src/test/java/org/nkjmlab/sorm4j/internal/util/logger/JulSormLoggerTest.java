package org.nkjmlab.sorm4j.internal.util.logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.context.logging.SormLogger;

class JulSormLoggerTest {

  private JulSormLogger logger;
  private Logger mockLogger;

  @BeforeEach
  void setUp() {
    mockLogger = mock(Logger.class);
    logger = new JulSormLogger(mockLogger);
  }

  @Test
  void testTrace() {
    logger.trace("Trace message {0}", 1);
    verify(mockLogger, times(1)).finer(anyString());
  }

  @Test
  void testDebug() {
    logger.debug("Debug message {0}", 2);
    verify(mockLogger, times(1)).fine(anyString());
  }

  @Test
  void testInfo() {
    logger.info("Info message {0}", 3);
    verify(mockLogger, times(1)).info(anyString());
  }

  @Test
  void testWarn() {
    logger.warn("Warn message {0}", 4);
    verify(mockLogger, times(1)).warning(anyString());
  }

  @Test
  void testError() {
    logger.error("Error message {0}", 5);
    verify(mockLogger, times(1)).severe(anyString());
  }

  @Test
  void testTraceWithDepth() {
    logger.trace(1, "Trace message {0}", 1);
    verify(mockLogger, times(1)).finer(anyString());
  }

  @Test
  void testDebugWithDepth() {
    logger.debug(1, "Debug message {0}", 2);
    verify(mockLogger, times(1)).fine(anyString());
  }

  @Test
  void testInfoWithDepth() {
    logger.info(1, "Info message {0}", 3);
    verify(mockLogger, times(1)).info(anyString());
  }

  @Test
  void testWarnWithDepth() {
    logger.warn(1, "Warn message {0}", 4);
    verify(mockLogger, times(1)).warning(anyString());
  }

  @Test
  void testErrorWithDepth() {
    logger.error(1, "Error message {0}", 5);
    verify(mockLogger, times(1)).severe(anyString());
  }

  @Test
  void testGetLogger() {
    SormLogger obtainedLogger = JulSormLogger.getLogger();
    assertNotNull(obtainedLogger);
  }
}
