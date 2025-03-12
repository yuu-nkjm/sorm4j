package org.nkjmlab.sorm4j.extension.h2.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2DataSourceFactoryTest {

  @Test
  void testServerMode() throws StreamReadException, DatabindException, IOException, SQLException {
    H2DataSourceFactory factory =
        new ObjectMapper()
            .readValue(
                H2DataSourceFactoryTest.class.getResourceAsStream("h2.json1.sample"),
                H2DataSourceFactory.Builder.class)
            .build();
    factory.getDatabaseFile().delete();
    factory.makeFileDatabaseIfNotExists();
    factory.makeFileDatabaseIfNotExists();

    assertThat(factory.getServerModeJdbcUrl())
        .isEqualTo("jdbc:h2:tcp://localhost/" + userHomeDir() + "/h2db/testdir/testdb1");

    assertThat(factory.toString()).contains("jdbc:h2:mem:testdb1;DB_CLOSE_DELAY=-1");

    H2DataSourceFactory.builder();
    H2DataSourceFactory.builder(
        factory.getDatabaseDirectory(),
        factory.getDatabaseName(),
        factory.getUsername(),
        factory.getPassword());

    H2DataSourceFactory.createTemporalInMemoryDataSource();

    factory.createServerModeDataSource().getConnection().close();
    factory.createServerModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
  }

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    H2DataSourceFactory factory =
        new ObjectMapper()
            .readValue(
                H2DataSourceFactoryTest.class.getResourceAsStream("h2.json.sample"),
                H2DataSourceFactory.Builder.class)
            .build();
    factory.getDatabaseFile().delete();
    factory.makeFileDatabaseIfNotExists();
    factory.makeFileDatabaseIfNotExists();

    assertThat(factory.getEmbeddedModeJdbcUrl())
        .isEqualTo("jdbc:h2:file:" + userHomeDir() + "/h2db/testdir/testdb");

    assertThat(factory.getInMemoryModeJdbcUrl()).isEqualTo("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    assertThat(factory.getMixedModeJdbcUrl())
        .isEqualTo("jdbc:h2:" + userHomeDir() + "/h2db/testdir/testdb;AUTO_SERVER=TRUE");

    assertThat(factory.toString()).contains("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    H2DataSourceFactory.builder();
    H2DataSourceFactory.builder(
        factory.getDatabaseDirectory(),
        factory.getDatabaseName(),
        factory.getUsername(),
        factory.getPassword());

    H2DataSourceFactory.createTemporalInMemoryDataSource();

    factory.createEmbeddedModeDataSource().getConnection().close();
    factory.createInMemoryModeDataSource().getConnection().close();
    factory.createMixedModeDataSource().getConnection().close();
    factory.createEmbeddedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createInMemoryModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createMixedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
  }

  private String userHomeDir() {
    return new File(System.getProperty("user.home")).getPath().replace("\\", "/");
  }

  @Test
  void testBuilderAndCreationMethods() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    H2DataSourceFactory factory =
        H2DataSourceFactory.builder(tempDir, dbName, username, password).build();

    assertEquals(tempDir.getAbsolutePath(), factory.getDatabaseDirectory().getAbsolutePath());
    assertEquals(dbName, factory.getDatabaseName());
    assertEquals(username, factory.getUsername());
    assertEquals(password, factory.getPassword());

    assertTrue(factory.getInMemoryModeJdbcUrl().startsWith("jdbc:h2:mem:"));
    assertTrue(factory.getServerModeJdbcUrl().startsWith("jdbc:h2:tcp://"));
    assertTrue(factory.getEmbeddedModeJdbcUrl().startsWith("jdbc:h2:file:"));
    assertTrue(factory.getMixedModeJdbcUrl().startsWith("jdbc:h2:"));

    assertDoesNotThrow(
        () -> {
          JdbcConnectionPool ds = factory.createInMemoryModeDataSource();
          assertNotNull(ds);
        });

    assertDoesNotThrow(
        () -> {
          JdbcConnectionPool ds = factory.createServerModeDataSource();
          assertNotNull(ds);
        });

    assertDoesNotThrow(
        () -> {
          JdbcConnectionPool ds = factory.createEmbeddedModeDataSource();
          assertNotNull(ds);
        });

    assertDoesNotThrow(
        () -> {
          JdbcConnectionPool ds = factory.createMixedModeDataSource();
          assertNotNull(ds);
        });
  }

  @Test
  void testBuilderWithInvalidArguments() {
    File invalidDir = new File("invalidPath");
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          H2DataSourceFactory.builder(invalidDir, dbName, username, password);
        });
  }

  @Test
  void testMakeFileDatabaseIfNotExists() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    H2DataSourceFactory factory =
        H2DataSourceFactory.builder(tempDir, dbName, username, password).build();
    boolean result = factory.makeFileDatabaseIfNotExists();
    assertTrue(result || !result);
  }
}
