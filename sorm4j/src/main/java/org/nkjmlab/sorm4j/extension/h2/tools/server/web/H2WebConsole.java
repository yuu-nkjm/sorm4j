package org.nkjmlab.sorm4j.extension.h2.tools.server.web;

import java.sql.Connection;

public class H2WebConsole {
  private H2WebConsole() {}

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
  public static void openBlocking(Connection connection) {

    H2WebServer server = H2WebServer.builder().webPort(0).build();
    server.start();
    server.getServer().setShutdownHandler(() -> server.stop());
    server.openBrowser(connection);
    try {
      while (server.isRunning() && !connection.isClosed()) {
        Thread.sleep(1000);
      }
    } catch (Exception e) {
    }
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
   * @return
   */
  public static H2WebServer open(Connection connection) {
    H2WebServer server = H2WebServer.builder().webPort(0).build();
    server.start();
    server.openBrowser(connection);
    return server;
  }
}
