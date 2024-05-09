package org.nkjmlab.sorm4j.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;

class SystemPropertyUtilsTest {

  @Test
  public void testConvertTilde() {
    File input = new File("~\\subdir");
    File expected = new File(SystemPropertyUtils.getUserHomeDirectory(), "/subdir");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertTilde1() {
    File input = new File("~/subdir");
    File expected = new File(SystemPropertyUtils.getUserHomeDirectory(), "/subdir");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertTilde2() {
    File input = new File("~/");
    File expected = SystemPropertyUtils.getUserHomeDirectory();
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertVariableInFilePathWithTemp() {
    File input = new File("%TEMP%/subdir");
    File expected = new File(SystemPropertyUtils.getTempDir(), "/subdir");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertVariableInFilePathWithTmpDir() {
    File input = new File("$TMPDIR/subdir");
    File expected = new File(SystemPropertyUtils.getTempDir(), "/subdir");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertVariableInFilePathWithUserProfile() {
    File input = new File("%USERPROFILE%/subdir");
    File expected = new File(SystemPropertyUtils.getUserHomeDirectory(), "/subdir");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }

  @Test
  public void testConvertVariableInFilePathWithRegularPath() {
    File input = new File("/regular/path");
    File expected = new File("/regular/path");
    File actual = SystemPropertyUtils.convertVariableInFilePath(input);
    assertThat(actual.getAbsolutePath()).isEqualTo(expected.getAbsolutePath());
  }
}
