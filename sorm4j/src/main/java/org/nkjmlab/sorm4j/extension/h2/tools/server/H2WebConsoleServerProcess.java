package org.nkjmlab.sorm4j.extension.h2.tools.server;

import java.util.concurrent.TimeUnit;

import org.nkjmlab.sorm4j.common.annotation.Experimental;

@Experimental
public class H2WebConsoleServerProcess {

  private static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;
  private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

  private final H2WebConsoleServerProperties properties;

  public H2WebConsoleServerProcess(H2WebConsoleServerProperties properties) {
    this.properties = properties;
  }

  public boolean awaitStart() {
    return awaitStart(DEFAULT_TIMEOUT, DEFAULT_TIMEUNIT);
  }

  /**
   * Starts H2 server process and wait for start server.
   *
   * @param props
   */
  public boolean awaitStart(long timeout, TimeUnit unit) {
    return H2ServerProcess.awaitStartServer(properties, timeout, unit);
  }
}
