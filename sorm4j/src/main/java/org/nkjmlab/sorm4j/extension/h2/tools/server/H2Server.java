package org.nkjmlab.sorm4j.extension.h2.tools.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

import org.h2.server.Service;
import org.h2.tools.Server;
import org.nkjmlab.sorm4j.internal.util.Try;

/**
 * Interface for managing an H2 database server instance. This interface provides methods to start,
 * stop, and check the status of an H2 TCP server, along with utility functions to determine port
 * availability.
 */
public interface H2Server {

  /**
   * Gets the underlying H2 server instance.
   *
   * @return the {@link Server} instance representing the H2 server
   */
  Server getServer();

  /**
   * Stops the H2 server process if it is currently running. If the server is not running, this
   * method does nothing.
   *
   * @throws SQLException if an error occurs while stopping the server
   */
  void stop() throws SQLException;

  /**
   * Checks whether the TCP port specified in the properties is free.
   *
   * @return {@code true} if the port is free (not in use), otherwise {@code false}
   */
  default boolean isPortFree() {
    try (ServerSocket socket = new ServerSocket(getPort())) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Checks whether the H2 TCP server is currently running.
   *
   * @return {@code true} if the server is running, otherwise {@code false}
   */
  default boolean isRunning() {
    return getServer().isRunning(false);
  }

  /**
   * Gets the H2 {@link Service} instance associated with this server.
   *
   * @return the service instance managing the server
   */
  default Service getService() {
    return getServer().getService();
  }

  /**
   * Retrieves the current status of the H2 server.
   *
   * @return a status string describing the server state
   */
  default String getStatus() {
    return getServer().getStatus();
  }

  /**
   * Gets the TCP port on which the H2 server is running.
   *
   * @return the port number used by the server
   */
  default int getPort() {
    return getServer().getPort();
  }

  /**
   * Retrieves the connection URL for the H2 server.
   *
   * @return the database connection URL
   */
  default String getUrl() {
    return getServer().getURL();
  }

  /**
   * Starts the H2 server process if it is not already running. If the server is already running,
   * this method does nothing.
   *
   * @throws SQLException if an error occurs while starting the server
   */
  default void start() {
    if (isRunning()) {
      return;
    }
    try {
      getServer().start();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }
}
