package org.nkjmlab.sorm4j.util.h2.server;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

@Experimental
public class H2TcpServerProcess extends H2ServerProcess {

  private static final SormLogger log = LoggerContext.getDefaultLoggerSupplier().get();

  public H2TcpServerProcess(H2TcpServerProperties properties) {
    super(properties);
  }


  /**
   * @see #awaitShutdown(long, TimeUnit)
   * @return
   */
  public boolean awaitShutdown() {
    return awaitShutdown(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
  }

  /**
   * Shutdown H2 TCP server process and await server start with timeout.
   *
   * @param timeout
   * @param unit
   * @return true if the H2 TCP server is not active.
   */
  public boolean awaitShutdown(long timeout, TimeUnit unit) {
    return awaitShutdownTcpServer((H2TcpServerProperties) properties, timeout, unit);
  }

  private static boolean awaitShutdownTcpServer(H2TcpServerProperties props, long timeout,
      TimeUnit unit) {
    if (!isActive(props.port)) {
      log.info("H2 TCP server is not active at {}.", props.port);
      return true;
    }
    try {
      log.info("Wait up [{} {}] for H2 {} server shutdowns at port {}.", timeout, unit,
          props.serverType, props.port);
      Server.shutdownTcpServer("tcp://localhost:" + props.port, props.password, false, false);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }

    try {
      long start = System.currentTimeMillis();
      while (isActive(props.port)) {
        long durationInMilli = System.currentTimeMillis() - start;
        if (durationInMilli > TimeUnit.MICROSECONDS.convert(timeout, unit)) {
          log.warn("H2 TCP server is still active.");
          return false;
        }
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (InterruptedException e) {
      throw Try.rethrow(e);
    }
    log.info("H2 TCP server is not active.");
    return true;
  }

}
