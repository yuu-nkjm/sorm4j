package org.nkjmlab.sorm4j.util.h2.server;

import java.io.File;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.internal.util.SystemPropertyUtils;
import org.nkjmlab.sorm4j.internal.util.Try;

@Experimental
public abstract class H2ServerPropertiesBuilder<T extends H2ServerPropertiesBuilder<T>> {
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

  public H2ServerPropertiesBuilder<T> setJavaCommand(String javaCommand) {
    this.javaCommand = javaCommand;
    return getThisBuilder();
  }

  public H2ServerPropertiesBuilder<T> setH2ClassPath(String h2ClassPath) {
    this.h2ClassPath = h2ClassPath;
    return getThisBuilder();
  }


  public H2ServerPropertiesBuilder<T> setServerType(String serverType) {
    this.serverType = serverType;
    return getThisBuilder();
  }

  public H2ServerPropertiesBuilder<T> setPort(int port) {
    this.port = port;
    return getThisBuilder();
  }

  public T setPassword(String password) {
    this.password = password;
    return getThisBuilder();
  }

  public abstract T getThisBuilder();

  public H2ServerPropertiesBuilder<T> setOptions(String... options) {
    this.options = options;
    return this;
  }

  public String getH2ClassPath() {
    return SystemPropertyUtils.getTildeExpandAbsolutePath(new File(h2ClassPath));
  }

  public abstract H2ServerProperties build();



}
