package org.nkjmlab.sorm4j.internal.extension;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface SormLogger {

  void trace(String format, Object... arguments);

  void debug(String format, Object... arguments);

  void info(String format, Object... arguments);

  void warn(String format, Object... arguments);

  void error(String format, Object... arguments);

}
