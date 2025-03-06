package org.nkjmlab.sorm4j.extension.h2.tools.server;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2TcpServerProcessTest {

  @Test
  void test() throws StreamReadException, DatabindException, IOException {

    H2TcpServerProperties prop =
        new ObjectMapper()
            .readValue(
                H2TcpServerProcessTest.class.getResourceAsStream("h2.tcpsrv.json.sample"),
                H2TcpServerProperties.Builder.class)
            .build();

    H2TcpServerProcess server = new H2TcpServerProcess(prop);
    server.awaitShutdown();
    server.awaitStart();
    server.awaitShutdown();
  }
}
