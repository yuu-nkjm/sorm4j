
package org.nkjmlab.sorm4j.internal.result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.result.ResultSetStream;

/**
 * Represents a result set from database.
 *
 * @param <T> element type in result
 */
public final class ResultSetStreamImpl<T> implements ResultSetStream<T> {

  private final Class<T> objectClass;
  private final OrmConnectionImpl ormConnection;
  private final ResultSet resultSet;
  private final PreparedStatement stmt;

  public ResultSetStreamImpl(OrmConnectionImpl ormConnection, Class<T> objectClass,
      PreparedStatement stmt, ResultSet resultSet) {
    this.ormConnection = ormConnection;
    this.objectClass = objectClass;
    this.stmt = stmt;
    this.resultSet = resultSet;
  }


  /**
   * Iterates all the rows of the result set. The Iterator must be closed to release database
   * resources. The iterator is closed automatically if hasNext is false.
   */
  private Iterator<T> iterator() {
    return new ResultSetIterator<>();
  }

  /**
   * Streams all the rows of the result set. The stream must be closed to release database
   * resources.
   */
  @Override
  public Stream<T> stream() {
    return StreamSupport
        .stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false)
        .onClose(() -> close());
  }

  @Override
  public void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException e) {
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e) {
      }
    }
  }


  private class ResultSetIterator<S> implements Iterator<S> {

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

    @SuppressWarnings("unchecked")
    @Override
    public S next() {
      try {
        return (S) ormConnection.mapRowToObject(objectClass, resultSet);
      } catch (SQLException e) {
        close();
        throw Try.rethrow(e);
      }
    }

    @Override
    public void remove() {
      close();
      throw new UnsupportedOperationException();
    }

  }

}
