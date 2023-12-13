package org.nkjmlab.sorm4j.util.h2.datasource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2LocalDataSourceFactoryTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException, SQLException {
    H2LocalDataSourceFactory factory =
        new ObjectMapper()
            .readValue(
                H2LocalDataSourceFactoryTest.class.getResourceAsStream("h2.json.sample"),
                H2LocalDataSourceFactory.Builder.class)
            .build();
    factory.getDatabaseFile().delete();
    factory.makeFileDatabaseIfNotExists();
    factory.makeFileDatabaseIfNotExists();

    assertThat(factory.getEmbeddedModeJdbcUrl())
        .isEqualTo("jdbc:h2:file:" + userHomeDir() + "/h2db/testdir/testdb");

    assertThat(factory.getInMemoryModeJdbcUrl()).isEqualTo("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    assertThat(factory.getServerModeJdbcUrl())
        .isEqualTo("jdbc:h2:tcp://localhost/" + userHomeDir() + "/h2db/testdir/testdb");

    assertThat(factory.getMixedModeJdbcUrl())
        .isEqualTo("jdbc:h2:" + userHomeDir() + "/h2db/testdir/testdb;AUTO_SERVER=TRUE");

    assertThat(factory.toString()).contains("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");

    H2LocalDataSourceFactory.builder();
    H2LocalDataSourceFactory.builder(
        factory.getDatabaseDirectory(),
        factory.getDatabaseName(),
        factory.getUsername(),
        factory.getPassword());

    H2LocalDataSourceFactory.createTemporalInMemoryDataSource();

    factory.createEmbeddedModeDataSource().getConnection();
    factory.createInMemoryModeDataSource().getConnection();
    factory.createMixedModeDataSource().getConnection();
    // factory.createServerModeDataSource().getConnection();
    factory.createEmbeddedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createInMemoryModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    factory.createMixedModeDataSource("DB_CLOSE_DELAY=-1").getConnection();
    // factory.createServerModeDataSource("DB_CLOSE_DELAY=-1").getConnection();

  }

  private String userHomeDir() {
    return new File(System.getProperty("user.home")).getPath().replace("\\", "/");
  }
}
