package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;
import org.nkjmlab.sorm4j.util.logger.SormLogger;

public class ProcessUtils {
  private static final SormLogger log = LoggerContext.getDefaultLoggerSupplier().get();

  public static boolean isWindowsOs() {
    return System.getProperty("os.name").toLowerCase().indexOf("windows") > -1;
  }

  public static boolean isJapaneseOs() {
    return Locale.getDefault() == Locale.JAPANESE || Locale.getDefault() == Locale.JAPAN;
  }


  /**
   * Reads read standard output after process finish.
   *
   * @param process
   * @return
   */
  private static String readStandardOutputAfterProcessFinish(Process process) {
    try {
      byte[] b = process.getInputStream().readAllBytes();
      return new String(b,
          isWindowsOs() && isJapaneseOs() ? "MS932" : StandardCharsets.UTF_8.toString());
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  public static Optional<String> getProcessIdBidingPort(int port) {
    try {
      List<String> command = isWindowsOs() ? List.of("cmd", "/c", "netstat", "-ano")
          : List.of("lsof", "-nPi", ":" + port);

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process proc = pb.start();
      String lines = readStandardOutputAfterProcessFinish(proc);
      if (isWindowsOs()) {
        return Arrays.stream(lines.split(System.lineSeparator()))
            .filter(l -> l.contains(":" + port + " ")).findAny().map(l -> {
              String[] t = l.split("\\s+");
              return t[t.length - 1];
            });
      } else {
        log.debug("comannd {} result ={}", command, lines);
        return Arrays.stream(lines.split(System.lineSeparator()))
            .filter(l -> l.contains(String.valueOf(port))).findAny().map(l -> {
              String[] t = l.split("\\s+");
              log.debug("{}", Arrays.toString(t));
              return t[1];
            });
      }
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  public static boolean stopProcessBindingPortIfExists(int port) {
    return getProcessIdBidingPort(port).map(pid -> {
      try {
        List<String> command =
            isWindowsOs() ? List.of("taskkill", "/F", "/T", "/PID", pid) : List.of("kill", pid);
        log.info("process [{}] is binding port [{}]. try killing {}", pid, port, command);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.start().waitFor();
      } catch (InterruptedException | IOException e) {
        throw Try.rethrow(e);
      }
      // log.info("Success to stop the process [{}] biding port :[{}].", pid, port);
      return true;
    }).orElse(false);
  }

}
