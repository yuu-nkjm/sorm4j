package org.nkjmlab.sorm4j.extension.h2.grammar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ScriptCompressionEncryptionTest {

  @Test
  public void testDefaultBuilder() {
    String password = "examplePassword";
    ScriptCompressionEncryption script =
        ScriptCompressionEncryption.defaultBuilder(password).build();
    assertEquals("compression DEFLATE cipher AES password 'examplePassword'", script.getSql());
  }

  @Test
  public void testBuilderWithCustomCompression() {
    ScriptCompressionEncryption script =
        ScriptCompressionEncryption.builder().compression("ZIP").password("pass").build();
    assertEquals("compression ZIP password 'pass'", script.getSql());
  }

  @Test
  public void testBuilderWithCustomCipher() {
    ScriptCompressionEncryption script =
        ScriptCompressionEncryption.builder().cipher("DES").password("pass").build();
    assertEquals("cipher DES password 'pass'", script.getSql());
  }

  @Test
  public void testBuilderWithCustomPassword() {
    ScriptCompressionEncryption script =
        ScriptCompressionEncryption.builder().password("myPassword").build();
    assertEquals("password 'myPassword'", script.getSql());
  }

  @Test
  public void testBuilderWithAllCustomProperties() {
    ScriptCompressionEncryption script =
        ScriptCompressionEncryption.builder()
            .compression("GZIP")
            .cipher("TripleDES")
            .password("securePass")
            .build();
    assertEquals("compression GZIP cipher TripleDES password 'securePass'", script.getSql());
  }

  @Test
  public void testBuilderWithNoProperties() {
    ScriptCompressionEncryption script = ScriptCompressionEncryption.builder().build();
    assertEquals("", script.getSql());
  }

  // Additional tests can be added to cover more combinations and edge cases.
}
