package org.nkjmlab.sorm4j.util.function.exception;

@FunctionalInterface
public interface ExceptionHandler {
  void handle(Exception e);

  static ExceptionHandler rethrow() {
    return e -> {
      throw Try.rethrow(e);
    };
  }


  static ExceptionHandler ignore() {
    return e -> {};
  }
}
