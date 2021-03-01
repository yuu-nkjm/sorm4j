
package org.nkjmlab.sorm4j;

import static java.util.Spliterator.*;
import static java.util.Spliterators.*;
import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.nkjmlab.sorm4j.mapping.AbstractOrmMapper;


public final class LazyResultSet<T> implements Iterable<T>, Closeable, AutoCloseable {

  private final Class<T> objectClass;
  private final AbstractOrmMapper ormMapper;
  private final ResultSet resultSet;
  private final PreparedStatement stmt;

  public LazyResultSet(AbstractOrmMapper ormMapper, Class<T> objectClass, PreparedStatement stmt,
      ResultSet resultSet) {
    this.ormMapper = ormMapper;
    this.objectClass = objectClass;
    this.stmt = stmt;
    this.resultSet = resultSet;
  }

  public T one() {
    T ret = ormMapper.loadOne(objectClass, resultSet);
    close();
    return ret;
  }

  public T first() {
    T ret = ormMapper.loadFirst(objectClass, resultSet);
    close();
    return ret;
  }

  public List<T> toList() {
    List<T> ret = ormMapper.loadPojoList(objectClass, resultSet);
    close();
    return ret;
  }

  public List<Map<String, Object>> toMapList() {
    List<Map<String, Object>> ret = ormMapper.loadMapList(resultSet);
    close();
    return ret;
  }

  @Override
  public Iterator<T> iterator() {
    return new LazyResultSetIterator<>(ormMapper, objectClass, stmt, resultSet);
  }

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
      this.getFunction = objectClass.equals(Map.class) ? () -> (S) orMapper.loadOneMap(resultSet)
          : () -> orMapper.loadOneObject(objectClass, resultSet);
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
