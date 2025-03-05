package org.nkjmlab.sorm4j.util.h2.tools.server;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.server.web.WebServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.common.Experimental;
import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.logging.LogContextImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

@Experimental
public class H2WebBrowsingService {
  private static final SormLogger log = LogContextImpl.getDefaultLoggerSupplier().get();
  private final WebServer webServer;

  /**
   * @param dataSource
   * @param keepAlive is false, the Web console server shutdown after the main thread finishes.
   */
  public H2WebBrowsingService(boolean keepAlive) {
    this.webServer = startTemporalWebConsoleServer(keepAlive);
  }

  public WebServer getWebServer() {
    return webServer;
  }

  private static WebServer startTemporalWebConsoleServer(boolean keepAlive) {
    try {
      Server server =
          Server.createWebServer(
              keepAlive
                  ? new String[] {"-webPort", "0"}
                  : new String[] {"-webPort", "0", "-webDaemon"});
      server.start();
      log.info("H2 temporal Web console server started at {}", server.getURL());
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
   * @see #startTemporalWebConsoleServer(boolean)
   * @return
   */
  public void open(Connection conn) {
    try {
      webServer.addSession(conn);
      String url = webServer.addSession(conn);
      Server.openBrowser(url);
      log.info("Web console will be open by web browser = {}", url);
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }
  }
}
