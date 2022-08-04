package org.nkjmlab.sorm4j.util.h2.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nkjmlab.sorm4j.internal.util.SystemPropertyUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

public abstract class H2ServerPropertiesBuilder {
  String javaCommand = Try.getOrElse(() -> SystemPropertyUtils.findJavaCommand(), "");
  String h2ClassPath =
      Try.getOrElse(() -> SystemPropertyUtils.findClassPathElement("^h2-[0-9\\.]*.jar$"), "");
  String serverType;
  int port;
  String password;
  String[] options = new String[0];

  public H2ServerPropertiesBuilder() {};

  public H2ServerPropertiesBuilder(String serverType, int port, String password) {
    this.serverType = serverType;
    this.port = port;
    this.password = password;
  }

  public H2ServerPropertiesBuilder setJavaCommand(String javaCommand) {
    this.javaCommand = javaCommand;
    return this;
  }

  public H2ServerPropertiesBuilder setH2ClassPath(String h2ClassPath) {
    this.h2ClassPath = h2ClassPath;
    return this;
  }


  public H2ServerPropertiesBuilder setServerType(String serverType) {
    this.serverType = serverType;
    return this;
  }

  public H2ServerPropertiesBuilder setPort(int port) {
    this.port = port;
    return this;
  }

  public H2ServerPropertiesBuilder setPassword(String password) {
    this.password = password;
    return this;
  }

  public H2ServerPropertiesBuilder setOptions(String... options) {
    this.options = options;
    return this;
  }

  public String getH2ClassPath() {
    return SystemPropertyUtils.getTildeExpandAbsolutePath(new File(h2ClassPath));
  }

  public H2ServerProperties build() {
    throw new IllegalStateException("not implemented yet");
  };

  public static class H2TcpServerPropertiesBuilder extends H2ServerPropertiesBuilder {
    private static final int DEFAULT_TCP_PORT = 9092;

    public H2TcpServerPropertiesBuilder() {
      super("TCP", DEFAULT_TCP_PORT, "");
    }

    public H2TcpServerPropertiesBuilder(String password) {
      super("TCP", DEFAULT_TCP_PORT, password);
    }


    @Override
    public H2ServerProperties build() {
      List<String> args =
          new ArrayList<>(List.of(javaCommand, "-cp", getH2ClassPath(), "org.h2.tools.Server"));
      args.addAll(List.of("-tcp", "-tcpPort", port + ""));
      args.addAll(Arrays.asList(options));
      args.add("-tcpPassword");
      args.add(password);
      return new H2ServerProperties(serverType, port, password, args.toArray(String[]::new));
    }
  }

  public static class H2WebConsoleServerPropertiesBuilder extends H2ServerPropertiesBuilder {
    private static final int DEFAULT_WEBCONSOLE_PORT = 8082;

    public H2WebConsoleServerPropertiesBuilder() {
      super("WEB_CONSOLE", DEFAULT_WEBCONSOLE_PORT, "");
    }

    public H2WebConsoleServerPropertiesBuilder(String password) {
      super("WEB_CONSOLE", DEFAULT_WEBCONSOLE_PORT, password);
    }

    @Override
    public H2ServerProperties build() {
      List<String> args =
          new ArrayList<>(List.of(javaCommand, "-cp", getH2ClassPath(), "org.h2.tools.Server"));
      args.addAll(List.of("-web", "-webPort", port + ""));
      args.addAll(Arrays.asList(options));
      args.add("-webAdminPassword");
      args.add(password);
      return new H2ServerProperties(serverType, port, password, args.toArray(String[]::new));
    }
  }

}
