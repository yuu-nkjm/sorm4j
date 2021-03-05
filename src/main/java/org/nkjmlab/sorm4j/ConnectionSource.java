package org.nkjmlab.sorm4j;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public interface ConnectionSource {

  Connection getConnection() throws SQLException;

  DataSource getDataSource();


}
