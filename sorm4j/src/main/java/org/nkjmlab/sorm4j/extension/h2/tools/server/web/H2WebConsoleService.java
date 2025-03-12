package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * A service class to manage an H2 Web Console server. This class provides functionalities to start
 * an embedded H2 Web Console as a daemon, open database sessions in the console, and stop the
 * server when needed.
 */
public class H2WebConsoleService {

  private final WebServer webServer;
  private final DataSource dataSource;

  /**
   * Initializes the H2 Web Console Service. Starts the H2 Web Console as a daemon and associates it
   * with the given {@link DataSource}.
   *
   * @param dataSource the {@link DataSource} to be used for obtaining database connections
   */
  public H2WebConsoleService(DataSource dataSource) {
    this.webServer = startWebConsoleDaemon();
    this.dataSource = dataSource;
  }

  /**
   * Returns the running {@link WebServer} instance.
   *
   * @return the {@link WebServer} instance managing the H2 Web Console
   */
  public WebServer getWebServer() {
    return webServer;
  }

  /**
   * Starts the H2 Web Console server as a daemon process. The server will listen on a dynamically
   * assigned port.
   *
   * @return the {@link WebServer} instance
   * @throws RuntimeException if a {@link SQLException} occurs while starting the server
   */
  private static WebServer startWebConsoleDaemon() {
    try {
      Server server = Server.createWebServer(new String[] {"-webPort", "0", "-webDaemon"});
      server.start();
      return (WebServer) server.getService();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
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
      return webServer.addSession(conn);
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

  /** Stops the H2 Web Console server. */
  public void stop() {
    webServer.stop();
  }
}
