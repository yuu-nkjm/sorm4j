package org.nkjmlab.sorm4j.internal.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import org.nkjmlab.sorm4j.internal.OrmConnectionImpl;
import org.nkjmlab.sorm4j.internal.util.Try;

class ResultSetIterator<T> implements Iterator<T> {

  private final OrmConnectionImpl ormConnection;
  private final Class<T> objectClass;
  private final ResultSet resultSet;

  public ResultSetIterator(
      OrmConnectionImpl connection, Class<T> objectClass, ResultSet resultSet) {
    this.ormConnection = connection;
    this.objectClass = objectClass;
    this.resultSet = resultSet;
  }

  @Override
  public boolean hasNext() {
    try {
      return resultSet.next();
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  /** This iterator is closed if hasNext is false. */
  @Override
  public T next() {
    try {
      return ormConnection.mapRowToObject(objectClass, resultSet);
    } catch (SQLException e) {
      throw Try.rethrow(e);
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
