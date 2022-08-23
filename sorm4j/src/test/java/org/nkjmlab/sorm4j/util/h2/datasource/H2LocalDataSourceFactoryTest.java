package org.nkjmlab.sorm4j.util.h2.datasource;

import static org.assertj.core.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.internal.util.SystemPropertyUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2LocalDataSourceFactoryTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException {
    H2LocalDataSourceFactory factory = new ObjectMapper()
        .readValue(H2LocalDataSourceFactoryTest.class.getResourceAsStream("h2.json.sample"),
            H2LocalDataSourceFactory.Builder.class)
        .build();
    factory.makeFileDatabaseIfNotExists();

    assertThat(factory.getEmbeddedModeJdbcUrl())
        .isEqualTo("jdbc:h2:file:" + userHomeDir() + "/h2db/testdir/testdb");

    assertThat(factory.getInMemoryModeJdbcUrl())
        .isEqualTo("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

    assertThat(factory.getServerModeJdbcUrl())
        .isEqualTo("jdbc:h2:tcp://localhost/" + userHomeDir() + "/h2db/testdir/testdb");

    assertThat(factory.getMixedModeJdbcUrl())
        .isEqualTo("jdbc:h2:" + userHomeDir() + "/h2db/testdir/testdb;AUTO_SERVER=TRUE");


  }

  private String userHomeDir() {
    return SystemPropertyUtils.getUserHomeDirectory().getPath().replace("\\", "/");
  }

}
