package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.logging.LogContextImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

@Experimental
public abstract class H2ServerProcess {
  private static final SormLogger log = LogContextImpl.getDefaultLoggerSupplier().get();

  static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;
  static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

  final H2ServerProperties properties;

  public H2ServerProcess(H2ServerProperties properties) {
    this.properties = properties;
  }

  /**
   * Await to start the a H2 server process.
   *
   * @see #awaitStart(long, TimeUnit)
   * @return
   */
  public boolean awaitStart() {
    return awaitStart(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
  }

  /**
   * Starts H2 TCP server process and await server start with timeout.
   *
   * @param timeout
   * @param unit
   * @return true if the H2 TCP server is active
   */
  public boolean awaitStart(long timeout, TimeUnit unit) {
    return awaitStartServer(properties, timeout, unit);
  }

  static boolean isActive(int port) {
    try (ServerSocket socket = new ServerSocket(port)) {
      // the port is not used => not active.
      return false;
    } catch (IOException e) {
      return true;
    }
  }

  /**
   * Starts H2 server process and wait for start server.
   *
   * @param props
   * @param timeout
   * @param unit
   * @return true if the server is active.
   */
  static boolean awaitStartServer(H2ServerProperties props, long timeout, TimeUnit unit) {

    if (isActive(props.port)) {
      log.info(
          "H2 {} server has been already active at http://localhost:{}",
          props.serverType,
          props.port);
      return true;
    }
    try {
      log.info(
          "H2 {} Server will be start => {}",
          props.serverType,
          Stream.of(props.args)
              .filter(arg -> !arg.equals(props.password))
              .collect(Collectors.toList()));
      ProcessBuilder pb = new ProcessBuilder(props.args);
      pb.redirectErrorStream(true);
      log.info(
          "Wait up [{} {}] for H2 {} server starts at port {}.",
          timeout,
          unit,
          props.serverType,
          props.port);
      pb.start();
    } catch (IOException e) {
      throw Try.rethrow(e);
    }

    try {
      long start = System.currentTimeMillis();

      while (!isActive(props.port)) {
        long durationInMillis = System.currentTimeMillis() - start;
        if (durationInMillis > TimeUnit.MICROSECONDS.convert(timeout, unit)) {
          log.error("H2 server {} has not started yet.", props.serverType);
          return false;
        }
        TimeUnit.SECONDS.sleep(1);
      }
      log.info(
          "[{} msec] H2 {} server is active at http://localhost:{}",
          System.currentTimeMillis() - start,
          props.serverType,
          props.port);
      return true;
    } catch (InterruptedException e) {
      throw Try.rethrow(e);
    }
  }
}
