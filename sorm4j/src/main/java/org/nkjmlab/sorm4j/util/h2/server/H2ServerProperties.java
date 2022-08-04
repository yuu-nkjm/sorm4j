package org.nkjmlab.sorm4j.util.h2.server;

import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.util.h2.server.H2ServerPropertiesBuilder.H2TcpServerPropertiesBuilder;
import org.nkjmlab.sorm4j.util.h2.server.H2ServerPropertiesBuilder.H2WebConsoleServerPropertiesBuilder;

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

  public static H2TcpServerPropertiesBuilder createTcpServerPropertiesBuilder() {
    return new H2TcpServerPropertiesBuilder();
  }

  public static H2WebConsoleServerPropertiesBuilder createWebConsoleServerPropertiesBuilder() {
    return new H2WebConsoleServerPropertiesBuilder();
  }


}
