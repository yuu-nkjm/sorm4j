package org.nkjmlab.sorm4j.extension.h2.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.jupiter.api.Test;

class BackupSqlTest {

  @Test
  void testBackupSqlBuilder() {
    File testFile = new File("/path/to/db");
    BackupSql backupSql = BackupSql.builder(testFile).build();

    assertNotNull(backupSql);
    assertEquals("backup to '" + testFile.getAbsolutePath() + "'", backupSql.getSql());
  }

  @Test
  void testGetSql() {
    String expectedSql = "backup to '/path/to/db'";
    BackupSql backupSql = new BackupSql(expectedSql);

    assertEquals(expectedSql, backupSql.getSql());
  }
}
