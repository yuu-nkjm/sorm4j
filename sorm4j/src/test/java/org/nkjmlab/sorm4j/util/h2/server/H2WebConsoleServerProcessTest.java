package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

class H2WebConsoleServerProcessTest {
  static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  @Test
  void testAwaitStart() throws StreamReadException, DatabindException, IOException {
    H2WebConsoleServerProperties prop =
        new ObjectMapper()
            .readValue(
                H2TcpServerProcessTest.class.getResourceAsStream("h2.webcon.json.sample"),
                H2WebConsoleServerProperties.Builder.class)
            .build();

    H2WebConsoleServerProcess server = new H2WebConsoleServerProcess(prop);
    server.awaitStart();
    log.debug("started");
    ProcessUtils.stopProcessBindingPortIfExists(prop.port);
  }
}
