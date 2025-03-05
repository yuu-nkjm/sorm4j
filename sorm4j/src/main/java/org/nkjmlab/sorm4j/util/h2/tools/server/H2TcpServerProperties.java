package org.nkjmlab.sorm4j.util.h2.tools.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nkjmlab.sorm4j.common.Experimental;

/** @author nkjm */
@Experimental
public class H2TcpServerProperties extends H2ServerProperties {

  public H2TcpServerProperties(String serverType, int port, String password, String[] args) {
    super(serverType, port, password, args);
  }

  /**
   * Create a {@link H2TcpServerProperties} object.
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends H2ServerPropertiesBuilder<Builder> {
    private static final int DEFAULT_TCP_PORT = 9092;

    /** This constructor is for external libraries. */
    public Builder() {
      super("TCP", DEFAULT_TCP_PORT, "");
    }

    @Override
    public H2TcpServerProperties build() {
      List<String> args =
          new ArrayList<>(List.of(javaCommand, "-cp", getH2ClassPath(), "org.h2.tools.Server"));
      args.addAll(List.of("-tcp", "-tcpPort", port + ""));
      args.addAll(Arrays.asList(options));
      args.add("-tcpPassword");
      args.add(password);
      return new H2TcpServerProperties(serverType, port, password, args.toArray(String[]::new));
    }

    @Override
    public Builder getThisBuilder() {
      return this;
    }
  }
}
