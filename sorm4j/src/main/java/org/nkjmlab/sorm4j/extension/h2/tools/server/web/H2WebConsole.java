package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import java.sql.Connection;
import java.util.function.Consumer;

public class H2WebConsole {
  private H2WebConsole() {}

  public static void openBlocking(Connection connection) {
    openBlocking(connection, url -> {});
  }

  /**
   * Starts an H2 Web Console server for the given database connection and opens a browser for
   * interacting with the database.
   *
   * <p>The web server is started with a dynamically assigned port, and the database connection
   * remains active during the session. The current transaction is preserved, allowing for real-time
   * debugging and inspection of the database state.
   *
   * <p>The method blocks until the user disconnects from the web session.
   *
   * @param connection the database connection to be used for the Web Console (must be open)
   */
  public static void openBlocking(Connection connection, Consumer<String> urlHandler) {
    H2WebServer server = createAndStartServer();
    String url = server.openBrowser(connection);
    urlHandler.accept(url);
    try {
      while (server.isRunning() && !connection.isClosed()) {
        Thread.sleep(1000);
      }
    } catch (Exception e) {
    }
  }

  private static H2WebServer createAndStartServer() {
    H2WebServer server = H2WebServer.builder().webPort(0).build();
    server.start();
    return server;
  }

  /**
   * Starts an H2 Web Console server with a dynamically assigned port and opens a browser for
   * interacting with the given database connection.
   *
   * <p>The web server is launched in the background without blocking the calling thread. The
   * console allows real-time inspection and interaction with the database.
   *
   * <p>The database connection must be active while using the Web Console. The session remains open
   * until the server is explicitly stopped.
   *
   * @param connection the database connection to be used for the Web Console (must be open)
   * @return the URL of the Web Console session
   */
  public static String open(Connection connection) {
    return createAndStartServer().openBrowser(connection);
  }
}
