package org.nkjmlab.sorm4j.connectionsource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.OrmException;

public class ConnectionSuplierConnectionSource implements ConnectionSource {

  private Supplier<Connection> connectionSupplier;

  public ConnectionSuplierConnectionSource(Supplier<Connection> connectionSupplier) {
    this.connectionSupplier = connectionSupplier;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return connectionSupplier.get();
  }

  @Override
  public DataSource getDataSource() {
    throw new OrmException("not implmented");
  }

}
