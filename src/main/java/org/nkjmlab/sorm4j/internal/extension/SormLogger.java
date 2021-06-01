package org.nkjmlab.sorm4j.internal.extension;

import org.nkjmlab.sorm4j.annotation.Experimental;

@Experimental
public interface SormLogger {

  void trace(String format, Object... params);

  void debug(String format, Object... params);

  void info(String format, Object... params);

  void warn(String format, Object... params);

  void error(String format, Object... params);

}
