package org.nkjmlab.sorm4j.internal.util;

import static java.util.Spliterator.*;
import static java.util.Spliterators.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetStream<T> {

  private final Function<ResultSet, T> resultSetHandler;
  private final ResultSet resultSet;

  public ResultSetStream(ResultSet resultSet, Function<ResultSet, T> resultSetHandler) {
    this.resultSet = resultSet;
    this.resultSetHandler = resultSetHandler;
  }


  public Stream<T> stream() {
    return StreamSupport.stream(spliteratorUnknownSize(
        new LazyResultSetIterator<>(() -> resultSetHandler.apply(resultSet)), ORDERED), false);
  }

  public void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException e) {
    } finally {
    }
  }

  private final class LazyResultSetIterator<S> implements Iterator<S> {
    private final Supplier<S> getFunction;

    public LazyResultSetIterator(Supplier<S> getFunction) {
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
