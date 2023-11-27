package org.nkjmlab.sorm4j.result;

import java.util.stream.Stream;
import org.nkjmlab.sorm4j.annotation.Experimental;
import org.nkjmlab.sorm4j.common.ConsumerHandler;
import org.nkjmlab.sorm4j.common.FunctionHandler;

@Experimental
public interface ResultSetStream<T> {

  void accept(ConsumerHandler<Stream<T>> handler);

  <R> R apply(FunctionHandler<Stream<T>, R> handler);
}
