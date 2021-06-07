
package org.nkjmlab.sorm4j.internal.sql.result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.ResultSetTraverser;
import org.nkjmlab.sorm4j.RowMapper;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.sql.result.LazyResultSet;

/**
 * Represents a result set from database.
 *
 * @param <T> element type in result
 */
public final class LazyResultSetImpl<T> implements LazyResultSet<T> {
  @SuppressWarnings("rawtypes")
  private static final Class<? extends Map> MAP_CLASS = LinkedHashMap.class;

  private final Class<T> objectClass;
  private final OrmConnectionImpl ormMapper;
  private final ResultSet resultSet;
  private final PreparedStatement stmt;

  @SuppressWarnings("unchecked")
  public LazyResultSetImpl(OrmConnectionImpl ormMapper, PreparedStatement stmt,
      ResultSet resultSet) {
    this(ormMapper, (Class<T>) MAP_CLASS, stmt, resultSet);
  }

  public LazyResultSetImpl(OrmConnectionImpl ormMapper, Class<T> objectClass,
      PreparedStatement stmt, ResultSet resultSet) {
    this.ormMapper = ormMapper;
    this.objectClass = objectClass;
    this.stmt = stmt;
    this.resultSet = resultSet;
  }

  /**
   * Returns the row in the result set and close. If the row is not unique, Exception is thrown.
   *
   * @return
   */
  @Override
  public T one() {
    @SuppressWarnings("unchecked")
    T ret = Try.getOrThrow(() -> objectClass.equals(MAP_CLASS) ? (T) ormMapper.loadOneMap(resultSet)
        : ormMapper.loadOne(objectClass, resultSet), Try::rethrow);
    close();
    return ret;
  }

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  @Override
  public T first() {
    @SuppressWarnings("unchecked")
    T ret =
        Try.getOrThrow(() -> objectClass.equals(MAP_CLASS) ? (T) ormMapper.loadFirstMap(resultSet)
            : ormMapper.loadFirst(objectClass, resultSet), Try::rethrow);
    close();
    return ret;
  }

  /**
   * Returns results in a List.
   *
   * @return
   */
  @Override
  public List<T> toList() {
    @SuppressWarnings("unchecked")
    List<T> ret = Try.getOrThrow(() -> objectClass.equals(MAP_CLASS)
        ? (List<T>) Try.getOrThrow(() -> ormMapper.traverseAndMapToMapList(resultSet), Try::rethrow)
        : ormMapper.loadPojoList(objectClass, resultSet), Try::rethrow);
    close();
    return ret;
  }

  @Override
  public List<T> toList(RowMapper<T> rowMapper) {
    List<T> ret = Try.getOrThrow(() -> ResultSetTraverser.from(rowMapper).traverseAndMap(resultSet),
        Try::rethrow);
    close();
    return ret;
  }

  /**
   * Iterates all the rows of the result set. The Iterator must be closed to release database
   * resources. The iterator is closed automatically if hasNext is false.
   */
  @Override
  public Iterator<T> iterator() {
    return new LazyResultSetIterator<>(ormMapper, objectClass, stmt, resultSet);
  }

  /**
   * Streams all the rows of the result set. The stream must be closed to release database
   * resources.
   */
  @Override
  public Stream<T> stream() {
    return StreamSupport
        .stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false)
        .onClose(Try.createRunnable(() -> close(), Try::rethrow));
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


  private final class LazyResultSetIterator<S> implements Iterator<S> {
    private final Supplier<S> nextSupplier;

    @SuppressWarnings("unchecked")
    public LazyResultSetIterator(OrmConnectionImpl ormMapper, Class<S> objectClass,
        PreparedStatement stmt, ResultSet resultSet) {
      this.nextSupplier = objectClass.equals(MAP_CLASS)
          ? Try.createSupplierWithThrow(() -> (S) ormMapper.mapRowToMap(resultSet), Try::rethrow)
          : Try.createSupplierWithThrow(() -> ormMapper.mapRowToObject(objectClass, resultSet),
              Try::rethrow);
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
      return nextSupplier.get();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

}
