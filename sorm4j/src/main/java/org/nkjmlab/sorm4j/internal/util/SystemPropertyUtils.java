package org.nkjmlab.sorm4j.internal.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SystemPropertyUtils {
  private SystemPropertyUtils() {}
  ;

  public static String[] getClassPathElements() {
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

  public static List<String> findClassPathElements(String regex) {
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

  public static Map<String, String> getJavaProperties() {
    return Stream.of(
            "os.name",
            "os.version",
            "java.class.version",
            "java.specification.version",
            "java.vm.name",
            "java.vm.version",
            "java.vm.vendor",
            "java.home",
            "java.class.path",
            "user.name",
            "user.home",
            "user.dir")
        .collect(
            Collectors.toMap(
                p -> p,
                p ->
                    ParameterizedStringFormatter.LENGTH_256.format(
                        "{}={}", p, System.getProperty(p))));
  }

  /**
   * Getting the user's home directory which is referenced by {@code
   * System.getProperty("user.home")}.
   *
   * @return
   */
  public static File getUserHomeDirectory() {
    return new File(System.getProperty("user.home"));
  }

  public static String getTildeExpandAbsolutePath(File path) {
    return (path.getName().equals("~")
            || path.getPath().startsWith("~/")
            || path.getPath().startsWith("~\\"))
        ? path.getPath().replace("~", getUserHomeDirectory().getAbsolutePath())
        : path.getAbsolutePath();
  }
}
