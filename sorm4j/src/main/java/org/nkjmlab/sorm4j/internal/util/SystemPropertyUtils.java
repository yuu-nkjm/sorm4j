package org.nkjmlab.sorm4j.internal.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SystemPropertyUtils {
  private SystemPropertyUtils() {}

  /**
   * Gets the user's home directory from {@code System.getProperty("user.home")}.
   *
   * @return User home directory as a Path.
   */
  static Path getUserHomeDirectory() {
    return Paths.get(System.getProperty("user.home"));
  }

  /**
   * Gets the system temporary directory from {@code System.getProperty("java.io.tmpdir")}.
   *
   * @return Temporary directory as a Path.
   */
  public static Path getTempDir() {
    return Paths.get(System.getProperty("java.io.tmpdir"));
  }

  /**
   * Converts environment variables like %TEMP%, $TMPDIR, %USERPROFILE%, and ~ in the given path.
   *
   * @param path The input path
   * @return The resolved path
   */
  public static Path convertVariableInPath(Path path) {
    Map<String, Path> replaceStringMap =
        Map.of(
            "%TEMP%", getTempDir(),
            "$TMPDIR", getTempDir(),
            "%USERPROFILE%", getUserHomeDirectory(),
            "~", getUserHomeDirectory());

    String pathStr = path.toString();

    for (Map.Entry<String, Path> entry : replaceStringMap.entrySet()) {
      if (pathStr.startsWith(entry.getKey())) {
        return entry
            .getValue()
            .resolve(stripLeadingSeparator(pathStr.substring(entry.getKey().length())));
      }
    }

    return path;
  }

  private static String stripLeadingSeparator(String pathStr) {
    return pathStr.replaceFirst("^[\\\\/]+", "");
  }
}
