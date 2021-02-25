package org.nkjmlab.sorm4j.connectionsource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public interface ConnectionSource {

  Connection getConnection() throws SQLException;

  DataSource getDataSource();
}
