package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class SystemPropertyUtilsTest {

  @Test
  public void testConvertTilde() {
    Path input = Paths.get("~\\subdir");
    Path expected = SystemPropertyUtils.getUserHomeDirectory().resolve("subdir");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertTilde1() {
    Path input = Paths.get("~/subdir");
    Path expected = SystemPropertyUtils.getUserHomeDirectory().resolve("subdir");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertTilde2() {
    Path input = Paths.get("~/");
    Path expected = SystemPropertyUtils.getUserHomeDirectory();
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertVariableInFilePathWithTemp() {
    Path input = Paths.get("%TEMP%/subdir");
    Path expected = SystemPropertyUtils.getTempDir().resolve("subdir");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertVariableInFilePathWithTmpDir() {
    Path input = Paths.get("$TMPDIR/subdir");
    Path expected = SystemPropertyUtils.getTempDir().resolve("subdir");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertVariableInFilePathWithUserProfile() {
    Path input = Paths.get("%USERPROFILE%/subdir");
    Path expected = SystemPropertyUtils.getUserHomeDirectory().resolve("subdir");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }

  @Test
  public void testConvertVariableInFilePathWithRegularPath() {
    Path input = Paths.get("/regular/path");
    Path expected = Paths.get("/regular/path");
    Path actual = SystemPropertyUtils.convertVariableInPath(input);
    assertThat(actual.toAbsolutePath().toString()).isEqualTo(expected.toAbsolutePath().toString());
  }
}
