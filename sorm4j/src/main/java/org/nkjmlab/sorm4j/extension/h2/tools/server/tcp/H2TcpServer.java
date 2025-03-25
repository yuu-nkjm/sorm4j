package org.nkjmlab.sorm4j.extension.h2.tools.server.tcp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.server.TcpServer;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.extension.h2.tools.server.H2Server;
import org.nkjmlab.sorm4j.util.function.exception.Try;

/**
 * A class that manages an H2 TCP server instance. This class provides methods to start, stop, and
 * check the status of the server.
 */
public class H2TcpServer implements H2Server {

  private final H2TcpServerProperties properties;
  private final Server server;

  /**
   * Creates an instance of {@code H2TcpServer} with the specified properties.
   *
   * @param properties the properties for configuring the H2 TCP server
   * @throws SQLException if an error occurs while creating the server instance
   */
  private H2TcpServer(H2TcpServerProperties properties) {
    this.properties = properties;
    try {
      this.server = Server.createTcpServer(properties.toArgs());
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * Stops the H2 TCP server process if it is currently running. If the server is not running, this
   * method does nothing.
   *
   * @throws SQLException if an error occurs while stopping the server
   */
  @Override
  public void stop() throws SQLException {
    if (!isRunning()) {
      return;
    }
    Server.shutdownTcpServer(getUrl(), properties.tcpPassword(), false, false);
  }

  public TcpServer getTcpServer() {
    return (TcpServer) getService();
  }

  @Override
  public Server getServer() {
    return server;
  }

  public static Builder builder(String tcpPassword) {
    return new Builder(tcpPassword);
  }

  /** A builder for constructing instances of {@code H2TcpServer}. */
  public static class Builder {

    private Integer tcpPort;
    private String tcpPassword;
    private boolean tcpSSL;
    private boolean tcpAllowOthers;
    private boolean tcpDaemon;
    private boolean trace;
    private boolean ifExists = true;
    private boolean ifNotExists = false;
    private String baseDir;
    private String keyFrom;
    private String keyTo;

    public Builder(String tcpPassword) {
      tcpPassword(tcpPassword);
    }

    /**
     * Sets the TCP port number.
     *
     * @param port the TCP port number
     * @return this builder instance
     */
    public Builder tcpPort(Integer port) {
      this.tcpPort = port;
      return this;
    }

    /**
     * Sets the TCP password.
     *
     * @param password the TCP password
     * @return this builder instance
     */
    public Builder tcpPassword(String password) {
      this.tcpPassword = password;
      return this;
    }

    /**
     * Enables or disables SSL for the TCP connection.
     *
     * @param tcpSSL {@code true} to enable SSL, {@code false} to disable it
     * @return this builder instance
     */
    public Builder tcpSSL(boolean tcpSSL) {
      this.tcpSSL = tcpSSL;
      return this;
    }

    /**
     * Allows or disallows remote connections to the server.
     *
     * @param tcpAllowOthers {@code true} to allow remote connections, {@code false} to restrict
     *     access
     * @return this builder instance
     */
    public Builder tcpAllowOthers(boolean tcpAllowOthers) {
      this.tcpAllowOthers = tcpAllowOthers;
      return this;
    }

    /**
     * Enables or disables the daemon mode for the TCP server.
     *
     * @param tcpDaemon {@code true} to run the server as a daemon, {@code false} otherwise
     * @return this builder instance
     */
    public Builder tcpDaemon(boolean tcpDaemon) {
      this.tcpDaemon = tcpDaemon;
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

    public Builder key(String keyFrom, String keyTo) {
      this.keyFrom = keyFrom;
      this.keyTo = keyTo;
      return this;
    }

    /**
     * Builds a new instance of {@code H2TcpServer} with the configured properties.
     *
     * @return a new {@code H2TcpServer} instance
     */
    public H2TcpServer build() {
      return new H2TcpServer(
          new H2TcpServerProperties(
              tcpPort,
              tcpPassword,
              tcpSSL,
              tcpAllowOthers,
              tcpDaemon,
              trace,
              ifExists,
              ifNotExists,
              baseDir,
              keyFrom,
              keyTo));
    }
  }

  /** A record that holds configuration properties for an H2 TCP server. */
  public static record H2TcpServerProperties(
      Integer tcpPort,
      String tcpPassword,
      boolean tcpSSL,
      boolean tcpAllowOthers,
      boolean tcpDaemon,
      boolean trace,
      boolean ifExists,
      boolean ifNotExists,
      String baseDir,
      String keyFrom,
      String keyTo) {

    /**
     * Converts the properties into an array of command-line arguments for the H2 server.
     *
     * @return an array of arguments for the H2 TCP server
     */
    public String[] toArgs() {
      List<String> args = new ArrayList<>();
      if (tcpPort != null) {
        args.add("-tcpPort");
        args.add(String.valueOf(tcpPort));
      }
      if (tcpPassword != null) {
        args.add("-tcpPassword");
        args.add(tcpPassword);
      }
      if (tcpSSL) {
        args.add("-tcpSSL");
      }
      if (tcpAllowOthers) {
        args.add("-tcpAllowOthers");
      }
      if (tcpDaemon) {
        args.add("-tcpDaemon");
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
      if (keyFrom != null) {
        args.add("-key");
        args.add(keyFrom);
        args.add(keyTo);
      }

      return args.toArray(String[]::new);
    }
  }
}
