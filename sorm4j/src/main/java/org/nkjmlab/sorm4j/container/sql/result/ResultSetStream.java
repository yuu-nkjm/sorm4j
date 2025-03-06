package org.nkjmlab.sorm4j.container.sql.result;

import java.util.stream.Stream;

import org.nkjmlab.sorm4j.common.handler.ConsumerHandler;
import org.nkjmlab.sorm4j.common.handler.FunctionHandler;

public interface ResultSetStream<T> {

  void accept(ConsumerHandler<Stream<T>> handler);

  <R> R apply(FunctionHandler<Stream<T>, R> handler);
}
