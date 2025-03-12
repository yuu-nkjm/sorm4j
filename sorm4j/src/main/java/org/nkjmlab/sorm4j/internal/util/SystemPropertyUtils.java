package org.nkjmlab.sorm4j.internal.util;

import java.io.File;

public class SystemPropertyUtils {
  private SystemPropertyUtils() {}

  /**
   * Getting the user's home directory which is referenced by {@code
   * System.getProperty("user.home")}.
   *
   * @return
   */
  static File getUserHomeDirectory() {
    return new File(System.getProperty("user.home"));
  }

  public static File getTempDir() {
    return new File(System.getProperty("java.io.tmpdir"));
  }

  public static File convertTildeInFilePath(File filePath) {
    return new File(
        (filePath.getName().equals("~")
                || filePath.getPath().startsWith("~/")
                || filePath.getPath().startsWith("~\\"))
            ? filePath.getPath().replace("~", getUserHomeDirectory().getAbsolutePath())
            : filePath.getAbsolutePath());
  }

  public static File convertVariableInFilePath(File filePath) {
    String path = filePath.toString();
    if (path.startsWith("%TEMP%")) {
      return new File(getTempDir(), path.replace("%TEMP%", ""));
    } else if (path.startsWith("$TMPDIR")) {
      return new File(getTempDir(), path.replace("$TMPDIR", ""));
    } else if (path.startsWith("%USERPROFILE%")) {
      return new File(getUserHomeDirectory(), path.replace("%USERPROFILE%", ""));
    } else if (path.contains("~")) {
      return SystemPropertyUtils.convertTildeInFilePath(filePath);
    } else {
      return filePath;
    }
  }
}
