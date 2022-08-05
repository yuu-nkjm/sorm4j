package org.nkjmlab.sorm4j.util.h2.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public class H2WebConsoleServerProperties extends H2ServerProperties {

  public H2WebConsoleServerProperties(String serverType, int port, String password, String[] args) {
    super(serverType, port, password, args);
  }

  public static class Builder extends H2ServerPropertiesBuilder<Builder> {
    private static final int DEFAULT_WEBCONSOLE_PORT = 8082;

    public Builder() {
      super("WEB_CONSOLE", DEFAULT_WEBCONSOLE_PORT, "");
    }

    public Builder(String password) {
      super("WEB_CONSOLE", DEFAULT_WEBCONSOLE_PORT, password);
    }

    @Override
    public H2WebConsoleServerProperties build() {
      List<String> args =
          new ArrayList<>(List.of(javaCommand, "-cp", getH2ClassPath(), "org.h2.tools.Server"));
      args.addAll(List.of("-web", "-webPort", port + ""));
      args.addAll(Arrays.asList(options));
      args.add("-webAdminPassword");
      args.add(password);
      return new H2WebConsoleServerProperties(serverType, port, password,
          args.toArray(String[]::new));
    }

    @Override
    public Builder getThisBuilder() {
      return this;
    }
  }

  public static H2WebConsoleServerProperties.Builder builder() {
    return new Builder();
  }
}
