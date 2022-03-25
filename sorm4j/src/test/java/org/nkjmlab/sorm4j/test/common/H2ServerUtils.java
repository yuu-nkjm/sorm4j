package org.nkjmlab.sorm4j.test.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.internal.util.Try;

public class H2ServerUtils {

  private static final Logger log = LogManager.getLogger();

  public static class H2ServerProperties {

    public final String name;
    public final int port;
    public final String password;
    public final String[] args;
    public final String argsWithoutPassword;

    public static class Options {
      public static final String IF_NOT_EXISTS = "-ifNotExists";
    }

    public H2ServerProperties(String name, int port, String password, String argsWithoutPassword,
        String[] args) {
      this.name = name;
      this.port = port;
      this.password = password;
      this.argsWithoutPassword = argsWithoutPassword;
      this.args = args;
    }

    public static abstract class Builder<T extends H2ServerProperties> {
      protected String javaCommand = SystemPropertyUtils.findJavaCommand();
      protected String h2ClassPath = SystemPropertyUtils.findOneClassPathElement("^h2-.*.jar$");
      protected final String name;
      protected int port;
      protected String password = "";
      protected String[] options = new String[0];

      Builder(String name, int port) {
        this.name = name;
        this.port = port;
      }

      public String getJavaCommand() {
        return javaCommand;
      }

      public void setJavaCommand(String javaCommand) {
        this.javaCommand = javaCommand;
      }

      public String getH2ClassPath() {
        return h2ClassPath;
      }

      public void setH2ClassPath(String h2ClassPath) {
        this.h2ClassPath = h2ClassPath;
      }

      public Builder<T> setPort(int port) {
        this.port = port;
        return this;
      }

      public Builder<T> setPassword(String password) {
        this.password = password;
        return this;
      }

      public Builder<T> setOptions(String... options) {
        this.options = options;
        return this;
      }

      public abstract T build();

    }
  }
  public static class H2TcpServerProperties extends H2ServerProperties {

    private static final H2TcpServerProperties DEFAULT_PROPERTIES = builder().build();
    private static final H2TcpServerProperties DEFAULT_PROPERTIES_WITH_IF_NOT_EXISTS =
        builder().setOptions(H2ServerProperties.Options.IF_NOT_EXISTS).build();

    public H2TcpServerProperties(String name, int port, String password, String argsWithoutPassword,
        String[] args) {
      super(name, port, password, argsWithoutPassword, args);
    }

    public static TcpPropBuilder builder() {
      return new TcpPropBuilder();
    }

    public static class TcpPropBuilder extends Builder<H2TcpServerProperties> {
      private static final int DEFAULT_TCP_PORT = 9092;

      private TcpPropBuilder() {
        super("TCP", DEFAULT_TCP_PORT);
      }

      @Override
      public H2TcpServerProperties build() {
        List<String> args =
            new ArrayList<>(List.of(javaCommand, "-cp", h2ClassPath, "org.h2.tools.Server"));
        args.addAll(List.of("-tcp", "-tcpPort", port + ""));
        args.addAll(Arrays.asList(options));
        args.add("-tcpPassword");
        String argsWithoutPassword = String.join(" ", args) + " " + "****";
        args.add(password);
        return new H2TcpServerProperties(name, port, password, argsWithoutPassword,
            args.toArray(String[]::new));
      }
    }

    public static H2TcpServerProperties getDefaultProperties() {
      return DEFAULT_PROPERTIES;
    }

    public static H2TcpServerProperties getPropertiesWithIfNotExists() {
      return DEFAULT_PROPERTIES_WITH_IF_NOT_EXISTS;
    }

  }

  public static class H2WebConsoleServerProperties extends H2ServerProperties {

    public H2WebConsoleServerProperties(String name, int port, String password,
        String argsWithoutPassword, String[] args) {
      super(name, port, password, argsWithoutPassword, args);
    }

    public static WebConsolePropBuilder builder() {
      return new WebConsolePropBuilder();
    }

    public static class WebConsolePropBuilder extends Builder<H2WebConsoleServerProperties> {
      private static final int DEFAULT_WEBCONSOLE_PORT = 8082;

      private WebConsolePropBuilder() {
        super("WEB_CONSOLE", DEFAULT_WEBCONSOLE_PORT);
      }

      @Override
      public H2WebConsoleServerProperties build() {
        List<String> args =
            new ArrayList<>(List.of(javaCommand, "-cp", h2ClassPath, "org.h2.tools.Server"));
        args.addAll(List.of("-web", "-webPort", port + ""));
        args.addAll(Arrays.asList(options));
        args.add("-webAdminPassword");
        String argsWithoutPassword = String.join(" ", args) + " " + "****";
        args.add(password);
        return new H2WebConsoleServerProperties(name, port, password, argsWithoutPassword,
            args.toArray(String[]::new));
      }
    }
  }



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
      log.error(e, e);
      return null;
    }
  }

  /**
   * Open a new browser tab or window.
   *
   * @param dataSource
   * @param keepAlive is false, the Web console server shutdown after the main thread finishes.
   *
   * @see #startWebConsoleServerThread(boolean)
   */
  public static void openBrowser(DataSource dataSource, boolean keepAlive) {
    Try.runOrElseThrow(() -> openBrowser(dataSource.getConnection(), keepAlive), Try::rethrow);
  }


  /**
   * Shutdowns default TCP server binding on default port (9092).
   */
  public static void shutdownTcpServer(H2TcpServerProperties props) {
    shutdownTcpServer(props, Long.MAX_VALUE, TimeUnit.SECONDS);
  }

  public static void shutdownTcpServer(H2TcpServerProperties props, long timeout, TimeUnit unit) {
    if (!isActive(props.port)) {
      log.info("H2 TCP server is not active.");
      return;
    }
    try {
      log.info("H2 TCP server will shutdown ...");
      Server.shutdownTcpServer("tcp://localhost:" + props.port, props.password, false, false);
      long start = System.currentTimeMillis();
      while (isActive(props.port)) {
        long durationInMilli = System.currentTimeMillis() - start;
        if (durationInMilli > TimeUnit.MICROSECONDS.convert(timeout, unit)) {
          break;
        }
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (SQLException | InterruptedException e) {
      log.error(e.getMessage());
    }
    if (isActive(props.port)) {
      log.warn("H2 TCP server is still active.");
    } else {
      log.info("H2 TCP server stopped.");
    }

  }

  /**
   * Starts H2 server process and wait for start server.
   *
   * @param props
   */

  public static void startServerProcessAndWaitFor(H2ServerProperties props) {
    startServerProcessAndWaitFor(props, Long.MAX_VALUE, TimeUnit.SECONDS);
  }



  /**
   * Starts H2 server process and wait.
   *
   * @param props
   * @param timeout
   * @param unit
   */
  public static void startServerProcessAndWaitFor(H2ServerProperties props, long timeout,
      TimeUnit unit) {

    if (isActive(props.port)) {
      log.info("H2 {} server has been already activated at http://localhost:{}", props.name,
          props.port);
      return;
    }
    try {
      log.info("H2 {} Server will be start. args without password={}", props.name,
          props.argsWithoutPassword);
      ProcessBuilder pb = new ProcessBuilder(props.args);
      pb.redirectErrorStream(true);
      log.info("Wait up to [{} {}] for H2 {} server start at port {}.", timeout, unit, props.name,
          props.port);
      pb.start();
      long start = System.currentTimeMillis();

      while (!isActive(props.port)) {
        long durationInMilli = System.currentTimeMillis() - start;
        if (durationInMilli > TimeUnit.MICROSECONDS.convert(timeout, unit)) {
          log.error("Fail to start or has not started h2 {} server yet.", props.name);
          return;
        }
        TimeUnit.SECONDS.sleep(1);
      }
      log.info("[{} msec] H2 {} server is available at http://localhost:{}",
          System.currentTimeMillis() - start, props.name, props.port);
    } catch (IOException | InterruptedException e) {
      throw Try.rethrow(e);
    }

  }


  public static void startDefaultTcpServerProcessAndWaitFor() {
    startServerProcessAndWaitFor(H2TcpServerProperties.builder().build());
  }

  public static void startDefaultWebConsoleServerProcessAndWaitFor() {
    startServerProcessAndWaitFor(H2WebConsoleServerProperties.builder().build());
  }


}
