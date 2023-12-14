package org.nkjmlab.sorm4j.util.h2.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

class ScriptSqlTest {

  @Test
  public void testBuilderWithNoData() {
    ScriptSql scriptSql = ScriptSql.builder().noData().build();
    assertEquals("script nodata", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithSimple() {
    ScriptSql scriptSql = ScriptSql.builder().simple().build();
    assertEquals("script simple", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithColumns() {
    ScriptSql scriptSql = ScriptSql.builder().columns().build();
    assertEquals("script columns", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithNoPasswords() {
    ScriptSql scriptSql = ScriptSql.builder().noPasswords().build();
    assertEquals("script noPasswords", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithNoSettings() {
    ScriptSql scriptSql = ScriptSql.builder().noSettings().build();
    assertEquals("script noSettings", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithDrop() {
    ScriptSql scriptSql = ScriptSql.builder().drop(true).build();
    assertEquals("script drop", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithFalseDrop() {
    ScriptSql scriptSql = ScriptSql.builder().drop(false).build();
    assertEquals("script", scriptSql.getSql()); // Assuming 'drop' option is not added if false
  }

  @Test
  public void testBuilderWithFileName() {
    // Assuming File and ScriptCompressionEncryption classes are correctly imported and used
    File file = new File("test.sql");
    ScriptSql scriptSql = ScriptSql.builder().to(file).charset("UTF-8").build();
    assertEquals(
        "script to '" + file.getAbsolutePath() + "' charset 'UTF-8'",
        scriptSql.getSql()); // Assuming wrapSingleQuote works as intended
  }

  @Test
  public void testBuilderWithAddTable() {
    ScriptSql scriptSql = ScriptSql.builder().addTable("users").build();
    assertEquals("script table users", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithMultipleTables() {
    ScriptSql scriptSql = ScriptSql.builder().addTable("users").addTable("products").build();
    assertEquals("script table users,products", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithAddSchema() {
    ScriptSql scriptSql = ScriptSql.builder().addSchema("public").build();
    assertEquals("script schema public", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithMultipleSchemas() {
    ScriptSql scriptSql = ScriptSql.builder().addSchema("public").addSchema("private").build();
    assertEquals("script schema public,private", scriptSql.getSql());
  }

  @Test
  public void testBuilderWithComplexOptions() {
    ScriptSql scriptSql =
        ScriptSql.builder()
            .noData()
            .columns()
            .blockSize(1024)
            .addTable("table1")
            .addTable("table2")
            .build();
    assertEquals("script nodata columns blocksize 1024 table table1,table2", scriptSql.getSql());
  }
}
