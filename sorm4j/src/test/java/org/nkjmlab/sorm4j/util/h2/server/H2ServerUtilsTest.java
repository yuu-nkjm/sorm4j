package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.nkjmlab.sorm4j.util.h2.server.H2ServerPropertiesBuilder.H2TcpServerPropertiesBuilder;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2ServerUtilsTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException {

    H2ServerProperties prop = new ObjectMapper()
        .readValue(H2ServerUtilsTest.class.getResourceAsStream("h2.tcpsrv.json.sample"),
            H2TcpServerPropertiesBuilder.class)
        .build();

    H2ServerUtils.awaitShutdownTcpServer(prop);
    H2ServerUtils.awaitStartServer(prop);


  }

}
