package org.nkjmlab.sorm4j.util.h2.server;

import java.sql.SQLException;

public class H2Startup {
  private H2Startup() {}

  public static boolean startServer(String... args) {
    try {
      org.h2.tools.Server.main(args);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public static boolean startConsole(String... args) {
    try {
      org.h2.tools.Console.main(args);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  public static boolean startDefaultLocalTcpServer() {
    return startServer("-tcp", "-tcpPort", "9092", "-ifNotExists");
  }

  public static boolean startDefaultWebConsole() {
    return startConsole("-web", "-tool");
  }
}
