
package org.nkjmlab.sorm4j.mapping;

import static java.util.Spliterator.*;
import static java.util.Spliterators.*;
import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.OrmException;
import org.nkjmlab.sorm4j.result.LazyResultSet;
import org.nkjmlab.sorm4j.util.Try;

/**
 * Represents a result set from database.
 *
 * @param <T> element type in result
 */
final class LazyResultSetImpl<T>
    implements Iterable<T>, Closeable, AutoCloseable, LazyResultSet<T> {

  private final Class<T> objectClass;
  private final AbstractOrmMapper ormMapper;
  private final ResultSet resultSet;
  private final PreparedStatement stmt;

  public LazyResultSetImpl(AbstractOrmMapper ormMapper, Class<T> objectClass,
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
    T ret = Try.getOrThrow(() -> ormMapper.loadOne(objectClass, resultSet), OrmException::new);
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
    T ret = Try.getOrThrow(() -> ormMapper.loadFirst(objectClass, resultSet), OrmException::new);
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
    List<T> ret =
        Try.getOrThrow(() -> ormMapper.loadPojoList(objectClass, resultSet), OrmException::new);
    close();
    return ret;
  }

  @Override
  public Map<String, Object> oneMap() {
    Map<String, Object> ret =
        Try.getOrThrow(() -> ormMapper.loadOneMap(resultSet), OrmException::new);
    close();
    return ret;
  }

  /**
   * Returns the first row in the result set and close.
   *
   * @return
   */
  @Override
  public Map<String, Object> firstMap() {
    Map<String, Object> ret =
        Try.getOrThrow(() -> ormMapper.loadFirstMap(resultSet), OrmException::new);
    close();
    return ret;
  }


  /**
   * Returns results in a List of {@code Map<String, Object>}.
   *
   * @return
   */
  @Override
  public List<Map<String, Object>> toMapList() {
    List<Map<String, Object>> ret =
        Try.getOrThrow(() -> ormMapper.loadMapList(resultSet), OrmException::new);
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
    return StreamSupport.stream(spliteratorUnknownSize(iterator(), ORDERED), false);
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
    private final Supplier<S> getFunction;

    @SuppressWarnings("unchecked")
    public LazyResultSetIterator(AbstractOrmMapper orMapper, Class<S> objectClass,
        PreparedStatement stmt, ResultSet resultSet) {
      this.getFunction = objectClass.equals(LinkedHashMap.class)
          ? Try.createSupplierWithThrow(() -> (S) orMapper.toSingleMap(resultSet),
              OrmException::new)
          : Try.createSupplierWithThrow(() -> orMapper.toSingleObject(objectClass, resultSet),
              OrmException::new);
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
        throw new OrmException(e);
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
