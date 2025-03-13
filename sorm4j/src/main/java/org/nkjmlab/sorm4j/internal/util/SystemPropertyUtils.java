package org.nkjmlab.sorm4j.internal.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SystemPropertyUtils {
  private SystemPropertyUtils() {}

  static String[] getClassPathElements() {
    return System.getProperty("java.class.path").split(File.pathSeparator);
  }

  public static String findClassPathElement(String regex) {
    List<String> elements = findClassPathElements(regex);
    if (elements.size() == 1) {
      return elements.get(0);
    } else {
      Object[] params = {regex, elements, getClassPathElements()};
      throw new IllegalArgumentException(
          ParameterizedStringFormatter.LENGTH_256.format(
              "{} should be one in classpath. found {}, in {}", params));
    }
  }

  static List<String> findClassPathElements(String regex) {
    String[] classPathElements = getClassPathElements();
    List<String> elements =
        Arrays.stream(classPathElements)
            .filter(elem -> new File(elem).getName().matches(regex))
            .collect(Collectors.toList());
    return elements;
  }

  public static String findJavaCommand() {
    String javaHome = System.getProperty("java.home");
    return new File(new File(javaHome, "bin"), "java").getAbsolutePath();
  }

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
