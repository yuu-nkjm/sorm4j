package org.nkjmlab.sorm4j.util.h2.server;

import org.nkjmlab.sorm4j.annotation.Experimental;

/**
 * This class represents H2 server properties.
 *
 * @author nkjm
 */
@Experimental
public class H2ServerProperties {

  public final String serverType;
  public final int port;
  public final String password;
  public final String[] args;

  public H2ServerProperties(String serverType, int port, String password, String[] args) {
    this.serverType = serverType;
    this.port = port;
    this.password = password;
    this.args = args;
  }
}
