package org.nkjmlab.sorm4j.extension.h2.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.h2.grammar.ScriptCompressionEncryption;

class RunScriptSqlTest {

  @Test
  public void testBuilderWithFileAndCharset() {
    File file = new File("script.sql");
    RunScriptSql scriptSql = RunScriptSql.builder().from(file).charset("UTF-8").build();
    assertEquals(
        "RUNSCRIPT FROM '" + file.getAbsolutePath() + "' charset UTF-8", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithFileOnly() {
    File file = new File("script.sql");
    RunScriptSql scriptSql = RunScriptSql.builder().from(file).build();
    assertEquals("RUNSCRIPT FROM '" + file.getAbsolutePath() + "'", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithFileAndCompressionEncryption() {
    File file = new File("script.sql");
    ScriptCompressionEncryption compressionEncryption =
        new ScriptCompressionEncryption.Builder()
            .compression("DEFLATE")
            .cipher("AES")
            .password("password")
            .build();

    RunScriptSql scriptSql =
        RunScriptSql.builder()
            .from(file)
            .scriptCompressionEncryption(compressionEncryption)
            .build();
    assertEquals(
        "RUNSCRIPT FROM '"
            + file.getAbsolutePath()
            + "' compression DEFLATE cipher AES password 'password'",
        scriptSql.getSql());
  }

  @Test
  public void testBuilderWithAllOptions() {
    File file = new File("script.sql");
    ScriptCompressionEncryption compressionEncryption =
        new ScriptCompressionEncryption.Builder()
            .compression("ZIP")
            .cipher("DES")
            .password("secure")
            .build();

    RunScriptSql scriptSql =
        RunScriptSql.builder()
            .from(file)
            .charset("UTF-8")
            .scriptCompressionEncryption(compressionEncryption)
            .build();
    assertEquals(
        "RUNSCRIPT FROM '"
            + file.getAbsolutePath()
            + "' compression ZIP cipher DES password 'secure' charset UTF-8",
        scriptSql.getSql());
  }
}
