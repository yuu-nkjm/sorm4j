package org.nkjmlab.sorm4j.internal.util;

import static java.util.Spliterator.*;
import static java.util.Spliterators.*;
import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.internal.util.Try.ThrowableFunction;

public class ResultSetStreamFactory implements Closeable, AutoCloseable {

  private final ResultSet resultSet;

  public ResultSetStreamFactory(ResultSet resultSet) {
    this.resultSet = resultSet;
  }


  public <T> Stream<T> createStream(ThrowableFunction<ResultSet, T> resultSetHandler) {
    return StreamSupport.stream(spliteratorUnknownSize(
        new ResultSetIterator<>(
            Try.createSupplierWithThrow(() -> resultSetHandler.apply(resultSet), Try::rethrow)),
        ORDERED), false);
  }

  @Override
  public void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException e) {
    } finally {
    }
  }

  private final class ResultSetIterator<S> implements Iterator<S> {
    private final Supplier<S> getFunction;

    public ResultSetIterator(Supplier<S> getFunction) {
      this.getFunction = getFunction;
    }

    @Override
    public boolean hasNext() {
      try {
        boolean hasNext = resultSet.next();
        if (!hasNext) {
          close();
        }
        return hasNext;
      } catch (SQLException e) {
        close();
        throw Try.rethrow(e);
      }
    }

    @Override
    public S next() {
      return getFunction.get();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }


}
