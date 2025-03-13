package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.extension.h2.tools.server.H2Server;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * A class that manages an H2 Web Console server. This class provides functionalities to start and
 * stop the embedded H2 Web Console server, open database sessions, and configure server properties
 * using a builder pattern.
 */
public class H2WebServer implements H2Server {

  private final Server server;
  private final DataSource dataSource;

  /**
   * Initializes the H2 Web Console Service with the specified properties.
   *
   * @param properties the properties for configuring the H2 Web Console server
   */
  public H2WebServer(H2WebServerProperties properties, DataSource dataSource) {
    try {
      this.server = Server.createWebServer(properties.toArgs());
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
    this.dataSource = dataSource;
  }

  /** Stops the H2 Web Console server. If the server is not running, this method does nothing. */
  @Override
  public void stop() {
    if (!isRunning()) {
      return;
    }
    server.stop();
  }

  @Override
  public Server getServer() {
    return server;
  }

  public WebServer getWebServer() {
    return (WebServer) getService();
  }

  /**
   * Creates a new session in the H2 Web Console using a connection obtained from the {@link
   * DataSource}.
   *
   * @return the URL of the Web Console session
   * @throws RuntimeException if any exception occurs while opening the session
   */
  public String createSession() {
    try (Connection conn = dataSource.getConnection()) {
      return createSession(conn);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Creates a new session in the H2 Web Console using the given database connection.
   *
   * <p>This method registers the provided {@link Connection} with the H2 Web Console and returns
   * the corresponding session URL.
   *
   * @param conn the database connection to be used for creating the session
   * @return the URL of the Web Console session
   * @throws RuntimeException if an error occurs while opening the session
   */
  public String createSession(Connection conn) {
    try {
      return getWebServer().addSession(conn);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Opens a new session in the H2 Web Console using a connection obtained from the {@link
   * DataSource}. Automatically opens a browser window for accessing the session.
   *
   * @return the URL of the Web Console session
   * @throws RuntimeException if any exception occurs while opening the session
   */
  public String openBrowser() {
    try {
      String url = createSession();
      Server.openBrowser(url);
      return url;
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Creates a new {@link Builder} instance for constructing an {@code H2WebServer}.
   *
   * @return a new {@code Builder} instance
   */
  public static Builder builder(DataSource dataSource) {
    return new Builder(dataSource);
  }

  /** A builder for constructing instances of {@code H2WebServer}. */
  public static class Builder {
    private static final int DEFAULT_WEB_PORT = 8082;
    private int webPort = DEFAULT_WEB_PORT;
    private boolean webSSL;
    private boolean webAllowOthers;
    private boolean webDaemon;
    private boolean trace;
    private boolean ifExists;
    private boolean ifNotExists;
    private String baseDir;
    private DataSource dataSource;

    public Builder(DataSource dataSource) {
      this.dataSource = dataSource;
    }

    public Builder webPort(int port) {
      this.webPort = port;
      return this;
    }

    public Builder webSSL(boolean webSSL) {
      this.webSSL = webSSL;
      return this;
    }

    public Builder webAllowOthers(boolean webAllowOthers) {
      this.webAllowOthers = webAllowOthers;
      return this;
    }

    public Builder webDaemon(boolean webDaemon) {
      this.webDaemon = webDaemon;
      return this;
    }

    public Builder trace(boolean trace) {
      this.trace = trace;
      return this;
    }

    public Builder ifExists(boolean ifExists) {
      this.ifExists = ifExists;
      return this;
    }

    public Builder ifNotExists(boolean ifNotExists) {
      this.ifNotExists = ifNotExists;
      return this;
    }

    public Builder baseDir(String baseDir) {
      this.baseDir = baseDir;
      return this;
    }

    public H2WebServer build() {
      return new H2WebServer(
          new H2WebServerProperties(
              webPort, webSSL, webAllowOthers, webDaemon, trace, ifExists, ifNotExists, baseDir),
          dataSource);
    }
  }

  /** A record that holds configuration properties for an H2 Web Console server. */
  public static record H2WebServerProperties(
      int webPort,
      boolean webSSL,
      boolean webAllowOthers,
      boolean webDaemon,
      boolean trace,
      boolean ifExists,
      boolean ifNotExists,
      String baseDir) {

    /**
     * Converts the properties into an array of command-line arguments for the H2 server.
     *
     * @return an array of arguments for the H2 Web Console server
     */
    public String[] toArgs() {
      List<String> args = new ArrayList<>();
      args.add("-webPort");
      args.add(String.valueOf(webPort));

      if (webSSL) {
        args.add("-webSSL");
      }
      if (webAllowOthers) {
        args.add("-webAllowOthers");
      }
      if (webDaemon) {
        args.add("-webDaemon");
      }
      if (trace) {
        args.add("-trace");
      }
      if (ifExists) {
        args.add("-ifExists");
      }
      if (ifNotExists) {
        args.add("-ifNotExists");
      }
      if (baseDir != null) {
        args.add("-baseDir");
        args.add(baseDir);
      }

      return args.toArray(String[]::new);
    }
  }
}
