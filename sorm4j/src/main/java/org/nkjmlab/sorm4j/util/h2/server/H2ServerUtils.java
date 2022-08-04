package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

@Experimental
public class H2ServerUtils {
  private static final SormLogger log = LoggerContext.getDefaultLoggerSupplier().get();

  private static boolean isActive(int port) {
    try (ServerSocket socket = new ServerSocket(port)) {
      return false;
    } catch (IOException e) {
      return true;
    }
  }


  private static WebServer startWebConsoleServerThread(boolean webDaemon) {
    try {
      Server server =
          Server.createWebServer(webDaemon ? new String[] {"-webPort", "0", "-webDaemon"}
              : new String[] {"-webPort", "0"});
      server.start();
      log.info("H2 Web Server is start at {}", server.getURL());
      WebServer webServer = (WebServer) server.getService();
      return webServer;
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Open a new browser tab or window.
   *
   * @param conn
   * @param keepAlive is false, the Web console server shutdown after the main thread finishes.
   *
   * @see #startWebConsoleServerThread(boolean)
   *
   * @return
   */

  public static WebServer openBrowser(Connection conn, boolean keepAlive) {
    try {
      boolean webDaemon = !keepAlive;
      WebServer webServer = startWebConsoleServerThread(webDaemon);
      webServer.addSession(conn);
      String url = webServer.addSession(conn);
      Server.openBrowser(url);
      log.info("Database open on browser = {}", url);
      return webServer;
    } catch (Exception e) {
      log.error("{}", e.getMessage());
      return null;
    }
  }

  /**
   * Open a new browser tab or window.
   *
   * @param dataSource
   * @param keepAlive is false, the Web console server shutdown after the main thread finishes.
   * @return
   *
   * @see #startWebConsoleServerThread(boolean)
   */
  public static WebServer openBrowser(DataSource dataSource, boolean keepAlive) {
    return Try.getOrElseThrow(() -> openBrowser(dataSource.getConnection(), keepAlive),
        Try::rethrow);
  }


  /**
   * Shutdowns default TCP server.
   *
   * @return
   */
  public static boolean awaitShutdownTcpServer(H2ServerProperties props) {
    return awaitShutdownTcpServer(props, Long.MAX_VALUE, TimeUnit.SECONDS);
  }

  public static boolean awaitShutdownTcpServer(H2ServerProperties props, long timeout,
      TimeUnit unit) {
    if (!isActive(props.port)) {
      log.info("H2 TCP server is not active at {}.", props.port);
      return true;
    }
    try {
      log.info("H2 TCP server will shutdown ...");
      Server.shutdownTcpServer("tcp://localhost:" + props.port, props.password, false, false);
    } catch (SQLException e) {
      log.error(e.getMessage());
      return false;
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

    return true;
  }

  /**
   * Starts H2 server process and wait for start server.
   *
   * @param props
   */

  public static boolean awaitStartServer(H2ServerProperties props) {
    return awaitStartServer(props, Long.MAX_VALUE, TimeUnit.SECONDS);
  }



  /**
   * Starts H2 server process and wait.
   *
   * @param props
   * @param timeout
   * @param unit
   * @return
   */
  public static boolean awaitStartServer(H2ServerProperties props, long timeout, TimeUnit unit) {

    if (isActive(props.port)) {
      log.info("H2 {} server has been already active at http://localhost:{}", props.serverType,
          props.port);
      return true;
    }
    try {
      log.info("H2 {} Server will be start => {}", props.serverType, Stream.of(props.args)
          .filter(arg -> !arg.equals(props.password)).collect(Collectors.toList()));
      ProcessBuilder pb = new ProcessBuilder(props.args);
      pb.redirectErrorStream(true);
      log.info("Wait up to [{} {}] for H2 {} server start at port {}.", timeout, unit,
          props.serverType, props.port);
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
      log.info("[{} msec] H2 {} server is active at http://localhost:{}",
          System.currentTimeMillis() - start, props.serverType, props.port);
      return true;
    } catch (InterruptedException e) {
      throw Try.rethrow(e);
    }

  }


  public static boolean awaitStartTcpServer() {
    return awaitStartServer(H2ServerProperties.createTcpServerPropertiesBuilder().build());
  }

  public static boolean awaitStartWebConsoleServer(String password) {
    return awaitStartServer(
        H2ServerProperties.createWebConsoleServerPropertiesBuilder().setPassword(password).build());
  }

  public static boolean awaitShutdownTcpServer() {
    return awaitShutdownTcpServer(H2ServerProperties.createTcpServerPropertiesBuilder().build());
  }


}
