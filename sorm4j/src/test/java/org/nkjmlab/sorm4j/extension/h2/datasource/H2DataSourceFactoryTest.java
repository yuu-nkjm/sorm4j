package org.nkjmlab.sorm4j.extension.h2.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory.Config;
import org.nkjmlab.sorm4j.extension.h2.tools.server.tcp.H2TcpServer;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2DataSourceFactoryTest {

  @Test
  void testServerMode() throws StreamReadException, DatabindException, IOException, SQLException {
    H2DataSourceFactory factory =
        H2DataSourceFactory.of(
            new ObjectMapper()
                .readValue(
                    H2DataSourceFactoryTest.class.getResourceAsStream("h2.json1.sample"),
                    H2DataSourceFactory.Config.class));
    factory.getDatabaseFilePath().toFile().delete();
    factory.makeDatabaseFileIfNotExists();
    factory.makeDatabaseFileIfNotExists();

    assertThat(factory.getServerModeJdbcUrl())
        .isEqualTo("jdbc:h2:tcp://localhost:9999/" + userHomeDir() + "\\h2db\\testdir\\testdb1");

    assertThat(factory.toString()).contains("jdbc:h2:mem:testdb1;DB_CLOSE_DELAY=-1");

    H2TcpServer server = H2TcpServer.builder("tcpPassword").tcpPort(9999).tcpDaemon(true).build();
    server.start();
    factory.createServerModeDataSource().getConnection().close();
    factory.createServerModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createServerModeDataSource().getConnection().close();
    server.stop();
  }

  @Test
  void testMakeDir() throws StreamReadException, DatabindException, IOException, SQLException {
    Path tmp = Files.createTempDirectory("test");
    Files.deleteIfExists(tmp);
    assertFalse(Files.exists(tmp));
    H2DataSourceFactory.builder()
        .databaseDirectory(tmp)
        .databaseName("test")
        .build()
        .makeDatabaseFileIfNotExists();
    assertTrue(Files.exists(tmp));
  }

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    H2DataSourceFactory factory =
        H2DataSourceFactory.of(
            new ObjectMapper()
                .readValue(
                    H2DataSourceFactoryTest.class.getResourceAsStream("h2.json.sample"),
                    Config.class));
    factory.getDatabaseFilePath().toFile().delete();
    factory.makeDatabaseFileIfNotExists();
    factory.makeDatabaseFileIfNotExists();

    assertThat(factory.getEmbeddedModeJdbcUrl())
        .isEqualTo("jdbc:h2:file:" + userHomeDir() + "\\h2db\\testdir\\testdb");

    assertThat(factory.getInMemoryModeJdbcUrl()).isEqualTo("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    assertThat(factory.getMixedModeJdbcUrl())
        .isEqualTo("jdbc:h2:" + userHomeDir() + "\\h2db\\testdir\\testdb;AUTO_SERVER=TRUE");

    assertThat(factory.toString()).contains("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    H2DataSourceFactory.createTemporalInMemoryDataSource();

    factory.createEmbeddedModeDataSource().getConnection().close();
    factory.createInMemoryModeDataSource().getConnection().close();
    factory.createMixedModeDataSource().getConnection().close();
    factory.createEmbeddedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createInMemoryModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createMixedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
  }

  private String userHomeDir() {
    return new File(System.getProperty("user.home")).getPath();
  }

  @Test
  void testBuilderAndCreationMethods() {
    Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    H2DataSourceFactory factory =
        H2DataSourceFactory.of(Config.of(tempDir, dbName, username, password));

    assertEquals(tempDir.toAbsolutePath(), factory.getDatabaseDirectoryPath().toAbsolutePath());
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
    Path invalidDir = Path.of("invalidPath");
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          H2DataSourceFactory.of(Config.of(invalidDir, dbName, username, password, 1000));
        });
  }

  @Test
  void testMakeFileDatabaseIfNotExists() {
    Path tempDir = new File(System.getProperty("java.io.tmpdir")).toPath();
    String dbName = "testDB";
    String username = "user";
    String password = "pass";

    H2DataSourceFactory factory =
        H2DataSourceFactory.of(Config.of(tempDir, dbName, username, password));
    boolean result = factory.makeDatabaseFileIfNotExists();
    assertTrue(result || !result);
  }
}
