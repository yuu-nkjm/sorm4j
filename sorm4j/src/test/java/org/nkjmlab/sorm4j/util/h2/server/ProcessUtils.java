package org.nkjmlab.sorm4j.util.h2.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.nkjmlab.sorm4j.context.logging.SormLogger;
import org.nkjmlab.sorm4j.internal.logging.LogContextImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

public class ProcessUtils {
  private static final SormLogger log = LogContextImpl.getDefaultLoggerSupplier().get();

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
      return new String(
          b, isWindowsOs() && isJapaneseOs() ? "MS932" : StandardCharsets.UTF_8.toString());
    } catch (IOException e) {
      throw Try.rethrow(e);
    }
  }

  public static Optional<String> getProcessIdBidingPort(int port) {
    try {
      List<String> command =
          isWindowsOs()
              ? List.of("cmd", "/c", "netstat", "-ano")
              : List.of("lsof", "-nPi", ":" + port);

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process proc = pb.start();
      String lines = readStandardOutputAfterProcessFinish(proc);
      if (isWindowsOs()) {
        return Arrays.stream(lines.split(System.lineSeparator()))
            .filter(l -> l.contains(":" + port + " "))
            .findAny()
            .map(
                l -> {
                  String[] t = l.split("\\s+");
                  return t[t.length - 1];
                });
      } else {
        log.debug("comannd {} result ={}", command, lines);
        return Arrays.stream(lines.split(System.lineSeparator()))
            .filter(l -> l.contains(String.valueOf(port)))
            .findAny()
            .map(
                l -> {
                  String[] t = l.split("\\s+");
                  log.debug("{}", Arrays.toString(t));
                  return t[1];
                });
      }
    } catch (Exception e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * If the process binding the the given port, it will be stopped.
   *
   * @param port
   * @return
   */
  public static boolean stopProcessBindingPortIfExists(int port) {
    return stopProcessBindingPortIfExists(port, 10, TimeUnit.SECONDS);
  }

  public static boolean stopProcessBindingPortIfExists(int port, long timeout, TimeUnit unit) {
    return getProcessIdBidingPort(port)
        .map(
            pid -> {
              try {
                log.info(
                    "process [{}] is binding port [{}]. try killing by ProcessHandle.destory()",
                    pid,
                    port);
                ProcessHandle proc = ProcessHandle.of(Long.valueOf(pid)).orElseThrow();
                proc.destroy();

                long start = System.currentTimeMillis();

                while (proc.isAlive()) {
                  long durationInMillis = System.currentTimeMillis() - start;
                  if (durationInMillis > TimeUnit.MICROSECONDS.convert(timeout, unit)) {
                    log.error("Process [{}] is active yet.");
                    return false;
                  }
                  TimeUnit.SECONDS.sleep(1);
                }
                return true;
              } catch (InterruptedException e) {
                throw Try.rethrow(e);
              }
            })
        .orElse(false);
  }

  public static boolean killForceProcessBindingPortIfExists(int port) {
    return getProcessIdBidingPort(port)
        .map(
            pid -> {
              try {
                List<String> command =
                    isWindowsOs()
                        ? List.of("taskkill", "/F", "/T", "/PID", pid)
                        : List.of("kill", "-9", pid);
                log.info("process [{}] is binding port [{}]. try killing {}", pid, port, command);
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start().waitFor();
              } catch (InterruptedException | IOException e) {
                throw Try.rethrow(e);
              }
              // log.info("Success to stop the process [{}] biding port :[{}].", pid, port);
              return true;
            })
        .orElse(false);
  }
}
